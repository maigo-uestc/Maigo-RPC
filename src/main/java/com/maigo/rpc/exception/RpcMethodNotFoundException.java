package com.maigo.rpc.exception;

public class RpcMethodNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = -1605324441723957563L;
	
	public RpcMethodNotFoundException(String methodName)
	{
		super(methodName + " is not found in current service interface!");
	}
}
