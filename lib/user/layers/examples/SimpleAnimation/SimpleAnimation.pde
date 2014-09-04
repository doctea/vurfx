
import com.nootropic.processing.layers.*;

int w = 250;
int h = w;
int startX, startY;
AppletLayers layers;

void setup() {
  size(250, 250);
  frameRate(24);
  layers = new AppletLayers(this);
  colorMode(HSB);
  cursor(CROSS);
}

void paint(java.awt.Graphics g) {
  if (layers != null) {
    layers.paint(this);
  } 
  else {
    super.paint(g);
  }
}

void draw() {
  background(255);
  if (mousePressed) {
    stroke(140);
    line(startX, startY, mouseX, mouseY);
  }
  ListIterator li = layers.getListIterator();
  while (li.hasNext()) {
    AnimatedObject ao = (AnimatedObject)li.next();
    if (ao.age > 100) {
      li.remove();
    }
  }
}

void keyPressed() {
  if (looping) {
    noLoop();
  } 
  else {
    loop();
  }
}

void mousePressed() {
  startX = mouseX;
  startY = mouseY;
}

void mouseReleased() {
  float scale = 0.2f;
  PVector v = new PVector((mouseX - startX)*scale, (mouseY - startY)*scale);
  layers.addLayer(new AnimatedObject(this, mouseX, mouseY, v));
}



