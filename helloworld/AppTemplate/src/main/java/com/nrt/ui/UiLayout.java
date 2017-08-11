package com.nrt.ui;

import java.util.List;
import java.util.ArrayList;

import com.nrt.basic.Rect;
import com.nrt.render.*;

public class UiLayout
{
	public static class Item
	{
		public enum EOrigin
		{
			Scaling,
			TopLeft,
			TopRight,
			BottomLeft,
			BottomRight,
		};
		
		public enum EFlow
		{
			HorizontalFirst,
			VerticalFirst,
		};

		public enum EFill
		{
			None,
			Horizontal,
			Vertical,
		};
		
		public enum ESize
		{
			Pixel,
			Percent,
		};
		
		public UiItem m_item = null;
		public UiLayout m_layout = null;
		
		public final Rect m_rectEnterBase = new Rect();
		public final Rect m_rectLeaveBase = new Rect();
				
		public EOrigin m_eOrigin = EOrigin.TopLeft;
		public EFlow m_eFlow = EFlow.HorizontalFirst;
		
		public EFill m_eFill = EFill.None;
		
		public ESize m_eSize = ESize.Pixel;
		
		public Item( UiItem item )
		{
			this( item, EOrigin.Scaling, EFlow.HorizontalFirst, EFill.None, ESize.Pixel );
		}
		
		public Item( UiItem item, EOrigin eOrigin, EFlow eFlow, EFill eFill, ESize eSize )
		{
			m_item = item;
			m_rectEnterBase.Set( item.GetEnterRect() );
			m_rectLeaveBase.Set( item.GetLeaveRect() );
			
			m_eOrigin = eOrigin;
			m_eFlow = eFlow;
			m_eFill = eFill;
			m_eSize = eSize;
		}
		
		public Item( UiLayout layout, EOrigin eOrigin, EFlow eFlow, EFill eFill, ESize eSize )
		{
			m_layout = layout;
			m_rectEnterBase.Set( layout.BaseRect );
			m_rectLeaveBase.Set( layout.BaseRect );
			m_eOrigin = eOrigin;
			m_eFlow = eFlow;
			m_eFill = eFill;
			m_eSize = eSize;
		}
		
		public final void Resize( Rect rectEnter, Rect rectLeave )
		{
			if( m_item != null )
			{
				m_item.Resize( rectEnter, rectLeave );
			}
			else if( m_layout != null )
			{
				
			}
		}
		
		public final Rect GetEnterBase()
		{
			return m_rectEnterBase;
		}
		
		public final Rect GetLeaveBase()
		{
			return m_rectLeaveBase;
		}
		
		public final Rect GetEnter()
		{
			if( m_item != null )
			{
				return m_item.GetEnterRect();
			}
			else if( m_layout != null )
			{
				return m_layout.Rect;
			}
			else
			{
				return null;
			}
		}
		
		public final Rect GetLeave()
		{
			if( m_item != null )
			{
				return m_item.GetLeaveRect();
			}
			else if( m_layout != null )
			{
				return m_layout.Rect;
			}
			else
			{
				return null;
			}
		}
	}
	
	public List<Item> ListItems = new ArrayList<Item>();
	
	public final Rect BaseRect = new Rect();
	public final Rect Rect = new Rect();
	
	public float Gap = 10;
	
	
	
	public UiLayout( Rect rectBase )
	{
		BaseRect.Set( rectBase );
		Rect.Set( rectBase );
	}
	
	public <uiitem extends UiItem> uiitem Add( uiitem item )
	{
		ListItems.add( new Item( item ) );		
		return item;
	}
	
	public <uiitem extends UiItem> uiitem  Add( uiitem item, Item.EOrigin eOrigin, Item.EFlow eFlow, Item.EFill eFill, Item.ESize eSize )
	{
		ListItems.add( new Item( item, eOrigin, eFlow, eFill, eSize));
		return item;
	}
	
	public void Resize( Rect rect )
	{
		float fBaseAspect = BaseRect.Width/BaseRect.Height;
		float fNewAspect = rect.Width/rect.Height;

		float fScale = 1.0f;		
		float fOffsetX = 0.0f;
		float fOffsetY = 0.0f;
		/*
		if( BaseRect.Width < BaseRect.Height )
		{
			//. vertical.
			float
			
		}
		*/
		if( fBaseAspect > fNewAspect )
		{
			fScale = rect.Width/BaseRect.Width;
			
			float w = rect.Width;
			float h = rect.Width/fBaseAspect;
			
			fOffsetX = (rect.Width - w)*0.5f;
			fOffsetY = (rect.Height - h)*0.5f;
			
			Rect.Set( 
				rect.X + fOffsetX,
				rect.Y + fOffsetY,
				w, h );
		}
		else
		{
			fScale = rect.Height/BaseRect.Height;
			//fScale = BaseRect.Height/rect.Height;
			
			float w = rect.Height*fBaseAspect;
			float h = rect.Height;
			
			fOffsetX = (rect.Width - w)*0.5f;
			fOffsetY = (rect.Height - h)*0.5f;
			
			Rect.Set( 
				rect.X + fOffsetX,
				rect.Y + fOffsetY,
				w, h );
		}
		
		for( int i = 0 ; i < ListItems.size() ; i++ )
		{
			Item item = ListItems.get(i);
			if( item.m_item != null )
			{
				switch( item.m_eOrigin )
				{
				case Scaling:
					item.Resize(	
					new Rect(
						(item.GetEnterBase().X - BaseRect.X)*fScale + Rect.X,
						(item.GetEnterBase().Y - BaseRect.Y)*fScale + Rect.Y,
						item.GetEnterBase().Width*fScale,
						item.GetEnterBase().Height*fScale ),
					new Rect(
						(item.GetLeaveBase().X - BaseRect.X)*fScale + Rect.X,
						(item.GetLeaveBase().Y - BaseRect.Y)*fScale + Rect.Y,
						item.GetLeaveBase().Width*fScale,
						item.GetLeaveBase().Height*fScale ) );
					break;
						
				case TopLeft:
					break;

				case TopRight:
					break;
					
				case BottomLeft:
					{
						float w = item.GetEnterBase().Width;//*fScale;
						float h = item.GetEnterBase().Height;//*fScale;
						
						Rect rectNew = new Rect( 
							rect.X + Gap, 
							rect.Y + rect.Height - Gap - h,
							w, h );
							
						MoveRect( rectNew, i, item.m_eOrigin, item.m_eFlow, rect );
						
						item.Resize( rectNew, rectNew );
					}
					break;
					
				case BottomRight:
					{
						float w = item.GetEnterBase().Width;//*fScale;
						float h = item.GetEnterBase().Height;//*fScale;

						Rect rectNew = new Rect( 
							rect.X + rect.Width - Gap - w, 
							rect.Y + rect.Height - Gap - h,	
							w, h );

						MoveRect( rectNew, i, item.m_eOrigin, item.m_eFlow, rect );
						item.Resize( rectNew, rectNew );
					}
					break;
				}
				
			}
			else if( item.m_layout != null )
			{
				
			}
		}
	}
	
	private Item IsHit( int end, Rect rect )
	{
		for( int i = 0 ; i < end ; i++ )
		{
			Item item = ListItems.get(i);
			if( item.GetEnter().IsInterect( rect ) )
			{
				return item;
			}
		}
		
		return null;
	}
	
	private final void MoveRect( Rect rectResult, int iItemEnd,  Item.EOrigin eOrigin, Item.EFlow eFlow, Rect rectRegion )
	{
		Rect rectFirst = new Rect( rectResult );
		
		switch( eOrigin )
		{
			case TopLeft:				
				switch( eFlow )
				{
					case HorizontalFirst:
						Move( rectResult, iItemEnd, 1, 0 );
						if( IsOutOfRegion( rectRegion, rectResult ) )
						{
							rectResult.Y = rectFirst.GetBottom() + Gap;
							Move( rectResult, iItemEnd, 0, 1 );
						}
						break;
						
					case VerticalFirst:
						Move( rectResult, iItemEnd, 0, 1 );
						if( IsOutOfRegion( rectRegion, rectResult ) )
						{
							rectResult.X = rectFirst.GetRight() + Gap;
							Move( rectResult, iItemEnd, 1, 0 );
						}
						break;
				}		
				break;
				
			case TopRight:
				switch( eFlow )
				{
					case HorizontalFirst:
						Move( rectResult, iItemEnd, -1, 0 );
						if( IsOutOfRegion( rectRegion, rectResult ) )
						{
							rectResult.Y = rectFirst.GetBottom() + Gap;
							Move( rectResult, iItemEnd, 0, 1 );
						}
						break;

					case VerticalFirst:
						Move( rectResult, iItemEnd, 0, 1 );
						if( IsOutOfRegion( rectRegion, rectResult ) )
						{
							rectResult.X = rectFirst.GetLeft() - rectResult.Width - Gap;
							Move( rectResult, iItemEnd, -1, 0 );
						}
						break;
				}		
				break;
				
			case BottomLeft:
				switch( eFlow )
				{
					case HorizontalFirst:
						Move( rectResult, iItemEnd, 1, 0 );
						if( IsOutOfRegion( rectRegion, rectResult ) )
						{
							rectResult.Y = rectFirst.GetTop() - rectResult.Height - Gap;
							Move( rectResult, iItemEnd, 0, -1 );
						}
						break;

					case VerticalFirst:
						Move( rectResult, iItemEnd, 0, -1 );
						if( IsOutOfRegion( rectRegion, rectResult ) )
						{
							rectResult.X = rectFirst.GetRight() + Gap;
							Move( rectResult, iItemEnd, 1, 0 );
						}
						break;
				}		
				break;
				
			case BottomRight:
				switch( eFlow )
				{
					case HorizontalFirst:
						Move( rectResult, iItemEnd, -1, 0 );
						if( IsOutOfRegion( rectRegion, rectResult ) )
						{
							rectResult.Y = rectFirst.GetTop() - rectResult.Height - Gap;
							Move( rectResult, iItemEnd, 0, -1 );
						}
						break;

					case VerticalFirst:
						Move( rectResult, iItemEnd, 0, -1 );
						if( IsOutOfRegion( rectRegion, rectResult ) )
						{
							rectResult.X = rectFirst.GetLeft() - rectResult.Width - Gap;
							Move( rectResult, iItemEnd, -1, 0 );
						}
						break;
				}		
				break;
		}		
	}
	
	private boolean IsOutOfRegion( Rect rectRegion, Rect rect )
	{
		if( rect.X < rectRegion.GetLeft() || (rectRegion.GetRight() - rect.Width ) < rect.X ||
			rect.Y < rectRegion.GetTop() || (rectRegion.GetBottom() - rect.Height ) < rect.Y )
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	private void Move( Rect rectInOut, int iItemEnd, int nHDir, int nVDir )
	{
		for( int i = 0 ; i < iItemEnd ; i++ )
		{
			Item item = ListItems.get(i);
			if( item.GetEnter().IsInterect( rectInOut ) )
			{
				if( nHDir < 0 )
				{
					rectInOut.X = item.GetEnter().GetLeft() - Gap - rectInOut.Width;
				}
				else if( 0 < nHDir )
				{
					rectInOut.X = item.GetEnter().GetRight() + Gap;
				}
				else if( nVDir < 0 )
				{
					rectInOut.Y = item.GetEnter().GetTop() - Gap - rectInOut.Height;
				}
				else if( 0 < nVDir )
				{
					rectInOut.Y = item.GetEnter().GetBottom() + Gap;
				}
				
				i = 0;
			}
		}
	}

	
	public Rect Transform( Rect rect )
	{
		/*
		float fBaseAspect = BaseRect.Width/BaseRect.Height;
		float fNewAspect = Rect.Width/Rect.Height;
		
		float fScale = 1.0f;		
		if( fBaseAspect < fNewAspect )
		{
			fScale = Rect.Width/BaseRect.Width;
		}
		else
		{
			fScale = Rect.Height/BaseRect.Height;
		}
		
		float fOffsetX = 
		
		*/
		return null;
		
	}
	
	public final void Render( final BasicRender br )
	{
		br.Begin( EPrimitive.LineLoop, BasicRender.EShader.Color );
		br.SetColor( 0xffff00ff );
		br.SetVertex( Rect.X, Rect.Y, 0.0f );
		br.SetVertex( Rect.X, Rect.Y + Rect.Height - 1, 0.0f );
		br.SetVertex( Rect.X + Rect.Width - 1, Rect.Y + Rect.Height -1, 0.0f );
		br.SetVertex( Rect.X + Rect.Width - 1, Rect.Y, 0.0f );		
		br.End();
	}
	
}
