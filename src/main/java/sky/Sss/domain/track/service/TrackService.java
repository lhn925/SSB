package sky.Sss.domain.track.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.dto.playlist.PlayListTrackInfoDto;
import sky.Sss.domain.track.dto.track.TrackInfoDto;
import sky.Sss.domain.track.dto.track.TrackInfoSaveDto;
import sky.Sss.domain.track.dto.playlist.PlayListInfoDto;
import sky.Sss.domain.track.dto.playlist.PlayListSettingDto;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.domain.track.dto.track.TrackInfoUpdateDto;
import sky.Sss.domain.track.entity.TempTrackStorage;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackTagLink;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.exception.SsbFileFileNotFoundException;
import sky.Sss.domain.track.exception.SsbFileLengthLimitOverException;
import sky.Sss.domain.track.repository.PlayListSettingRepository;
import sky.Sss.domain.track.repository.TrackRepository;
import sky.Sss.domain.user.entity.User;
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

    /**
     * track 생성
     *
     * @param trackInfoSaveDto
     * @throws IOException
     */
    @Transactional
    public TrackInfoDto saveTrackFile(TrackInfoSaveDto trackInfoSaveDto, MultipartFile coverImgFile, String sessionId) {
        User user = userQueryService.findOne();
        // 시간제한 180분
        // 임시 디비에 있던걸
        // ssbTrack 에 옮기는 작업
        TempTrackStorage tempTrackStorage = tempTrackStorageService.findOne(trackInfoSaveDto.getId(), sessionId,
            trackInfoSaveDto.getToken(), user);

        Set<TrackTagsDto> tagSet = trackInfoSaveDto.getTagSet();

        // 현재 ssbTrack 에 저장되어 있는 track
        Integer totalTrackLength = getTotalLength(user);

        // 태그 확인
        Set<SsbTrackTags> ssbTrackTags = getSsbTrackTags(tagSet);
        Integer totalUploadTrackLength = 0;
        SsbTrack ssbTrack = saveTrack(user, tempTrackStorage, totalUploadTrackLength, totalTrackLength,
            trackInfoSaveDto,
            ssbTrackTags);

        if (coverImgFile != null) {
            UploadFileDto uploadFileDto = getUploadFileDto(coverImgFile,
                tempTrackStorage.getToken());
            SsbTrack.updateTrackCoverImg(uploadFileDto.getStoreFileName(), ssbTrack);
        }
        tempTrackStorageService.delete(tempTrackStorage);
        return new TrackInfoDto(ssbTrack, user.getUserName());

    }

    /**
     * 플레이리스트 생성
     *
     * @param playListSettingDto
     * @throws IOException
     */
    @Transactional
    public PlayListInfoDto saveTrackFiles(PlayListSettingDto playListSettingDto, MultipartFile coverImgFile,
        String sessionId) {
        User user = userQueryService.findOne();
        // playList 안에 있는 Track 정보
        List<PlayListTrackInfoDto> trackPlayListFileDtoList = playListSettingDto.getPlayListTrackInfoDtoList();
        // 플레이 리스트 저장
        SsbPlayListSettings ssbPlayListSettings = SsbPlayListSettings.createSsbPlayListSettings(playListSettingDto,
            user);
        // Token 생성
        String playListToken = UserTokenUtil.getToken();
        //playList token 저장
        SsbPlayListSettings.updatePlayListToken(playListToken, ssbPlayListSettings);
        // ssbTrack 저장을 위한 Map 생성
        Map<Integer, SsbTrack> trackFileMap = new HashMap<>();

        // 태그 조회
        Set<SsbTrackTags> ssbTrackTags = getSsbTrackTags(playListSettingDto.getTagSet());
        Integer totalTrackLength = getTotalLength(user);

        List<TempTrackStorage> tempList = new ArrayList<>();

        // upload 할 length 저장
        Integer totalUploadTrackLength = 0;
        for (PlayListTrackInfoDto metaDto : trackPlayListFileDtoList) {
            TempTrackStorage tempTrack = tempTrackStorageService.findOne(metaDto.getId(), sessionId, metaDto.getToken(),
                user);
            // ssbTrack 저장
            SsbTrack ssbTrack = saveTrack(user, tempTrack, totalUploadTrackLength, totalTrackLength, metaDto,
                ssbTrackTags);

            tempList.add(tempTrack);
            // tag 저장
            // 순서를 키값으로 저장
            trackFileMap.put(metaDto.getOrder(), ssbTrack);
        }
        // 앨범 태그 저장
        if (ssbTrackTags != null) {
            List<SsbPlayListTagLink> playListTagLinks = getPlayListTagLinks(ssbTrackTags, ssbPlayListSettings);
            SsbPlayListSettings.addPlayListTagLink(ssbPlayListSettings, playListTagLinks);
        }
        // 플레이 리스트 구성 및 순서 저장
        SsbPlayListTracks.createSsbPlayListTrackList(trackFileMap,
            ssbPlayListSettings);

        // 임시파일 DB에서 삭제
        for (TempTrackStorage tempTrackStorage : tempList) {
            tempTrackStorageService.delete(tempTrackStorage);
        }

        // 커버 이미지 업데이트
        if (coverImgFile != null) {
            for (Integer key : trackFileMap.keySet()) {
                trackFileMap.get(key);
                UploadFileDto trackUploadFileDto = getUploadFileDto(coverImgFile,
                    trackFileMap.get(key).getToken());
                SsbTrack.updateTrackCoverImg(trackUploadFileDto.getStoreFileName(), trackFileMap.get(key));
            }
            UploadFileDto playListUploadFileDto = getUploadFileDto(coverImgFile,
                playListToken);
            SsbPlayListSettings.updatePlayListCoverImg(playListUploadFileDto.getStoreFileName(), ssbPlayListSettings);
        }
        // 등록
        playListSettingRepository.save(ssbPlayListSettings);

        return new PlayListInfoDto(ssbPlayListSettings);
    }

    @Transactional
    public void updateTrackInfo(TrackInfoUpdateDto trackInfoUpdateDto, MultipartFile coverImgFile) {
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = findOne(trackInfoUpdateDto.getId(), trackInfoUpdateDto.getToken(),
            user);
        if (coverImgFile != null) {
            // 기존 이미지 삭제
            SsbTrack.deleteSsbTrackCover(ssbTrack, fileStore);

            UploadFileDto uploadFileDto = getUploadFileDto(coverImgFile, ssbTrack.getToken());
            SsbTrack.updateTrackCoverImg(uploadFileDto.getStoreFileName(), ssbTrack);
        }
        // 태그 수정
        Set<SsbTrackTags> ssbTrackTags = getSsbTrackTags(trackInfoUpdateDto.getTagSet());
        List<SsbTrackTagLink> trackTagLinks = getTrackTagLinks(ssbTrackTags, ssbTrack);
        SsbTrack.addTagLink(ssbTrack, trackTagLinks);
        // 내용 수정
        SsbTrack.uploadTrackInfo(ssbTrack, trackInfoUpdateDto.getGenre(), trackInfoUpdateDto.getGenreType(),
            trackInfoUpdateDto.isPrivacy(), trackInfoUpdateDto.isDownload(), trackInfoUpdateDto.getTitle(),
            trackInfoUpdateDto.getDesc());
    }


    @Transactional
    public void deleteTrack(Long id, String token) {
        User user = userQueryService.findOne();
        SsbTrack ssbTrack = findOne(id, token, user);

        if (ssbTrack.getCoverUrl() != null) {
            SsbTrack.deleteSsbTrackCover(ssbTrack, fileStore);
        }
        SsbTrack.deleteSsbTrack(ssbTrack, fileStore);
        SsbTrack.changeEnabled(ssbTrack, true);
    }

    public SsbTrack findOne(Long id, String token, User user) {
        return trackRepository.findOne(id, user, token, false).orElseThrow(() -> new SsbFileFileNotFoundException());
    }

    /**
     * cover Img upload
     *
     * @return
     * @throws IOException
     */
    private UploadFileDto getUploadFileDto(MultipartFile coverImgFile, String token) {
        UploadFileDto uploadFileDto = fileStore.storeFileSave(coverImgFile,
            FileStore.TRACK_COVER_DIR, token, 800);
        return uploadFileDto;
    }

    private SsbTrack saveTrack(User user, TempTrackStorage tempTrack,
        Integer totalUploadTrackLength, Integer totalTrackLength,
        TrackInfoSaveDto metaDto, Set<SsbTrackTags> ssbTrackTags) {



        // ssbTrack 생성
        SsbTrack ssbTrack = SsbTrack.createSsbTrack(metaDto, tempTrack, user);
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

        // 트랙 저장
        trackRepository.save(ssbTrack);
        return ssbTrack;
    }

    public Integer getTotalLength(User user) {
        Integer totalTrackLength = trackRepository.getTotalTrackLength(user, false);
        return totalTrackLength;
    }

    // 태그 등록
    private List<SsbTrackTagLink> getTrackTagLinks(Set<SsbTrackTags> tags, SsbTrack ssbTrack) {
        if (tags != null && tags.size() > 0) {
            List<SsbTrackTagLink> ssbTrackTagLinks = tags.stream().map(tag -> SsbTrackTagLink.createSsbTrackTagLink(
                ssbTrack, tag)).collect(Collectors.toList());
            return ssbTrackTagLinks;
        }
        return null;
    }

    // 태그 등록
    private List<SsbPlayListTagLink> getPlayListTagLinks(Set<SsbTrackTags> tags,
        SsbPlayListSettings ssbPlayListSettings) {
        if (tags.size() > 0) {
            List<SsbPlayListTagLink> ssbTrackTagLinks = tags.stream()
                .map(tag -> SsbPlayListTagLink.createSsbTrackTagLink(
                    ssbPlayListSettings, tag)).collect(Collectors.toList());

            return ssbTrackTagLinks;
        }
        return null;
    }


    // DB 태그 여부 가져오기
    private Set<SsbTrackTags> getSsbTrackTags(Set<TrackTagsDto> tagSet) {
        if (tagSet != null && tagSet.size() > 0) {
            Set<SsbTrackTags> tags = new HashSet<>();
            tagSet.forEach(tag -> {
                tags.add(trackTagService.getTags(tag.getTag()));
            });
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

}
