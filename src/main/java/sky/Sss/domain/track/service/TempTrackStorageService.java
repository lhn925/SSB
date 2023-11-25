package sky.Sss.domain.track.service;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.temp.TempTrackInfoDto;
import sky.Sss.domain.track.dto.temp.TempTrackFileUploadDto;
import sky.Sss.domain.track.entity.TempTrackStorage;
import sky.Sss.domain.track.exception.SsbFileFileNotFoundException;
import sky.Sss.domain.track.repository.TempTrackStorageRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.UserTokenUtil;
import sky.Sss.global.file.dto.UploadTrackFileDto;
import sky.Sss.global.file.utili.FileStore;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TempTrackStorageService {

    private final TempTrackStorageRepository tempTrackStorageRepository;
    private final UserQueryService userQueryService;
    private final FileStore fileStore;

    /**
     * track 생성
     *
     * @throws IOException
     */
    @Transactional
    public TempTrackInfoDto saveTempTrackFile(TempTrackFileUploadDto tempTrackFileUploadDto, String sessionId) {
        User user = userQueryService.findOne();
        // track/{fileToken}폴더/track 이름
        // cover/fileToken/cover
        String fileToken = UserTokenUtil.getToken();

        UploadTrackFileDto uploadTrackFileDto = (UploadTrackFileDto) fileStore.storeFileSave(
            tempTrackFileUploadDto.getTrackFile(),
            FileStore.TRACK_DIR,
            fileToken);
        TempTrackStorage tempTrackStorage = TempTrackStorage.createTempTrackStorage(uploadTrackFileDto, fileToken,
            sessionId, user, tempTrackFileUploadDto.isPlayList());

        tempTrackStorageRepository.save(tempTrackStorage);

        // 구분값 추가
        TempTrackInfoDto tempTrackInfoDto = new TempTrackInfoDto(tempTrackStorage.getId(), tempTrackStorage.getToken(),
            uploadTrackFileDto);
        return tempTrackInfoDto;
    }

    public TempTrackStorage findOne(Long id, String sessionId, String token, User user)
        throws SsbFileFileNotFoundException {
        TempTrackStorage findOne = tempTrackStorageRepository.findOne(id, token, sessionId, user).orElseThrow(
            () -> new SsbFileFileNotFoundException());

        return findOne;
    }

    @Transactional
    public void delete(TempTrackStorage tempTrackStorage) {
        tempTrackStorageRepository.delete(tempTrackStorage);
    }

    @Transactional
    public void delete(Long id, String token, String sessionId) throws IOException {
        User user = userQueryService.findOne();

        TempTrackStorage tempTrackStorage = tempTrackStorageRepository.findOne(id, token, sessionId, user)
            .orElseThrow(() -> new SsbFileFileNotFoundException());

        // 임시파일 삭제
        TempTrackStorage.deleteTempFile(tempTrackStorage, fileStore);

        // DB에서 삭제
        delete(tempTrackStorage);
    }

}
