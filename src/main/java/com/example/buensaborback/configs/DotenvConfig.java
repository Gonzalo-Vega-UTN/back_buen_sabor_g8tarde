package com.example.buensaborback.configs;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.AbstractEnvironment;

@Configuration
public class DotenvConfig {

    @Bean
    public Dotenv dotenv() {
        String activeProfile = System.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
        System.out.println("ACTIVE_PROFILES_PROPERTY_NAME "  + activeProfile);
        if (activeProfile == null) {
            activeProfile = "dev"; // Valor predeterminado si no se establece ningún perfil
        }

        System.out.println("ACTIVE_PROFILES_PROPERTY_NAME "  + activeProfile);

        String envFileName = ".env." + activeProfile;
        return Dotenv.configure()
                .filename(envFileName)
                .ignoreIfMissing()
                .load();
    }
}
