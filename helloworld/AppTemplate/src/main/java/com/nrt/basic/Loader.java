package com.nrt.basic;

import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

import android.content.res.*;
import android.view.*;
import android.graphics.*;

import com.nrt.basic.RawImage;
import com.nrt.basic.PngDecoder;

public class Loader
{
	public AssetManager m_assetManager = null;

	public Loader(AssetManager assetManager)
	{
		m_assetManager = assetManager;
	}

	public String[] LoadTextFile(String strAssetName)
	{
		List<String> listResult = new ArrayList<String>();

		try
		{
			InputStream stream = m_assetManager.open(strAssetName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

			try
			{
				String strLine;
				while ((strLine = reader.readLine()) != null)
				{
					listResult.add(strLine);
				}
			}
			finally
			{
				reader.close();
			}
		}
		catch ( IOException ex )
		{

		}

		return listResult.toArray(new String[listResult.size()]);
	}

	public InputStream OpenAsset(String strAssetName)
	{
		try
		{
			return m_assetManager.open(strAssetName);	
		}
		catch ( IOException e )
		{
			return null;
		}
	}

	public Bitmap LoadImage(String strAssetName)
	{
		InputStream stream = OpenAsset(strAssetName);
		Bitmap result = null;
		try
		{
			result = BitmapFactory.decodeStream(stream);
			stream.reset();
			stream.close();
		}
		catch ( IOException e )
		{

		}


		return result;
	}

	public RawImage LoadPng( String strAssetName )
	{
		try
		{
			InputStream stream = OpenAsset(strAssetName);
			if( stream != null )
			{
				RawImage result = PngDecoder.Decode( stream, null );
				//stream.close();
				return result;
			}
			else
			{
				return null;
			}
		}
		catch ( Exception e )
		{
			DebugLog.Error.WriteLine( e.getMessage() );
			return null;
		}
	}
	
}
