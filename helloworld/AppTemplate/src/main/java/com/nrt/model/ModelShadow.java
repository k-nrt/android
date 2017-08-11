package com.nrt.model;
import com.nrt.render.*;
import com.nrt.math.*;
import java.security.*;

public class ModelShadow
{
	public RenderTexture Texture = null;
	
	public final Float4x4 World = Float4x4.Identity();
	public final Float4x4 View = Float4x4.Identity();
	public final Float4x4 Projection = Float4x4.Identity();
	
	private boolean m_isDirty = true;
	
	private final Float4x4 m_matrixWorldViewProjection = com.nrt.math.Float4x4.Identity();
	private final Float3 m_f3ShadowDirection = new Float3();
	
	public Float4x4 GetWorldViewProjection()
	{
		if( m_isDirty )
		{
			Float4x4.Mul( 
				m_matrixWorldViewProjection,
				World, 
				View );
			Float4x4.Mul( 
				m_matrixWorldViewProjection,
				m_matrixWorldViewProjection,
				Projection );
				
			m_isDirty = false;			
		}
		
		return m_matrixWorldViewProjection;
	}
	
	public Float3 GetShadowDirection()
	{
		return m_f3ShadowDirection;
	}
	
	public void SetWorld( Float4x4 matrixWorld )
	{
		World.Set( matrixWorld );	
		m_isDirty = true;
	}
	
	public void SetOrthoProjection( Float3 f3Center, Float3 f3Direction, float fRange, float fDepth )
	{
		Float3 f3Eye = f3Center;
		Float3 f3At = Float3.Mad( Float3.Local(), f3Direction, -fDepth, f3Center );
		
		Float3 f3Up = Float3.Local( 0.0f, 1.0f, 0.0f );
		
		Float3 f3Cross = Float3.Cross( Float3.Local(), Float3.Sub( Float3.Local(), f3At, f3Eye ), f3Up );
		
		if( Float3.LengthSquared( f3Cross ) <= 0.0f )
		{
			f3Up.Set( 0.0f, 0.0f, 1.0f );
		}
		
		Float4x4.Ortho( Projection, -fRange, fRange, -fRange, fRange, -fDepth, fDepth );
		Float4x4.LookAt( View, f3Eye, f3At, f3Up );


		
		m_f3ShadowDirection.Set( f3Direction );		
		m_isDirty = true;
	}
}

