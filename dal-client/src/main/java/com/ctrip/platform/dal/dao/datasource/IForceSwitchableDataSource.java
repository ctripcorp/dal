package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.FirstAidKit;
import com.ctrip.platform.dal.dao.configure.SwitchableDataSourceStatus;

public interface IForceSwitchableDataSource {
    /**
     * force switch datasource with input ip and port
     *
     * @param ip of the new datasource
     * @param port of the new datasource
     * @return SwitchableDataSourceStatus before switch
     */
    //SwitchableDataSourceStatus forceSwitch(String ip, Integer port);

    SwitchableDataSourceStatus forceSwitch(FirstAidKit configure, final String ip, final Integer port);

    FirstAidKit getFirstAidKit();

    /**
     * get status of current datasource
     */
    SwitchableDataSourceStatus getStatus();

    /**
     * restore datasource to status before force switch
     *
     * @return SwitchableDataSourceStatus before restore
     */
    SwitchableDataSourceStatus restore();

    void addListener(SwitchListener listener);

    interface SwitchListener {
        void onForceSwitchSuccess(SwitchableDataSourceStatus currentStatus);

        void onForceSwitchFail(SwitchableDataSourceStatus currentStatus, Throwable cause);

        void onRestoreSuccess(SwitchableDataSourceStatus currentStatus);

        void onRestoreFail(SwitchableDataSourceStatus currentStatus, Throwable cause);
    }
}
