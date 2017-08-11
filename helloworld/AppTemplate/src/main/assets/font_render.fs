precision mediump float;

uniform sampler2D u_texture0;
uniform sampler2D u_texture1;

uniform sampler2D u_texture2;
uniform sampler2D u_texture3;
/*
uniform sampler2D u_texture4;
uniform sampler2D u_texture5;
uniform sampler2D u_texture6;
uniform sampler2D u_texture7;
*/
varying vec4 v_color;
varying vec4 v_texcoord;

void main()
{
	vec4 v4Font; //= texture2D( u_texture0, v_texcoord.xy );
	float t = v_texcoord.z;
	if( t < 2.0 )
	{
		if( t < 1.0 )
		{
			v4Font = texture2D( u_texture0, v_texcoord.xy );
		}
		else
		{
			v4Font = texture2D( u_texture1, v_texcoord.xy );
		}
	}
	else
	{
		if( t < 3.0 )
		{
			v4Font = texture2D( u_texture2, v_texcoord.xy );
		}
		else
		{
			v4Font = texture2D( u_texture3, v_texcoord.xy );
		}
	}
	/*
	float t = v_texcoord.z;
	if( t < 4.0 )
	{
		if( t < 2.0 )
		{
			if( t < 1.0 )
			{
				v4Font = texture2D( u_texture0, v_texcoord.xy );
			}
			else
			{
				v4Font = texture2D( u_texture1, v_texcoord.xy );
			}
		}
		else
		{
			if( t < 3.0 )
			{
				v4Font = texture2D( u_texture2, v_texcoord.xy );
			}
			else
			{
				v4Font = texture2D( u_texture3, v_texcoord.xy );
			}
		}
	}
	else
	{
		if( t < 6.0 )
		{
			if( t < 5.0 )
			{
				v4Font = texture2D( u_texture4, v_texcoord.xy );
			}
			else
			{
				v4Font = texture2D( u_texture5, v_texcoord.xy );
			}
		}
		else
		{
			if( t < 7.0 )
			{
				v4Font = texture2D( u_texture6, v_texcoord.xy );
			}
			else
			{
				v4Font = texture2D( u_texture7, v_texcoord.xy );
			}
		}
	}
	*/
	vec4 v4Color = v_color;
	float c = v_texcoord.w;
	if( c < 2.0 )
	{
		if( c < 1.0 )
		{
			v4Color.w *= v4Font.x;
		}
		else
		{
			v4Color.w *= v4Font.y;
		}
	}
	else
	{
		if( c < 3.0 )
		{
			v4Color.w *= v4Font.z;
		}
		else
		{
			v4Color.w *= v4Font.w;
		}
	}
	
	v4Color.w = v4Font.x;
	
	gl_FragColor = v4Color;
	//gl_FragColor = v4Font; //vec4(1,0,0,1);
}

