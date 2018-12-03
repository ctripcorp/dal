package com.ctrip.platform.dal.dao.client;

public interface ILogSamplingStrategy {
   boolean validate(ILogEntry entry);
}
