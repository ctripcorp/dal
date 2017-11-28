package com.ctrip.framework.dal.mysql.test.cases.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ctrip.framework.dal.mysql.test.cases.AbstractTestCase;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationOrder;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationOrderDetail;
import com.ctrip.framework.dal.mysql.test.model.Order;
import com.ctrip.framework.dal.mysql.test.model.OrderDetail;
import com.ctrip.framework.dal.mysql.test.service.IdGenerator;
import com.ctrip.framework.dal.mysql.test.service.OrderService;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class BatchOrderTestCase extends AbstractTestCase {
  @Autowired
  private OrderService orderService;

  @Autowired
  private IdGenerator idGenerator;

  private List<Order> orders;

  @Override
  public void setUp() {
    orders = Lists.newArrayList();

    long someUserId = idGenerator.nextUserId();
    long anotherUserId = idGenerator.nextUserId();

    orders.add(assembleOrder(someUserId));
    orders.add(assembleOrder(anotherUserId));
    orders.add(assembleOrder(anotherUserId));
  }

  @Override
  public void tearDown() {

  }

  @Override
  public void execute() {
    List<IntegrationOrder> results = orderService.placeOrder(orders);

    check(results.size() == orders.size(),
        String.format("Incorrect size of orders: %d, expect: %d", results.size(), orders.size()));

    List<Long> orderIds = Lists.newArrayList();

    for (IntegrationOrder o : results) {
      orderIds.add(o.getOrderId());
    }

    int orderDetailCount = 0;

    for (Order order : orders) {
      orderDetailCount += order.getOrderDetails().size();
    }

    List<IntegrationOrderDetail> orderDetails = orderService.queryOrderDetailByOrderIdList(orderIds);
    check(orderDetailCount == orderDetails.size(),
        String.format("Incorrect size of order details: %d, expect: %d", orderDetails.size(), orderDetailCount));

    int[] deleted = orderService.deleteByOrderIdList(orderIds);

    check(deleted.length == orders.size(), String.format("Delete order failed: %s", orderIds));

    for (int i = 0; i < deleted.length; i++) {
      check(deleted[i] == 1, String.format("Delete for index %d failed", i));
    }

    List<IntegrationOrderDetail> orderDetailsAfterDelete = orderService.queryOrderDetailByOrderIdList(orderIds);

    check(orderDetailsAfterDelete.isEmpty(), String.format("Order details for orders: %s are not deleted", orderIds));
  }

  @Override
  public String description() {
    return "Batch order";
  }

  private Order assembleOrder(long userId) {
    Order order = new Order(userId);

    order.addOrderDetail(new OrderDetail("someItem", 1));
    order.addOrderDetail(new OrderDetail("anotherItem", 2));

    return order;
  }
}
