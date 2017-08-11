varying vec3 v_local;

#ifdef VERTEX_SHADER
void VsMain()
{
	v_local = a_position;
}
#endif //VERTEX_SHADER

#ifdef FRAGMENT_SHADER
uniform float u_discardStart;
uniform float u_discardEnd;
uniform float u_discardUnit;
uniform float u_discardRadius;
uniform float u_startRadius;

uniform sampler2D u_sampler2dNoise;

float saturate( float a )
{
	return max(min(a,1.0),0.0);
}


vec4 FsMain( vec4 v4Color )
{

#if 1
	vec3 v3TexCoord = fract( v_local*(1.0/100.0) );
	v3TexCoord.xy *= 1.0/8.0;
	float z = v3TexCoord.z;
	z = floor( z*64.0 );
	
	float oy = floor(z/8.0);
	float ox = z - oy*8.0;
	
	v3TexCoord.xy += vec2(ox,oy)/8.0;
	float s = (length(v_local)-u_discardStart)/(u_discardEnd-u_discardStart);
	float f1 = texture2D( u_sampler2dNoise, v3TexCoord.xy ).x;
	
	s = (1.0 - s)*1.4;
	float w = 0.125;

	if( f1 < s )
	{
		discard;
	}
	else if( f1 < (s+w) )
	{
		float l = ((s+w) - f1)/w;
	
		vec4 v4FlashColor = vec4( 10.0, 2.0, 1.0, 1.0 );
		return lerp( l, v4Color, v4FlashColor );
	}
	else
	{
		return v4Color;
	}

#else
	vec3 c0 = vec3( 0.0, 0.0, 0.0 );
	vec3 c1 = vec3( -0.5, -0.5, -0.5 );
	//float f = length( fract( v_local*(1.0/100.0) + c0) - 0.5) *
	//	length( fract( v_local*(1.0/1000.0) + c1 ) - 0.5);
	
	float s = (length(v_local)-u_discardStart)/(u_discardEnd-u_discardStart);
	float f1 = length( fract((v_local/u_discardUnit)) + c1 )*2.0;
	
	s = (1.0 - s)*1.4;
	float w = 0.25;

	if( f1 < s )
	{
		discard;
	}
	else if( f1 < (s+w) )
	{
		float l = ((s+w) - f1)/w;
	
		vec4 v4FlashColor = vec4( 10.0, 2.0, 1.0, 1.0 );
		return lerp( l, v4Color, v4FlashColor );
	}
	else
	{
		return v4Color;
	}
#endif
	
#if 0
	if( s < 0.0 || f1 < (1.0 - s))
	{
		discard;
	}
	else if( s < 1.0 )
	{/*
		float l = s - (s - 0.1);
		float a = (f1 - u_discardRadius)/l;
		
		vec4 v4FlashColor = vec4( 10.0, 2.0, 1.0, 1.0 );
		return lerp( a, v4FlashColor, v4Color );
	*/
		return vec4( s, s, s, 1 );
	}
	else
	{
		return v4Color;
	}
#endif

	/*
	float f = saturate(f0*f1);
		
	if( f < u_discardRadius )
	{
		discard;
		//gl_FragColor = v4Shading;
		//return vec4(fract(v_local/100.0),1);
	}
	else if( f < u_startRadius )
	{
		float l = (u_startRadius - u_discardRadius);
		float a = (f - u_discardRadius)/l;
		
		vec4 v4FlashColor = vec4( 10.0, 2.0, 1.0, 1.0 );
		return lerp( a, v4FlashColor, v4Color );
	}
	else
	{
		//return vec4(0,1,0,1);
		return v4Color;
	}
	*/
}
#endif //FRAGMENT_SHADER

