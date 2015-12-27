package com.maigo.rpc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class InfoPrinter 
{
	/**
	 * is allow to printer info
	 */
	public final static boolean ACTIVE = true;
	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static BlockingDeque<String> printMissionQueue = new LinkedBlockingDeque<String>();
    private static ExecutorService threadPool;

    static
    {
        //set up the thread for print
        threadPool = Executors.newSingleThreadExecutor();
        threadPool.execute(new Runnable()
        {
            public void run()
            {
                try
                {
                    while(true)
                    {
                        String info = printMissionQueue.take();
                        Date date = new Date();
                        System.out.println("[" + DATE_FORMAT.format(date) + "]:" + info);
                    }
                }
                catch (InterruptedException e)
                {
                    //exit
                }
            }
        });
    }

	public static void println(String info)
	{
		if(!ACTIVE)
			return;

        printMissionQueue.add(info);
	}

    public static void exit()
    {
        if(!ACTIVE)
            return;

        threadPool.shutdownNow();
    }
}
