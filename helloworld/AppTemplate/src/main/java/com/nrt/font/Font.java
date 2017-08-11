package com.nrt.font;

import java.nio.Buffer; 
import java.nio.ByteBuffer; 
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.util.Log;
import java.util.*;

import android.graphics.Bitmap;
import android.graphics.*;
import java.nio.charset.*;
import android.util.*;

import com.nrt.basic.DebugLog;
import com.nrt.render.*;
import com.nrt.framework.*;



public class Font
{
	final int MaxTextures = 4;

	public final StaticTexture[] m_textureFonts = new StaticTexture[MaxTextures];
	public final StaticTexture[] m_textureBoarders = new StaticTexture[MaxTextures];

	public int m_nFontSize = 0;
	public int m_nBoarder = 0;
	public int m_nGap = 0;
	public int m_nRectSize = 0;

	public CharsetEncoder m_encoder = null; 
	public Bitmap m_bitmap = null;
	public Canvas m_canvas = null;
	public Paint m_paint = null;
	
	public Bitmap m_bitmap2 = null;
	public Canvas m_canvas2 = null;
	
	public int m_nTextureSize = 0;

	public final ByteBuffer[] m_bufferFonts = new ByteBuffer[MaxTextures];
	public final ByteBuffer[] m_bufferBoarders = new ByteBuffer[MaxTextures];



	//public ByteBuffer m_bufferSubImage = null;

	DelayResourceQueue DelayResourceQueue = null;

	public FontCharInfo[] m_infos = new FontCharInfo[65536];
	public int m_nPixelByteSize = 4;

	public volatile boolean IsReady = false;

	public static class PageRenderer implements Runnable
	{
		public Bitmap m_bitmap;
		public Canvas m_canvas;
		public ByteBuffer m_bufferFonts;
		public ByteBuffer m_bufferBoarders;
		public List<FontCharInfo> m_listInfos;

		public  PageRenderer(int x, int y)
		{

		}

		public void run()
		{
		}
	}




	public Font(DelayResourceQueue drq, int nTextureSize, int nFontSize, int nBoarder, int nGap)
	{
		//super( new ThreadGroup( "fontgp" ), "font" );
		
		DelayResourceQueue = drq;
		m_nTextureSize = nTextureSize;
		m_nFontSize = nFontSize;
		m_nBoarder = nBoarder;
		m_nGap = nGap;
		m_nRectSize = m_nFontSize + m_nBoarder * 2 + m_nGap * 2;

		Bitmap.Config bitmapConfig = ((m_nPixelByteSize == 2) ?Bitmap.Config.ARGB_4444: Bitmap.Config.ARGB_8888);

		m_encoder = Charset.forName("sjis").newEncoder(); 
		m_bitmap = Bitmap.createBitmap(m_nRectSize, m_nRectSize, bitmapConfig);
		m_bitmap.setHasAlpha(true);
		//m_src = new int[m_nRectSize*m_nRectSize];
		//m_dst = new int[m_nRectSize*m_nRectSize];

		m_canvas = new Canvas(m_bitmap);

		m_paint = new Paint();
		m_paint.setColor(Color.WHITE);
		m_paint.setTextSize(m_nFontSize);
		m_paint.setAntiAlias(false);

		m_bitmap2 = Bitmap.createBitmap(m_nTextureSize, m_nTextureSize, bitmapConfig);
		m_bitmap2.setHasAlpha(true);
			//m_bitmap2.setHasAlpha(true);
		m_canvas2 = new Canvas(m_bitmap2);
		
		
		//m_bitmapFont = Bitmap.createBitmap( m_nTextureSize, m_nTextureSize, Bitmap.Config.ARGB_4444 );
		//m_bitmapFont.setHasAlpha( true );
		//m_bitmapBoarder = Bitmap.createBitmap( m_nTextureSize, m_nTextureSize, Bitmap.Config.ARGB_4444 );
		//m_bitmapBoarder.setHasAlpha( true );
		Bitmap bitmap = Bitmap.createBitmap(m_nTextureSize, m_nTextureSize, bitmapConfig);
		bitmap.setHasAlpha(true);
		for (int i = 0 ; i < m_textureFonts.length ; i++)
		{
			//m_textureFonts[i] = new StaticTexture(drq, bitmap, false);
			//m_textureBoarders[i] = new StaticTexture(null, bitmap, false);

			m_bufferFonts[i] = ByteBuffer.allocateDirect(m_nTextureSize * m_nTextureSize * m_nPixelByteSize);
			m_bufferFonts[i].order(ByteOrder.nativeOrder());
			m_bufferBoarders[i] = ByteBuffer.allocateDirect(m_nTextureSize * m_nTextureSize * m_nPixelByteSize);
			m_bufferBoarders[i].order(ByteOrder.nativeOrder());
			//
		}
		//m_bufferSubImage = ByteBuffer.allocateDirect(m_nRectSize * m_nRectSize * m_nPixelByteSize);
		//m_bufferSubImage.order(ByteOrder.nativeOrder());
		//Thread thread = Thread.currentThread();
		//this.setPriority(10);
		//drq.RegisterThread( this );
		//start();
		
		run();
	}

	//@Override
	public void run()
	{
		CreateCharInfo();
		Update2();
		IsReady = true;
	}

	public void CreateCharInfo()
	{
		int nX = 0;
		int nY = 0;
		int nChannel = 0;
		int iTexture = 1;
		int iFirstCharSet = 128;

		for (int i = 0 ; i < m_infos.length ; i++)
		{
			char c = (char) i;
			if (m_encoder.canEncode(c))
			{
				char[] cc = { c };
				if( i < iFirstCharSet )
				{
					m_infos[i] = new FontCharInfo(
							(float) nX / (float) m_nTextureSize,
							(float) nY / (float) m_nTextureSize,
							m_paint.measureText(cc, 0, 1), nChannel, 0 );
				}
				else
				{
					m_infos[i] = new FontCharInfo(
							(float) nX / (float) m_nTextureSize,
							(float) nY / (float) m_nTextureSize,
							m_paint.measureText(cc, 0, 1), nChannel, iTexture);

					nX += m_nRectSize;
					if ((nX + m_nRectSize) > m_nTextureSize)
					{
						nX = 0;
						nY += m_nRectSize;
						if ((nY + m_nRectSize) > m_nTextureSize)
						{
							nY = 0;
							nChannel += 1;

							if (nChannel > 3)
							{
								nChannel = 0;
								iTexture += 1;

								SubSystem.Log.WriteLine("Font Texture " + iTexture);

								if (iTexture >= m_textureFonts.length)
								{
									break;
								}
							}
						}
					}
				}
			}
			else
			{
				m_infos[i] = null;
			}
		}
	}
	
	public void Update2() //throws ThreadForceDestroyException
	{
		int iPrevChannel = 0;
		int iPrevTexture = 0;
		int[] pixels = new int[m_nTextureSize*m_nTextureSize];
		int[] fontPixels = new int[m_nTextureSize*m_nTextureSize];
		for (int i = 32 ; i < m_infos.length ; i++)
		{
			//this.DelayResourceQueue.TestInterrupted();
 			if (m_infos[i] == null)
			{
				continue;
			}

			int iChannel = m_infos[i].Channel;
			int iTexture = m_infos[i].Texture;
			
			if( iChannel != iPrevChannel )
			{
				m_bitmap2.getPixels(pixels,0,m_nTextureSize,0,0,m_nTextureSize,m_nTextureSize);
				m_paint.setColor(Color.BLACK);
				m_canvas2.drawRect(0, 0, m_bitmap2.getWidth(), m_bitmap2.getHeight(), m_paint);

				CopyTexturePixels(fontPixels,pixels,iPrevChannel);

				if( iTexture != iPrevTexture )
				{
					CreateTexture(iPrevTexture,fontPixels);
					iPrevTexture = iTexture;
				}
				
				iPrevChannel = iChannel;
			}
			else
			{
				char c = (char) i;

				char[] cc = { c };
				float ascent = m_paint.getFontMetrics().ascent;
				float descent = m_paint.getFontMetrics().descent;
				float bottom = m_nRectSize / 2 - (ascent + descent) / 2.0f;
				float cx = m_nGap + m_nBoarder;

				int nX = (int)(m_infos[i].U * (float)m_nTextureSize);
				int nY = (int)(m_infos[i].V * (float)m_nTextureSize);
				
				m_paint.setColor(Color.WHITE);
				m_paint.setAntiAlias(true);
 				m_canvas2.drawText( cc, 0, 1, nX + cx, nY + bottom, m_paint);
			}

			//Thread.yield();
		}

		m_bitmap2.getPixels(pixels,0,m_nTextureSize,0,0,m_nTextureSize,m_nTextureSize);
		CopyTexturePixels(fontPixels,pixels,iPrevChannel);
		CreateTexture(iPrevTexture,fontPixels);

		DelayResourceQueue.Add("フォントできたよ");
		//m_bufferFonts = null;
		//m_bufferBoarders = null;
	}

	private void CopyTexturePixels( int[] pixelsTo, int[] pixelsFrom, int iChannel )
	{
		if( iChannel <= 0 )
		{
			for( int y = 0 ; y < m_nTextureSize ; y++ )
			{
				for( int x = 0 ; x < m_nTextureSize ; x++ )
				{
					int pos = x + y*m_nTextureSize;
					int pixel = pixelsFrom[pos];
					int r = pixel & 0xff;
					int a = (pixel >> 24) & 0xff;
					pixelsTo[pos] = ((r*a) >> 8)& 0xff;
				}
			}
		}
		else if( iChannel <= 3 )
		{
			for( int y = 0 ; y < m_nTextureSize ; y++ )
			{
				for( int x = 0 ; x < m_nTextureSize ; x++ )
				{
					int pos = x + y*m_nTextureSize;
					int pixel = pixelsFrom[pos];
					int r = pixel & 0xff;
					int a = (pixel >> 24) & 0xff;

					pixelsTo[pos] |= (((r*a) >> 24) & 0xff) << (iChannel*8);
				}
			}
		}
	}

	private void CreateTexture(int iTexture, int[] fontPixels)
	{
		m_bufferFonts[iTexture].position(0);
		m_bufferBoarders[iTexture].position(0);
		for( int y = 0 ; y < m_nTextureSize ; y++ )
		{
			for( int x = 0 ; x < m_nTextureSize ; x++ )
			{
				int pos = x + y*m_nTextureSize;
				m_bufferFonts[iTexture].putInt( fontPixels[pos] );

				int r = 0;
				int g = 0;
				int b = 0;
				int a = 0;
				for (int by = -m_nBoarder ; by <= m_nBoarder ; by++)
				{
					for (int bx = -m_nBoarder ; bx <= m_nBoarder ; bx++)
					{
						int xx = x + bx;
						int yy = y + by;
						if( xx < 0 | m_nTextureSize <= xx | yy < 0 | m_nTextureSize <= yy )
						{
							continue;
						}
						int pixel = fontPixels[xx+yy*m_nTextureSize];
						r += pixel & 0xff;
						g += (pixel >> 8) & 0xff;
						b += (pixel >> 16)& 0xff;
						a += (pixel >> 24) & 0xff;
					}
				}

				int nbSamples = m_nBoarder+1;
				nbSamples *= nbSamples;
				r /= nbSamples;
				g /= nbSamples;
				b /= nbSamples;
				a /= nbSamples;

				m_bufferBoarders[iTexture].putInt
						(
								(a<<24)|(b<<16)|(g<<8)|r
						);
			}
		}
		m_bufferFonts[iTexture].position(0);
		m_textureFonts[iTexture] = new StaticTexture
				(
						DelayResourceQueue,
						TextureInternalFormat.RGBA,
						m_nTextureSize, m_nTextureSize,
						TextureSourceFormat.RGBA,
						(
								(m_nPixelByteSize == 2) ?
										TextureSourceType.UnsignedShort_4_4_4_4 :
										TextureSourceType.UnsignedByte
						),
						m_bufferFonts[iTexture],
						false
				);

		m_bufferBoarders[iTexture].position(0);
		m_textureBoarders[iTexture] = new StaticTexture
				(
						DelayResourceQueue,
						TextureInternalFormat.RGBA,
						m_nTextureSize, m_nTextureSize,
						TextureSourceFormat.RGBA,
						(
								(m_nPixelByteSize == 2) ?
										TextureSourceType.UnsignedShort_4_4_4_4 :
										TextureSourceType.UnsignedByte
						),
						m_bufferBoarders[iTexture],
						false
				);

	}
	public void Update0()
		//throws ThreadForceDestroyException
	{
		//DebugLog.Error.WriteLine(Integer.toString(m_nCode));
		for (int i = 32 ; i < m_infos.length ; i++)
		{
			if (m_infos[i] == null)
			{
				continue;
			}

			char c = (char) i;

			char[] cc = { c };
			float a = m_paint.getFontMetrics().ascent;
			float d = m_paint.getFontMetrics().descent;
			float b = m_nRectSize / 2 - (a + d) / 2.0f;
			float cx = m_nGap + m_nBoarder;


			//m_infos[m_nCode] = new FontCharInfo(
			//	(float)m_nX / (float)m_nTextureSize,
			//	(float)m_nY / (float)m_nTextureSize,
			//	m_paint.measureText(cc, 0, 1), m_nChannel, m_iTexture);

			m_paint.setColor(Color.BLACK);
			m_canvas.drawRect(0, 0, m_bitmap.getWidth(), m_bitmap.getHeight(), m_paint);

			m_paint.setColor(Color.WHITE);
			m_paint.setAntiAlias(true);
			for (int y = -m_nBoarder ; y <= m_nBoarder ; y++)
			{
				for (int x = -m_nBoarder ; x <= m_nBoarder ; x++)
				{
					m_canvas.drawText(cc, 0, 1, cx + x, b + y, m_paint);
				}
			}

			int m_nX = (int)(m_infos[i].U * (float)m_nTextureSize);
			int m_nY = (int)(m_infos[i].V * (float)m_nTextureSize);
			int m_nChannel = m_infos[i].Channel;
			int m_iTexture = m_infos[i].Texture;

			for (int y = 0 ; y < m_nRectSize ; y++)
			{
				for (int x = 0 ; x < m_nRectSize ; x++)
				{
					int r = Color.red(m_bitmap.getPixel(x, y));
					int pos = (m_nX + x + (m_nY + y) * m_nTextureSize) * m_nPixelByteSize;

					if (m_nPixelByteSize == 2)
					{
						short v = (m_nChannel == 0 ? 0 : m_bufferBoarders[m_iTexture].getShort(pos));

						v |= (short)(((r & 0xf0) >> 4) << (12 - 4 * m_nChannel));

						m_bufferBoarders[m_iTexture].putShort(pos, v);
					}
					else 
					{
						int v = (m_nChannel == 0 ? 0 : m_bufferBoarders[m_iTexture].getInt(pos));

						v |= (short)(((r & 0xff) >> 0) << (32 - 8 * m_nChannel));

						m_bufferBoarders[m_iTexture].putInt(pos, v);
					}
				}
			}

			m_paint.setColor(Color.BLACK);
			m_canvas.drawRect(0, 0, m_bitmap.getWidth(), m_bitmap.getHeight(), m_paint);

			m_paint.setAntiAlias(true);
			m_paint.setColor(Color.WHITE);
			m_canvas.drawText(cc, 0, 1, cx, b, m_paint);

			for (int y = 0 ; y < m_nRectSize ; y++)
			{
				for (int x = 0 ; x < m_nRectSize ; x++)
				{
					int r = Color.red(m_bitmap.getPixel(x, y));
					int pos = (m_nX + x + (m_nY + y) * m_nTextureSize) * m_nPixelByteSize;

					if (m_nPixelByteSize == 2)
					{
						short v = (m_nChannel == 0 ? 0 : m_bufferFonts[m_iTexture].getShort(pos));

						v |= (short)(((r & 0xf0) >> 4) << (12 - 4 * m_nChannel));

						m_bufferFonts[m_iTexture].putShort(pos, v);
					}
					else
					{

						int v = (m_nChannel == 0 ? 0 : m_bufferFonts[m_iTexture].getInt(pos));

						v |= (short)(((r & 0xff) >> 0) << (32 - 8 * m_nChannel));

						m_bufferFonts[m_iTexture].putInt(pos, v);
					}
				}
			}
			
			Thread.yield();
		}

		for (int i = 0 ; i < m_textureFonts.length ; i++)
		{
			m_textureFonts[i] = new StaticTexture(DelayResourceQueue,
												  TextureInternalFormat.RGBA,
												  m_nTextureSize, m_nTextureSize,
												  TextureSourceFormat.RGBA,
												  ((m_nPixelByteSize == 2) ? 
												  TextureSourceType.UnsignedShort_4_4_4_4 :
												  TextureSourceType.UnsignedByte),
												  m_bufferFonts[i], false);

			m_textureBoarders[i] = new StaticTexture(DelayResourceQueue,
													 TextureInternalFormat.RGBA,
													 m_nTextureSize, m_nTextureSize,
													 TextureSourceFormat.RGBA,
													 ((m_nPixelByteSize == 2) ? 
													 TextureSourceType.UnsignedShort_4_4_4_4 :
													 TextureSourceType.UnsignedByte),
													 m_bufferBoarders[i], false);

			m_bufferFonts[i] = null;
			m_bufferBoarders[i] = null;
		}

		DelayResourceQueue.Add("フォントできたよ");
		//m_bufferFonts = null;
		//m_bufferBoarders = null;

	}
}

