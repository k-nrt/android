package com.nrt.framework;

import com.nrt.font.BitmapFont;
import com.nrt.font.FontRender;
import com.nrt.font.Font;
import com.nrt.render.BasicRender;
import com.nrt.render.DelayResourceQueue;
import com.nrt.render.FrameLinearIndexBuffer;
import com.nrt.render.FrameLinearVertexBuffer;
import com.nrt.render.GfxCommandBuffer;
import com.nrt.render.GfxCommandContext;
import com.nrt.render.MatrixCache;
import com.nrt.render.Render;

/**
 * Created by itari on 2017/05/28.
 */

//import java.util.concurrent.atomic.AtomicLong;

public class RenderSystem
{
	//. Per Jobs x Per buffers.
	FrameLinearVertexBuffer[] m_vertexBuffers = null;
	FrameLinearIndexBuffer[] m_indexBuffers = null;
	GfxCommandBuffer[] m_gfxCommandBuffers = null;

	//. Per Jobs.
	GfxCommandContext[] m_gfxCommandContexts = null;
	BasicRender[] m_basicRenders = null;
	MatrixCache[] m_matrixCaches = null;
	FontRender[] m_fontRenders = null;
	BitmapFont[] m_bitmapFonts = null;

	RenderContext[] m_renderContexts = null;

	Font m_font = null;

	public Object m_locker = new Object();
	public long BuilderFrame = 0;
	public long RenderFrame = 0;

	public int m_nbPresentationBuffers = 1;
	public int m_nbCommandBuilderJobs = 1;

	public static final class Configuration
	{
		public int PresentationBufferCount = 4;
		public int GfxCommandBuilderJobCount = 1;
		public int VertexBufferInitialSizeInBytes = 1024 * 1024;
		public int VertexBufferExpandSizeInBytes = 256 * 1024;
		public int IndexBufferInitialSizeInBytes = 256 * 1024;
		public int IndexBufferExpandSizeInBytes = 64 * 1024;

		public int GfxCommandInitialCommandCount = 4096;
		public int GfxCommandExpandCommandCount = 4096;
		public int GfxCommandInitialIntegerCount = 4096;
		public int GfxCommandExpandIntegerCount = 4096;
		public int GfxCommandInitialFloatCount = 4096;
		public int GfxCommandExpandFloatCount = 4096;
		public int GfxCommandInitialObjectCount = 4096;
		public int GfxCommandExpandObjectCount = 4096;
	}

	public RenderSystem(final DelayResourceQueue drq, final Configuration configuation, final BitmapFont.Pattern[] patterns )
	{
		int nbBuffers = configuation.PresentationBufferCount * configuation.GfxCommandBuilderJobCount;
		m_vertexBuffers = new FrameLinearVertexBuffer[nbBuffers];
		m_indexBuffers = new FrameLinearIndexBuffer[nbBuffers];
		m_gfxCommandBuffers = new GfxCommandBuffer[nbBuffers];
		for (int i = 0; i < nbBuffers; i++)
		{
			m_vertexBuffers[i] = new FrameLinearVertexBuffer(drq, configuation.VertexBufferInitialSizeInBytes, configuation.VertexBufferExpandSizeInBytes);
			m_indexBuffers[i] = new FrameLinearIndexBuffer(drq, configuation.IndexBufferInitialSizeInBytes, configuation.IndexBufferExpandSizeInBytes);
			m_gfxCommandBuffers[i] = new GfxCommandBuffer
					(
							configuation.GfxCommandInitialCommandCount, configuation.GfxCommandExpandCommandCount,
							configuation.GfxCommandInitialIntegerCount, configuation.GfxCommandExpandIntegerCount,
							configuation.GfxCommandInitialFloatCount, configuation.GfxCommandExpandFloatCount,
							configuation.GfxCommandInitialObjectCount, configuation.GfxCommandExpandObjectCount
					);
		}

		int nbJobs = configuation.GfxCommandBuilderJobCount;
		m_gfxCommandContexts = new GfxCommandContext[nbJobs];
		m_basicRenders = new BasicRender[nbJobs];
		m_matrixCaches = new MatrixCache[nbJobs];
		m_fontRenders = new FontRender[nbJobs];
		m_bitmapFonts = new BitmapFont[nbJobs];

		m_renderContexts = new RenderContext[nbJobs];

		for (int i = 0; i < nbJobs; i++)
		{
			m_gfxCommandContexts[i] = new GfxCommandContext();
			m_basicRenders[i] = new BasicRender(drq);
			m_matrixCaches[i] = new MatrixCache();
			m_fontRenders[i] = new FontRender(drq);
			m_bitmapFonts[i] = new BitmapFont(drq,patterns);
			m_renderContexts[i] = new RenderContext();
		}

		m_nbPresentationBuffers = configuation.PresentationBufferCount;
		m_nbCommandBuilderJobs = configuation.GfxCommandBuilderJobCount;
	}

	public void SetFont(final Font font)
	{
		m_font = font;
		for(int i = 0 ; i < m_nbCommandBuilderJobs ; i++ )
		{
			m_fontRenders[i].SetFont(m_font);
			m_fontRenders[i].SetSize(16);
			m_fontRenders[i].SetFontColor(0xffffffff);
			m_fontRenders[i].SetBoarderColor(0xff000000);
		}
	}

	private int GetPresentationBufferIndex(long frame)
	{
		return (int)(frame % m_nbPresentationBuffers);
	}

	private int GetBuilderBufferStartIndex(long frame)
	{
		int index = GetPresentationBufferIndex(frame);
		return index * m_nbCommandBuilderJobs;
	}

	public boolean BeginBuilerFrame()
	{
		long builder = 0;
		long render = 0;

		synchronized (m_locker)
		{
			builder = BuilderFrame;
			render = RenderFrame;
		}

		if(m_nbPresentationBuffers <= (builder - render))
		{
			return false;
		}

		int frame = GetPresentationBufferIndex(builder);
		int buffer = GetBuilderBufferStartIndex(builder);

		for (int i = 0; i < m_nbCommandBuilderJobs; i++)
		{
			m_gfxCommandBuffers[buffer + i].Rewind();
			m_vertexBuffers[buffer + i].Rewind();
			m_indexBuffers[buffer + i].Rewind();
			m_gfxCommandContexts[i].BeginFrame(m_gfxCommandBuffers[buffer + i]);
			m_basicRenders[i].BeginFrame(m_gfxCommandContexts[i], m_matrixCaches[i], m_vertexBuffers[buffer + i], m_indexBuffers[buffer + i]);
			m_fontRenders[i].BeginFrame(m_gfxCommandContexts[i], m_matrixCaches[i], m_vertexBuffers[buffer + i], m_indexBuffers[buffer + i]);
			m_bitmapFonts[i].BeginFrame(m_gfxCommandContexts[i], m_matrixCaches[i], m_vertexBuffers[buffer + i], m_indexBuffers[buffer + i]);
			m_renderContexts[i].BeginFrame
					(
							m_gfxCommandContexts[i],
							m_matrixCaches[i],
							m_vertexBuffers[buffer + i],
							m_indexBuffers[buffer + i],
							m_basicRenders[i],
							m_fontRenders[i],
							m_bitmapFonts[i]
					);

		}

		return true;
	}

	public final RenderContext GetRenderContext(int job)
	{
		return m_renderContexts[job];
	}

	public void EndBuilderFrame()
	{
		synchronized (m_locker)
		{
			BuilderFrame++;
		}
	}

	public boolean UpdateResources()
	{
		long builder = 0;
		long render = 0;
		synchronized (m_locker)
 		{
			builder = BuilderFrame;
			render = RenderFrame;
		}

		if(builder <= render)
		{
			return false;
		}

		int buffer = GetBuilderBufferStartIndex(render);

		for (int i = 0; i < m_nbCommandBuilderJobs; i++)
		{
			m_vertexBuffers[buffer + i].UpdateResource();
			m_indexBuffers[buffer + i].UpdateResource();
		}
		return true;
	}

	public boolean ProcessCommands(final Render r)
	{
		long builder = 0;
		long render = 0;
		synchronized (m_locker)
		{
			builder = BuilderFrame;
			render = RenderFrame;
		}

		if(builder <= render)
		{
			return false;
		}

		int buffer = GetBuilderBufferStartIndex(render);
		for (int i = 0; i < m_nbCommandBuilderJobs; i++)
		{
			m_gfxCommandBuffers[buffer + i].ProcessCommands(r, 0, -1);
		}

		synchronized (m_locker)
		{
			RenderFrame++;
		}

		return true;
	}
}
