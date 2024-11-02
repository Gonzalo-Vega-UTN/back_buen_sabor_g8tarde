package com.example.buensaborback.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Value("${auth0.audience}")
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Value("${web.cors.allowed-origins}")
    private String corsAllowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults()) //por defecto spring va a buscar un bean con el nombre "corsConfigurationSource".
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests

                        // Definir reglas para ArticuloInsumoController
                        .requestMatchers(HttpMethod.GET, "/api/articulos/insumos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/articulos/insumos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/articulos/insumos").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/articulos/insumos/bulk").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/articulos/insumos/uploads").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/articulos/insumos/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/articulos/insumos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/articulos/insumos/search").permitAll()

                        // Definir reglas para ArticuloManufacturadoController
                        .requestMatchers(HttpMethod.GET, "/api/articulos/manufacturados").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/articulos/manufacturados/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/articulos/manufacturados").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/articulos/manufacturados/bulk").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/articulos/manufacturados/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/articulos/manufacturados/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/articulos/manufacturados/search").permitAll()

                        // Categorias
                        .requestMatchers(HttpMethod.GET, "/api/categorias").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/padres").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/alta").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/categorias/bulk").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").permitAll()

                        //Clientes
                        .requestMatchers(HttpMethod.GET, "/api/clientes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/clientes/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/clientes").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/clientes/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").permitAll()

                        //Domicilios
                        .requestMatchers(HttpMethod.GET, "/api/domicilios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/domicilios/alta").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/domicilios/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/domicilios/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/domicilios").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/domicilios/**").permitAll()

                        //Empresa
                        .requestMatchers(HttpMethod.GET, "/api/empresas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/empresas/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/empresas").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/empresas/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/empresas/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/empresas/**").permitAll()

                        //Mercado Pago
                        .requestMatchers(HttpMethod.POST, "/api/mercado-pago/crear_preference_mp").permitAll()

                        //Pedido
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pedidos").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/actualizar/**").permitAll()

                        //Promocion
                        .requestMatchers(HttpMethod.GET, "/api/promociones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/promociones/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/promociones").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/promociones/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/promociones/**").permitAll()

                        //PromocionDetalle
                        .requestMatchers(HttpMethod.GET, "/api/promociones-detalles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/promociones-detalles/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/promociones-detalles").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/promociones-detalles/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/promociones-detalles/**").permitAll()

                        //Reportes
                        .requestMatchers(HttpMethod.GET, "/api/reportes/top-products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reportes/ReporteTotales").permitAll()

                        //Sucursal
                        .requestMatchers(HttpMethod.POST, "/api/sucursales").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sucursales/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sucursales").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/sucursales/{id}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/sucursales/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sucursales/empresa/{empresaId}").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/sucursales/{id}**").permitAll()

                        //Unidad Medida
                        .requestMatchers(HttpMethod.GET, "/api/unidades-medida").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/unidades-medida/alta").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/unidades-medida/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/unidades-medida/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/unidades-medida").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/unidades-medida/bulk").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/unidades-medida/{id}").permitAll()

                        //Usuario
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/validar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/cliente/{nombreUsuario}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").permitAll()

                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(corsAllowedOrigins.split(",")));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("X-Get-Header"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("permissions");
        converter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }
}
