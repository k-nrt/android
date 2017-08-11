package com.nrt.vmath;
import java.nio.*;

public class Local
{
	public static ByteBuffer s_locals = ByteBuffer.allocateDirect(1024*1024);
	public static int s_pos = 0;

	public static int Push()
	{		
		return s_pos;
	}

	public static void Pop(int pos)
	{
		s_pos = pos;
	}

	public static int Local(int nbQWords)
	{
		int next = s_pos+nbQWords*16;
		if( s_locals.limit() < next )
		{
			s_pos = 0;
			return 0;
		}
		
		int result = s_pos;
		s_pos = next;
		return result;
	}
}

