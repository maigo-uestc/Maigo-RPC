package com.maigo.rpc.aop;

/**
 * Invoke when RpcClient or RpcServer invoke a method. It is used 
 * for such as count the invoke time or save the invoke log.
 */
public interface RpcInvokeHook 
{
	public void beforeInvoke(String methodName, Object[] args);
	public void afterInvoke(String methodName, Object[] args);	
}
