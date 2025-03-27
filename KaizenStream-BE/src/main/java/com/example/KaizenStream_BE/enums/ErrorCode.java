package com.example.KaizenStream_BE.enums;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(999,"Uncategorized exception !", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"Invalid Message Key",HttpStatus.BAD_REQUEST),

    USER_EXISTED(1002,"User existed",HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1002,"User don't  exist",HttpStatus.NOT_FOUND),

    USERNAME_INVALID(1003, "User name must be at least 3 characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1005,"Wrong password",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006,"You do not have permission",HttpStatus.FORBIDDEN),

    PASSWORD_INVALID(1004, "Password must be at least 8 characters",HttpStatus.BAD_REQUEST),
    INVALID_DOB(1005,"Your age must be at least {min}",HttpStatus.FORBIDDEN),
    INVALID_EMAIL(1008, "Invalid email adress",HttpStatus.NOT_FOUND),
    INVALID_ROLE( 1007 , "Invalid role default",HttpStatus.NOT_FOUND),

    ITEM_NOT_EXIST(1009, "Item does not exist", HttpStatus.BAD_REQUEST),

    WALLET_NOT_EXIST(1010, "Wallet doest not exist", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE(1011,"Your wallet is not enough money", HttpStatus.BAD_REQUEST),

    LIVESTREAM_NOT_EXIST(1012, "Livestream is not exist", HttpStatus.BAD_REQUEST)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode=statusCode;
    }

    private  int code;
    private  String message;
    private HttpStatusCode statusCode;

}
