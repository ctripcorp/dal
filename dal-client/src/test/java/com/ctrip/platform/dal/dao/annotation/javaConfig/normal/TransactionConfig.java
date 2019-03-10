package com.ctrip.platform.dal.dao.annotation.javaConfig.normal;

import com.ctrip.platform.dal.dao.annotation.EnableDalTransaction;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDalTransaction
@ComponentScan(basePackages = "com.ctrip.platform.dal.dao.annotation.javaConfig.normal")
public class TransactionConfig {}
