package com.maigo.rpc.context;

public class RpcRequest 
{
	private final int id;
	private final String methodName;
	private final Object[] args;
	
	public RpcRequest(int id, String methodName, Object[] args) 
	{
		this.id = id;
		this.methodName = methodName;
		this.args = args;
	}

	public int getId() 
	{
		return id;
	}

	public String getMethodName() 
	{
		return methodName;
	}

	public Object[] getArgs() 
	{
		return args;
	}
}
