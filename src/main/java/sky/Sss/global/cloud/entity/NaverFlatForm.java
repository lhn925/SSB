package sky.Sss.global.cloud.entity;

import java.io.UnsupportedEncodingException;
import java.net.http.HttpClient;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NaverFlatForm {
    private String accessKey;
    private String secretKey;
    private String serviceId;
    private String phone;

    @Value("${sms.relaxed-binding.accessKey}")
    private void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
    @Value("${sms.relaxed-binding.secretKey}")
    private void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Value("${sms.relaxed-binding.serviceId}")
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    @Value("${sms.relaxed-binding.phone}")
    private void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 시그니 처 생성
     * 요청에 맞게 StringToSign을 생성하고 SecretKey로 HmacSHA256 알고리즘으로 암호화한 후 Base64로 인코딩
     * 인코딩한 값을 x-ncp-apigw-signature-v2로 사용
     * @param method
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public String makeSignature(Long time,String method)
        throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String apiUrl = "/sms/v2/services/"+this.serviceId+"/messages";

        String message = new StringBuilder().append(method)
            .append(space)
            .append(apiUrl)
            .append(newLine)
            .append(time)
            .append(newLine)
            .append(this.accessKey).toString();

        SecretKeySpec signingKey = new SecretKeySpec(getSecretKey().getBytes("utf-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");

        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("utf-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);
        return encodeBase64String;
    }
}
