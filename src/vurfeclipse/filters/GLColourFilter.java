package vurfeclipse.filters;


import processing.core.PGraphics;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;

public class GLColourFilter extends ShaderFilter {

  int mode = 0;
  //transient GLTexture t;

  public GLColourFilter(Scene sc) {
    super(sc,"PhaseRGB.glsl");
  }

  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/

  //PShader glFilter;
  public boolean initialise() {
	super.initialise();
    // set up inital variables or whatevs
    /*temp = new int[sc.w*sc.h];
    pixelCount = sc.w*sc.h;*/


    //glFilter = new GLTextureFilter();
    //glFilter.setTint((int)random(255)); //random(1),random(1),random(1));
    //glFilter = new GLTextureFilter(APP, "SwapRGB.xml");

    //glFilter = APP.getApp().loadShader("PhaseRGB.glsl"); //new (APP.getApp(), "PhaseRGB.xml");

    //t = new GLTexture(APP,sc.w,sc.h);
    return true;
  }

  float r_shift, g_shift, b_shift;

  public void setRedGreenBlueShift(float r, float g, float b) {
    this.r_shift = r;
    this.g_shift = g;
    this.b_shift = b;
    glFilter.set("rshift", r);
    glFilter.set("gshift", g);
    glFilter.set("bshift", b);
  }

  public Filter nextMode() {
    mode++;
    if(mode>5) mode = 0;
    glFilter.set("swap_mode", mode);
    return this;
  }

  int a_bitshift = 255<<24;

  int[] temp;


  boolean autoChange = false;
  int autoCount = 0;
  int autoLimit = 5;

  int pixelCount;
  public boolean applyMeatToBuffers() {

    //GLTexture t = new GLTexture(APP,sc.w,sc.h);
    /*if (src==out) {
      t.copy(src.getTexture());
    } else {
      t = src.getTexture();
    }*/

    //Canvas t = ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_in);//.getTexture();

    //t.filter(glFilter, ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_out).getSurf());//.getTexture());
    //this.filter(t.getSurf(), glFilter, ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_out).getSurf());
	  
	PGraphics t = in();  
	
    this.filter(t, glFilter, out());

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
