package com.ctrip.framework.dal.mysql.test.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class Order {
  private final long userId;
  private final List<OrderDetail> orderDetails;

  public Order(long userId) {
    this.userId = userId;
    this.orderDetails = Lists.newArrayList();
  }

  public void addOrderDetail(OrderDetail orderDetail) {
    this.orderDetails.add(orderDetail);
  }

  public long getUserId() {
    return userId;
  }

  public List<OrderDetail> getOrderDetails() {
    return orderDetails;
  }
}
