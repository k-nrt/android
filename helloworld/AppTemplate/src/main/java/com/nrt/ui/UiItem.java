package com.nrt.ui;

import com.nrt.input.FramePointer;
import com.nrt.render.BasicRender;
import com.nrt.basic.Rect;
import com.nrt.render.GfxCommandContext;
import com.nrt.font.BitmapFont;

public interface UiItem
{
	public boolean IsEnter(float x, float y);
	public boolean IsLeave(float x, float y);
	public boolean OnDown(int id, FramePointer.Pointer pointer);

	public boolean OnEnter(int id, FramePointer.Pointer pointer);
	public void OnMove(int id, FramePointer.Pointer pointer);
	public void OnLeave(int id, FramePointer.Pointer pointer);
	public void OnUp(int id, FramePointer.Pointer pointer);

	public void OnUpdate(float fElapsedTime);
	public void OnRender(final BasicRender br, final BitmapFont bf);
	
	//. for Layout Engine.
	public void Resize( Rect rectEnter, Rect rectLeave );
	public Rect GetEnterRect();
	public Rect GetLeaveRect();
}
