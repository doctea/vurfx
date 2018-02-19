package vurfeclipse.filters;

import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
//import codeanticode.gsvideo.*;
import processing.core.PGraphics;
import processing.video.*;


/*void captureEvent(GSCapture cam) {
  cam.read();
}*/

public class WebcamFilter extends Filter {

	boolean pixelMode = true; // false mode doesnt work?

	//int capW, capH;

	int cameraNumber = 0;

	transient Capture webcamStream;

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
	public WebcamFilter(Scene sc) {
		this(sc, sc.w, sc.h);
	}
	public WebcamFilter(Scene sc, int cameraNumber) {
		this(sc);
		this.cameraNumber = cameraNumber;
	}

	int capW = 640, capH = 480;

	public boolean initialise() {
		  super.initialise();

		webcamStream = new Capture(APP.getApp(), capW, capH); //, cameraName); //, global_fps);
		//webcamStream = new GSCapture(APP, 640, 480, cameraName); //, global_fps);      
		//webcamStream = new GSCapture(APP, capW, capH, global_fps);

		System.out.println("WebcamFilter initialised for cameraNumber: " + this.cameraNumber);
		System.out.println("WebcamFilter OUT is " + out());

		webcamStream.start();
		System.out.println("webcam initialise?");
		//APP.exit();
		return true;
	}	  

	public boolean applyMeatToBuffers() {
		//System.out.println("applyMeatToBuffers()");
		if (webcamStream!=null && webcamStream.available()) {
			//System.out.println("got webcamstream");
			if (webcamStream.available() == true) {
				webcamStream.read();
				out().beginDraw();
				//out.scale(1.0f); //-1,-1);
				out().pushMatrix();
				//out.scale(-1.0f, 1.0f);
				//out.image(webcamStream,0,0,sc.w,sc.h);
				out().set(0, 0,  webcamStream);
				out().popMatrix();
				out().endDraw();
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
