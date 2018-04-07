#define PROCESSING_TEXTURE_SHADER

uniform sampler2D src_tex_unit0;

varying vec4 vertTexCoord;

uniform vec2 dest_tex_size;
uniform float dest_tex_size_x;
uniform float dest_tex_size_y;

uniform float iTime;
uniform float scale; // = 7.2;
uniform int iterations; // = 32;
uniform float centre_x; // = .5;
uniform float centre_y; // = .5;
uniform float adjustor; // = .25;

uniform float colourPhase; // = .25;

uniform float r;

//#define iResolution dest_tex_size
#define fragCoord vertTexCoord

void main()
{
	vec2 iResolution = vec2(1.0,1.0); //dest_tex_size_x,dest_tex_size_y);

	vec2 uv = (fragCoord.xy-vec2(centre_x,centre_y)*iResolution.xy) * scale / iResolution.y;

    //float r = 1.0;
    float a = iTime*.1;
    float c = cos(a)*r;
    float s = sin(a)*r;
    for ( int i=0; i<iterations; i++ )
    {
    	uv = abs(uv);
        uv -= adjustor;
        uv = uv*c + s*uv.yx*vec2(1,-1);
    }

    gl_FragColor = .5+.5*sin(
        iTime
		+ (colorPhase * vec4(13,17,23,1))
		*
        texture( src_tex_unit0, uv*vec2(1,-1)+centre_x, -1.0 )
	);
}
