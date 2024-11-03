package com.example.buensaborback.configs;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local", "dev"})
public class EnvironmentVariablesConfig {

    private final Dotenv dotenv;

    @Autowired
    public EnvironmentVariablesConfig(Dotenv dotenv) {
        this.dotenv = dotenv;
    }

    @PostConstruct
    public void init() {
        if (dotenv == null) {
            throw new RuntimeException("Dotenv configuration could not be loaded");
        }

        System.setProperty("DB_HOST", dotenv.get("DB_HOST", "default_db_host"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT", "default_db_port"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME", "default_db_name"));
        System.setProperty("DB_USER", dotenv.get("DB_USER", "default_db_user"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD", "default_db_password"));
        System.setProperty("CLOUDINARY_CLOUD_NAME", dotenv.get("CLOUDINARY_CLOUD_NAME", "default_cloud_name"));
        System.setProperty("CLOUDINARY_API_KEY", dotenv.get("CLOUDINARY_API_KEY", "default_api_key"));
        System.setProperty("CLOUDINARY_SECRET", dotenv.get("CLOUDINARY_SECRET", "default_secret"));
    }
}
