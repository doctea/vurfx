#version 120

uniform sampler2D src_tex_unit0; //Texture0;
//uniform vec2 gl_TexCoord[];


void main(void)
{
    vec2 texCoord = gl_TexCoord[0].xy;
    vec4 colorOrg = texture2D( src_tex_unit0, texCoord );

    float d = distance(texCoord, vec2(0.5,0.5));

    gl_FragColor = vec4(colorOrg.r*2,colorOrg.g,colorOrg.b, d*2);
    //gl_FragColor = vec4(colorOrg.r-(d/4),colorOrg.g-(d/4,colorOrg.b-(d/4), d/4);
    //gl_FragColor = vec4(colorOrg.rgb, 0);

}
