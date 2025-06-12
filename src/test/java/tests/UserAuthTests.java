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
import org.apache.commons.lang3.RandomStringUtils;

@Epic("User Authentication")
@Feature("Signup & Login API Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserAuthTests {

    private static String testEmail;
    private static String testPassword;
    private static String baseURL;

    @BeforeAll
    @Step("Load base URL before tests")
    public static void setup() {

        baseURL = ConfigManager.get("baseURI");
    }

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Signup with valid credentials")
    @DisplayName("User Signup")
    @Description("Register a new user with valid email and password")
    void testSignup() {
        String randomUsername = RandomStringUtils.randomAlphanumeric(10);
        testEmail = randomUsername + "@example.com";
        testPassword = "password123";

        JSONObject payload = new JSONObject();
        payload.put("email", testEmail);
        payload.put("password", testPassword);

        Response response = sendSignupRequest(payload);

        System.out.println("Signup status: " + response.statusCode());
        System.out.println("Signup response: " + response.asString());
        assertThat(response.statusCode(), is(200));
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    @Story("Handle duplicate signup")
    @DisplayName("Duplicate Signup")
    @Description("Attempt to sign up with an already registered email")
    void testDuplicateSignup() {
        String randomUsername = RandomStringUtils.randomAlphanumeric(10);
        String email = randomUsername + "@example.com";

        JSONObject payload = new JSONObject();
        payload.put("email", email);
        payload.put("password", "password123");

        // First signup
        Response firstResponse = sendSignupRequest(payload);
        System.out.println("Initial Signup status: " + firstResponse.statusCode());
        assertThat(firstResponse.statusCode(), is(200));

        // Duplicate signup
        Response duplicateResponse = sendSignupRequest(payload);
        System.out.println("Duplicate Signup status: " + duplicateResponse.statusCode());
        assertThat(duplicateResponse.statusCode(), is(400));
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login with valid credentials")
    @DisplayName("User Login")
    @Description("Login with the credentials used during signup")
    void testLogin() {
        JSONObject payload = new JSONObject();
        payload.put("email", testEmail);
        payload.put("password", testPassword);

        Response response = sendLoginRequest(payload);
        System.out.println("Login status: " + response.statusCode());
        assertThat(response.statusCode(), is(200));
        assertThat(response.jsonPath().getString("access_token"), notNullValue());
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @Story("Login with incorrect password")
    @DisplayName("Login with Wrong Password")
    @Description("Attempt to login with the correct email but wrong password")
    void testLoginWrongPassword() {
        JSONObject payload = new JSONObject();
        payload.put("email", testEmail);
        payload.put("password", "wrongpass");

        Response response = sendLoginRequest(payload);
        System.out.println("Wrong login status: " + response.statusCode());
        assertThat(response.statusCode(), is(400));
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @Story("Login with incorrect email")
    @DisplayName("Login with Wrong Email")
    @Description("Attempt to login with a wrong email and correct password")
    void testLoginWrongEmail() {
        JSONObject payload = new JSONObject();
        payload.put("email", "test@newemail.com");
        payload.put("password", testPassword);

        Response response = sendLoginRequest(payload);
        System.out.println("Wrong login status: " + response.statusCode());
        assertThat(response.statusCode(), is(400));
    }

    @Step("Send Signup Request")
    private Response sendSignupRequest(JSONObject payload) {
        return ApiClient.sendRequest(
                "POST",
                baseURL + "/signup",
                Map.of("Content-Type", "application/json"),
                payload.toString(),
                null,
                null
        );
    }

    @Step("Send Login Request")
    private Response sendLoginRequest(JSONObject payload) {
        return ApiClient.sendRequest(
                "POST",
                baseURL + "/login",
                Map.of("Content-Type", "application/json"),
                payload.toString(),
                null,
                null
        );
    }
}
