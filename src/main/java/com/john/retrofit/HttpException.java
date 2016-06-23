package com.john.retrofit;


import retrofit2.Response;

/**
 * Created by oceanzhang on 16/3/9.
 */
public class HttpException extends Exception{
    private final int code;
    private final String message;
    private final transient Response<?> response;

    public HttpException(Response<?> response) {
        super("HTTP "+response.code() + " " + response.message());
        this.response = response;
        this.code = response.code();
        this.message = response.message();
    }
    public int code(){
        return this.code;
    }
    public String message(){
        return this.message;
    }
    public Response<?> response(){
        return this.response;
    }
}
