package vurfeclipse.filters;


import processing.core.PApplet;
import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class BlendDrawer extends Filter {
  
  int rotation = 0;
  
  int x = 0, y = 0;
  int w, h;
  
  int currentBlendMode = 4;
  
  String blendModes[] = {
    "BlendColor.xml",
    "BlendLuminance.xml",
    "BlendMultiply.xml",
    "BlendSubtract.xml",
    "BlendAdd.xml",
    "BlendColorDodge.xml",
    "BlendColorBurn.xml",
    "BlendDarken.xml",
    "BlendLighten.xml",
    "BlendDifference.xml",
    "BlendInverseDifference.xml",
    "BlendExclusion.xml",
    "BlendOverlay.xml",
    "BlendScreen.xml",
    //"BlendHardLight.xml",
    "BlendSoftLight.xml",
    "BlendUnmultiplied.xml",
    "BlendPremultiplied.xml"
  };
  
  transient GLTextureFilter blendFilters[] = new GLTextureFilter[blendModes.length];
  
  transient GLTextureFilter glFilter;
  transient GLTexture t;
  
  
  public BlendDrawer(Scene sc) {
    super(sc);
    this.w = sc.w;
    this.h = sc.h;
  }
  BlendDrawer(Scene sc, int x, int y, int w, int h) {
    this(sc);
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  BlendDrawer(Scene sc, int rotation) {
    this(sc);
    this.rotation = rotation;
  }
  public BlendDrawer(Scene sc, int x, int y, int w, int h, int rotation) {
    this(sc, x, y, w, h);
    this.rotation = rotation;
  }


  public void setParameterDefaults () {
    super.setParameterDefaults();
    //println("setting defaults");
    //this.changeParameterValue("Opacity", 1.0);
    //this.changeParameterValue("BlendMode", 4);
    addParameter("Opacity", 1.0f, 0.0f, 1.0f);
    addParameter("BlendMode", 4, 0, blendModes.length);
    addParameter("Zoom", new Float(1.0f), -2.0f, 2.0f);
    addParameter("X", new Float(0.0f), -2.0f, 2.0f);
    addParameter("Y", new Float(0.0f), -2.0f, 2.0f);
  }
  
  public void updateParameterValue(String paramName, Object value) {
    if (paramName.equals("BlendMode")) 
      this.setBlendMode((Integer)value); 
    else
      super.updateParameterValue(paramName, value);
  }
  
  /*public void setXYOffset(int x, int y) {
    this.offsetx = x;
    this.offsety = y;
  }*/
  
  
  public void setBlendMode(int n) {
    this.currentBlendMode = n;
    //changeParameterValue("BlendMode", n);
  }
  
  public GLTextureFilter getFilterNumber(int n) {
    if (this.blendFilters==null) blendFilters = new GLTextureFilter[blendModes.length];
    if (this.blendFilters[n]==null) {
      println("BlendDrawer#getFilterNumber(n) initialising GLTextureFilter");
      this.blendFilters[n] = new GLTextureFilter(APP.getApp(),blendModes[n]);
    }
    return this.blendFilters[n];
  }
  
  public boolean initialise() {
    super.initialise();
    // set up inital variables or whatevs 
    
    //this.glFilter = new GLTextureFilter(APP,blendModes[currentBlendMode]);//"BlendColor.xml");
    this.t = new GLTexture(APP.getApp(),sc.w,sc.h);
    
    for (int i = 0 ; i < blendModes.length ; i++) {
      getFilterNumber(i);
    }
    
    return true;
  }
  
  public boolean applyMeatToBuffers() {
    //println("in applymeattobuffers in pointdrawer (" + this + "), src is " + src);
    
    // image draw mode
    //out.getTexture().blend();
    //out.setBlendMode(REPLACE);
    //tint(128);
    //out.image(src.getTexture(),x,y,w,h);
    if (t!=null)
      t.clear(0);
    out.pushMatrix();

    out.translate((Float)getParameterValue("X"), (Float)getParameterValue("Y"));
    if (rotation!=0) {
        out.rotate(PApplet.radians(rotation));
      }    
        
    if ((Float)getParameterValue("Zoom")!=1.0f) {
    	out.scale((Float)getParameterValue("Zoom"));
    }
    
    GLTextureFilter tf = getFilterNumber(currentBlendMode);
    tf.setParameterValue("Opacity", new Float((double)(Float)this.getParameterValue("Opacity"))); 
    tf.apply(new GLTexture[]{src.getTexture(), out.getTexture()}, t); // all are called the same way
    
    out.image(t,x,y,w,h);
    
    //if (rotation!=0) {
      out.popMatrix();
    //}    

    
    // pixel copy mode
      //arrayCopy(src.pixels, out.pixels);
      
    return true;
  }
  
  /*public String getFilterLabel() {
    return super.getFilterLabel() + " [" + currentBlendMode + "]:" + blendModes[currentBlendMode];
  }*/
  /*public String toString() {
    return super.toString() + " " + blendModes[currentBlendMode];
  }*/
  
  public void nextMode () {
    currentBlendMode++;
    if(this.currentBlendMode>=blendModes.length)
      currentBlendMode = 0;
      
    changeParameterValue("BlendMode", currentBlendMode);
      
    //println("Switched to currentBlendMode " + currentBlendMode + " " + blendModes[currentBlendMode]);
  }
  
  public void beginDraw() {
    //src.loadPixels();
    //out.loadPixels();
    if (out==null) setOutputCanvas(canvas_out);
    if (src==null) setInputCanvas(canvas_in);
    if (t==null) this.t = new GLTexture(APP.getApp(),sc.w,sc.h);
    out.beginDraw();
  }
  
  public void endDraw() {
    //out.updatePixels();
    out.endDraw();
  }
  
}
