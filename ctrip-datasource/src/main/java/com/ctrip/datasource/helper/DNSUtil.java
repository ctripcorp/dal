package com.ctrip.datasource.helper.DNS;

import com.ctrip.platform.dal.dao.helper.Action;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DNSUtil {
    private static final String DAL = "DAL";
    private String RESOLVE_DOMAIN_URL_FORMAT = "ResolveDomainUrl:%s";
    private String DOMAIN_URL_FORMAT = "Domain Url:%s, IP:%s";
    private final int DNS_RESOLVE_TIMEOUT_IN_MILLIS = 1 * 1000;
    private final TimeUnit TIME_UNIT_IN_MILLIS = TimeUnit.MILLISECONDS;

    private ConcurrentMap<String, com.ctrip.datasource.helper.DNS.DNSInfo> map = new ConcurrentHashMap<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public String resolveDNS(String domain) {
        if (domain == null || domain.isEmpty())
            return "";

        String ip = "";
        com.ctrip.datasource.helper.DNS.DNSInfo info = null;
        Transaction t = Cat.newTransaction(DAL, String.format(RESOLVE_DOMAIN_URL_FORMAT, domain));

        try {
            info = getDNSInfo(domain);
            ip = resolveDNSWithTimeout(domain);
            t.addData(String.format(DOMAIN_URL_FORMAT, domain, ip));
            t.setStatus(Message.SUCCESS);

            info.SetSuccessStatus();
        } catch (Throwable e) {
            t.setStatus(e);

            final Throwable ex = e;
            info.SetFailStatus(new Action() {
                @Override
                public void invoke() throws Exception {
                    Cat.logError(ex);
                }
            });
        } finally {
            t.complete();
        }

        return ip;
    }

    private com.ctrip.datasource.helper.DNS.DNSInfo getDNSInfo(String domain) {
        com.ctrip.datasource.helper.DNS.DNSInfo info = null;
        map.get(domain);
        if (info != null)
            return info;

        info = new com.ctrip.datasource.helper.DNS.DNSInfo();
        map.putIfAbsent(domain, info);
        return info;
    }

    private String resolveDNSWithTimeout(String domain) throws Exception {
        Future<String> future = executor.submit(new ResolveDNS(domain));
        String result = "";
        try {
            result = future.get(DNS_RESOLVE_TIMEOUT_IN_MILLIS, TIME_UNIT_IN_MILLIS);
        } catch (Throwable e) {
            future.cancel(true);
            throw e;
        }
        return result;
    }

    private class ResolveDNS implements Callable<String> {
        private String domain;

        public ResolveDNS(String domain) {
            this.domain = domain;
        }

        @Override
        public String call() throws UnknownHostException {
            // Thread.sleep(2 * 1000);
            InetAddress address = InetAddress.getByName(domain);
            if (address == null)
                return "";

            return address.getHostAddress();
        }
    }

}
