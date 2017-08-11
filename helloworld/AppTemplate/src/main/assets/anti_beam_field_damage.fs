precision mediump float;

//uniform vec3 u_position;
//uniform float u_radius;
//uniform vec3 u_normal;

uniform float u_damageAnim;

//uniform vec3 u_viewPosition;

//uniform	vec4 u_colorIn;
//uniform	vec4 u_colorOut;

uniform vec3 u_damagePosition;
uniform float u_damageRadius;
uniform float u_damageAlpha;

varying vec3 v_world;
/*
float GetTexture( )
{
	float fDot = dot( u_normal, (v_world - u_position) );
	float fPitch = u_radius/1.5;
	float fPi = 3.141593;

	float c0 = cos( fPi*(fDot/fPitch) + u_anim*fPi*2.0 )*0.5+0.5;
	float c1 = cos( fPi*(fDot/fPitch) - u_anim*fPi*2.0 )*0.5+0.5;

	//return (c0+c1)*0.5;
	return c0;
}
*/
float GetDamageTexture()
{
	float fDistance = length( v_world - u_damagePosition );
	float fPitch = u_damageRadius*0.2;
	float fPi = 3.141593;
	
	float c0 = cos( fPi*(fDistance/fPitch) - u_damageAnim*fPi*2.0 )*0.5+0.5;
	
	if( fDistance < u_damageRadius )
	{
		return c0*(1.0-(fDistance/u_damageRadius))*u_damageAlpha;
	}
	else
	{
		return 0.0;
	}
	
}

void main()
{
	/*
	vec3 v3Direction = normalize( v_world - u_viewPosition );
	vec3 v3ViewToCenter = u_position - u_viewPosition;
	float d = dot( v3Direction, v3ViewToCenter );
	vec3 v3Nearest = u_viewPosition + d*v3Direction;

	float l = length( v3Nearest - u_position );
	
	float u_radiusIn = u_radius*0.5;
	
	vec4 v4Color = vec4( 0.0, 0.0, 0.0, 0.0 );
	
	
	if( u_radiusIn < l && l < u_radius )
	{
		float f = (l - u_radiusIn)/(u_radius - u_radiusIn);
		f *= GetTexture();
		v4Color = u_colorIn*(1.0-f) + u_colorOut*(f);
	}
	*/
	
	vec4 v4Color = vec4(GetDamageTexture());
	
	if( v4Color.w <= 0.0 )
	{
		discard;
	}

	gl_FragColor = v4Color;
}


