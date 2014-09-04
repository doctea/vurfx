package vurfeclipse.filters;

import vurfeclipse.scenes.Scene;

public class BlankFilter extends Filter {
  
  int bgcolour = 0;
  
  public BlankFilter(Scene sc) {
    super(sc);
  }
  
  
  public boolean initialise() {
    // set up inital variables or whatevs 
    return true;
  }
  
  public boolean applyMeatToBuffers() {
    //System.out.println("in applymeattobuffers in pointdrawer (" + this + "), src is " + src);
    
    // just copy src to out
    //out.image(src,0,0);
    //out.pixels = src.pixels;
    out.background(bgcolour);
    
    return true;
  }
  
}
