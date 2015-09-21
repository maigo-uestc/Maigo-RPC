package com.maigo.rpc.test;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.maigo.rpc.client.RpcClientProxyBuilder;

public class JUnitMultiThreadSafeTest 
{
	public static JUnitTestInterface jUnitTestInterface;
	public final static int THREADS = 20;
	public final static int INVOKES = 1000;
	public final static int TIMEOUT = 10000;
	
	@BeforeClass
	public static void init()
	{
		jUnitTestInterface = RpcClientProxyBuilder.create(JUnitTestInterface.class)
				.timeout(2000)
				.threads(4)
				.connect("127.0.0.1", 3721)
				.build();
	}

	@Test
	public void testMultiThreadSafe() 
	{
		ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
		CountDownLatch countDownLatch = new CountDownLatch(THREADS * INVOKES);
		for(int i=0; i<THREADS; i++)
		{
			threadPool.execute(new JUnitMultiThreadSafeTestThread(i, jUnitTestInterface, countDownLatch));
		}
		
		try 
		{
			if(countDownLatch.await(TIMEOUT, TimeUnit.MILLISECONDS))
				return;
			else
				fail("some thread did not finish all the invoke.");
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	private static class JUnitMultiThreadSafeTestThread extends Thread
	{
		private int threadId;
		private JUnitTestInterface jUnitTestInterface;
		private CountDownLatch countDownLatch;
		
		public JUnitMultiThreadSafeTestThread(int threadId, JUnitTestInterface jUnitTestInterface,
				CountDownLatch countDownLatch)
		{
			this.threadId = threadId;
			this.jUnitTestInterface = jUnitTestInterface;
			this.countDownLatch = countDownLatch;
		}
		
		@Override
		public void run() 
		{
			for(int i=0; i<INVOKES; i++)
			{
				int result = jUnitTestInterface.methodForMultiThread(threadId);
				if(result == threadId)
					countDownLatch.countDown();
			}
		}
	}
}
