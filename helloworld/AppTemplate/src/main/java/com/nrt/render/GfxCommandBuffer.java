package com.nrt.render;

import com.nrt.math.Float4;
import com.nrt.math.Float3;

public class GfxCommandBuffer
{
	public GfxCommand[] Commands = null;
	public int[] Integers = null;
	public float[] Floats = null;
	public Object[] Objects = null;

	int m_nbExpandCommands = 0;
	int m_nbExpandIntegers = 0;
	int m_nbExpandFloats = 0;
	int m_nbExpandObjects = 0;

	public int CommandPosition = 0;
	public int IntegerPosition = 0;
	public int FloatPosition = 0;
	public int ObjectPosition = 0;

	public GfxCommandBuffer()
	{
	}
	
	public GfxCommandBuffer
			(
					int nbInitialCommands, int nbExpandCommands,
					int nbInitialIntegers, int nbExpandIntegers,
					int nbInitialFloats, int nbExpandFloats,
					int nbInitialObjects, int nbExpandObjects
			)
	{
		Commands = new GfxCommand[nbInitialCommands];
		for(int i = 0 ; i < Commands.length ; i++ )
		{
			Commands[i] = new GfxCommand();
		}
		m_nbExpandCommands = nbExpandCommands;

		Integers = new int[nbInitialIntegers];
		m_nbExpandIntegers = m_nbExpandIntegers;

		Floats = new float[nbInitialFloats];
		m_nbExpandFloats = nbExpandFloats;

		Objects = new Object[nbInitialObjects];
		m_nbExpandObjects = nbExpandObjects;
	}

	public final void Rewind()
	{
		CommandPosition = 0;
		IntegerPosition = 0;
		FloatPosition = 0;
		ObjectPosition = 0;
	}

	public final void AppendCommand( final GfxCommand.Processor processor  )
	{
		if( Commands.length <= CommandPosition )
		{
			GfxCommand[] commands = new GfxCommand[Commands.length+m_nbExpandCommands];
			for(int i=0;i<Commands.length;i++)
			{
				commands[i] = Commands[i];
			}
			for(int i=Commands.length;i<Commands.length+m_nbExpandCommands;i++)
			{
				commands[i] = new GfxCommand();
			}
			Commands = null;
			Commands = commands;
		}
		Commands[CommandPosition].Processor = processor;
		Commands[CommandPosition].Integers = IntegerPosition;
		Commands[CommandPosition].Floats = FloatPosition;
		Commands[CommandPosition].Objects = ObjectPosition;
		CommandPosition++;
	}

	public final void AppendInteger(int value)
	{
		if(Integers.length <= IntegerPosition)
		{
			int[] integers = new int[Integers.length+m_nbExpandIntegers];
			java.lang.System.arraycopy(Integers,0,integers,0,Integers.length);
			Integers = null;
			Integers = integers;
		}
		Integers[IntegerPosition] = value;
		IntegerPosition++;
	}

	public final void AppendFloat(float value)
	{
		if(Floats.length <= FloatPosition)
		{
			float[] floats = new float[Floats.length+m_nbExpandFloats];
			java.lang.System.arraycopy(Floats,0,floats,0,Floats.length);
			Floats = null;
			Floats = floats;
		}
		Floats[FloatPosition] = value;
		FloatPosition++;
	}

	public final void AppendObject(final Object obj)
	{
		if(Objects.length <= ObjectPosition)
		{
			Object[] objects = new Object[Objects.length+m_nbExpandObjects];
			for(int i=0;i<Objects.length;i++)
			{
				objects[i] = Objects[i];
			}
			Objects = null;
			Objects = objects;
		}
		Objects[ObjectPosition] = obj;
		ObjectPosition++;
	}

	public void ProcessCommands( Render r, int start, int end )
	{
		if(Commands == null )
		{
			return;
		}

		if( end < 0 || CommandPosition < end)
		{
			end = CommandPosition;
		}

		for( int i = start ; i < end ; i++ )
		{
			final GfxCommand c = Commands[i];
			if( c.Processor == null )
			{
				break;
			}
			c.Processor.OnCommand(r,c, this);
		}
	}

}
