package com.nrt.helloworld;
import com.nrt.model.*;
import com.nrt.math.*;
import java.security.*;
import com.nrt.clipper.*;
//import android.transition.*;
import com.nrt.render.*;

public class GameModel
{
	public Model Model = null;
	public FrameBuffer ShadowFrameBuffer = null;
	public final ModelShadow Shadow = new ModelShadow();
	public final Float4x4 Transform = Float4x4.Identity();
	
	private final Box m_box = new Box();
	private final Box m_boxScissor = new Box();
	
	public boolean IsVisible = false;
	public boolean IsFrustumCulling = false;
	
	public void SetShadowBuffer( FrameBuffer frameBuffer )
	{
		ShadowFrameBuffer = frameBuffer;
		Shadow.Texture = frameBuffer.ColorRenderTexture;
	}
	
	public void UpdateTransform( Float4x4 matrixTransform )
	{
		Transform.Set( matrixTransform );
		
		if( Model != null )
		{
			m_box.Update( Model.Min, Model.Max, Transform );
		}
	}
	
	public void UpdateFrustumCulling( Frustum frustum )
	{
		if( Model != null )
		{
			m_box.ScissorBackFace( m_boxScissor, frustum.Surfaces );
			if( m_boxScissor.PolygonCount <= 0 )
			{
				IsFrustumCulling = true;
			}
			else
			{
				IsFrustumCulling = false;
			}
		}
		else
		{
			IsFrustumCulling = false;
		}		
	}
	
	public boolean IsVible()
	{
		if( IsVisible && IsFrustumCulling == false && Model != null )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}

