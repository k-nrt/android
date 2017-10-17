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

import com.nrt.basic.Job;
import com.nrt.basic.JobStatus;
import java.util.concurrent.atomic.*;
import com.nrt.basic.*;

public class Font
{
	final int MaxTextures = 4;

	public final StaticTexture[] m_textureFonts = new StaticTexture[MaxTextures];
	public final StaticTexture[] m_textureBoarders = new StaticTexture[MaxTextures];

	public int m_nFontSize = 0;
	public int m_nBoarder = 0;
	public int m_nGap = 0;
	public int m_nRectSize = 0;
	
	public int m_nTextureSize = 0;
	
/*
	public CharsetEncoder m_encoder = null; 
	public Bitmap m_bitmap = null;
	public Canvas m_canvas = null;
	public Paint m_paint = null;
	
	public Bitmap m_bitmap2 = null;
	public Canvas m_canvas2 = null;
	

	public final ByteBuffer[] m_bufferFonts = new ByteBuffer[MaxTextures];
	public final ByteBuffer[] m_bufferBoarders = new ByteBuffer[MaxTextures];
*/


	//public ByteBuffer m_bufferSubImage = null;

	//DelayResourceQueue DelayResourceQueue = null;

	public FontCharInfo[] m_infos = new FontCharInfo[65536];
	//public int m_nPixelByteSize = 4;

	public boolean IsReady()
	{
		return m_markerTextures.Done;
	}
	
	public AtomicInteger m_textureCounter = new AtomicInteger();
	public AtomicInteger m_requestedTextures = new AtomicInteger();
	public DelayResourceQueueMarker m_markerTextures = new DelayResourceQueueMarker
	(
		"Font textures"
	);
	
	public static class CharInfoFactory implements Job
	{
		public JobScheduler m_jobScheduler = null;
		public DelayResourceQueue m_delayResourceQueue = null;
		public int m_width = 0;
		public int m_height = 0;
		public int m_fontSize = 0;
		public int m_rectSize = 0;
		public int m_gapSize = 0;
		public int m_borderSize = 0;
		public FontCharInfo[] m_infos = null;
		public StaticTexture[] m_textureFonts = null;
		public StaticTexture[] m_textureBorders = null;
		public AtomicInteger m_textureCounter = null;
		public AtomicInteger m_requestedTextures = null;
		
		public CharsetEncoder m_encoder = null; 
		
		public CharInfoFactory
		(
			JobScheduler jobScheduler,
			DelayResourceQueue delayResourceQueue,		
			int width, int height, 
			int fontSize, int rectSize, int gapSize, int borderSize,
			FontCharInfo[] infos,
			StaticTexture[] textureFonts, StaticTexture[] textureBorders,
			AtomicInteger textureCounter,
			AtomicInteger requestedTextures
			
		)
		{
			m_jobScheduler = jobScheduler;
			m_delayResourceQueue = delayResourceQueue;			
			m_width = width;
			m_height = height;
			m_fontSize = fontSize;
			m_rectSize = rectSize;
			m_gapSize = gapSize;
			m_borderSize = borderSize;
			m_infos = infos;			
			m_textureFonts = textureFonts;
			m_textureBorders = textureBorders;
			m_textureCounter = textureCounter;
			m_requestedTextures = requestedTextures;
			
			m_encoder = Charset.forName("sjis").newEncoder();
		}
		
		public JobStatus Run()
		{
			CreateCharInfo();
			return JobStatus.Done;
		}
		
		public void CreateCharInfo()
		{
			int nX = 0;
			int nY = 0;
			int nbChannels = 0;
			int startChar = 0;
			
			Paint paint = new Paint();
			paint.setTextSize(m_fontSize);
			
			TextureRenderer textureRenderer = null;
			FontRenderer[] fontRenderers = new FontRenderer[4];
			
			//AtomicInteger channelCounter = null;
			
			for (int i = 0 ; i < m_infos.length ; i++)
			{
				char c = (char) i;
				if (m_encoder.canEncode(c))
				{
					char[] cc = { c };
					m_infos[i] = new FontCharInfo
					(
						(float) nX / (float) m_width,
						(float) nY / (float) m_height,
						paint.measureText(cc, 0, 1),
						nbChannels%4,
						nbChannels/4
					);

					nX += m_rectSize;
					if (m_width < (nX + m_rectSize))
					{
						nX = 0;
						nY += m_rectSize;
						if (m_height < (nY + m_rectSize) )
						{
							if( (nbChannels%4) == 0 )
							{
								int textureIndex = nbChannels/4;
								
								if( textureIndex < m_textureFonts.length )
								{								
									textureRenderer = new TextureRenderer
									(
										m_delayResourceQueue, 
										m_width, m_height, 
										m_textureFonts, m_textureBorders, textureIndex,
										m_textureCounter
									);
																
									m_jobScheduler.Add( textureRenderer );
									int counter = m_requestedTextures.getAndIncrement();
									SubSystem.Log.WriteLine( "New TextureRenderer " + counter);
								}
								else
								{
									textureRenderer = null;
								}
							}
							
							if( textureRenderer != null )
							{
								FontRenderer fontRenderer = new FontRenderer
								(
									m_width,m_height,
									m_fontSize,m_rectSize,m_gapSize,m_borderSize,
									m_infos, startChar, i+1,
									textureRenderer.m_channelCounter
								);
								m_jobScheduler.Add(fontRenderer);
								
								int channel = nbChannels%4;
								fontRenderers[channel] = fontRenderer;
								
								if(channel==3)
								{
									textureRenderer.SetFontRenderer
									(
										fontRenderers[0],
										fontRenderers[1],
										fontRenderers[2],
										fontRenderers[3]
									);
								}
							}
							
							nY = 0;
							nbChannels++;
							startChar = i+1;
						}
					}
				}
				else
				{
					m_infos[i] = null;
				}
			}
			
			if( (nbChannels%4) == 0 )
			{
				int textureIndex = nbChannels/4;
				if( textureIndex < m_textureFonts.length )
				{
					textureRenderer = new TextureRenderer
					(
						m_delayResourceQueue, 
						m_width, m_height, 
						m_textureFonts, m_textureBorders, textureIndex,
						m_textureCounter
					);

					m_jobScheduler.Add( textureRenderer );
					int counter = m_requestedTextures.getAndIncrement();
					SubSystem.Log.WriteLine( "New TextureRenderer " + counter);
				}
				else
				{
					textureRenderer = null;
				}
			}

			if( textureRenderer != null )
			{
				FontRenderer fontRenderer = new FontRenderer
				(
					m_width,m_height,
					m_fontSize,m_rectSize,m_gapSize,m_borderSize,
					m_infos, startChar, m_infos.length,
					textureRenderer.m_channelCounter
				);
				m_jobScheduler.Add(fontRenderer);
				int channel = nbChannels%4;
				fontRenderers[channel] = fontRenderer;
				
				for(int i = channel + 1; i < 4 ; i++ )
				{
					fontRenderer = new FontRenderer
					(
						m_width,m_height,
						m_fontSize,m_rectSize,m_gapSize,m_borderSize,
						m_infos, 0, 0,
						textureRenderer.m_textureCounter
					);
					m_jobScheduler.Add(fontRenderer);
					fontRenderers[i] = fontRenderer;
				}
				
				textureRenderer.SetFontRenderer
				(
					fontRenderers[0],
					fontRenderers[1],
					fontRenderers[2],
					fontRenderers[3]
				);				
			}
		}
	}

	public static class FontRenderer implements Job
	{
		public Bitmap m_bitmap;
		public Canvas m_canvas;
		
		public FontCharInfo[] m_infos;
		public int m_start;
		public int m_end;
		public int[] m_fontPixels;
		public int[] m_borderPixels;
		
		public int m_fontSize;
		public int m_rectSize;
		public int m_gapSize;
		public int m_borderSize;
		
		public AtomicInteger m_channelCounter;
		
		public FontRenderer
		(
			int width, int height, 
			int fontSize, int rectSize, int gapSize, int borderSize,
			FontCharInfo[] infos, int start, int end,
			AtomicInteger channelCounter
		)
		{
			m_bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
			m_bitmap.setHasAlpha(false);
			m_fontSize = fontSize;
			m_rectSize = rectSize;
			m_gapSize = gapSize;
			m_borderSize = borderSize;
			m_infos = infos;
			m_start = start;
			m_end = end;
			m_channelCounter = channelCounter;
		}

		public JobStatus Run()
		{
			DrawFonts();
			CreatePixels();
			int counter = m_channelCounter.getAndIncrement();
			SubSystem.Log.WriteLine("FontRenderer Done "+counter);
			return JobStatus.Done;
		}
		
		public void DrawFonts()
		{
			if( m_end <= m_start )
			{
				return;
			}
			
			Canvas canvas = new Canvas(m_bitmap);
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTextSize(m_fontSize);
			paint.setAntiAlias(true);
			
			int width = m_bitmap.getWidth();
			int height = m_bitmap.getHeight();
			
			for(int i = m_start ; i < m_end ; i++ )
			{
				if( m_infos[i] == null )
				{
					continue;
				}
				
				char c = (char) i;
				char[] cc = { c };

				float ascent = paint.getFontMetrics().ascent;
				float descent = paint.getFontMetrics().descent;
				float bottom = m_rectSize / 2 - (ascent + descent) / 2.0f;
				float cx = m_gapSize + m_borderSize;

				int nX = (int)(m_infos[i].U * (float)width);
				int nY = (int)(m_infos[i].V * (float)height);
				
				canvas.drawText( cc, 0, 1, nX + cx, nY + bottom, paint);
			}			
		}
		
		public void CreatePixels()
		{
			int width = m_bitmap.getWidth();
			int height = m_bitmap.getHeight();
			m_fontPixels = new int[width*height];		
			m_borderPixels = new int[width*height];
			if(m_end <= m_start)
			{
				return;
			}
			
			m_bitmap.getPixels(m_fontPixels,0,width,0,0,width,height);
			
			for( int y = 0 ; y < height ; y++ )
			{
				for( int x = 0 ; x < width ; x++ )
				{					
					int pixel = 0;
					int samples = 0;

					for (int by = -m_borderSize ; by <= m_borderSize ; by++)
					{
						for (int bx = -m_borderSize ; bx <= m_borderSize ; bx++)
						{
							int xx = x + bx;
							int yy = y + by;
							samples++;
							
							if( xx < 0 | width <= xx | yy < 0 | height <= yy )
							{
								continue;
							}
							pixel += m_fontPixels[xx+yy*width];
						}
					}
					pixel /= samples;
					m_borderPixels[x+y*width] = pixel;                                    
				}
			}
		}
	}

	public static class TextureRenderer implements Job
	{
		public AtomicInteger m_channelCounter = new AtomicInteger();
		public FontRenderer[] m_fontRenderers = new FontRenderer[4];
		
		public DelayResourceQueue m_delayResourceQueue = null;
		public int m_width = 0;
		public int m_height = 0;
		
		public StaticTexture[] m_textureFonts = null;
		public StaticTexture[] m_textureBorders = null;
		public int m_textureIndex = 0;
		
		public AtomicInteger m_textureCounter = null;
		
		public TextureRenderer
		(
			DelayResourceQueue delayResourceQueue, 
			int width, int height, 
			StaticTexture[] textureFonts, StaticTexture[] textureBorders, int textureIndex,
			AtomicInteger textureCounter
		)
		{
			m_delayResourceQueue = delayResourceQueue;
			m_width = width;
			m_height = height;
			
			m_textureFonts = textureFonts;
			m_textureBorders = textureBorders;
			
			m_textureIndex = textureIndex;			
			m_textureCounter = textureCounter;
		}
		
		public void SetFontRenderer(FontRenderer r, FontRenderer g, FontRenderer b, FontRenderer a )
		{
			m_fontRenderers[0] = r;
			m_fontRenderers[1] = g;
			m_fontRenderers[2] = b;
			m_fontRenderers[3] = a;
		}
		
		public JobStatus Run()
		{
			if( m_channelCounter.get() < 4 )
			{
				//SubSystem.Log.WriteLine(  " " +m_channelCounter.get() );
				return JobStatus.Yield;
			}
			else
			{
				CreateTexture();
				int counter = m_textureCounter.getAndIncrement();
				SubSystem.Log.WriteLine("TextureRenderer Done "+counter);
				
				return JobStatus.Done;
			}
		}
		
		public void CreateTexture()
		{
			int bufferSize = m_width*m_height*4;
			ByteBuffer bufferFont = ByteBuffer.allocateDirect(bufferSize);
			ByteBuffer bufferBorder = ByteBuffer.allocateDirect(bufferSize);

			for( int y = 0 ; y < m_height ; y++ )
			{
				for( int x = 0 ; x < m_width ; x++ )
				{
					int pos = x + y*m_width;
					int fontPixel = 0;
					int borderPixel = 0;
					fontPixel |= ((m_fontRenderers[0].m_fontPixels[pos] & 0xff0000) >> 16) << 16;
					borderPixel |= ((m_fontRenderers[0].m_borderPixels[pos] & 0xff0000) >> 16) << 16;
					fontPixel |= ((m_fontRenderers[0].m_fontPixels[pos] & 0xff0000) >> 16) << 8;
					borderPixel |= ((m_fontRenderers[0].m_borderPixels[pos] & 0xff0000) >> 16) << 8;
					fontPixel |= ((m_fontRenderers[0].m_fontPixels[pos] & 0xff0000) >> 16) << 0;
					borderPixel |= ((m_fontRenderers[0].m_borderPixels[pos] & 0xff0000) >> 16) << 0;
					fontPixel |= ((m_fontRenderers[0].m_fontPixels[pos] & 0xff0000) >> 16) << 24;
					borderPixel |= ((m_fontRenderers[0].m_borderPixels[pos] & 0xff0000) >> 16) << 24;
					
					
					bufferFont.putInt( fontPixel );
					bufferBorder.putInt( borderPixel );                               
				}
			}
			bufferFont.position(0);
			m_textureFonts[m_textureIndex] = new StaticTexture
			(
				m_delayResourceQueue,
				TextureInternalFormat.RGBA,
				m_width, m_height,
				TextureSourceFormat.RGBA,
				TextureSourceType.UnsignedByte,
				bufferFont,
				false
			);

			bufferBorder.position(0);
			m_textureBorders[m_textureIndex] = new StaticTexture
			(
				m_delayResourceQueue,
				TextureInternalFormat.RGBA,
				m_width, m_height,
				TextureSourceFormat.RGBA,
				TextureSourceType.UnsignedByte,
				bufferBorder,
				false
			);
			
			SubSystem.Log.WriteLine("Create FontTexture");
		}
	}
	
	public static class TextureMarker implements Job
	{
		public DelayResourceQueue m_delayResourceQueue = null;
		public AtomicInteger m_textureCounter = null;
		public AtomicInteger m_requestedTextures = null;
		
		public DelayResourceQueueMarker m_markerTextures = null;
		
		public TextureMarker
		(
			DelayResourceQueue delayResourceQueue,
			AtomicInteger textureCounter,
			AtomicInteger requestedTextures,
			DelayResourceQueueMarker markerTextures
		)
		{
			m_delayResourceQueue = delayResourceQueue;
			m_textureCounter = textureCounter;
			m_requestedTextures = requestedTextures;
			m_markerTextures = markerTextures;
		}
		
		public JobStatus Run()
		{
			int requestedTextures = m_requestedTextures.get();
			int processedTextures = m_textureCounter.get();
			
			if( 0 < requestedTextures && requestedTextures == processedTextures )
			{
				m_delayResourceQueue.Add( m_markerTextures );
				return JobStatus.Done;
			}
			else
			{
				return JobStatus.Yield;
			}
		}
	}

	public Font( JobScheduler jobScheduler, DelayResourceQueue drq, int nTextureSize, int nFontSize, int nBoarder, int nGap)
	{
		//DelayResourceQueue = drq;
		m_nTextureSize = nTextureSize;
		m_nFontSize = nFontSize;
		m_nBoarder = nBoarder;
		m_nGap = nGap;
		m_nRectSize = m_nFontSize + m_nBoarder * 2 + m_nGap * 2;
		
		CharInfoFactory charInfoFactory = new CharInfoFactory
		(
			jobScheduler,
			drq,
			nTextureSize, nTextureSize,
			m_nFontSize, m_nRectSize, m_nGap, m_nBoarder,
			m_infos,
			m_textureFonts, m_textureBoarders,
			m_textureCounter,
			m_requestedTextures
		);
		
		jobScheduler.Add( charInfoFactory );
		
		TextureMarker textureMarker = new TextureMarker
		(
			drq,
			m_textureCounter,
			m_requestedTextures,
			m_markerTextures
		);
		
		jobScheduler.Add( textureMarker );
	}
}

