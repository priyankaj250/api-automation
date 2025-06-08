package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();
    private static final String DEFAULT_ENV = "dev";

    static {
        String env = System.getProperty("env", DEFAULT_ENV);
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config/" + env + ".properties");
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config for env: " + env, e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
