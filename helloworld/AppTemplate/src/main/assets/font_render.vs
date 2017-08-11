uniform mat4 u_worldViewProjection;

attribute vec3 a_position;
attribute vec4 a_color;
attribute vec4 a_texcoord;

varying vec4 v_color;
varying vec4 v_texcoord;

void main()
{
	gl_Position = u_worldViewProjection * vec4(a_position,1.0);
	v_color = a_color;
	v_texcoord = a_texcoord;
}


