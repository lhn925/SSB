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
    public String home(HttpServletRequest request) {
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

}
