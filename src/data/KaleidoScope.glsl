#define PI 3.141592653589793
uniform sampler2D src_tex_unit0;

varying vec4 vertTexCoord;

uniform int mirror_x;
uniform int mirror_y;
uniform int half_x;
uniform int half_y;

void main(void)
{
    vec2 tex_coord = vertTexCoord.st;

    //vec4 ts = textureSize2D(src_tex_unit0);
    if (mirror_x==1 && tex_coord.x > 0.5) {
      tex_coord.xy = vec2(1.0-tex_coord.x, tex_coord.y);
    }
    if (mirror_y==1 && tex_coord.y > 0.5) {
      tex_coord.xy = vec2(tex_coord.x, 1.0-tex_coord.y);
    }

    if (half_x==1 && mirror_x==1) {
    	tex_coord.x = tex_coord.x + 0.5;
    } else if (half_x==1 && mirror_x==1) {
    	tex_coord.x = tex_coord.x - 0.5;
    }
    if (half_y==1 && mirror_y==1) {
    	tex_coord.y = tex_coord.y + 0.5;
    } else if (half_y==1 && mirror_y==1) {
    	tex_coord.y = tex_coord.y - 0.5;
    }

    gl_FragColor = vec4( texture2D(src_tex_unit0, tex_coord).rgba );
    
}
