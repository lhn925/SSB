package sky.Sss.domain.track.service.playList;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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


    @Transactional
    public PlayListInfoDto addPlyAndTracks(PlayListSettingSaveDto playListSettingSaveDto,
        MultipartFile coverImgFile, User user, List<SsbTrackTags> ssbTrackTags) {
        // 플레이리스트 생성
        PlayListInfoDto playListInfoDto = addPly(user, playListSettingSaveDto, ssbTrackTags,
            coverImgFile);

        // 플레이 리스트에 user 추가
        List<TrackInfoRepDto> trackInfoRepDtoList = trackService.addTrackFiles(user, playListInfoDto.getId(),
            playListInfoDto.getCoverUrl(),
            playListInfoDto.getCreatedDateTime(), ssbTrackTags,playListSettingSaveDto.isPrivacy(),true,
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
        feedService.addFeedList(ssbFeedList,playListInfoDto.getCreatedDateTime());
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
        // tracks 삭제
        List<PlayListTrackDeleteDto> deleteList = playListSettingUpdateDto.getTrackDeleteDtoList();
        deleteTracks(ssbPlayListSettings, deleteList);
        // track order 수정
        List<PlayListTrackUpdateDto> orderList = playListSettingUpdateDto.getTrackUpdateDtoList();

        // delete 를 삭제하고 나서 orders를 수정하는게 나을까
        // 아님 그대로 놨두고 다시 불러왔을때 수정하는게 나을까

        // orders 수정
        changeOrders(ssbPlayListSettings, orderList);

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

    private void changeOrders(SsbPlayListSettings ssbPlayListSettings, List<PlayListTrackUpdateDto> orderList) {
        if (orderList != null && !orderList.isEmpty()) {
            orderList.forEach(orderDto -> {
                ssbPlayListSettings.getPlayListTracks().stream()
                    .filter(track -> Objects.equals(orderDto.getId(), track.getId()))
                    .findFirst().ifPresent(
                        ssbPlayListTracks -> SsbPlayListTracks.changeOrders(ssbPlayListTracks, orderDto.getOrder()));
            });
        }
    }

    private void deleteTracks(SsbPlayListSettings ssbPlayListSettings, List<PlayListTrackDeleteDto> deleteList) {
        if (deleteList != null && !deleteList.isEmpty()) {
            List<SsbPlayListTracks> removeList = new ArrayList<>();
            deleteList.forEach(trackDto -> {
                ssbPlayListSettings.getPlayListTracks().stream()
                    .filter(track -> Objects.equals(trackDto.getId(), track.getId())).findFirst()
                    .ifPresent(removeList::add);
            });
            plyTracksService.deleteTracksInBatch(removeList);
        }
    }

}
