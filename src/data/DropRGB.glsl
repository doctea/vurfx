uniform int swap_mode;

uniform sampler2D src_tex_unit0;
uniform vec2 src_tex_offset0;

void main(void)
{
    //gl_FragColor = vec4(texture2DRect(src_tex_unit0, src_tex_offset0).abrg);
    vec4 color = texture2D(src_tex_unit0, gl_TexCoord[0].st);

    if(swap_mode==0)
    	gl_FragColor = vec4(0.0, color.g, color.b, color.a);//color.brga);
    else if (swap_mode==1)
	gl_FragColor = vec4(color.r, 0.0, color.g, color.a);
    else if (swap_mode==2)
        gl_FragColor = vec4(color.r, color.g, 0.0, color.gra);
}
