#version 120
uniform int swap_mode;

uniform sampler2D src_tex_unit0;
uniform vec2 src_tex_offset0;

void main(void)
{
    //gl_FragColor = vec4(texture2DRect(src_tex_unit0, src_tex_offset0).abrg);
    vec4 color = texture2D(src_tex_unit0, gl_TexCoord[0].st);

    if(swap_mode==0.0)
    	gl_FragColor = vec4(color.brga);
    else if (swap_mode==1.0)
	gl_FragColor = vec4(color.rbga);
    else if (swap_mode==2)
        gl_FragColor = vec4(color.gbra);
    else if (swap_mode==3)
        gl_FragColor = vec4(color.bgra);
    else if (swap_mode==4)
        gl_FragColor = vec4(color.rbga);
    else if (swap_mode==5)
        gl_FragColor = vec4(color.grba);

    /*if(gl_FragColor.g == gl_FragColor.r && gl_FragColor.r == gl_FragColor.b) {
        if((1.0*float(swap_mode)/3.0)>1.0) gl_FragColor.r = 0.0;
	if((1.0*float(swap_mode)/4.0)>1.0) gl_FragColor.g = 0.0;
	if((1.0*float(swap_mode)/5.0)>1.0) gl_FragColor.b = 0.0;
    }*/


}
