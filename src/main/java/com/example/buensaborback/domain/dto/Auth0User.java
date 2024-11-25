package com.example.buensaborback.domain.dto;

import com.example.buensaborback.domain.entities.enums.Rol;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Auth0User {
    private boolean blocked;  //campo para crear user
    @JsonProperty("created_at")
    private String createdAt;
    private String email; //campo para crear user
    @JsonProperty("email_verified")
    private Boolean emailVerified;
    private List<Identity> identities;
    private String name; //campo para crear user
    private String nickname; //campo para crear user
    private String picture;
    @JsonProperty("updated_at")
    private String updatedAt;
    @JsonProperty("user_id")
    private String userId;
    @Builder.Default
    @JsonProperty(defaultValue = "Username-Password-Authentication")
    private String connection = "Username-Password-Authentication";
    private String password; //campo para crear user, pero nunca es devuelto
    private Rol rol;

    @Data
    public static class Identity {
        private String connection;
        @JsonProperty("user_id")
        private String userId;
        private String provider;
        @JsonProperty("isSocial")
        private boolean isSocial;
    }


    @JsonSetter
    public void setConnection(String connection) {
        this.connection = (connection != null) ? connection : "Username-Password-Authentication";
    }
}

