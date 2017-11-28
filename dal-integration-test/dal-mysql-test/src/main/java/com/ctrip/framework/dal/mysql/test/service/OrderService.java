package com.ctrip.framework.dal.mysql.test.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ctrip.framework.dal.mysql.test.dao.IntegrationOrderDao;
import com.ctrip.framework.dal.mysql.test.dao.IntegrationOrderDetailDao;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationOrder;
import com.ctrip.framework.dal.mysql.test.entity.IntegrationOrderDetail;
import com.ctrip.framework.dal.mysql.test.model.Order;
import com.ctrip.framework.dal.mysql.test.model.OrderDetail;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class OrderService {

  @Autowired
  private IdGenerator idGenerator;

  @Autowired
  private IntegrationOrderDao orderDao;

  @Autowired
  private IntegrationOrderDetailDao orderDetailDao;

  @Autowired
  @Qualifier("dalServiceDBClient")
  private DalClient dalServiceDBClient;

  private DalHints inAllShardsDalHints;

  public OrderService() {
    inAllShardsDalHints = new DalHints();
    inAllShardsDalHints.inAllShards();
  }

  public IntegrationOrder placeOrder(Order order) {
    final IntegrationOrder integrationOrder = assembleOrder(order);
    final List<IntegrationOrderDetail> integrationOrderDetails =
        assembleOrderDetails(integrationOrder.getOrderId(), order);

    try {
      dalServiceDBClient.execute(new DalCommand() {
        @Override
        public boolean execute(DalClient client) throws SQLException {

          orderDao.insert(integrationOrder);
          orderDetailDao.insert(new DalHints().setShardValue(integrationOrder.getUserId()), integrationOrderDetails);

          return true;
        }
      }, new DalHints().setShardValue(integrationOrder.getUserId()));

      return queryByUserIdAndOrderId(integrationOrder.getUserId(), integrationOrder.getOrderId());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<IntegrationOrder> placeOrder(List<Order> orders) {
    final Map<IntegrationOrder, List<IntegrationOrderDetail>> integrationOrders = Maps.newHashMap();
    List<Long> orderIdList = Lists.newArrayList();

    for (Order order : orders) {
      IntegrationOrder o = assembleOrder(order);
      integrationOrders.put(o, assembleOrderDetails(o.getOrderId(), order));
      orderIdList.add(o.getOrderId());
    }

    for (final IntegrationOrder order : integrationOrders.keySet()) {
      try {
        dalServiceDBClient.execute(new DalCommand() {
          @Override
          public boolean execute(DalClient client) throws SQLException {

            orderDao.insert(order);
            orderDetailDao.insert(new DalHints().setShardValue(order.getUserId()), integrationOrders.get(order));

            return true;
          }
        }, new DalHints().setShardValue(order.getUserId()));
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    return queryByOrderIdList(orderIdList);
  }

  public IntegrationOrder queryByOrderId(long orderId) {
    try {
      return orderDao.queryByOrderId(orderId, inAllShardsDalHints);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public IntegrationOrder queryByUserIdAndOrderId(long userId, long orderId) {
    try {
      return orderDao.queryByOrderId(orderId, new DalHints().setShardValue(userId));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<IntegrationOrder> queryByOrderIdList(List<Long> orderIdList) {
    // TODO Currently query among multiple tables are not supported
    List<IntegrationOrder> orders = Lists.newArrayList();
    for (Long orderId : orderIdList) {
      orders.add(queryByOrderId(orderId));
    }

    return orders;
  }

  public List<IntegrationOrderDetail> queryOrderDetailByOrderId(long orderId) {
    try {
      return orderDetailDao.queryByOrderId(orderId, inAllShardsDalHints);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<IntegrationOrderDetail> queryOrderDetailByOrderIdList(List<Long> orderIdList) {
    // TODO Currently query among multiple tables are not supported
    List<IntegrationOrderDetail> orderDetails = Lists.newArrayList();
    for (Long orderId : orderIdList) {
      orderDetails.addAll(queryOrderDetailByOrderId(orderId));
    }

    return orderDetails;
  }

  public List<IntegrationOrder> queryByUserId(long userId) {
    List<IntegrationOrder> orders = Lists.newArrayList();
    try {
      //FIXME currently query among multiple tables are not supported
      orders.addAll(orderDao.queryByUserId(userId, new DalHints().setTableShardValue(1)));
      orders.addAll(orderDao.queryByUserId(userId, new DalHints().setTableShardValue(2)));

      return orders;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public int deleteByOrderId(long orderId) {
    try {
      int deleted = orderDao.delete(queryByOrderId(orderId));

      orderDetailDao.deleteByOrderId(orderId, inAllShardsDalHints);

      return deleted;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public int[] deleteByOrderIdList(List<Long> orderIdList) {
    try {
      int[] deleted = orderDao.delete(queryByOrderIdList(orderIdList));

      for (Long orderId : orderIdList) {
        deleteByOrderId(orderId);
      }

      return deleted;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private IntegrationOrder assembleOrder(Order order) {
    IntegrationOrder o = new IntegrationOrder();
    o.setUserId(order.getUserId());
    o.setOrderId(idGenerator.nextOrderId());

    return o;
  }

  private List<IntegrationOrderDetail> assembleOrderDetails(long orderId, Order order) {
    List<IntegrationOrderDetail> orderDetails = Lists.newArrayList();
    for (OrderDetail orderDetail : order.getOrderDetails()) {
      IntegrationOrderDetail od = new IntegrationOrderDetail();
      od.setOrderId(orderId);
      od.setItem(orderDetail.getItem());
      od.setQuantity(orderDetail.getQuantity());
      orderDetails.add(od);
    }

    return orderDetails;
  }
}
