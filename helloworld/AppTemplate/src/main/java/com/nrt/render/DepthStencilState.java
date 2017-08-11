package com.nrt.render;

public class DepthStencilState
{
	public boolean EnableDepthTest = true;
	public EDepthFunc DepthFunc = EDepthFunc.Less;
	public boolean DepthMask = true;
	
	public boolean EnableStencilTest = false;
	public StencilFunc FrontStencilFunc = StencilFunc.Always;
	public int FrontStencilRef = 0;
	public int FrontStencilMask = 0xffffffff;
	public int FrontStencilWriteMask = 0xffffffff;	
	public StencilOp FrontStencilFail = StencilOp.Keep;
	public StencilOp FrontDepthFail = StencilOp.Keep;
	public StencilOp FrontDepthPass = StencilOp.Keep;
	
	public StencilFunc BackStencilFunc = StencilFunc.Always;
	public int BackStencilRef = 0;
	public int BackStencilMask = 0xffffffff;
	public int BackStencilWriteMask = 0xffffffff;	
	public StencilOp BackStencilFail = StencilOp.Keep;
	public StencilOp BackDepthFail = StencilOp.Keep;
	public StencilOp BackDepthPass = StencilOp.Keep;
	
	
	public DepthStencilState()
	{}
	
	public DepthStencilState(boolean enableDepthTest, EDepthFunc depthFunc, boolean depthMask )
	{
		EnableDepthTest = enableDepthTest;
		DepthFunc = depthFunc;
		DepthMask = depthMask;
	}
	
	public DepthStencilState
	(
		boolean enableDepthTest, 
		EDepthFunc depthFunc,
		boolean depthMask,
		boolean enableStencilTest,
		StencilFunc frontStencilFunc,
		int frontStencilRef,
		int frontStencilMask,
		int frontStencilWriteMask,
		StencilOp frontStencilFail,
		StencilOp frontDepthFail,
		StencilOp frontDepthPass,
		StencilFunc backStencilFunc,
		int backStencilRef,
		int backStencilMask,
		int backStencilWriteMask,
		StencilOp backStencilFail,
		StencilOp backDepthFail,
		StencilOp backDepthPass
	)
	{
		EnableDepthTest = enableDepthTest;
		DepthFunc = depthFunc;
		DepthMask = depthMask;

		EnableStencilTest = enableStencilTest;
		FrontStencilFunc = frontStencilFunc;
		FrontStencilRef = frontStencilRef;
		FrontStencilMask = frontStencilMask;
		FrontStencilWriteMask = frontStencilWriteMask;
		FrontStencilFail = frontStencilFail;
		FrontDepthFail = frontDepthFail;
		FrontDepthPass = frontDepthPass;

		BackStencilFunc = backStencilFunc;
		BackStencilRef = backStencilRef;
		BackStencilMask = backStencilMask;
		BackStencilWriteMask = backStencilWriteMask;	
		BackStencilFail = backStencilFail;
		BackDepthFail = backDepthFail;
		BackDepthPass = backDepthPass;
	}
}
