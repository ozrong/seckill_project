package com.ozr.boot.error;

/**
 * @Author OZR
 * @Date 2021/6/28 13:15
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);

}
