package com.ozr.boot.error;

/**
 * @Author OZR
 * @Date 2021/6/28 13:27
 */

//包装器业务异常实现 （设计模式  包装器）
public class BusinessException extends  Exception implements  CommonError {
    private CommonError commonError;
    //直接接收EmBusinessError的传参用于构造业务异常
    public BusinessException(CommonError commonError) {
        super();
        this.commonError=commonError;
    }
    //自定义errMsg构造业务异常
    public BusinessException(CommonError commonError,String errMsg){
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }
    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }
    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }
    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
