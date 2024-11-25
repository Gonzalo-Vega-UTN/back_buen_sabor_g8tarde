package com.example.buensaborback.configs;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;

@Configuration
@Slf4j
@Profile({"local", "dev"})
public class DotenvConfig {
    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    @Bean
    public Dotenv dotenv() {
        log.info("Cargando configuracion del perfil " + activeProfile);
        return Dotenv.configure()
                .filename(".env") // O ".env.dev" si quieres usar otro archivo para dev
                .load();
    }
}

/*
 @Bean
    public Dotenv dotenv() {
        String activeProfile = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
        if (activeProfile == null) {
            activeProfile = "dev"; // Valor predeterminado si no se establece ningún perfil
        }

        String envFileName = ".env." + activeProfile;
        return Dotenv.configure()
                .filename(envFileName)
                .ignoreIfMissing()
                .load();
    }
 */