package com.ctrip.sysdev.das.tester;

import java.io.File;

import com.ctrip.sysdev.das.common.cfg.DasConfigureService;
import com.ctrip.sysdev.das.common.db.ConfigureServiceReader;
import com.ctrip.sysdev.das.common.db.DasConfigureReader;

public class DirectClientDaoTest {
	public static void main(String[] args) {
		DasConfigureReader reader = new ConfigureServiceReader(new DasConfigureService("localhost:8080", new File("e:/snapshot.json")));
		try {
			DalClient client = new DalClient(reader, "HtlProductdb");
			client.executeQuery("select [HotelID],[LatestBookTime],[UID]  from HotelLatestBookInfo hl with(nolock)  Join resource r with(nolock) on r.resource = hl.HotelID join city c (nolock) on c.city = r.city and c.city in (select city from city (nolock) where Country = 1) ");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
