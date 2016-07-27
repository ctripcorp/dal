package com.ctrip.platform.dal.dao.sqlbuilder;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.StatementParameters;

public class FreeUpdateSqlBuilder implements SqlBuilder {
	private String updateSqlTemplate;
	// Not used for now, to make it consistant with FreeSelectSqlBuilder
	private DatabaseCategory dbCategory;
	private StatementParameters parameters;
	
	public FreeUpdateSqlBuilder(DatabaseCategory dbCategory) {
		this.dbCategory = dbCategory;
	}

	/**
	 * If there is IN parameter, no matter how many values in the IN clause, the IN clause only need to 
	 * contain one "?".
	 * E.g. UPDATE ... WHERE id IN ?
	 * @param updateSqlTemplate
	 * @return
	 */
	public FreeUpdateSqlBuilder setTemplate(String updateSqlTemplate) {
		this.updateSqlTemplate = updateSqlTemplate;
		return this;
	}
	
	public FreeUpdateSqlBuilder with(StatementParameters parameters) {
		this.parameters = parameters;
		return this;
	}
	
	@Override
	public StatementParameters buildParameters() {
		return parameters;
	}
	
	public String build(){
		return updateSqlTemplate;
	}
}
