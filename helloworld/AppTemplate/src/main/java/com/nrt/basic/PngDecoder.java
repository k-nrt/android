package com.nrt.basic;

import java.io.*;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.Color;
import java.util.zip.*;
import android.sax.*;
import java.nio.*;
import android.opengl.*;

public class PngDecoder
{
	public static Bitmap Image = null;
	public static void Print( TextView view, String strText )
	{
		if( view != null )
		{
			view.append( strText );
		}
		else
		{
			DebugLog.Error.WriteLine( strText );
			/*
			int a = 0;
			for( int i = 0 ; i < 1000000 ; i++ )
			{
				a= (int) Math.pow( a+i, (double) i );
			}
			*/
		}
		
	}
	
	public static RawImage Decode(InputStream stream, TextView view ) throws Exception
	{		
		//view = null;
		byte[] signeture = ReadBytes(stream, 8);

		IHDR ihdr = null;
		Inflater inflater = new Inflater();
		byte[] decompressedData = null;//new byte[ihdr.Width*ihdr.Height*4];

		while (true)
		{
			int length = ReadInteger(stream);
			String type = ReadString(stream, 4);
			byte[] data= null;
			if (length > 0)
			{
				data = ReadBytes(stream, length);
			}
			int crc = ReadInteger(stream);

			Print( view, type + " " + Integer.toString(length) + "\n" );
			

			if (type.compareTo("IHDR") == 0)
			{
				ihdr = new IHDR(type, data, crc);
				if (ihdr.BitDepth != 8)
				{
					throw new Exception(String.format("bit depth %d is not supported.", ihdr.BitDepth));
				}

				if (ihdr.Compression != 0)
				{
					throw new Exception(String.format("compression method %d is not supported.", ihdr.Compression));
				}

				if (ihdr.Filter != 0)
				{
					throw new Exception(String.format("filter method %d is not supported.", ihdr.Filter));
				}

				if (ihdr.Interace != 0)
				{
					throw new Exception(String.format("interace method %d is not supported.", ihdr.Interace));
				}

				decompressedData = new byte[ihdr.GetDecompressedSize()];

				Print( view, String.format("%dx%d %d %s %d %d %d\n",
											  ihdr.Width, ihdr.Height, ihdr.BitDepth, ihdr.ColorType,
											  ihdr.Compression, ihdr.Filter, ihdr.Interace));
				}
			else if (type.compareTo("IDAT") == 0)
			{
				inflater.setInput(data);

				if (inflater.needsInput() == false)
				{
					int pos = (int) inflater.getBytesWritten();
					int remains = decompressedData.length - pos;

					int size = inflater.inflate(decompressedData, pos, remains);

					Print( view, String.format("inflate %d/%d\n", pos + size, decompressedData.length));
					
				}
			}
			else if (type.compareTo("IEND") == 0)
			{
				break;
			}
		}
		inflater.end();
		stream.close();

		ByteBuffer pixels = ByteBuffer.allocateDirect(ihdr.Width * ihdr.Height * ihdr.GetChannelCount());


		int[] colors = new int[ihdr.Width * ihdr.Height];
		int stride = ihdr.GetScanlineSize();
		int channels= ihdr.GetChannelCount();
		for (int y = 0 ; y < ihdr.Height ; y++)
		{
			int pos = ihdr.GetScanlineSize() * y;
			int filter = decompressedData[pos];
			pos++;

			Print( view, String.format("%d %d\n", y, filter));

			if (filter == 0)
			{
				None(decompressedData, stride, channels, pos);
			}
			else if (filter == 1)
			{
				Sub(decompressedData, stride, channels, pos);
			}
			else if (filter == 2)
			{
				Up(decompressedData, stride, channels, pos);				
			}
			else if (filter == 3)
			{
				Average(decompressedData, stride, channels, pos);
			}
			else if (filter == 4)
			{
				Paeth(decompressedData, stride, channels, pos);				
			}
			else
			{
				throw new Exception("unknown filter type.");
			}

			for (int x = 0 ; x < ihdr.Width ; x++)
			{
				int color = Color.argb(
					0xff,//decompressedData[pos+3],
					((int)decompressedData[pos + 0]) & 0xff,
					((int)decompressedData[pos + 1]) & 0xff,
					((int)decompressedData[pos + 2]) & 0xff);

				colors[x + y * ihdr.Width] = color;
				pixels.put(decompressedData, pos, channels);

				pos += channels;
			}
		}
		pixels.position(0);

		Image = Bitmap.createBitmap(colors, ihdr.Width, ihdr.Height, Bitmap.Config.ARGB_8888);

		RawImage image = new RawImage();
		image.Width = ihdr.Width;
		image.Height = ihdr.Height;

		switch (ihdr.ColorType)
		{
			case GrayScale: image.Format = GLES20.GL_LUMINANCE; break;					
			case TrueColor: image.Format = GLES20.GL_RGB; break;					
			case IndexColor: image.Format = GLES20.GL_LUMINANCE; break;					
			case GrayScaleAlpha: image.Format = GLES20.GL_LUMINANCE_ALPHA; break;
			case TrueColorAlpha: image.Format = GLES20.GL_RGBA; break;
		}

		image.Type = GLES20.GL_UNSIGNED_BYTE;

		image.Pixels = pixels;

		Print( view, String.format("decompressed\n"));

		return image;
	}

	public static void None(byte[] data, int stride, int channels, int pos)
	{
	}

	public static void Sub(byte[] data, int stride, int channels, int pos)
	{
		for (int x = 0 ; x < stride - 1; x++)
		{
			int left = pos - channels;
			if (x < channels)
			{
				//data[start+x] = decompressedData[pos];
			}
			else
			{
				data[pos] = (byte) (ToUInt32(data[pos]) + ToUInt32(data[left]));
			}
			pos++;
		}

	}

	public static void Up(byte[] data, int stride, int channels, int pos)
	{
		for (int x = 0 ; x < stride - 1; x++)
		{
			int up = pos - stride;
			if (up < 0)
			{
				//data[start+x] = decompressedData[pos];
			}
			else
			{
				data[pos] = (byte) (ToUInt32(data[pos]) + ToUInt32(data[up]));
			}
			pos++;
		}
	}

	public static void Average(byte[] data, int stride, int channels, int pos)
	{
		for (int x = 0 ; x < stride - 1; x++)
		{
			int up = pos - stride;
			int left = pos - channels;
			if (up < 0)
			{
				if (x < channels)
				{

				}
				else
				{
					data[pos] = (byte) (ToUInt32(data[pos]) + ToUInt32(data[left]) / 2);
				}
			}
			else
			{
				if (x < channels)
				{
					data[pos] = (byte) (ToUInt32(data[pos]) + ToUInt32(data[up]) / 2);

				}
				else
				{
					data[pos] = (byte) (ToUInt32(data[pos]) + (ToUInt32(data[up]) + ToUInt32(data[left])) / 2);
				}
			}
			pos++;
		}
	}

	public static void Paeth(byte[] data, int stride, int channels, int pos)
	{
		for (int x = 0 ; x < stride - 1; x++)
		{
			int up = pos - stride;
			int left = pos - channels;
			if (up < 0)
			{
				if (x < channels)
				{

				}
				else
				{
					data[pos] = (byte) ((int) data[pos] + Paeth(data[left], (byte)0, (byte)0));
				}
			}
			else
			{
				if (x < channels)
				{
					data[pos] = (byte) ((int) data[pos] + Paeth((byte) 0, data[up], (byte)0));
				}
				else
				{
					data[pos] = (byte) ((int) data[pos] + Paeth(data[left], data[up], data[up - channels]));
				}
			}
			pos++;
		}

	}


	public static int Paeth(byte sa, byte sb, byte sc)
	{
		// sc sb
		// sa xx

		int a = ToUInt32(sa);
		int b = ToUInt32(sb);
		int c = ToUInt32(sc);
		int p = a + b - c;
		int pa = abs(p - a);
		int pb = abs(p - b);
		int pc = abs(p - c);
		if (pa <= pb && pa <= pc)
		{
			return a;
		}
		else if (pb <= pc)
		{
			return b;
		}
		else
		{
			return c;
		}
		/*
		 p = a + b - c
		 pa = abs(p - a)
		 pb = abs(p - b)
		 pc = abs(p - c)
		 if pa <= pb and pa <= pc then Pr = a
		 else if pb <= pc then Pr = b
		 else Pr = c
		 return Pr
		 */
	}

	private static int abs(int v)
	{
		return ((v < 0) ? -v : v);
	}

	private static int ToUInt32(byte v)
	{
		return ((int)v) & 0xff;
	}

	public static byte[] ReadBytes(InputStream stream, int size)
	{
		byte[] result = new byte[size];

		try
		{
			stream.read(result);
		}
		catch ( Exception e )
		{

		}
		return result;
	}

	public static int ReadInteger(InputStream stream)
	{
		byte[] data = ReadBytes(stream, 4);
		return (((int)data[0]) & 0xff) << 24 |
			(((int)data[1]) & 0xff) << 16 |
			(((int)data[2]) & 0xff) << 8 |
			(((int)data[3]) & 0xff);
	}
	public static String ReadString(InputStream stream, int length)
	{
		byte[] data = ReadBytes(stream, length);
		String result = "";
		for (byte value : data)
		{
			result += (char) value;
		}
		return result;
	}

	static class Chunk
	{
		public String Type;
		public byte[] Data;
		public int CRC;

		public Chunk(String type, byte[] data, int crc)
		{
			Type = type;
			Data = data;
			CRC = crc;
		}

		public static int GetInteger(byte[] data, int pos)
		{
			return (((int)data[pos + 0]) & 0xff) << 24 |
				(((int)data[pos + 1]) & 0xff) << 16 |
				(((int)data[pos + 2]) & 0xff) << 8 |
				(((int)data[pos + 3]) & 0xff);

		}
	}

	static class IHDR extends Chunk
	{
		public enum EColorType
		{
			GrayScale(0),
			TrueColor(2),
			IndexColor(3),
			GrayScaleAlpha(4),
			TrueColorAlpha(6);

			public int Value = 0;
			private EColorType( int value ) { Value = value; }

			public static EColorType FromInteger( int value )
			{
				switch ( value )
				{
					case 0: return GrayScale;
					case 2: return TrueColor;
					case 3: return IndexColor;
					case 4: return GrayScaleAlpha;
					case 6: return TrueColorAlpha;
					default: return TrueColorAlpha;
				}

			}			
		}

		public int Width;
		public int Height;
		public int BitDepth;
		public EColorType ColorType;
		public int Compression;
		public int Filter;
		public int Interace;

		public IHDR(String type, byte[] data, int crc)
		{
			super(type, data, crc);

			Width = GetInteger(data, 0);
			Height = GetInteger(data, 4);

			BitDepth = data[8];

			ColorType = EColorType.FromInteger(data[9]);
			Compression = data[10];
			Filter = data[11];
			Interace = data[12];
		}

		public int GetDecompressedSize()
		{
			return Height * GetScanlineSize();
		}

		public int GetScanlineSize()
		{
			switch (ColorType)
			{
				case GrayScale: return 1 + (Width * 1 * BitDepth) / 8;
				case TrueColor: return 1 + (Width * 3 * BitDepth) / 8;
				case IndexColor: return 1 + (Width * 1 * BitDepth) / 8;
				case GrayScaleAlpha: return 1 + (Width * 2 * BitDepth) / 8;
				case TrueColorAlpha: return 1 + (Width * 4 * BitDepth) / 8;
			}

			return 0;
		}

		public int GetChannelCount()
		{
			switch (ColorType)
			{
				case GrayScale: return 1;
				case TrueColor: return 3;
				case IndexColor: return 1;
				case GrayScaleAlpha: return 2;
				case TrueColorAlpha: return 4;
			}

			return 0;
		}
	}

}
