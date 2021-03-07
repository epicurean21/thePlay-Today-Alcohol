package kr.co.theplay.service.api.advice.exception;

public class CommonBadRequestException extends CommonRuntimeException{
    public  CommonBadRequestException(String message){
        super(message);
    }
}
