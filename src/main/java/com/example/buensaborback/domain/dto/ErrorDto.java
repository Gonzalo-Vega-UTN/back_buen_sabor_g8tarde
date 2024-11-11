package com.example.buensaborback.domain.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ErrorDto {
    private String message;
    private int statusCode;
    private String error;
    private String errorCode;
}
