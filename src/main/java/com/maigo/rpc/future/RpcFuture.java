package com.maigo.rpc.future;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.maigo.rpc.exception.RpcTimeoutException;

public class RpcFuture 
{
	public final static int STATE_AWAIT = 0;
	public final static int STATE_SUCCESS = 1;
	public final static int STATE_EXCEPTION = 2;
	
	private CountDownLatch countDownLatch;
	private Object result;
	private Throwable throwable;
	private int state;
	private RpcFutureListener rpcFutureListener = null;
	
	public RpcFuture()
	{
		countDownLatch = new CountDownLatch(1);
		state = STATE_AWAIT;
	}
	
	public Object get() throws Throwable
	{
		try 
		{
			countDownLatch.await();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		if(state == STATE_SUCCESS)
			return result;
		else if(state == STATE_EXCEPTION)
			throw throwable;
		else //should not run to here!
			throw new RuntimeException("RpcFuture Exception!");
	}
	
	public Object get(long timeout) throws Throwable
	{
		boolean awaitSuccess = true;
		try 
		{
			awaitSuccess = countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		if(!awaitSuccess)
			throw new RpcTimeoutException();
		
		if(state == STATE_SUCCESS)
			return result;
		else if(state == STATE_EXCEPTION)
			throw throwable;
		else //should not run to here!
			throw new RuntimeException("RpcFuture Exception!");
	}
	
	/**
	 * get result successfully
	 * @param result
	 */
	public void setResult(Object result) 
	{
		this.result = result;
		state = STATE_SUCCESS;
		
		if(rpcFutureListener != null)
			rpcFutureListener.onResult(result);
		
		countDownLatch.countDown();
	}
	
	/**
	 * exception occur when invoke
	 * @param throwable
	 */
	public void setThrowable(Throwable throwable) 
	{
		this.throwable = throwable;
		state = STATE_EXCEPTION;
		
		if(rpcFutureListener != null)
			rpcFutureListener.onException(throwable);
		
		countDownLatch.countDown();
	}
	
	public boolean isDone()
	{
		return state != STATE_AWAIT;
	}
	
	public void setRpcFutureListener(RpcFutureListener rpcFutureListener) 
	{		
		if(state != STATE_AWAIT)
			throw new RuntimeException("unable to set listener to a RpcFuture which is done.");
		
		this.rpcFutureListener = rpcFutureListener;
	}
}
