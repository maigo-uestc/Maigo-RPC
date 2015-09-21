package com.maigo.rpc.serializer;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

public class KryoSerializer
{
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
	
	public static void serialize(Object object, ByteBuf byteBuf) 
	{
		Kryo kryo = KryoHolder.get();
		int startIdx = byteBuf.writerIndex();
        ByteBufOutputStream byteOutputStream = new ByteBufOutputStream(byteBuf);
        try 
        {
			byteOutputStream.write(LENGTH_PLACEHOLDER);
			Output output = new Output(1024*4, -1);
	        output.setOutputStream(byteOutputStream);
	        kryo.writeClassAndObject(output, object);
	        
	        output.flush();
	        output.close();
	        
	        int endIdx = byteBuf.writerIndex();

	        byteBuf.setInt(startIdx, endIdx - startIdx - 4);
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	}

	public static Object deserialize(ByteBuf byteBuf) 
	{
		if(byteBuf == null)
            return null;
		
        Input input = new Input(new ByteBufInputStream(byteBuf));
        Kryo kryo = KryoHolder.get();
        return kryo.readClassAndObject(input);
	}	
}