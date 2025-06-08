package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import utils.ApiClient;
import utils.ConfigManager;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Book Management")
@Feature("Update Book API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UpdateBookTest {

    private static String baseURL;
    private static String authToken;

    @BeforeAll
    @Step("Authenticate user before tests")
    public static void setup() {
        baseURL = ConfigManager.get("baseURI");

        JSONObject loginPayload = new JSONObject();
        loginPayload.put("email", ConfigManager.get("email"));
        loginPayload.put("password", ConfigManager.get("password"));

        Response loginResponse = ApiClient.sendRequest(
                "POST",
                baseURL + "/login",
                Map.of("Content-Type", "application/json"),
                loginPayload.toString(),
                null,
                null
        );

        authToken = loginResponse.jsonPath().getString("access_token");
    }

    // Helper method to create a book and return its ID
    @Step("Create a new book and return its ID")
    private int createBook() {
        JSONObject payload = new JSONObject();
        payload.put("name", "Original Book");
        payload.put("author", "Adam");
        payload.put("published_year", 2000);
        payload.put("book_summary", "Detailed summary");

        Map<String, String> headers = Map.of(
                "Content-Type", "application/json",
                "Authorization", "Bearer " + authToken
        );

        Response response = ApiClient.sendRequest(
                "POST",
                baseURL + "/books/",
                headers,
                payload.toString(),
                null,
                null
        );

        Assertions.assertEquals(200, response.statusCode());
        return response.jsonPath().getInt("id");
    }

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update an existing book")
    @DisplayName("Update book successfully (Positive)")
    @Description("Ensures a book's details can be updated successfully when a valid ID is provided.")
    void testUpdateBook() {
        int bookId = createBook();

        JSONObject updatePayload = new JSONObject();
        updatePayload.put("name", "Updated Book Name");
        updatePayload.put("author", "Updated Author");
        updatePayload.put("published_year", 2023);
        updatePayload.put("book_summary", "Updated summary");

        Map<String, String> headers = Map.of(
                "Content-Type", "application/json",
                "Authorization", "Bearer " + authToken
        );

        Response response = ApiClient.sendRequest(
                "PUT",
                baseURL + "/books/{id}",
                headers,
                updatePayload.toString(),
                Map.of("id", bookId),
                null
        );

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getString("name"), equalTo(updatePayload.getString("name")));
        assertThat(response.jsonPath().getString("author"), equalTo(updatePayload.getString("author")));
        assertThat(response.jsonPath().getInt("published_year"), is(updatePayload.getInt("published_year")));
        assertThat(response.jsonPath().getString("book_summary"), equalTo(updatePayload.getString("book_summary")));
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    @Story("Update book with invalid ID")
    @DisplayName("Update non-existent book (Negative)")
    @Description("Checks that updating a book with a non-existent ID returns a 404 error.")
    void testUpdateNonExistentBook() {
        JSONObject updatePayload = new JSONObject();
        updatePayload.put("name", "Non-existent Book");

        Map<String, String> headers = Map.of(
                "Content-Type", "application/json",
                "Authorization", "Bearer " + authToken
        );

        Response response = ApiClient.sendRequest(
                "PUT",
                baseURL + "/books/{id}",
                headers,
                updatePayload.toString(),
                Map.of("id", 99999),
                null
        );

        assertThat(response.statusCode(), is(404));
    }
}