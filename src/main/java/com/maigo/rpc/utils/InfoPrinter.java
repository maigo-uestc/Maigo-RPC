package com.maigo.rpc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InfoPrinter 
{
	/**
	 * is allow to printer info
	 */
	public final static boolean ACTIVE = true;
	
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void println(String info)
	{
		if(!ACTIVE)
			return;
		
		Date date = new Date();
		System.out.println("[" + DATE_FORMAT.format(date) + "]:" + info);
	}
}
