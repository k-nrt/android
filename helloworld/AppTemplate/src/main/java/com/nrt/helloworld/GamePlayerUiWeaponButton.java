package com.nrt.helloworld;

import com.nrt.render.BlendFunc;
import com.nrt.ui.*;
import com.nrt.math.*;
import com.nrt.basic.*;
import com.nrt.font.*;
import com.nrt.render.*;

import com.nrt.framework.*;

public final class GamePlayerUiWeaponButton extends UiRectButton
{
	//public String Caption = null; 
	public boolean IsActive = true;
	//public float Reload = 0.0f;
	public GamePlayerWeapon WeaponDesc = null;

	public GamePlayerUiWeaponButton( Rect rect, GamePlayerWeapon weaponDesc )
	{
		super( rect );
		WeaponDesc = weaponDesc;			
	}

	private final Rect m_rect = new Rect();

	@Override
	public void OnRender(final BasicRender br, final BitmapFont bf)
	{
		m_rect.Set( Enter );

		final GfxCommandContext gfxc = br.GetCommandContext();
		//GLES20.glEnable( GLES20.GL_BLEND );
		//GLES20.glBlendFunc( GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA );
		BlendState blendSrcAlphaOneMinusSrcAlpha = new BlendState(true, BlendFunc.SrcAlpha, BlendFunc.OneMinusSrcAlpha);
		gfxc.SetBlendState(blendSrcAlphaOneMinusSrcAlpha);

		br.SetColor( 0.0f, 0.0f, 0.2f, 0.5f );
		br.FillRectangle( m_rect );


		final Float4 f4Red = Float4.Local().Set( 0.95f, 0.3f, 0.15f, 1.0f );
		final Float4 f4Green   = Float4.Local().Set( 0.3f, 0.95f, 0.6f, 1.0f );

		if( !IsActive )
		{
			Float4.Mul( f4Green, f4Green, 0.5f );
			Float4.Mul( f4Red, f4Red, 0.5f );
			f4Green.W = f4Red.W = 1.0f;
		}

		int nGreen = RgbaColor.FromRgba( f4Green );
		int nRed = RgbaColor.FromRgba( f4Red );

		//final BitmapFont bf = SubSystem.BitmapFont;

		//hf.SetSize(8.0f);
		bf.SetColor( nGreen );

		bf.Begin();
		bf.Draw(m_rect.X + 3.0f, FMath.Floor(m_rect.Y + (m_rect.Height * 0.25f) - 4.0f), 0.0f, WeaponDesc.Name);
		bf.End();


		float fReload = WeaponDesc.GetReloadRate();
		if (fReload >= 1.0f)
		{
			br.SetColor( nGreen );//0.3f, 0.95f, 0.6f, 1.0f);
		}
		else
		{
			br.SetColor( nRed );//0.95f, 0.3f, 0.15f, 1.0f);			
		}

		float w = m_rect.Width;
		float h = m_rect.Height;

		m_rect.Y += 2.0f + h * 0.5f;
		m_rect.Height *= 0.5f;
		m_rect.Height -= 4.0f;
		m_rect.X += 2.0f;
		m_rect.Width = (w - 4.0f) * fReload;

		br.FillRectangle(m_rect);

		if (m_nbPrevPointers < m_nbPointers)
		{
			br.SetColor(1, 0, 0, 1);
		}
		else if (m_nbPointers > 0)
		{
			br.SetColor(0, 0, 1, 1);			
		}
		else
		{
			br.SetColor(0, 0.5f, 1, 1);
		}
		br.Rectangle(Enter);
	}
}
