package com.nrt.render;

public interface VertexBuffer
{
	public int GetVertexBufferName();
	/*
	public VertexBuffer(ByteBuffer buffer, Buffer.EUsage eUsage )
	{
		super( Buffer.EType.Vertex, buffer, eUsage );
	}
	*/
	/*
	public VertexBuffer(VertexAttribute[] arrayAttributes, int nStride, float[] data)
	{
		Stream = new VertexStream(arrayAttributes, nStride);

		CountComponents();

		int[] names = { 0 };

		// ハードウェア側の準備
		GLES20.glGenBuffers(1, names, 0);
		Name = names[0];

		if (data != null)
		{
			SetData(data);
		}
	}
	*/
	/*
	 private void CountStride()
	 {
	 Stride = 0;
	 for (int i = 0; i < VertexAttributes.length ; i++)
	 {
	 VertexAttribute va = VertexAttributes[i];
	 switch (va.Format)
	 {
	 case GLES20.GL_BYTE:
	 case GLES20.GL_UNSIGNED_BYTE:
	 Stride += 4;
	 break;

	 case GLES20.GL_UNSIGNED_INT:
	 Stride += 4 * va.Components;
	 break;

	 case GLES20.GL_FLOAT:
	 Stride += 4 * va.Components;
	 break;
	 }
	 }
	 }
	 */
	 
	 /*
	private void CountComponents()
	{
		m_nbComponents = 0;
		for (int i = 0 ; i < Stream.VertexAttributes.length ; i++)
		{
			VertexAttribute va = Stream.VertexAttributes[i];
			m_nbComponents += va.Components;
		}
	}

	public void SetData(float[] arrayFloats)
	{
		Vertices = arrayFloats.length / m_nbComponents;
		ByteBuffer buffer = ByteBuffer.allocateDirect(Vertices * Stream.Stride);
		buffer.order(ByteOrder.nativeOrder());
		byte[] data = new byte[Vertices * Stream.Stride];
		int i = 0;
		for (int v = 0 ; v < Vertices ; v++)
		{
			for (int a = 0 ; a < Stream.VertexAttributes.length ; a++)
			{
				VertexAttribute va = Stream.VertexAttributes[a];
				int offset = v * Stream.Stride + va.Offset;

				for (int c = 0;c < va.Components;c++)
				{
					float f = arrayFloats[i];
					i++;
					switch (va.Format)
					{
						case GLES20.GL_BYTE:
							{
								//buffer.position(offset);
								//buffer.put((byte)f);
								data[offset] = (byte) f;
								offset++;
							}
							break;

						case GLES20.GL_UNSIGNED_BYTE:
							{
								//buffer.position(offset);
								//buffer.put(offset, (byte) (0xff & (int) f) );
								//buffer.put((byte)0xff);
								data[offset] = (byte) (0xff & (int) f);
								offset++;
							}
							break;

						case GLES20.GL_UNSIGNED_INT:
							{
								int iv = (int) f;
								data[offset] = (byte)(iv & 0xff);
								data[offset + 1] = (byte)((iv >> 8) & 0xff);
								data[offset + 2] = (byte)((iv >> 16) & 0xff);
								data[offset + 3] = (byte)((iv >> 24) & 0xff);
								offset += 4;
							}
							break;

						case GLES20.GL_FLOAT:
							{
								//buffer.putFloat(offset, f);
								int iv = Float.floatToIntBits(f);
								data[offset] = (byte)(iv & 0xff);
								data[offset + 1] = (byte)((iv >> 8) & 0xff);
								data[offset + 2] = (byte)((iv >> 16) & 0xff);
								data[offset + 3] = (byte)((iv >> 24) & 0xff);
								offset += 4;
							}
							break;
					}
				}
			}
		}

		buffer.put(data);
		buffer.position(0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Name);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, buffer.capacity(), buffer, GLES20.GL_STATIC_DRAW);
	}
	*/
}
