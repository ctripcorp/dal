package com.ctrip.platform.dal.daogen.vo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupUserVO {

	public static void main(String[] args) {

		Pattern pattern = Pattern.compile(".+\\((\\w+)\\).*");
		Matcher m = pattern.matcher("xgz夏光智(S44266)");
		if (m.find()) {
			System.out.println(m.group(1));
		}
		System.out.println(1);
	}
}
