package kr.co.theplay.service.api.advice.exception;

public class CommonConflictException extends CommonRuntimeException{

    public CommonConflictException(String message){
        super(message);
    }
}
