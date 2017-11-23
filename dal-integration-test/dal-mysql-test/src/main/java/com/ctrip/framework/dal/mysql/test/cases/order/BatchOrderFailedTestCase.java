package com.ctrip.framework.dal.mysql.test.cases.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ctrip.framework.dal.mysql.test.cases.AbstractTestCase;
import com.ctrip.framework.dal.mysql.test.model.Order;
import com.ctrip.framework.dal.mysql.test.model.OrderDetail;
import com.ctrip.framework.dal.mysql.test.service.IdGenerator;
import com.ctrip.framework.dal.mysql.test.service.OrderService;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class BatchOrderFailedTestCase extends AbstractTestCase {

  @Autowired
  private IdGenerator idGenerator;

  @Autowired
  private OrderService orderService;

  private List<Order> orders;
  private long someUserId;
  private long anotherUserId;

  @Override
  public void setUp() {
    orders = Lists.newArrayList();

    someUserId = idGenerator.nextUserId();
    anotherUserId = idGenerator.nextUserId();

    orders.add(assembleOrder(someUserId));
    orders.add(assembleOrder(anotherUserId));
  }

  @Override
  public void tearDown() {

  }

  @Override
  public void execute() {
    try {
      orderService.placeOrder(orders);
      check(1 == 0, "Exceptions should be thrown when inserting duplicate item details!");
    } catch (Throwable ex) {
      // ignore
    }

    check(orderService.queryByUserId(someUserId).isEmpty(),
        String.format("No order expected for user: %d", someUserId));
    check(orderService.queryByUserId(anotherUserId).isEmpty(),
        String.format("No order expected for user: %d", anotherUserId));
  }

  @Override
  public String description() {
    return "Use Dal Command to batch inserting orders with duplicated key";
  }

  private Order assembleOrder(long userId) {
    Order order = new Order(userId);

    order.addOrderDetail(new OrderDetail("someItem", 1));
    order.addOrderDetail(new OrderDetail("someItem", 2));

    return order;
  }
}
