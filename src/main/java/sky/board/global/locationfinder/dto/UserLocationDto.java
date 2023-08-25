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

}