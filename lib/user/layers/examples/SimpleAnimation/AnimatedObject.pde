class AnimatedObject extends Layer {
  int x, y;
  PVector v;
  int c;
  int size;
  int age;
  int alpha = 180;
  int hue = 140;

  AnimatedObject(PApplet parent, int x, int y, PVector v) {
    super(parent);
    this.x = x;
    this.y = y;
    this.v = v;
    c = color(hue, random(128, 255), 255, alpha);
    size = (int)v.mag() * 5 + 10;
  }

  void setup() {
    smooth();
    colorMode(HSB);
  }

  void draw() {
    age++;
    background(0, 0);
    noStroke();
    fill(c);
    x += v.x;
    if (x > w) {
      x = x-w;
    }
    if (x <= 0) {
      x = w+x;
    }

    y += v.y;
    if (y > h) {
      y = y-h;
    }
    if (y <= 0) {
      y = h+y;
    }

    ellipse(x, y, size, size);
  }
}

