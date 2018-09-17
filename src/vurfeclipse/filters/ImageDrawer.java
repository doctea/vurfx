package vurfeclipse.filters;

import processing.core.PImage;
import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;

class ImageDrawer extends Filter {
  
  int numBlobs = 500;
  
  int offsetx=0,offsety=0;
  
  PImage image_src;
  String fileName = "output/image48173.40247.jpg";
  
  boolean fileChanged = true;
  
  ImageDrawer(Scene sc) {
    super(sc);
  }
  
  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/
  
  public boolean initialise() {
    // set up inital variables or whatevs 
    image_src = new PImage(sc.w,sc.h);
    //PImage im = loadImage(fileName); //"image48173.40247.jpg");
    //image_src.putImage(im);
    image_src.loadTexture(fileName);
    fileChanged = true;
    
    return true;
  }
  
  public boolean applyMeatToBuffers() {
    //System.out.println("in applymeattobuffers in pointdrawer (" + this + "), src is " + src);
    //if (fileChanged) {
      //image draw mode
      out.image(image_src,0,0);
      //fileChanged = false; 
    //}
      
    
    // pixel copy mode
      //arrayCopy(src.pixels, out.pixels);
    return true;
  }
  
  /*public void beginDraw() {
    //src.loadPixels();
    //out.loadPixels();
    out.beginDraw();
  }
  
  public void endDraw() {
    //out.updatePixels();
    out.endDraw();
  }*/
  
}
