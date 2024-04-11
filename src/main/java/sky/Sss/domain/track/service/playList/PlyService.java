package sky.Sss.domain.track.service.playList;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.feed.entity.SsbFeed;
import sky.Sss.domain.feed.service.FeedService;
import sky.Sss.domain.track.dto.playlist.PlayListInfoDto;
import sky.Sss.domain.track.dto.playlist.PlayListSettingSaveDto;
import sky.Sss.domain.track.dto.playlist.PlayListSettingUpdateDto;
import sky.Sss.domain.track.dto.playlist.PlayListTrackDeleteDto;
import sky.Sss.domain.track.dto.playlist.PlayListTrackUpdateDto;
import sky.Sss.domain.track.dto.playlist.redis.PlyTracksPositionRedisDto;
import sky.Sss.domain.track.dto.track.TrackInfoRepDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.repository.playList.PlySettingRepository;
import sky.Sss.domain.track.service.common.RepostCommonService;
import sky.Sss.domain.track.service.common.TagLinkCommonService;
import sky.Sss.domain.track.service.track.TrackService;
import sky.Sss.domain.track.service.track.TrackTagService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.TokenUtil;
import sky.Sss.global.file.dto.UploadFileDto;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PlyService {


    private final PlySettingRepository plySettingRepository;
    private final PlyTracksService plyTracksService;
    private final UserQueryService userQueryService;
    private final TrackService trackService;
    private final TrackTagService trackTagService;
    private final FeedService feedService;
    private final RepostCommonService repostCommonService;
    private final TagLinkCommonService tagLinkCommonService;
    private final RedisCacheService redisCacheService;
    private final int PLY_TRACK_LIMIT = 500;

    @Transactional
    public PlayListInfoDto addPlyAndTracks(PlayListSettingSaveDto playListSettingSaveDto,
        MultipartFile coverImgFile, List<SsbTrackTags> ssbTrackTags) {
        // 플레이리스트 사이즈 제한 500
        if (playListSettingSaveDto.getPlayListTrackInfoDtoList().size() > PLY_TRACK_LIMIT) {
            throw new IllegalArgumentException("ply.limit.error");
        }
        User user = userQueryService.findOne();
        // 플레이리스트 생성
        PlayListInfoDto playListInfoDto = addPly(user, playListSettingSaveDto, ssbTrackTags,
            coverImgFile);

        // 플레이 리스트에 user 추가
        List<TrackInfoRepDto> trackInfoRepDtoList = trackService.addTrackFiles(user, playListInfoDto.getId(),
            playListInfoDto.getCoverUrl(),
            playListInfoDto.getCreatedDateTime(), ssbTrackTags, playListInfoDto.getToken(), true,
            playListSettingSaveDto.getPlayListTrackInfoDtoList());

        List<SsbFeed> ssbFeedList = new ArrayList<>();

        // playList Feed
        SsbFeed playListFeed = SsbFeed.create(playListInfoDto.getId(), user,
            ContentsType.PLAYLIST);

        // 비공개가 아니면 peed 날짜 업로드
        SsbFeed.updateReleaseDateTime(playListFeed, playListInfoDto.getCreatedDateTime());

        // 추가
        ssbFeedList.add(playListFeed);

        // 각 track 에 Feed 업로드
        trackInfoRepDtoList.forEach(track -> {
            SsbFeed ssbFeed = SsbFeed.create(track.getId(), user, ContentsType.TRACK);
            SsbFeed.updateReleaseDateTime(ssbFeed, track.getCreatedDateTime());
            ssbFeedList.add(ssbFeed);
        });
        feedService.addFeedList(ssbFeedList, playListInfoDto.getCreatedDateTime());
        return playListInfoDto;
    }


    /**
     * 플레이리스트 생성
     *
     * @param playListSettingSaveDto
     * @param coverImgFile
     * @return
     */
    @Transactional
    public PlayListInfoDto addPly(User user, PlayListSettingSaveDto playListSettingSaveDto,
        List<SsbTrackTags> ssbTrackTagsList, MultipartFile coverImgFile) {
        // 플레이 리스트 저장
        SsbPlayListSettings ssbPlayListSettings = SsbPlayListSettings.create(playListSettingSaveDto,
            user);

        // Token 생성
        String playListToken = TokenUtil.getToken();
        //playList token 저장
        SsbPlayListSettings.updateToken(playListToken, ssbPlayListSettings);

        // 배포되었는지 확인
        SsbPlayListSettings.updateIsRelease(ssbPlayListSettings, !ssbPlayListSettings.getIsPrivacy());
        // 커버 이미지 업데이트
        String storeFileCoverName = null;

        if (coverImgFile != null) {
            storeFileCoverName = trackService.getUploadFileDto(coverImgFile).getStoreFileName();
        }
        // 커버 이미지 저장
        SsbPlayListSettings.updateCoverImg(storeFileCoverName, ssbPlayListSettings);

        // 등록
        plySettingRepository.save(ssbPlayListSettings);

        // 앨범 태그 링크 저장
        if (ssbTrackTagsList != null && !ssbTrackTagsList.isEmpty()) {
            List<SsbPlayListTagLink> playListTagLinks = getPlayListTagLinks(ssbTrackTagsList, ssbPlayListSettings);
            tagLinkCommonService.addPlyTagLinks(playListTagLinks);
        }
        return PlayListInfoDto.create(ssbPlayListSettings);

    }

    // 태그 등록
    public List<SsbPlayListTagLink> getPlayListTagLinks(List<SsbTrackTags> tags,
        SsbPlayListSettings ssbPlayListSettings) {
        if (tags != null && !tags.isEmpty()) {
            return tags.stream()
                .map(tag -> SsbPlayListTagLink.createSsbTrackTagLink(
                    ssbPlayListSettings, tag)).collect(Collectors.toList());
        }
        return null;
    }

    @Transactional
    public void updatePlyInfo(PlayListSettingUpdateDto playListSettingUpdateDto, MultipartFile coverImgFile) {
        if (playListSettingUpdateDto.getTagList().size() > 30) {
            throw new IllegalArgumentException("track.tag.size");
        }
        User user = userQueryService.findOne();
        SsbPlayListSettings ssbPlayListSettings = findOneJoinTracks(playListSettingUpdateDto.getId(),
            playListSettingUpdateDto.getToken(), user, Status.ON);
        /**
         * 메모리 사용량 증가.
         * 원소 삽입 / 삭제하는 연산 코드량 증가.(앞 / 뒤 링크 조절 필요)
         */
        // tracks 삭제
        List<PlayListTrackDeleteDto> deleteList = playListSettingUpdateDto.getTrackDeleteDtoList();

        List<SsbPlayListTracks> playListTracks = ssbPlayListSettings.getPlayListTracks();

        // track linked 수정
        List<PlayListTrackUpdateDto> updateList = playListSettingUpdateDto.getTrackUpdateDtoList();

        // 링크드 리스트를 이용하여
        // 변경할려는 특정 노드의 변경위치 기준 앞 노드의 child 링크와 뒷 노드의 parent 링크 변경
        // 변경노드의 ParentId 와 childId 변경
        // 변경노드를 기존에 링크 하고 있던 두 노드의 parentId와 childId 변경
        // orders 수정
        // 순서변경이 일어났을 경우만 업데이트
        // 총 두개
        // sort 된 배열 반납
        playListTracks = updateTrackLinked(playListTracks, updateList);

        // link 삭제
        deleteLinkTracks(playListTracks, deleteList);

        //  수정 태그 리스트
        List<SsbTrackTags> newTagList = trackTagService.getSsbTrackTags(playListSettingUpdateDto.getTagList());

        // 삭제 태그 링크
        List<SsbPlayListTagLink> removeTagLinks = new ArrayList<>();

        // 기존 태그 링크
        List<SsbPlayListTagLink> existTagLinks = ssbPlayListSettings.getTags();
        // 포함되어 있지 않은 태그 링크 삭제
        filterNewTags(newTagList, removeTagLinks, existTagLinks);

        tagLinkCommonService.delPlyTagLinksInBatch(removeTagLinks);

        boolean modifyPrivacy = playListSettingUpdateDto.isPrivacy();
        //태그 링크 추가

        List<SsbPlayListTagLink> playListTagLinks = getPlayListTagLinks(newTagList, ssbPlayListSettings);

        if (playListTagLinks != null && !playListTagLinks.isEmpty()) {
            tagLinkCommonService.addPlyTagLinks(playListTagLinks);
        }

        // 공개 이면서 공개 날짜 가 없을 경우
        // 비공개 -> 공개, 배포 false -> 최초 배포
        if (!playListSettingUpdateDto.isPrivacy() && !ssbPlayListSettings.getIsRelease()) {
            SsbFeed ssbFeed = feedService.findOne(user, ssbPlayListSettings.getId(), ContentsType.PLAYLIST);
            SsbFeed.updateReleaseDateTime(ssbFeed, LocalDateTime.now());
        }

        // privacy 업데이트 내용이 다를경우 Repost isPrivacy 업데이트
        if (modifyPrivacy != ssbPlayListSettings.getIsPrivacy()) {
            repostCommonService.privacyAllUpdate(ssbPlayListSettings.getId(), modifyPrivacy, ContentsType.PLAYLIST);
        }

        // 내용 수정
        SsbPlayListSettings.updateInfo(ssbPlayListSettings, playListSettingUpdateDto.getTitle(),
            playListSettingUpdateDto.getDesc(), playListSettingUpdateDto.getPlayListType(),
            playListSettingUpdateDto.isDownload(), playListSettingUpdateDto.isPrivacy());

        // 이미지 수정
        if (coverImgFile != null) {
            UploadFileDto uploadFileDto = trackService.getUploadFileDto(coverImgFile);
            SsbPlayListSettings.updateCoverImg(uploadFileDto.getStoreFileName(), ssbPlayListSettings);
        }
        // redis playList update
        if (!updateList.isEmpty() || !deleteList.isEmpty()) {
            SsbPlayListTracks startValue = playListTracks.stream().filter(track -> track.getParentId() == 0)
                .findFirst().orElse(null);
            if (startValue != null) {
                String key = RedisKeyDto.REDIS_PLY_POSITION_MAP_KEY;
                Map<Long, SsbPlayListTracks> tracksMap = playListTracks.stream()
                    .collect(Collectors.toMap(SsbPlayListTracks::getId, track -> track));
                Map<Integer, PlyTracksPositionRedisDto> positionMap = new HashMap<>();
                getPlyPositionRedisDto(startValue, tracksMap, positionMap);
                redisCacheService.upsertCacheMapValueByKey(positionMap,
                    key, ssbPlayListSettings.getToken());
            }
        }
    }

    @Transactional
    public void deletePly(Long id, String token) {
        User user = userQueryService.findOne();
        SsbPlayListSettings ssbPlayListSettings = findOneJoinTracks(id, token, user, Status.ON);
        //status 변경
        SsbPlayListSettings.changeStatus(ssbPlayListSettings, Status.OFF);

        feedService.deleteFeed(user, ssbPlayListSettings.getId(), ContentsType.PLAYLIST);
        // coverImg 삭제
//        SsbPlayListSettings.deleteCoverImg(fileStore, ssbPlayListSettings);
//        // link 삭제
        tagLinkCommonService.delPlyTagLinksInBatch(ssbPlayListSettings.getTags());
//        // tracks 삭제

        plyTracksService.deleteTracksInBatch(ssbPlayListSettings.getPlayListTracks());

    }

    public SsbPlayListSettings findOne(Long id, String token, User user, Status isStatus) {
        return plySettingRepository.findOne(id, token, user, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public SsbPlayListSettings findOneJoinTracks(Long id, String token, User user, Status isStatus) {
        return plySettingRepository.findOneJoinTracks(id, token, user, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public void filterNewTags(List<SsbTrackTags> newTagList, List<SsbPlayListTagLink> removeTagLinks,
        List<SsbPlayListTagLink> existTagLinks) {
        // 중복 태그
        List<SsbTrackTags> duplicateTags = new ArrayList<>();
        if (existTagLinks != null && !existTagLinks.isEmpty()) {// 기존 태그가 있는경우
            existTagLinks.forEach(oldTagLink -> {
                    if (newTagList == null) { // 기존 태그가 있는데 태그를 전부다 삭제 할때
                        removeTagLinks.add(oldTagLink);
                    } else { // 기존태그가 있고 업데이트 태그도 있을때
                        SsbTrackTags trackTag = newTagList.stream()
                            .filter(newTags -> newTags.getId().equals(oldTagLink.getSsbTrackTags().getId())).findFirst()
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
        if (newTagList != null && !newTagList.isEmpty()) {
            duplicateTags.forEach(newTagList::remove);
        }

    }

    /**
     * track linked 업데이트
     *
     * @param playListTracks
     * @param updateList
     * @return
     */
    @Transactional
    public List<SsbPlayListTracks> updateTrackLinked(List<SsbPlayListTracks> playListTracks,
        List<PlayListTrackUpdateDto> updateList) {
        if (playListTracks.size() <= 1 || updateList == null || updateList.isEmpty()) {
            return playListTracks;
        }
        Map<Long, SsbPlayListTracks> trackMap = playListTracks.stream()
            .collect(Collectors.toMap(SsbPlayListTracks::getId, track -> track));

        for (PlayListTrackUpdateDto dto : updateList) {
            SsbPlayListTracks updateLink = trackMap.get(dto.getId());
            if (updateLink == null) {
                continue;
            }
            // 업데이트할 포인터의 값
            Long currentParentId = dto.getParentId();
            Long currentChildId = dto.getChildId();

            // 이전 위치에 앞(child) 뒤(parent) 값 업데이트
            // 현재 위치에 앞(child) 뒤(parent) 값 업데이트
            // 값이 같지 않다면
            // 이전 위치의 부모와 자식 업데이트

            Long prevParentId = updateLink.getParentId();
            Long prevChildId = updateLink.getChildId();

            // 예를 들어 업데이트할 LinkedId 와 PrevLinkId가 둘다 똑같다면?
            // 순환 반복 문제 발생
            // currentParentId == prevChildId
            if (prevChildId.equals(currentParentId) && prevParentId.equals(currentChildId)) {
                throw new RuntimeException();
            }

            changeChildId(trackMap, prevParentId, prevChildId);
            changeParentId(trackMap, prevChildId, prevParentId);

            // 양 옆 포인터를 전부 업데이트 된 Id 로 변경
            // 현재 위치 에서 앞에 노드 child Id 값에다가 나의 아이디값 업데이트
            changeChildId(trackMap, currentParentId, updateLink.getId());
            // 현재 위치에서 이전 노드값에다가 나의 아이디를 Parent Id로 참조
            changeParentId(trackMap, currentChildId, updateLink.getId());

            // 업데이트 해야할 트랙

            // 현재 변경할려는 track 포인터 업데이트
            SsbPlayListTracks.changeParentId(updateLink, currentParentId);
            SsbPlayListTracks.changeChildId(updateLink, currentChildId);
        }
        List<SsbPlayListTracks> sortedList = new ArrayList<>();
        // 첫 번째 배열 구하기
        SsbPlayListTracks firstValue = playListTracks.stream().filter(ply -> ply.getParentId() == null)
            .findFirst().orElse(null);
        // 정렬 후
        addSortedList(firstValue, trackMap, sortedList);
        // 사이즈가 똑같지 않으면 error
        // 기초 포지션대로 정렬
        if (sortedList.size() != playListTracks.size()) {
            throw new IllegalArgumentException("track.linked.sorted.error");
        }
        // 정리
        return sortedList;
    }

    private static void changeChildId(Map<Long, SsbPlayListTracks> trackMap, Long parentId, Long childId) {
        // parent 위치를 가리 키고 있는 객체를 찾아서
        // child 포인터 변경
        if (parentId != null) {
            SsbPlayListTracks prevParent = trackMap.get(parentId);
            if (prevParent != null) {
                SsbPlayListTracks.changeChildId(prevParent, childId);
            }
        }
    }

    private static void changeParentId(Map<Long, SsbPlayListTracks> trackMap, Long childId, Long parentId) {
        // child 위치를 가리 키고 있는 객체를 찾아서
        // parentId 포인터 변경
        if (childId != null) {
            SsbPlayListTracks currentChild = trackMap.get(childId);
            if (currentChild != null) {
                SsbPlayListTracks.changeParentId(currentChild, parentId);
            }
        }
    }

    // 삭제할려는 Track link 가 연속되어 있는 경우
    // 쿼리 최적화를 위해
    // parentId 와 childId
    @Transactional
    public void deleteLinkTracks(List<SsbPlayListTracks> playListTracks, List<PlayListTrackDeleteDto> deleteList) {

        List<SsbPlayListTracks> removeList = new ArrayList<>();

        Map<Long, SsbPlayListTracks> trackMap = playListTracks.stream()
            .collect(Collectors.toMap(SsbPlayListTracks::getId, track -> track));
        deleteList.forEach(delete ->
            {
                SsbPlayListTracks ssbPlayListTracks = trackMap.get(delete.getId());
                if (ssbPlayListTracks != null) {
                    removeList.add(ssbPlayListTracks);
                }
            }
        );

        Set<Long> parentSearchIds = new HashSet<>();
        Set<Long> childSearchIds = new HashSet<>();
        // removeList 중에서 연속된 링크드 리스트가 있는지 확인
        for (SsbPlayListTracks findRemove : removeList) {
            playListTracks.remove(findRemove);

            // parentId 가 null 이 아닌 경우

            // 연속된 링크 데이터중 가장 상위 객체 단일 노드 삭제인 경우 자기 자신
            SsbPlayListTracks highestParent = findParentTrack(removeList, findRemove, parentSearchIds);

            // 연속된 링크 데이터중 가장 하위 객체 단일 노드 삭제인 경우 자기 자신
            SsbPlayListTracks lowestChild = findChildTrack(removeList, findRemove, childSearchIds);

            // 삭제 할려는 마지막 객체의 parent 객체값
            // 예시
            // 1 -> 2 -> 3 -> 4 링크드 배열이 있는데
            // 여기서 중간에 2나 3 혹은 둘다 없어졌을 경우
            // 만약에 둘다 없어졌을 경우를 생각해본다면
            // 2가 1을 가리키고 있는 포인터 값을 4에게 주입
            // 3이 4를 가리키고 있는 포인터 값을 1에게 주입

            if (lowestChild != null && highestParent != null) {
                // 가장 상위 객체에게 가장 하위 객체 id값
                SsbPlayListTracks parentValue = trackMap.get(highestParent.getParentId());
                if (parentValue != null) {
                    SsbPlayListTracks.changeChildId(parentValue, lowestChild.getChildId());
                }
                SsbPlayListTracks childValue = trackMap.get(lowestChild.getChildId());

                if (childValue != null) {
                    SsbPlayListTracks.changeParentId(childValue, highestParent.getParentId());
                }

            }
        }
        plyTracksService.deleteTracksInBatch(removeList);

    }

    private SsbPlayListTracks findParentTrack(List<SsbPlayListTracks> removeList, SsbPlayListTracks findRemove,
        Set<Long> searchIds) {
        SsbPlayListTracks currentTrack = findRemove;
        while (currentTrack != null && !searchIds.contains(currentTrack.getId())) {
            searchIds.add(currentTrack.getId());
            SsbPlayListTracks finalCurrentTrack = currentTrack; // Java lambda final or effectively final constraint
            currentTrack = removeList.stream()
                .filter(remove -> Objects.equals(remove.getChildId(), finalCurrentTrack.getId()))
                .findFirst()
                .orElse(null);
            if (currentTrack == null) {
                return finalCurrentTrack;
            }
        }
        return null;
    }


    private SsbPlayListTracks findChildTrack(List<SsbPlayListTracks> removeList, SsbPlayListTracks findRemove,
        Set<Long> searchIds) {
        SsbPlayListTracks currentTrack = findRemove;
        while (currentTrack != null && !searchIds.contains(currentTrack.getId())) {
            searchIds.add(currentTrack.getId());
            SsbPlayListTracks lastValidTrack = currentTrack; // 검사 하기 전 마지막 트랙
            currentTrack = removeList.stream()
                .filter(remove -> Objects.equals(remove.getParentId(), lastValidTrack.getId()))
                .findFirst()
                .orElse(null);
            if (currentTrack == null) {
                return lastValidTrack;
            }
        }

        return null;
    }

    private void addSortedList(SsbPlayListTracks startValue, Map<Long, SsbPlayListTracks> map,
        List<SsbPlayListTracks> sortedList) {
        SsbPlayListTracks currentValue = startValue;
        int index = 0;
        while (currentValue != null) {
            index++;
            sortedList.add(currentValue);
            currentValue = map.get(currentValue.getChildId());
            if (index > map.size()) { // 무한 루프 방지용
                throw new RuntimeException();
            }
        }
    }


    private void getPlyPositionRedisDto(SsbPlayListTracks startValue, Map<Long, SsbPlayListTracks> map,
        Map<Integer, PlyTracksPositionRedisDto> sortedMap) {
        SsbPlayListTracks currentValue = startValue;
        int position = 0;
        while (currentValue != null) {
            sortedMap.put(position, new PlyTracksPositionRedisDto(currentValue));
            position++;
            currentValue = map.get(currentValue.getChildId());
            if (position > map.size()) { // 무한 루프 방지용
                throw new RuntimeException();
            }
        }
    }

    private void addSortedMap(SsbPlayListTracks startValue, Map<Long, SsbPlayListTracks> map,
        Map<Integer, SsbPlayListTracks> sortedMap) {
        SsbPlayListTracks currentValue = startValue;
        int position = 0;
        while (currentValue != null) {
            sortedMap.put(position, currentValue);
            position++;
            currentValue = map.get(currentValue.getChildId());
            if (position > map.size()) { // 무한 루프 방지용
                throw new RuntimeException();
            }
        }
    }



}
