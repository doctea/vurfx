package vurfeclipse.filters;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.*;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;
import SimpleOpenNI.*;



public class OpenNIFilter extends Filter {

  transient SimpleOpenNI context;
  float        zoomF = 0.3f;
  float        rotX = PApplet.radians(180);  // by default rotate the hole scene 180deg around the x-axis,
                                     // the data from openni comes upside down
  float        rotY = PApplet.radians(0);
  PShape       pointCloud;
  int          steps = 2;
  private int cameraId = 0;

/*  String depthOutputName;
  String irOutputName;

  public OpenNIFilter setDepthIROutputCanvasName(String n, String o) {
	  this.depthOutputName = n;
	  this.irOutputName = o;
	  return this;
  }*/

  public OpenNIFilter(Scene sc) {
    super(sc);
  }

  public OpenNIFilter(Scene sc, int cameraId) {
  	super(sc);
		// TODO Auto-generated constructor stub
  	this.cameraId = cameraId;
	}

	public boolean initialise() {

  	println("Initialising OpenNIFilter...");
		//if (true) return false;
		if (!initKinect()) {
			println("Initialisation failed!");
			return false;
		}
		println("Initialisation succeeded - starting reader thread...");
		rt = new ReaderThread();
		rt.start();
		println("Started reader thread...!");

    return true;
  }

  class ReaderThread extends Thread {
  	long lastDepthTimeStamp = -1;
	  public void run() {
		  while (!isMuted()) {
			  if (context==null) initKinect();
			  try {
				  context.update();
				  //System.out.println("OpenNIFilter ReaderThread loop...");

				  if ((Boolean)getParameterValue("rgb")==true) {
		  		      //newRgb = context.rgbImage().get();
		  		      newRgb = t = rgb = context.rgbImage();
		  		      //rgb.setModified(false);

		  		      newFrame = true;
				  }


				  if (lastDepthTimeStamp <= context.depthImageTimeStamp()) {
				  		//lastDepthTimeStamp = context.depthImageTimeStamp();

	  		      if ((Boolean)getParameterValue("depth")==true) {
	  		    	  newDepth = depth = context.depthImage().get();

	    		      newFrame = true;
	  		      }
				  }

				  Thread.sleep((int)1000/30);
			  } catch (Exception e) {};
		  }
	  }
  }

  public boolean initKinect() {

	    //context = new SimpleOpenNI(APP.getApp(), SimpleOpenNI.RUN_MODE_MULTI_THREADED);
	  	//context = new SimpleOpenNI(this.cameraId, APP.getApp());
	  	context = new SimpleOpenNI(this.cameraId, APP.getApp(), SimpleOpenNI.RUN_MODE_MULTI_THREADED);
	  	//context = new SimpleOpenNI();
	    if(context.isInit() == false)
	    {
	       System.out.println("Can't init SimpleOpenNI for cameraId " + this.cameraId + ", maybe the camera is not connected - used to exit early here!!");
	       //((VurfEclipse)APP.getApp()).exit();
	       return false;
	    }

	    // disable mirror
	    context.setMirror(false);

	    // enable depthMap generation
	    context.enableDepth(); //320,240,10);
	    //context.enableRGB(320,240,10);
	    context.enableRGB(); //320,240,10);
	    //context.enableIR();
   
	    // align depth data to image data
	    
	    context.alternativeViewPointDepthToImage();
	    context.setDepthColorSyncEnabled(true);

	    return context.isInit();
  }

  int mode = 0;
  public Filter nextMode() {
    /*mode++;
    if (mode>3) mode = 0;*/
  	
  	//context.close();
  	this.initKinect();

    //enableForMode(mode);
    return this;
  }

  public void setParameterDefaults () {
	    //this.setParameterValue("radius", 10.0);
	    //this.setParameterValue("rotation", 0.0);
	    super.setParameterDefaults();
	    this.addParameter("rgb", new Boolean(true));//, 1.0f, 5.0f);
	    this.addParameter("depth", new Boolean(true)); //(0), -sc.w/2, sc.w/2);
	    this.addParameter("pointCloud", new Boolean(false)); //(0), -sc.w/2, sc.w/2);
	    
	    this.addParameter("pc_rot_x", new Integer(0), new Integer(0), new Integer(360));
	    this.addParameter("pc_rot_y", new Integer(0), new Integer(0), new Integer(360));
	    this.addParameter("pc_rot_z", new Integer(0), new Integer(0), new Integer(360));
	    this.addParameter("pc_zoom", new Integer(-1600), new Integer(-10000), new Integer(10000));
	    
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
  transient Canvas scaled = Canvas.makeCanvas(640,480,((VurfEclipse)APP.getApp()).pr.gfx_mode,"/dlibcanvas");

  PImage rgb;
  PImage depth;

  transient PImage t = scaled.getSurf().get();

  boolean newFrame = false;

  transient ReaderThread rt;// = new ReaderThread();

  transient PImage newRgb;
  transient PImage newDepth;
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

    if ((Boolean)this.getParameterValue("pointCloud")==true) drawPointCloud();

    //if ((Boolean)this.getParameterValue("ir")==true) drawIR();
    return true;
  }

  public void drawRGB() {
	    if (newFrame==true && out()!=null && rgb!=null && /*rgb!=newRgb &&*/ sc!=null) { // && (rgb==null || newRgb.isModified())) {
	        newFrame = false;

			    //GLGraphicsOffScreen out = sc.getCanvas("rgb").getSurf();
	        
	        //t = rgb.get();//,0,0); //,sc.w,sc.h);

	        //out.image(context.rgbImage(),0,0);
			    //GLGraphicsOffScreen out_depth = sc.getCanvas("depth").getSurf();


	        //out.image(t,0,0,sc.w,sc.h);//,(float)0,(float)0,(float)sc.w,(float)sc.h);
	        // by jove!
	        //out.copy(rgb, 0, 0, out.width, out.height, 0, 0, rgb.width, rgb.height);
	        out().copy(rgb, 0, 0, rgb.width, rgb.height, 0, 0, out().width, out().height);
	        out().fill(255);
	        //out.rect(sc.w/2,sc.h/2,40,40);
	        //newRgb.delete();

	        //out.background(t);
	        //out.image(context.rgbImage(),0,0);
	      }
  }


  public void drawDepth() {
	    if (context!=null) {
		    //newDepth = context.depthImage().get();
		    PGraphics out_depth = sc.getCanvas("depth").getSurf();
		    
		    //println("depth is " + sc.getCanvas("/depth").toString());

		    if (out_depth!=null && newDepth!=null && /*newDepth!=depth &&*/ sc!=null) {
		        //out_depth.fill(255);
		        //out_depth.rect(100,100,40,40);

		    	//PImage t = depth = newDepth;
		    	//out_depth.image(t,0,0,sc.w,sc.h);
		      out_depth.copy(newDepth, 0, 0, newDepth.width, newDepth.height, 0, 0, out_depth.width, out_depth.height);
		      //out_depth.rect(40, 40, 20, 20);
		      //out_depth.textSize(100);
		      //out.textFont(getFont(), 11); //256);
		      //out_depth.text("DEPTH", 200, 200);
		      
		      //out.textSize(100);
		      //out.text("OUT", 200, 200);
		      //out.rect(20, 20, 20, 20);;
		    	//out_depth.copy(depth, 0, 0, depth.width, depth.height, 0, 0, out_depth.width, out_depth.height);
		    	//newDepth.delete();
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
	  PGraphics out = out();
	  out.perspective(APP.getApp().radians(45),
              (float)out.width/(float)out.height,
              10,15000);

	  //System.out.println(this+"#drawPointCloud()");
	  // update the cam    
	  //context.update();

	  //GLGraphicsOffScreen out = sc.getCanvas("pointCloud").getSurf();

	  out.pushMatrix();
	  out.background(random(255),0,0);
	  out.fill(random(255));
	  //out.stroke(random(255));
	  //out.rect(0,out.width/2,0,out.height/2);

	  //if (true) return;

	  out.translate(out.width/2, out.height/2, 0);
	  out.rotateX(rotX);
	  out.rotateY(rotY);
	  //out.scale(zoomF);

	  PImage  rgbImage = context.rgbImage();
	  //rgbImage.loadPixels();
	  int[]   depthMap = context.depthMap();
	  int     steps   = 8;  // to speed up the drawing, draw every third point
	  int     index;
	  PVector realWorldPoint;
	  int pixelColor;

	  out.strokeWeight((float)steps/2);

	  /*out.rotateX((float) Math.toRadians((Integer)this.getParameterValue("pc_rot_x")));
	  out.rotateY((float) Math.toRadians((Integer)this.getParameterValue("pc_rot_y")));
	  out.rotateZ((float) Math.toRadians((Integer)this.getParameterValue("pc_rot_z")));*/
	  
	  out.translate(0,0,(Integer)this.getParameterValue("pc_zoom"));  // set the rotation center of the scene 1000 infront of the camera

	  PVector[] realWorldMap = context.depthMapRealWorld();
	  out.beginShape(processing.core.PConstants.POINTS);
	  for(int y=0;y < context.depthHeight();y+=steps)
	  {
	    for(int x=0;x < context.depthWidth();x+=steps)
	    {
	      index = x + y * context.depthWidth();
	      if(depthMap[index] > 0)
	      {
	        // get the color of the point
	        pixelColor = rgbImage.pixels[index];
	        out.stroke(pixelColor);
	        //out.stroke(255);//haxxx

	        // draw the projected point
	        realWorldPoint = realWorldMap[index];
	        //realWorldPoint.rotate((float) Math.toRadians((Integer)this.getParameterValue("pc_rot_x")), (float) Math.toRadians((Integer)this.getParameterValue("pc_rot_y")), (float) Math.toRadians((Integer)this.getParameterValue("pc_rot_z")));
	        out.vertex(
	        		(float) (realWorldPoint.x * Math.sin(Math.toRadians((Integer)this.getParameterValue("pc_rot_x")))),
	        		(float) (realWorldPoint.y * Math.sin(Math.toRadians((Integer)this.getParameterValue("pc_rot_y")))),
	        		(float) (realWorldPoint.z * Math.sin(Math.toRadians((Integer)this.getParameterValue("pc_rot_z"))))
	        		
	        		/*(float) (realWorldPoint.x * (Integer)this.getParameterValue("pc_rot_x")),
	        		(float) (realWorldPoint.y * (Integer)this.getParameterValue("pc_rot_y")),
	        		(float) (realWorldPoint.z * (Integer)this.getParameterValue("pc_rot_z"))*/
	        );  // make realworld z negative, in the 3d drawing coordsystem +z points in the direction of the eye
	        	        
	        System.out.println("drawing one at " + realWorldPoint);
	      } else {
	    	  //System.out.println("Skipped 'cos depth is >0");
	      }
	    }
	  }
	  out.endShape();

	  out.popMatrix();

	  //out.rect(out.width/2,out.height/2,20,20);

	  // draw the kinect cam
	  //out.strokeWeight(1);
	  //context.drawCamFrustum();


  }

}

