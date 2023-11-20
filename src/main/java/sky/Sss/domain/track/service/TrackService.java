package sky.Sss.domain.track.service;


import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.TrackFileUploadDto;
import sky.Sss.domain.track.dto.TrackPlayListFileDto;
import sky.Sss.domain.track.dto.TrackPlayListSettingDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlayListTracks;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.TrackLengthLimitOverException;
import sky.Sss.domain.track.repository.PlayListSettingRepository;
import sky.Sss.domain.track.repository.PlayListTracksRepository;
import sky.Sss.domain.track.repository.TrackRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.UserTokenUtil;
import sky.Sss.global.file.dto.UploadFileDto;
import sky.Sss.global.file.dto.UploadTrackFileDto;
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
    private final PlayListTracksRepository playListTracksRepository;

    /**
     * track 생성
     *
     * @param trackFileUploadDto
     * @throws IOException
     */
    @Transactional
    public void saveTrackFile(TrackFileUploadDto trackFileUploadDto) throws IOException {
        User user = userQueryService.findOne();

        // track/{fileToken}폴더/track 이름
        // cover/fileToken/cover
        UploadTrackFileDto uploadTrackFileDto = null;

        String fileToken = UserTokenUtil.getToken();

        uploadTrackFileDto = (UploadTrackFileDto) fileStore.storeFileSave(trackFileUploadDto.getTrackFile(),
            FileStore.TRACK_DIR,
            fileToken);

        // 총업로드 제한 180분 이 넘는지 확인
        Integer totalTrackLength = trackRepository.getTotalTrackLength(user);

        Integer trackLength = uploadTrackFileDto.getTrackLength();

        SsbTrack ssbTrack = SsbTrack.createSsbTrack(trackFileUploadDto, uploadTrackFileDto, user);

        //fileToken
        SsbTrack.updateToken(fileToken, ssbTrack);

        Boolean isLengthOver = (totalTrackLength + trackLength) > FileStore.TRACK_UPLOAD_LIMIT;
        checkLimit(ssbTrack, isLengthOver);

        // 트랙 커버 이미지 업데이트
        UploadFileDto uploadFileDto = fileStore.storeFileSave(trackFileUploadDto.getCoverImgFile(),
            FileStore.TRACK_COVER_DIR, fileToken, 800);
        SsbTrack.updateTrackCoverImg(uploadFileDto.getStoreFileName(), ssbTrack);
        trackRepository.save(ssbTrack);
    }

    private void checkLimit(SsbTrack ssbTrack, Boolean isLengthOver)
        throws IOException {

        // 현재 디비에 저장되어 있는 트랙의 길이가 180분을 넘길 경우
        //  저장 할려는 트랙의 길이와 현재 디비에 저장되어 있는 길이의 합이 180분을 넘길 경우
        if (isLengthOver) {
            // 저장했던 파일 삭제
            SsbTrack.deleteSsbTrack(ssbTrack, fileStore);
            SsbTrack.deleteSsbTrackCover(ssbTrack, fileStore);
            throw new TrackLengthLimitOverException();
        }
    }


    /**
     * 플레이리스트 생성
     *
     * @param trackPlayListSettingDto
     * @throws IOException
     */
    @Transactional
    public void saveTrackFiles(TrackPlayListSettingDto trackPlayListSettingDto) throws IOException {
        User user = userQueryService.findOne();

        // playList 안에 있는 Track 정보
        List<TrackPlayListFileDto> trackPlayListFileDtoList = trackPlayListSettingDto.getTrackPlayListFileDtoList();

        // ssbTrack 저장을 위한 Map 생성
        Map<Integer, SsbTrack> trackFileMap = new HashMap<>();

        Integer totalUploadTrackLength = 0;
        for (TrackPlayListFileDto trackPlayListFileDto : trackPlayListFileDtoList) {
            String fileToken = UserTokenUtil.getToken();
            // track 파일 저장
            UploadTrackFileDto uploadTrackFileDto = (UploadTrackFileDto) fileStore.storeFileSave(
                trackPlayListFileDto.getTrackFile(), FileStore.TRACK_DIR, fileToken);

            // ssbTrack 생성
            SsbTrack ssbTrack = SsbTrack.createSsbTrack(trackPlayListFileDto, uploadTrackFileDto, user);

            // token값 저장
            SsbTrack.updateToken(fileToken, ssbTrack);

            // 커버 이미지 저장
            UploadFileDto uploadFileDto = fileStore.storeFileSave(trackPlayListSettingDto.getCoverImgFile(),
                FileStore.TRACK_COVER_DIR, fileToken, 800);

            SsbTrack.updateTrackCoverImg(uploadFileDto.getStoreFileName(), ssbTrack);

            // 순서를 키값으로 저장
            trackFileMap.put(trackPlayListFileDto.getOrder(), ssbTrack);

            // 총 파일 트랙 길이 저장
            totalUploadTrackLength += uploadTrackFileDto.getTrackLength();
        }
        // 총업로드 제한 180분 이 넘는지 확인
        Integer totalTrackLength = trackRepository.getTotalTrackLength(user);
        Boolean isLengthOver = (totalTrackLength + totalUploadTrackLength) > FileStore.TRACK_UPLOAD_LIMIT;

        if (isLengthOver) {
            for (Integer key : trackFileMap.keySet()) {
                SsbTrack.deleteSsbTrack(trackFileMap.get(key), fileStore); // 트랙 삭제
                SsbTrack.deleteSsbTrackCover(trackFileMap.get(key), fileStore);// 커버 이미지 삭제
            }
            throw new TrackLengthLimitOverException();
        }

        String playListToken = UserTokenUtil.getToken();
        // 앨범 대표 이미지 저장 후
        UploadFileDto uploadFileDto = fileStore.storeFileSave(trackPlayListSettingDto.getCoverImgFile(),
            FileStore.TRACK_COVER_DIR, playListToken, 800);

        // 트랙 저장
        trackRepository.saveAll(trackFileMap.values());

        // 플레이 리스트 저장
        SsbPlayListSettings ssbPlayListSettings = SsbPlayListSettings.createSsbPlayListSettings(trackPlayListSettingDto,
            user);

        // token 저장
        SsbPlayListSettings.updatePlayListToken(playListToken, ssbPlayListSettings);

        // 커버 이미지 업데이트
        SsbPlayListSettings.updatePlayListCoverImg(uploadFileDto.getStoreFileName(), ssbPlayListSettings);
        playListSettingRepository.save(ssbPlayListSettings);

        // 플레이 리스트 구성 및 순서 저장
        List<SsbPlayListTracks> ssbPlayListTrackList = SsbPlayListTracks.createSsbPlayListTrackList(trackFileMap,
            ssbPlayListSettings);
        playListTracksRepository.saveAll(ssbPlayListTrackList);
    }


}
