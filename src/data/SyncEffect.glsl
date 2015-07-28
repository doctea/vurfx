#define PI 3.141592653589793

uniform sampler2D src_tex_unit0;

uniform float step_x;
uniform float step_y;

void main(void)
{
    vec2 tex_coord = gl_TexCoord[0].st;

    //vec4 ts = textureSize2D(src_tex_unit0);
    /*if (mirror_x==1 && gl_TexCoord[0].x > 0.5) {
      tex_coord.xy = vec2(1.0-tex_coord.x, tex_coord.y);
    }
    if (mirror_y==1 && gl_TexCoord[0].y > 0.5) {
      tex_coord.xy = vec2(tex_coord.x, 1.0-tex_coord.y);
    }*/
    
    
    
    //tex_coord.xy = vec2(tex_coord.x * step_x, tex_coord.y * step_y);
   		// ^^ this one for straightforward 'grid' 
   
    
    //float ang = atan2(tex_coord.xy);
    //float d = distance(vec2(0.0,0.0),tex_coord.xy);  
    
    //tex_coord.x = sin(ang) * (d + step_x);
    //tex_coord.y = cos(ang) * (d + step_x);
    
    vec2 new_coord = vec2(0.0,0.0);
    new_coord.x = 0.5 + sin(cos(tex_coord.x) * step_x);
    new_coord.y = 0.5 + cos(tan(tex_coord.y) * step_y);
    tex_coord.xy = new_coord.xy;
    // ^^^ this one for sin-wavey wibblewobbly-ness
    
    gl_FragColor = vec4( texture2D(src_tex_unit0, tex_coord).rgba );
    
}
