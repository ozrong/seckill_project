package com.ozr.boot.error;

/**
 * @Author OZR
 * @Date 2021/6/28 13:17
 */
public enum EmBusinessError implements CommonError {
    //10000开通为用户信息相关错误
    USER_NOT_EXIST(10001,"用户不存在"),

    //00000 通用错误类型
    PARAMETER_VALIDATION_ERROR(00001,"参数不合法"),
    UNKNOW_ERROR(00002,"未知错误"),
    LOGIN_ERROR(10002,"手机号或密码错误"),
    STOCK_NOT_ENOUGH(20001,"库存数量不足"),
    USER_NOT_LOGIN(10003,"用户未登录"),
    MQ_SEND_FAAIL(10004,"异步更新库存失败"),
    RATELIMITER(10005,"活动过于火爆，稍后再试")

    ;
    private int errCode;
    private String errMsg;

    private EmBusinessError(int errCode,String errMsg){
        this.errCode=errCode;
        this.errMsg=errMsg;
    }
    @Override
    public int getErrCode() {
        return this.errCode;
    }
    @Override
    public String getErrMsg() {
        return errMsg;
    }
   //修改错误描述
    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg=errMsg;
        return this;
    }
}
