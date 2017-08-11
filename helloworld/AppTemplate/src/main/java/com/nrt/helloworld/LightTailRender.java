package com.nrt.helloworld;

import java.util.*;
import com.nrt.clipper.*;
import com.nrt.framework.*;
import com.nrt.math.*;
import com.nrt.render.*;


class VertexBuilder
{
	public List<Byte> m_listBuffer = new ArrayList<Byte>();
	public int Stride = -1;
	
	
	
	public void Put( Object...args )
	{
		
		int nStride = 0;
		for( Object arg : args )
		{
			if( arg instanceof Byte )
			{
				PutByte( (Byte) arg );
				nStride += 1;
			}
			else if( arg instanceof Short )
			{
				PutShort( (Short) arg );
				nStride += 2;
			}
			else if( arg instanceof Integer )
			{
				PutInteger( (Integer) arg );
				nStride += 4;
			}
			else if( arg instanceof Float )
			{
				PutFloat( (Float) arg );
				nStride += 4;
			}
		}
		
		if( Stride < 0 )
		{
			Stride = nStride;
		}
		
	}
	
	
	
	private void PutByte( byte v )
	{
		m_listBuffer.add( v );
	}
	
	private void PutShort( short v )
	{
		PutByte((byte)(v & 0xff));
		PutByte((byte)((v>>8) & 0xff));
	}
	
	private void PutInteger( int v )
	{
		PutByte((byte)(v & 0xff));
		PutByte((byte)((v>>8) & 0xff));
		PutByte((byte)((v>>16) & 0xff));
		PutByte((byte)((v>>24) & 0xff));
	}
	
	public void PutFloat( float v )
	{
		PutInteger( Float.floatToIntBits( v ) );
	}
	
	public int Size()
	{
		return m_listBuffer.size();
	}
	
	public byte[] ToArray()// byte[] result )
	{
		byte[] result = new byte[m_listBuffer.size()];
		for( int i = 0 ; i < m_listBuffer.size() ; i++ )
		{
			result[i] = m_listBuffer.get( i );
		//	System.Error.WriteLine( Byte.toString( result[i] ) );
		}
		/*
		for( int i = 0; i < m_listBuffer.size() ; i++ )
		{
			System.Error.Write( String.format( "%02x ", 0xff & (int) result[i] ) );
			if( (i%20) == 19)
			{
				System.Error.WriteLine( " " );
			}
			
		}
		*/
		
		return result;
		//m_listBuffer.toArray( result );
		//return result;
	}
	
	
}

	

class LightTailRender
{
	public StaticVertexBuffer VertexBuffer = null;
	public StaticIndexBuffer IndexBuffer = null;
	public VertexStream VertexStream = null;
	public Program Program = null;
	
	public Uniform u_m4WorldViewProjection = null;
	public Uniform u_fRadius = null;

	public Uniform u_v3ViewPosition = null;

	public Uniform u_v4ColorIn = null;
	public Uniform u_v4ColorOut = null;

	public Uniform u_v4TransformsX = null;
	public Uniform u_v4TransformsY = null;
	public Uniform u_v4TransformsZ = null;
	
	public int SegmentCount = 0; //. 節の数.
	public int PolygonCount = 0; //. 何角形か.

	public int WeightCount = 0; //. 節の間の分割数.

	/*
	public struct Vertex
	{
	public Vector3 Position;
	public Vector3 Normal;
	public Vector2 Indices;
	public Vector2 Weights;

	public Vertex(float x, float y, float z, float nx, float ny, float nz, float i0, float i1, float w0, float w1)
	{
		Position = new Vector3(x, y, z);
		Normal = new Vector3(nx, ny, nz);
		Indices = new Vector2(i0, i1);
		Weights = new Vector2(w0, w1);
	}
	}
	*/
	
	public LightTailRender()
	{
	}

	public LightTailRender( DelayResourceQueue drq,  int nbSegments, int nbPolygons)
	//throws ThreadForceDestroyException
	{
		SegmentCount = nbSegments;
		PolygonCount = nbPolygons;
		int nbWeights = WeightCount = 2;
		
		//int nbVertices = nbSegments*nbWeights*nbPolygons;
		//int nStride = 10*4;

		//List<Vertex> listVertices = new List<Vertex>();
		//ByteBuffer buffer = ByteBuffer.allocateDirect( nbVertices );
		//buffer.order( ByteOrder.nativeOrder() );
		VertexBuilder builder = new VertexBuilder();

		for (int i = 0 ; i < nbSegments ; i++)
		{
			for (int j = 0 ; j < nbWeights ; j++)
			{
				for (int r = 0 ; r < nbPolygons ; r++)
				{
					float rad = FMath.PI * 2.0f * ((float)r / (float)nbPolygons);
					float c = FMath.Cos(rad)*1.0f;
					float s = FMath.Sin(rad)*1.0f;

					float fWeight = 1.0f - ((float)j / (float)nbWeights);
					
					/*
					Vertex vertex = new Vertex
					(
						c, s, 0.0f,
						c, s, 0.0f,
						i, ((i == nbSegments - 1) ? i : i + 1),
						fWeight, 1.0f - fWeight
					);

					listVertices.Add(vertex);
					*/
					/*
					builder.PutFloat( c );
					builder.PutFloat( s );
					builder.PutFloat( 0.0f );
					builder.PutFloat( c );
					builder.PutFloat( s );
					builder.PutFloat( 0.0f );
					builder.PutFloat( i );
					builder.PutFloat( ((i == nbSegments - 1) ? i : i + 1) );
					builder.PutFloat( fWeight );
					builder.PutFloat( 1.0f - fWeight );
					*/				
	
					
					
					builder.Put(
					c, s, 0.0f,
					c, s, 0.0f,
					(float) i, (float) ((i == nbSegments - 1) ? i : i + 1),
					fWeight, 1.0f - fWeight );
					
				}

				if (i == (nbSegments - 1))
				{
					break;
				}
			}
		}

		List<Short> listIndices = new ArrayList<Short>();

		for (int i = 0 ; i < nbSegments - 1 ; i++)
		{
			for (int j = 0 ; j < nbWeights ; j++)
			{
				int i0 = (i * nbWeights + j) * nbPolygons;
				int i1 = (i * nbWeights + j + 1) * nbPolygons;

				for (int r = 0 ; r < nbPolygons ; r++)
				{
					int i00 = i0 + r;
					int i01 = i0 + (r + 1) % nbPolygons;
					int i10 = i1 + r;
					int i11 = i1 + (r + 1) % nbPolygons;

					listIndices.add( (short) i00 );
					listIndices.add((short) i10);
					listIndices.add((short) i01);

					listIndices.add((short) i10);
					listIndices.add((short) i11);
					listIndices.add((short) i01);
				}
			}
		}
		
		VertexAttribute[] arrayVertexAttributes =
		{
			new VertexAttribute( 0, 3, EVertexAttributeFormat.Float, false, 0 ),// Format.Float3, //. Position.
			new VertexAttribute( 1, 3, EVertexAttributeFormat.Float, false, 12 ),//VertexFormat.Float3, //. Normal.
			new VertexAttribute( 2, 2, EVertexAttributeFormat.Float, false, 24 ),//VertexFormat.Float2, //. Indices.
			new VertexAttribute( 3, 2, EVertexAttributeFormat.Float, false, 32 ),//VertexFormat.Float2, //. Weights.
		};
		
		VertexStream = new VertexStream( arrayVertexAttributes, 10*4 );

		VertexBuffer = new StaticVertexBuffer( drq,  builder.ToArray() );// listVertices.Count, listIndices.Count, arrayVertexFormats);
		//VertexBuffer.SetVertices(listVertices.ToArray());
		//VertexBuffer.SetIndices(listIndices.ToArray());
		short[] indices = new short[listIndices.size()];
		for( int i = 0 ; i < indices.length ; i++ )
		{
			indices[i] = listIndices.get( i );
		}
		
		//listIndices.toArray( indices );
		IndexBuffer = new StaticIndexBuffer( drq, indices );
		
		AttributeBinding[] arrayAttributeBindings = 
		{
			new AttributeBinding( 0,  "a_f3Position" ),
			new AttributeBinding( 1, "a_f3Normal" ),
			new AttributeBinding( 2, "a_f2Indices" ),
			new AttributeBinding( 3, "a_f2Weights" ),
		};

		VertexShader vs = new VertexShader( drq, SubSystem.Loader.LoadTextFile( "light_tail_render.vs" ));
		FragmentShader fs = new FragmentShader( drq, SubSystem.Loader.LoadTextFile( "light_tail_render.fs" ));
		
		Program = new Program( drq, arrayAttributeBindings, vs, fs );
		
		u_m4WorldViewProjection = new Uniform( drq, Program, "u_m4WorldViewProjection" );
		u_v3ViewPosition = new Uniform( drq, Program, "u_v3ViewPosition" );
		u_fRadius = new Uniform( drq, Program, "u_fRadius" );
		u_v4ColorIn = new Uniform( drq, Program, "u_v4ColorIn" );
		u_v4ColorOut = new Uniform( drq, Program, "u_v4ColorOut" );
		
		u_v4TransformsX = new Uniform( drq, Program, "u_v4TransformsX" );
		u_v4TransformsY = new Uniform( drq, Program, "u_v4TransformsY" );
		u_v4TransformsZ = new Uniform( drq, Program, "u_v4TransformsZ" );
		
	}

	/// <summary>
	/// Render the specified gc and mc.
	/// </summary>
	/// <param name='gc'>
	/// Gc.
	/// </param>
	/// <param name='mc'>
	/// Mc.
	/// </param>
	/*
	 public void Render( ref GraphicsContext gc, ref MatrixCache mc )
	 {
	 mc.UpdateMatrices();

	 foreach( LightTail lightTail in LightTails )
	 {
	 Shader.SetUniformValue( "WorldViewProjection", mc.ViewProjection );

	 Vector4[] arrayTransformsX = new Vector4[16];
	 Vector4[] arrayTransformsY = new Vector4[16];
	 Vector4[] arrayTransformsZ = new Vector4[16];

	 for( int i = 0 ; i < 16 ; i++ )
	 {
	 Matrix4 matrix = Matrix4.Transpose( lightTail.Transforms[i] );
	 arrayTransformsX[i] = matrix.ColumnX;
	 arrayTransformsY[i] = matrix.ColumnY;
	 arrayTransformsZ[i] = matrix.ColumnZ;
	 }

	 //Shader.SetUniformValues( "Transforms", arrayTransforms, 0, 0, 16 );
	 Shader.Program.SetUniformValue( 1, arrayTransformsX, 0, 0, 16 );
	 Shader.Program.SetUniformValue( 2, arrayTransformsY, 0, 0, 16 );
	 Shader.Program.SetUniformValue( 3, arrayTransformsZ, 0, 0, 16 );

	 gc.SetShaderProgram( Shader.Program );
	 gc.SetVertexBuffer( 0, VertexBuffer );
	 gc.DrawArrays( DrawMode.Triangles, 0, VertexBuffer.IndexCount );
	 }
	 }
	 */
	 
	 
	private final float[] m_arrayTransformsX = new float[16*4];
	private final float[] m_arrayTransformsY = new float[16*4];
	private final float[] m_arrayTransformsZ = new float[16*4];
	
	/*

	public void Render( BasicRender br, LightTail lightTail, float fRadius, Float4 v4ColorIn, Float4 v4ColorOut )
	{
		if (lightTail.Length < 2)
		{
			return;
		}

		MatrixCache mc = br.GetMatrixCache();
	//	mc.Update();
	//	mc.Invert();

		Render r = br.GetRender();
		
		r.SetProgram( Program );
		
		r.SetMatrix( u_m4WorldViewProjection, mc.GetWorldViewProjection().Values );
		r.SetFloat( u_fRadius, fRadius );
		r.SetFloat3( u_v3ViewPosition, mc.GetViewInverse().GetAxisW( Float3.Local()) );
		r.SetFloat4( u_v4ColorIn, v4ColorIn );
		r.SetFloat4( u_v4ColorOut, v4ColorOut );
		
		
		
		
		for (int i = 0 ; i < lightTail.Length ; i += 15)
		{
			int nbTransforms = lightTail.Length - i;
			if (nbTransforms > 16)
			{
				nbTransforms = 16;
			}

			for (int j = 0 ; j < nbTransforms ; j++)
			{
				Float4x4 matrix = lightTail.GetTransform(i + j);
				m_arrayTransformsX[j*4+0] = matrix.Values[0];//X().X;
				m_arrayTransformsX[j*4+1] = matrix.Values[4];//Y().X;
				m_arrayTransformsX[j*4+2] = matrix.Values[8];//Z().X;
				m_arrayTransformsX[j*4+3] = matrix.Values[12];//W().X;
				m_arrayTransformsY[j*4+0] = matrix.Values[1];//X().Y;
				m_arrayTransformsY[j*4+1] = matrix.Values[5];//Y().Y;
				m_arrayTransformsY[j*4+2] = matrix.Values[9];//Z().Y;
				m_arrayTransformsY[j*4+3] = matrix.Values[13];//W().Y;
				m_arrayTransformsZ[j*4+0] = matrix.Values[2];//X().Z;
				m_arrayTransformsZ[j*4+1] = matrix.Values[6];//Y().Z;
				m_arrayTransformsZ[j*4+2] = matrix.Values[10];//Z().Z;
				m_arrayTransformsZ[j*4+3] = matrix.Values[14];//W().Z;
			}

			r.SetFloat4Array( u_v4TransformsX, m_arrayTransformsX );
			r.SetFloat4Array( u_v4TransformsY, m_arrayTransformsY );
			r.SetFloat4Array( u_v4TransformsZ, m_arrayTransformsZ );
			//Shader.Program.SetUniformValue(1, m_arrayTransformsX, 0, 0, 16);
			//Shader.Program.SetUniformValue(2, m_arrayTransformsY, 0, 0, 16);
			//Shader.Program.SetUniformValue(3, m_arrayTransformsZ, 0, 0, 16);

			//gc.SetShaderProgram(Shader.Program);
			r.SetVertexBuffer( VertexBuffer );
			r.EnableVertexStream( VertexStream, 0 );
			r.SetIndexBuffer( IndexBuffer );
			//gc.SetVertexBuffer(0, VertexBuffer);
			//gc.DrawArrays(DrawMode.Triangles, 0, 6 * WeightCount * PolygonCount * (nbTransforms - 1));
			GLES20.glDrawElements(
				GLES20.GL_TRIANGLES,
				6 * WeightCount * PolygonCount * (nbTransforms - 1),
				GLES20.GL_UNSIGNED_SHORT, 0 );
				
			//GLES20.glDrawArrays( GLES20.GL_LINE_STRIP, 12, 0 );
		}
	}
	*/

	private final Box m_boxScissor = new Box();
	
	public void Render( GfxCommandContext r, MatrixCache mc, LightTail lightTail, Frustum frustum, float fScale, Float4 v4ColorIn, Float4 v4ColorOut )
	{
		if (lightTail.Length < 2)
		{
			return;
		}
		
		lightTail.m_boxMinMax.ScissorBackFace( m_boxScissor, frustum.Surfaces );
		if( m_boxScissor.PolygonCount <= 0 )
		{
			return;
		}
		
		//MatrixCache mc = br.GetMatrixCache();
		//Render r = br.GetRender();

		r.SetProgram( Program );

		r.SetMatrix( u_m4WorldViewProjection, mc.GetWorldViewProjection().Values );
		r.SetFloat( u_fRadius, 1.0f );
		r.SetFloat3( u_v3ViewPosition, mc.GetViewInverse().GetAxisW( Float3.Local()) );
		r.SetFloat4( u_v4ColorIn, v4ColorIn );
		r.SetFloat4( u_v4ColorOut, v4ColorOut );

		for (int i = 0 ; i < lightTail.Length ; i += 15)
		{
			int nbTransforms = lightTail.Length - i;
			if (nbTransforms > 16)
			{
				nbTransforms = 16;
			}

			for (int j = 0 ; j < nbTransforms ; j++)
			{
				Float4x4 matrix = lightTail.GetTransform(i + j);
				//matrix.AxisX *= arrayRadius[i + j] * fScale;
				//matrix.AxisY *= arrayRadius[i + j] * fScale;
				//matrix = Matrix4.Transpose(matrix);
				//m_arrayTransformsX[j] = matrix.ColumnX;
				//m_arrayTransformsY[j] = matrix.ColumnY;
				//m_arrayTransformsZ[j] = matrix.ColumnZ;
				float fRadius = lightTail.Radius[i+j]*fScale;
				m_arrayTransformsX[j*4+0] = matrix.Values[0]*fRadius;//X().X*fRadius;
				m_arrayTransformsX[j*4+1] = matrix.Values[4]*fRadius;//Y().X*fRadius;
				m_arrayTransformsX[j*4+2] = matrix.Values[8];//Z().X;
				m_arrayTransformsX[j*4+3] = matrix.Values[12];//W().X;
				m_arrayTransformsY[j*4+0] = matrix.Values[1]*fRadius;//X().Y*fRadius;
				m_arrayTransformsY[j*4+1] = matrix.Values[5]*fRadius;//Y().Y*fRadius;
				m_arrayTransformsY[j*4+2] = matrix.Values[9];//Z().Y;
				m_arrayTransformsY[j*4+3] = matrix.Values[13];//W().Y;
				m_arrayTransformsZ[j*4+0] = matrix.Values[2]*fRadius;//X().Z*fRadius;
				m_arrayTransformsZ[j*4+1] = matrix.Values[6]*fRadius;//Y().Z*fRadius;
				m_arrayTransformsZ[j*4+2] = matrix.Values[10];//Z().Z;
				m_arrayTransformsZ[j*4+3] = matrix.Values[14];//W().Z;
			}

			r.SetFloat4Array( u_v4TransformsX, m_arrayTransformsX );
			r.SetFloat4Array( u_v4TransformsY, m_arrayTransformsY );
			r.SetFloat4Array( u_v4TransformsZ, m_arrayTransformsZ );
			
			//Shader.Program.SetUniformValue(1, m_arrayTransformsX, 0, 0, 16);
			//Shader.Program.SetUniformValue(2, m_arrayTransformsY, 0, 0, 16);
			//Shader.Program.SetUniformValue(3, m_arrayTransformsZ, 0, 0, 16);

			//gc.SetShaderProgram(Shader.Program);
			//gc.SetVertexBuffer(0, VertexBuffer);
			//gc.DrawArrays(DrawMode.Triangles, 0, 6 * WeightCount * PolygonCount * (nbTransforms - 1));
			
			r.SetVertexBuffer( VertexBuffer );
			r.EnableVertexStream( VertexStream, 0 );
			r.SetIndexBuffer( IndexBuffer );
			
			//GLES20.glDrawElements(GLES20.GL_TRIANGLES,	6 * WeightCount * PolygonCount * (nbTransforms - 1),	GLES20.GL_UNSIGNED_SHORT, 0 );
			r.DrawElements(EPrimitive.Triangles, 6 * WeightCount * PolygonCount * (nbTransforms - 1), EIndexFormat.UnsignedShort, 0 );

			r.DisableVertexStream( VertexStream );
		}
	}
}
