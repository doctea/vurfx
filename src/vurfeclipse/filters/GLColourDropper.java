package vurfeclipse.filters;


import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
import codeanticode.glgraphics.*;
import processing.core.PGraphics;
import processing.opengl.PShader;

class GLColourDropper extends Filter {

  int mode = 0;
  PGraphics t;

  GLColourDropper(Scene sc) {
    super(sc);
  }

  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/

  PShader glFilter;
  public boolean initialise() {
    // set up initial variables or whatevs
    temp = new int[sc.w*sc.h];
    pixelCount = sc.w*sc.h;


    //glFilter = new GLTextureFilter();
    //glFilter.setTint((int)random(255)); //random(1),random(1),random(1));
    glFilter = APP.getApp().loadShader("DropRGB.glsl");

    t = new PGraphics();
    t.setSize(sc.w, sc.h);
    return true;
  }

  public Filter nextMode() {
    mode++;
    if(mode>3) mode = 0;
    glFilter.set("swap_mode", mode);
    return this;
  }

  int a_bitshift = 255<<24;

  int[] temp;


  boolean autoChange = false;
  int autoCount = 0;
  int autoLimit = 3;

  int pixelCount;
  public boolean applyMeatToBuffers() {

    //GLTexture t = new GLTexture(APP,sc.w,sc.h);
    if (src==out) {
      //t.copy(src);
    	t.image(src, 0, 0, sc.w, sc.h);
    } else {
      t = src;
    }

    t.filter(glFilter,out);
    //t.shader(glFilter);

    return true;
  }
/*
  public void beginDraw() {
    //System.out.println("beginDraw in " + this);
  }
  public void endDraw() {
    //out.loadPixels(); // makes no difference apparently
    //out.updatePixels(); // stops form working
    //out.loadTexture(); // this line stops this from working...
  }
  */
}
