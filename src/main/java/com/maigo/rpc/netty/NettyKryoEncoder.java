package com.maigo.rpc.netty;

import com.maigo.rpc.serializer.KryoSerializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyKryoEncoder extends MessageToByteEncoder<Object>
{	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception 
	{
		KryoSerializer.serialize(msg, out);
	}
}
