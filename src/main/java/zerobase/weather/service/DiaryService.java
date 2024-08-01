package zerobase.weather.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;
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
public class DiaryService {

    @Value("${openweathermap.key}")
    private String apiKey;
    private final DiaryRepository diaryRepository;

    public void createDiary(LocalDate date, String text) {

        // open weather api에서 json 받아오기
        String weatherString = getWeatherString();

        // 받아온 json을 parsing 하기
        Map<String, Object> parsedWeather = parseWeather(weatherString);

        //파싱된 데이터 + 일기 값 db에 넣기
        Diary diary = new Diary();
        diary.setWeather(parsedWeather.get("main").toString());
        diary.setIcon(parsedWeather.get("icon").toString());
        diary.setTemperature((Double) parsedWeather.get("temp"));
        diary.setText(text);
        diary.setDate(date);
        diaryRepository.save(diary);
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
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        System.out.println(jsonString);

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");

        JSONObject weatherData = (JSONObject) weatherArray.getFirst();
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;


    }

    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaies(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate,endDate);
    }

    public void update(LocalDate date, String text) {
        Diary newDiary = diaryRepository.findFirstByDate(date);
        newDiary.setText(text);
        diaryRepository.save(newDiary);
    }

    public void delete(LocalDate date) {
        diaryRepository.deleteAllbyDate(date);

    }
}
