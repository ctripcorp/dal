package com.ctrip.platform.dal.daogen.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ctrip.platform.dal.daogen.Consts;
import com.ctrip.platform.dal.daogen.entity.GenTaskBySqlBuilder;

public class SqlBuilder {
	
	public static String formatSql(GenTaskBySqlBuilder task) {

		// 数据库中存储的模式： ID,0;Name,1; 表示"ID = "以及"Name != "
		String[] conditions = task.getCondition().split(";");
		// 数据库中存储的模式： ID,Name
		String[] fields = task.getFields().split(",");

		List<String> formatedConditions = new ArrayList<String>();
		// 将所有WHERE条件拼接，如ID_0,Name_1，for循环后将变为一个数组： [" ID = ", " Name != "]
		for (String con : conditions) {
			String[] keyValue = con.split(",");
			if (keyValue.length != 2) {
				continue;
			}
			// Between类型的操作符需要特殊处理
			if (keyValue[1].equals("6")) {
				if (task.getSql_style().equals("csharp")) {
					formatedConditions.add(String.format(
							" BETWEEN @%s_start AND @%s_end ", keyValue[0],
							keyValue[0]));
				} else {
					formatedConditions.add(" BETWEEN ? AND ? ");
				}
			} else {
				if (task.getSql_style().equals("csharp")) {
					formatedConditions.add(String.format(" %s %s @%s ",
							keyValue[0],
							Consts.WhereConditionMap.get(keyValue[1]),
							keyValue[0]));
				} else {
					formatedConditions.add(String.format(" %s %s ? ",
							keyValue[0],
							Consts.WhereConditionMap.get(keyValue[1])));
				}
			}
		}

		if (task.getCrud_type().equalsIgnoreCase("Select")) {
			if (formatedConditions.size() > 0) {
				return String
						.format("SELECT %s FROM %s WHERE %s", task.getFields(),
								task.getTable_name(), StringUtils.join(
										formatedConditions.toArray(), " AND "));
			} else {
				return String.format("SELECT %s FROM %s", task.getFields(),
						task.getTable_name());
			}
		} else if (task.getCrud_type().equalsIgnoreCase("Insert")) {

				List<String> placeHodler = new ArrayList<String>();
				for (String field : fields) {
					if (task.getSql_style().equals("csharp")) {
						placeHodler.add(String.format(" @%s ", field));
					} else {
						placeHodler.add(" ? ");
					}
				}
				return String.format("INSERT INTO %s (%s) VALUES (%s)",
						task.getTable_name(), task.getFields(),
						StringUtils.join(placeHodler.toArray(), ","));
			

		} else if (task.getCrud_type().equalsIgnoreCase("Update")) {
				List<String> placeHodler = new ArrayList<String>();
				for (String field : fields) {
					if (task.getSql_style().equals("csharp")) {
						placeHodler.add(String.format(" %s = @%s ", field,
								field));
					} else {
						placeHodler.add(String.format(" %s = ? ", field));
					}
				}
				if (formatedConditions.size() > 0) {
					return String.format("UPDATE %s SET %s WHERE %s", task
							.getTable_name(), StringUtils.join(
							placeHodler.toArray(), ","), StringUtils.join(
							formatedConditions.toArray(), " AND "));
				} else {
					return String.format("UPDATE %s SET %s ",
							task.getTable_name(),
							StringUtils.join(placeHodler.toArray(), ","));
				}

		} else if (task.getCrud_type().equalsIgnoreCase("Delete")) {
				if (formatedConditions.size() > 0) {
					return String.format("Delete FROM %s WHERE %s", task
							.getTable_name(), StringUtils.join(
							formatedConditions.toArray(), " AND "));
				} else {
					return String
							.format("Delete FROM %s", task.getTable_name());
				}
		}
	

		return "";

	}

}
