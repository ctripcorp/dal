package com.ctrip.platform.dal.dao;

import com.ctrip.framework.dal.cluster.client.sharding.context.ShardData;
import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.common.enums.ParametersType;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.*;

public class StatementParameters implements ShardData {
	private static final String SQLHIDDENString = "*";

	private List<StatementParameter> parameters = new LinkedList<StatementParameter>();
	private List<StatementParameter> resultParameters=new LinkedList<StatementParameter>();
	private Set<Integer> parametersIndexSet = new HashSet<>();
	private ParametersType existingParametersType;

	public StatementParameters add(StatementParameter parameter) {
		int index = parameter.getIndex();
		if (parameters.size() == 0)
			existingParametersType = getType(index);

		checkMixedParametersTypes(index);
		resetIndexForNoIndexParameter(parameter);
		checkDuplicateIndex(index);

		parameters.add(parameter);
		parametersIndexSet.add(parameter.getIndex());
		return this;
	}

	public StatementParameters addAll(StatementParameters extraParameters) {
		int index = parameters.size() + 1;
		StatementParameters tempParameters=extraParameters.duplicate();
		for (StatementParameter p : tempParameters.values())
			add(p.setIndex(index++));
		return this;
	}

	public StatementParameters set(int index, Object value) {
		return add(new StatementParameter(index, value));
	}

	public StatementParameters set(int index, int sqlType, Object value) {
		return add(new StatementParameter(index, sqlType, value));
	}

	public StatementParameters set(int index, String name, int sqlType, Object value) {
		return add(new StatementParameter(index, sqlType, value).setName(name));
	}

	public StatementParameters set(int index, String name, Object value) {
		return add(new StatementParameter(index, value).setName(name));
	}

	public StatementParameters set(String name, int sqlType, Object value) {
		return add(new StatementParameter(name, sqlType, value));
	}

	public StatementParameters set(String name, Object value) {
		return add(new StatementParameter(-1, value).setName(name));
	}

	public StatementParameters registerInOut(String name, int sqlType, Object value) {
		return add(StatementParameter.registerInOut(name, sqlType, value));
	}

	public StatementParameters registerOut(String name, int sqlType) {
		return add(StatementParameter.registerOut(name, sqlType));
	}

	public StatementParameters registerInOut(int index, int sqlType, Object value) {
		return add(StatementParameter.registerInOut(index, sqlType, value));
	}

	public StatementParameters registerOut(int index, int sqlType) {
		return add(StatementParameter.registerOut(index, sqlType));
	}

	public StatementParameters setSensitive(int index, String name, Object value) {
		return add(new StatementParameter(index, value).setSensitive(true).setName(name));
	}

	public StatementParameters setSensitive(int index, Object value) {
		return add(new StatementParameter(index, value).setSensitive(true));
	}

	public StatementParameters setSensitive(String name, Object value) {
		return add(new StatementParameter(-1, value).setSensitive(true).setName(name));
	}

	public StatementParameters setSensitive(int index, int sqlType, Object value) {
		return add(new StatementParameter(index, sqlType, value).setSensitive(true));
	}

	public StatementParameters setSensitive(int index, String name, int sqlType, Object value) {
		return add(new StatementParameter(index, sqlType, value).setSensitive(true).setName(name));
	}

	public StatementParameters setSensitive(String name, int sqlType, Object value) {
		return add(new StatementParameter(name, sqlType, value).setSensitive(true));
	}

	public StatementParameters registerInOutSensitive(String name, int sqlType, Object value) {
		return add(StatementParameter.registerInOut(name, sqlType, value).setSensitive(true));
	}

	public StatementParameters registerOutSensitive(String name, int sqlType) {
		return add(StatementParameter.registerOut(name, sqlType).setSensitive(true));
	}

	/**
	 * Register result parameter for update count. It is used to get the update count for
	 * update statement executed in the store procedure.
	 * <p>
	 * When executed, you can use getValue() to get the update count. E.g.
	 * <p>
	 * StatementParameters parameters = new StatementParameters();
	 * parameters.setResultsParameter("count");
	 * DalClientFactory.getClient(logicDbName).call(YOUR_SP, parameters, hints);
	 * Object value = parameters.get("count", null).getValue());
	 *
	 * @param name user defined name represents update count
	 * @return
	 */
	public StatementParameters setResultsParameter(String name) {
		return addResultParameter(new StatementParameter().setResultsParameter(true).setName(name));
	}

	/**
	 * Register result parameter for result set. It is used to get the result set for
	 * select statement executed in the store procedure.
	 * <p>
	 * When executed, you can use getValue() to get the extracted value from result set. E.g
	 * <p>
	 * StatementParameters parameters = new StatementParameters();
	 * parameters.setResultsParameter("result", new DalScalarExtractor());
	 * DalClientFactory.getClient(logicDbName).call(YOUR_SP, parameters, hints);
	 * Object value = parameters.get("result", null).getValue());
	 *
	 * @param name user defined name represents result set
	 * @return
	 */
	public StatementParameters setResultsParameter(String name, DalResultSetExtractor<?> extractor) {
		return addResultParameter(new StatementParameter().setResultsParameter(true).setResultSetExtractor(extractor).setName(name));
	}

	public StatementParameters addResultParameter(StatementParameter resultParameter) {
		resultParameters.add(resultParameter);
		return this;
	}

	public List<StatementParameter> getResultParameters() {
		return resultParameters;
	}

	public StatementParameter getResultParameterByName(String name) {
		if (name == null)
			return null;

		for (StatementParameter parameter : resultParameters) {
			if (parameter.getName() != null && parameter.getName().equalsIgnoreCase(name))
				return parameter;
		}
		return null;
	}

	public int setInParameter(int index, String name, List<?> values, boolean sensitive) {
		add(new StatementParameter(index++, values).setName(name).setSensitive(sensitive).setInParam(true));
		return index;
	}

	public int setInParameter(int index, List<?> values) {
		return setInParameter(index, null, values, false);
	}

	public int setSensitiveInParameter(int index, List<?> values) {
		return setInParameter(index, null, values, true);
	}

	public int setInParameter(int index, String name, List<?> values) {
		return setInParameter(index, name, values, false);
	}

	public int setSensitiveInParameter(int index, String name, List<?> values) {
		return setInParameter(index, name, values, true);
	}

	public int setInParameter(int index, String name, int sqlType, List<?> values, boolean sensitive) {
		add(new StatementParameter(index++, sqlType, values).setName(name).setSensitive(sensitive).setInParam(true));
		return index;
	}

	public int setInParameter(int index, String name, int sqlType, List<?> values) {
		return setInParameter(index, name, sqlType, values, false);
	}

	public int setSensitiveInParameter(int index, String name, int sqlType, List<?> values) {
		return setInParameter(index, name, sqlType, values, true);
	}

	public int setInParameter(int index, int sqlType, List<?> values) {
		return setInParameter(index, null, sqlType, values, false);
	}

	public int setSensitiveInParameter(int index, int sqlType, List<?> values) {
		return setInParameter(index, null, sqlType, values, true);
	}


	/**
	 * Set multiple parameters to sensitive by parameter index.
	 *
	 * @param indexList
	 * @return
	 */
	public StatementParameters setSensitiveByIndex(List<Integer> indexList) {
		for (StatementParameter p : parameters)
			if (indexList.contains(p.getIndex()))
				p.setSensitive(true);
		return this;
	}

	/**
	 * Set multiple parameters to sensitive by parameter name.
	 *
	 * @param nameList
	 * @return
	 */
	public StatementParameters setSensitiveByName(List<String> nameList) {
		for (StatementParameter p : parameters)
			if (nameList.contains(p.getName()))
				p.setSensitive(true);
		return this;
	}

	public int size() {
		return parameters.size();
	}

	public int nextIndex() {
		return parameters.size() + 1;
	}

	public StatementParameter get(int i) {
		return parameters.get(i);
	}

	public StatementParameter getLast() {
		return parameters.get(parameters.size() - 1);
	}

	public StatementParameter get(String name, ParameterDirection direction) {
		if (name == null)
			return null;

		for (StatementParameter parameter : parameters) {
			if (parameter.getName() != null && parameter.getName().equalsIgnoreCase(name) && direction == parameter.getDirection())
				return parameter;
		}
		return null;
	}

	/**
	 * Set the parameter to be nullable, if it is , the parameter maybe ignored
	 */
	public void nullable() {
		getLast().nullable();
	}

	/**
	 * Set if the parameter is valid or not by the condition
	 */
	public void when(boolean condition) {
		getLast().when(condition);
	}

	public List<StatementParameter> values() {
		return parameters;
	}

	public String toLogString() {
		StringBuilder valuesSb = new StringBuilder();
		int i = 0;
		for (StatementParameter param : this.values()) {
			valuesSb.append(String.format("%s=%s",
					param.getName() == null ? param.getIndex() : param.getName(),
					param.isSensitive() ? SQLHIDDENString : param.getValue()));
			if (++i < size())
				valuesSb.append(",");
		}
		return valuesSb.toString();
	}

	public StatementParameters duplicateWith(String name, Object value) {
		StatementParameters tempParameters = new StatementParameters();

		for (StatementParameter parameter : parameters) {
			Object pValue = name.equals(parameter.getName()) ? value : parameter.getValue();

			tempParameters.add(new StatementParameter(parameter).setValue(pValue));
		}

		return tempParameters;
	}

	public StatementParameters duplicate() {
		StatementParameters tempParameters = new StatementParameters();

		for (StatementParameter parameter : parameters) {
			tempParameters.add(new StatementParameter(parameter));
		}

		return tempParameters;
	}

	public List<List<?>> getAllInParameters() {
		List<List<?>> inParams = new ArrayList<>();
		for (StatementParameter parameter : parameters)
			if (parameter.isInParam())
				inParams.add((List<?>) parameter.getValue());

		return inParams;
	}

	public boolean containsInParameter() {
		for (StatementParameter p : parameters)
			if (p.isInParam())
				return true;
		return false;
	}

	/**
	 * Expand in parameters if necessary. This must be executed before execution
	 */
	public void compile() {
		if (!containsInParameter())
			return;

		//To be safe, order parameters by original index
		Collections.sort(parameters);

		// Make a copy of original parameters
		List<StatementParameter> tmpParameters = new LinkedList<StatementParameter>(parameters);

		// The change will be made into original parameters
		int i = 0;
		for (StatementParameter p : tmpParameters) {
			if (p.isInParam()) {
				// Remove the original
				parameters.remove(p);
				List<?> values = p.getValue();
				for (Object val : values) {
					parameters.add(i, new StatementParameter(p).setIndex((i + 1)).setInParam(false).setValue(val));
					i++;
				}
			} else {
				p.setIndex(++i);
			}
		}
	}

	public void checkDuplicateIndex(int index) {
		if (parametersIndexSet.contains(index)) {
			throw new DalRuntimeException(String.format("Duplicate index %s in statement parameters", index));
		}
	}

	public void checkMixedParametersTypes(int index) {
		if ((getType(index) != existingParametersType))
			throw new DalRuntimeException(String.format("Please don't put index parameters and no-index parameters together"));
	}

	public void resetIndexForNoIndexParameter(StatementParameter parameter) {
		if (existingParametersType == ParametersType.noIndex) {
			int index = parameters.size() + 1;
			parameter.setIndex(index);
		}
	}

	/**
	 * Remove invalid parameter and reorder index
	 */
	public StatementParameters buildParameters() {
		Collections.sort(parameters);
		List<StatementParameter> newParameters = new LinkedList<StatementParameter>();
		int i = 1;
		for (StatementParameter parameter : parameters) {
			if (parameter.isValid()) {
				parameter.setIndex(i++);
				newParameters.add(parameter);
			}
		}
		parameters = newParameters;
		return this;
	}

	public ParametersType getType(int parameterIndex) {
		return (parameterIndex < 0) ? ParametersType.noIndex : ParametersType.index;
	}

	public ParametersType getExistingParametersType() {
		return existingParametersType;
	}

	@Override
	public Object getValue(String name) {
		StatementParameter parameter = get(name, ParameterDirection.Input);
		return parameter != null ? parameter.getValue() : null;
	}

	@Override
	public Set<String> getNames() {
		Set<String> names = new HashSet<>();
		for (StatementParameter parameter : parameters) {
			if (parameter.getName() != null && parameter.getDirection() == ParameterDirection.Input)
				names.add(parameter.getName());
		}
		return names;
	}

	/*enum ParametersType {
		noIndex,
		index
	}*/
}