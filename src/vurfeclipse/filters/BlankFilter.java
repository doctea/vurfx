package vurfeclipse.filters;

import vurfeclipse.scenes.Scene;

public class BlankFilter extends Filter {
  
  int bgcolour = 0;
  int alpha = 255;
  
  public BlankFilter(Scene sc) {
    super(sc);
  }
  
  
  public boolean initialise() {
	super.initialise();
    // set up inital variables or whatevs 
    return true;
  }
  
  public boolean applyMeatToBuffers() {
    println("#applyMeattoBuffers (out is " + src + ")");
    //System.exit(1);
    
    // just copy src to out
    //out.image(src,0,0);
    //out.pixels = src.pixels;
    out.background(bgcolour, alpha);
    return true;
  }
  
}
