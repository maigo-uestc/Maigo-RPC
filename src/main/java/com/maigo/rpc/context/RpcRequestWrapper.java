package com.maigo.rpc.context;

import io.netty.channel.Channel;

/**
 * wrap the RpcRequest and add a Channel field to keep the channel which this request is from
 */
public class RpcRequestWrapper
{
	private RpcRequest rpcRequest;
	private Channel channel;
	
	public RpcRequestWrapper(RpcRequest rpcRequest, Channel channel) 
	{
		this.rpcRequest = rpcRequest;
		this.channel = channel;
	}

	public int getId() 
	{
		return rpcRequest.getId();
	}

	public String getMethodName() 
	{
		return rpcRequest.getMethodName();
	}

	public Object[] getArgs() 
	{
		return rpcRequest.getArgs();
	}
	
	public Channel getChannel() 
	{
		return channel;
	}
}
