package kr.co.theplay.service.api.advice.exception;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class ApiParamNotValidException extends RuntimeException {

    private Errors errors;

    public ApiParamNotValidException(Errors errors){
        this.errors = errors;
    }
}
