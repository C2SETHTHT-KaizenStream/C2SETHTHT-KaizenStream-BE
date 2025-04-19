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
    USER_ALREADY_HAS_PROFILE(409, "User already has a profile",HttpStatus.BAD_REQUEST),

    ITEM_NOT_EXIST(1009, "Item does not exist", HttpStatus.BAD_REQUEST),

    WALLET_NOT_EXIST(1010, "Wallet doest not exist", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE(1011,"Your wallet is not enough money", HttpStatus.OK),
    PROFILES_NOT_EXIST(1004,"Profile_id is not exist", HttpStatus.BAD_REQUEST),
    LIVESTREAM_NOT_EXIST(1012, "Livestream is not exist", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_EXIST(1002, "Schedule does not exist",HttpStatus.NOT_FOUND ),

    IMAGE_UPLOAD_FAILED(1006, "Avatar upload failed", HttpStatus.INTERNAL_SERVER_ERROR),
    REPORT_NOT_EXIST(1013, "Report is not exist", HttpStatus.BAD_REQUEST),
    
    IMAGE_NOT_FOUND(1014, "Image not found", HttpStatus.NOT_FOUND),

    BLOG_NOT_FOUND(2001, "Blog does not exist", HttpStatus.NOT_FOUND),
    BLOG_ACCESS_DENIED(2002, "You do not have permission to perform this action", HttpStatus.FORBIDDEN),
    BLOG_INVALID_STATUS(2003, "Invalid blog status", HttpStatus.BAD_REQUEST),
    BLOG_REQUIRED_TITLE(2004, "Blog title is required", HttpStatus.BAD_REQUEST),
    BLOG_REQUIRED_CONTENT(2005, "Blog content is required", HttpStatus.BAD_REQUEST),

    COMMENT_NOT_FOUND(3001, "Comment does not exist", HttpStatus.NOT_FOUND),
    BLOG_NOT_OWNER(3002, "You are not the owner of this blog", HttpStatus.FORBIDDEN),

    LIVESTREAM_NOT_FOUND(4001, "Livestream does not exist", HttpStatus.NOT_FOUND),

    ACCOUNT_BANNED(1014, "This account has been banned", HttpStatus.OK);
    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode=statusCode;
    }

    private  int code;
    private  String message;
    private HttpStatusCode statusCode;


}
