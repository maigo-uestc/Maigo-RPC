package com.maigo.rpc.test;

public class JUnitTestCustomException extends RuntimeException
{
	private static final long serialVersionUID = 591530421634999576L;

	public JUnitTestCustomException()
	{
		super("CustomException");
	}
}
