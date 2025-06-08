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
@Feature("Get Book API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GetBookTest {

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
    @Step("Create a book and return its ID")
    private int createBook() {
        JSONObject payload = new JSONObject();
        payload.put("name", "Tuesdays");
        payload.put("author", "Mitch Albom");
        payload.put("published_year", 1997);
        payload.put("book_summary", "A heartwarming memoir about life lessons from a dying professor");

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
    @Story("Get book by valid ID")
    @DisplayName("Get created book by ID (Positive)")
    @Description("Verifies that a book can be retrieved successfully by its ID after creation.")
    void testGetBook() {
        int bookId = createBook();

        Response response = ApiClient.sendRequest(
                "GET",
                baseURL + "/books/{id}",
                Map.of("Authorization", "Bearer " + authToken),
                null,
                Map.of("id", bookId),
                null
        );

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getInt("id"), is(bookId));
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    @Story("Get book by non-existent ID")
    @DisplayName("Get non-existent book by ID (Negative)")
    @Description("Validates that the API returns 404 for a non-existent book ID.")
    void testGetNonExistentBook() {
        Response response = ApiClient.sendRequest(
                "GET",
                baseURL + "/books/{id}",
                Map.of("Authorization", "Bearer " + authToken),
                null,
                Map.of("id", 99999),
                null
        );

        assertThat(response.statusCode(), is(404));
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.MINOR)
    @Story("Get book with invalid ID format")
    @DisplayName("Get book with invalid book ID format (Negative)")
    @Description("Checks if API handles an invalid ID format properly with a 422 status.")
    void testGetBookInvalidIdFormat() {
        Response response = ApiClient.sendRequest(
                "GET",
                baseURL + "/books/{id}",
                Map.of("Authorization", "Bearer " + authToken),
                null,
                Map.of("id", "invalidId"),  // invalid string id
                null
        );

        assertThat(response.statusCode(), is(422));
    }
}
