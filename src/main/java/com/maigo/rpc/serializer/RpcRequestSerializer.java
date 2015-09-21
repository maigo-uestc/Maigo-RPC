package com.maigo.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.maigo.rpc.context.RpcRequest;

public class RpcRequestSerializer extends Serializer<RpcRequest>
{
	@Override
	public void write(Kryo kryo, Output output, RpcRequest object) 
	{
		output.writeInt(object.getId());
		output.writeByte(object.getMethodName().length());
		output.write(object.getMethodName().getBytes());
		kryo.writeClassAndObject(output, object.getArgs());
	}

	@Override
	public RpcRequest read(Kryo kryo, Input input, Class<RpcRequest> type) 
	{
		RpcRequest rpcRequest = null;
		int id = input.readInt();
		byte methodLength = input.readByte();
		byte[] methodBytes = input.readBytes(methodLength);
		String methodName = new String(methodBytes);
		Object[] args = (Object[])kryo.readClassAndObject(input);
		
		rpcRequest = new RpcRequest(id, methodName, args);
		
		return rpcRequest;
	}
}
