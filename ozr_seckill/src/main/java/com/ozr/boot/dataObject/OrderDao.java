package com.ozr.boot.dataObject;

public class OrderDao {
    //活动的id
    private  Integer promoId;

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_order.id
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_order.user_id
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    private Integer userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_order.item_id
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    private Integer itemId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_order.item_amount
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    private Integer itemAmount;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_order.item_price
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    private Double itemPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tbl_seckill_order.order_price
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    private Double orderPrice;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_order.id
     *
     * @return the value of tbl_seckill_order.id
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_order.id
     *
     * @param id the value for tbl_seckill_order.id
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_order.user_id
     *
     * @return the value of tbl_seckill_order.user_id
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_order.user_id
     *
     * @param userId the value for tbl_seckill_order.user_id
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_order.item_id
     *
     * @return the value of tbl_seckill_order.item_id
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_order.item_id
     *
     * @param itemId the value for tbl_seckill_order.item_id
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_order.item_amount
     *
     * @return the value of tbl_seckill_order.item_amount
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public Integer getItemAmount() {
        return itemAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_order.item_amount
     *
     * @param itemAmount the value for tbl_seckill_order.item_amount
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public void setItemAmount(Integer itemAmount) {
        this.itemAmount = itemAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_order.item_price
     *
     * @return the value of tbl_seckill_order.item_price
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public Double getItemPrice() {
        return itemPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_order.item_price
     *
     * @param itemPrice the value for tbl_seckill_order.item_price
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tbl_seckill_order.order_price
     *
     * @return the value of tbl_seckill_order.order_price
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public Double getOrderPrice() {
        return orderPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tbl_seckill_order.order_price
     *
     * @param orderPrice the value for tbl_seckill_order.order_price
     *
     * @mbg.generated Wed Jun 30 13:36:24 CST 2021
     */
    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }
}