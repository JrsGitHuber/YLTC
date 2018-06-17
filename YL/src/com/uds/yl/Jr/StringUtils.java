package com.uds.yl.Jr;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
	
	public static String GetNameByString(String str) throws Exception {
		String returnStr = str.replaceAll("[/\\\\:*?<>|]", "");
		if (returnStr.equals("")) {
			returnStr = GetNewNameByTime();
		}
		return returnStr;
	}
	
	private static String GetNewNameByTime() {
		return "δ����" + new SimpleDateFormat("_yyyyMMdd_HHmmsss").format(new Date());
	}
}
