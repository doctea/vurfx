#define PROCESSING_TEXTURE_SHADER

uniform sampler2D src_tex_unit0;

varying vec4 vertColor;
varying vec4 vertTexCoord;

//uniform vec2 src_tex_offset0;
uniform float dest_tex_size_x;
uniform float dest_tex_size_y;

uniform float pixel_size;

//varying vec4 vertTexCoord;

//#define vertTexCoord gl_TexCoord[0]
//#define vertTexCoord src_tex_offset0


// version from old vurfx - non-processing3 version
void main(void)
{
    float d = 1.0 / pixel_size;
    vec2 tex_coords = vertTexCoord.st;

	int fx = int(tex_coords.s * dest_tex_size_x / pixel_size);
	int fy = int(tex_coords.t * dest_tex_size_y / pixel_size);
	
    float s = pixel_size * (float(fx) + d) / dest_tex_size_x;
    float t = pixel_size * (float(fy) + d) / dest_tex_size_y;
    
    s -= pixel_size*2. /dest_tex_size_x;
    t -= pixel_size*2. /dest_tex_size_y;

    gl_FragColor = texture2D(src_tex_unit0, vec2(s, t)).rgba;
}

// new version pinched from https://processing.org/tutorials/pshader/
/*void main() {
  //pixel_size =
  //float mult = 50.0 * (1.0/pixel_size);
	float mult = pixel_size; // / dest_tex_size_x;
  int si = int(vertTexCoord.s * mult);
  int sj = int(vertTexCoord.t * mult);
  gl_FragColor = texture2D(src_tex_unit0, vec2(float(si) / mult, float(sj) / mult));// * vertColor;
}*/

