package vurfeclipse.filters;

import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
import codeanticode.gsvideo.*;
import processing.core.PGraphics;


/*void captureEvent(GSCapture cam) {
  cam.read();
}*/

public class WebcamFilter extends Filter {

  boolean pixelMode = true; // false mode doesnt work?
  
  //int capW, capH;
  
  int cameraNumber = 0;

  transient GSCapture webcamStream;
  
  transient PGraphics tex;
  
  WebcamFilter(Scene sc, int capW, int capH, int cameraNumber) {
    this(sc, capW, capH);
    this.cameraNumber = cameraNumber; 
  }
  public WebcamFilter(Scene sc, int capW, int capH) {
    super(sc);
    this.capW = capW;
    this.capH = capH;
  }
  WebcamFilter(Scene sc) {
    this(sc, sc.w, sc.h);
  }
  public WebcamFilter(Scene sc, int cameraNumber) {
    this(sc);
    this.cameraNumber = cameraNumber;
  }
  
  int capW = 640, capH = 480;
  
  public boolean initialise() {
      webcamStream = new GSCapture(APP.getApp(), capW, capH); //, cameraName); //, global_fps);
      //webcamStream = new GSCapture(APP, 640, 480, cameraName); //, global_fps);      
      //webcamStream = new GSCapture(APP, capW, capH, global_fps);
      
      System.out.println("WebcamFilter initialised for cameraNumber: " + this.cameraNumber);
      System.out.println("WebcamFilter OUT is " + out);
      
      if (pixelMode) {
        //tex = new GLTexture(APP.getApp(),sc.w,sc.h);
    	tex = this.sc.host.createCanvas(getPath() + "/ca/buffer",this.getFilterLabel(), capW,capH).getSurf();
        //webcamStream.setPixelDest(tex);
        webcamStream.setPixelDest(tex, true);
        //webcamStream.setPixelDest(out.getTexture());
      }
      webcamStream.start();
      System.out.println("webcam initialise?");
      //APP.exit();
      return true;
  }	  
  
  public boolean disable_initialise() {
    /*try {
      quicktime.QTSession.open();
    } catch (quicktime.QTException qte) { 
      qte.printStackTrace();
    }*/
    
    //webcamStream = new GSCapture(APP, sc.w, sc.h, 30);
    
    String[] cameras = GSCapture.list();
    if (cameraNumber >= cameras.length ) {
      System.out.println("No camera available for " + cameraNumber + "!");
      //APP.exit();
      return false;
    } else {
      //String cameraName = "USB Video Class Video:" + this.cameraNumber;
      String cameraName = cameras[cameraNumber]; //"USB Video Class Video:" + this.cameraNumber;
      
      //webcamStream = new GSCapture(APP, capW, capH, cameraName, global_fps);
      //webcamStream = new GSCapture(APP, capW, capH, cameraName);    
      //webcamStream = new GSCapture(APP, capW, capH);          
      webcamStream = new GSCapture(APP.getApp(), capW, capH); //, cameraName); //, global_fps);
      //webcamStream = new GSCapture(APP, 640, 480, cameraName); //, global_fps);      
      //webcamStream = new GSCapture(APP, capW, capH, global_fps);
      
      System.out.println("WebcamFilter initialised for cameraNumber: " + this.cameraNumber);
      System.out.println("WebcamFilter OUT is " + out);
      
      if (pixelMode) {
        //tex = new GLTexture(APP.getApp(),sc.w,sc.h);
        tex = this.sc.host.createCanvas("/shaderfilter/"+this.getFilterName(), this.getFilterLabel()).getSurf();
        //webcamStream.setPixelDest(tex);
        webcamStream.setPixelDest(tex, true);
        //webcamStream.setPixelDest(out.getTexture());
      }
      webcamStream.start();
      System.out.println("webcam initialise?");
      //APP.exit();
      return true;
    }
  }
  
  public boolean applyMeatToBuffers() {
    //System.out.println("applyMeatToBuffers()");
    if (webcamStream!=null && webcamStream.available()) {
      //System.out.println("got webcamstream");
      webcamStream.read();
      
      if (!pixelMode) {
        webcamStream.loadPixels();
        //out.loadPixels();
        //out.pixels = webcamStream.pixels;
        //arrayCopy(webcamStream.pixels, out.pixels);
        //out.getTexture().putBuffer(webcamStream.pixels);
      } else {
        //if (tex.putPixelsIntoTexture()) {
    	if (tex.isModified()) {
    	  tex.updatePixels();
          out.beginDraw();
          out.image(tex,0,0,sc.w,sc.h);
          out.endDraw();
        }
      }
      return true;
    } else {
      return false;  // didn't get a new frame so return false
    }
  }  
  
  public void beginDraw () {
    /*if (!pixelMode)
      out.loadPixels();
    else*/
      //out.beginDraw();
  }
  public void endDraw () {
    /*if (!pixelMode)
      out.updatePixels();
    else {*/
      //out.endDraw();
      //out.loadPixels();
      //out.loadTexture();
    //}
  }
}
