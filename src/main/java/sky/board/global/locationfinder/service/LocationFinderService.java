package sky.board.global.locationfinder.service;


import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.DatabaseReader.Builder;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import sky.board.domain.user.utill.HttpReqRespUtils;
import sky.board.global.locationfinder.dto.UserLocationDto;
import sky.board.global.locationfinder.model.DataPath;

@Slf4j
@Service
public class LocationFinderService {


    public UserLocationDto findLocation() throws IOException, GeoIp2Exception {
        DatabaseReader databaseReader = getClassPathResource(DataPath.CITY_DB.getValue());
        InetAddress ipAddress = getInetAddress();
        CityResponse response = databaseReader.city(ipAddress);
        return UserLocationDto.createUserLocationDto(response);
    }


    // 사용자 ip추출
    private String getIp() {
        String ip = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
        return ip;
    }

    private InetAddress getInetAddress() throws UnknownHostException {
        InetAddress ipAddress = InetAddress.getByName(getIp());
        return ipAddress;
    }

    private DatabaseReader getClassPathResource(String mmdbPath) throws IOException {
        ClassPathResource resource = new ClassPathResource(mmdbPath);
        DatabaseReader databaseReader = new Builder(resource.getFile()).build();
        return databaseReader;
    }


}
