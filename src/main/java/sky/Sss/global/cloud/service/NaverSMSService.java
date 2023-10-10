package sky.Sss.global.cloud.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sky.Sss.global.cloud.dto.MessageDto;
import sky.Sss.global.cloud.dto.SmsRequestDto;
import sky.Sss.global.cloud.dto.SmsResponseDto;
import sky.Sss.global.cloud.entity.NaverFlatForm;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverSMSService {


    private final NaverFlatForm naverFlatForm;

    /**
     * {
     *   "statusCode": "200",
     *   "statusName": "success",
     *   "messages": [
     *     {
     *       "messageId": "132597da-68ab-499b-b509-174d06143b9e",
     *       "requestTime": "2023-10-03 18:11:58",
     *       "from": "01063743155",
     *       "to": "01064033149",
     *       "contentType": "COMM",
     *       "countryCode": "82",
     *       "content": "개별메시지 내용테스트입니다",
     *       "completeTime": "2023-10-03 18:12:03",
     *       "status": "COMPLETED",
     *       "telcoCode": "LGT",
     *       "statusCode": "0",
     *       "statusName": "success",
     *       "statusMessage": "성공"
     *     }
     *   ]
     * }
     * @return
     */
    public SmsResponseDto sendSms(MessageDto messageDto) {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();

        SmsResponseDto smsResponseDto = null;

        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-apigw-timestamp", time.toString());
            headers.set("x-ncp-iam-access-key", naverFlatForm.getAccessKey());
            headers.set("x-ncp-apigw-signature-v2", naverFlatForm.makeSignature(time, "POST"));

            MessageDto message = MessageDto.builder().
                to(messageDto.getTo())
                .content(messageDto.getContent()).build();
            List<MessageDto> messages = new ArrayList<>();
            messages.add(message);
            SmsRequestDto smsRequestDto = SmsRequestDto.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(naverFlatForm.getPhone())
                .messages(messages)
                .build();
            ObjectMapper objectMapper = new ObjectMapper();
            String body = objectMapper.writeValueAsString(smsRequestDto);
            log.info("body = {}", body);

            HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

            //Spring에서 지원하는 객체로 간편하게 Rest 방식 API를 호출할 수 있는 Spring 내장 클래스
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            smsResponseDto = restTemplate.postForObject(
                new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + naverFlatForm.getServiceId() + "/messages"),
                httpBody, SmsResponseDto.class);

            log.info("smsResponseDto.getStatusCode() = {}", smsResponseDto.getStatusCode());
            log.info("smsResponseDto.getRequestId() = {}", smsResponseDto.getRequestId());
            log.info("smsResponseDto.getStatusName() = {}", smsResponseDto.getStatusName());
            log.info("smsResponseDto.getRequestTime() = {}", smsResponseDto.getRequestTime());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return smsResponseDto;
    }


}
