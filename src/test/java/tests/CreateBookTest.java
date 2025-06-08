package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import utils.ApiClient;
import utils.ConfigManager;
import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Book Management")
@Feature("Create Book API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateBookTest {

    private static String baseURL;
    private static String authToken;

    @BeforeAll
    @Step("Setup base URL and authenticate user")
    static void setup() {
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

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create a valid new book")
    @DisplayName("Create a new book successfully (Positive)")
    @Description("This test verifies that a book can be created successfully with valid details.")
    public void testCreateBook() {
        JSONObject payload = createBookPayload("Emma", "Jane Austen", 1815, "Very interesting book");

        Response response = sendCreateBookRequest(payload);

        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getString("id"), notNullValue());
        assertThat(response.jsonPath().getString("name"), equalTo(payload.get("name")));
        assertThat(response.jsonPath().getString("book_summary"), equalTo(payload.get("book_summary")));
        assertThat(response.jsonPath().getString("author"), equalTo(payload.get("author")));
        assertThat(response.jsonPath().getString("published_year"), equalTo(payload.get("published_year").toString()));
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    @Story("Create book with missing required field")
    @DisplayName("Create Book - Missing Required Fields (Negative)")
    @Description("This test checks that creating a book without 'name' fails with proper error response.")
    void testCreateBookMissingFields() {
        JSONObject payload = createBookPayload(null, "Mitch Albom", 1997,
                "A heartwarming memoir about life lessons from a dying professor");

        Response response = sendCreateBookRequest(payload);
        assertThat(response.statusCode(), is(500));
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.MINOR)
    @Story("Create book with duplicate ID")
    @DisplayName("Create Book - Duplicate Book ID (Negative)")
    @Description("This test ensures the API rejects book creation with an already used ID.")
    void testCreateBookDuplicateId() {
        JSONObject payload = createBookPayload("Emma", "Jane Austen", 1815, "Very interesting book");

        Response firstResponse = sendCreateBookRequest(payload);
        assertThat(firstResponse.statusCode(), is(200));
        String createdId = firstResponse.jsonPath().getString("id");

        payload.put("id", createdId);
        Response secondResponse = sendCreateBookRequest(payload);
        assertThat(secondResponse.statusCode(), is(500));
    }

    @Step("Create book payload with title: {0}, author: {1}, year: {2}")
    private JSONObject createBookPayload(String name, String author, int year, String summary) {
        JSONObject payload = new JSONObject();
        if (name != null) payload.put("name", name);
        payload.put("author", author);
        payload.put("published_year", year);
        payload.put("book_summary", summary);
        return payload;
    }

    @Step("Send POST /books/ request with auth token")
    private Response sendCreateBookRequest(JSONObject payload) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + authToken);

        return ApiClient.sendRequest(
                "POST",
                baseURL + "/books/",
                headers,
                payload.toString(),
                null,
                null
        );
    }
}
