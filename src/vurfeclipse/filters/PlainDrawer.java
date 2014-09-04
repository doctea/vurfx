package vurfeclipse.filters;


import processing.core.PApplet;
import vurfeclipse.scenes.Scene;

public class PlainDrawer extends Filter {
  
  int offsetx=0,offsety=0;
  
  int rotation = 0;
  
  int x = 0, y = 0;
  int w, h;
  
  public PlainDrawer(Scene sc) {
    super(sc);
    this.w = sc.w;
    this.h = sc.h;
  }
  public PlainDrawer(Scene sc, int x, int y, int w, int h) {
    this(sc);
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  public PlainDrawer(Scene sc, int x, int y, int w, int h, int rotation) {
    this(sc, x, y, w, h);
    this.rotation = rotation;
  }
  
  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/
  
  public void setParameterDefaults () {
    super.setParameterDefaults();
    this.addParameter("tint", new Integer(255), new Integer(0), new Integer(255));
    this.addParameter("rotation", new Integer(0), new Integer(0), new Integer(360));
  }
  
  public boolean initialise() {
    // set up inital variables or whatevs 
    return true;
  }
  
  public boolean applyMeatToBuffers() {
    //System.out.println("in applymeattobuffers in pointdrawer (" + this + "), src is " + src);
    
    // image draw mode
    //out.getTexture().blend();
    //out.setBlendMode(REPLACE);
    //tint(128);
    //if (rotation!=0) {
    out.pushMatrix();
    out.tint(255,(Integer)getParameterValue("tint"));    
    if (((Integer)getParameterValue("rotation")!=0)) {
      out.imageMode(PApplet.CENTER);
      out.translate(x+(w/2),y+(h/2));
      out.rotate(PApplet.radians((Integer)getParameterValue("rotation")));
      //out.rect(x,y,20,20);
      out.image(src.getTexture(),x,y,w,h);//,w,h);      
      out.imageMode(PApplet.CORNER);
    } else {
      out.image(src.getTexture(),x,y,w,h);
    }
    //System.out.println(this + " copying from " + src + " to " + out);


    out.noTint();
    out.popMatrix(); 
    
    // pixel copy mode
      //arrayCopy(src.pixels, out.pixels);
      
    return true;
  }
  
  public void beginDraw() {
    //src.loadPixels();
    //out.loadPixels();
    if (out==null) setOutputCanvas(canvas_out);
    if (src==null) setInputCanvas(canvas_in);
    out.beginDraw();
  }
  
  public void endDraw() {
    //out.updatePixels();
    out.endDraw();
  }
  
}
