package sky.Sss.domain.track.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jaudiotagger.tag.mp4.atom.Mp4ContentTypeValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import sky.Sss.domain.track.dto.TempTrackInfoDto;
import sky.Sss.domain.track.dto.TrackMetaUploadDto;
import sky.Sss.domain.track.dto.TrackPlayListMetaDto;
import sky.Sss.domain.track.dto.TrackPlayListSettingDto;
import sky.Sss.domain.track.dto.TrackTagsDto;
import sky.Sss.domain.track.dto.TrackTempFileUploadDto;
import sky.Sss.domain.track.entity.TempTrackStorage;
import sky.Sss.domain.track.model.MusicGenre;
import sky.Sss.domain.track.model.PlayListType;
import sky.Sss.domain.track.model.TrackGenre;


@SpringBootTest
class TrackServiceTest {


    @Autowired
    TrackService trackService;

    @Autowired
    TempTrackStorageService tempTrackStorageService;

    @Test
    void save() throws IOException {

        String grantedAuth = "USER";
        List<SimpleGrantedAuthority> collect = Arrays.stream(grantedAuth.split(",")).map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        User user = new User("lim222", "",collect );

        String tokne = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsaW0yMjIiLCJpYXQiOjE3MDAxMTYzNTYsImF1dGgiOiJST0xFX1VTRVIiLCJyZWRpcyI6ImJlYzBiZTZjODI2ZjBhNjFhZTE4IiwiZXhwIjoxNzAwMTE5OTU2fQ.XaV4Q3sdrXz3QTl3DmdHAJS8b9e5YNErjREhZltFFrEb5ViNTPd-C_E_C-OiCRjzOtNomKtvWLvrPoUoXywV1A";
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            user, tokne, collect);

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        File file = new File("/Users/imhaneul/Downloads/sky.m4a");
        File coverImage = new File("/Users/imhaneul/Downloads/IMG_7813.PNG");

        InputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("sky","sky.m4a", Mp4ContentTypeValue.MUSIC_VIDEO.name(), fileInputStream);
        InputStream fileInputStream5 = new FileInputStream(coverImage);
        MockMultipartFile multipartFile5 = new MockMultipartFile("cover","IMG_7813.PNG", "image/x-png", fileInputStream5);

        String sessionId = "a8702363-50ad-4de1-8e6b-928cf988e3a9";
        TrackTempFileUploadDto temp = new TrackTempFileUploadDto();

        temp.setPlayList(false);
        temp.setTrackFile(multipartFile);

        TempTrackInfoDto tempTrackInfoDto = tempTrackStorageService.saveTempTrackFile(temp, sessionId);

        TrackMetaUploadDto trackMetaUploadDto = new TrackMetaUploadDto();
        Set<TrackTagsDto> tagsDtoSet = new HashSet<>();
        for (int i = 1; i<=10; i++) {
            TrackTagsDto trackTagsDto = new TrackTagsDto();
            trackTagsDto.setId(0L);
            trackTagsDto.setTag("iu" + i);
            tagsDtoSet.add(trackTagsDto);
        }

        trackMetaUploadDto.setTagSet(tagsDtoSet);
        trackMetaUploadDto.setTitle("sky");
        trackMetaUploadDto.setCoverImgFile(multipartFile5);
        trackMetaUploadDto.setGenre("hiphop");
        trackMetaUploadDto.setDownload(false);
        trackMetaUploadDto.setPrivacy(false);
        trackMetaUploadDto.setToken(tempTrackInfoDto.getToken());
        trackMetaUploadDto.setId(tempTrackInfoDto.getId());

        trackService.saveTrackFile(trackMetaUploadDto,sessionId);
    }


    @Test
    void savePlayList() throws IOException {

        String grantedAuth = "USER";
        List<SimpleGrantedAuthority> collect = Arrays.stream(grantedAuth.split(",")).map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        User user = new User("lim222", "", collect);
        String sessionId = "a8702363-50ad-4de1-8e6b-928cf988e3a9";
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsaW0yMjIiLCJpYXQiOjE3MDA0NTI0MzEsImF1dGgiOiJST0xFX1VTRVIiLCJyZWRpcyI6ImQyZWQ0OTAzZjVkMGI3YzNlYjEzIiwiZXhwIjoxNzAwNDU2MDMxfQ.QPCH3I9_d2hW1lIMLIrqCiui1xUfhvePbXGoVltUDniX7hJtYKvcu-X8qtvtCYwIYkoHDE2TFJZ0eLMfrroXVA";
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            user, token, collect);

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        File coverImage = new File("/Users/imhaneul/Downloads/IMG_7813.PNG");

        InputStream fileInputStream5 = new FileInputStream(coverImage);
        MockMultipartFile multipartFile5 = new MockMultipartFile("cover", "IMG_7813.PNG", "image/x-png",
            fileInputStream5);

        TrackPlayListSettingDto trackPlayListSettingDto = new TrackPlayListSettingDto();
        trackPlayListSettingDto.setPlayListType(PlayListType.ALBUM);
        trackPlayListSettingDto.setDesc("안녕하세요 앨범 소개글입니다");
        trackPlayListSettingDto.setCoverImgFile(multipartFile5);
        trackPlayListSettingDto.setPlayListTitle("앨범타이틀!");
        trackPlayListSettingDto.setIsDownload(true);
        trackPlayListSettingDto.setIsPrivacy(false);
        Set<TrackTagsDto> tagsDtoSet = new HashSet<>();
        for (int i = 1; i<=10; i++) {
            TrackTagsDto trackTagsDto = new TrackTagsDto();
            trackTagsDto.setId(0L);
            trackTagsDto.setTag("iu" + i);
            tagsDtoSet.add(trackTagsDto);
        }
        trackPlayListSettingDto.setTagSet(tagsDtoSet);

        List<TrackPlayListMetaDto> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            File file1 = new File("/Users/imhaneul/Downloads/sky.m4a");
            InputStream fileInputStream1 = new FileInputStream(file1);
            MockMultipartFile multipartFile1 = new MockMultipartFile("sky" + i, "sky.m4a",
                Mp4ContentTypeValue.MUSIC_VIDEO.name(), fileInputStream1);

            TrackTempFileUploadDto trackTempFileUploadDto = new TrackTempFileUploadDto();
            trackTempFileUploadDto.setPlayList(true);
            trackTempFileUploadDto.setTrackFile(multipartFile1);

            TempTrackInfoDto tempTrackInfoDto = tempTrackStorageService.saveTempTrackFile(trackTempFileUploadDto,
                sessionId);


            TrackPlayListMetaDto trackPlayListMetaDto = new TrackPlayListMetaDto();
            trackPlayListMetaDto.setOrder(i+1);
            trackPlayListMetaDto.setTitle("아이유" + i);
            trackPlayListMetaDto.setDesc(trackPlayListSettingDto.getDesc());
            trackPlayListMetaDto.setGenre(MusicGenre.COUNTRY.name());
            trackPlayListMetaDto.setDownload(true);
            trackPlayListMetaDto.setPrivacy(false);
            trackPlayListMetaDto.setToken(tempTrackInfoDto.getToken());
            trackPlayListMetaDto.setId(tempTrackInfoDto.getId());
            trackPlayListMetaDto.setGenreType(TrackGenre.MUSIC);
            list.add(trackPlayListMetaDto);
        }

        trackPlayListSettingDto.setTrackPlayListMetaDto(list);

        trackService.saveTrackFiles(trackPlayListSettingDto, "a8702363-50ad-4de1-8e6b-928cf988e3a9");

    }



}