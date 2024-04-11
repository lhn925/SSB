package sky.Sss.domain.track.service.temp;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.temp.TempTrackDeleteDto;
import sky.Sss.domain.track.dto.temp.TempTrackInfoDto;
import sky.Sss.domain.track.dto.temp.TempTrackFileUploadDto;
import sky.Sss.domain.track.dto.temp.TempTracksDeleteDto;
import sky.Sss.domain.track.entity.temp.TempTrackStorage;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.repository.temp.TempTrackStorageRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.TokenUtil;
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
    public TempTrackInfoDto saveTempTrackFile(TempTrackFileUploadDto tempTrackFileUploadDto) {
        User user = userQueryService.findOne();
        // track/{fileToken}폴더/track 이름
        // cover/fileToken/cover
        String fileToken = TokenUtil.getToken();

        log.info("fileToken = {}", fileToken);

        UploadTrackFileDto uploadTrackFileDto = (UploadTrackFileDto) fileStore.storeTrackFileSave(
            tempTrackFileUploadDto.getTrackFile(),
            FileStore.TRACK_DIR, fileToken);
        TempTrackStorage tempTrackStorage = TempTrackStorage.createTempTrackStorage(uploadTrackFileDto, fileToken, user,
            tempTrackFileUploadDto.isPlayList(),
            tempTrackFileUploadDto.isPrivacy());

        tempTrackStorageRepository.save(tempTrackStorage);

        // 구분값 추가
        return TempTrackInfoDto.create(tempTrackStorage.getId(), tempTrackStorage.getToken(),
            uploadTrackFileDto);
    }

    public TempTrackStorage findOne(Long id, String token, User user, boolean isPlayList)
        throws SsbFileNotFoundException {
        return tempTrackStorageRepository.findOne(id, token, user, isPlayList).orElseThrow(
            SsbFileNotFoundException::new);
    }


    public List<TempTrackStorage> findByList(User user, List<String> tokens, List<Long> ids,
        boolean isPlayList)
        throws SsbFileNotFoundException {
        List<TempTrackStorage> tempTrackStorageList = tempTrackStorageRepository.findByUid(user,
            tokens, ids, isPlayList);
        if (tempTrackStorageList.isEmpty()) {
            throw new SsbFileNotFoundException();
        }
        return tempTrackStorageList;
    }

    public List<TempTrackStorage> findByUid(long uid, boolean isPrivacy, boolean isPlayList)
        throws SsbFileNotFoundException {
        return tempTrackStorageRepository.findByUid(uid, isPrivacy, isPlayList);
    }


    @Transactional
    public void deleteAllBatch(List<TempTrackStorage> tempList) {
        if (!tempList.isEmpty()) {
            tempTrackStorageRepository.deleteAllInBatch(tempList);
        }
    }

    @Transactional
    public void delete(TempTrackStorage tempTrackStorage) {
        tempTrackStorageRepository.delete(tempTrackStorage);
    }

    @Transactional
    public void deleteAll(List<TempTrackDeleteDto> tempTrackDeleteDtoList) {
        User user = userQueryService.findOne();

        List<Long> ids = tempTrackDeleteDtoList.stream().map(TempTrackDeleteDto::getId).toList();
        List<String> tokens = tempTrackDeleteDtoList.stream().map(TempTrackDeleteDto::getToken).toList();

        List<TempTrackStorage> tempTrackStorageList = tempTrackStorageRepository.findAllByTokens(user.getId(),tokens,ids);
        try {
            tempTrackStorageList.forEach(temp -> {
                TempTrackStorage.deleteTempFile(temp, fileStore);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // DB에서 삭제
        deleteAllBatch(tempTrackStorageList);
    }

}
