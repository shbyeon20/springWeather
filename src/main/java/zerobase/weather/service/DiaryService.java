package zerobase.weather.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);


    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherData() {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    private DateWeather getWeatherFromApi() {
        // open weather api에서 json 받아오기
        String weatherString = getWeatherString();

        // 받아온 json을 parsing 하기
        Map<String, Object> parsedWeather = parseWeather(weatherString);
        // dateweather에 정보집어넣기
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setWeather(parsedWeather.get("weather").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));


        return dateWeather;

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("createDiary");

        DateWeather dateWeather = getWeather(date);
        Diary diary = Diary.fromDateWeather(dateWeather, text);

        diaryRepository.save(diary);
        logger.info("Diary created");
    }

    private DateWeather getWeather(LocalDate date) {
                return dateWeatherRepository.findFirstByDate(date).orElse(getWeatherFromApi());
    }


    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2" +
                ".5/weather?q=seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) {
                br =
                        new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br =
                        new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();


        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        System.out.println(weatherArray.toString());

        JSONObject weatherData = (JSONObject) weatherArray.getFirst();
        resultMap.put("weather", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;


    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        logger.debug("readDiary");
        return diaryRepository.findAllByDate(date);
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiaies(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void update(LocalDate date, String text) {
        Diary newDiary = diaryRepository.findFirstByDate(date);
        newDiary.setText(text);
        diaryRepository.save(newDiary);
    }

    public void delete(LocalDate date) {
        diaryRepository.deleteAllByDate(date);

    }
}
