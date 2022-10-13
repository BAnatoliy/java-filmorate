package ru.yandex.practicum.filmorate.controllers.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.adapters.LocalDateAdapter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootApplication
public class FilmControllerRequestTest {
    private ConfigurableApplicationContext context;
    private Gson gson;
    private final HttpClient client = HttpClient.newHttpClient();
    private final URI uri = URI.create("http://localhost:8080/films");

    @BeforeEach
    public void init() {
       context = SpringApplication.run(FilmorateApplication.class);
       GsonBuilder gsonBuilder = new GsonBuilder();
       gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
       gson = gsonBuilder.create();
    }

    @Test
    public void should_List_Of_Films_Be_Empty_When_Films_Not_Created() throws IOException, InterruptedException {
        List<Film> filmList = sendGetRequest();
        assertTrue(filmList.isEmpty());
    }

    @Test
    public void should_Create_One_Valid_Film_And_Not_Create_Two_Invalid_Films_When_Name_Of_Films_Is_Empty_Or_Null() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film(null, "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new Film("", "drama", LocalDate.of(1995, 7, 25),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3);

        assertAll(
                () -> {
                    assertEquals(400, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<Film> filmList = sendGetRequest();
        assertEquals(1, filmList.size());
    }

    @Test
    public void should_Create_Two_Valid_Film_When_Length_Of_Description_Is_199_And_200_And_Not_Create_One_Invalid_Films_When_Length_Of_Description_Is_201() throws IOException, InterruptedException {
        Film film = new Film("Otto", "classclassclassclassclassclassclassclassclassclassclassclassclass" +
                "classclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclass" +
                "classclassclassclassclassclassclassc", LocalDate.of(2005, 10, 17),100);
        String json1 = gson.toJson(film);
        HttpResponse<String> response1 = sendPostRequest(json1);
        assertEquals(201, film.getDescription().length());

        Film film2 = new Film("Tom", "comedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedy" +
                "comedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedy" +
                "comedycomedycomedycomedycomedyc", LocalDate.of(2007, 12, 27),110);
        String json2 = gson.toJson(film2);
        HttpResponse<String> response2 = sendPostRequest(json2);
        assertEquals(199, film2.getDescription().length());

        Film film3 = new Film("Titanic", "dramadramadramadramadramadramadramadramadramadramadramadramadramadrama" +
                "dramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadrama" +
                "dramadramadramadramadramadramadrama", LocalDate.of(1995, 7, 25),127);
        String json3 = gson.toJson(film3);
        HttpResponse<String> response3 = sendPostRequest(json3);
        assertEquals(200, film3.getDescription().length());

        assertAll(
                () -> {
                    assertEquals(400, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                }
        );

        List<Film> filmList = sendGetRequest();
        assertEquals(2, filmList.size());
    }

    @Test
    public void should_Create_Two_Valid_Film_And_Not_Create_One_Invalid_Films_When_This_Film_Already_Created() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response3 = sendPostRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(500, response3.statusCode());
                }
        );

        List<Film> filmList = sendGetRequest();
        assertEquals(2, filmList.size());
    }

    @Test
    public void should_Create_One_Valid_Film_When_ReleaseDate_Of_Film_Is_1895_12_28_And_1895_12_29_And_Not_Create_One_Invalid_Film_When_ReleaseDate_Of_Film_Is_1895_12_27() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(1895, 12, 29),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(1895, 12, 28),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1895, 12, 27),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<Film> filmList = sendGetRequest();
        assertEquals(2, filmList.size());
    }

    @Test
    public void should_Create_Two_Valid_Films_When_Duration_Of_Film_Is_0_And_Positive_And_Not_Create_Two_Invalid_Films_When_Duration_Of_Film_Is_Negative() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                1));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                0));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1997, 7, 25),
                -1));
        HttpResponse<String> response3 = sendPostRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<Film> filmList = sendGetRequest();
        assertEquals(2, filmList.size());
    }

    @Test
    public void should_Create_Three_Valid_Films_And_Find_All_Films() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1995, 7, 25),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                }
        );

        List<Film> filmList = sendGetRequest();
        assertEquals(3, filmList.size());
    }

    @Test
    public void should_Update_Film_When_Created_Film_With_The_Same_Id() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1995, 7, 25),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3);

        Film newFilm = new Film("Titanic2", "drama2", LocalDate.of(1997, 7, 25),127);
        newFilm.setId(3);
        String json4 = gson.toJson(newFilm);
        HttpResponse<String> response4 = sendPutRequest(json4);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                    assertEquals(200, response4.statusCode());
                }
        );

        List<Film> filmList = sendGetRequest();
        assertEquals(3, filmList.size());
    }

    @Test
    public void should_Not_Update_Film_When_Film_With_The_Same_Id_Not_Created() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1995, 7, 25),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3);

        Film newFilm = new Film("Titanic2", "drama2", LocalDate.of(1997, 7, 25),127);
        newFilm.setId(333);
        String json4 = gson.toJson(newFilm);
        HttpResponse<String> response4 = sendPutRequest(json4);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                    assertEquals(500, response4.statusCode());
                }
        );

        List<Film> filmList = sendGetRequest();
        assertEquals(3, filmList.size());
    }

    @AfterEach
    public void close() {
        SpringApplication.exit(context);
    }

    private HttpResponse<String> sendPostRequest(String json) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private List<Film> sendGetRequest() throws IOException, InterruptedException {
        Type typeListOfUsers = new TypeToken<List<User>>(){}.getType();
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").GET().build();
        HttpResponse<String> response4 = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response4.body(), typeListOfUsers);
    }

    private HttpResponse<String> sendPutRequest(String json) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").PUT(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
