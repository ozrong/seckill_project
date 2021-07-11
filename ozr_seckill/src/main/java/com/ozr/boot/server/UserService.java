package com.ozr.boot.server;

import com.ozr.boot.error.BusinessException;
import com.ozr.boot.server.model.UserModel;

/**
 * @Author OZR
 * @Date 2021/6/28 10:29
 */
public interface UserService {

    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;

    /**
     *
     * @param telphone 手机号
     * @param encrptPassword 用户密码（加密后的）
     * @throws BusinessException
     * @return
     */
    UserModel validataLogin(String telphone, String encrptPassword) throws BusinessException;
    UserModel getUserByIdFromCache(Integer id);
}
