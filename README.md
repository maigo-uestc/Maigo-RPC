# Maigo-RPC
A simple RPC framework base on Netty and KryoSerializer.


Usage
1.Start up the server.
    JUnitTestInterfaceImpl jUnitTestInterfaceImpl = new JUnitTestInterfaceImpl();
		RpcServer rpcServer = RpcServerBuilder.create()
				  .serviceInterface(JUnitTestInterface.class)
				  .serviceProvider(jUnitTestInterfaceImpl)
				  .threads(4)
				  .bind(3721)
				  .build();
		rpcServer.start();
	
2.Use in sync way - Build a client proxy
  jUnitTestInterface = RpcClientProxyBuilder.create(JUnitTestInterface.class)
												.timeout(2000)
												.threads(4)
												.hook(hook)
												.connect("127.0.0.1", 3721)
												.build();
												
	jUnitTestInterface.methodWithoutArg();
	jUnitTestInterface.methodWithArgs("age", 23);
	......(use the client proxy just as a simple proxy object)
	
3.Also support async way - Build an async client proxy
  RpcFuture can be used to get result or exception by call RpcFuture.get() or RpcFuture.setRpcFutureListener()

  rpcClientAsyncProxy = RpcClientProxyBuilder.create(JUnitTestInterface.class)
												.timeout(2000)
												.threads(4)
												.hook(hook)
												.connect("127.0.0.1", 3721)
												.buildAsyncProxy();
	
	RpcFuture rpcFuture = rpcClientAsyncProxy.call("methodDelayOneSecond");
	rpcFuture.setRpcFutureListener(new RpcFutureListener() 
		{			
			public void onResult(Object result) 
			{
				System.out.println("onResult:"+result);
			}
			
			public void onException(Throwable throwable) 
			{
				System.out.println("onException:"+throwable);
			}
		});
	String result = rpcFuture.get();
	
more usage can be seen in com.maigo.rpc.test package
