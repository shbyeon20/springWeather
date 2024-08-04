package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.DateWeather;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DateWeatherRepository extends JpaRepository<DateWeather, Long> {
    Optional<DateWeather> findFirstByDate(LocalDate date);
}
