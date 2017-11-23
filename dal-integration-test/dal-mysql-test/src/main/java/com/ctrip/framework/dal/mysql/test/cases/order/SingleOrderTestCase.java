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

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class SingleOrderTestCase extends AbstractTestCase {

  @Autowired
  private OrderService orderService;

  @Autowired
  private IdGenerator idGenerator;

  private long userId;

  @Override
  public void setUp() {
    userId = idGenerator.nextUserId();
  }

  @Override
  public void tearDown() {

  }

  @Override
  public void execute() {
    Order order = new Order(userId);
    order.addOrderDetail(new OrderDetail("someItem", 1));
    order.addOrderDetail(new OrderDetail("anotherItem", 2));

    IntegrationOrder result = orderService.placeOrder(order);
    List<IntegrationOrderDetail> orderDetailList = orderService.queryOrderDetailByOrderId(result.getOrderId());

    check(result.getId().longValue() > 0, String.format("Invalid generated id: %d", result.getId()));
    check(result.getOrderId() > 0, String.format("Invalid order id: %d", result.getOrderId()));
    check(order.getOrderDetails().size() == orderDetailList.size(), String.format(
        "Incorrect size of order details: %d, expect: %d", orderDetailList.size(), order.getOrderDetails().size()));

    int deleted = orderService.deleteByOrderId(result.getOrderId());

    check(deleted == 1, String.format("Delete order by orderId: %d failed.", result.getOrderId()));

    List<IntegrationOrderDetail> orderDetailListAfterDelete =
        orderService.queryOrderDetailByOrderId(result.getOrderId());

    check(orderDetailListAfterDelete.isEmpty(),
        String.format("Order details for order: %d are not deleted", result.getOrderId()));

  }

  @Override
  public String description() {
    return "Single order";
  }
}
