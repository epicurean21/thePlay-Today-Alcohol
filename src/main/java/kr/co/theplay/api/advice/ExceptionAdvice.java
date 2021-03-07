package kr.co.theplay.api.advice;

import io.swagger.models.auth.In;
import jdk.nashorn.internal.runtime.ECMAException;
import kr.co.theplay.service.api.advice.exception.CommonBadRequestException;
import kr.co.theplay.service.api.advice.exception.CommonNotFoundException;
import kr.co.theplay.service.api.advice.exception.CommonRuntimeException;
import kr.co.theplay.service.api.common.ResponseService;
import kr.co.theplay.service.api.common.model.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;
    private final MessageSource messageSource;

    /**
     *
     * BAD REQUEST 에 대한 공통처리리     */

    @ExceptionHandler(CommonNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult CommonNotFound(HttpServletRequest request, CommonNotFoundException e){
        return generateFailResult(request, e);
    }

    /**
     *
     * BAD REQUEST 에 대한 공통처리리     */

    @ExceptionHandler(CommonBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected CommonResult CommonBadRequest(HttpServletRequest request, CommonBadRequestException e) {
        return generateFailResult(request, e);
    }

    private CommonResult generateFailResult(HttpServletRequest request, CommonRuntimeException e){
        return responseService.getFailResult(
                Integer.parseInt(getMessage(e.getMessage() + ".code")),
                getMessage(e.getMessage()+".msg", e.getDetailMessages())
        );
    }

    private String getMessage(String code){
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args){
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
