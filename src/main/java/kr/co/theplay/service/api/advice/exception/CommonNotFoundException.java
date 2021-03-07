package kr.co.theplay.service.api.advice.exception;

public class CommonNotFoundException extends CommonRuntimeException{

    public CommonNotFoundException(String message){
        super(message);
    }
}
