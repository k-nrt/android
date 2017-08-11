uniform mat4 u_world;
uniform mat4 u_worldViewProjection;

attribute vec3 a_position;

varying vec3 v_world;

void main()
{
	v_world = (u_world*vec4(a_position, 1.0 )).xyz;
	gl_Position = u_worldViewProjection*vec4(a_position,1.0);
}


