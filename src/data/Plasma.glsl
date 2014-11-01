// http://www.bidouille.org/prog/plasma
//precision mediump float;
#define PI 3.1415926535897932384626433832795
#extension GL_EXT_gpu_shader4 : enable
 
uniform int u_time_2;
uniform int width;
uniform int height;
//varying vec2 v_coords;
 
void main() {
	//gl_FragColor = vec4(1,0.25,0.75,1); //vec4(sin(u_time),sin(u_time),sin(u_time),sin(u_time));

    vec2 v_coords = gl_TexCoord[0].st;

    float u_time = float(u_time_2)/50.0;///10.0;

    float v = 0.0;
    vec2 u_k = vec2(float(width),float(height));
    vec2 c = v_coords * u_k - u_k/2.0;
    v += sin((c.x+u_time));
    v += sin((c.y+u_time)/2.0);
    v += sin((c.x+c.y+u_time)/2.0);
    c += u_k/2.0 * vec2(sin(u_time/3.0), cos(u_time/2.0));
    v += sin(sqrt(c.x*c.x+c.y*c.y+1.0)+u_time);
    v = v/2.0;

    //vec3 col = vec3(1, sin(PI*v), cos(PI*v));
    vec3 col = vec3(1,sin(PI*v)/2.0,v/4.0);

    gl_FragColor = vec4(col*.5 + .5, 1);
}
