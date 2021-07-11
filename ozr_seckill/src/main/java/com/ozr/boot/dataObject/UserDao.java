package com.ozr.boot.dataObject;

public class UserDao {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_user.id
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_user.name
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_user.gender
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    private Integer gender;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_user.telphone
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    private String telphone;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_user.age
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    private Integer age;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_user.register_moda
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    private String registerModa;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_user.third_party_id
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    private String thirdPartyId;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_user.id
     *
     * @return the value of tbl_seckill_user.id
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_user.id
     *
     * @param id the value for tbl_seckill_user.id
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_user.name
     *
     * @return the value of tbl_seckill_user.name
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_user.name
     *
     * @param name the value for tbl_seckill_user.name
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_user.gender
     *
     * @return the value of tbl_seckill_user.gender
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public Integer getGender() {
        return gender;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_user.gender
     *
     * @param gender the value for tbl_seckill_user.gender
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_user.telphone
     *
     * @return the value of tbl_seckill_user.telphone
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public String getTelphone() {
        return telphone;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_user.telphone
     *
     * @param telphone the value for tbl_seckill_user.telphone
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public void setTelphone(String telphone) {
        this.telphone = telphone == null ? null : telphone.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_user.age
     *
     * @return the value of tbl_seckill_user.age
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public Integer getAge() {
        return age;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_user.age
     *
     * @param age the value for tbl_seckill_user.age
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_user.register_moda
     *
     * @return the value of tbl_seckill_user.register_moda
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public String getRegisterModa() {
        return registerModa;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_user.register_moda
     *
     * @param registerModa the value for tbl_seckill_user.register_moda
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public void setRegisterModa(String registerModa) {
        this.registerModa = registerModa == null ? null : registerModa.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_user.third_party_id
     *
     * @return the value of tbl_seckill_user.third_party_id
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public String getThirdPartyId() {
        return thirdPartyId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_user.third_party_id
     *
     * @param thirdPartyId the value for tbl_seckill_user.third_party_id
     *
     * @mbg.generated Mon Jun 28 16:44:27 CST 2021
     */
    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId == null ? null : thirdPartyId.trim();
    }

    @Override
    public String toString() {
        return "UserDao{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", telphone='" + telphone + '\'' +
                ", age=" + age +
                ", registerModa='" + registerModa + '\'' +
                ", thirdPartyId='" + thirdPartyId + '\'' +
                '}';
    }
}