package com.maigo.rpc.test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;

import com.maigo.rpc.aop.RpcInvokeHook;
import com.maigo.rpc.client.RpcClientAsyncProxy;
import com.maigo.rpc.client.RpcClientProxyBuilder;
import com.maigo.rpc.exception.RpcMethodNotFoundException;
import com.maigo.rpc.exception.RpcTimeoutException;
import com.maigo.rpc.future.RpcFuture;
import com.maigo.rpc.future.RpcFutureListener;

public class JUnitFunctionTest 
{
	/**
	 * use for sync-mode test
	 */
	public static JUnitTestInterface jUnitTestInterface;
	
	/**
	 * use for async-mode test
	 */
	public static RpcClientAsyncProxy rpcClientAsyncProxy;
	
	private static int integerBeforeHook = 0;
	private static int integerAfterHook = 0;
	private CountDownLatch countDownLatch;
	
	@BeforeClass
	public static void init()
	{
		RpcInvokeHook hook = new RpcInvokeHook() 
		{			
			public void beforeInvoke(String methodName, Object[] args) 
			{
				integerBeforeHook++;
			}
			
			public void afterInvoke(String methodName, Object[] args) 
			{
				integerAfterHook++;
			}
		};
		
		jUnitTestInterface = RpcClientProxyBuilder.create(JUnitTestInterface.class)
												.timeout(2000)
												.threads(4)
												.hook(hook)
												.connect("127.0.0.1", 3721)
												.build();
		
		rpcClientAsyncProxy = RpcClientProxyBuilder.create(JUnitTestInterface.class)
												.timeout(2000)
												.threads(4)
												.hook(hook)
												.connect("127.0.0.1", 3721)
												.buildAsyncProxy();
	}
	
	@Test
	public void testMethodWithoutArg() 
	{
		assertEquals("this is return from methodWithoutArg()", 
				jUnitTestInterface.methodWithoutArg());
	}
	
	@Test
	public void testMethodWithArgs() 
	{
		assertEquals("age = 23", jUnitTestInterface.methodWithArgs("age", 23));
		assertEquals("born = 1992", jUnitTestInterface.methodWithArgs("born", 1992));
	}
	
	@Test
	public void methodWithCustomObject() 
	{
		JUnitTestCustomObject beforeCustomObject = new JUnitTestCustomObject("before", 3);
		JUnitTestCustomObject afterCustomObject = 
				jUnitTestInterface.methodWithCustomObject(beforeCustomObject);
		assertEquals("before after", afterCustomObject.getString());
		assertEquals(50, afterCustomObject.getI());
	}
	
	@Test
	public void testMethodReturnList() 
	{
		List<String> list = jUnitTestInterface.methodReturnList("hello", "world");
		assertEquals("hello", list.get(0));
		assertEquals("world", list.get(1));
	}
	
	@Test(expected=JUnitTestCustomException.class)
	public void testMethodThrowException() 
	{
		jUnitTestInterface.methodThrowException();	
	}
	
	@Test(expected=RpcTimeoutException.class)
	public void testMethodTimeOut() 
	{
		jUnitTestInterface.methodTimeOut();	
	}
	
	@Test
	public void testMethodReturnVoid() 
	{
		jUnitTestInterface.methodReturnVoid();
	}
	
	@Test
	public void testMethodHook() 
	{
		integerBeforeHook = 100;
		integerAfterHook = 500;
		jUnitTestInterface.methodWithoutArg();
		assertEquals(101, integerBeforeHook);
		assertEquals(501, integerAfterHook);
	}
	
	@Test
	public void testFutureSuccess()
	{
		RpcFuture rpcFuture = rpcClientAsyncProxy.call("methodDelayOneSecond");
		assertNotNull(rpcFuture);
		assertFalse(rpcFuture.isDone());
		
		try 
		{
			Thread.sleep(2000);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		assertTrue(rpcFuture.isDone());
		try 
		{
			assertEquals("I have sleep 1000ms already.", rpcFuture.get());
		}
		catch (Throwable e) 
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFutureError()
	{
		RpcFuture rpcFuture = rpcClientAsyncProxy.call("methodThrowException");
		assertNotNull(rpcFuture);	
		try 
		{
			rpcFuture.get();
		} 
		catch (Throwable e) 
		{
			if(e instanceof JUnitTestCustomException)
				return;
			
			fail(e + " was caught when testFutureError.");
		}
		
		fail("failed to catch JUnitTestCustomException.");
	}
	
	@Test
	public void testFutureListener()
	{
		//1.test for get a result by RpcFutureListener
		countDownLatch = new CountDownLatch(1);
		
		RpcFuture rpcFuture = rpcClientAsyncProxy.call("methodDelayOneSecond");
		assertNotNull(rpcFuture);
		rpcFuture.setRpcFutureListener(new RpcFutureListener() 
		{			
			public void onResult(Object result) 
			{
				countDownLatch.countDown();
			}
			
			public void onException(Throwable throwable) 
			{
				fail(throwable + " was caught when testFutureListener.");
			}
		});
		
		try 
		{
			if(!countDownLatch.await(2000, TimeUnit.MILLISECONDS))
				fail("failed to get result by RpcFutureListener.");
		} 
		catch (InterruptedException e) 
		{
			fail(e + " was caught when testFutureListener.");
		}
		
		
		//2.test for get a result by RpcFutureListener
		countDownLatch = new CountDownLatch(1);
		rpcFuture = rpcClientAsyncProxy.call("methodThrowException");
		assertNotNull(rpcFuture);
		rpcFuture.setRpcFutureListener(new RpcFutureListener() 
		{			
			public void onResult(Object result) 
			{
				fail("failed to catch JUnitTestCustomException.");			
			}
			
			public void onException(Throwable throwable) 
			{
				if(throwable instanceof JUnitTestCustomException)
					countDownLatch.countDown();
				else
					fail("failed to catch JUnitTestCustomException.");
			}
		});
		
		try 
		{
			if(!countDownLatch.await(1000, TimeUnit.MILLISECONDS))
				fail("failed to get exception by RpcFutureListener.");
		} 
		catch (InterruptedException e) 
		{
			fail(e + " was caught when testFutureListener.");
		}
	}
	
	@Test(expected=RpcMethodNotFoundException.class)
	public void testMethodNotFound()
	{
		rpcClientAsyncProxy.call("methodWhichIsNotExist");
	}
}
