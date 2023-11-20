package sky.Sss.domain.track.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.stream.ImageInputStream;
import org.jaudiotagger.tag.mp4.atom.Mp4ContentTypeValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.thymeleaf.util.ContentTypeUtils;
import sky.Sss.domain.track.dto.TrackFileUploadDto;
import sky.Sss.domain.track.dto.TrackPlayListFileDto;
import sky.Sss.domain.track.dto.TrackPlayListSettingDto;
import sky.Sss.domain.track.model.PlayListType;
import sky.Sss.domain.track.model.TrackGenre;


@SpringBootTest
class TrackServiceTest {


    @Autowired
    TrackService trackService;

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

        InputStream fileInputStream = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("sky","sky.m4a", Mp4ContentTypeValue.MUSIC_VIDEO.name(), fileInputStream);

        TrackFileUploadDto trackFileUploadDto = new TrackFileUploadDto();
        trackFileUploadDto.setTrackFile(multipartFile);
        trackFileUploadDto.setTitle("sky");
        trackFileUploadDto.setGenre("hiphop");
        trackFileUploadDto.setIsPrivacy(false);
        trackFileUploadDto.setIsDownload(false);

        trackService.saveTrackFile(trackFileUploadDto);
    }


    @Test
    void savePlayList() throws IOException {

        String grantedAuth = "USER";
        List<SimpleGrantedAuthority> collect = Arrays.stream(grantedAuth.split(",")).map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
        User user = new User("lim222", "",collect );

        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsaW0yMjIiLCJpYXQiOjE3MDA0NTI0MzEsImF1dGgiOiJST0xFX1VTRVIiLCJyZWRpcyI6ImQyZWQ0OTAzZjVkMGI3YzNlYjEzIiwiZXhwIjoxNzAwNDU2MDMxfQ.QPCH3I9_d2hW1lIMLIrqCiui1xUfhvePbXGoVltUDniX7hJtYKvcu-X8qtvtCYwIYkoHDE2TFJZ0eLMfrroXVA";
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            user, token, collect);

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        File file1 = new File("/Users/imhaneul/Downloads/sky.m4a");
        File file2 = new File("/Users/imhaneul/Downloads/sky.m4a");
        File file3 = new File("/Users/imhaneul/Downloads/sky.m4a");
        File file4 = new File("/Users/imhaneul/Downloads/sky.m4a");
        File coverImage = new File("/Users/imhaneul/Downloads/IMG_7813.PNG");



        InputStream fileInputStream1 = new FileInputStream(file1);
        InputStream fileInputStream2 = new FileInputStream(file2);
        InputStream fileInputStream3 = new FileInputStream(file3);
        InputStream fileInputStream4 = new FileInputStream(file4);
        InputStream fileInputStream5 = new FileInputStream(coverImage);
        MockMultipartFile multipartFile1 = new MockMultipartFile("sky1","sky.m4a", Mp4ContentTypeValue.MUSIC_VIDEO.name(), fileInputStream1);
        MockMultipartFile multipartFile2 = new MockMultipartFile("sky2","sky.m4a", Mp4ContentTypeValue.MUSIC_VIDEO.name(), fileInputStream2);
        MockMultipartFile multipartFile3 = new MockMultipartFile("sky3","sky.m4a", Mp4ContentTypeValue.MUSIC_VIDEO.name(), fileInputStream3);
        MockMultipartFile multipartFile4 = new MockMultipartFile("sky3","sky.m4a", Mp4ContentTypeValue.MUSIC_VIDEO.name(), fileInputStream4);


        MockMultipartFile multipartFile5 = new MockMultipartFile("cover","IMG_7813.PNG", "image/x-png", fileInputStream5);

        List<MockMultipartFile> list = new ArrayList<>();
        list.add(multipartFile1);
        list.add(multipartFile2);
        list.add(multipartFile3);
        list.add(multipartFile4);

        TrackPlayListSettingDto trackPlayListSettingDto = new TrackPlayListSettingDto();
        trackPlayListSettingDto.setPlayListType(PlayListType.ALBUM);
        trackPlayListSettingDto.setDesc("안녕하세요 앨범 소개글입니다");
        trackPlayListSettingDto.setCoverImgFile(multipartFile5);
        trackPlayListSettingDto.setPlayListTitle("앨범타이틀!");
        trackPlayListSettingDto.setIsDownload(true);
        trackPlayListSettingDto.setIsPrivacy(false);
        trackPlayListSettingDto.setCoverImgFile(multipartFile5);
        List<TrackPlayListFileDto> fileList = new ArrayList<>();


        Integer order = 1;
        for (MockMultipartFile multipartFile : list) {
            TrackPlayListFileDto trackPlayListFileDto = new TrackPlayListFileDto();
            trackPlayListFileDto.setOrder(order++);
            trackPlayListFileDto.setTrackFile(multipartFile);
            trackPlayListFileDto.setTitle("아이유"+order);
            trackPlayListFileDto.setDesc(trackPlayListSettingDto.getDesc());
            trackPlayListFileDto.setGenre("hiphop");
            trackPlayListFileDto.setGenreType(TrackGenre.MUSIC);
            fileList.add(trackPlayListFileDto);
        }

        trackPlayListSettingDto.setTrackPlayListFileDtoList(fileList);

        trackService.saveTrackFiles(trackPlayListSettingDto);
    }
}