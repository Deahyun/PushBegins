package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserLog {
	public static void Log(String tag, String data) {
		SimpleDateFormat dt = new SimpleDateFormat("HH:mm:ss");
		String now = dt.format(new Date());
		System.out.println("[" + now + "] <" + tag + ">: " + data);
	}
}
