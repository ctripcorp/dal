package com.ctrip.platform.dal.util;

public class CompactSqlText {
	
	public static String getCompactSql(String sql) {
		StringBuffer sqlnew=new StringBuffer();
		StringBuffer word=new StringBuffer();
		char[] chrs = sql.toCharArray(); // sql字符拆到数组里
		int len = chrs.length;

		// 遍历sql字符数组
		for (int i = 0; i < len; i++) {
			char chr = chrs[i];

			// 对单引号的处理（对 单引号 和 操作符 是分两个逻辑处理）
			if (chr == '\'') {
				for (i++; i < len; i++) {
					// 如果不等于'，continue
					if (chrs[i] != '\'')
						continue;

					// 循环到结尾，break
					if (i == len - 1)
						break;

					// 如果下一字符是'，继续
					if (chrs[i + 1] == '\'')
						i++;
					else
						break;
				}
				continue;
			}

			// 过滤字符
			if ((chr >= 97 && chr <= 122)) {
				// 普通字符，加入word流
				word.append(chr);
				continue;
			} else {
				// 对断词符号的处理 right(a.name,8)
				if (chr == ' ' || chr == ',' || chr == '(' || chr == ')'
						|| chr == '[' || chr == ']' || chr == '=' || chr == '<'
						|| chr == '>' || chr == '\t' || chr == '\r'
						|| chr == '\n' || chr == '`' || chr == '!'
						|| chr == '+' || chr == '-' || chr == '*' || chr == '/'
						|| chr == '%' || chr == '&' || chr == '|' || chr == '^') {
					// 如果保留该符号，则进行处理
					if (chr == '=' || chr == '<' || chr == '>' || chr == '!') {
						word.append(chr);
						sqlnew.append(checkWord(word.toString()));
						word.setLength(0);
					} else {
						// 断词符号不在保留范围内，则丢弃，并将之前的word流加入sqlnew
						if (word.length() > 0) {
							sqlnew.append(checkWord(word.toString()));
							word.setLength(0);
						}
					}

					continue;
				}

				// 普通字符，加入word流
				word.append(chr);
			}
		}

		if (word.length() > 0) {
			sqlnew.append(checkWord(word.toString()));
		}

		return sqlnew.toString();
	}

	public static String checkWord(String word) {
		char[] chrs = word.toCharArray();
		char chr = chrs[0];
		if (chr == '@' || chr == '$') {
			return "";
		}
		if (chr >= '0' && chr <= '9') {
			return "";
		}
		return word;
	}
	
}
