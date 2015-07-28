package vurfeclipse;

import java.io.Serializable;

import processing.core.PVector;

import codeanticode.glgraphics.GLGraphicsOffScreen;

public class Canvas implements Serializable /*implements Pathable*/ {
	

	
	static public GLGraphicsOffScreen createGLBuffer(int width, int height, String mode) {
	  //GLGraphicsOffScreen s = getStaticGLBuff(width,height); //
	  GLGraphicsOffScreen s = new GLGraphicsOffScreen(APP.getApp(), width, height);//, true, 4);
	  s.setDepthMask(true);
	  s.background(0,0,0,0);
	  //s.beginDraw(); s.endDraw();
	  //System.out.println("create gl buffer");
	  return s;
	}
	
	
	
  int w,h;
  String gfx_mode;
  transient protected GLGraphicsOffScreen arsesurf;
  
  public String canvasName = "Unnamed Canvas";
  
  public GLGraphicsOffScreen getSurf() {
    if (null==arsesurf) {
      arsesurf = createGLBuffer(w,h,gfx_mode);
    }
    return arsesurf;
  }
  
  public Object getObjectForPath(String path) {
    // there are no children of Canvas so can safely return null
    return this; 
  }
  
  /*Canvas(GLGraphicsOffScreen surf) {
    this.surf = surf;
  }*/
  Canvas() {
    //this.arsesurf =  createGLBuffer(w,h,gfx_mode);
  }
  
  Canvas(int w, int h, String gfx_mode) {
    this();
    this.w = w;
    this.h = h;
    this.gfx_mode = gfx_mode;
  }
  Canvas(int w, int h, String gfx_mode, String canvasName) {
    this(w,h,gfx_mode);
    this.canvasName = canvasName;
  }
  public String toString () {
    return canvasName;
  }
/*  Canvas() {
    this.surf = createGLBuffer(w,h,gfx_mode);
  }*/
  
  public static Canvas makeCanvas(int w, int h, String gfx_mode, String canvasName) {
    return new Canvas(w,h,gfx_mode,canvasName);
  }

  PVector size;
  public PVector getSize() {
	  if (size!=null) size = new PVector(this.w,this.h);
	  return size;
  }
    
  
  
}
