package com.nrt.ui;

import java.util.List;
import java.util.ArrayList;

import com.nrt.font.BitmapFont;
import com.nrt.input.DevicePointer;
import com.nrt.input.FramePointer;

import com.nrt.render.BasicRender;
import com.nrt.render.GfxCommandContext;

public class UiForm
{
	public List<UiItem> Items = null;
	public UiItem[] OwnerItems = null;

	public UiForm()
	{
		Items = new ArrayList<UiItem>();
		OwnerItems = new UiItem[DevicePointer.Pointers.length];
	}

	public <Type> Type Add(Type item)
	{
		Items.add((UiItem) item);
		return item;
	}

	public void Update(FramePointer framePointer, float fElapsedTime)
	{
		for (int i = 0 ; i < OwnerItems.length ; i++)
		{
			UiItem item = OwnerItems[i];
			FramePointer.Pointer pointer = framePointer.Pointers[i];

			if (item != null)
			{
				if (pointer.Down)
				{
					if (item.IsLeave(
							pointer.Position.X,
							pointer.Position.Y) == false)
					{
						item.OnLeave(i, pointer); 
						OwnerItems[i] = null;
					}
					else
					{
						item.OnMove(i, pointer);
					}
				}
				else
				{
					item.OnUp(i, pointer);
					OwnerItems[i] = null;
				}
			}
		}

		for (int i = 0 ; i < Items.size() ; i++)
		{
			UiItem item = Items.get(Items.size() - 1 - i);

			for (int j = 0 ; j < framePointer.Pointers.length ; j++)
			{
				FramePointer.Pointer pointer = framePointer.Pointers[j];
				if (pointer.Down == false)
				{
					continue;
				}

				if (OwnerItems[j] != null)
				{
					continue;
				}

				if (item.IsEnter(pointer.Position.X, pointer.Position.Y))
				{
					boolean isOwned = false;
					if (pointer.Push)
					{
						isOwned = item.OnDown(j, pointer);
					}
					else
					{
						isOwned = item.OnEnter(j, pointer);
					}

					if( isOwned )
					{
						OwnerItems[j] = item;
					}
				}
			}

			item.OnUpdate(fElapsedTime);
		}
	}

	public void Render(final BasicRender br, final BitmapFont bf)
	{
		for (int i = 0 ; i < Items.size() ; i++)
		{
			Items.get(i).OnRender(br,bf);
		}
	}
}
