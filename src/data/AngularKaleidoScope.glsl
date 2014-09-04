
uniform sampler2D src_tex_unit0;
varying vec4 vertexcoord;


void main() {
	gl_FragColor = texture2D ( src_tex_unit0, abs(vertexcoord.xy) );
}
