package com.maigo.rpc.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.maigo.rpc.client.RpcClientProxyBuilder;
import com.maigo.rpc.utils.InfoPrinter;

public class RpcPerformanceTest 
{
	public static JUnitTestInterface jUnitTestInterface;
	public final static int THREADS = 16;
	public final static int INVOKES = 10000;
	public final static int TIMEOUT = 300;
		
	public static void main(String[] args) throws Exception 
	{
		jUnitTestInterface = RpcClientProxyBuilder.create(JUnitTestInterface.class)
				.timeout(0)
				.threads(4)
				.connect("127.0.0.1", 3721)
				.build();
		
		ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
		CountDownLatch countDownLatch = new CountDownLatch(THREADS * INVOKES);
		InfoPrinter.println("RpcPerformanceTest started.");
		long startTime = System.currentTimeMillis();
		for(int i=0; i<THREADS; i++)
		{
			threadPool.execute(new RpcPerformanceTestThread(jUnitTestInterface, countDownLatch));
		}
		
		if(countDownLatch.await(TIMEOUT, TimeUnit.SECONDS))
		{
			long endTime = System.currentTimeMillis();
			double tps = THREADS * INVOKES / ((endTime - startTime) / 1000f);
			InfoPrinter.println("RpcPerformanceTest finished.");
			InfoPrinter.println("Result tps = " + tps);			
		}
		else
		{
			InfoPrinter.println("RpcPerformanceTest failed.");			
		}
	}
	
	private static class RpcPerformanceTestThread extends Thread
	{
		private JUnitTestInterface jUnitTestInterface;
		private CountDownLatch countDownLatch;
		
		public RpcPerformanceTestThread(JUnitTestInterface jUnitTestInterface,
				CountDownLatch countDownLatch)
		{
			this.jUnitTestInterface = jUnitTestInterface;
			this.countDownLatch = countDownLatch;
		}
		
		@Override
		public void run() 
		{
			for(int i=0; i<INVOKES; i++)
			{
				jUnitTestInterface.methodForPerformance();
				countDownLatch.countDown();
			}
		}
	}
}
