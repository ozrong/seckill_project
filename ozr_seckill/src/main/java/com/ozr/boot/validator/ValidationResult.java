package com.ozr.boot.validator;

import org.apache.commons.lang3.StringUtils;


import java.util.HashMap;
import java.util.Map;

/**
 * @Author OZR
 * @Date 2021/6/29 13:38
 */
public class ValidationResult {
    //校验结果是否有错
    private boolean  hasErrors = false;

    //存放错误信息的map
    private Map<String,String> errorMsgMap = new HashMap<>();


    //实现通用的通过格式化字符串信息过去错误结果的msg方法

    public String getErrMsg(){
        return StringUtils.join(this.errorMsgMap.values().toArray(),"," );
    }
    public boolean isHasErrors() {
        return hasErrors;
    }
    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }
}
