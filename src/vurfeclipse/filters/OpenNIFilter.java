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
  float        zoomF = 0.3f;
  float        rotX = PApplet.radians(180);  // by default rotate the hole scene 180deg around the x-axis, 
                                     // the data from openni comes upside down
  float        rotY = PApplet.radians(0);
  PShape       pointCloud;
  int          steps = 2;
  
  String depthOutputName;
  String irOutputName;
  
  public OpenNIFilter setDepthIROutputCanvasName(String n, String o) {
	  this.depthOutputName = n;
	  this.irOutputName = o;
	  return this;
  }
  
  public OpenNIFilter(Scene sc) {
    super(sc);
  }
  
  public boolean initialise() {
	  
	//if (true) return false;
	if (!initKinect()) return false;
    
	rt = new ReaderThread();
	rt.start();    
      
    return true;
  }
  
  class ReaderThread extends Thread {
	  public void run() {
		  while (!isMuted()) {
			  
			  if (context==null) initKinect();
			  try {
			  context.update();
			  //System.out.println("OpenNIFilter ReaderThread loop...");
			  
  		      newRgb = context.rgbImage().get();
  		      t = rgb = context.rgbImage();
  		      rgb.setModified(false);  		      
			  
  		      newFrame = true;
  		      
				  Thread.sleep(50);
			  } catch (Exception e) {};
		  }
	  }
  }  
  
  public boolean initKinect() {

	    //context = new SimpleOpenNI(APP.getApp(), SimpleOpenNI.RUN_MODE_MULTI_THREADED);
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
	    //context.enableIR();
	    
	    //context.
	   
	    // align depth data to image data
	    context.alternativeViewPointDepthToImage();
	    context.setDepthColorSyncEnabled(true);
	      
	    
	    return context.isInit();
  }
  
  int mode = 0;
  public void nextMode() {
    mode++;
    if (mode>3) mode = 0;
    
    //enableForMode(mode);
  } 
  
  public void setParameterDefaults () {
	    //this.setParameterValue("radius", 10.0);
	    //this.setParameterValue("rotation", 0.0);
	    super.setParameterDefaults();
	    this.addParameter("rgb", new Boolean(true));//, 1.0f, 5.0f);
	    this.addParameter("depth", new Boolean(false)); //(0), -sc.w/2, sc.w/2);
	    //this.addParameter("ir", new Boolean(false)); //(0), -sc.w/2, sc.w/2);
	    //this.addParameter("translate_y", new Integer(0), -sc.h/2, sc.h/2);
	    /*this.addParameter("tint", new Integer(128), 0, 255);//new Integer(128));
	     this.addParameter("shape", new Integer(0), 0, b.shapesCount);
	     this.addParameter("colour", color(random(255),random(255),random(255),128));*/
	    //this.addParameter("radius", 0.5, 0.01, 20.0);
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
  
  boolean newFrame = false;
  
  ReaderThread rt;// = new ReaderThread();
  
  PImage newRgb;
  PImage newDepth;
  //PImage newIR;
  
  public boolean applyMeatToBuffers() {
	  if (context==null) return false;
    //drawPointCloud();
    //context.update();
	/*if (rt==null) {
		rt = new ReaderThread();
		rt.start();
	}*/
    
	if ((Boolean)this.getParameterValue("rgb")==true) drawRGB();
    
    if ((Boolean)this.getParameterValue("depth")==true) drawDepth();
    
    //if ((Boolean)this.getParameterValue("ir")==true) drawIR();
    
    return true;
  }
  
  public void drawRGB() {
	    if (newFrame==true && out!=null && rgb!=newRgb && sc!=null) { // && (rgb==null || newRgb.isModified())) {
	        newFrame = false;
	       
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
  }
  
  
  public void drawDepth() {
	    if (context!=null) {
		    newDepth = context.depthImage().get();
		    GLGraphicsOffScreen out_depth = sc.getCanvas(depthOutputName).getSurf();
		    if (out_depth!=null && newDepth!=depth && sc!=null) {
		    	PImage t = depth = newDepth;
		    	
		    	//out_depth.image(t,0,0,sc.w,sc.h);
		        out_depth.copy(t, 0, 0, t.width, t.height, 0, 0, out.width, out.height);
		    	newDepth.delete();
		    }
	    }	  
  }
  
  /*public void drawIR() {
	    if (context!=null) {
		    newIR = context.irImage().get();
		    GLGraphicsOffScreen out_depth = sc.getCanvas(irOutputName).getSurf();
		    if (out_depth!=null && newIR!=depth && sc!=null) {
		    	PImage t = depth = newIR;
		    	
		    	//out_depth.image(t,0,0,sc.w,sc.h);
		        out_depth.copy(t, 0, 0, t.width, t.height, 0, 0, out.width, out.height);
		    	newIR.delete();
		    }
	    }	  
	}  */
  
  public void drawPointCloud() {
    
    
  }
  
}

