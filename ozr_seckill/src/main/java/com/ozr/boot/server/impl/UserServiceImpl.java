package com.ozr.boot.server.impl;

import com.ozr.boot.dao.UserDaoMapper;
import com.ozr.boot.dao.UserPasswordDaoMapper;
import com.ozr.boot.dataObject.UserDao;
import com.ozr.boot.dataObject.UserPasswordDao;
import com.ozr.boot.error.BusinessException;
import com.ozr.boot.error.EmBusinessError;
import com.ozr.boot.server.UserService;
import com.ozr.boot.server.model.UserModel;
import com.ozr.boot.validator.ValidationResult;
import com.ozr.boot.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;


/**
 * @Author OZR
 * @Date 2021/6/28 10:29
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDaoMapper userDaoMapper;

    @Autowired
    UserPasswordDaoMapper userPasswordDaoMapper;


    @Autowired
    ValidatorImpl validator;


    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 为什么不能直接的返回UserDao呢？？
     * 因为在实际开发中，这个UserDao和数据库是一一对应的，是绝不能直接给前端的
     * 在service层必须的有一个model的概念
     * 在model里面定义数据库和请求的逻辑交互
     * dao只是和和数据表进行映射，
     * 真正的一个user对象的信息 可能在多个表里面，所以使用model层来的得到真正的user对象
     *
     *
     * @param id
     */
    @Override
    public UserModel getUserById(Integer id) {
        UserDao userDao = userDaoMapper.selectByPrimaryKey(id);
        if(userDao == null){
            return null;
        }
        /*在userPasswordDaoMapper中只有通过数据表的key来查询这个UserPasswordDao
        * 添加一个方法使用userId来查userPasswordDao
        * */

        //通过用户id得到对应的加密密码；
        UserPasswordDao userPasswordDao = userPasswordDaoMapper.selectByUserId(userDao.getId());
        UserModel userModel = convertModelFromDataObject(userDao, userPasswordDao);
        return userModel;
    }

    @Override
    @Transactional //事物
    public void register(UserModel userModel) throws BusinessException {
        if(userModel==null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userModel.getName())
//                || userModel.getGender()==null
//                || userModel.getAge() ==null
//                || StringUtils.isEmpty(userModel.getTelphone())){
//            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult validationResult = validator.validate(userModel);
        if(validationResult.isHasErrors()){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validationResult.getErrMsg());
        }

        //实现从model到POJO(dataObjec)方法
        UserDao userDao = convertFromModel(userModel);
        try{
            userDaoMapper.insertSelective(userDao);
        }catch(DuplicateKeyException e){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号重复");
        }

        //insertSelective 让一些为null的字段不插入，就使用数据库默认值，
        //而且在数据库中尽量不要存入null这种值

        //insertSelective对应的sql可以拿到只增的主键（id） 拿到主键会自动的赋值给userDao
        //我们需要把这个主键赋值userModel中的id
        // 然后才从给userModel取出id赋值给userPasswordDao中的user_id
        userModel.setId(userDao.getId());


        UserPasswordDao userPasswordDao = convertPasswordFromModel(userModel);
        userPasswordDaoMapper.insertSelective(userPasswordDao);
        return;
    }

    @Override
    public UserModel validataLogin(String telphone, String encrptPassword) throws BusinessException {
        //通过用户手机获取用户信息
        UserDao userDao = userDaoMapper.selectByTelphone(telphone);
        if(userDao == null){
            throw  new BusinessException(EmBusinessError.LOGIN_ERROR);
        }
        UserPasswordDao userPasswordDao = userPasswordDaoMapper.selectByUserId(userDao.getId());
        UserModel userModel = convertModelFromDataObject(userDao, userPasswordDao);

        //对比密码是否匹配
        if(!StringUtils.equals(encrptPassword,userModel.getEncrptPassword())){
            throw new BusinessException(EmBusinessError.LOGIN_ERROR);
        }

        return userModel;



    }

    @Override
    public UserModel getUserByIdFromCache(Integer id) {
        UserModel userModel   = (UserModel)redisTemplate.opsForValue().get("user_validate_"+id);
        if(userModel == null){
            userModel = this.getUserById(id);
            redisTemplate.opsForValue().set("user_validate_"+id,userModel);
            redisTemplate.expire("user_validate_"+id,10, TimeUnit.MINUTES);
        }
        return userModel;
    }


    //pojo->model
    public UserModel convertModelFromDataObject(UserDao userDao, UserPasswordDao userPasswordDao){
        if(userDao == null) return null;
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDao,userModel);
        /**
         * BeanUtils.copyProperties(userDao,userModel)
         * 将userDao的属性 复制到userModel对应的属性中
         */
        if(userPasswordDao != null){
            userModel.setEncrptPassword(userPasswordDao.getEncrptPassword());
        }
        return userModel;
    }

    //model->pojo
    private UserDao convertFromModel(UserModel userModel){
        if(userModel ==null){
            return null;
        }
        UserDao userDao = new UserDao();
        BeanUtils.copyProperties(userModel,userDao);
        return userDao;
    }

    private UserPasswordDao convertPasswordFromModel(UserModel userModel){
        if(userModel ==null) return null;
        UserPasswordDao userPasswordDao = new UserPasswordDao();
        userPasswordDao.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDao.setUserId(userModel.getId());
        return userPasswordDao;
    }
}
