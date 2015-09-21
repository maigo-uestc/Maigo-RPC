package com.maigo.rpc.context;

public class RpcResponse 
{	
	private int id;
	private Object result;
	private Throwable throwable;
	private boolean isInvokeSuccess;
	
	public int getId() 
	{
		return id;
	}
	
	public void setId(int id) 
	{
		this.id = id;
	}
	
	public Object getResult() 
	{
		return result;
	}
	
	public void setResult(Object result) 
	{
		this.result = result;
	}
	
	public Throwable getThrowable() 
	{
		return throwable;
	}
	
	public void setThrowable(Throwable throwable) 
	{
		this.throwable = throwable;
	}
	
	public boolean isInvokeSuccess() 
	{
		return isInvokeSuccess;
	}
	
	public void setInvokeSuccess(boolean isInvokeSuccess) 
	{
		this.isInvokeSuccess = isInvokeSuccess;
	}

}
