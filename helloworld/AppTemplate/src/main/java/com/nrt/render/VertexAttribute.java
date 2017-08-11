package com.nrt.render;

public class VertexAttribute
{
	public int Index = 0;
	public int Components = 0;
	public int Format = 0;
	public boolean Normalize = false;
	public int Offset = 0;

	public VertexAttribute()
	{}
	public VertexAttribute(
		int index, int nbComponents,
		EVertexAttributeFormat format, boolean isNormalize,
		int nOffset)
	{
		Index = index;
		Components = nbComponents;
		Format = format.Value;
		Normalize = isNormalize;
		Offset = nOffset;
	}
}

