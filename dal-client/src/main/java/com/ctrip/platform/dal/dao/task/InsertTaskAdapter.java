package com.ctrip.platform.dal.dao.task;

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.*;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorConfig;
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

		IIdGeneratorConfig idGenConfig = getDatabaseSet(logicDbName).getIdGenConfig();
		if (idGenConfig != null) {
			idGenerator = idGenConfig.getIdGenerator(logicDbName, rawTableName);
		}
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
		
		if(parser.isAutoIncrement() && hints.isIdentityInsertDisabled() &&
				(null == idGenerator || idGenerator instanceof NullIdGenerator))
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

	public BulkTaskContext<T> createTaskContext(DalHints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) throws SQLException {
		BulkTaskContext<T> context = new BulkTaskContext<T>(rawPojos);
		Set<String> unqualifiedColumns = filterUnqualifiedColumns(hints, daoPojos, rawPojos);
		context.setUnqualifiedColumns(unqualifiedColumns);
		if (context instanceof DalContextConfigure)
			context.setShardingCategory(shardingCategory);
		return context;
	}

	public void processIdentityField(DalHints hints, List<Map<String, ?>> pojos) {
		if (parser.isAutoIncrement() && idGenerator != null && !(idGenerator instanceof NullIdGenerator)) {
			String identityFieldName = parser.getPrimaryKeyNames()[0];
			int identityFieldType = getColumnType(identityFieldName);
			boolean identityInsertDisabled = hints.isIdentityInsertDisabled();
			for (Map pojo : pojos) {
				if (identityInsertDisabled || null == pojo.get(identityFieldName)) {
					Number id = idGenerator.nextId();
					checkIdentityTypes(identityFieldType, id);
					pojo.put(identityFieldName, idGenerator.nextId());
				}
			}
		}
	}

	private void checkIdentityTypes(int identityFieldType, Number id) {
		boolean ok = false;
		switch (identityFieldType) {
			case Types.BIGINT:
				if (id instanceof Long || id instanceof Integer || id instanceof Short || id instanceof Byte)
					ok = true;
				break;
			case Types.INTEGER:
				if (id instanceof Integer || id instanceof Short || id instanceof Byte)
					ok = true;
				break;
			case Types.SMALLINT:
				if (id instanceof Short || id instanceof Byte)
					ok = true;
				break;
			case Types.TINYINT:
				if (id instanceof Byte)
					ok = true;
				break;
			default:
				throw new DalRuntimeException("Unsupported auto-incremental column type");
		}
		if (!ok) {
			throw new DalRuntimeException("The range of the generated id type exceeds that of the auto-incremental column type");
		}
	}

	public Map<String, Object> getIdentityField(Map<String, ?> pojo) {
		if (!parser.isAutoIncrement()) {
			return null;
		}
		String identityFieldName = parser.getPrimaryKeyNames()[0];
		Object identityFieldValue = pojo.get(identityFieldName);
		if (null == identityFieldValue) {
			return null;
		}
		Map<String, Object> key = new HashMap<>();
		key.put(identityFieldName, identityFieldValue);
		return key;
	}
}
