precision highp float;

varying vec3 v_f3SegmentDirection;
varying vec3 v_f3SegmentPosition;
varying vec3 v_f3World;
varying float v_fRadius;

//	float4 out Color : COLOR,
	
uniform vec3 u_v3ViewPosition;
uniform vec4 u_v4ColorIn;
uniform vec4 u_v4ColorOut;

vec4 lerp( vec4 v0, vec4 v1, float l )
{
	return v0*( 1.0 - l ) + v1*l;
}


void main()
{
	vec3 f3SegmentDirection = normalize( v_f3SegmentDirection );
	vec3 f3EyeDirection = normalize( v_f3World - u_v3ViewPosition );

	vec3 f3Cross = normalize( cross( f3SegmentDirection, f3EyeDirection ) );
	
	float fDistance = abs( dot( f3Cross, v_f3SegmentPosition - u_v3ViewPosition ) );
	
	//float4 f4ColorIn  = float4( 1.0f, 2.0f, 3.0f, 1.0f );
	//float4 f4ColorOut = float4( 0.0f, 0.0f, 0.5f, 1.0f );
	
	gl_FragColor = lerp( u_v4ColorIn, u_v4ColorOut, fDistance/v_fRadius );

}



/*
void main
(
	float3 in v_f3SegmentDirection : TEXCOORD0,
	float3 in v_f3SegmentPosition  : TEXCOORD1,
	float3 in v_f3World            : TEXCOORD2,
	float  in v_fRadius           : TEXCOORD3,

	float4 out Color : COLOR,
	
	uniform float3 ViewPosition,
	uniform float4 ColorIn,
	uniform float4 ColorOut
)
{
	float3 f3SegmentDirection = normalize( v_f3SegmentDirection );
	float3 f3EyeDirection = normalize( v_f3World - ViewPosition );

	float3 f3Cross = normalize( cross( f3SegmentDirection, f3EyeDirection ) );
	
	float fDistance = abs( dot( f3Cross, v_f3SegmentPosition - ViewPosition ) );
	
	//float4 f4ColorIn  = float4( 1.0f, 2.0f, 3.0f, 1.0f );
	//float4 f4ColorOut = float4( 0.0f, 0.0f, 0.5f, 1.0f );
	
	Color = lerp( ColorIn, ColorOut, fDistance/v_fRadius );
}
*/
