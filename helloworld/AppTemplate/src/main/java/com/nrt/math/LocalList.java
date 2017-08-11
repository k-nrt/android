package com.nrt.math;

public class LocalList<_tType>
{
	public _tType[] Values = null;
	public int Position = 0;
	
	public LocalList( _tType[] values )
	{
		Values = values;
		Position = 0;
	}

	public _tType Local()
	{
		_tType result = Values[Position];
		Position++;
		if( Position >= Values.length )
		{
			Position = 0;
		}
		return result;
	}
}

