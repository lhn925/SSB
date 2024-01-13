package sky.Sss.domain.track.model;

public enum Hour {
    HOUR_00(0),
    HOUR_01(1),
    HOUR_02(2),
    HOUR_03(3),
    HOUR_04(4),
    HOUR_05(5),
    HOUR_06(6),
    HOUR_07(7),
    HOUR_08(8),
    HOUR_09(9),
    HOUR_10(10),
    HOUR_11(11),
    HOUR_12(12),
    HOUR_13(13),
    HOUR_14(14),
    HOUR_15(15),
    HOUR_16(16),
    HOUR_17(17),
    HOUR_18(18),
    HOUR_19(19),
    HOUR_20(20),
    HOUR_21(21),
    HOUR_22(22),
    HOUR_23(23);

    private int value;


    public static Hour findByHour(int hour) {
        Hour[] values = values();
        for (Hour value : values) {
            if (value.getValue() == hour) {
                return value;
            }
        }
        return null;
    }

    Hour(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
