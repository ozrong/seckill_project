package com.ozr.boot.controller.viewObject;

/**
 * @Author OZR
 * @Date 2021/6/28 13:43
 */
public class UserVO {
    private Integer id;
    private String name;
    private Integer gender;
    private String telphone;
    private Integer age;
    /*以下的信息前端是不会要得*/
//    private String registerModa;
//    private String thirdPartyId;
//    private String encrptPassword;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getTelphone() {
        return telphone;
    }

    public void setTelphone(String telphone) {
        this.telphone = telphone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
