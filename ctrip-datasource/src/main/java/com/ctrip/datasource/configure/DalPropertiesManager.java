package com.ctrip.datasource.configure;

import com.ctrip.datasource.configure.qconfig.DalPropertiesProviderImpl;
import com.ctrip.datasource.datasource.DalPropertiesChanged;
import com.ctrip.datasource.datasource.DalPropertiesProvider;
import com.ctrip.platform.dal.common.enums.TableParseSwitch;
import com.ctrip.platform.dal.dao.configure.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.exceptions.DalException;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;


/**
 * Created by lilj on 2018/7/22.
 */
public class DalPropertiesManager {
    private static final String DAL = "DAL";
    protected static final Logger LOGGER = LoggerFactory.getLogger(DalPropertiesManager.class);
    private static final String SET_TABLE_PARSE_SWITCH = "Set TableParseSwitch";
    private static final String DALPROPERTIES_REFRESH_TABLEPARSESWITCH = "DalProperties::refreshTableParseSwitch";
    private static final String DALPROPERTIES_NOTIFY_LISTENER_START = "DalProperties.notifyListener.start";
    private static final String DALPROPERTIES_NOTIFY_LISTENER_END = "DalProperties.notifyListener.end";
    private DalPropertiesProvider dalPropertiesProvider = new DalPropertiesProviderImpl();
    private DalPropertiesLocator dalPropertiesLocator = DalElementFactory.DEFAULT.getDalPropertiesLocator();
    private AtomicReference<Boolean> isTableParseSwitchListenerAddedRef = new AtomicReference<>(false);

    private volatile static DalPropertiesManager manager = null;

    public synchronized static DalPropertiesManager getInstance() {
        if (manager == null) {
            manager = new DalPropertiesManager();
        }
        return manager;
    }

    public synchronized void setup(){
        // set table parse switch status
        TableParseSwitch status = dalPropertiesProvider.getTableParseSwitch();
        dalPropertiesLocator.setTableParseSwitch(status);

        boolean isTableParseSwitchListenerAdded = isTableParseSwitchListenerAddedRef.get().booleanValue();
        if (!isTableParseSwitchListenerAdded) {
            addTableParseSwitchChangedListener();
            isTableParseSwitchListenerAddedRef.compareAndSet(false, true);
        }
    }

    private void addTableParseSwitchChangedListener() {
        dalPropertiesProvider.addTableParseSwitchChangedListener(new DalPropertiesChanged() {
            @Override
            public void onTableParseSwitchChanged(TableParseSwitch status) {
                TableParseSwitch currentStatus = dalPropertiesLocator.getTableParseSwitch();
                if (currentStatus.equals(status)) {
                    String msg = String.format("New TableParseSwitch equals to current switch: %s", status.toString());
                    Cat.logEvent(DAL, DALPROPERTIES_REFRESH_TABLEPARSESWITCH, Message.SUCCESS, msg);
                    LOGGER.info(msg);
                    return;
                }

                addParseTableSwitchNotifyTask(status);
            }
        });
    }


    private void addParseTableSwitchNotifyTask(TableParseSwitch status) {
        Transaction t = Cat.newTransaction(DAL, DALPROPERTIES_REFRESH_TABLEPARSESWITCH);
        String newTableParseSwitchStatus = String.format("New Table_Parse_Switch status: %s", status.toString());
        String oldTableParseSwitchStatus = String.format("Old Table_Parse_Switch status: %s", dalPropertiesLocator.getTableParseSwitch().toString());
        t.addData(oldTableParseSwitchStatus);
        t.addData(newTableParseSwitchStatus);

        t.addData(DALPROPERTIES_NOTIFY_LISTENER_START);
        Cat.logEvent(DAL, DALPROPERTIES_REFRESH_TABLEPARSESWITCH, Message.SUCCESS, newTableParseSwitchStatus);
        Cat.logEvent(DAL, DALPROPERTIES_REFRESH_TABLEPARSESWITCH, Message.SUCCESS, DALPROPERTIES_NOTIFY_LISTENER_START);

        try {
            // set switch status
            dalPropertiesLocator.setTableParseSwitch(status);
            t.addData(SET_TABLE_PARSE_SWITCH);
            Cat.logEvent(DAL, DALPROPERTIES_REFRESH_TABLEPARSESWITCH, Message.SUCCESS, SET_TABLE_PARSE_SWITCH);

            t.addData(DALPROPERTIES_NOTIFY_LISTENER_END);
            t.setStatus(Transaction.SUCCESS);
            Cat.logEvent(DAL, DALPROPERTIES_REFRESH_TABLEPARSESWITCH, Message.SUCCESS, DALPROPERTIES_NOTIFY_LISTENER_END);
        } catch (Throwable e) {
            DalException exception = new DalException("RefreshTableParseSwitchError", e);
            t.setStatus(exception);
            Cat.logError(exception);
            LOGGER.error(String.format("DalException:%s", e.getMessage()), exception);
        } finally {
            t.complete();
        }
    }

    public void setDalPropertiesProvider(DalPropertiesProvider provider) {
        this.dalPropertiesProvider = provider;
    }
}
