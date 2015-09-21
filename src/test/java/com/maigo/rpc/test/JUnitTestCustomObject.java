package com.maigo.rpc.test;

public class JUnitTestCustomObject 
{
	private String string;
	private int i;
		
	public JUnitTestCustomObject(String string, int i) 
	{
		super();
		this.string = string;
		this.i = i;
	}
	
	public String getString() 
	{
		return string;
	}

	public void setString(String string) 
	{
		this.string = string;
	}

	public int getI() 
	{
		return i;
	}

	public void setI(int i) 
	{
		this.i = i;
	}

	@Override
	public boolean equals(Object obj) 
	{
		JUnitTestCustomObject object = null;
		if(obj instanceof JUnitTestCustomObject)
			object = (JUnitTestCustomObject)obj;
		else
			return false;
		
		return (this.string.equals(object.string)) && (this.i == object.i);
	}
}
