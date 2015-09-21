package com.maigo.rpc.client;

import java.lang.reflect.Proxy;

import com.maigo.rpc.aop.RpcInvokeHook;

public class RpcClientProxyBuilder 
{
	public static class ProxyBuilder<T>
	{		
		private Class<T> clazz;
		private RpcClient rpcClient;
		
		private long timeoutMills = 0;
		private RpcInvokeHook rpcInvokeHook = null;	
		private String host;
		private int port;
		private int threads;
		
		private ProxyBuilder(Class<T> clazz)
		{
			this.clazz = clazz;
		}
		
		/**
		 * timeout time in mills. Set to 0 means no timeout and keep waiting for
		 * result. Only works in synchronous way. (default 0)
		 */
		public ProxyBuilder<T> timeout(long timeoutMills)
		{	
			this.timeoutMills = timeoutMills;	
			if(timeoutMills < 0)
				throw new IllegalArgumentException("timeoutMills can not be minus!");
			
			return this;
		}
		
		/**
		 * set the RpcInvokeHook which will be invoke when the proxy object invoke
		 * a method. (default null)
		 */
		public ProxyBuilder<T> hook(RpcInvokeHook hook)
		{	
			this.rpcInvokeHook = hook;			
			return this;
		}
		
		/**
		 * set the IP address and port of the RpcServer. Note that this method will
		 * only set the value but do not connect immediately. Connection will be done in
		 * build() or buildAsyncProxy().
		 */
		public ProxyBuilder<T> connect(String host, int port)
		{	
			this.host = host;		
			this.port = port;
			return this;
		}
		
		/**
		 * set the count of threads to handle request from client. (default availableProcessors)
		 */
		public ProxyBuilder<T> threads(int threadCount)
		{
			this.threads = threadCount;	
			return this;
		}
		
		/**
		 * build the synchronous proxy.In synchronous way, Thread will be blocked until 
		 * get the result or timeout.
		 */
		@SuppressWarnings("unchecked")
		public T build()
		{
			if(threads <= 0)
				threads = Runtime.getRuntime().availableProcessors();
			
			rpcClient = new RpcClient(timeoutMills, rpcInvokeHook, host, port, threads);
			rpcClient.connect();
			
			return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, rpcClient);
		}
		
		/**
		 * build the asynchronous proxy.In asynchronous way, a RpcFuture will be 
		 * return immediately.
		 */
		public RpcClientAsyncProxy buildAsyncProxy()
		{
			if(threads <= 0)
				threads = Runtime.getRuntime().availableProcessors();
			
			rpcClient = new RpcClient(timeoutMills, rpcInvokeHook, host, port, threads);
			rpcClient.connect();
			
			return new RpcClientAsyncProxy(rpcClient, clazz);			
		}
	}
	
	public static <T> ProxyBuilder<T> create(Class<T> targetClass)
	{
		return new ProxyBuilder<T>(targetClass);
	}
}
