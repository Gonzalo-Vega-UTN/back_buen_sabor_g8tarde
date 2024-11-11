package com.example.buensaborback.presentation.advice;

import com.example.buensaborback.domain.dto.ErrorDto;
import com.example.buensaborback.presentation.advice.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice(annotations = RestController.class)
public class RestControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(RestControllerAdvice.class);

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(NotFoundException e){
        String errorMsg = e.getClass().getSimpleName()+ " : " + e.getMessage();
        logger.error(errorMsg);
        return new ResponseEntity<>(ErrorDto.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = EntityInUseException .class)
    public ResponseEntity<ErrorDto> handleEntityInUseException (EntityInUseException  e){
        String errorMsg = e.getClass().getSimpleName()+ " : " + e.getMessage();
        logger.error(errorMsg);
        return new ResponseEntity<>(ErrorDto.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.CONFLICT.value())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ErrorDto> handleBadRequestException(BadRequestException e){
        String errorMsg = e.getClass().getSimpleName()+ " : " + e.getMessage();
        logger.error(errorMsg);
        return new ResponseEntity<>(ErrorDto.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UnauthorizeException.class)
    public ResponseEntity<ErrorDto> handleUnauthorizeException(UnauthorizeException e){
        String errorMsg = e.getClass().getSimpleName()+ " : " + e.getMessage();
        logger.error(errorMsg);
        return new ResponseEntity<>(ErrorDto.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .build(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = DuplicateEntryException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEntryException(DuplicateEntryException e){
        String errorMsg = e.getClass().getSimpleName()+ " : " + e.getMessage();
        logger.error(errorMsg);
        return new ResponseEntity<>(ErrorDto.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.CONFLICT.value())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InsufficientStock.class)
    public ResponseEntity<ErrorDto> handleInsufficientStock(InsufficientStock e){
        String errorMsg = e.getClass().getSimpleName()+ " : " + e.getMessage();
        logger.error(errorMsg);
        return new ResponseEntity<>(ErrorDto.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.CONFLICT.value())
                .build(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ImageUploadLimitException.class)
    public ResponseEntity<ErrorDto> handleImageUploadLimitException(ImageUploadLimitException e){
        String errorMsg = e.getClass().getSimpleName()+ " : " + e.getMessage();
        logger.error(errorMsg);
        return new ResponseEntity<>(ErrorDto.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .build(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = Auth0Exception.class)
    public ResponseEntity<ErrorDto> handleAuth0Exception(Auth0Exception e){
        String errorMsg = e.getClass().getSimpleName()+ " : " + e.getMessage();
        logger.error(errorMsg);
        return new ResponseEntity<>(ErrorDto.builder()
                .message(e.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build(), HttpStatus.BAD_REQUEST);
    }
}
