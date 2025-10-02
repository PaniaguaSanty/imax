package com.cinema.imax_showTime_service.exception;

public class MovieNotFoundException extends RuntimeException{

    public MovieNotFoundException(String message) {
        super(message);
    }

}
