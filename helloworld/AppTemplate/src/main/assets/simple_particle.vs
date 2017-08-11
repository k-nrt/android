attribute vec4 a_positionIndex;
attribute vec4 a_uvOffsetAlphaRadius;
attribute float a_angle;

varying vec2 v_texCoord;
varying float v_alpha;


//uniform mat4 u_worldViewProjection,
uniform mat4 u_worldView;
uniform mat4 u_projection;

uniform vec2 u_targetSize;

void main()
{
	vec3 f3View = (u_worldView*vec4(a_positionIndex.xyz, 1.0 )).xyz; //mul( float4(a_PositionIndex.xyz,1.0f), WorldView ).xyz;
	int index = int( a_positionIndex.w );

	float fRadius = a_uvOffsetAlphaRadius.w;
	vec2 f2TexCoord = a_uvOffsetAlphaRadius.xy;

	mat3 matrix3x3;
	float c = cos( a_angle );
	float s = sin( a_angle );
	matrix3x3[0] = vec3( c,s,0.0 );
	matrix3x3[1] = vec3( -s,c,0.0 );
	matrix3x3[2] = vec3( 0.0, 0.0, 1.0);

	if( index == 0 )
	{
		f3View += ( vec3( -1.0, 1.0, 0.0 )*matrix3x3 )*fRadius;
	}
	else if( index == 1 )
	{
		f3View += ( vec3( -1.0, -1.0, 0.0 )*matrix3x3 )*fRadius;
		f2TexCoord += vec2( 0.0, 1.0 )*u_targetSize;
	}
	else if(index == 2 )
	{
		f3View += ( vec3(  1.0, -1.0, 0.0 )*matrix3x3 )*fRadius;
		f2TexCoord += vec2( 1.0, 1.0 )*u_targetSize;	
	}
	else
	{
		f3View += ( vec3( 1.0, 1.0, 0.0 )*matrix3x3 )*fRadius;
		f2TexCoord += vec2( 1.0, 0.0 )*u_targetSize;	
	}

	gl_Position	= u_projection*vec4( f3View, 1.0 ); //mul( float4(f3View,1.0f), Projection );
	v_texCoord = f2TexCoord;
	v_alpha = a_uvOffsetAlphaRadius.z;
}
	
	

/*
void main
(
	float4 in a_PositionIndex    : POSITION,
	float4 in a_UVOffsetAlphaRadius : TEXCOORD0,
	float in a_Angle : TEXCOORD1,

	float4 out v_Position : POSITION,
	float2 out v_TexCoord : TEXCOORD0,
	float out v_Alpha : TEXCOORD1,

	uniform float4x4 WorldViewProjection,
	uniform float4x4 WorldView,
	uniform float4x4 Projection,
	
	uniform float TargetSize
)
{
	float3 f3View = mul( float4(a_PositionIndex.xyz,1.0f), WorldView ).xyz;
	int index = a_PositionIndex.w;

	float fRadius = a_UVOffsetAlphaRadius.w;
	float2 f2TexCoord = a_UVOffsetAlphaRadius.xy;

	float3x3 matrix3x3;
	float c = cos( a_Angle );
	float s = sin( a_Angle );
	matrix3x3[0] = float3( c,s,0.0f );
	matrix3x3[1] = float3( -s,c,0.0f );
	matrix3x3[2] = float3( 0.0f, 0.0f, 1.0f);

	if( index == 0 )
	{
		f3View += mul( float3( -1.0f, 1.0f, 0.0f ), matrix3x3 )*fRadius;
	}
	else if( index == 1 )
	{
		f3View += mul( float3( -1.0f, -1.0f, 0.0f ), matrix3x3 )*fRadius;
		f2TexCoord += float2( 0.0f, 1.0f )*TargetSize;
	}
	else if(index == 2 )
	{
		f3View += mul( float3(  1.0f, -1.0f, 0.0f ), matrix3x3 )*fRadius;
		f2TexCoord += float2( 1.0f, 1.0f )*TargetSize;	
	}
	else
	{
		f3View += mul( float3(  1.0f,  1.0f, 0.0f ), matrix3x3 )*fRadius;
		f2TexCoord += float2( 1.0f, 0.0f )*TargetSize;	
	}

	v_Position	= mul( float4(f3View,1.0f), Projection );
	v_TexCoord = f2TexCoord;
	v_Alpha = a_UVOffsetAlphaRadius.z;
	
	
	//if( f4Position.w )
	//{
	//	v_PointSize = TargetSize*f4Position.x/f4Position.w;
	//}
	//else
	//{
	//	v_PointSize = 1.0f;
	//}
}
*/
