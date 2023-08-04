package sky.board;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.DatabaseReader.Builder;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sky.board.domain.user.utill.HttpReqRespUtils;
import sky.board.global.model.DataPath;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home";
    }



    /**
     * 로그인 성공 후 호출되는 API
     * 0dksmf071
     * 0dlagksmf2
     * @param user
     * @param model
     * @return
     */
    @GetMapping("/view/dashboard")
    public String dashBoardPage(@AuthenticationPrincipal UserDetails user, Model model, HttpServletRequest request){
        return "redirect:/";
    }

    /**
     * 데이터베이스를 통해 요청 ip에 대한 국가 정보 확인
     */

    @GetMapping("/example/city")
    @ResponseBody
    public ResponseEntity infoFromCityDB(HttpServletRequest request) throws IOException, GeoIp2Exception {
//        String ip = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
        String ip = "218.239.21.150";

        ClassPathResource resource = new ClassPathResource(DataPath.CITY_DB.getValue());

        DatabaseReader databaseReader = new Builder(resource.getFile()).build();

        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = databaseReader.city(ipAddress);

        String isoCode = response.getCountry().getIsoCode();

        Locale locale = new Locale("en", isoCode);

        log.info("locale.getCountry() = {}", locale.getCountry());
        log.info("locale.getDisplayName() = {}", locale.getDisplayName());
        log.info("locale.getDisplayCountry() = {}", locale.getDisplayCountry());
        log.info("locale.getDisplayCountry(new Locale(en)) = {}", locale.getDisplayCountry(new Locale("ja")));



        return ResponseEntity.ok(response);
    }


}
