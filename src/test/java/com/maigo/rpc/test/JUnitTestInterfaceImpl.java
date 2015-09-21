package com.maigo.rpc.test;

import java.util.Arrays;
import java.util.List;

public class JUnitTestInterfaceImpl implements JUnitTestInterface
{
	public String methodWithoutArg() 
	{
		return "this is return from methodWithoutArg()";
	}

	public String methodWithArgs(String arg1, int arg2) 
	{
		return arg1 + " = " + arg2;
	}

	public JUnitTestCustomObject methodWithCustomObject(
			JUnitTestCustomObject customObject) 
	{
		JUnitTestCustomObject object = new JUnitTestCustomObject(customObject.getString() + " after", 
				customObject.getI() + 47);
		return object;
	}

	public List<String> methodReturnList(String arg1, String arg2) 
	{
		return Arrays.asList(arg1, arg2);
	}

	public void methodThrowException()
	{
		throw new JUnitTestCustomException();
	}

	public void methodTimeOut() 
	{
		try 
		{
			Thread.sleep(5000);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}

	public void methodReturnVoid() 
	{
		return;
	}

	public String methodDelayOneSecond() 
	{
		try 
		{
			Thread.sleep(1000);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		return "I have sleep 1000ms already.";
	}

	public int methodForMultiThread(int threadId) 
	{		
		return threadId;
	}

	public String methodForPerformance() 
	{
		return "Maigo";
	}
}
