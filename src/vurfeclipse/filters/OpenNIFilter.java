package vurfeclipse.filters;

import codeanticode.glgraphics.GLGraphicsOffScreen;
import vurfeclipse.*;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;
import SimpleOpenNI.*;



public class OpenNIFilter extends Filter {

  SimpleOpenNI context;
  float        zoomF =0.3f;
  float        rotX = PApplet.radians(180);  // by default rotate the hole scene 180deg around the x-axis, 
                                     // the data from openni comes upside down
  float        rotY = PApplet.radians(0);
  PShape       pointCloud;
  int          steps = 2;
  
  String depthOutputName;
  
  public OpenNIFilter setDepthOutputCanvasName(String n) {
	  this.depthOutputName = n;
	  return this;
  }
  
  public OpenNIFilter(Scene sc) {
    super(sc);
  }
  
  public boolean initialise() {
    context = new SimpleOpenNI(APP.getApp());
    if(context.isInit() == false)
    {
       System.out.println("Can't init SimpleOpenNI, maybe the camera is not connected!"); 
       ((VurfEclipse)APP.getApp()).exit();
       return false;  
    }
    
    // disable mirror
    context.setMirror(false);
  
    // enable depthMap generation 
    context.enableDepth();
  
    context.enableRGB();
    
    //context.
   
    // align depth data to image data
    context.alternativeViewPointDepthToImage();
    context.setDepthColorSyncEnabled(true);
      
      
    return true;
  }
  
  int mode = 0;
  public void nextMode() {
    mode++;
    if (mode>3) mode = 0;
    
    //enableForMode(mode);
  } 
  
  /*void initPeasyCam(){
    cam = new PeasyCam(APP, 0, 0, 0, 600);
    cam.setMinimumDistance(1);
    cam.setMaximumDistance(100000);
    cam.setDistance(400);
    cam.setRotations(0,0,0);
  }*/  
  
  //PImage video_frame_ = new PImage(640,480);
  Canvas scaled = Canvas.makeCanvas(640,480,((VurfEclipse)APP.getApp()).pr.gfx_mode,"/dlibcanvas");
  
  PImage rgb;
  PImage depth;
  
  PImage t = scaled.getSurf().get();
  public boolean applyMeatToBuffers() { 
    //drawPointCloud();
    context.update();
    
    PImage newRgb;
    newRgb = context.rgbImage().get();
    if (out!=null && rgb!=newRgb && sc!=null) { // && (rgb==null || newRgb.isModified())) {
      
      t = rgb = context.rgbImage();
      rgb.setModified(false);
     
      //t = rgb.get();//,0,0); //,sc.w,sc.h);
    
      //out.image(context.rgbImage(),0,0);
     
      
      //out.image(t,0,0,sc.w,sc.h);//,(float)0,(float)0,(float)sc.w,(float)sc.h);
      // by jove!
      //out.copy(rgb, 0, 0, out.width, out.height, 0, 0, rgb.width, rgb.height);
      out.copy(rgb, 0, 0, rgb.width, rgb.height, 0, 0, out.width, out.height);
      out.fill(255); out.rect(sc.w/2,sc.h/2,40,40);
      newRgb.delete();
      
      //out.background(t);
      //out.image(context.rgbImage(),0,0);
    }
    
    
    PImage newDepth;
    newDepth = context.depthImage().get();
    GLGraphicsOffScreen out_depth = sc.getCanvas(depthOutputName).getSurf();
    if (out_depth!=null && newDepth!=depth && sc!=null) {
    	t = depth = newDepth;
    	
    	//out_depth.image(t,0,0,sc.w,sc.h);
        out_depth.copy(t, 0, 0, t.width, t.height, 0, 0, out.width, out.height);
    	newDepth.delete();
    }
    
    return true;
  }
  
  public void drawPointCloud() {
    
    
  }
  
}

