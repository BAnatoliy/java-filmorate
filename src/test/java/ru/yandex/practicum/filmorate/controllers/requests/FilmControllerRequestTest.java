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
        List<Film> filmList = getListFilm();
        assertTrue(filmList.isEmpty());
    }

    @Test
    public void should_Create_One_Valid_Film_And_Not_Create_Two_Invalid_Films_When_Name_Of_Films_Is_Empty_Or_Null() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film(null, "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2, uri);
        String json3 = gson.toJson(new Film("", "drama", LocalDate.of(1995, 7, 25),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3, uri);

        assertAll(
                () -> {
                    assertEquals(400, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<Film> filmList = getListFilm();
        assertEquals(1, filmList.size());
    }

    @Test
    public void should_Create_Two_Valid_Film_When_Length_Of_Description_Is_199_And_200_And_Not_Create_One_Invalid_Films_When_Length_Of_Description_Is_201() throws IOException, InterruptedException {
        Film film = new Film("Otto", "classclassclassclassclassclassclassclassclassclassclassclassclass" +
                "classclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclass" +
                "classclassclassclassclassclassclassc", LocalDate.of(2005, 10, 17),100);
        String json1 = gson.toJson(film);
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        assertEquals(201, film.getDescription().length());

        Film film2 = new Film("Tom", "comedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedy" +
                "comedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedy" +
                "comedycomedycomedycomedycomedyc", LocalDate.of(2007, 12, 27),110);
        String json2 = gson.toJson(film2);
        HttpResponse<String> response2 = sendPostRequest(json2, uri);
        assertEquals(199, film2.getDescription().length());

        Film film3 = new Film("Titanic", "dramadramadramadramadramadramadramadramadramadramadramadramadramadrama" +
                "dramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadrama" +
                "dramadramadramadramadramadramadrama", LocalDate.of(1995, 7, 25),127);
        String json3 = gson.toJson(film3);
        HttpResponse<String> response3 = sendPostRequest(json3, uri);
        assertEquals(200, film3.getDescription().length());

        assertAll(
                () -> {
                    assertEquals(400, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                }
        );

        List<Film> filmList = getListFilm();
        assertEquals(2, filmList.size());
    }

    @Test
    public void should_Create_Two_Valid_Film_And_Not_Create_One_Invalid_Films_When_This_Film_Already_Created() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2, uri);
        String json3 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response3 = sendPostRequest(json3, uri);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<Film> filmList = getListFilm();
        assertEquals(2, filmList.size());
    }

    @Test
    public void should_Create_One_Valid_Film_When_ReleaseDate_Of_Film_Is_1895_12_28_And_1895_12_29_And_Not_Create_One_Invalid_Film_When_ReleaseDate_Of_Film_Is_1895_12_27() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(1895, 12, 29),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(1895, 12, 28),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2, uri);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1895, 12, 27),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3, uri);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<Film> filmList = getListFilm();
        assertEquals(2, filmList.size());
    }

    @Test
    public void should_Create_Two_Valid_Films_When_Duration_Of_Film_Is_0_And_Positive_And_Not_Create_Two_Invalid_Films_When_Duration_Of_Film_Is_Negative() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                1));
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                0));
        HttpResponse<String> response2 = sendPostRequest(json2, uri);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1997, 7, 25),
                -1));
        HttpResponse<String> response3 = sendPostRequest(json3, uri);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<Film> filmList = getListFilm();
        assertEquals(2, filmList.size());
    }

    @Test
    public void should_Create_Three_Valid_Films_And_Find_All_Films() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2, uri);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1995, 7, 25),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3, uri);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                }
        );

        List<Film> filmList = getListFilm();
        assertEquals(3, filmList.size());
    }

    @Test
    public void should_Update_Film_When_Created_Film_With_The_Same_Id() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2, uri);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1995, 7, 25),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3, uri);

        Film newFilm = new Film("Titanic2", "drama2", LocalDate.of(1997, 7, 25),127);
        newFilm.setId(3);
        String json4 = gson.toJson(newFilm);
        HttpResponse<String> response4 = sendPutRequest(json4, uri);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                    assertEquals(200, response4.statusCode());
                }
        );

        List<Film> filmList = getListFilm();
        assertEquals(3, filmList.size());
    }

    @Test
    public void should_Not_Update_Film_When_Film_With_The_Same_Id_Not_Created() throws IOException, InterruptedException {
        String json1 = gson.toJson(new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100));
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        String json2 = gson.toJson(new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110));
        HttpResponse<String> response2 = sendPostRequest(json2, uri);
        String json3 = gson.toJson(new Film("Titanic", "drama", LocalDate.of(1995, 7, 25),
                127));
        HttpResponse<String> response3 = sendPostRequest(json3, uri);

        Film newFilm = new Film("Titanic2", "drama2", LocalDate.of(1997, 7, 25),127);
        newFilm.setId(333);
        String json4 = gson.toJson(newFilm);
        HttpResponse<String> response4 = sendPutRequest(json4, uri);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                    assertEquals(404, response4.statusCode());
                }
        );

        List<Film> filmList = getListFilm();
        assertEquals(3, filmList.size());
    }

    @Test
    public void should_Get_Created_Film_By_Id_And_Get_Status_Code_404_When_Film_Not_Created() throws IOException, InterruptedException {
        Film film1 = new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100);
        String json1 = gson.toJson(film1);
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);
        String json2 = gson.toJson(film2);
        HttpResponse<String> response2 = sendPostRequest(json2, uri);

        HttpResponse<String> response3 = sendGetRequest(URI.create(uri + "/1"));
        HttpResponse<String> response4 = sendGetRequest(URI.create(uri + "/2"));

        Film gottenFilm1 = gson.fromJson(response3.body(), Film.class);
        Film gottenFilm2 = gson.fromJson(response4.body(), Film.class);

        assertAll(() -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                    assertEquals(200, response4.statusCode());
                    assertEquals(film1, gottenFilm1);
                    assertEquals(film2, gottenFilm2);
                }
        );

        HttpResponse<String> response5 = sendGetRequest(URI.create(uri + "/3"));
        assertEquals(404, response5.statusCode());
    }

    @Test
    public void should_Delete_Created_Film_By_Id_And_Get_Status_Code_404_When_Film_Not_Created() throws IOException, InterruptedException {
        Film film1 = new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100);
        String json1 = gson.toJson(film1);
        HttpResponse<String> response1 = sendPostRequest(json1, uri);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);
        String json2 = gson.toJson(film2);
        HttpResponse<String> response2 = sendPostRequest(json2, uri);
        List<Film> filmListBeforeDelete = getListFilm();

        HttpResponse<String> response3 = sendDeleteRequest(URI.create(uri + "/1"));
        List<Film> filmListAfterDelete = getListFilm();

        assertAll(() -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                    assertEquals(2, filmListBeforeDelete.size());
                    assertEquals(1, filmListAfterDelete.size());
                }
        );

        HttpResponse<String> response4 = sendDeleteRequest(URI.create(uri + "/3"));
        assertEquals(404, response4.statusCode());
    }

    @Test
    public void should_Not_Add_Like_When_User_Not_Created_And_Film_Not_Created_() throws IOException, InterruptedException {
        Film film1 = new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);

        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));

        String json1 = gson.toJson(film1);
        String json2 = gson.toJson(film2);
        String json3 = gson.toJson(user1);
        String json4 = gson.toJson(user2);

        URI uriUsers = URI.create("http://localhost:8080/users");
        sendPostRequest(json1, uri);
        sendPostRequest(json2, uri);
        sendPostRequest(json3, uriUsers);
        sendPostRequest(json4, uriUsers);

        HttpResponse<String> response = sendPutRequestWithoutBody(URI.create(uri + "/1/like/1"));
        HttpResponse<String> response1 = sendPutRequestWithoutBody(URI.create(uri + "/2/like/2"));

        HttpResponse<String> response2 = sendGetRequest(URI.create(uri + "/1"));
        HttpResponse<String> response3 = sendGetRequest(URI.create(uri + "/2"));

        Film film3 = gson.fromJson(response2.body(), Film.class);
        Film film4 = gson.fromJson(response3.body(), Film.class);

        assertAll(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals(200, response1.statusCode());
                    assertEquals(1, film3.getIdUsersLike().size());
                    assertEquals(1, film4.getIdUsersLike().size());
                }
        );

        HttpResponse<String> response4 = sendPutRequestWithoutBody(URI.create(uri + "/3/like/1"));
        HttpResponse<String> response5 = sendPutRequestWithoutBody(URI.create(uri + "/2/like/3"));

        assertAll(() -> {
                    assertEquals(404, response4.statusCode());
                    assertEquals(404, response5.statusCode());
                }
        );
    }

    @Test
    public void should_Not_Delete_Like_When_User_Not_Created_And_Film_Not_Created_() throws IOException, InterruptedException {
        Film film1 = new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);

        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        String json1 = gson.toJson(film1);
        String json2 = gson.toJson(film2);
        String json3 = gson.toJson(user1);
        String json4 = gson.toJson(user2);
        sendPostRequest(json1, uri);
        sendPostRequest(json2, uri);

        URI uriUsers = URI.create("http://localhost:8080/users");
        sendPostRequest(json3, uriUsers);
        sendPostRequest(json4, uriUsers);
        sendPutRequestWithoutBody(URI.create(uri + "/1/like/1"));
        sendPutRequestWithoutBody(URI.create(uri + "/1/like/2"));
        HttpResponse<String> response2 = sendGetRequest(URI.create(uri + "/1"));

        Film film3 = gson.fromJson(response2.body(), Film.class);
        assertEquals(2, film3.getIdUsersLike().size());

        sendDeleteRequest(URI.create(uri + "/1/like/1"));
        HttpResponse<String> response3 = sendGetRequest(URI.create(uri + "/1"));
        Film film4 = gson.fromJson(response3.body(), Film.class);
        assertEquals(1, film4.getIdUsersLike().size());

        HttpResponse<String> response4 = sendPutRequestWithoutBody(URI.create(uri + "/3/like/1"));
        HttpResponse<String> response5 = sendPutRequestWithoutBody(URI.create(uri + "/1/like/3"));

        assertAll(() -> {
                    assertEquals(404, response4.statusCode());
                    assertEquals(404, response5.statusCode());
                }
        );
    }

    @Test
    public void should_Get_10_Best_Films_When_Not_Count_And_Should_Get9_Best_Film_When_Count_Equals_9_And_Should_Status_Code_400_When_Count_Is_Negative() throws IOException, InterruptedException {
        Film film1 = new Film("1", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film2 = new Film("2", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film3 = new Film("3", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film4 = new Film("4", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film5 = new Film("5", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film6 = new Film("6", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film7 = new Film("7", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film8 = new Film("8", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film9 = new Film("9", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film10 = new Film("10", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film11 = new Film("11", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film12 = new Film("12", "class", LocalDate.of(2005, 10, 17),
                100);

        sendPostRequest(gson.toJson(film1), uri);
        sendPostRequest(gson.toJson(film2), uri);
        sendPostRequest(gson.toJson(film3), uri);
        sendPostRequest(gson.toJson(film4), uri);
        sendPostRequest(gson.toJson(film5), uri);
        sendPostRequest(gson.toJson(film6), uri);
        sendPostRequest(gson.toJson(film7), uri);
        sendPostRequest(gson.toJson(film8), uri);
        sendPostRequest(gson.toJson(film9), uri);
        sendPostRequest(gson.toJson(film10), uri);
        sendPostRequest(gson.toJson(film11), uri);
        sendPostRequest(gson.toJson(film12), uri);

        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("Q", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        String json3 = gson.toJson(user1);
        String json4 = gson.toJson(user2);
        URI uriUsers = URI.create("http://localhost:8080/users");
        sendPostRequest(json3, uriUsers);
        sendPostRequest(json4, uriUsers);

        sendPutRequestWithoutBody(URI.create(uri + "/2/like/1"));
        sendPutRequestWithoutBody(URI.create(uri + "/2/like/2"));
        sendPutRequestWithoutBody(URI.create(uri + "/1/like/1"));
        Type type = new TypeToken<List<Film>>() {
        }.getType();

        HttpResponse<String> response = sendGetRequest(URI.create(uri + "/popular"));
        List<Film> filmList = gson.fromJson(response.body(), type);

        assertAll(() -> {
            assertEquals(10, filmList.size());
            assertEquals(2, filmList.get(0).getIdUsersLike().size());
            assertEquals(1, filmList.get(1).getIdUsersLike().size());
                }
        );

        HttpResponse<String> response2 = sendGetRequest(URI.create(uri + "/popular?count=9"));
        List<Film> filmList2 = gson.fromJson(response2.body(), type);

        assertAll(() -> {
                    assertEquals(9, filmList2.size());
                    assertEquals(2, filmList2.get(0).getIdUsersLike().size());
                    assertEquals(1, filmList2.get(1).getIdUsersLike().size());
                }
        );

        HttpResponse<String> response3 = sendGetRequest(URI.create(uri + "/popular?count=-3"));
        assertEquals(400, response3.statusCode());
    }

    @AfterEach
    public void close() {
        SpringApplication.exit(context);
    }

    private HttpResponse<String> sendPostRequest(String json, URI uri) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private List<Film> getListFilm() throws IOException, InterruptedException {
        Type typeListOfUsers = new TypeToken<List<User>>(){}.getType();
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), typeListOfUsers);
    }

    private HttpResponse<String> sendGetRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPutRequest(String json, URI uri) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").PUT(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPutRequestWithoutBody(URI uri) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.noBody();
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").PUT(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
