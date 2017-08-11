package com.nrt.basic;

import java.io.InputStream;
import java.io.IOException;

import com.nrt.helloworld.*;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
import com.nrt.math.Float4x4;


public class LoaderBase
{
	public InputStream Stream = null;
	public LoaderBase(InputStream stream)
	{
		Stream = stream;			
	}

	public void Close()
	{
		try
		{
			Stream.close();
		}
		catch ( IOException e )
		{
			return;
		}
	}

	public int ReadByte()
	{
		try
		{
			return Stream.read();
		}
		catch ( IOException e )
		{
			return 0;
		}
	}

	public byte[] ReadBytes(int size)
	{
		byte[] result = new byte[size];
		try
		{
			Stream.read(result);
		}
		catch ( IOException e )
		{
			result = null;
		}
		return result;
	}

	public int Read7BitEncodedInt()
	{
		int result = 0;
		for (int i = 0 ; i < 32 ; i += 7)
		{
			int v = ReadByte();
			result |= (v & 0x7f) << i;
			if ((v & 0x80) == 0)
			{
				break;
			}
		}

		return result;
	}

	public String ReadString()
	{
		int length = Read7BitEncodedInt();
		String result = "";

		for (int i = 0 ; i < length ; i++)
		{
			result += (char) ReadByte();
		}

		return result;
	}

	public int ReadInt32()
	{
		int result = 0;
		for (int i = 0 ; i < 32 ; i += 8)
		{
			result |= ReadByte() << i;
		}

		return result;
	}

	public short ReadUInt16()
	{
		int result = 0;
		for (int i = 0 ; i < 16 ; i += 8)
		{
			result |= ReadByte() << i;
		}

		return (short) result;
	}

	public boolean ReadBoolean()
	{
		return (ReadByte() != 0 ? true : false);
	}

	public float ReadSingle()
	{
		return Float.intBitsToFloat(ReadInt32());
	}

	public Float3 ReadFloat3()
	{
		float x = ReadSingle();
		float y = ReadSingle();
		float z = ReadSingle();
		return new Float3(x, y, z);
	}

	public Float4x4 ReadFloat4x4()
	{
		float[] values = new float[16];
		for (int i = 0; i < 16 ; i++)
		{
			values[i] = ReadSingle();
		}
		return new Float4x4(values);
	}

	public Float4 ReadFloat4()
	{
		float x = ReadSingle();
		float y = ReadSingle();
		float z = ReadSingle();
		float w = ReadSingle();
		return new Float4(x, y, z, w);			
	}
}
