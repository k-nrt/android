package com.nrt.model;

import com.nrt.render.*;

public class ModelMesh
{
	public static class AttributeFormat
	{
		public int Format = 0;
		public int ComponentCount = 0;
		public boolean Normalize = false;
		public int Offset = 0;
	}

	public String Name = "";
	public int Stride = 0;
	public AttributeFormat[] Formats = null;
	public byte[] Vertices = null;
	public short[] Indices = null;
	
	public VertexStream Stream = null;
	public StaticVertexBuffer VertexBuffer = null;
	public StaticIndexBuffer IndexBuffer = null;
}

