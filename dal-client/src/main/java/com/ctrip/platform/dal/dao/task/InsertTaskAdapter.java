package com.ctrip.platform.dal.dao.task;

import java.util.*;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.sharding.idgen.IdGenerator;
import com.ctrip.platform.dal.sharding.idgen.NullIdGenerator;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.getDatabaseSet;

public class InsertTaskAdapter<T> extends TaskAdapter<T> {
	public static final String TMPL_SQL_INSERT = "INSERT INTO %s (%s) VALUES(%s)";

	protected Set<String> insertableColumns;
	protected Set<String> notInsertableColumns;
	protected String columnsForInsert;
	protected String columnsForInsertWithId;
	protected List<String> validColumnsForInsert;
	protected List<String> validColumnsForInsertWithId;

	protected IdGenerator idGenerator;
	
	public void initialize(DalParser<T> parser) {
		super.initialize(parser);
		
		insertableColumns = new HashSet<>();
		Collections.addAll(insertableColumns, parser.getInsertableColumnNames());
		
		notInsertableColumns = new HashSet<>();
		Collections.addAll(notInsertableColumns, parser.getColumnNames());
		notInsertableColumns.removeAll(insertableColumns);
		
		validColumnsForInsert = buildValidColumnsForInsert();
		validColumnsForInsertWithId = buildValidColumnsForInsertWithId();
		
		columnsForInsert = combineColumns(validColumnsForInsert, COLUMN_SEPARATOR);
		columnsForInsertWithId = combineColumns(validColumnsForInsertWithId, COLUMN_SEPARATOR);

		idGenerator = getDatabaseSet(logicDbName).getIdGenConfig().getIdGenerator(logicDbName, rawTableName);
	}
	
	private List<String> buildValidColumnsForInsert() {
		List<String> validColumns = new LinkedList<>(buildValidColumnsForInsertWithId());
		
		if(parser.isAutoIncrement())
			validColumns.remove(parser.getPrimaryKeyNames()[0]);
		
		return validColumns;
	}	

	private List<String> buildValidColumnsForInsertWithId() {
		return Arrays.asList(parser.getInsertableColumnNames());
	}
	
	public List<String> buildValidColumnsForInsert(Set<String> unqualifiedColumns) {
		List<String> finalValidColumnsForInsert = new ArrayList<>(Arrays.asList(parser.getInsertableColumnNames()));
		finalValidColumnsForInsert.removeAll(unqualifiedColumns);
		
		return finalValidColumnsForInsert;
	}
	
	public Set<String> filterUnqualifiedColumns(DalHints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) {
		Set<String> unqualifiedColumns = new HashSet<>(notInsertableColumns);
		
		if(parser.isAutoIncrement() && hints.isIdentityInsertDisabled() && idGenerator instanceof NullIdGenerator)
			unqualifiedColumns.add(parser.getPrimaryKeyNames()[0]);

		if(hints.isInsertNullField()) {
			return unqualifiedColumns;
		}

		Set<String> nullColumns = new HashSet<>(insertableColumns);
		String[] columnsToCheck = nullColumns.toArray(new String[nullColumns.size()]);
		boolean changed = false;
		for (Map<String, ?> pojo: daoPojos) {
			if(nullColumns.isEmpty())
				break;

			if(changed) {
				columnsToCheck = nullColumns.toArray(new String[nullColumns.size()]);
				changed = false;
			}
			
			for (int i = 0; i < columnsToCheck.length; i++) {
				String colName = columnsToCheck[i];
				if(pojo.get(colName) != null) {
					nullColumns.remove(colName);
					changed = true;
				}
			}
		}		

		unqualifiedColumns.addAll(nullColumns);
		
		return unqualifiedColumns;
	}

	public void removeUnqualifiedColumns(Map<String, ?> pojo, Set<String> unqualifiedColumns) {
		if(unqualifiedColumns.size() == 0)
			return;
		
		for(String columName: unqualifiedColumns) {
			pojo.remove(columName);
		}
	}

	/**
	 * Insert auto-generated id if necessary
	 * @param hints
	 * @param pojo
	 * @return processed field map
	 */
	public Map<String, ?> processIdentityField(DalHints hints, Map<String, ?> pojo) {
		if (parser.isAutoIncrement()) {
			String identityFieldName = parser.getPrimaryKeyNames()[0];
			if (!(idGenerator instanceof NullIdGenerator) &&
					(hints.isIdentityInsertDisabled() || !pojo.containsKey(identityFieldName))) {
				Map<String, Object> newPojo = new LinkedHashMap<>();
				for (Map.Entry<String, ?> entry : pojo.entrySet()) {
					newPojo.put(entry.getKey(), entry.getValue());
				}
				newPojo.put(identityFieldName, idGenerator.nextId());
				return newPojo;
			}
		}
		return pojo;
	}

	protected void processIdentityField(DalHints hints, List<Map<String, ?>> pojos) {
		if (parser.isAutoIncrement() && !(idGenerator instanceof NullIdGenerator)) {
			String identityFieldName = parser.getPrimaryKeyNames()[0];
			boolean identityInsertDisabled = hints.isIdentityInsertDisabled();
			for (Map pojo : pojos) {
				if (identityInsertDisabled && !pojo.containsKey(identityFieldName)) {
					pojo.put(identityFieldName, idGenerator.nextId());
				}
			}
		}
	}
}
