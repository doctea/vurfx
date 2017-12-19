package vurfeclipse.filters;

import vurfeclipse.scenes.Scene;

public class BlankFilter extends Filter {
  
  int bgcolour = 0;
  int alpha = 255;
  
  public BlankFilter(Scene sc) {
    super(sc);
  }
  public BlankFilter(Scene sc, int w) {
	    super(sc);
  }
	    
  @Override
  public boolean initialise() {
	super.initialise();
    // set up inital variables or whatevs 
    return true;
  }
  
  @Override
  public boolean applyMeatToBuffers() {
    //println("#applyMeattoBuffers (out is " + src + ")");
    //System.exit(1);
    
    // just copy src to out
    //out.image(src,0,0);
    //out.pixels = src.pixels;
    //out.background(bgcolour, alpha);
	out.fill(bgcolour,alpha);
	out.rect(0,0,sc.w,sc.h);
    return true;
  }
  
  
  @Override
  synchronized public void setParameterDefaults () {
    super.setParameterDefaults();
    this.addParameter("alpha", new Integer(255), 0, 255);
    this.addParameter("bgcolour", new Integer(0), 0, 255);//new Integer(128));
  }
  
  @Override
  synchronized public void updateParameterValue(String paramName, Object value) {
	if(paramName.equals("alpha"))
        alpha = (Integer)value;//b.setEdge((Boolean)value); 
    else if(paramName.equals("bgcolour"))
        bgcolour = (Integer)value;
  }  
  
}
