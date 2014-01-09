package com.ctrip.sysdev.das.common.db;

import com.ctrip.sysdev.das.common.cfg.DasConfigureService;
import com.ctrip.sysdev.das.common.ns.DasNameService;
import com.ctrip.sysdev.das.common.to.MasterLogicDB;

public class FlexDasConfigureReader implements DasConfigureReader {
	private DasConfigureReader[] readers;
	private DasConfigureReader curReader;
	
	private static FlexDasConfigureReader clientReader;
	private static FlexDasConfigureReader dasReader;
	private static FlexDasConfigureReader consoleReader;
	
	public FlexDasConfigureReader(DasConfigureReader[] readers) {
		this.readers = readers;
		curReader = readers[0];
	}
	
	public static FlexDasConfigureReader getDasReader() {
		if(dasReader != null)
			return dasReader;
		return dasReader = new FlexDasConfigureReader(new DasConfigureReader[]{
				new LocalDasConfigureReader(),
				new NsDasConfigureReader(new DasConfigureService(null, null)),
		});
	}
	
	public static FlexDasConfigureReader getConsoleReader() {
		if(consoleReader != null)
			return consoleReader;
		return consoleReader = new FlexDasConfigureReader(new DasConfigureReader[]{
				new LocalDasConfigureReader(),
				new NsDasConfigureReader(new DasConfigureService(null, null)),
		});
	}

	public static FlexDasConfigureReader getClientReader() {
		if(clientReader != null)
			return clientReader;
		return clientReader = new FlexDasConfigureReader(new DasConfigureReader[]{
				new LocalDasConfigureReader(),
				new NsDasConfigureReader(new DasConfigureService(null, null)),
		});
	}

	// TODO need to check reader back online. timeout, etc
	@Override
	public MasterLogicDB getMasterLogicDB(String logicdbName) throws Exception {
		MasterLogicDB db = null;
		Exception exception = null;
		
		try {
			return curReader.getMasterLogicDB(logicdbName);
		} catch (Exception e) {
			exception = e;
		}

		for(DasConfigureReader reader: readers) {
			try {
				db = reader.getMasterLogicDB(logicdbName);
				curReader = reader;
			} catch (Exception e) {
				exception = e;
			}
		}
		
		if(db == null)
			throw exception;
		return db;
	}
}
