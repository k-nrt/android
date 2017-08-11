package com.nrt.font;

public class FontCharInfo
{
	public float U = 0;
	public float V = 0;
	public float Width = 0;
	public int Channel = 0;
	public int Texture = 0;

	public FontCharInfo( float u, float v, float width, int channel, int texture )
	{
		U = u;
		V = v;
		Width = width;
		Channel = channel;
		Texture = texture;
	}
}

