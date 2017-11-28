package com.ctrip.framework.dal.mysql.test.cases.combined;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ctrip.framework.dal.mysql.test.cases.AbstractTestCase;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationOrder;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationUser;
import com.ctrip.framework.dal.mysql.test.model.Order;
import com.ctrip.framework.dal.mysql.test.model.OrderDetail;
import com.ctrip.framework.dal.mysql.test.service.OrderService;
import com.ctrip.framework.dal.mysql.test.service.UniqueKeyGenerator;
import com.ctrip.framework.dal.mysql.test.service.UserService;
import com.google.common.collect.Lists;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Component
public class WholeProcessTestCase extends AbstractTestCase {

  @Autowired
  private OrderService orderService;

  @Autowired
  private UserService userService;

  @Override
  public void setUp() {

  }

  @Override
  public void tearDown() {

  }

  @Override
  public void execute() {
    String someUserName = UniqueKeyGenerator.generate();
    String anotherUserName = UniqueKeyGenerator.generate();
    String yetAnotherUserName = UniqueKeyGenerator.generate();

    // 1. Register
    IntegrationUser someUser = userService.register(someUserName);
    IntegrationUser anotherUser = userService.register(anotherUserName);

    check(someUser.getUserId() > 0, String.format("Invalid user id: %d", someUser.getUserId()));
    check(someUser.getUserName().equals(someUserName), String.format(
        "User name after insert doesn't match, expected: %s, actual: %s", someUserName, someUser.getUserName()));
    check(anotherUser.getUserId() > 0, String.format("Invalid user id: %d", anotherUser.getUserId()));
    check(anotherUser.getUserName().equals(anotherUserName), String.format(
        "User name after insert doesn't match, expected: %s, actual: %s", anotherUserName, anotherUser.getUserName()));

    // 2. Rename
    IntegrationUser someUserWithNewName = userService.rename(someUser.getUserId(), yetAnotherUserName);

    check(someUser.getUserId().equals(someUserWithNewName.getUserId()), String.format(
        "User id doesn't match after update! old: %d, new: %d", someUser.getUserId(), someUserWithNewName.getUserId()));
    check(someUserWithNewName.getUserName().equals(yetAnotherUserName),
        String.format("Update userId: %d to new user name: %s failed!", someUser.getUserId(), yetAnotherUserName));

    // 3. Place Order
    List<Order> orders = Lists.newArrayList();
    orders.add(assembleOrder(someUser.getUserId()));
    orders.add(assembleOrder(someUser.getUserId()));
    orders.add(assembleOrder(anotherUser.getUserId()));
    orders.add(assembleOrder(anotherUser.getUserId()));

    List<IntegrationOrder> insertedOrders = orderService.placeOrder(orders);

    check(insertedOrders.size() == orders.size(),
        String.format("Incorrect size of orders: %d, expect: %d", insertedOrders.size(), orders.size()));

    // 4. Clear
    for (IntegrationOrder o : insertedOrders) {
      orderService.deleteByOrderId(o.getOrderId());
      check(orderService.queryByOrderId(o.getOrderId()) == null,
          String.format("Delete order failed: %d", o.getOrderId()));
    }

    List<Long> insertedUserIds = Lists.newArrayList();
    insertedUserIds.add(someUser.getUserId());
    insertedUserIds.add(anotherUser.getUserId());

    userService.deleteByUserIdList(insertedUserIds);

    check(userService.queryByUserIdList(insertedUserIds).isEmpty(),
        String.format("Delete user failed: %s", insertedUserIds));

  }

  private Order assembleOrder(long userId) {
    Order order = new Order(userId);

    order.addOrderDetail(new OrderDetail("someItemForWholeProcess", 1));
    order.addOrderDetail(new OrderDetail("anotherItemForWholeProcess", 2));

    return order;
  }

  @Override
  public String description() {
    return "Whole process test";
  }
}
