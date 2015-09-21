package com.maigo.rpc.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.maigo.rpc.server.RpcServer;
import com.maigo.rpc.server.RpcServerBuilder;

public class JUnitServerTest 
{
	@Test
	public void testServerStart() 
	{
		JUnitTestInterfaceImpl jUnitTestInterfaceImpl = new JUnitTestInterfaceImpl();
		RpcServer rpcServer = RpcServerBuilder.create()
				  .serviceInterface(JUnitTestInterface.class)
				  .serviceProvider(jUnitTestInterfaceImpl)
				  .threads(4)
				  .bind(3721)
				  .build();
		rpcServer.start();
	}
	
	public static void main(String[] args) 
	{
		new JUnitServerTest().testServerStart();
	}
}
