package com.nrt.render;

public class GfxCommand
{
	public static interface Processor
	{
		public void OnCommand( Render r, GfxCommand c, GfxCommandBuffer cb );
	}

	public Processor Processor = null;
	public int Integers = 0;
	public int Floats = 0;
	public int Objects = 0;
	
	public GfxCommand()
	{}

	public void Store( Processor processor,  int integerPosition, int floatPosition, int objectPosition )
	{
		Processor = processor;
		Integers = integerPosition;
		Floats= floatPosition;
		Objects = objectPosition;
	}	
}
