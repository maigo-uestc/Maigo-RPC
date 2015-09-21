package com.maigo.rpc.client;

import com.maigo.rpc.context.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class RpcClientDispatchHandler extends ChannelInboundHandlerAdapter
{
	private RpcClientResponseHandler rpcClientResponseHandler;
	private RpcClientChannelInactiveListener rpcClientChannelInactiveListener = null;
	
	public RpcClientDispatchHandler(
			RpcClientResponseHandler rpcClientResponseHandler, 
			RpcClientChannelInactiveListener rpcClientChannelInactiveListener) 
	{
		this.rpcClientResponseHandler = rpcClientResponseHandler;
		this.rpcClientChannelInactiveListener = rpcClientChannelInactiveListener;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception 
	{		
		RpcResponse rpcResponse = (RpcResponse)msg;
		rpcClientResponseHandler.addResponse(rpcResponse);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception 
	{
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception 
	{
		if(rpcClientChannelInactiveListener != null)
			rpcClientChannelInactiveListener.onInactive();
	}	
}
