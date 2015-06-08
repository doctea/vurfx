package vurfeclipse.filters;


import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class GLColourFilter extends Filter {
  
  int mode = 0;
  //transient GLTexture t;
  
  public GLColourFilter(Scene sc) {
    super(sc);
  }
  
  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/
  
  GLTextureFilter glFilter;
  public boolean initialise() {
    // set up inital variables or whatevs 
    /*temp = new int[sc.w*sc.h];
    pixelCount = sc.w*sc.h;*/
    
    
    //glFilter = new GLTextureFilter();
    //glFilter.setTint((int)random(255)); //random(1),random(1),random(1));
    //glFilter = new GLTextureFilter(APP, "SwapRGB.xml");
	  
    glFilter = new GLTextureFilter(APP.getApp(), "PhaseRGB.xml");

    //t = new GLTexture(APP,sc.w,sc.h);
    return true;
  }

  float r_shift, g_shift, b_shift;
  
  public void setRedGreenBlueShift(float r, float g, float b) {
    this.r_shift = r;
    this.g_shift = g;
    this.b_shift = b;
    glFilter.setParameterValue("rshift", r);
    glFilter.setParameterValue("gshift", g);
    glFilter.setParameterValue("bshift", b);    
  }
  
  public void nextMode() {
    mode++;
    if(mode>5) mode = 0;
    glFilter.setParameterValue("swap_mode", mode);
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
    
    GLTexture t = ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_in).getSurf().getTexture();
        
    t.filter(glFilter, ((VurfEclipse)APP.getApp()).pr.getCanvas(canvas_out).getSurf().getTexture());
    
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
