package com.example.buensaborback.presentation.advice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class Auth0Exception extends RuntimeException{
    public Auth0Exception(String message) {
        super(message);
    }
}
