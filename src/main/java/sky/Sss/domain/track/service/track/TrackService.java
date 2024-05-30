package sky.Sss.domain.track.service.track;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.feed.entity.SsbFeed;
import sky.Sss.domain.feed.service.FeedService;
import sky.Sss.domain.track.dto.BaseTrackDto;
import sky.Sss.domain.track.dto.playlist.PlayListTrackInfoReqDto;
import sky.Sss.domain.track.dto.playlist.redis.PlyTracksPositionRedisDto;
import sky.Sss.domain.track.dto.track.redis.RedisTrackDto;
import sky.Sss.domain.track.dto.track.rep.TrackInfoRepDto;
import sky.Sss.domain.track.dto.track.req.TrackInfoSaveReqDto;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.domain.track.dto.track.req.TrackInfoModifyReqDto;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;
import sky.Sss.domain.track.dto.track.rep.TrackPlayRepDto;
import sky.Sss.domain.track.dto.track.reply.TracksInfoReqDto;
import sky.Sss.domain.track.entity.temp.TempTrackStorage;
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
import sky.Sss.domain.user.service.follows.UserFollowsService;
import sky.Sss.global.file.dto.UploadFileDto;
import sky.Sss.global.file.utili.FileStore;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

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
    private final TrackLikesService trackLikesService;
    private final FeedService feedService;
    private final RepostCommonService repostCommonService;
    private final UserFollowsService userFollowsService;

    private final PlyTracksService plyTracksService;
    private final RedisCacheService redisCacheService;


    /**
     * track 생성
     *
     * @param trackInfoSaveReqDto
     * @throws IOException
     */
    @Transactional
    public TrackInfoRepDto addTrackFile(TrackInfoSaveReqDto trackInfoSaveReqDto, MultipartFile coverImgFile) {

        User user = userQueryService.findOne();
        // 시간제한 180분
        // 임시 디비에 있던걸
        // ssbTrack 에 옮기는 작업
        TempTrackStorage tempTrackStorage = tempTrackStorageService.findOne(trackInfoSaveReqDto.getId(),
            trackInfoSaveReqDto.getToken(), user, false);

        // 현재 ssbTrack 에 저장되어 있는 track
        Integer totalTrackLength = getTotalLength(user);

        // 업로드 하기전 check
        checkLimit(totalTrackLength, 0);

        Integer totalUploadTrackLength = tempTrackStorage.getTrackLength();

        checkLimit(totalTrackLength, totalUploadTrackLength);

        SsbTrack ssbTrack = createTrack(user, tempTrackStorage,
            trackInfoSaveReqDto);

        // 사용자에게 한번 배포가 되었는지 확인
        // true: 배포함 ,False : 배포되지 않음
        SsbTrack.updateIsRelease(ssbTrack, !ssbTrack.getIsPrivacy());

        String storeFileName = null;
        if (coverImgFile != null) { // 저장할 이미지가 있으면 업로드

            storeFileName = getUploadFileDto(coverImgFile).getStoreFileName();
        }
        SsbTrack.updateCoverImg(storeFileName, ssbTrack);

        // 트랙 저장
        trackRepositoryImpl.save(ssbTrack);

        List<TrackTagsDto> tagList = trackInfoSaveReqDto.getTagList();
        // 태그 확인
        List<SsbTrackTags> ssbTrackTags = trackTagService.getSsbTrackTags(tagList);
        // 링크 연결
        List<SsbTrackTagLink> trackTagLinks = getTrackTagLinks(ssbTrackTags, ssbTrack);
        tagLinkCommonService.addTrackTagLinks(trackTagLinks);

        tempTrackStorageService.delete(tempTrackStorage);

        SsbFeed ssbFeed = SsbFeed.create(ssbTrack.getId(), user, ContentsType.TRACK);
        SsbFeed.updateReleaseDateTime(ssbFeed, ssbTrack.getCreatedDateTime());

        // 비공개가 아니면
        feedService.addFeed(ssbFeed);

        return TrackInfoRepDto.create(ssbTrack);

    }


    // 여기
    public SsbTrack getEntityTrack(Long id, String token, User user, Status isStatus) {
        return trackQueryService.getEntityTrack(id, token, user, isStatus);
    }

//    public SsbTrack findOne(Long id, String token, Status isStatus) {
//        return trackQueryService.findOne(id, token, isStatus);
//    }

    public SsbTrack findOneJoinUser(Long id, Status isStatus) {
        return trackQueryService.findOneJoinUser(id, isStatus);
    }

    public Integer getTotalLength(User user) {
        return trackQueryService.getTotalLength(user);
    }
    public Integer getTotalLength() {
        User user = userQueryService.findOne();
        return getTotalLength(user);
    }

    /**
     * 다중 Track 저장 후 tokenList 반납
     *
     * @throws IOException
     */
    @Transactional
    public List<TrackInfoRepDto> addTrackFiles(User user, long settingsId, String coverUrl,
        LocalDateTime createdDateTime,
        List<SsbTrackTags> ssbTrackTags, String plyToken, boolean isPlayList,
        List<PlayListTrackInfoReqDto> trackPlayListFileDtoList) {
        // playList 안에 있는 Track 정보

        // ssbTrack 저장을 위한 Map 생성
        Map<Integer, SsbTrack> trackFileMap = new HashMap<>();

        // 트랙 토큰
        List<String> tokenList = trackPlayListFileDtoList.stream().map(BaseTrackDto::getToken)
            .toList();
        // 임시파일 id
        List<Long> tempIdList = trackPlayListFileDtoList.stream().map(BaseTrackDto::getId).toList();

        // 임시파일 리스트
        List<TempTrackStorage> tempList = tempTrackStorageService.findByList(user, tokenList, tempIdList,
            isPlayList);

        // 사이즈가 맞지 않는 경우
        if (tempList.size() != trackPlayListFileDtoList.size()) {
            throw new SsbFileNotFoundException();
        }

        // 현재 유저가 업로드한 트랙 길이
        Integer totalTrackLength = getTotalLength(user);

        // 다른 트랙파일을 업로드하기전에 이미 업로드된 Track File  확인 후
        // 초과면 error
        checkLimit(totalTrackLength, 0);

        // 태그 조회
        List<SsbTrack> saveTracks = new ArrayList<>();

        // 외래키 연결을 위한 객체 생성
        SsbPlayListSettings ssbPlayListSettings = SsbPlayListSettings.builder().id(settingsId).build();

        // upload 할 length 저장
        int totalUploadTrackLength = 0;

        // temp TotalLength 전부 더하기
        boolean isDelete = false;
        Map<Integer, PlayListTrackInfoReqDto> dtoMap = trackPlayListFileDtoList.stream()
            .collect(Collectors.toMap(PlayListTrackInfoReqDto::getOrder, dto -> dto));

        Map<String, TempTrackStorage> tempMap = tempList.stream()
            .collect(Collectors.toMap(TempTrackStorage::getToken, temp -> temp));

        int size = dtoMap.size();
        for (int i = 0; i < size; i++) {
            PlayListTrackInfoReqDto playListTrackInfoReqDto = dtoMap.get(i);
            totalUploadTrackLength += tempMap.get(playListTrackInfoReqDto.getToken()).getTrackLength();
            try {
                // 180분을 정확히 채우는건 힘드니 조금 넘겨서 업로드
                checkLimit(totalTrackLength, totalUploadTrackLength);
            } catch (SsbFileLengthLimitOverException e) {
                // isDelete 를 true 로 변경 뒤 남은 Track 은 전부 다 삭제
                // 총 길이가 220 분을 넘는 하나당 22분을 짜리의  트랙이  10개가 있다고 치면
                // 9개만 허용해서 총 길이 198분만 업로드
                if (isDelete) {
                    dtoMap.remove(i);
                } else {
                    isDelete = true;
                }
            }
        }
        for (int order : dtoMap.keySet()) {
            PlayListTrackInfoReqDto metaDto = dtoMap.get(order);

            TempTrackStorage tempTrack = tempMap.get(metaDto.getToken());

            if (tempTrack == null) {
                throw new SsbFileNotFoundException();
            }
            // ssbTrack 저장
            SsbTrack ssbTrack = createTrack(user, tempTrack, metaDto);

            SsbTrack.updateIsRelease(ssbTrack, !ssbTrack.getIsPrivacy());
            // tag 저장
            // 순서를 키값으로 저장
            trackFileMap.put(metaDto.getOrder(), ssbTrack);
            saveTracks.add(ssbTrack);
        }
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

        plyTracksService.addPlayListTracks(ssbPlayListTrackList, createdDateTime);

        // 링크드 연결 처리를 위해 다시 search
        // 전체 리스트가 1보다 클 경우
        Map<Integer, PlyTracksPositionRedisDto> redisDtoMap = new HashMap<>();

        String key = RedisKeyDto.REDIS_PLY_POSITION_MAP_KEY;
        if (ssbPlayListTrackList.size() > 0) {
            List<SsbPlayListTracks> savedPlyTracks = plyTracksService.findByPlyTracks(ssbPlayListSettings.getId(),
                Sort.by(Order.asc("position")));
            Map<Integer, SsbPlayListTracks> savePlyTrackMap = savedPlyTracks.stream()
                .collect(Collectors.toMap(SsbPlayListTracks::getPosition, save -> save));

            for (SsbPlayListTracks plyTrack : savedPlyTracks) {
                // parentId 저장
//                savedPlyTracks.stream().filter(data -> data.getPosition() == (plyTrack.getPosition() - 1)).
//                    findFirst().ifPresent(find -> SsbPlayListTracks.changeParentId(plyTrack, find.getId()));
                Long findParentId = 0L;
                if (plyTrack.getPosition() != 0) {
                    findParentId = savePlyTrackMap.get(plyTrack.getPosition() - 1).getId();
                }
                SsbPlayListTracks.changeParentId(plyTrack, findParentId);

                Long findChildId = 0L;
                if ((plyTrack.getPosition() + 1) != savedPlyTracks.size()) {
                    findChildId = savePlyTrackMap.get(plyTrack.getPosition() + 1).getId();
                }
                // 포지션이 전체 size 보다 크지 않을경우에만
                SsbPlayListTracks.changeChildId(plyTrack, findChildId);

                // redis 추가
                redisDtoMap.put(plyTrack.getPosition(), new PlyTracksPositionRedisDto(plyTrack));
            }
            redisCacheService.upsertCacheMapValueByKey(redisDtoMap, key, plyToken);
        }
        return trackInfoList;
    }

    @Transactional
    public void updateTrackInfo(TrackInfoModifyReqDto trackInfoModifyReqDto, MultipartFile coverImgFile) {
        if (trackInfoModifyReqDto.getTagList().size() > 30) {
            throw new IllegalArgumentException("track.tag.size");
        }

        User user = userQueryService.findOne();

        SsbTrack ssbTrack = getEntityTrack(trackInfoModifyReqDto.getId(), trackInfoModifyReqDto.getToken(),
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

        boolean modifyPrivacy = trackInfoModifyReqDto.isPrivacy();

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
            trackInfoModifyReqDto.isPrivacy(), trackInfoModifyReqDto.isDownload(),
            trackInfoModifyReqDto.getTitle(),
            trackInfoModifyReqDto.getDesc());

        if (coverImgFile != null) {
            // 기존 이미지 삭제
            UploadFileDto uploadFileDto = getUploadFileDto(coverImgFile);
            SsbTrack.updateCoverImg(uploadFileDto.getStoreFileName(), ssbTrack);
        }
        trackQueryService.setTrackIdInRedis(RedisTrackDto.create(ssbTrack));

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
            return null;
        }
        boolean isOwnerPost = ssbTrack.getUser().getToken().equals(user.getToken()); // 작성자인지 확인
        // 파일에 권한이 있는지 없는지 확인
        // isPrivacy : true 비공개 ,false 공개
        // isOwnerPost : true 사용자 자신의 게시물 , false 사용자 자신의 게시물 x
        // 비공개인데 자신의 권한이 없을 경우
        if (ssbTrack.getIsPrivacy() && !isOwnerPost) {// 비공개일 경우 재생권한이 있는지 확인
            return null;
        }
        trackPlayRepDto = TrackPlayRepDto.create(ssbTrack);
        // 비회원 플레이 X
        // 해당 트랙에 접근 권한이 없을 경우 플레이 x
        trackPlayMetricsService.addAllPlayLog(userAgent, trackPlayRepDto, ssbTrack, user);
        updateIsOwner(trackPlayRepDto, user, isOwnerPost);

        return trackPlayRepDto;
    }

    public TrackInfoSimpleDto getTrackInfoSimpleDto(long id) {
        User user = null;
        boolean isMember = true;
        try {
            user = userQueryService.findOne();
        } catch (UserInfoNotFoundException e) {
            // 비회원인지 확인
            isMember = false;
        }
        List<TrackInfoSimpleDto> simpleDtoList;
        Set<Long> ids = new HashSet<>();
        ids.add(id);
        if (isMember) {
            simpleDtoList = trackQueryService.getTrackInfoSimpleDtoList(ids, user, Status.ON);
        } else {
            simpleDtoList = trackQueryService.getTrackInfoSimpleDtoList(ids, Status.ON);
        }

        if (simpleDtoList.isEmpty()) {
            return null;
        }
        return simpleDtoList.get(0);
    }

    public List<TrackInfoSimpleDto> getTrackInfoSimpleDtoList(TracksInfoReqDto tracksInfoReqDto) {
        Set<Long> idSet = new HashSet<>(tracksInfoReqDto.getIds());
        User user = null;
        boolean isMember = true;
        try {
            user = userQueryService.findOne();
        } catch (UserInfoNotFoundException e) {
            // 비회원인지 확인
            isMember = false;
        }
        List<TrackInfoSimpleDto> simpleDtoList = null;
        if (isMember) {
            simpleDtoList = trackQueryService.getTrackInfoSimpleDtoList(idSet, user, Status.ON);
        } else {
            // 비회원일 경우 liked 포함 X
            simpleDtoList = trackQueryService.getTrackInfoSimpleDtoList(idSet, Status.ON);
        }
        return simpleDtoList;
    }


    private void updateIsOwner(TrackInfoSimpleDto trackInfoSimpleDto, User user,
        boolean isOwnerPost) {
        // coverUrl이 없을 경우 user 프로필 사진으로 대체
        if (trackInfoSimpleDto.getCoverUrl() == null) {
            TrackInfoSimpleDto.updateCoverUrl(trackInfoSimpleDto, user.getPictureUrl());
        }
        TrackInfoSimpleDto.updateIsOwner(trackInfoSimpleDto, isOwnerPost);
        // 자신의 트랙이 아닐 경우
        // token null
        if (!isOwnerPost) {
            TrackInfoSimpleDto.updateToken(trackInfoSimpleDto, null);
        }
    }

    @Transactional
    public void deleteTrack(Long id, String token) {
        User user = userQueryService.findOne();
        // 여기
        SsbTrack ssbTrack =  getEntityTrack(id, token, user, Status.ON);

        // Feed 삭제
        feedService.deleteFeed(user, ssbTrack.getId(), ContentsType.TRACK);

        // repost 삭제
        tagLinkCommonService.deleteTagLinksInBatch(ssbTrack.getTags());

        // 캐시삭제
        redisCacheService.delete(RedisKeyDto.REDIS_USER_TOTAL_LENGTH_MAP_KEY + "::" + user.getUserId());
        SsbTrack.deleteTrackFile(ssbTrack, fileStore);
        SsbTrack.changeStatus(ssbTrack, Status.OFF);
        trackQueryService.setTrackIdInRedis(RedisTrackDto.create(ssbTrack));
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
        TrackInfoSaveReqDto metaDto) {

        // ssbTrack 생성
        SsbTrack ssbTrack = SsbTrack.create(metaDto, tempTrack, user);
        // token값 저장
        SsbTrack.updateToken(tempTrack.getToken(), ssbTrack);
        // 총 파일 트랙 길이 저장
        return ssbTrack;
    }

    // 태그 등록
    private List<SsbTrackTagLink> getTrackTagLinks(List<SsbTrackTags> tags, SsbTrack ssbTrack) {
        if (!tags.isEmpty()) {
            return tags.stream().map(tag -> SsbTrackTagLink.createSsbTrackTagLink(
                ssbTrack, tag)).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    private void checkLimit(Integer totalTrackLength, Integer totalUploadTrackLength) {
        boolean isLengthOver = (totalTrackLength + totalUploadTrackLength) > FileStore.TRACK_UPLOAD_LIMIT;
        if (isLengthOver) {
            throw new SsbFileLengthLimitOverException();
        }
    }

    public UrlResource getSsbTrackFile(String fileName) {

        return fileStore.getUrlResource(FileStore.TRACK_DIR + fileName);
    }

}
