package kr.co.theplay.service.common;

import kr.co.theplay.service.common.model.CommonResult;
import kr.co.theplay.service.common.model.ListResult;
import kr.co.theplay.service.common.model.SingleResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {
    @Getter
    @AllArgsConstructor
    public enum CommonResponse{
        SUCCESS(0, "성공하였습니다."),
        FAIL(-1, "실패하였습니다.");

        int code;
        String msg;
    }

    private MessageSource messageSource;

    @Builder
    public ResponseService(MessageSource messageSource){
        this.messageSource = messageSource;
    }

    /**
     * 응답 데이터를 처리하는 메소드들.
     */

    /* 단일 결과 처리 메소드 */
    public <T> SingleResult<T> getSingleResult(T data){
        SingleResult<T> result = new SingleResult<>();
        result.setData(data);
        setSuccessResult(result);
        return result;
    }

    /* 리스트 결과 처리 메소드 */
    public <T> ListResult<T> getListResult(List<T> list){
        ListResult<T> result = new ListResult<>();
        result.setList(list);
        setSuccessResult(result);
        return result;
    }



    /**
     * 응답 성공/실패 여부를 처리하여 세팅하는 메소드들
     */

    /* api 요청 성공 시 성공여부 세팅 */
    private void setSuccessResult(CommonResult result){
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    /* api 요청 실패 시 성공여부 세팅 */
    private void setFailResult(CommonResult result){
        result.setSuccess(false);
        result.setCode(CommonResponse.FAIL.getCode());
        result.setMsg(CommonResponse.FAIL.getMsg());
    }



    /**
     * 응답 성공/실패 시 결과를 리턴하는 메소드
     */

    public CommonResult getSuccessResult(){
        CommonResult result = new CommonResult();
        setSuccessResult(result);
        return result;
    }

    public CommonResult getFailResult(int code, String msg){
        CommonResult result = new CommonResult();
        setFailResult(result);
        return result;
    }
}
