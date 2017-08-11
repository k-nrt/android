package com.nrt.render;

import android.opengl.Matrix;
import com.nrt.math.Float4x4;

public class MatrixCache
{
	public enum EMatrix
	{
		World(0),
		View(1),
		Projection(2),
		WorldView(3),
		ViewProjection(4),
		WorldViewProjection(5),
		/*
		WorldInverse,
		ViewInverse,
		ProjectionInverse,
		WorldViewInverse,
		ViewProjectionInverse,
		WorldViewProjectionInverse,
		
		WorldTranspose,
		ViewTranspose,
		ProjectionTranspose,
		WorldViewTranspose,
		ViewProjectionTranspose,
		WorldViewProjectionTranspose,
		
		WorldInverseTranspose,
		ViewInverseTranspose,
		ProjectionInverseTranspose,
		WorldViewInverseTranspose,
		ViewProjectionInverseTranspose,
		WorldViewProjectionInverseTranspose,
		*/
		Max(6);
		
		public int Index = 0;
		public int Mask = 0;
		public int ResetMask = -1;
		
		private EMatrix(int index)
		{
			Index = index;
			Mask = 1 << index;
			ResetMask = ~Mask;
		}
	};

	public final Float4x4[] m_arrayMatrices = new Float4x4[EMatrix.Max.ordinal()];
	public final Float4x4[] m_arrayInverseMatrices = new Float4x4[EMatrix.Max.ordinal()];
	public final Float4x4[] m_arrayTransposeMatrices = new Float4x4[EMatrix.Max.ordinal()];
	public final Float4x4[] m_arrayInverseTransposeMatrices = new Float4x4[EMatrix.Max.ordinal()];
	
	public int m_nDirty = 0;
	public int m_nTransposeDirty = 0;
	public int m_nInverseDirty = 0;
	public int m_nInverseTransposeDirty = 0;
	
	public MatrixCache()
	{
		for( int i = 0 ; i  < m_arrayMatrices.length ; i++ )
		{
			m_arrayMatrices[i] = Float4x4.Identity();
			m_arrayInverseMatrices[i] = Float4x4.Identity();
			m_arrayTransposeMatrices[i] = Float4x4.Identity();
			m_arrayInverseTransposeMatrices[i] = Float4x4.Identity();
		}
	}
	
	public void SetWorld( Float4x4 a )
	{
		m_arrayMatrices[EMatrix.World.Index].Set( a );
		
		int nDirty = EMatrix.WorldView.Mask | EMatrix.WorldViewProjection.Mask;
		m_nDirty |= nDirty;
		
		nDirty |= EMatrix.World.Mask;
		m_nInverseDirty |= nDirty;
		m_nTransposeDirty |= nDirty;
		m_nInverseTransposeDirty |= nDirty;		
	}
	
	public void SetView( Float4x4 a )
	{
		m_arrayMatrices[EMatrix.View.ordinal()].Set( a );
		int nDirty = EMatrix.WorldView.Mask | EMatrix.ViewProjection.Mask | EMatrix.WorldViewProjection.Mask;
		m_nDirty |= nDirty;

		nDirty |= EMatrix.View.Mask;
		m_nInverseDirty |= nDirty;
		m_nTransposeDirty |= nDirty;
		m_nInverseTransposeDirty |= nDirty;		
	}
	
	public void SetProjection( Float4x4 a )
	{
		m_arrayMatrices[EMatrix.Projection.ordinal()].Set( a );
		int nDirty = EMatrix.ViewProjection.Mask | EMatrix.WorldViewProjection.Mask;
		m_nDirty |= nDirty;

		nDirty |= EMatrix.Projection.Mask;
		m_nInverseDirty |= nDirty;
		m_nTransposeDirty |= nDirty;
		m_nInverseTransposeDirty |= nDirty;		
		
	}
	/*
	public void Update()
	{
		//m_arrayMatrices[EMatrix.WorldView.ordinal()] = Float4x4.Mul( m_arrayMatrices[EMatrix.World.ordinal()], m_arrayMatrices[EMatrix.View.ordinal()]);
		//m_arrayMatrices[EMatrix.ViewProjection.ordinal()] = Float4x4.Mul( m_arrayMatrices[EMatrix.View.ordinal()], m_arrayMatrices[EMatrix.Projection.ordinal()]);
		//m_arrayMatrices[EMatrix.WorldViewProjection.ordinal()] = Float4x4.Mul( m_arrayMatrices[EMatrix.WorldView.ordinal()], m_arrayMatrices[EMatrix.Projection.ordinal()]);
		Float4x4.Mul( m_arrayMatrices[EMatrix.WorldView.ordinal()], m_arrayMatrices[EMatrix.World.ordinal()], m_arrayMatrices[EMatrix.View.ordinal()]);
		Float4x4.Mul( m_arrayMatrices[EMatrix.ViewProjection.ordinal()], m_arrayMatrices[EMatrix.View.ordinal()], m_arrayMatrices[EMatrix.Projection.ordinal()]);
		Float4x4.Mul( m_arrayMatrices[EMatrix.WorldViewProjection.ordinal()], m_arrayMatrices[EMatrix.WorldView.ordinal()], m_arrayMatrices[EMatrix.Projection.ordinal()]);
	}
	
	public void Invert()
	{
		//m_arrayMatrices[EMatrix.WorldInverse.ordinal()] = Float4x4.Invert( m_arrayMatrices[EMatrix.World.ordinal()] );
		//m_arrayMatrices[EMatrix.ViewInverse.ordinal()] = Float4x4.Invert( m_arrayMatrices[EMatrix.View.ordinal()] );
		//m_arrayMatrices[EMatrix.ProjectionInverse.ordinal()] = Float4x4.Invert( m_arrayMatrices[EMatrix.Projection.ordinal()] );
		//m_arrayMatrices[EMatrix.WorldViewInverse.ordinal()] = Float4x4.Invert( m_arrayMatrices[EMatrix.WorldView.ordinal()] );
		//m_arrayMatrices[EMatrix.ViewProjectionInverse.ordinal()] = Float4x4.Invert( m_arrayMatrices[EMatrix.ViewProjection.ordinal()] );
		//m_arrayMatrices[EMatrix.WorldViewProjectionInverse.ordinal()] = Float4x4.Invert( m_arrayMatrices[EMatrix.WorldViewProjection.ordinal()] );
	
		Float4x4.Invert( m_arrayMatrices[EMatrix.WorldInverse.ordinal()], m_arrayMatrices[EMatrix.World.ordinal()] );
		Float4x4.Invert( m_arrayMatrices[EMatrix.ViewInverse.ordinal()], m_arrayMatrices[EMatrix.View.ordinal()] );
		Float4x4.Invert( m_arrayMatrices[EMatrix.ProjectionInverse.ordinal()], m_arrayMatrices[EMatrix.Projection.ordinal()] );
		Float4x4.Invert( m_arrayMatrices[EMatrix.WorldViewInverse.ordinal()], m_arrayMatrices[EMatrix.WorldView.ordinal()] );
		Float4x4.Invert( m_arrayMatrices[EMatrix.ViewProjectionInverse.ordinal()],  m_arrayMatrices[EMatrix.ViewProjection.ordinal()] );
		Float4x4.Invert( m_arrayMatrices[EMatrix.WorldViewProjectionInverse.ordinal()], m_arrayMatrices[EMatrix.WorldViewProjection.ordinal()] );
	}
	
	public void Transpose()
	{
		//m_arrayMatrices[EMatrix.WorldTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.World.ordinal()] );
		//m_arrayMatrices[EMatrix.ViewTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.View.ordinal()] );
		//m_arrayMatrices[EMatrix.ProjectionTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.Projection.ordinal()] );
		//m_arrayMatrices[EMatrix.WorldViewTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldView.ordinal()] );
		//m_arrayMatrices[EMatrix.ViewProjectionTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.ViewProjection.ordinal()] );
		//m_arrayMatrices[EMatrix.WorldViewProjectionTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldViewProjection.ordinal()] );
		
		//m_arrayMatrices[EMatrix.WorldInverseTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldInverse.ordinal()] );
		//m_arrayMatrices[EMatrix.ViewInverseTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.ViewInverse.ordinal()] );
		//m_arrayMatrices[EMatrix.ProjectionInverseTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.ProjectionInverse.ordinal()] );
		//m_arrayMatrices[EMatrix.WorldViewInverseTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldViewInverse.ordinal()] );
		//m_arrayMatrices[EMatrix.ViewProjectionInverseTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.ViewProjectionInverse.ordinal()] );
		//m_arrayMatrices[EMatrix.WorldViewProjectionInverseTranspose.ordinal()] = Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldViewProjectionInverse.ordinal()] );

		Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldTranspose.ordinal()], m_arrayMatrices[EMatrix.World.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.ViewTranspose.ordinal()], m_arrayMatrices[EMatrix.View.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.ProjectionTranspose.ordinal()], m_arrayMatrices[EMatrix.Projection.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldViewTranspose.ordinal()], m_arrayMatrices[EMatrix.WorldView.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.ViewProjectionTranspose.ordinal()], m_arrayMatrices[EMatrix.ViewProjection.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldViewProjectionTranspose.ordinal()], m_arrayMatrices[EMatrix.WorldViewProjection.ordinal()] );

		Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldInverseTranspose.ordinal()], m_arrayMatrices[EMatrix.WorldInverse.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.ViewInverseTranspose.ordinal()], m_arrayMatrices[EMatrix.ViewInverse.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.ProjectionInverseTranspose.ordinal()], m_arrayMatrices[EMatrix.ProjectionInverse.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldViewInverseTranspose.ordinal()], m_arrayMatrices[EMatrix.WorldViewInverse.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.ViewProjectionInverseTranspose.ordinal()], m_arrayMatrices[EMatrix.ViewProjectionInverse.ordinal()] );
		Float4x4.Transpose( m_arrayMatrices[EMatrix.WorldViewProjectionInverseTranspose.ordinal()], m_arrayMatrices[EMatrix.WorldViewProjectionInverse.ordinal()] );
		
	}
	*/
	
	public Float4x4 GetWorld() { return m_arrayMatrices[EMatrix.World.Index]; }
	public Float4x4 GetView() { return m_arrayMatrices[EMatrix.View.Index]; }
	public Float4x4 GetProjection() { return m_arrayMatrices[EMatrix.Projection.Index]; }
	
	public Float4x4 GetWorldView()
	{
		if( (m_nDirty & EMatrix.WorldView.Mask) != 0 )
		{
			m_nDirty &= EMatrix.WorldView.ResetMask;
			return Float4x4.Mul( 
				m_arrayMatrices[EMatrix.WorldView.Index],
				m_arrayMatrices[EMatrix.World.Index],
				m_arrayMatrices[EMatrix.View.Index]);
		}
		else
		{
			return m_arrayMatrices[EMatrix.WorldView.Index];
		}
	}
	
	public Float4x4 GetViewProjection()
	{
		if( (m_nDirty & EMatrix.ViewProjection.Mask ) != 0 )
		{
			m_nDirty &= EMatrix.ViewProjection.ResetMask;
			return Float4x4.Mul( 
				m_arrayMatrices[EMatrix.ViewProjection.Index],
				m_arrayMatrices[EMatrix.View.Index],
				m_arrayMatrices[EMatrix.Projection.Index]);
		}
		else
		{
			return m_arrayMatrices[EMatrix.ViewProjection.Index];
		}
	}
	
	public Float4x4 GetWorldViewProjection()
	{
		if( (m_nDirty & EMatrix.WorldViewProjection.Mask ) != 0 )
		{
			m_nDirty &= EMatrix.WorldViewProjection.ResetMask;
			return Float4x4.Mul( 
				m_arrayMatrices[EMatrix.WorldViewProjection.Index], 
				GetWorldView(), 
				m_arrayMatrices[EMatrix.Projection.Index]);
		}
		else
		{
			return m_arrayMatrices[EMatrix.WorldViewProjection.Index];
		}
	}

	
	public Float4x4 GetWorldInverse()
	{
		EMatrix eMatrix = EMatrix.World;
		if( (m_nInverseDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseDirty &= eMatrix.ResetMask;
			return Float4x4.Invert( 
				m_arrayInverseMatrices[eMatrix.Index], 
				m_arrayMatrices[eMatrix.Index] );
		}
		else
		{
			return m_arrayInverseMatrices[eMatrix.Index];
		}
	}
	
	public Float4x4 GetViewInverse()
	{
		EMatrix eMatrix = EMatrix.View;
		if( (m_nInverseDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseDirty &= eMatrix.ResetMask;
			return Float4x4.Invert( 
				m_arrayInverseMatrices[eMatrix.Index], 
				m_arrayMatrices[eMatrix.Index] );
		}
		else
		{
			return m_arrayInverseMatrices[eMatrix.Index];
		}
	}
	public Float4x4 GetProjectionInverse()
	{
		EMatrix eMatrix = EMatrix.Projection;
		if( (m_nInverseDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseDirty &= eMatrix.ResetMask;
			return Float4x4.Invert( 
				m_arrayInverseMatrices[eMatrix.Index], 
				m_arrayMatrices[eMatrix.Index] );
		}
		else
		{
			return m_arrayInverseMatrices[eMatrix.Index];
		}
	}

	public Float4x4 GetWorldViewInverse()
	{
		EMatrix eMatrix = EMatrix.WorldView;
		if( (m_nInverseDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseDirty &= eMatrix.ResetMask;
			return Float4x4.Invert( 
				m_arrayInverseMatrices[eMatrix.Index], 
				GetWorldView() );
		}
		else
		{
			return m_arrayInverseMatrices[eMatrix.Index];
		}
	}
	
	public Float4x4 GetViewProjectionInverse()
	{
		EMatrix eMatrix = EMatrix.ViewProjection;
		if( (m_nInverseDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseDirty &= eMatrix.ResetMask;
			return Float4x4.Invert( 
				m_arrayInverseMatrices[eMatrix.Index], 
				GetViewProjection() );
		}
		else
		{
			return m_arrayInverseMatrices[eMatrix.Index];
		}
	}
	public Float4x4 GetWorldViewProjectionInverse() 
	{
		EMatrix eMatrix = EMatrix.WorldViewProjection;
		if( (m_nInverseDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseDirty &= eMatrix.ResetMask;
			return Float4x4.Invert( 
				m_arrayInverseMatrices[eMatrix.Index], 
				GetWorldViewProjection() );
		}
		else
		{
			return m_arrayInverseMatrices[eMatrix.Index];
		}
	}
	
	public Float4x4 GetWorldTranspose()
	{
		EMatrix eMatrix = EMatrix.World;
		if( (m_nTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayTransposeMatrices[eMatrix.Index], 
				m_arrayMatrices[eMatrix.Index] );
		}
		else
		{
			return m_arrayTransposeMatrices[eMatrix.Index];
		}
	}
	public Float4x4 GetViewTranspose()
	{
		EMatrix eMatrix = EMatrix.View;
		if( (m_nTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayTransposeMatrices[eMatrix.Index], 
				m_arrayMatrices[eMatrix.Index] );
		}
		else
		{
			return m_arrayTransposeMatrices[eMatrix.Index];
		}
	}
	
	public Float4x4 GetProjectionTranspose()
	{
		EMatrix eMatrix = EMatrix.Projection;
		if( (m_nTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayTransposeMatrices[eMatrix.Index], 
				m_arrayMatrices[eMatrix.Index] );
		}
		else
		{
			return m_arrayTransposeMatrices[eMatrix.Index];
		}
	}
	
	public Float4x4 GetWorldViewTranspose()
	{
		EMatrix eMatrix = EMatrix.WorldView;
		if( (m_nTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayTransposeMatrices[eMatrix.Index], 
				GetWorldView() );
		}
		else
		{
			return m_arrayTransposeMatrices[eMatrix.Index];
		}
	}

	public Float4x4 GetViewProjectionTranspose() 
	{
		EMatrix eMatrix = EMatrix.ViewProjection;
		if( (m_nTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayTransposeMatrices[eMatrix.Index], 
				GetViewProjection() );
		}
		else
		{
			return m_arrayTransposeMatrices[eMatrix.Index];
		}
	}

	public Float4x4 GetWorldViewProjectionTranspose()
	{
		EMatrix eMatrix = EMatrix.WorldViewProjection;
		if( (m_nTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayTransposeMatrices[eMatrix.Index], 
				GetWorldViewProjection() );
		}
		else
		{
			return m_arrayTransposeMatrices[eMatrix.Index];
		}
	}
	public Float4x4 GetWorldInverseTranspose()
	{
		EMatrix eMatrix = EMatrix.World;
		if( (m_nInverseTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayInverseTransposeMatrices[eMatrix.Index], 
				GetWorldInverse() );
		}
		else
		{
			return m_arrayInverseTransposeMatrices[eMatrix.Index];
		}
	}
	
	public Float4x4 GetViewInverseTranspose()
	{
		EMatrix eMatrix = EMatrix.World;
		if( (m_nInverseTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayInverseTransposeMatrices[eMatrix.Index], 
				GetViewInverse() );
		}
		else
		{
			return m_arrayInverseTransposeMatrices[eMatrix.Index];
		}
	}

	public Float4x4 GetProjectionInverseTranspose()
	{
		EMatrix eMatrix = EMatrix.Projection;
		if( (m_nInverseTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayInverseTransposeMatrices[eMatrix.Index], 
				GetProjectionInverse() );
		}
		else
		{
			return m_arrayInverseTransposeMatrices[eMatrix.Index];
		}
	}
	
	public Float4x4 GetWorldViewInverseTranspose()
	{
		EMatrix eMatrix = EMatrix.WorldView;
		if( (m_nInverseTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose( 
				m_arrayInverseTransposeMatrices[eMatrix.Index], 
				GetWorldViewInverse() );
		}
		else
		{
			return m_arrayInverseTransposeMatrices[eMatrix.Index];
		}
	}
	
	public Float4x4 GetViewProjectionInverseTranspose() 
	{
		EMatrix eMatrix = EMatrix.ViewProjection;
		if( (m_nInverseTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose(
				m_arrayInverseTransposeMatrices[eMatrix.Index],
				GetViewProjectionInverse() );
		}
		else
		{
			return m_arrayInverseTransposeMatrices[eMatrix.Index];
		}
	}
	
	public Float4x4 GetWorldViewProjectionInverseTranspose()
	{
		EMatrix eMatrix = EMatrix.WorldViewProjection;
		if( (m_nInverseTransposeDirty & eMatrix.Mask) != 0 )
		{
			m_nInverseTransposeDirty &= eMatrix.ResetMask;
			return Float4x4.Transpose(
				m_arrayInverseTransposeMatrices[eMatrix.Index], 
				GetWorldViewProjectionInverse() );
		}
		else
		{
			return m_arrayInverseTransposeMatrices[eMatrix.Index];
		}
	}
}
