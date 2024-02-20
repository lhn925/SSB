package sky.Sss.domain.track.service.playList;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.track.dto.playlist.PlayListInfoDto;
import sky.Sss.domain.track.dto.playlist.PlayListSettingSaveDto;
import sky.Sss.domain.track.dto.playlist.PlayListSettingUpdateDto;
import sky.Sss.domain.track.dto.playlist.PlayListTrackDeleteDto;
import sky.Sss.domain.track.dto.playlist.PlayListTrackUpdateDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlayListTagLink;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.entity.track.SsbTrackTags;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.repository.playList.PlySettingRepository;
import sky.Sss.domain.track.service.track.TrackService;
import sky.Sss.domain.track.service.track.TrackTagService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.file.dto.UploadFileDto;
import sky.Sss.global.file.utili.FileStore;

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


    /**
     * 플레이리스트 생성 및 트랙파일 저장
     *
     * @param playListSettingSaveDto
     * @param coverImgFile
     * @param sessionId
     * @return
     */
    @Transactional
    public PlayListInfoDto addPly(PlayListSettingSaveDto playListSettingSaveDto, MultipartFile coverImgFile,
        String sessionId) {
        return trackService.addTrackFiles(playListSettingSaveDto, coverImgFile, sessionId);
    }

    @Transactional
    public void updatePlyInfo(PlayListSettingUpdateDto playListSettingUpdateDto, MultipartFile coverImgFile) {
        User user = userQueryService.findOne();
        SsbPlayListSettings ssbPlayListSettings = findOne(playListSettingUpdateDto.getId(),
            playListSettingUpdateDto.getToken(), user,Status.ON);
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
        List<SsbTrackTags> newTagList = trackService.getSsbTrackTags(playListSettingUpdateDto.getTagList());

        // 삭제 태그 링크
        List<SsbPlayListTagLink> removeTagLinks = new ArrayList<>();

        // 기존 태그 링크
        List<SsbPlayListTagLink> existTagLinks = ssbPlayListSettings.getTags();
        // 포함되어 있지 않은 태그 링크 삭제
        filterNewTags(newTagList, removeTagLinks, existTagLinks);

        trackTagService.delPlyTagLinksInBatch(removeTagLinks);

        //태그 링크 추가
        List<SsbPlayListTagLink> playListTagLinks = trackService.getPlayListTagLinks(newTagList, ssbPlayListSettings);
        SsbPlayListSettings.addTagLink(ssbPlayListSettings, playListTagLinks);

        // 내용 수정
        SsbPlayListSettings.updateInfo(ssbPlayListSettings, playListSettingUpdateDto.getTitle(),
            playListSettingUpdateDto.getDesc(), playListSettingUpdateDto.getPlayListType(),
            playListSettingUpdateDto.isDownload(), playListSettingUpdateDto.isPrivacy());

        // 이미지 수정
        if (coverImgFile != null) {
//            SsbPlayListSettings.deleteCoverImg(fileStore, ssbPlayListSettings);
            UploadFileDto uploadFileDto = trackService.getUploadFileDto(coverImgFile);
            SsbPlayListSettings.updateCoverImg(uploadFileDto.getStoreFileName(), ssbPlayListSettings);
        }
    }

    @Transactional
    public void deletePly(Long id, String token) {
        User user = userQueryService.findOne();
        SsbPlayListSettings ssbPlayListSettings = findOne(id, token, user, Status.ON);
        //status 변경
        SsbPlayListSettings.changeStatus(ssbPlayListSettings, Status.OFF);

        // coverImg 삭제
//        SsbPlayListSettings.deleteCoverImg(fileStore, ssbPlayListSettings);
//        // link 삭제
        trackTagService.delPlyTagLinksInBatch(ssbPlayListSettings.getTags());
//        // tracks 삭제

        plyTracksService.deleteTracksInBatch(ssbPlayListSettings.getPlayListTracks());

    }

    public SsbPlayListSettings findOne(Long id, String token, User user, Status isStatus) {
        return plySettingRepository.findOne(id, token, user, isStatus.getValue())
            .orElseThrow(() -> new SsbFileNotFoundException());
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

    private void changeOrders(SsbPlayListSettings ssbPlayListSettings, List<PlayListTrackUpdateDto> orderList) {
        if (orderList != null && !orderList.isEmpty()) {
            orderList.forEach(orderDto -> {
                SsbPlayListTracks ssbPlayListTracks = ssbPlayListSettings.getPlayListTracks().stream()
                    .filter(track -> orderDto.getId() == track.getId())
                    .findFirst().orElse(null);
                if (ssbPlayListTracks != null) {
                    SsbPlayListTracks.changeOrders(ssbPlayListTracks, orderDto.getOrder());
                }
            });
        }
    }

    private void deleteTracks(SsbPlayListSettings ssbPlayListSettings, List<PlayListTrackDeleteDto> deleteList) {
        if (deleteList != null && !deleteList.isEmpty()) {
            List<SsbPlayListTracks> removeList = new ArrayList<>();
            deleteList.forEach(trackDto -> {
                SsbPlayListTracks removeTracks = ssbPlayListSettings.getPlayListTracks().stream()
                    .filter(track -> trackDto.getId() == track.getId()).findFirst().orElse(null);
                if (removeTracks != null) {
                    removeList.add(removeTracks);
                }
            });
            plyTracksService.deleteTracksInBatch(removeList);
        }
    }

}
