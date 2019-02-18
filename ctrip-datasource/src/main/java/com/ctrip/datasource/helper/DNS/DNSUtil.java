package com.ctrip.datasource.helper.DNS;

import com.ctrip.platform.dal.dao.helper.Action;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DNSUtil {
    private String RESOLVE_DOMAIN_URL_FORMAT = "ResolveDomainUrl:%s";
    private String DOMAIN_URL_FORMAT = "Domain Url:%s, IP:%s";
    private final int DNS_RESOLVE_TIMEOUT_IN_MILLIS = 1 * 1000;
    private final TimeUnit TIME_UNIT_IN_MILLIS = TimeUnit.MILLISECONDS;
    private static final Pattern pattern = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    private ConcurrentMap<String, DNSInfo> map = new ConcurrentHashMap<>();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public String resolveDNS(String domain) {
        if (domain == null || domain.isEmpty())
            return "";

        boolean isIP = isValidIP(domain);
        if (isIP)
            return "";

        String ip = "";
        DNSInfo info = null;
        Transaction t = Cat.newTransaction(DalLogTypes.DAL, String.format(RESOLVE_DOMAIN_URL_FORMAT, domain));

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

    private boolean isValidIP(String domain) {
        Matcher match = pattern.matcher(domain);
        return match.find();
    }

    private DNSInfo getDNSInfo(String domain) {
        DNSInfo info = null;
        map.get(domain);
        if (info != null)
            return info;

        info = new DNSInfo();
        map.putIfAbsent(domain, info);
        return info;
    }

    private String resolveDNSWithTimeout(String domain) throws Exception {
        Future<String> future = executor.submit(new ResolveDNS(domain));
        String result;
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
