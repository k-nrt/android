package com.nrt.render;

import android.opengl.*;
import com.nrt.basic.*;
import com.nrt.math.*;
import com.nrt.framework.*;

public class BasicRender
{
	public enum EShader
	{
		Color,
		ColorTexture,
		ColorTextureAlphaTest,
		Max,
	}

	private Program[] m_programs = new Program[EShader.Max.ordinal()];
	private VertexStream[] m_streams = new VertexStream[EShader.Max.ordinal()];
	private Uniform[] u_worldViewProjections = new Uniform[EShader.Max.ordinal()];
	//private Render m_render = null; 
	private MatrixCache m_matrixCache = null;
	
	

	//private RingVertexBuffer[] m_vertexBuffers = null;
	//private RingIndexBuffer[] m_indexBuffers = null;

	//private RingVertexBuffer m_vertexBuffer = null;
	//private RingIndexBuffer m_indexBuffer = null;
	
	private FrameLinearVertexBuffer m_vertexBuffer = null;
	private FrameLinearIndexBuffer m_indexBuffer = null;
	
	private int m_nStride = 0;
	//private int m_nbMaxVertices = 0;
	
	
	private Float4 m_f4Color = new Float4(0.0f);
	private Float4 m_f4Texcoord = new Float4(0.0f);

	private EShader m_eShader = EShader.Color;
	private int m_nbVertices = 0;
	private EPrimitive m_primitive = EPrimitive.Points;
	public int PrimitiveCount = 0;

	private Texture m_texture = null;
	private SamplerState m_samplerState = null;

	private Sampler[] u_sampler0 = new Sampler[EShader.Max.ordinal()];
	
	private DelayResourceQueueMarker Marker = new DelayResourceQueueMarker("BasicRenderReady");

	public BasicRender(DelayResourceQueue drq)
	{
		//m_render = r;
		//m_matrixCache = mc;

		/*
		int nbBuffers = 4;
		m_vertexBuffers = new RingVertexBuffer[nbBuffers];
		m_indexBuffers = new RingIndexBuffer[nbBuffers];
		for(int i = 0 ; i < nbBuffers ; i++)
		{
			m_vertexBuffers[i] = new RingVertexBuffer( SubSystem.DelayResourceQueue, nVertexBufferSize);
			m_indexBuffers[i] = new RingIndexBuffer( SubSystem.DelayResourceQueue, nIndexBufferSize);
		}
		m_vertexBuffer = m_vertexBuffers[0];
		m_indexBuffer = m_indexBuffers[0];
		*/
		//m_vertexBuffer = new BlockVertexBuffer(SubSystem.DelayResourceQueue, 4*1024, 64);
		//m_indexBuffer = new BlockIndexBuffer(SubSystem.DelayResourceQueue, 2*1024, 64);
		
		VertexAttribute[] vaColors =
		{
			new VertexAttribute(0, 3, EVertexAttributeFormat.Float, false, 0),
			new VertexAttribute(1, 4, EVertexAttributeFormat.Float, false, 12),
			//new VertexAttribute( 2, 2, GLES20.GL_FLOAT, false, 28 ),
		};
		m_streams[EShader.Color.ordinal()] = new VertexStream(vaColors, 0);

		VertexAttribute[] vaColorTextures =
		{
			new VertexAttribute(0, 3, EVertexAttributeFormat.Float, false, 0),
			new VertexAttribute(1, 4, EVertexAttributeFormat.Float, false, 12),
			new VertexAttribute(2, 2, EVertexAttributeFormat.Float, false, 28),
		};	
		m_streams[EShader.ColorTexture.ordinal()] = new VertexStream(vaColorTextures, 0);	
		m_streams[EShader.ColorTextureAlphaTest.ordinal()] = new VertexStream(vaColorTextures, 0);

		String[] strVsColor = 
		{
			"uniform mat4 u_worldViewProjection;",
			"attribute vec3 a_position;",
			"attribute vec4 a_color;",
			//	"attribute vec2 a_texcoord;",
			"varying vec4 v_color;",
		//	"varying vec2 v_texcoord;",
			"void main(){",
			"gl_Position = u_worldViewProjection*vec4(a_position,1.0);",
			"v_color = a_color;",
			//	"v_texcoord = a_texcoord;",
			"}",
		};

		String[] strFsColor =
		{
			"precision mediump float;",
			"varying vec4 v_color;",
			//	"varying vec4 v_texcoord;",
			"void main(){",
			"gl_FragColor=v_color;",
			//	"gl_FragColor=vec4(1.0,1.0,1.0,1.0);",
			"}",
		};

		AttributeBinding[] abColors =
		{
			new AttributeBinding(0, "a_position"),
			new AttributeBinding(1, "a_color"),
			//	new AttributeBinding( 2, "a_texcoord" ),
		};

		m_programs[EShader.Color.ordinal()] = new Program
		(
			drq,
			abColors,
			new VertexShader( drq, strVsColor),
			new FragmentShader( drq, strFsColor)
		);

		u_worldViewProjections[EShader.Color.ordinal()] = new Uniform
		(
			drq, 
			m_programs[EShader.Color.ordinal()], 
			"u_worldViewProjection"
		);

		String[] strVsColorTexture = 
		{
			"precision highp float;",
			"uniform highp mat4 u_worldViewProjection;",
			"attribute vec3 a_position;",
			"attribute vec4 a_color;",
			"attribute highp vec2 a_texcoord;",
			"varying vec4 v_color;",
			"varying highp vec2 v_texcoord;",
			"void main(){",
			"gl_Position = u_worldViewProjection*vec4(a_position,1.0);",
			"v_color = a_color;",
			"v_texcoord = a_texcoord;",
			"}",
		};

		String[] strFsColorTexture =
		{
			"precision highp float;",
			"varying vec4 v_color;",
			"varying highp vec2 v_texcoord;",
			"uniform sampler2D u_texture0;",
			"void main(){",
			"gl_FragColor = texture2D( u_texture0, v_texcoord )*v_color;",
			"}",
		};

		AttributeBinding[] abColorTextures =
		{
			new AttributeBinding(0, "a_position"),
			new AttributeBinding(1, "a_color"),
			new AttributeBinding(2, "a_texcoord"),
		};

		m_programs[EShader.ColorTexture.ordinal()] = new Program
		(
			drq, 
			abColorTextures,
			new VertexShader(drq,strVsColorTexture),
			new FragmentShader(drq,strFsColorTexture)
		);

		u_worldViewProjections[EShader.ColorTexture.ordinal()] = new Uniform
		(
			drq, 
			m_programs[EShader.ColorTexture.ordinal()], 
			"u_worldViewProjection"
		);
		
		u_sampler0[EShader.ColorTexture.ordinal()] = new Sampler
		(
			drq, 
			m_programs[EShader.ColorTexture.ordinal()], 
			0,
			"u_texture0"
		);
		
		drq.Add( Marker );
	}
	
	public GfxCommandContext GfxCommandContext = null;
	
	public final void BeginFrame
	(
		final GfxCommandContext gfxCommandContext,
		final MatrixCache matrixCache,
		final FrameLinearVertexBuffer vertexBuffer,
		final FrameLinearIndexBuffer indexBuffer
	)
	{
		if( !Marker.Done )
		{
			return;
		}
		/*
		int nBuffer = (int)(nRenderFrame%(long)m_vertexBuffers.length);
		m_vertexBuffer = m_vertexBuffers[nBuffer];
		m_vertexBuffer.Rewind();
		m_indexBuffer = m_indexBuffers[nBuffer];
		m_indexBuffer.Rewind();
		*/
		PrimitiveCount = 0;
		GfxCommandContext = gfxCommandContext;
		m_matrixCache = matrixCache;
		m_vertexBuffer = vertexBuffer;
		m_indexBuffer = indexBuffer;
	}

	void EndFrame()
	{
		GfxCommandContext = null;
		m_matrixCache = null;
		m_vertexBuffer = null;
		m_indexBuffer = null;
	}

	public void SetColor(float r, float g, float b, float a)
	{
		m_f4Color.X = r;
		m_f4Color.Y = g;
		m_f4Color.Z = b;
		m_f4Color.W = a;
	}
	
	public void SetColor( Float4 f4Rgba )
	{
		m_f4Color.Set( f4Rgba );
	}
	
	public void SetColor( int rgba )
	{
		m_f4Color.W = ((float)(0xff & rgba))/255.0f;
		m_f4Color.Z = ((float)(0xff & (rgba >> 8)))/255.0f;
		m_f4Color.Y = ((float)(0xff & (rgba >> 16)))/255.0f;
		m_f4Color.X = ((float)(0xff & (rgba >> 24)))/255.0f;
	}

	public void SetTexcoord(float u, float v)
	{
		m_f4Texcoord.X = u;
		m_f4Texcoord.Y = v;
	}

	public void SetTexture(Texture texture)
	{
		m_texture = texture;
		//m_render.Bind( GLES20.GL_TEXTURE0, texture );
		//m_render.Bind( u_sampler0[m_eShader.ordinal()], texture );
	}
	
	public void SetSamplerState( SamplerState samplerState )
	{
		m_samplerState = samplerState;
	}

	public int m_vertexBufferOffset = 0;
	public int m_indexBufferOffset = 0;
	
	public void Begin(EPrimitive primitive, EShader shader)
	{
		if(!Marker.Done)
		{
			return;
		}
		
		final GfxCommandContext gfxc = GfxCommandContext;
		final MatrixCache mc = m_matrixCache;
		final FrameLinearVertexBuffer vb = m_vertexBuffer;
		final FrameLinearIndexBuffer ib = m_indexBuffer;
		
		if(gfxc == null || mc == null || vb == null || ib == null)
		{
			return;
		}
		
		m_primitive = primitive;
		m_eShader = shader;
		m_nbVertices = 0;
		
		int iShader = m_eShader.ordinal();
		
		m_nStride = m_streams[iShader].Stride;
		
		gfxc.SetProgram(m_programs[iShader]);
		gfxc.SetMatrix(u_worldViewProjections[iShader], mc.GetWorldViewProjection().Values);

		if (u_sampler0[m_eShader.ordinal()] != null && m_texture != null)
		{
			gfxc.SetTexture(u_sampler0[m_eShader.ordinal()], m_texture);
			gfxc.SetSamplerState( u_sampler0[m_eShader.ordinal()], m_samplerState );
		}

		vb.Align(16);
		m_vertexBufferOffset = vb.GetPosition();
		ib.Align(4);
		m_indexBufferOffset = ib.GetPosition();
	}
	
	public final void SetVertex( final Float3 v )
	{
		KickVertex( v.X, v.Y, v.Z );
	}

	public final void SetVertex(float x, float y, float z)
	{
		KickVertex(x,y,z);
	}
	
	private final void KickVertex(float x, float y, float z)
	{
		if(!Marker.Done)
		{
			return;
		}
		
		final FrameLinearVertexBuffer vb = m_vertexBuffer;
		final FrameLinearIndexBuffer ib = m_indexBuffer;
		if(vb == null || ib == null)
		{
			return;
		}

		vb.Add(x, y, z);
		vb.Add(m_f4Color.X, m_f4Color.Y, m_f4Color.Z, m_f4Color.W);
		if (m_eShader == EShader.ColorTexture || m_eShader ==  EShader.ColorTextureAlphaTest)
		{
			vb.Add(m_f4Texcoord.X, m_f4Texcoord.Y);
		}
		ib.Add((short)m_nbVertices);
		m_nbVertices++;
	}
	
	public void End()
	{
		if(!Marker.Done)
		{
			return;
		}

		final GfxCommandContext gfxc = GfxCommandContext;
		final FrameLinearVertexBuffer vb = m_vertexBuffer;
		final FrameLinearIndexBuffer ib = m_indexBuffer;
		
		if(gfxc == null || vb == null || ib == null)
		{
			return;
		}
		
		//vb.End();
		//ib.End();

		if(0<m_nbVertices)
		{
			gfxc.SetVertexBuffer(m_vertexBuffer);
			gfxc.EnableVertexStream(m_streams[m_eShader.ordinal()], m_vertexBufferOffset);
			gfxc.SetIndexBuffer(m_indexBuffer);
			gfxc.DrawElements(m_primitive, m_nbVertices, EIndexFormat.UnsignedShort, m_indexBufferOffset );

			gfxc.DisableVertexStream(m_streams[m_eShader.ordinal()]);

		}
		
  		m_nbVertices = 0;
		PrimitiveCount++;
	}

	public void FillRectangle(Rect rect)
	{
		Begin(EPrimitive.TriangleStrip, EShader.Color);
		SetVertex(rect.X, rect.Y, 0.0f);
		SetVertex(rect.X, rect.Y + rect.Height, 0.0f);
		SetVertex(rect.X + rect.Width, rect.Y, 0.0f);
		SetVertex(rect.X + rect.Width, rect.Y + rect.Height, 0.0f);
		End();
	}

	public void FillRectangle( float x, float y, float z, float w, float h )
	{
		Begin(EPrimitive.TriangleStrip, EShader.Color);
		SetVertex(x, y, z);
		SetVertex(x, y + h, z);
		SetVertex(x + w, y, z);
		SetVertex(x + w, y + h, z);
		End();
	}

	public final void Rectangle(final Rect rect)
	{
		Begin(EPrimitive.LineStrip, EShader.Color);
		SetVertex(rect.X, rect.Y, 0.0f);
		SetVertex(rect.X + rect.Width, rect.Y, 0.0f);
		SetVertex(rect.X + rect.Width, rect.Y + rect.Height, 0.0f);

		SetVertex(rect.X, rect.Y + rect.Height, 0.0f);
		SetVertex(rect.X, rect.Y, 0.0f);

		End();
	}
	
	public void Rectangle( float x, float y, float w, float h )
	{
		Begin(EPrimitive.LineStrip, EShader.Color);
		SetVertex(x, y, 0.0f);
		SetVertex(x + w, y, 0.0f);
		SetVertex(x + w, y + h, 0.0f);

		SetVertex(x, y + h, 0.0f);
		SetVertex(x, y, 0.0f);

		End();
		
	}

	public void Arc(float x, float y, float radius, int nbPolygons)
	{
		Begin(EPrimitive.LineStrip, EShader.Color);

		for (int i = 0 ; i <= nbPolygons ; i++)
		{
			double rad = ((double)i / (double)nbPolygons) * Math.PI * 2.0;
			float s = (float) Math.sin(rad);
			float c = (float) Math.cos(rad);

			SetVertex(x + c * radius, y + s * radius, 0.0f);
		}
		End();
	}
	
	public static final int RgbaToAbgr( int rgba )
	{
		return ((rgba >> 24) & 0xff) |((rgba >> 8) & 0xff00) | ((rgba << 8) & 0xff0000) | ((rgba << 24) & 0xff000000);
	}
	
	public void Arc(float x, float y, float radiusIn, int rgbaIn, float radiusOut, int rgbaOut, int nbPolygons)
	{
		Begin(EPrimitive.TriangleStrip, EShader.Color);

		//int abgrIn = RgbaToAbgr( rgbaIn );
		//int abgrOut = RgbaToAbgr( rgbaOut );
		for (int i = 0 ; i <= nbPolygons ; i++)
		{
			double rad = ((double)i / (double)nbPolygons) * Math.PI * 2.0;
			float s = (float) Math.sin(rad);
			float c = (float) Math.cos(rad);
			
			SetColor( rgbaOut );
			SetVertex(x + c * radiusOut, y + s * radiusOut, 0.0f);
			
			SetColor( rgbaIn );
			SetVertex(x + c * radiusIn, y + s * radiusIn, 0.0f);
			
		}
		End();
	}

	public void Axis( float x, float y, float z, float size )
	{
		Begin( EPrimitive.Lines, EShader.Color );
		
		SetColor( 1.0f, 0.0f, 0.0f, 1.0f );
		SetVertex( x, y, z );
		SetVertex( x + size, y, z );

		SetColor( 0.0f, 1.0f, 0.0f, 1.0f );
		SetVertex( x, y, z );
		SetVertex( x, y + size, z );
	
		SetColor( 0.0f, 0.0f, 1.0f, 1.0f );
		SetVertex( x, y, z );
		SetVertex( x, y, z + size );
		
		End();
	}
	/*
	public Render GetRender()
	{
		return m_render;
	}
	*/
	public GfxCommandContext GetCommandContext()
	{
		return GfxCommandContext;
	}

	public MatrixCache GetMatrixCache()
	{
		return m_matrixCache;
	}

	public FrameLinearVertexBuffer GetVertexBuffer()
	{
		return m_vertexBuffer;
	}

	public FrameLinearIndexBuffer GetIndexBuffer()
	{
		return m_indexBuffer;
	}
	
	
}

