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
@Feature("Delete Book API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteBookTest {

    private static String baseURL;
    private static String authToken;
    private static int bookId;

    @BeforeAll
    @Step("Authenticate user before tests")
    public static void setup() {
        baseURL = ConfigManager.get("baseURI");

        // Login once and get token
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
    @Step("Create a book for testing deletion")
    private int createBook() {
        JSONObject payload = new JSONObject();
        payload.put("name", "Gitanjali");
        payload.put("author", "Rabindranath Tagore");
        payload.put("published_year", 1910);
        payload.put("book_summary", "A collection of devotional poems");

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
    @Story("Delete an existing book")
    @DisplayName("Delete book (Positive)")
    @Description("This test validates successful deletion of an existing book by ID.")
    void testDeleteBook() {
        // Step 1: Create a book
        bookId = createBook();

        // Step 2: Delete the created book
        Response deleteResponse = ApiClient.sendRequest(
                "DELETE",
                baseURL + "/books/{id}",
                Map.of("Authorization", "Bearer " + authToken),
                null,
                Map.of("id", bookId),
                null
        );

        assertThat(deleteResponse.statusCode(), is(200));
        assertThat(deleteResponse.jsonPath().getString("message").toLowerCase(), containsString("deleted"));
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    @Story("Attempt to delete a non-existent book")
    @DisplayName("Delete book - Book Not Found (Negative)")
    @Description("This test ensures the API returns 404 when attempting to delete a non-existent book ID.")
    void testDeleteBookNotFound() {
        // Use an invalid/non-existent book ID
        int invalidBookId = bookId + 9999;

        Map<String, String> deleteHeaders = Map.of("Authorization", "Bearer " + authToken);

        Response deleteResponse = ApiClient.sendRequest(
                "DELETE",
                baseURL + "/books/{id}",
                deleteHeaders,
                null,
                Map.of("id", invalidBookId),
                null
        );

        // Adjust status code and message checks according to your API spec
        assertThat(deleteResponse.statusCode(), is(404));
    }
}
