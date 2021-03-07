package kr.co.theplay.service.api.advice.exception;

public class CommonRuntimeException extends RuntimeException{

    private String[] detailMessages;

    public String[] getDetailMessages() {
        return this.detailMessages;
    }

    public CommonRuntimeException(String message){
        super(message);
    }
}
