package com.maigo.rpc.context;

public class RpcResponse
{	
	private int id;
	private Object result;
	private Throwable throwable;
	private boolean isInvokeSuccess;

	public RpcResponse(int id, Object resultOrThrowable, boolean isInvokeSuccess)
    {
        this.id = id;
        this.isInvokeSuccess = isInvokeSuccess;

        if(isInvokeSuccess)
            result = resultOrThrowable;
        else
            throwable = (Throwable)resultOrThrowable;
    }
	
	public int getId() 
	{
		return id;
	}
	
	public Object getResult() 
	{
		return result;
	}
	
	public Throwable getThrowable() 
	{
		return throwable;
	}

	public boolean isInvokeSuccess()
	{
		return isInvokeSuccess;
	}
}
