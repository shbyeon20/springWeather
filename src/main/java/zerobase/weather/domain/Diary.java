package zerobase.weather.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String weather;
    private String icon;
    private double temperature;
    private String text;
    private LocalDate date;

    public static Diary fromDateWeather(DateWeather dateWeather, String text) {

        return Diary.builder().
                date(dateWeather.getDate()).
                icon(dateWeather.getIcon()).
                weather(dateWeather.getWeather()).
                temperature(dateWeather.getTemperature()).
                text(text).
                build();
    }
}


