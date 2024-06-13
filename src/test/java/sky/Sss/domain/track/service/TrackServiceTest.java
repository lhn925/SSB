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
import sky.Sss.domain.track.dto.temp.TempTrackInfoDto;
import sky.Sss.domain.track.dto.playlist.req.PlayListTrackInfoReqDto;
import sky.Sss.domain.track.dto.track.req.TrackInfoSaveReqDto;
import sky.Sss.domain.track.dto.playlist.req.PlayListSettingSaveDto;
import sky.Sss.domain.track.dto.tag.TrackTagsDto;
import sky.Sss.domain.track.dto.temp.TempTrackFileUploadDto;
import sky.Sss.domain.track.model.SubMusicGenre;
import sky.Sss.domain.track.service.temp.TempTrackStorageService;
import sky.Sss.domain.track.service.track.TrackService;


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
        TempTrackFileUploadDto temp = new TempTrackFileUploadDto();

//        temp.setIsPlayList(false);
        temp.setTrackFile(multipartFile);

        TempTrackInfoDto tempTrackInfoDto = tempTrackStorageService.saveTempTrackFile(temp);

        TrackInfoSaveReqDto trackMetaUploadDto = new TrackInfoSaveReqDto();
        Set<TrackTagsDto> tagsDtoSet = new HashSet<>();
        for (int i = 1; i<=10; i++) {
            TrackTagsDto trackTagsDto = new TrackTagsDto();
            trackTagsDto.setId(0L);
            trackTagsDto.setTag("iu" + i);
            tagsDtoSet.add(trackTagsDto);
        }

//        trackMetaUploadDto.setTagSet(tagsDtoSet);
        trackMetaUploadDto.setTitle("sky");
//        trackMetaUploadDto.setCoverImgFile(multipartFile5);
        trackMetaUploadDto.setGenre("hiphop");
        trackMetaUploadDto.setToken(tempTrackInfoDto.getToken());
        trackMetaUploadDto.setId(tempTrackInfoDto.getId());

//        trackService.addTrackFile(trackMetaUploadDto,sessionId);
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

        PlayListSettingSaveDto playListSettingSaveDto = new PlayListSettingSaveDto();
        playListSettingSaveDto.setDesc("안녕하세요 앨범 소개글입니다");
        Set<TrackTagsDto> tagsDtoSet = new HashSet<>();
        for (int i = 1; i<=10; i++) {
            TrackTagsDto trackTagsDto = new TrackTagsDto();
            trackTagsDto.setId(0L);
            trackTagsDto.setTag("iu" + i);
            tagsDtoSet.add(trackTagsDto);
        }
//        playListSettingSaveDto.setTagSet(tagsDtoSet);

        List<PlayListTrackInfoReqDto> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            File file1 = new File("/Users/imhaneul/Downloads/sky.m4a");
            InputStream fileInputStream1 = new FileInputStream(file1);
            MockMultipartFile multipartFile1 = new MockMultipartFile("sky" + i, "sky.m4a",
                Mp4ContentTypeValue.MUSIC_VIDEO.name(), fileInputStream1);

            TempTrackFileUploadDto tempTrackFileUploadDto = new TempTrackFileUploadDto();
//            tempTrackFileUploadDto.setIsPlayList(true);
            tempTrackFileUploadDto.setTrackFile(multipartFile1);

            TempTrackInfoDto tempTrackInfoDto = tempTrackStorageService.saveTempTrackFile(tempTrackFileUploadDto);


            PlayListTrackInfoReqDto trackPlayListMetaDto = new PlayListTrackInfoReqDto();
            trackPlayListMetaDto.setOrder(i+1);
            trackPlayListMetaDto.setTitle("아이유" + i);
            trackPlayListMetaDto.setDesc(playListSettingSaveDto.getDesc());
            trackPlayListMetaDto.setGenre(SubMusicGenre.COUNTRY.name());

            trackPlayListMetaDto.setToken(tempTrackInfoDto.getToken());
            trackPlayListMetaDto.setId(tempTrackInfoDto.getId());
//            trackPlayListMetaDto.setGenreType(MainGenreType.MUSIC);
            list.add(trackPlayListMetaDto);
        }

        playListSettingSaveDto.setPlayListTrackInfoDtoList(list);


    }


    @Test
    public void trackLog() {

    // given

    // when

    // then

    }



}