package sky.board.global.locationfinder.dto;


import com.maxmind.geoip2.model.CityResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import sky.board.domain.user.model.UserAgent;

@Getter
public class UserLocationDto {

    private String ipAddress;

    private String city;

    private String countryName;
    // 위도
    private String latitude;

    // 경도
    private String longitude;

    @Builder
    public UserLocationDto(String ipAddress, String city, String countryName, String latitude, String longitude) {
        this.ipAddress = ipAddress;
        this.city = city;
        this.countryName = countryName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public static UserLocationDto createUserLocationDto(CityResponse response) {
        String countryName = response.getCountry().getIsoCode();
        String cityName = response.getCity().getName();
        String ipAddress = response.getTraits().getIpAddress();
        String latitude = String.valueOf(response.getLocation().getLatitude());
        String longitude = String.valueOf(response.getLocation().getLongitude());

        return UserLocationDto.builder()
            .city(cityName)
            .countryName(countryName)
            .ipAddress(ipAddress)
            .latitude(latitude)
            .longitude(longitude).build();
    }
    public static UserAgent isDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toUpperCase();
        if (userAgent.indexOf(UserAgent.MOBI.name()) > -1) {
            return UserAgent.MOBI;
        } else {
            return UserAgent.PC;
        }
    }

    public static String getClientOS(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        String os = "";
        userAgent = userAgent.toLowerCase();
        if (userAgent.indexOf("windows nt 10.0") > -1) {
            os = "Windows10";
        }else if (userAgent.indexOf("windows nt 6.1") > -1) {
            os = "Windows7";
        }else if (userAgent.indexOf("windows nt 6.2") > -1 || userAgent.indexOf("windows nt 6.3") > -1 ) {
            os = "Windows8";
        }else if (userAgent.indexOf("windows nt 6.0") > -1) {
            os = "WindowsVista";
        }else if (userAgent.indexOf("windows nt 5.1") > -1) {
            os = "WindowsXP";
        }else if (userAgent.indexOf("windows nt 5.0") > -1) {
            os = "Windows2000";
        }else if (userAgent.indexOf("windows nt 4.0") > -1) {
            os = "WindowsNT";
        }else if (userAgent.indexOf("windows 98") > -1) {
            os = "Windows98";
        }else if (userAgent.indexOf("windows 95") > -1) {
            os = "Windows95";
        }else if (userAgent.indexOf("iphone") > -1) {
            os = "iPhone";
        }else if (userAgent.indexOf("ipad") > -1) {
            os = "iPad";
        }else if (userAgent.indexOf("android") > -1) {
            os = "android";
        }else if (userAgent.indexOf("mac") > -1) {
            os = "mac";
        }else if (userAgent.indexOf("linux") > -1) {
            os = "Linux";
        }else{
            os = "Other";
        }
        return os;
    }


    public static String getClientBrowser(HttpServletRequest request) {
        String userAgent = getUserAgent(request);
        String browser = "";
        if (userAgent.indexOf("Trident/7.0") > -1) {
            browser = "ie11";
        }
        else if (userAgent.indexOf("MSIE 10") > -1) {
            browser = "ie10";
        }
        else if (userAgent.indexOf("MSIE 9") > -1) {
            browser = "ie9";
        }
        else if (userAgent.indexOf("MSIE 8") > -1) {
            browser = "ie8";
        }
        else if (userAgent.indexOf("Chrome/") > -1) {
            browser = "Chrome";
        }
        else if (userAgent.indexOf("Chrome/") == -1 && userAgent.indexOf("Safari/") >= -1) {
            browser = "Safari";
        }
        else if (userAgent.indexOf("Firefox/") >= -1) {
            browser = "Firefox";
        }
        else {
            browser ="Other";
        }
        return browser;
    }

    private static String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("USER-AGENT");
        return userAgent;
    }


}