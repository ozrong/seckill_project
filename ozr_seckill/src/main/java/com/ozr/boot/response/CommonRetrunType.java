package com.ozr.boot.response;

/**
 * @Author OZR
 * @Date 2021/6/28 11:23
 */
public class CommonRetrunType {
    //我们规定的返回状态 规定是:success 或者fail
    private String status;
    //返回的数据
    private Object data;


    //状态是success 则data为前端要的json数据

    //状态是fail 则data为通用的错误码格式


    /***
     * 直接传入result，就默认是处理成功了
     * 否者调用creat(Object result, String status)
     *
     * @param result
     * @return
     */

    //定义一个通用的创建方式
    public static CommonRetrunType creat(Object result){
        return  CommonRetrunType.creat(result,"success");
    }

    public static CommonRetrunType creat(Object result, String status){
        CommonRetrunType type = new CommonRetrunType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
