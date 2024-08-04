package zerobase.weather.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    List<Diary> findAllByDate(LocalDate date);
    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
    Diary findFirstByDate(LocalDate date);


    @Transactional // transaction이 없으면 문제가 생긴다? 전체 다이어리를 지워달라고 해도 안지워졌음.
    void deleteAllByDate(LocalDate date);
}

