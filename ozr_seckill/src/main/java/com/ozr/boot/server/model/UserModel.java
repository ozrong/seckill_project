package com.ozr.boot.server.model;



import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @Author OZR
 * @Date 2021/6/28 10:37
 */
public class UserModel  implements Serializable {

    private static final long serialVersionUID = 4646554200794013244L;
    private Integer id;
    @NotBlank(message = "用户名不能为空")//@NotBlank指定注解不能为null 也不能为""

    private String name;
    @NotNull(message = "性别不能不填")// @NotNull 不能为null
    private Integer gender;

    @NotBlank(message = "电话号码不能为空")

    private String telphone;

    @Min(value = 0,message = "年龄必须大于0")
    @Max(value = 150,message = "年龄不能大于150岁")
    @NotNull(message = "年龄不能不填")
    private Integer age;
    private String registerModa;
    private String thirdPartyId;

    //用户其实还有其他的信息
    //eg password （另一个表中）
    @NotBlank(message = "密码不能为空")
    private String encrptPassword;


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

    public String getRegisterModa() {
        return registerModa;
    }

    public void setRegisterModa(String registerModa) {
        this.registerModa = registerModa;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getEncrptPassword() {
        return encrptPassword;
    }

    public void setEncrptPassword(String encrptPassword) {
        this.encrptPassword = encrptPassword;
    }
}
