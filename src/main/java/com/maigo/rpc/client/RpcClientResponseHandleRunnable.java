package com.maigo.rpc.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import com.maigo.rpc.context.RpcResponse;
import com.maigo.rpc.future.RpcFuture;

public class RpcClientResponseHandleRunnable implements Runnable
{
	private ConcurrentMap<Integer, RpcFuture> invokeIdRpcFutureMap;
	private BlockingQueue<RpcResponse> responseQueue;
	
	public RpcClientResponseHandleRunnable(
			ConcurrentMap<Integer, RpcFuture> invokeIdRpcFutureMap,
			BlockingQueue<RpcResponse> responseQueue) 
	{
		this.invokeIdRpcFutureMap = invokeIdRpcFutureMap;
		this.responseQueue = responseQueue;
	}

	public void run() 
	{
		while(true)
		{
			try 
			{
				RpcResponse rpcResponse = responseQueue.take();
				
				int id = rpcResponse.getId();
				RpcFuture rpcFuture = invokeIdRpcFutureMap.remove(id);
				
				if(rpcResponse.isInvokeSuccess())				
					rpcFuture.setResult(rpcResponse.getResult());	
				else
					rpcFuture.setThrowable(rpcResponse.getThrowable());				
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
