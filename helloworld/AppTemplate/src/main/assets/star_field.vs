uniform mat4 u_worldViews[8];
uniform mat4 u_projections[8];

uniform mat4 u_viewInverse;

uniform float u_fogNear;
uniform float u_fogFar; 

uniform float u_fIntensities[8];

attribute vec3 a_pos;
attribute float a_index;
varying vec4 v_color;
//varying float v_fog;

void main()
{
	float fSize = 2000.0;
	float fHSize = fSize*0.5;
	vec3 f3ViewPosition;

	//modf( vec3((u_viewInverse[3])/fSize), f3ViewPosition );
	f3ViewPosition = vec3(u_viewInverse[3]/fSize) - fract( vec3( u_viewInverse[3]/fSize ) );
	f3ViewPosition = f3ViewPosition*fSize;

	vec3 f3Position = a_pos.xyz*fHSize + f3ViewPosition;

	if( f3Position.x < u_viewInverse[3].x - fHSize )
	{
		f3Position.x += fSize;
	}
	else if( f3Position.x > u_viewInverse[3].x + fHSize )
	{
		f3Position.x -= fSize;
	}
		
	if( f3Position.y < u_viewInverse[3].y - fHSize )
	{
		f3Position.y += fSize;
	}
	else if( f3Position.y > u_viewInverse[3].y + fHSize )
	{
		f3Position.y -= fSize;
	}

	if( f3Position.z < u_viewInverse[3].z - fHSize )
	{
		f3Position.z += fSize;
	}
	else if( f3Position.z > u_viewInverse[3].z + fHSize )
	{
		f3Position.z -= fSize;
	}

	int i = int(a_index);
	//"gl_Position = u_projections[i] * u_worldViews[i] * vec4(a_pos.xyz + vec3(0.0,a_index,0.0),1.0);",
	
	vec4 v4HPos = u_projections[i] * u_worldViews[i] * vec4(f3Position,1.0);
	gl_Position = v4HPos;
	//"if(a_index == 0.0 ) { gl_Position = u_projections[0] * u_worldViews[0] * vec4(a_pos.xyz,1.0);}",
	//"else { gl_Position = u_projections[3] * u_worldViews[3] * vec4(a_pos.xyz,1.0);}",
	
	//gl_PointSize = 2.0;
	
	
	float z = v4HPos.w;
	float n = u_fogNear;
	float f = u_fogFar;
	z = clamp( 1.0 - (z - n)/(f-n), 0.0, 1.0 );
	
	float c = u_fIntensities[i];//(4.0-a_index)/4.0;
	v_color = vec4(c,c,c,z);
}
/*
void main
(
	float4 in a_Position  : POSITION,
	float4 out v_Position : POSITION,
	float out v_PSize : PSIZE,
	uniform float4x4 WorldViewProjection,
	uniform float4x4 WorldView,
	uniform float4x4 Projection,
	uniform float4x4 ViewInverse,
	uniform float TargetWidth
)
{
	float fSize = 2000.0f;
	float fHSize = fSize*0.5f;
	float3 f3ViewPosition;

	modf( float3((ViewInverse[3])/fSize), f3ViewPosition );
	f3ViewPosition = f3ViewPosition*fSize;

	float3 f3Position = a_Position.xyz*fHSize + f3ViewPosition;

	if( f3Position.x < ViewInverse[3].x - fHSize )
	{
		f3Position.x += fSize;
	}
	else if( f3Position.x > ViewInverse[3].x + fHSize )
	{
		f3Position.x -= fSize;
	}
		
	if( f3Position.y < ViewInverse[3].y - fHSize )
	{
		f3Position.y += fSize;
	}
	else if( f3Position.y > ViewInverse[3].y + fHSize )
	{
		f3Position.y -= fSize;
	}

	if( f3Position.z < ViewInverse[3].z - fHSize )
	{
		f3Position.z += fSize;
	}
	else if( f3Position.z > ViewInverse[3].z + fHSize )
	{
		f3Position.z -= fSize;
	}
	
	v_Position = mul( float4(f3Position,1.0f), WorldViewProjection );
	
	float fPSize = 3.0f;
	
	float4 f4ViewPosition = mul( float4(f3Position,1.0f), WorldView );
	float4 f4ProjectionSize = mul( float4( fPSize, fPSize, f4ViewPosition.z, 1.0f ), Projection );

	if( f4ProjectionSize.w != 0.0f )	
	{
		v_PSize = min( f4ProjectionSize.x*TargetWidth/f4ProjectionSize.w, 3.0f );
	}
	else
	{		
		v_PSize = 1.0f;
	}
}
*/
