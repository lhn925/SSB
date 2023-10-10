package sky.Sss.global.locationfinder.model;

public enum DataPath {
    CITY_DB("data/GeoLite2-City.mmdb"),
    COUNTRY_DB("data/GeoLite2-Country.mmdb");


    private final String value;

    DataPath(String value) {
        this.value = value;
    }
    public String getValue() {


        return value;
    }
}
