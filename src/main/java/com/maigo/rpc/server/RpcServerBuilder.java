package com.maigo.rpc.server;

import com.maigo.rpc.aop.RpcInvokeHook;

public class RpcServerBuilder 
{
	private Class<?> interfaceClass;
	private Object serviceProvider;
	
	private int port;	
	private int threads;
	private RpcInvokeHook rpcInvokeHook;
	
	public static RpcServerBuilder create()
	{
		return new RpcServerBuilder();
	}
	
	/**
	 * set the interface to provide service
	 * @param interfaceClass
	 */
	public RpcServerBuilder serviceInterface(Class<?> interfaceClass)
	{
		this.interfaceClass = interfaceClass;
		return this;
	}
	
	/**
	 * set the real object to provide service
	 */
	public RpcServerBuilder serviceProvider(Object serviceProvider)
	{
		this.serviceProvider = serviceProvider;
		return this;
	}
	
	/**
	 * set the port to bind
	 */
	public RpcServerBuilder bind(int port)
	{
		this.port = port;
		return this;
	}
	
	/**
	 * set the count of threads to handle request from client. (default availableProcessors)
	 */
	public RpcServerBuilder threads(int threadCount)
	{
		this.threads = threadCount;	
		return this;
	}
	
	/**
	 * set the hook of the method invoke in server
	 */
	public RpcServerBuilder hook(RpcInvokeHook rpcInvokeHook)
	{
		this.rpcInvokeHook = rpcInvokeHook;	
		return this;
	}
	
	public RpcServer build()
	{
		if(threads <= 0)
			threads = Runtime.getRuntime().availableProcessors();
				
		RpcServer rpcServer = new RpcServer(interfaceClass, serviceProvider, port, 
				threads, rpcInvokeHook);
		
		return rpcServer;
	}
}