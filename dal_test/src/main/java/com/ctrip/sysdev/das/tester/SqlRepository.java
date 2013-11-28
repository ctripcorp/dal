package com.ctrip.sysdev.das.tester;

public class SqlRepository {
	// This is the base one. It will combine with others
	private String[] baseSqlRep = new String[] {
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
			"select * from Person",
	};
	
	private String[] sqlRep1 = new String[] {
			"select * from Person",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
	};

	private String[] sqlRep2 = new String[] {
			"select * from Person",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
	};
	
	private String[] sqlRep3 = new String[] {
			"select * from Person",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
			"",
	};
	
	private String[][] sqlReps = new String[][]{
			sqlRep1,
			sqlRep2,
			sqlRep3,
	};
	
	public SqlRepository() {
		for(int i = 0; i < sqlReps.length; i++){
			String[] sqlRep = new String[baseSqlRep.length + sqlReps[i].length];
			System.arraycopy(baseSqlRep, 0, sqlRep, 0, baseSqlRep.length);
			System.arraycopy(sqlReps[i], 0, sqlRep, baseSqlRep.length, sqlReps[i].length);	
			sqlReps[i] = sqlRep;
		}
	}
	
	public String[][] getSqlReps() {
		return sqlReps;
	}
	
}
