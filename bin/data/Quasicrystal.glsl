//precision mediump float;

varying vec2 position;
uniform float time;

float wave(vec2 p, float angle) {
  vec2 direction = vec2(cos(angle), sin(angle));
  return cos(dot(p, direction));
}

float wrap(float x) {
  return abs(mod(x, 2.)-1.);
}

void main() {
  vec2 p = (position - 0.5) * 50.;

  float brightness = 0.;
  for (float i = 1.; i <= 11.; i++) {
    brightness += wave(p, time / i);
  }

  brightness = wrap(brightness);

  gl_FragColor.rgb = vec3(brightness);
  gl_FragColor.r = 1.;
  gl_FragColor.g = 0.5;
  gl_FragColor.a = 1.;
}
