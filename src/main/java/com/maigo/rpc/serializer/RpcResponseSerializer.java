package com.maigo.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.maigo.rpc.context.RpcResponse;

public class RpcResponseSerializer extends Serializer<RpcResponse> 
{
	@Override
	public void write(Kryo kryo, Output output, RpcResponse object) 
	{
		output.writeInt(object.getId());
		output.writeBoolean(object.isInvokeSuccess());
		if(object.isInvokeSuccess())
			kryo.writeClassAndObject(output, object.getResult());
		else
			kryo.writeClassAndObject(output, object.getThrowable());
	}

	@Override
	public RpcResponse read(Kryo kryo, Input input, Class<RpcResponse> type) 
	{
		int id = input.readInt();
		boolean isInvokeSuccess = input.readBoolean();
		Object resultOrThrowable = kryo.readClassAndObject(input);

		RpcResponse rpcResponse = new RpcResponse(id, resultOrThrowable, isInvokeSuccess);
		
		return rpcResponse;
	}
}
