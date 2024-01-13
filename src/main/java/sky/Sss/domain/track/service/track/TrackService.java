package sky.Sss.domain.track.service.track;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.dto.playlist.PlayListTrackInfoDto;
import sky.Sss.domain.track.dto.track.TrackInfoRepDto;
import sky.Sss.domain.track.dto.track.TrackInfoSaveDto;
import sky.Sss.domain.track.dto.playlist.PlayListInfoDto;
import sky.Sss.domain.track.dto.playlist.PlayListSettingSaveDto;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.domain.track.dto.track.TrackInfoUpdateDto;
import sky.Sss.domain.track.dto.track.TrackPlayRepDto;
import sky.Sss.domain.track.entity.TempTrackStorage;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.exception.SsbFileNotFoundException;
import sky.Sss.domain.track.exception.SsbFileLengthLimitOverException;
import sky.Sss.domain.track.repository.playList.PlayListSettingRepository;
import sky.Sss.domain.track.repository.track.TrackRepository;
import sky.Sss.domain.track.service.temp.TempTrackStorageService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.UserTokenUtil;
import sky.Sss.global.file.dto.UploadFileDto;
import sky.Sss.global.file.utili.FileStore;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TrackService {

    private final FileStore fileStore;
    private final TrackRepository trackRepository;
    private final UserQueryService userQueryService;
    private final PlayListSettingRepository playListSettingRepository;
    private final TrackTagService trackTagService;
    private final TempTrackStorageService tempTrackStorageService;
    private final TrackPlayCountService trackPlayCountService;
    /**
     * track 생성
     *
     * @param trackInfoSaveDto
     * @throws IOException
     */
    @Transactional
    public TrackInfoRepDto saveTrackFile(TrackInfoSaveDto trackInfoSaveDto, MultipartFile coverImgFile, String sessionId) {
        User user = userQueryService.findOne();
        // 시간제한 180분
        // 임시 디비에 있던걸
        // ssbTrack 에 옮기는 작업
        TempTrackStorage tempTrackStorage = tempTrackStorageService.findOne(trackInfoSaveDto.getId(), sessionId,
            trackInfoSaveDto.getToken(), user);

        List<TrackTagsDto> tagList = trackInfoSaveDto.getTagList();

        // 현재 ssbTrack 에 저장되어 있는 track
        Integer totalTrackLength = getTotalLength(user);

        // 태그 확인
        List<SsbTrackTags> ssbTrackTags = getSsbTrackTags(tagList);
        Integer totalUploadTrackLength = 0;
        SsbTrack ssbTrack = createTrack(user, tempTrackStorage, totalUploadTrackLength, totalTrackLength,
            trackInfoSaveDto,
            ssbTrackTags);

        // 트랙 저장
        trackRepository.save(ssbTrack);

        String storeFileName = null;
        if (coverImgFile != null) { // 저장할 이미지가 있으면 업로드
            storeFileName = getUploadFileDto(coverImgFile).getStoreFileName();
        }
        SsbTrack.updateCoverImg(storeFileName, ssbTrack);
        tempTrackStorageService.delete(tempTrackStorage);
        return TrackInfoRepDto.create(ssbTrack);

    }

    /**
     * 플레이리스트 생성
     *
     * @param playListSettingSaveDto
     * @throws IOException
     */
    @Transactional
    public PlayListInfoDto saveTrackFiles(PlayListSettingSaveDto playListSettingSaveDto, MultipartFile coverImgFile,
        String sessionId) {
        User user = userQueryService.findOne();
        // playList 안에 있는 Track 정보
        List<PlayListTrackInfoDto> trackPlayListFileDtoList = playListSettingSaveDto.getPlayListTrackInfoDtoList();
        // 플레이 리스트 저장
        SsbPlayListSettings ssbPlayListSettings = SsbPlayListSettings.create(playListSettingSaveDto,
            user);
        // Token 생성
        String playListToken = UserTokenUtil.getToken();
        //playList token 저장
        SsbPlayListSettings.updateToken(playListToken, ssbPlayListSettings);
        // ssbTrack 저장을 위한 Map 생성
        Map<Integer, SsbTrack> trackFileMap = new HashMap<>();

        List<String> tokens = trackPlayListFileDtoList.stream().map(dto -> dto.getToken()).collect(Collectors.toList());
        List<Long> ids = trackPlayListFileDtoList.stream().map(dto -> dto.getId()).collect(Collectors.toList());

        List<TempTrackStorage> tempList = tempTrackStorageService.findByList(sessionId, user, tokens, ids);

        // 사이즈가 맞지 않는 경우
        if (tempList.size() != trackPlayListFileDtoList.size()) {
            throw new SsbFileNotFoundException();
        }

        Integer totalTrackLength = getTotalLength(user);
        // 태그 조회
        List<SsbTrackTags> ssbTrackTags = getSsbTrackTags(playListSettingSaveDto.getTagList());
        //
        List<SsbTrack> savaTracks = new ArrayList<>();

        // upload 할 length 저장
        Integer totalUploadTrackLength = 0;
        for (PlayListTrackInfoDto metaDto : trackPlayListFileDtoList) {
            TempTrackStorage tempTrack = tempList.stream().filter(temp -> temp.getToken().equals(metaDto.getToken()))
                .findFirst()
                .orElseThrow(() -> new SsbFileNotFoundException());
            // ssbTrack 저장
            SsbTrack ssbTrack = createTrack(user, tempTrack, totalUploadTrackLength, totalTrackLength, metaDto,
                ssbTrackTags);

            tempList.add(tempTrack);
            // tag 저장
            // 순서를 키값으로 저장
            trackFileMap.put(metaDto.getOrder(), ssbTrack);
            savaTracks.add(ssbTrack);
        }

        // 앨범 태그 저장
        if (ssbTrackTags != null) {
            List<SsbPlayListTagLink> playListTagLinks = getPlayListTagLinks(ssbTrackTags, ssbPlayListSettings);
            SsbPlayListSettings.addTagLink(ssbPlayListSettings, playListTagLinks);
        }
        // 플레이 리스트 구성 및 순서 저장
        SsbPlayListTracks.createSsbPlayListTrackList(trackFileMap,
            ssbPlayListSettings);

        // 임시파일 DB에서 삭제
        tempTrackStorageService.deleteAllBatch(tempList);

        // 커버 이미지 업데이트
        String storeFileCoverName = null;
        if (coverImgFile != null) {
            storeFileCoverName = getUploadFileDto(coverImgFile).getStoreFileName();
        }
        for (Integer key : trackFileMap.keySet()) {
            trackFileMap.get(key);
            SsbTrack.updateCoverImg(storeFileCoverName, trackFileMap.get(key));
        }
        SsbPlayListSettings.updateCoverImg(storeFileCoverName, ssbPlayListSettings);

        trackRepository.saveAll(savaTracks);
        // 등록
        playListSettingRepository.save(ssbPlayListSettings);

        return PlayListInfoDto.create(ssbPlayListSettings);
    }

    @Transactional
    public void updateTrackInfo(TrackInfoUpdateDto trackInfoUpdateDto, MultipartFile coverImgFile) {
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = findOne(trackInfoUpdateDto.getId(), trackInfoUpdateDto.getToken(),
            user, Status.ON);
        // 현재 태그에도 속하지 않고
        // 업데이트 태그에도 속하지 않는 놈은 삭제

        // 새로운 태그 수정
        List<SsbTrackTags> newTagList = getSsbTrackTags(trackInfoUpdateDto.getTagList());

        // 삭제 태그 링크
        List<SsbTrackTagLink> removeTagLinks = new ArrayList<>();

        // 기존 태그 링크
        List<SsbTrackTagLink> existTagLinks = ssbTrack.getTags();

        // 태그 필터
        filterNewTags(newTagList, removeTagLinks, existTagLinks);

        // 포함되어 있지 않은 태그 링크 삭제
        trackTagService.deleteTagLinksInBatch(removeTagLinks);

        List<SsbTrackTagLink> trackTagLinks = getTrackTagLinks(newTagList, ssbTrack);
        SsbTrack.addTagLink(ssbTrack, trackTagLinks);
        // 내용 수정
        SsbTrack.uploadInfo(ssbTrack, trackInfoUpdateDto.getGenre(), trackInfoUpdateDto.getGenreType(),
            trackInfoUpdateDto.getIsPrivacy(), trackInfoUpdateDto.getIsDownload(), trackInfoUpdateDto.getTitle(),
            trackInfoUpdateDto.getDesc());

        if (coverImgFile != null) {
            // 기존 이미지 삭제
//            SsbTrack.deleteCoverImg(ssbTrack, fileStore);
            UploadFileDto uploadFileDto = getUploadFileDto(coverImgFile);
            SsbTrack.updateCoverImg(uploadFileDto.getStoreFileName(), ssbTrack);
        }
    }

    public void filterNewTags(List<SsbTrackTags> newTagList, List<SsbTrackTagLink> removeTagLinks,
        List<SsbTrackTagLink> existTagLinks) {
        // 중복 태그
        List<SsbTrackTags> duplicateTags = new ArrayList<>();
        if (existTagLinks != null && !existTagLinks.isEmpty()) {// 기존 태그가 있는경우
            existTagLinks.forEach(oldTagLink -> {
                    if (newTagList == null) { // 기존 태그가 있는데 태그를 전부다 삭제 할때
                        removeTagLinks.add(oldTagLink);
                    } else { // 기존태그가 있고 업데이트 태그도 있을때
                        SsbTrackTags trackTag = newTagList.stream()
                            .filter(newTags -> newTags.getId() == oldTagLink.getSsbTrackTags().getId()).findFirst()
                            .orElse(null);
                        if (trackTag == null) { // 기존태그 == 새로운태그 비교 후 없을 경우 삭제
                            removeTagLinks.add(oldTagLink);
                        } else { // 기존에 태그가 있는 경우는 newTagList 에서 제외
                            duplicateTags.add(trackTag);
                        }
                    }
                }
            );
        }
        // 중복 태그 insert x
        duplicateTags.forEach(tags -> newTagList.remove(tags));
    }


    /**
     * 권한 확인 후
     * track file token 반환
     *
     * @param id
     * @return
     */
    @Transactional
    public TrackPlayRepDto authorizedTrackInfo(Long id, Status isStatus, String userAgent) {
        // 요청 유저
        TrackPlayRepDto trackPlayDto;
        SsbTrack ssbTrack = findOneJoinUser(id, isStatus);
        User playUser = userQueryService.findOne();
        Boolean isMember = playUser != null; // 멤버인지 확인
        Boolean isOwnerPost = isMember ? ssbTrack.getUser().equals(playUser) : false; // 작성자인지 확인
        // 파일에 권한이 있는지 없는지 확인
        // isPrivacy : true 비공개 ,false 공개
        // isOwnerPost : true 사용자 자신의 게시물 , false 사용자 자신의 게시물 x
        if (ssbTrack.getIsPrivacy()) {// 비공개일 경우 재생권한이 있는지 확인
            trackPlayDto = isOwnerPost ? TrackPlayRepDto.create(ssbTrack) : null;
        } else { // 비공개가 아닐경우
            trackPlayDto = TrackPlayRepDto.create(ssbTrack);
        }
        // 비회원 조회수 측정 x
        // 자신의 track은 자신이 플레이를 해도 측정 x
        // 해당 트랙에 접근 권한이 없을 경우 x
        if (isMember && !isOwnerPost && trackPlayDto != null) {
            SsbChartIncludedPlays playCounts = trackPlayCountService.save(ssbTrack, playUser, userAgent);
            trackPlayDto.setTrackCountRepDto(playCounts);
        }
        return trackPlayDto;
    }

    @Transactional
    public void deleteTrack(Long id, String token) {
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = findOne(id, token, user, Status.ON);

        // 커버이미지 삭제
//        SsbTrack.deleteCoverImg(ssbTrack, fileStore);

        trackTagService.deleteTagLinksInBatch(ssbTrack.getTags());

        SsbTrack.deleteTrackFile(ssbTrack, fileStore);
        SsbTrack.changeStatus(ssbTrack, Status.OFF);
    }

    public SsbTrack findOne(Long id, String token, User user, Status isStatus) {
        return trackRepository.findOne(id, user, token, isStatus.getValue())
            .orElseThrow(() -> new SsbFileNotFoundException());
    }

    public SsbTrack findOne(Long id, String token, Status isStatus) {
        return trackRepository.findOne(id, token, isStatus.getValue())
            .orElseThrow(() -> new SsbFileNotFoundException());
    }


    public SsbTrack findOneJoinUser(Long id, Status isStatus) {
        return trackRepository.findByIdJoinUser(id, isStatus.getValue())
            .orElseThrow(() -> new SsbFileNotFoundException());
    }

    /**
     * cover Img upload
     *
     * @return
     * @throws IOException
     */
    public UploadFileDto getUploadFileDto(MultipartFile coverImgFile) {
        UploadFileDto uploadFileDto = fileStore.storeFileSave(coverImgFile,
            FileStore.COVER_TYPE, 500);
        return uploadFileDto;
    }

    private SsbTrack createTrack(User user, TempTrackStorage tempTrack,
        Integer totalUploadTrackLength, Integer totalTrackLength,
        TrackInfoSaveDto metaDto, List<SsbTrackTags> ssbTrackTags) {

        // ssbTrack 생성
        SsbTrack ssbTrack = SsbTrack.create(metaDto, tempTrack, user);
        // token값 저장
        SsbTrack.updateToken(tempTrack.getToken(), ssbTrack);

        // 태그 Link 추가
        if (ssbTrackTags != null) {
            List<SsbTrackTagLink> trackTagLinks = getTrackTagLinks(ssbTrackTags, ssbTrack);
            SsbTrack.addTagLink(ssbTrack, trackTagLinks);
        }
        // 총 파일 트랙 길이 저장
        totalUploadTrackLength += tempTrack.getTrackLength();

        // 총업로드 제한 180분 이 넘는지 확인
        checkLimit(totalTrackLength, totalUploadTrackLength);
        return ssbTrack;
    }


    public Integer getTotalLength(User user) {
        Integer totalTrackLength = trackRepository.getTotalTrackLength(user, false);
        return totalTrackLength;
    }

    // 태그 등록
    private List<SsbTrackTagLink> getTrackTagLinks(List<SsbTrackTags> tags, SsbTrack ssbTrack) {
        if (tags != null && !tags.isEmpty()) {
            List<SsbTrackTagLink> ssbTrackTagLinks = tags.stream().map(tag -> SsbTrackTagLink.createSsbTrackTagLink(
                ssbTrack, tag)).collect(Collectors.toList());
            return ssbTrackTagLinks;
        }
        return null;
    }

    // 태그 등록
    public List<SsbPlayListTagLink> getPlayListTagLinks(List<SsbTrackTags> tags,
        SsbPlayListSettings ssbPlayListSettings) {
        if (tags != null && !tags.isEmpty()) {
            List<SsbPlayListTagLink> ssbTrackTagLinks = tags.stream()
                .map(tag -> SsbPlayListTagLink.createSsbTrackTagLink(
                    ssbPlayListSettings, tag)).collect(Collectors.toList());

            return ssbTrackTagLinks;
        }
        return null;
    }


    // DB 태그 여부 가져오기
    public List<SsbTrackTags> getSsbTrackTags(List<TrackTagsDto> tagList) {
        if (tagList != null && tagList.size() > 0) {
            List<SsbTrackTags> tags = new ArrayList<>();
            List<SsbTrackTags> addTags = new ArrayList<>();
            tagList.forEach(tag -> {
                SsbTrackTags tagsByStr = trackTagService.getTagsByStr(tag.getTag());
                if (tagsByStr != null) {
                    tags.add(tagsByStr);
                } else {
                    addTags.add(SsbTrackTags.createSsbTrackTag(tag.getTag()));
                }
            });
            // 기존에 없던 태그 저장 후 추가
            if (!addTags.isEmpty()) {
                trackTagService.addTags(addTags).stream().forEach(addTag -> tags.add(addTag));
            }
            return tags;
        }
        return null;
    }

    private void checkLimit(Integer totalTrackLength, Integer trackLength) {
        Boolean isLengthOver = (totalTrackLength + trackLength) > FileStore.TRACK_UPLOAD_LIMIT;
        if (isLengthOver) {
            throw new SsbFileLengthLimitOverException();
        }
    }

    public UrlResource getSsbTrackFile(String fileName) {

        return fileStore.getUrlResource(FileStore.TRACK_DIR + fileName);
    }

}