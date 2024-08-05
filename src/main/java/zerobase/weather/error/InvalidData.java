package zerobase.weather.error;

public class InvalidData extends RuntimeException {
    private static final String MESSAGE =  "너무 먼 미래 혹은 과거의 날짜입니다.";

    public InvalidData() {
        super(MESSAGE);
    }

}
