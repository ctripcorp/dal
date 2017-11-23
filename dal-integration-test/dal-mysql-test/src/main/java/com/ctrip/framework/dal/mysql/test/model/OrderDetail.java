package com.ctrip.framework.dal.mysql.test.model;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class OrderDetail {

  private final String item;
  private final int quantity;

  public OrderDetail(String item, int quantity) {
    this.item = item;
    this.quantity = quantity;
  }

  public String getItem() {
    return item;
  }

  public int getQuantity() {
    return quantity;
  }
}
