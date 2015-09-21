package com.maigo.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import com.maigo.rpc.aop.RpcInvokeHook;
import com.maigo.rpc.context.RpcRequest;
import com.maigo.rpc.future.RpcFuture;
import com.maigo.rpc.netty.NettyKryoDecoder;
import com.maigo.rpc.netty.NettyKryoEncoder;
import com.maigo.rpc.utils.InfoPrinter;

public class RpcClient implements InvocationHandler
{
	private long timeoutMills = 0;
	private RpcInvokeHook rpcInvokeHook = null;	
	private String host;
	private int port;
	
	private RpcClientResponseHandler rpcClientResponseHandler;
	private AtomicInteger invokeIdGenerator = new AtomicInteger(0);
	private Bootstrap bootstrap;
	
	/**
	 * channel that connected with the server
	 */
	private Channel channel;
	private RpcClientChannelInactiveListener rpcClientChannelInactiveListener;
	
	protected RpcClient(long timeoutMills, RpcInvokeHook rpcInvokeHook, String host, int port,
			int threads) 
	{
		this.timeoutMills = timeoutMills;
		this.rpcInvokeHook = rpcInvokeHook;
		this.host = host;
		this.port = port;	
		
		rpcClientResponseHandler = new RpcClientResponseHandler(threads);
		rpcClientChannelInactiveListener = new RpcClientChannelInactiveListener() 
		{			
			public void onInactive() 
			{
				InfoPrinter.println("connection with server is closed.");
				InfoPrinter.println("try to reconnect to the server.");
				channel = null;
				do
	            {
	            	channel = tryConnect();
	            }
	            while(channel == null);
			}
		};
	}
	
	public RpcFuture call(String methodName, Object ... args)
	{
		if(rpcInvokeHook != null)
			rpcInvokeHook.beforeInvoke(methodName, args);
		
		RpcFuture rpcFuture = new RpcFuture();
		int id = invokeIdGenerator.addAndGet(1);
		rpcClientResponseHandler.register(id, rpcFuture);
		
		RpcRequest rpcRequest = new RpcRequest(id, methodName, args);
		if(channel != null)
			channel.writeAndFlush(rpcRequest);
		else
			return null;
		
		return rpcFuture;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable 
	{	
		RpcFuture rpcFuture = call(method.getName(), args);
		if(rpcFuture == null)
		{
			InfoPrinter.println("RpcClient is unavailable when disconnect with the server.");
			return null;
		}
		
		Object result;
		if(timeoutMills == 0)
			result = rpcFuture.get();
		else
			result = rpcFuture.get(timeoutMills);
		
		if(rpcInvokeHook != null)
			rpcInvokeHook.afterInvoke(method.getName(), args);
		
		return result;
	}

	public void connect()
	{
		bootstrap = new Bootstrap();
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try 
        {
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                     .handler(new ChannelInitializer<Channel>() 
                     {
                    	 @Override
                    	 protected void initChannel(Channel ch) throws Exception 
                    	 {
                    		ch.pipeline().addLast(new NettyKryoDecoder(), 
                    				new RpcClientDispatchHandler(rpcClientResponseHandler, rpcClientChannelInactiveListener), 
                    				new NettyKryoEncoder());
                    	 }
					 });
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
          
            do
            {
            	channel = tryConnect();
            }
            while(channel == null);
            
        } 
        catch(Exception e)
        {
        	e.printStackTrace();
        }				
	}
	
	private Channel tryConnect()
	{		
		try 
		{
			InfoPrinter.println("Try to connect to [" + host + ":" + port + "].");
			ChannelFuture future = bootstrap.connect(host, port).sync();
			if(future.isSuccess())
			{
				InfoPrinter.println("Connect to [" + host + ":" + port + "] successed.");
				return future.channel();
			}
			else
			{				
				InfoPrinter.println("Connect to [" + host + ":" + port + "] failed.");
				InfoPrinter.println("Try to reconnect in 10s.");
				Thread.sleep(10000);
				return null;
			}
		} 
		catch (Exception exception) 
		{
			InfoPrinter.println("Connect to [" + host + ":" + port + "] failed.");
			InfoPrinter.println("Try to reconnect in 10 seconds.");
			try 
			{
				Thread.sleep(10000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			return null;
		}
	}
}
