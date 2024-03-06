package sky.Sss.domain.track.service.track;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.feed.entity.SsbFeed;
import sky.Sss.domain.feed.service.FeedService;
import sky.Sss.domain.track.dto.BaseTrackDto;
import sky.Sss.domain.track.dto.playlist.PlayListTrackInfoReqDto;
import sky.Sss.domain.track.dto.track.TrackInfoRepDto;
import sky.Sss.domain.track.dto.track.TrackInfoSaveReqDto;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.domain.track.dto.track.TrackInfoModifyReqDto;
import sky.Sss.domain.track.dto.track.TrackPlayRepDto;
import sky.Sss.domain.track.entity.TempTrackStorage;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.exception.checked.SsbFileLengthLimitOverException;
import sky.Sss.domain.track.repository.track.TrackRepositoryImpl;
import sky.Sss.domain.track.service.common.RepostCommonService;
import sky.Sss.domain.track.service.common.TagLinkCommonService;
import sky.Sss.domain.track.service.playList.PlyTracksService;
import sky.Sss.domain.track.service.temp.TempTrackStorageService;
import sky.Sss.domain.track.service.track.play.TrackPlayMetricsService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.file.dto.UploadFileDto;
import sky.Sss.global.file.utili.FileStore;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TrackService {

    private final FileStore fileStore;
    private final UserQueryService userQueryService;
    private final TrackTagService trackTagService;
    private final TagLinkCommonService tagLinkCommonService;
    private final TempTrackStorageService tempTrackStorageService;
    private final TrackQueryService trackQueryService;
    private final TrackRepositoryImpl trackRepositoryImpl;
    private final TrackPlayMetricsService trackPlayMetricsService;
    private final FeedService feedService;
    private final RepostCommonService repostCommonService;

    private final PlyTracksService plyTracksService;


    /**
     * track 생성
     *
     * @param trackInfoSaveReqDto
     * @throws IOException
     */
    @Transactional
    public TrackInfoRepDto addTrackFile(TrackInfoSaveReqDto trackInfoSaveReqDto, MultipartFile coverImgFile,
        String sessionId) {
        User user = userQueryService.findOne();
        // 시간제한 180분
        // 임시 디비에 있던걸
        // ssbTrack 에 옮기는 작업
        TempTrackStorage tempTrackStorage = tempTrackStorageService.findOne(trackInfoSaveReqDto.getId(), sessionId,
            trackInfoSaveReqDto.getToken(), user);

        List<TrackTagsDto> tagList = trackInfoSaveReqDto.getTagList();

        // 현재 ssbTrack 에 저장되어 있는 track
        Integer totalTrackLength = getTotalLength(user);

        // 태그 확인
        List<SsbTrackTags> ssbTrackTags = trackTagService.getSsbTrackTags(tagList);
        Integer totalUploadTrackLength = 0;
        SsbTrack ssbTrack = createTrack(user, tempTrackStorage, totalUploadTrackLength, totalTrackLength,
            trackInfoSaveReqDto);

        // 사용자에게 한번 배포가 되었는지 확인
        // true: 배포함 ,False : 배포되지 않음
        SsbTrack.updateIsRelease(ssbTrack, !ssbTrack.getIsPrivacy());

        // 트랙 저장
        trackRepositoryImpl.save(ssbTrack);

        // 링크 연결
        if (ssbTrackTags != null) {
            List<SsbTrackTagLink> trackTagLinks = getTrackTagLinks(ssbTrackTags, ssbTrack);
            tagLinkCommonService.addTrackTagLinks(trackTagLinks);
        }

        String storeFileName = null;
        if (coverImgFile != null) { // 저장할 이미지가 있으면 업로드
            storeFileName = getUploadFileDto(coverImgFile).getStoreFileName();
        }

        SsbTrack.updateCoverImg(storeFileName, ssbTrack);
        tempTrackStorageService.delete(tempTrackStorage);

        SsbFeed ssbFeed = SsbFeed.create(ssbTrack.getId(), user, ContentsType.TRACK);
        SsbFeed.updateReleaseDateTime(ssbFeed, ssbTrack.getCreatedDateTime());

        // 비공개가 아니면
        feedService.addFeed(ssbFeed);

        return TrackInfoRepDto.create(ssbTrack);

    }

    public SsbTrack findOne(Long id, String token, User user, Status isStatus) {
        return trackQueryService.findOne(id, token, user, isStatus);
    }

    public SsbTrack findOne(Long id, String token, Status isStatus) {
        return trackQueryService.findOne(id, token, isStatus);
    }

    public SsbTrack findOneJoinUser(Long id, Status isStatus) {
        return trackQueryService.findOneJoinUser(id, isStatus);
    }

    public Integer getTotalLength(User user) {
        return trackQueryService.getTotalLength(user);
    }

    /**
     * 다중 Track 저장 후 tokenList 반납
     *
     * @throws IOException
     */
    @Transactional
    public List<TrackInfoRepDto> addTrackFiles(User user, long settingsId, String coverUrl, LocalDateTime createdDateTime,
        List<SsbTrackTags> ssbTrackTags,
        List<PlayListTrackInfoReqDto> trackPlayListFileDtoList, String sessionId) {
        // playList 안에 있는 Track 정보

        // ssbTrack 저장을 위한 Map 생성
        Map<Integer, SsbTrack> trackFileMap = new HashMap<>();

        // 트랙 토큰
        List<String> tokenList = trackPlayListFileDtoList.stream().map(BaseTrackDto::getToken)
            .toList();
        // 임시파일 id
        List<Long> tempIdList = trackPlayListFileDtoList.stream().map(BaseTrackDto::getId).toList();


        // 임시파일 리스트
        List<TempTrackStorage> tempList = tempTrackStorageService.findByList(sessionId, user, tokenList, tempIdList);

        // 사이즈가 맞지 않는 경우
        if (tempList.size() != trackPlayListFileDtoList.size()) {
            throw new SsbFileNotFoundException();
        }

        Integer totalTrackLength = getTotalLength(user);
        // 태그 조회
        List<SsbTrack> saveTracks = new ArrayList<>();

        // 외래키 연결을 위한 객체 생성
        SsbPlayListSettings ssbPlayListSettings = SsbPlayListSettings.builder().id(settingsId).build();

        // upload 할 length 저장
        Integer totalUploadTrackLength = 0;
        for (PlayListTrackInfoReqDto metaDto : trackPlayListFileDtoList) {
            TempTrackStorage tempTrack = tempList.stream().filter(temp -> temp.getToken().equals(metaDto.getToken()))
                .findFirst()
                .orElseThrow(SsbFileNotFoundException::new);
            // ssbTrack 저장
            SsbTrack ssbTrack = createTrack(user, tempTrack, totalUploadTrackLength, totalTrackLength, metaDto);

            SsbTrack.updateIsRelease(ssbTrack, !ssbTrack.getIsPrivacy());
            // tag 저장
            // 순서를 키값으로 저장
            trackFileMap.put(metaDto.getOrder(), ssbTrack);
            saveTracks.add(ssbTrack);
        }

        log.info("tempList size = {}", tempList.size());
        // 임시파일 DB에서 삭제
        tempTrackStorageService.deleteAllBatch(tempList);

        for (Integer key : trackFileMap.keySet()) {
            trackFileMap.get(key);
            SsbTrack.updateCoverImg(coverUrl, trackFileMap.get(key));
        }
        trackRepositoryImpl.saveAll(saveTracks, createdDateTime);

        // save 한 track 을 select 한 후 id,createdDateTime Update
        List<TrackInfoRepDto> trackInfoList = trackQueryService.getTrackInfoRepDto(tokenList, user, Status.ON);
        trackInfoList.forEach(info ->
            saveTracks.stream()
                .filter(save -> save.getToken().equals(info.getToken()))
                .findFirst()
                .ifPresent(save -> save.updateTrackInfo(info.getId(), info.getCreatedDateTime()))
        );
        // 태그 링크 추가
        // 태그 Link 추가
        if (ssbTrackTags != null) {
            List<SsbTrackTagLink> trackTagLinks = new ArrayList<>();
            saveTracks.forEach(track -> {
                trackTagLinks.addAll(getTrackTagLinks(ssbTrackTags, track));
            });
            tagLinkCommonService.addTrackTagLinks(trackTagLinks);
        }
        // 플레이 리스트 트랙 목록 save
        List<SsbPlayListTracks> ssbPlayListTrackList = SsbPlayListTracks.createSsbPlayListTrackList(trackFileMap,
            ssbPlayListSettings);
        plyTracksService.addPlayListTracks(ssbPlayListTrackList,createdDateTime);

        return trackInfoList;
    }

    @Transactional
    public void updateTrackInfo(TrackInfoModifyReqDto trackInfoModifyReqDto, MultipartFile coverImgFile) {
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = findOne(trackInfoModifyReqDto.getId(), trackInfoModifyReqDto.getToken(),
            user, Status.ON);
        // 현재 태그에도 속하지 않고
        // 업데이트 태그에도 속하지 않는 놈은 삭제

        // 새로운 태그 수정
        List<SsbTrackTags> newTagList = trackTagService.getSsbTrackTags(trackInfoModifyReqDto.getTagList());

        // 삭제 태그 링크
        List<SsbTrackTagLink> removeTagLinks = new ArrayList<>();

        // 기존 태그 링크
        List<SsbTrackTagLink> existTagLinks = ssbTrack.getTags();

        // 태그 필터
        filterNewTags(newTagList, removeTagLinks, existTagLinks);

        // 포함되어 있지 않은 태그 링크 삭제
        tagLinkCommonService.deleteTagLinksInBatch(removeTagLinks);

        if (newTagList != null) {
            List<SsbTrackTagLink> trackTagLinks = getTrackTagLinks(newTagList, ssbTrack);
            tagLinkCommonService.addTrackTagLinks(trackTagLinks);
        }

        boolean modifyPrivacy = trackInfoModifyReqDto.getIsPrivacy();

        // 비공개 -> 공개, 배포 false -> 최초 배포 일 경우에 Update 날짜 변경
        if (!modifyPrivacy && !ssbTrack.getIsRelease()) {
            feedService.updateReleaseDateTime(user, ssbTrack.getId(), ContentsType.TRACK, LocalDateTime.now());
        }
        // privacy 업데이트 내용이 다를경우 Repost isPrivacy 업데이트
        if (modifyPrivacy != ssbTrack.getIsPrivacy()) {
            repostCommonService.privacyAllUpdate(ssbTrack.getId(), modifyPrivacy, ContentsType.TRACK);
        }

        // 내용 수정
        SsbTrack.uploadInfo(ssbTrack, trackInfoModifyReqDto.getGenre(), trackInfoModifyReqDto.getGenreType(),
            trackInfoModifyReqDto.getIsPrivacy(), trackInfoModifyReqDto.getIsDownload(),
            trackInfoModifyReqDto.getTitle(),
            trackInfoModifyReqDto.getDesc());

        if (coverImgFile != null) {
            // 기존 이미지 삭제
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
                            .filter(newTags -> Objects.equals(newTags.getId(), oldTagLink.getSsbTrackTags().getId()))
                            .findFirst()
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
        duplicateTags.forEach(newTagList::remove);
    }


    /**
     * 권한 확인 후
     * track file token 반환
     *
     * @param id
     * @return
     */
    @Transactional
    public TrackPlayRepDto getAuthorizedTrackInfo(Long id, Status isStatus, String userAgent) {
        TrackPlayRepDto trackPlayRepDto;
        SsbTrack ssbTrack = findOneJoinUser(id, isStatus);
        // 요청 유저
        User user = null;
        boolean isMember = true;
        try {
            user = userQueryService.findOne();
        } catch (UserInfoNotFoundException e) {
            // 비회원인지 확인
            isMember = false;
        }
        boolean isOwnerPost = isMember && ssbTrack.getUser().equals(user); // 작성자인지 확인
        // 파일에 권한이 있는지 없는지 확인
        // isPrivacy : true 비공개 ,false 공개
        // isOwnerPost : true 사용자 자신의 게시물 , false 사용자 자신의 게시물 x
        if (ssbTrack.getIsPrivacy()) {// 비공개일 경우 재생권한이 있는지 확인
            trackPlayRepDto = isOwnerPost ? TrackPlayRepDto.create(ssbTrack) : null;
        } else { // 비공개가 아닐경우
            trackPlayRepDto = TrackPlayRepDto.create(ssbTrack);
        }

        // 비회원 조회수 측정 x
        // 자신의 track은 자신이 플레이를 해도 측정 x
        // 해당 트랙에 접근 권한이 없을 경우 x
        if (isMember && !isOwnerPost && trackPlayRepDto != null) {
            trackPlayMetricsService.addAllPlayLog(userAgent, trackPlayRepDto, ssbTrack, user);
        }
        return trackPlayRepDto;
    }

    @Transactional
    public void deleteTrack(Long id, String token) {
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = findOne(id, token, user, Status.ON);

        // 커버이미지 삭제
//        SsbTrack.deleteCoverImg(ssbTrack, fileStore);

        // Feed 삭제
        feedService.deleteFeed(user, ssbTrack.getId(), ContentsType.TRACK);

        // repost 삭제
        tagLinkCommonService.deleteTagLinksInBatch(ssbTrack.getTags());

        SsbTrack.deleteTrackFile(ssbTrack, fileStore);
        SsbTrack.changeStatus(ssbTrack, Status.OFF);
    }

    /**
     * cover Img upload
     *
     * @return
     * @throws IOException
     */
    public UploadFileDto getUploadFileDto(MultipartFile coverImgFile) {
        return fileStore.storeFileSave(coverImgFile,
            FileStore.COVER_TYPE, 500);
    }

    private SsbTrack createTrack(User user, TempTrackStorage tempTrack,
        Integer totalUploadTrackLength, Integer totalTrackLength,
        TrackInfoSaveReqDto metaDto) {

        // ssbTrack 생성
        SsbTrack ssbTrack = SsbTrack.create(metaDto, tempTrack, user);
        // token값 저장
        SsbTrack.updateToken(tempTrack.getToken(), ssbTrack);
        // 총 파일 트랙 길이 저장
        totalUploadTrackLength += tempTrack.getTrackLength();

        // 총업로드 제한 180분 이 넘는지 확인
        checkLimit(totalTrackLength, totalUploadTrackLength);
        return ssbTrack;
    }

    // 태그 등록
    private List<SsbTrackTagLink> getTrackTagLinks(List<SsbTrackTags> tags, SsbTrack ssbTrack) {

        return tags.stream().map(tag -> SsbTrackTagLink.createSsbTrackTagLink(
            ssbTrack, tag)).collect(Collectors.toList());
    }

    private void checkLimit(Integer totalTrackLength, Integer trackLength) {
        boolean isLengthOver = (totalTrackLength + trackLength) > FileStore.TRACK_UPLOAD_LIMIT;
        if (isLengthOver) {
            throw new SsbFileLengthLimitOverException();
        }
    }

    public UrlResource getSsbTrackFile(String fileName) {

        return fileStore.getUrlResource(FileStore.TRACK_DIR + fileName);
    }

}
