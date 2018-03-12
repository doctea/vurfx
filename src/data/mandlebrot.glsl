//uniform sampler1D tex;
uniform sampler2D src_tex_unit0;
//uniform vec2 center;
uniform float x;
uniform float y;

uniform float rotate;

#define tex src_tex_unit0


#define center vec2(x,y)
uniform float scale;
uniform int iter;

varying vec4 vertTexCoord;

//#define gl_TexCoord vertTexCoord.st

void main() {
    vec2 z, c;
    vec2 TexCoord = vertTexCoord.st;

    c.x = //1.3333 *
    		(TexCoord.x - 0.5) * scale - x; //center.x;
    c.y = (TexCoord.y - 0.5) * scale - y; //center.y;

    //c.x = rotate/60.0 * cos(c.x);
    //c.y = rotate/60.0 * tan(c.y);
    float old_x = c.x;
    c.x = c.x * cos(rotate/60.0) - c.y * sin(rotate/60.0);
    c.y = old_x * sin(rotate/60.0) + c.y * cos(rotate/60.0);

    int i;
    z = c;
    for(i=0; i<iter; i++) {
        float x = (z.x * z.x - z.y * z.y) + c.x;
        float y = (z.y * z.x + z.x * z.y) + c.y;

        if((x * x + y * y) > 4.0) break;
        z.x = x;
        z.y = y;
    }

    //gl_FragColor = texture1D(tex, (i == iter ? 0.0 : float(i)) / 100.0);
    //float v = (i == iter ? 0.0 : iter/float(i)) / 100.0;
    //gl_FragColor = vec4(0.7 + sin(v), 0.3 + sin(v), 1.0/v, 1.0);
    gl_FragColor = texture2D(tex, vec2((i == iter ? 0.0 : float(i)) / 10.0, (i == iter ? 0.0 : float(i)) / 10.0));

}

/*
 * // http://nuclear.mutantstargoat.com/articles/sdr_fract/#mbrot_sdr
 * uniform sampler1D tex;
uniform vec2 center;
uniform float scale;
uniform int iter;

void main() {
    vec2 z, c;

    c.x = 1.3333 * (gl_TexCoord[0].x - 0.5) * scale - center.x;
    c.y = (gl_TexCoord[0].y - 0.5) * scale - center.y;

    int i;
    z = c;
    for(i=0; i<iter; i++) {
        float x = (z.x * z.x - z.y * z.y) + c.x;
        float y = (z.y * z.x + z.x * z.y) + c.y;

        if((x * x + y * y) > 4.0) break;
        z.x = x;
        z.y = y;
    }

    gl_FragColor = texture1D(tex, (i == iter ? 0.0 : float(i)) / 100.0);
}
	*/
