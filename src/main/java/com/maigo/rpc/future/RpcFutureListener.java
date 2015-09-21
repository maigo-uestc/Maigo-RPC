package com.maigo.rpc.future;

public interface RpcFutureListener 
{
	public void onResult(Object result);
	public void onException(Throwable throwable);
}
