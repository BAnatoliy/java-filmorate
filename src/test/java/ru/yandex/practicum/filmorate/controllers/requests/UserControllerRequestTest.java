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
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootApplication
public class UserControllerRequestTest {
    private ConfigurableApplicationContext context;
    private final URI uri = URI.create("http://localhost:8080/users");
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    public void init() {
        context = SpringApplication.run(FilmorateApplication.class);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateAdapter());
        gson = gsonBuilder.create();
        client = HttpClient.newHttpClient();
    }

    @Test
    public void should_List_Of_Users_Be_Empty_When_Users_Not_Created() throws IOException, InterruptedException {
        List<User> userList = sendGetRequest();
        assertTrue(userList.isEmpty());
    }

    @Test
    public void should_Create_Three_Valid_Users_And_Find_All_Users() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new User( "ZX", "John", "zx@google.com", LocalDate.of(1997, 7, 25)));
        HttpResponse<String> response3 = sendPostRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                }
        );

        List<User> userList = sendGetRequest();
        assertEquals(3, userList.size());
    }

    @Test
    public void should_Set_User_Name_Like_Login_When_Name_Is_Empty_Or_Null() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new User("AS", "", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new User( "ZX", null, "zx@google.com", LocalDate.of(1997, 7, 25)));
        HttpResponse<String> response3 = sendPostRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                }
        );

        List<User> userList = sendGetRequest().stream().sorted(Comparator.comparingInt(User::getId)).collect(Collectors.toList());
        assertAll(
                () -> {
                    assertEquals("Ann", userList.get(0).getName());
                    assertEquals("AS", userList.get(1).getName());
                    assertEquals("ZX", userList.get(2).getName());
                    assertEquals(3, userList.size());
                }
        );
    }

    @Test
    public void should_Create_Two_Valid_User_And_Not_Create_User_When_This_User_Already_Created() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response3 = sendPostRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(500, response3.statusCode());
                }
        );

        List<User> userList = sendGetRequest();
        assertEquals(2, userList.size());
    }

    @Test
    public void should_Create_One_Valid_User_And_Not_Create_User_When_Email_Is_Null_Or_Not_Contain_Symbol_Of_Email() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response = sendPostRequest(json1);
        assertEquals(200, response.statusCode());

        String json2 = gson.toJson(new User("AS", "Billy", "as_yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(json2);
        assertEquals(400, response2.statusCode());

        String json3 = gson.toJson(new User("ZX", "John", null, LocalDate.of(1997, 7, 25)));
        HttpResponse<String> response3 = sendPostRequest(json3);
        assertEquals(400, response3.statusCode());

        List<User> userList = sendGetRequest();
        assertEquals(1, userList.size());
    }

    @Test
    public void should_Create_One_Valid_User_And_Not_Create_User_When_Login_Is_Null_Or_Empty() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new User(null, "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new User( "", "John", "zx@google.com", LocalDate.of(1997, 7, 25)));
        HttpResponse<String> response3 = sendPostRequest(json3);
        String json4 = gson.toJson(new User( "   ", "Roy", "ty@bk.ru", LocalDate.of(2001, 5, 9)));
        HttpResponse<String> response4 = sendPostRequest(json4);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(400, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                    assertEquals(400, response4.statusCode());
                }
        );

        List<User> userList = sendGetRequest();
        assertEquals(1, userList.size());
    }

    @Test
    public void should_Create_Two_Valid_User_And_Not_Create_User_When_Birthday_After_Today() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.now().minusDays(1)));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.now()));
        HttpResponse<String> response2 = sendPostRequest(json2);
        String json3 = gson.toJson(new User( "ZX", "John", "zx@google.com", LocalDate.now().plusDays(1)));
        HttpResponse<String> response3 = sendPostRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<User> userList = sendGetRequest();
        assertEquals(2, userList.size());
    }

    @Test
    public void should_Not_Update_User_When_This_User_Not_Created() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(json2);

        User newUser = new User("ZX", "John", "zx@google.com", LocalDate.of(1997, 7, 25));
        newUser.setId(111);
        String json3 = gson.toJson(newUser);
        HttpResponse<String> response3 = sendPutRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(500, response3.statusCode());
                }
        );

        List<User> userList = sendGetRequest();
        assertEquals(2, userList.size());
    }

    @Test
    public void should_Update_User_When_This_User_Created() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(json2);

        User newUser = new User("ZX", "John", "zx@google.com", LocalDate.of(1997, 7, 25));
        newUser.setId(2);
        String json3 = gson.toJson(newUser);
        HttpResponse<String> response3 = sendPutRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                }
        );

        List<User> userList = sendGetRequest();
        assertEquals(2, userList.size());
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

    private List<User> sendGetRequest() throws IOException, InterruptedException {
        Type typeListOfUsers = new TypeToken<List<User>>(){}.getType();
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").GET().build();
        HttpResponse<String> response4 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response4.body(), typeListOfUsers);
    }

    private HttpResponse<String> sendPutRequest(String json) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").PUT(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
