package com.nrt.font;
import com.nrt.render.*;
import java.nio.*;
//import android.opengl.*;
import com.nrt.framework.*;

public class BitmapFont
{
	public FontCharInfo[] m_infos = null;
	public StaticTexture m_texture = null;

	public Program m_program = null;
	public Uniform u_worldViewProjection = null;
	public Sampler u_sampler0 = null;

	public SamplerState m_samplerLinear = null;

	public BlendState m_blendStateTransparent = new BlendState
	(true, BlendFunc.SrcAlpha, BlendFunc.OneMinusSrcAlpha);

	public RasterizerState m_rasterizerStateDisableCullFace = new RasterizerState(false, ECullFace.Back, EFrontFace.CCW);

	public DepthStencilState m_depthStencilStateDisable = new DepthStencilState(false, EDepthFunc.Always, false);

	public VertexStream m_stream = null;

	public float m_fSize = 8.0f;

	public int m_rgbaFont = 0xffffffff;

	public GfxCommandContext m_gfxCommandContext;
	public MatrixCache m_matrixCache = null;
	FrameLinearVertexBuffer m_vertexBuffer = null;
	FrameLinearIndexBuffer m_indexBuffer = null;
	
	public DelayResourceQueueMarker Marker = new DelayResourceQueueMarker("BitmapFontReady");
	public int StringCount = 0;

	public static class Pattern
	{
		public String Targets = "";
		public String[] Image = null;

		public Pattern(String targets, String[] image)
		{
			Targets = targets;
			Image = image;
		}
	}

	public BitmapFont(DelayResourceQueue drq, Pattern[] patterns)
	{
		int nTextureSize = 128;

		ByteBuffer bufferFont = ByteBuffer.allocateDirect(nTextureSize * nTextureSize * 4);

		for (Pattern pattern : patterns)
		{
			for (int i = 0 ; i < pattern.Targets.length() ; i++)
			{
				int c =  pattern.Targets.charAt(i);
				int ox = (c % 16) * 8;
				int oy = (c / 16) * 8;

				for (int y = 0 ; y < 8 ; y++)
				{
					for (int x = 0 ; x < 6 ; x++)
					{
						if (pattern.Image[y].charAt(x + i * 6) == '-')
						{
							bufferFont.putInt(((ox + x) + (oy + y) * nTextureSize) * 4,  0x0000);
						}
						else
						{
							bufferFont.putInt(((ox + x) + (oy + y) * nTextureSize) * 4,  0xffffffff);
						}
					}
				}
			}

		}

		//m_texture = new StaticTexture( m_bitmap );

		m_texture = new StaticTexture(
			drq,
			TextureInternalFormat.RGBA, nTextureSize, nTextureSize,
			TextureSourceFormat.RGBA, TextureSourceType.UnsignedByte, bufferFont, false);

		String[] strVsColorTexture = 
		{
			"uniform highp mat4 u_worldViewProjection;",
			"attribute highp vec3 a_position;",
			"attribute vec4 a_color;",
			"attribute highp vec2 a_texcoord;",
			"varying vec4 v_color;",
			"varying highp vec2 v_texcoord;",
			"void main(){",
			"gl_Position = u_worldViewProjection*vec4(a_position,1.0);",
			"v_color = a_color.xyzw;",
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

		m_program = new Program(drq,
								abColorTextures,
								new VertexShader(drq, strVsColorTexture),
								new FragmentShader(drq, strFsColorTexture));

		u_worldViewProjection
			= new Uniform(drq, m_program, "u_worldViewProjection");
		u_sampler0 = new Sampler(drq, m_program, 0, "u_texture0");

		VertexAttribute[] va = 
		{
			new VertexAttribute(0, 3, EVertexAttributeFormat.Float, false, 0),
			new VertexAttribute(1, 4, EVertexAttributeFormat.UnsignedByte, true, 12),
			new VertexAttribute(2, 2, EVertexAttributeFormat.Float, false, 16),
		};

		m_stream = new VertexStream(va, 0);
		m_samplerLinear = new SamplerState(EMagFilter.Nearest, EMinFilter.Nearest, EWrap.ClampToEdge, EWrap.ClampToEdge);

		drq.Add(Marker);
	}

	public final void BeginFrame
	(
		final GfxCommandContext gfxCommandContext,
		final MatrixCache matrixCache,
		final FrameLinearVertexBuffer vertexBuffer,
		final FrameLinearIndexBuffer indexBuffer
	)
	{
		if(!Marker.Done)
		{
			return;
		}
		
		m_gfxCommandContext = gfxCommandContext;
		m_matrixCache = matrixCache;
		m_vertexBuffer = vertexBuffer;
		m_indexBuffer = indexBuffer;
		StringCount = 0;
	}

	public final void EndFrame()
	{
		m_gfxCommandContext = null;
		m_matrixCache = null;
		m_vertexBuffer = null;
		m_indexBuffer = null;
	}
	
	public final void SetSize(float size)
	{
		m_fSize = size;
	}

	public final void SetColor(int rgba)
	{
		m_rgbaFont = 
			((rgba >> 24) & 0xff) |
			((rgba >> 8) & 0xff00) |
			((rgba << 8) & 0xff0000) |
			((rgba << 24) & 0xff000000);
	}
	

	public final void Begin()
	{
		if (!Marker.Done)
		{
			return;
		}
		
		final GfxCommandContext gfxc = m_gfxCommandContext;
		final MatrixCache mc = m_matrixCache;

		if (gfxc == null || mc == null)
		{
			return;
		}

		gfxc.SetBlendState(m_blendStateTransparent);
		gfxc.SetRasterizerState(m_rasterizerStateDisableCullFace);
		gfxc.SetDepthStencilState(m_depthStencilStateDisable);

		gfxc.SetProgram(m_program);

		gfxc.SetMatrix(u_worldViewProjection, mc.GetWorldViewProjection().Values);
		gfxc.SetTexture(u_sampler0, m_texture);
		gfxc.SetSamplerState(u_sampler0, m_samplerLinear);
	}

	public final void Draw(float x, float y, float z, final String strText)
	{
		KickVertices(x, y, z, strText, m_rgbaFont);
	}

	private final void KickVertices(float fX, float fY, float fZ, final String strText, int rgba)
	{
		if (!Marker.Done)
		{
			return;
		}

		final GfxCommandContext gfxc = m_gfxCommandContext;
		final FrameLinearVertexBuffer vb = m_vertexBuffer;
		final FrameLinearIndexBuffer ib = m_indexBuffer;

		if (gfxc == null ||
			vb == null || 
			ib == null)
		{
			return;
		}

		int nbIndices = 0;
		int nIndexOffset = 0;


		ib.Align(4);
		int nIndexBufferOffset = ib.GetPosition();

		vb.Align(16);
		int nVertexBufferOffset = vb.GetPosition();

		//vb.Begin();
		//ib.Begin();

		float xsize = (m_fSize * 6.0f) / 8.0f;
		float ysize = m_fSize;
		float usize = 6.0f / 128.0f;
		float vsize = 8.0f / 128.0f;
		float x = fX;

		int length = strText.length();

		for (int i = 0 ; i < length ; i++)
		{
			int c = strText.charAt(i);
			float u = ((float)((c % 16) * 8)) / 128.0f;
			float v = ((float)((c / 16) * 8)) / 128.0f;


			vb.Add(x, fY, fZ);
			vb.Add(rgba);
			vb.Add(u, v);

			vb.Add(x, fY + ysize, fZ);
			vb.Add(rgba);
			vb.Add(u, v + vsize);

			vb.Add(x + xsize, fY, fZ);
			vb.Add(rgba);
			vb.Add(u + usize, v);

			vb.Add(x + xsize, fY + ysize, fZ);
			vb.Add(rgba);
			vb.Add(u + usize, v + vsize);

			x += xsize;

			ib.Add((short) (0 + nIndexOffset));
			ib.Add((short) (1 + nIndexOffset));
			ib.Add((short) (2 + nIndexOffset));
			ib.Add((short) (2 + nIndexOffset));
			ib.Add((short) (1 + nIndexOffset));
			ib.Add((short) (3 + nIndexOffset));

			nbIndices += 6;
			nIndexOffset += 4;
		}

		//vb.End();
		//ib.End();
		if (0 < nbIndices)
		{
			gfxc.SetVertexBuffer(vb);
			gfxc.EnableVertexStream(m_stream, nVertexBufferOffset);
			gfxc.SetIndexBuffer(ib);
			gfxc.DrawElements(EPrimitive.Triangles, nbIndices, EIndexFormat.UnsignedShort, nIndexBufferOffset);

			gfxc.DisableVertexStream(m_stream);
		}
		StringCount++;
	}

	public final void End()
	{
		if (!Marker.Done)
		{
			return;
		}

		final GfxCommandContext gfxc = m_gfxCommandContext;
		if (gfxc == null)
		{
			return;
		}

		gfxc.DisableVertexStream(m_stream);
		gfxc.SetVertexBuffer(null);
		gfxc.SetIndexBuffer(null);
		gfxc.SetBlendState(null);
	}
}
