package com.nrt.render;

public class RasterizerState
{
	public boolean EnableCullFace = false;
	public ECullFace  CullFace = ECullFace.Back;
	public EFrontFace FrontFace = EFrontFace.CCW;

	public boolean EnableDither = false;
	
	
	public boolean EnableSampleCoverage = false;
	
	public boolean EnableScissorTest = false;
	
	
	public RasterizerState()
	{}
	
	public RasterizerState( boolean enableCullFace, ECullFace cullFace, EFrontFace frontFace )
	{
		EnableCullFace = enableCullFace;
		CullFace = cullFace;
		FrontFace = frontFace;
	}
	
	public RasterizerState
	(
		boolean enablrCullFace, 
		ECullFace cullFace,
		EFrontFace frontFace,
		boolean enanleDither,
		boolean enableSampleCoverage,
		boolean enableScissorTest
		)
	{
		EnableCullFace = enablrCullFace;
		CullFace = cullFace;
		FrontFace = frontFace;
		
		EnableDither = enanleDither;
		EnableSampleCoverage = enableSampleCoverage;
		EnableScissorTest = enableScissorTest;
		
	}
}
