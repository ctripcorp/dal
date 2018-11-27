package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name = "TVP")
@Table(name = "P_TicketNo")
public class PTicketNo implements DalPojo {

    /**
     * 机票号（票号）
     */
    @Id
    @Column(name = "TicketNo")
    @Type(value = Types.CHAR)
    private String ticketNo;

    /**
     * 机票类型：BSP-BSP票，BIL-本票，EXT-外开票
     */
    @Column(name = "TicketType")
    @Type(value = Types.CHAR)
    private String ticketType;

    /**
     * 航空公司三字码
     */
    @Column(name = "AirLineCode")
    @Type(value = Types.CHAR)
    private String airLineCode;

    /**
     * N-国内，I-国际
     */
    @Column(name = "Flight_Intl")
    @Type(value = Types.CHAR)
    private String flightIntl;

    /**
     * 票台
     */
    @Id
    @Column(name = "FlightAgency")
    @Type(value = Types.INTEGER)
    private Integer flightAgency;

    /**
     * 票点
     */
    @Column(name = "SendSite")
    @Type(value = Types.INTEGER)
    private Integer sendSite;

    /**
     * 订单ID
     */
    @Column(name = "OrderID")
    @Type(value = Types.BIGINT)
    private Long orderID;

    /**
     * 乘客姓名
     */
    @Column(name = "Passenger")
    @Type(value = Types.VARCHAR)
    private String passenger;

    /**
     * 该机票第1程
     */
    @Column(name = "Flight1")
    @Type(value = Types.SMALLINT)
    private Short flight1;

    /**
     * 该机票第2程
     */
    @Column(name = "Flight2")
    @Type(value = Types.SMALLINT)
    private Short flight2;

    /**
     * 该机票第3程
     */
    @Column(name = "Flight3")
    @Type(value = Types.SMALLINT)
    private Short flight3;

    /**
     * 该机票第4程
     */
    @Column(name = "Flight4")
    @Type(value = Types.SMALLINT)
    private Short flight4;

    /**
     * 票面价
     */
    @Column(name = "PrintPrice")
    @Type(value = Types.DECIMAL)
    private BigDecimal printPrice;

    /**
     * 卖价
     */
    @Column(name = "Amount")
    @Type(value = Types.DECIMAL)
    private BigDecimal amount;

    /**
     * 底价
     */
    @Column(name = "Cost")
    @Type(value = Types.DECIMAL)
    private BigDecimal cost;

    /**
     * 税费
     */
    @Column(name = "Tax")
    @Type(value = Types.DECIMAL)
    private BigDecimal tax;

    /**
     * 记录编号
     */
    @Column(name = "RecordNo")
    @Type(value = Types.CHAR)
    private String recordNo;

    /**
     * 状态：COM-出票，RET-退票，PAR-部分退票，INV-废票，STO –入库未领用，BLA-未使用
     */
    @Column(name = "Status")
    @Type(value = Types.CHAR)
    private String status;

    /**
     * 支付状态，0-未支付，1-部分支付，2-全部支付
     */
    @Column(name = "PaidStatus")
    @Type(value = Types.SMALLINT)
    private Short paidStatus;

    /**
     * 出票或废票操作人
     */
    @Column(name = "Operator")
    @Type(value = Types.VARCHAR)
    private String operator;

    /**
     * 出票或废票操作时间
     */
    @Column(name = "OperateTime")
    @Type(value = Types.TIMESTAMP)
    private Timestamp operateTime;

    /**
     * 该机票第1程是否退票
     */
    @Column(name = "ReturnFlight1")
    @Type(value = Types.CHAR)
    private String returnFlight1;

    /**
     * 该机票第2程是否退票
     */
    @Column(name = "ReturnFlight2")
    @Type(value = Types.CHAR)
    private String returnFlight2;

    /**
     * 该机票第3程是否退票
     */
    @Column(name = "ReturnFlight3")
    @Type(value = Types.CHAR)
    private String returnFlight3;

    /**
     * 该机票第4程是否退票
     */
    @Column(name = "ReturnFlight4")
    @Type(value = Types.CHAR)
    private String returnFlight4;

    /**
     * 退票录入人
     */
    @Column(name = "ROperator")
    @Type(value = Types.VARCHAR)
    private String rOperator;

    /**
     * 退票录入时间
     */
    @Column(name = "ROperateTime")
    @Type(value = Types.TIMESTAMP)
    private Timestamp rOperateTime;

    /**
     * 是否审核
     */
    @Column(name = "BSPChecked")
    @Type(value = Types.CHAR)
    private String bSPChecked;

    /**
     * NULL
     */
    @Column(name = "AirLineChecked")
    @Type(value = Types.CHAR)
    private String airLineChecked;

    /**
     * 退票RID
     */
    @Column(name = "RefundID")
    @Type(value = Types.INTEGER)
    private Integer refundID;

    /**
     * 审核时间
     */
    @Column(name = "BSPCheckedTime")
    @Type(value = Types.TIMESTAMP)
    private Timestamp bSPCheckedTime;

    /**
     * 审核人
     */
    @Column(name = "BSPCheckedEID")
    @Type(value = Types.VARCHAR)
    private String bSPCheckedEID;

    /**
     * 外航销售价(国际机票)
     */
    @Column(name = "FAmount")
    @Type(value = Types.DECIMAL)
    private BigDecimal fAmount;

    /**
     * 航空公司2字码
     */
    @Id
    @Column(name = "AirLine")
    @Type(value = Types.CHAR)
    private String airLine;

    /**
     * 是否BSPET（T 电子票
     */
    @Column(name = "BSPET")
    @Type(value = Types.CHAR)
    private String bSPET;

    /**
     * 是否邮寄
     */
    @Column(name = "IsPost")
    @Type(value = Types.CHAR)
    private String isPost;

    /**
     * 油费
     */
    @Column(name = "OilFee")
    @Type(value = Types.DECIMAL)
    private BigDecimal oilFee;

    /**
     * 付款申请单编号
     */
    @Column(name = "BillID")
    @Type(value = Types.INTEGER)
    private Integer billID;

    /**
     * 扣率
     */
    @Column(name = "costrate")
    @Type(value = Types.DECIMAL)
    private BigDecimal costrate;

    /**
     * 是否挂起
     */
    @Column(name = "isHandUp")
    @Type(value = Types.CHAR)
    private String isHandUp;

    /**
     * 是否电子票(国际的本票 T 电子票)
     */
    @Column(name = "ETicket")
    @Type(value = Types.CHAR)
    private String eTicket;

    /**
     * 改期升舱主表ID
     */
    @Column(name = "RbkID")
    @Type(value = Types.BIGINT)
    private Long rbkID;

    /**
     * 更改费
     */
    @Column(name = "ChangeFee")
    @Type(value = Types.DECIMAL)
    private BigDecimal changeFee;

    /**
     * 度假底价
     */
    @Column(name = "VacationCost")
    @Type(value = Types.DECIMAL)
    private BigDecimal vacationCost;

    /**
     * 是否BOP票，T为是
     */
    @Column(name = "IsBOP")
    @Type(value = Types.CHAR)
    private String isBOP;

    /**
     * 支付流水号
     */
    @Column(name = "PaymentNo")
    @Type(value = Types.VARCHAR)
    private String paymentNo;

    /**
     * 最后修改时间
     */
    @Column(name = "DataChange_LastTime")
    @Type(value = Types.TIMESTAMP)
    private Timestamp datachangeLasttime;

    /**
     * 国际改签服务费(收客人的)
     */
    @Column(name = "RebookingServerFee")
    @Type(value = Types.DECIMAL)
    private BigDecimal rebookingServerFee;

    /**
     * 改签手续费(付供应商)
     */
    @Column(name = "SettlementFee")
    @Type(value = Types.DECIMAL)
    private BigDecimal settlementFee;

    /**
     * 是否自动审核
     */
    @Column(name = "IsAutoAudit")
    @Type(value = Types.CHAR)
    private String isAutoAudit;

    /**
     * 出票单号
     */
    @Column(name = "IssueBillID")
    @Type(value = Types.BIGINT)
    private Long issueBillID;

    /**
     * 回填税费
     */
    @Column(name = "AgencyTicketTax")
    @Type(value = Types.DECIMAL)
    private BigDecimal agencyTicketTax;

    /**
     * 外币票面价
     */
    @Column(name = "ForeignCurrencyPrintPrice")
    @Type(value = Types.DECIMAL)
    private BigDecimal foreignCurrencyPrintPrice;

    /**
     * 外币销售价
     */
    @Column(name = "ForeignCurrencyAmount")
    @Type(value = Types.DECIMAL)
    private BigDecimal foreignCurrencyAmount;

    /**
     * 外币底价
     */
    @Column(name = "ForeignCurrencyCost")
    @Type(value = Types.DECIMAL)
    private BigDecimal foreignCurrencyCost;

    /**
     * 外币税
     */
    @Column(name = "ForeignCurrencyTax")
    @Type(value = Types.DECIMAL)
    private BigDecimal foreignCurrencyTax;

    /**
     * 外币燃油
     */
    @Column(name = "ForeignCurrencyOilFee")
    @Type(value = Types.DECIMAL)
    private BigDecimal foreignCurrencyOilFee;

    /**
     * 外币改期费
     */
    @Column(name = "ForeignCurrencyChangeFee")
    @Type(value = Types.DECIMAL)
    private BigDecimal foreignCurrencyChangeFee;

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getAirLineCode() {
        return airLineCode;
    }

    public void setAirLineCode(String airLineCode) {
        this.airLineCode = airLineCode;
    }

    public String getFlightIntl() {
        return flightIntl;
    }

    public void setFlightIntl(String flightIntl) {
        this.flightIntl = flightIntl;
    }

    public Integer getFlightAgency() {
        return flightAgency;
    }

    public void setFlightAgency(Integer flightAgency) {
        this.flightAgency = flightAgency;
    }

    public Integer getSendSite() {
        return sendSite;
    }

    public void setSendSite(Integer sendSite) {
        this.sendSite = sendSite;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public Short getFlight1() {
        return flight1;
    }

    public void setFlight1(Short flight1) {
        this.flight1 = flight1;
    }

    public Short getFlight2() {
        return flight2;
    }

    public void setFlight2(Short flight2) {
        this.flight2 = flight2;
    }

    public Short getFlight3() {
        return flight3;
    }

    public void setFlight3(Short flight3) {
        this.flight3 = flight3;
    }

    public Short getFlight4() {
        return flight4;
    }

    public void setFlight4(Short flight4) {
        this.flight4 = flight4;
    }

    public BigDecimal getPrintPrice() {
        return printPrice;
    }

    public void setPrintPrice(BigDecimal printPrice) {
        this.printPrice = printPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public String getRecordNo() {
        return recordNo;
    }

    public void setRecordNo(String recordNo) {
        this.recordNo = recordNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Short getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(Short paidStatus) {
        this.paidStatus = paidStatus;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Timestamp getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Timestamp operateTime) {
        this.operateTime = operateTime;
    }

    public String getReturnFlight1() {
        return returnFlight1;
    }

    public void setReturnFlight1(String returnFlight1) {
        this.returnFlight1 = returnFlight1;
    }

    public String getReturnFlight2() {
        return returnFlight2;
    }

    public void setReturnFlight2(String returnFlight2) {
        this.returnFlight2 = returnFlight2;
    }

    public String getReturnFlight3() {
        return returnFlight3;
    }

    public void setReturnFlight3(String returnFlight3) {
        this.returnFlight3 = returnFlight3;
    }

    public String getReturnFlight4() {
        return returnFlight4;
    }

    public void setReturnFlight4(String returnFlight4) {
        this.returnFlight4 = returnFlight4;
    }

    public String getROperator() {
        return rOperator;
    }

    public void setROperator(String rOperator) {
        this.rOperator = rOperator;
    }

    public Timestamp getROperateTime() {
        return rOperateTime;
    }

    public void setROperateTime(Timestamp rOperateTime) {
        this.rOperateTime = rOperateTime;
    }

    public String getBSPChecked() {
        return bSPChecked;
    }

    public void setBSPChecked(String bSPChecked) {
        this.bSPChecked = bSPChecked;
    }

    public String getAirLineChecked() {
        return airLineChecked;
    }

    public void setAirLineChecked(String airLineChecked) {
        this.airLineChecked = airLineChecked;
    }

    public Integer getRefundID() {
        return refundID;
    }

    public void setRefundID(Integer refundID) {
        this.refundID = refundID;
    }

    public Timestamp getBSPCheckedTime() {
        return bSPCheckedTime;
    }

    public void setBSPCheckedTime(Timestamp bSPCheckedTime) {
        this.bSPCheckedTime = bSPCheckedTime;
    }

    public String getBSPCheckedEID() {
        return bSPCheckedEID;
    }

    public void setBSPCheckedEID(String bSPCheckedEID) {
        this.bSPCheckedEID = bSPCheckedEID;
    }

    public BigDecimal getFAmount() {
        return fAmount;
    }

    public void setFAmount(BigDecimal fAmount) {
        this.fAmount = fAmount;
    }

    public String getAirLine() {
        return airLine;
    }

    public void setAirLine(String airLine) {
        this.airLine = airLine;
    }

    public String getBSPET() {
        return bSPET;
    }

    public void setBSPET(String bSPET) {
        this.bSPET = bSPET;
    }

    public String getIsPost() {
        return isPost;
    }

    public void setIsPost(String isPost) {
        this.isPost = isPost;
    }

    public BigDecimal getOilFee() {
        return oilFee;
    }

    public void setOilFee(BigDecimal oilFee) {
        this.oilFee = oilFee;
    }

    public Integer getBillID() {
        return billID;
    }

    public void setBillID(Integer billID) {
        this.billID = billID;
    }

    public BigDecimal getCostrate() {
        return costrate;
    }

    public void setCostrate(BigDecimal costrate) {
        this.costrate = costrate;
    }

    public String getIsHandUp() {
        return isHandUp;
    }

    public void setIsHandUp(String isHandUp) {
        this.isHandUp = isHandUp;
    }

    public String getETicket() {
        return eTicket;
    }

    public void setETicket(String eTicket) {
        this.eTicket = eTicket;
    }

    public Long getRbkID() {
        return rbkID;
    }

    public void setRbkID(Long rbkID) {
        this.rbkID = rbkID;
    }

    public BigDecimal getChangeFee() {
        return changeFee;
    }

    public void setChangeFee(BigDecimal changeFee) {
        this.changeFee = changeFee;
    }

    public BigDecimal getVacationCost() {
        return vacationCost;
    }

    public void setVacationCost(BigDecimal vacationCost) {
        this.vacationCost = vacationCost;
    }

    public String getIsBOP() {
        return isBOP;
    }

    public void setIsBOP(String isBOP) {
        this.isBOP = isBOP;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public Timestamp getDatachangeLasttime() {
        return datachangeLasttime;
    }

    public void setDatachangeLasttime(Timestamp datachangeLasttime) {
        this.datachangeLasttime = datachangeLasttime;
    }

    public BigDecimal getRebookingServerFee() {
        return rebookingServerFee;
    }

    public void setRebookingServerFee(BigDecimal rebookingServerFee) {
        this.rebookingServerFee = rebookingServerFee;
    }

    public BigDecimal getSettlementFee() {
        return settlementFee;
    }

    public void setSettlementFee(BigDecimal settlementFee) {
        this.settlementFee = settlementFee;
    }

    public String getIsAutoAudit() {
        return isAutoAudit;
    }

    public void setIsAutoAudit(String isAutoAudit) {
        this.isAutoAudit = isAutoAudit;
    }

    public Long getIssueBillID() {
        return issueBillID;
    }

    public void setIssueBillID(Long issueBillID) {
        this.issueBillID = issueBillID;
    }

    public BigDecimal getAgencyTicketTax() {
        return agencyTicketTax;
    }

    public void setAgencyTicketTax(BigDecimal agencyTicketTax) {
        this.agencyTicketTax = agencyTicketTax;
    }

    public BigDecimal getForeignCurrencyPrintPrice() {
        return foreignCurrencyPrintPrice;
    }

    public void setForeignCurrencyPrintPrice(BigDecimal foreignCurrencyPrintPrice) {
        this.foreignCurrencyPrintPrice = foreignCurrencyPrintPrice;
    }

    public BigDecimal getForeignCurrencyAmount() {
        return foreignCurrencyAmount;
    }

    public void setForeignCurrencyAmount(BigDecimal foreignCurrencyAmount) {
        this.foreignCurrencyAmount = foreignCurrencyAmount;
    }

    public BigDecimal getForeignCurrencyCost() {
        return foreignCurrencyCost;
    }

    public void setForeignCurrencyCost(BigDecimal foreignCurrencyCost) {
        this.foreignCurrencyCost = foreignCurrencyCost;
    }

    public BigDecimal getForeignCurrencyTax() {
        return foreignCurrencyTax;
    }

    public void setForeignCurrencyTax(BigDecimal foreignCurrencyTax) {
        this.foreignCurrencyTax = foreignCurrencyTax;
    }

    public BigDecimal getForeignCurrencyOilFee() {
        return foreignCurrencyOilFee;
    }

    public void setForeignCurrencyOilFee(BigDecimal foreignCurrencyOilFee) {
        this.foreignCurrencyOilFee = foreignCurrencyOilFee;
    }

    public BigDecimal getForeignCurrencyChangeFee() {
        return foreignCurrencyChangeFee;
    }

    public void setForeignCurrencyChangeFee(BigDecimal foreignCurrencyChangeFee) {
        this.foreignCurrencyChangeFee = foreignCurrencyChangeFee;
    }

}
