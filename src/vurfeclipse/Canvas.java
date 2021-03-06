package vurfeclipse;

import java.io.Serializable;

import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.Texture;

public class Canvas implements Serializable /*implements Pathable*/ {
	

	
	static public PGraphics createGLBuffer(int width, int height) {
	  //GLGraphicsOffScreen s = getStaticGLBuff(width,height); //
	  //GLGraphicsOffScreen s = new GLGraphicsOffScreen(APP.getApp(), width, height);//, true, 4);
	  //PGraphics s = new PGraphics();
	  if (true || ((VurfEclipse)APP.getApp()).isReady()) {
		  PGraphicsOpenGL s = (PGraphicsOpenGL) APP.getApp().createGraphics(width,height,APP.getApp().P3D);
		  //s.textureSampling(5); // Texture.TRILINEAR
		  s.textureWrap(s.REPEAT);
		  //s.setDepthMask(true);
		  s.setSize(width, height);
		  //s.pixelDensity = 2;
		  /*s.beginDraw();
	  		s.background(0,0,0,0);
	  		s.endDraw();*/
		  //s.beginDraw(); s.endDraw();
		  //System.out.println("create gl buffer");
		  return s;
	  }
	  return null;
	}
	
	
	
  int w,h;
  String gfx_mode;
  transient protected PGraphics arsesurf;
  
  public String canvasName = "Unnamed Canvas";
  
  public synchronized PGraphics getSurf() {
    if (null==arsesurf) {
      arsesurf = createGLBuffer(w,h);
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
