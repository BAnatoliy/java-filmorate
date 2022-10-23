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
        List<User> userList = getListUsers();
        assertTrue(userList.isEmpty());
    }

    @Test
    public void should_Create_Three_Valid_Users_And_Find_All_Users() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(uri, json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(uri, json2);
        String json3 = gson.toJson(new User( "ZX", "John", "zx@google.com", LocalDate.of(1997, 7, 25)));
        HttpResponse<String> response3 = sendPostRequest(uri, json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                }
        );

        List<User> userList = getListUsers();
        assertEquals(3, userList.size());
    }

    @Test
    public void should_Set_User_Name_Like_Login_When_Name_Is_Empty_Or_Null() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(uri, json1);
        String json2 = gson.toJson(new User("AS", "", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(uri, json2);
        String json3 = gson.toJson(new User( "ZX", null, "zx@google.com", LocalDate.of(1997, 7, 25)));
        HttpResponse<String> response3 = sendPostRequest(uri, json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                }
        );

        List<User> userList = getListUsers().stream().sorted(Comparator.comparingLong(User::getId)).collect(Collectors.toList());
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
        HttpResponse<String> response1 = sendPostRequest(uri, json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(uri, json2);
        String json3 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response3 = sendPostRequest(uri, json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<User> userList = getListUsers();
        assertEquals(2, userList.size());
    }

    @Test
    public void should_Create_One_Valid_User_And_Not_Create_User_When_Email_Is_Null_Or_Not_Contain_Symbol_Of_Email() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response = sendPostRequest(uri, json1);
        assertEquals(200, response.statusCode());

        String json2 = gson.toJson(new User("AS", "Billy", "as_yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(uri, json2);
        assertEquals(400, response2.statusCode());

        String json3 = gson.toJson(new User("ZX", "John", null, LocalDate.of(1997, 7, 25)));
        HttpResponse<String> response3 = sendPostRequest(uri, json3);
        assertEquals(400, response3.statusCode());

        List<User> userList = getListUsers();
        assertEquals(1, userList.size());
    }

    @Test
    public void should_Create_One_Valid_User_And_Not_Create_User_When_Login_Is_Null_Or_Empty() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(uri, json1);
        String json2 = gson.toJson(new User(null, "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(uri, json2);
        String json3 = gson.toJson(new User( "", "John", "zx@google.com", LocalDate.of(1997, 7, 25)));
        HttpResponse<String> response3 = sendPostRequest(uri, json3);
        String json4 = gson.toJson(new User( "   ", "Roy", "ty@bk.ru", LocalDate.of(2001, 5, 9)));
        HttpResponse<String> response4 = sendPostRequest(uri, json4);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(400, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                    assertEquals(400, response4.statusCode());
                }
        );

        List<User> userList = getListUsers();
        assertEquals(1, userList.size());
    }

    @Test
    public void should_Create_Two_Valid_User_And_Not_Create_User_When_Birthday_After_Today() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.now().minusDays(1)));
        HttpResponse<String> response1 = sendPostRequest(uri, json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.now()));
        HttpResponse<String> response2 = sendPostRequest(uri, json2);
        String json3 = gson.toJson(new User( "ZX", "John", "zx@google.com", LocalDate.now().plusDays(1)));
        HttpResponse<String> response3 = sendPostRequest(uri, json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(400, response3.statusCode());
                }
        );

        List<User> userList = getListUsers();
        assertEquals(2, userList.size());
    }

    @Test
    public void should_Not_Update_User_When_This_User_Not_Created() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(uri, json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(uri, json2);

        User newUser = new User("ZX", "John", "zx@google.com", LocalDate.of(1997, 7, 25));
        newUser.setId(111);
        String json3 = gson.toJson(newUser);
        HttpResponse<String> response3 = sendPutRequest(json3);

        assertAll(
                () -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(404, response3.statusCode());
                }
        );

        List<User> userList = getListUsers();
        assertEquals(2, userList.size());
    }

    @Test
    public void should_Update_User_When_This_User_Created() throws IOException, InterruptedException {
        String json1 = gson.toJson(new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27)));
        HttpResponse<String> response1 = sendPostRequest(uri, json1);
        String json2 = gson.toJson(new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15)));
        HttpResponse<String> response2 = sendPostRequest(uri, json2);

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

        List<User> userList = getListUsers();
        assertEquals(2, userList.size());
    }

    @Test
    public void should_Get_Created_User_By_Id_And_Get_Status_Code_404_When_User_Not_Created() throws IOException, InterruptedException {
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        String json1 = gson.toJson(user1);
        HttpResponse<String> response1 = sendPostRequest(uri, json1);
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        String json2 = gson.toJson(user2);
        HttpResponse<String> response2 = sendPostRequest(uri, json2);

        HttpResponse<String> response3 = sendGetRequest(URI.create(uri + "/1"));
        HttpResponse<String> response4 = sendGetRequest(URI.create(uri + "/2"));

        User gottenUser1 = gson.fromJson(response3.body(), User.class);
        User gottenUser2 = gson.fromJson(response4.body(), User.class);

        assertAll(() -> {
                    assertEquals(200, response1.statusCode());
                    assertEquals(200, response2.statusCode());
                    assertEquals(200, response3.statusCode());
                    assertEquals(200, response4.statusCode());
                    assertEquals(user1, gottenUser1);
                    assertEquals(user2, gottenUser2);
                }
        );

        HttpResponse<String> response5 = sendGetRequest(URI.create(uri + "/3"));
        assertEquals(404, response5.statusCode());
    }

    @Test
    public void should_Delete_Created_User_By_Id_And_Get_Status_Code_404_When_User_Not_Created() throws IOException, InterruptedException {
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        String json1 = gson.toJson(user1);
        sendPostRequest(uri, json1);
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        String json2 = gson.toJson(user2);
        sendPostRequest(uri, json2);
        List<User> userListBeforeDelete = getListUsers();

        HttpResponse<String> response = sendDeleteRequest(URI.create(uri + "/1"));
        List<User> userListAfterDelete = getListUsers();

        assertAll(() -> {
                    assertEquals(2, userListBeforeDelete.size());
                    assertEquals(1, userListAfterDelete.size());
                    assertEquals(200, response.statusCode());
                }
        );

        HttpResponse<String> response2 = sendDeleteRequest(URI.create(uri + "/3"));
        assertEquals(404, response2.statusCode());
    }

    @Test
    public void should_Not_Add_Friend_When_User_Not_Created_And_Friend_Not_Created_() throws IOException, InterruptedException {
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        User user3 = new User("AqS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));

        sendPostRequest(uri, gson.toJson(user1));
        sendPostRequest(uri, gson.toJson(user2));
        sendPostRequest(uri, gson.toJson(user3));

        HttpResponse<String> response = sendPutRequestWithoutBody(URI.create(uri + "/1/friends/2"));
        HttpResponse<String> response1 = sendPutRequestWithoutBody(URI.create(uri + "/3/friends/1"));

        HttpResponse<String> response2 = sendGetRequest(URI.create(uri + "/1"));
        HttpResponse<String> response3 = sendGetRequest(URI.create(uri + "/2"));
        HttpResponse<String> response4 = sendGetRequest(URI.create(uri + "/3"));

        User user4 = gson.fromJson(response2.body(), User.class);
        User user5 = gson.fromJson(response3.body(), User.class);
        User user6 = gson.fromJson(response4.body(), User.class);

        assertAll(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals(200, response1.statusCode());
                    assertEquals(2, user4.getFriendsId().size());
                    assertEquals(1, user5.getFriendsId().size());
                    assertEquals(1, user6.getFriendsId().size());
                }
        );

        HttpResponse<String> response5 = sendPutRequestWithoutBody(URI.create(uri + "/5/friends/1"));
        HttpResponse<String> response6 = sendPutRequestWithoutBody(URI.create(uri + "/2/friends/7"));

        assertAll(() -> {
                    assertEquals(404, response5.statusCode());
                    assertEquals(404, response6.statusCode());
                }
        );
    }

    @Test
    public void should_Not_Delete_Friend_When_User_Not_Created_And_Friend_Not_Created_() throws IOException, InterruptedException {
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        User user3 = new User("AqS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        sendPostRequest(uri, gson.toJson(user1));
        sendPostRequest(uri, gson.toJson(user2));
        sendPostRequest(uri, gson.toJson(user3));
        sendPutRequestWithoutBody(URI.create(uri + "/1/friends/2"));
        sendPutRequestWithoutBody(URI.create(uri + "/3/friends/1"));
        Type type = new TypeToken<List<User>>() {
        }.getType();

        HttpResponse<String> response = sendGetRequest(URI.create(uri + "/1/friends"));
        List<User> listUsers1Friends = gson.fromJson(response.body(), type);
        assertEquals(2, listUsers1Friends.size());

        sendDeleteRequest(URI.create(uri + "/1/friends/2"));
        HttpResponse<String> response1 = sendGetRequest(URI.create(uri + "/1/friends"));
        List<User> listUsers1FriendsAfterDelete = gson.fromJson(response1.body(), type);
        assertEquals(1, listUsers1FriendsAfterDelete.size());

        HttpResponse<String> response2 = sendDeleteRequest(URI.create(uri + "/5/friends"));
        assertEquals(405, response2.statusCode());
    }

    @Test
    public void should_Get_List_Of_Users_Friend_When_User_Created() throws IOException, InterruptedException {
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        User user3 = new User("AqS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));

        sendPostRequest(uri, gson.toJson(user1));
        sendPostRequest(uri, gson.toJson(user2));
        sendPostRequest(uri, gson.toJson(user3));

        sendPutRequestWithoutBody(URI.create(uri + "/1/friends/2"));
        sendPutRequestWithoutBody(URI.create(uri + "/3/friends/1"));

        Type type = new TypeToken<List<User>>() {
        }.getType();
        HttpResponse<String> response = sendGetRequest(URI.create(uri + "/1/friends"));
        List<User> listFriendUsers1 = gson.fromJson(response.body(), type);
        assertAll(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals(2, listFriendUsers1.size());
                }
        );

        HttpResponse<String> response2 = sendGetRequest(URI.create(uri + "/33/friends"));
        assertEquals(404, response2.statusCode());
    }

    @Test
    public void should_Get_List_Of_Users_Common_Friends_And_Other_User_When_User_And_Other_User_Created() throws IOException, InterruptedException {
        User user1 = new User("QW", "Ann", "qw@mail.ru", LocalDate.of(1995, 12, 27));
        User user2 = new User("AS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));
        User user3 = new User("AqS", "Billy", "as@yandex.ru", LocalDate.of(2000, 1, 15));

        sendPostRequest(uri, gson.toJson(user1));
        sendPostRequest(uri, gson.toJson(user2));
        sendPostRequest(uri, gson.toJson(user3));

        sendPutRequestWithoutBody(URI.create(uri + "/1/friends/2"));
        sendPutRequestWithoutBody(URI.create(uri + "/3/friends/1"));

        Type type = new TypeToken<List<User>>() {
        }.getType();
        HttpResponse<String> response = sendGetRequest(URI.create(uri + "/2/friends/common/3"));

        List<User> listCommonFriendsUsers2AndUsers3 = gson.fromJson(response.body(), type);
        assertAll(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals(1, listCommonFriendsUsers2AndUsers3.size());
                    assertEquals(user1, listCommonFriendsUsers2AndUsers3.get(0));
                }
        );

        HttpResponse<String> response2 = sendGetRequest(URI.create(uri + "/22/friends/common/3"));
        HttpResponse<String> response3 = sendGetRequest(URI.create(uri + "/2/friends/common/33"));

        assertAll(() -> {
        assertEquals(404, response2.statusCode());
        assertEquals(404, response3.statusCode());
                }
        );
    }

    @AfterEach
    public void close() {
        SpringApplication.exit(context);
    }

    private HttpResponse<String> sendDeleteRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPostRequest(URI uri, String json) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").POST(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private List<User> getListUsers() throws IOException, InterruptedException {
        Type typeListOfUsers = new TypeToken<List<User>>(){}.getType();
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").GET().build();
        HttpResponse<String> response4 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response4.body(), typeListOfUsers);
    }

    private HttpResponse<String> sendGetRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request2 = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").GET().build();
        return client.send(request2, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPutRequest(String json) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").PUT(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendPutRequestWithoutBody(URI uri) throws IOException, InterruptedException {
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.noBody();
        HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").PUT(body).build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
