package com.nrt.render;

import android.opengl.*;
import com.nrt.math.Float3;
import com.nrt.math.Float4;
//import com.nrt.math.Vec3;
//import com.nrt.math.Vec4;


public class Render
{
	public int ScanOutWidth = 0;
	public int ScanOutHeight = 0;
	
	public BlendState m_blendStateNull = new BlendState();
	public DepthStencilState m_depthStencilStateNull = new DepthStencilState();
	public RasterizerState m_rasterizerStateNull = new RasterizerState();
		
	public void SetFrameBuffer( FrameBuffer frameBuffer )
	{
		if( frameBuffer != null )
		{
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, frameBuffer.Name );
		}
		else
		{
			GLES20.glBindFramebuffer( GLES20.GL_FRAMEBUFFER, 0 );			
		}
	}
	
	public void SetScissor(int x, int y, int width, int height)
	{
		GLES20.glScissor(x,y,width,height);
	}
	
	public void SetViewport(int x, int y, int width, int height)
	{
		GLES20.glViewport(x,y,width,height);
	}

	public void SetClearColor( float r, float g, float b, float a )
	{
		GLES20.glClearColor( r, g, b, a );
	}
	
	public void Clear( EClearBuffer eClear )
	{
		GLES20.glClear( eClear.Value );
	}
	
	
	public void SetVertexBuffer(VertexBuffer vb)
	{
		if( vb != null )
		{
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vb.GetVertexBufferName());
		}
		else
		{
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		}
	}

	public void SetIndexBuffer(IndexBuffer ib)
	{
		if( ib != null )
		{
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ib.GetIndexBufferName());
		}
		else
		{
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		}
	}
	
	public void SetVertexBuffer(int name)
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, name);
	}

	public void SetIndexBuffer(int name)
	{
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, name);
	}

	public void EnableVertexStream(VertexStream stream, int offset)
	{
		for (int i = 0 ; i < stream.VertexAttributes.length ; i++)
		{
			VertexAttribute va = stream.VertexAttributes[i];
			GLES20.glEnableVertexAttribArray(va.Index);
			GLES20.glVertexAttribPointer( 
				va.Index, 
				va.Components, 
				va.Format, 
				va.Normalize, 
				stream.Stride, 
				va.Offset + offset);
		}
	}
	
	public void DisableVertexStream(VertexStream stream)
	{
		for (int i = 0 ; i < stream.VertexAttributes.length ; i++)
		{
			VertexAttribute va = stream.VertexAttributes[i];
			GLES20.glDisableVertexAttribArray(va.Index);
		}
	}

	public void DisableVertexStream()
	{
		for (int i = 0 ; i < GLES20.GL_MAX_VERTEX_ATTRIBS ; i++)
		{
			GLES20.glDisableVertexAttribArray(i);
		}	
	}

	public void SetProgram(Program program)
	{
		GLES20.glUseProgram(program.Name);
	}

	public void SetTexture(int texUnit, Texture texture)
	{
		GLES20.glActiveTexture(texUnit);
		if( texture != null )
		{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.GetTextureName());
		}
		else
		{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		}
	}

	public void SetTexture(Sampler s, Texture texture)
	{
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + s.TextureUnit);
		if( texture != null )
		{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.GetTextureName());
		}
		else
		{
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		}
		GLES20.glUniform1i(s.Index, s.TextureUnit);
	}

	public void SetSamplerState( Sampler s, SamplerState samplerState )
	{
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + s.TextureUnit);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, samplerState.MagFilter.Value);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, samplerState.MinFilter.Value);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, samplerState.WrapS.Value);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, samplerState.WrapT.Value);
		
	}
	
	public void SetMatrix(Uniform u, float[] matrix)
	{
		GLES20.glUniformMatrix4fv(u.Index, 1, false, matrix, 0);
	}

	public void SetMatrix(Uniform u, float[] matrix, int offset )
	{
		GLES20.glUniformMatrix4fv(u.Index, 1, false, matrix, 0);
	}
	
	public void SetMatrices(Uniform u, int nbMatrices, float[] matrices, int start)
	{
		GLES20.glUniformMatrix4fv(u.Index, nbMatrices, false, matrices, start * 16);
	}
	
	public void SetMatrixArray(Uniform u, int nbMatrices, float[] matrices, int offset)
	{
		GLES20.glUniformMatrix4fv(u.Index, nbMatrices, false, matrices, offset );
	}
	
	public void SetFloat4Array( Uniform u, float[] f4Array )
	{
		GLES20.glUniform4fv( u.Index, f4Array.length/4, f4Array, 0 );
	}

	public void SetFloat4Array( Uniform u, int nCount, float[] f4Array, int offset )
	{
		GLES20.glUniform4fv( u.Index, nCount, f4Array, offset );
	}

	public void SetFloat4(Uniform u, float x, float y, float z, float w)
	{
		GLES20.glUniform4f(u.Index, x, y, z, w);
	}

	public void SetFloat4(Uniform u, Float4 v )
	{
		GLES20.glUniform4f(u.Index, v.X, v.Y, v.Z, v.W );
	}
	/*
	public void SetFloat4(Uniform u, Vec4 v )
	{
		GLES20.glUniform4f(u.Index, v.X, v.Y, v.Z, v.W );
	}
	*/

	public void SetFloat3(Uniform u, float x, float y, float z)
	{
		GLES20.glUniform3f(u.Index, x, y, z);
	}

	public void SetFloat3(Uniform u, float[] v, int o )
	{
		GLES20.glUniform3f(u.Index, v[o+0], v[o+1], v[o+2]);
	}
	
	
	public void SetFloat3( Uniform u, Float3 v )
	{
		GLES20.glUniform3f( u.Index, v.X, v.Y, v.Z );
	}
	/*
	public void SetFloat3( Uniform u, Vec3 v )
	{
		GLES20.glUniform3f( u.Index, v.X, v.Y, v.Z );
	}
	*/
	public void SetFloat3Array( Uniform u, float[] f3Array )
	{
		GLES20.glUniform3fv( u.Index, f3Array.length/3, f3Array, 0 );
	}

	public void SetFloat3Array( Uniform u, int nCount, float[] f3Array, int offset )
	{
		GLES20.glUniform3fv( u.Index, nCount, f3Array, offset );
	}
	
	public void SetFloat2( Uniform u, float x, float y )
	{
		GLES20.glUniform2f( u.Index, x, y );
	}

	public void SetFloat2Array( Uniform uniform, int nCount, float[] f2Array, int offset )
	{
		GLES20.glUniform2fv( uniform.Index, nCount, f2Array, offset );
	}

	public void SetFloat( Uniform u, float x )
	{
		GLES20.glUniform1f( u.Index, x );
	}
	
	public void SetFloatArray( Uniform u, int nCount, float[] values, int offset )
	{
		GLES20.glUniform1fv( u.Index, nCount, values, offset );
	}
	
	public void SetBlendState( BlendState blendState )
	{
		if( blendState == null )
		{
			blendState = m_blendStateNull;
		}
		
		if( blendState.EnableBlend )
		{
			GLES20.glEnable( GLES20.GL_BLEND );
			
			GLES20.glBlendFuncSeparate
			(
				blendState.SrcColor.Value, blendState.DstColor.Value,
				blendState.SrcAlpha.Value, blendState.DstAlpha.Value
			);
			
			GLES20.glBlendEquationSeparate
			( 
				blendState.ColorEquation.Value, 
				blendState.AlphaEquation.Value
			);
			
			GLES20.glBlendColor
			(
				blendState.BlendColorRed,
				blendState.BlendColorGreen,
				blendState.BlendColorBlue,
				blendState.BlendColorAlpha
			);
		}
		else
		{
			GLES20.glDisable( GLES20.GL_BLEND );
		}
		
		if( blendState.EnableAlphaToCoverage )
		{
			GLES20.glEnable( GLES20.GL_SAMPLE_ALPHA_TO_COVERAGE );
		}
		else
		{
			GLES20.glDisable( GLES20.GL_SAMPLE_ALPHA_TO_COVERAGE );
		}
		
		GLES20.glColorMask
		(
			blendState.ColorMaskRed,
			blendState.ColorMaskGreen,
			blendState.ColorMaskBlue,
			blendState.ColorMaskAlpha
		);
	}

	public final void SetDepthStencilState( DepthStencilState dss )
	{
		if( dss == null )
		{
			dss = m_depthStencilStateNull;
		}
		
		if( dss.EnableDepthTest )
		{
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		}
		else
		{
			GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		}
		
		GLES20.glDepthFunc( dss.DepthFunc.Value );
		
		GLES20.glDepthMask( dss.DepthMask );
		
		if(dss.EnableStencilTest)
		{
			GLES20.glEnable( GLES20.GL_STENCIL_TEST);
		}
		else
		{
			GLES20.glDisable( GLES20.GL_STENCIL_TEST );
		}
		
		GLES20.glStencilFuncSeparate( GLES20.GL_FRONT, dss.FrontStencilFunc.Value, dss.FrontStencilRef, dss.FrontStencilMask );
		GLES20.glStencilMaskSeparate( GLES20.GL_FRONT, dss.FrontStencilWriteMask );
		GLES20.glStencilOpSeparate( GLES20.GL_FRONT, dss.FrontStencilFail.Value, dss.FrontDepthFail.Value, dss.FrontDepthPass.Value );

		GLES20.glStencilFuncSeparate( GLES20.GL_BACK, dss.BackStencilFunc.Value, dss.BackStencilRef, dss.BackStencilMask );
		GLES20.glStencilMaskSeparate( GLES20.GL_BACK, dss.BackStencilWriteMask );
		GLES20.glStencilOpSeparate( GLES20.GL_BACK, dss.BackStencilFail.Value, dss.BackDepthFail.Value, dss.BackDepthPass.Value );
	}
	
	public final void SetRasterizerState( RasterizerState rs)
	{
		if( rs == null )
		{
			rs = m_rasterizerStateNull;
		}
		
		if(rs.EnableCullFace)
		{
			GLES20.glEnable(GLES20.GL_CULL_FACE);
			GLES20.glCullFace(rs.CullFace.Value);
			GLES20.glFrontFace(rs.FrontFace.Value);
		}
		else
		{
			GLES20.glDisable(GLES20.GL_CULL_FACE);
		}
		
		if(rs.EnableDither)
		{
			GLES20.glEnable(GLES20.GL_DITHER);
		}

		else
		{
			GLES20.glDisable(GLES20.GL_DITHER);
		}
		
		if(rs.EnableSampleCoverage)
		{
			GLES20.glEnable(GLES20.GL_SAMPLE_COVERAGE);
		}

		else
		{
			GLES20.glDisable(GLES20.GL_SAMPLE_COVERAGE);
		}
		
		if(rs.EnableScissorTest)
		{
			GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
		}

		else
		{
			GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
		}
	}
	
	public void DrawElements(int primitive, int nbVertices, EIndexFormat eFormat, int nByteOffset)
	{
		GLES20.glDrawElements( 
			primitive, 
			nbVertices, 
			eFormat.Value, 
			nByteOffset
		);
	}
	
	public void DrawElements(EPrimitive primitive, int nbVertices, EIndexFormat eFormat, int nByteOffset)
	{
		GLES20.glDrawElements( 
			primitive.Value, 
			nbVertices, 
			eFormat.Value, 
			nByteOffset
		);
	}
	
	public void DrawArrays(int primitive, int iFirst, int nbVertices)
	{
		GLES20.glDrawArrays(primitive, iFirst, nbVertices);
	}
}
