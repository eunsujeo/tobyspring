package study.spring.toby.chapter05.good.user.example_5_1_1.domain;

public enum Level {
    BASIC(1), SILVER(2), GOLD(3);

    private final int value;

    Level(int value) {
        this.value = value;
    }

    // 값을 가져오는 메소드
    public int intValue() {
        return value;
    }

    public static Level valueOf(int value) {
        switch (value) {
            case 1 : return BASIC;
            case 2 : return SILVER;
            case 3 : return GOLD;
            default:
                throw new AssertionError("Unknown value : " + value);
        }
    }
}
