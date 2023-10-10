package sky.Sss.global.locationfinder.service;


import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.DatabaseReader.Builder;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import sky.Sss.domain.user.utili.HttpReqRespUtils;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.model.DataPath;

@Slf4j
@Service
public class LocationFinderService {

    public UserLocationDto findLocation() {

        DatabaseReader databaseReader = getClassPathResource(DataPath.CITY_DB.getValue());
        InetAddress ipAddress = null;
        CityResponse response = null;
        try {
            ipAddress = getInetAddress();
            response = databaseReader.city(ipAddress);
        } catch (GeoIp2Exception | IOException e) {
            throw new RuntimeException(e);
        }
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

    private DatabaseReader getClassPathResource(String mmdbPath) {
        InputStream resource = null;
        DatabaseReader databaseReader = null;
        try {

            resource = new ClassPathResource(mmdbPath).getInputStream();
            databaseReader = new Builder(resource).build();
        } catch (IOException e) {
            throw new RuntimeException("error");
        } finally {
            try {
                if (resource != null) {
                    resource.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("error");
            }
        }

        return databaseReader;
    }


}
