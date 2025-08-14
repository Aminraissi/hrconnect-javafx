package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static final String ENV_FILE = ".env";
    private static Properties properties = new Properties();

    static {
        loadEnvironmentVariables();
    }

    private static void loadEnvironmentVariables() {
        // First try to load from .env file
        try (FileInputStream input = new FileInputStream(ENV_FILE)) {
            properties.load(input);
            System.out.println("Configuration loaded from .env file");
        } catch (IOException e) {
            System.out.println("Warning: .env file not found, using system environment variables only");
        }
    }

    public static String get(String key) {
        // First check .env file properties
        String value = properties.getProperty(key);

        // If not found in .env, check system environment variables
        if (value == null) {
            value = System.getenv(key);
        }

        return value;
    }
}
