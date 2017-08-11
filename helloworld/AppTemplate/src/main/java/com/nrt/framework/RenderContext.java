package com.nrt.framework;

import com.nrt.font.BitmapFont;
import com.nrt.font.FontRender;
import com.nrt.render.BasicRender;
import com.nrt.render.FrameLinearIndexBuffer;
import com.nrt.render.FrameLinearVertexBuffer;
import com.nrt.render.GfxCommandContext;
import com.nrt.render.MatrixCache;

/**
 * Created by itari on 2017/05/20.
 */

public class RenderContext
{
    GfxCommandContext m_gfxCommandContext = null;
    MatrixCache m_matrixCache = null;
    FrameLinearVertexBuffer m_vertexBuffer = null;
    FrameLinearIndexBuffer m_indexBuffer = null;

    BasicRender m_basicRender = null;
    FontRender m_fontRender = null;
    BitmapFont m_bitmapFont = null;

    public final void BeginFrame(final GfxCommandContext gfxc, final MatrixCache mc, final FrameLinearVertexBuffer vb, final FrameLinearIndexBuffer ib, final BasicRender br, final FontRender fr, final BitmapFont bf)
    {
        m_gfxCommandContext = gfxc;
        m_matrixCache = mc;
        m_vertexBuffer = vb;
        m_indexBuffer = ib;
        m_basicRender = br;
        m_fontRender = fr;
        m_bitmapFont = bf;
    }

    public final void EndFrame()
    {
        m_gfxCommandContext = null;
        m_matrixCache = null;
        m_vertexBuffer = null;
        m_indexBuffer = null;
        m_basicRender = null;
        m_fontRender = null;
        m_bitmapFont = null;
    }

    public final GfxCommandContext GetCommandContext()
    {
        return m_gfxCommandContext;
    }

    public final MatrixCache GetMatrixCache()
    {
        return m_matrixCache;
    }

    public final FrameLinearVertexBuffer GetVertexBuffer()
    {
        return m_vertexBuffer;
    }

    public final FrameLinearIndexBuffer GetIndexBuffer()
    {
        return m_indexBuffer;
    }

    public final BasicRender GetBasicRender()
    {
        return m_basicRender;
    }

    public final FontRender GetFontRender() { return m_fontRender; }

    public final BitmapFont GetBitmapFont() { return m_bitmapFont; }
}
