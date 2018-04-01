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
	
	int mode = 0;
	int maxModes = Capture.list().length;

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
	
	  public void setParameterDefaults () {
		    super.setParameterDefaults();
		    //println("setting defaults");
		    //this.changeParameterValue("Opacity", 1.0);
		    //this.changeParameterValue("BlendMode", 4);
		    addParameter("camera_number", new Integer(0), 0, Capture.list().length);
	  }

	public boolean initialise() {
		super.initialise();
		
		this.initialiseWebcam();
		//APP.exit();
		return true;
	}	  
	
	@Override
	public Filter nextMode() {
		int orig = cameraNumber;
		cameraNumber++;
		if (cameraNumber>maxModes) cameraNumber = 0;
		if (cameraNumber!=orig) {
			this.changeParameterValue("camera_number", cameraNumber);
			//this.initialiseWebcam();
		}
		return this;
	}

	private void initialiseWebcam() {
		if (this.webcamStream!=null) this.webcamStream.stop();
		this.webcamStream = null;

		if (this.cameraNumber>=Capture.list().length) 
			return;		

		webcamStream = new Capture(APP.getApp(), capW, capH, Capture.list()[this.cameraNumber]); //, this.cameraNumber); //, cameraName); //, global_fps);
		//webcamStream = new GSCapture(APP, 640, 480, cameraName); //, global_fps);      
		//webcamStream = new GSCapture(APP, capW, capH, global_fps);

		System.out.println("WebcamFilter initialised for cameraNumber: " + this.cameraNumber);
		System.out.println("WebcamFilter OUT is " + out());

		webcamStream.start(); //TODO: why doesn't this compile on macosx..?
		//webcamStream.run();
		System.out.println("webcam initialise?");		
	}
	synchronized public void updateParameterValue(String paramname, Object value) {
		if (paramname.equals("camera_number")) {
			if (this.cameraNumber!=(int)value) {
				this.cameraNumber = (int)value;
				webcamStream = null;
				this.initialiseWebcam();
			}
		}
	}
	
	public boolean applyMeatToBuffers() {
		//System.out.println("applyMeatToBuffers()");
		if (webcamStream!=null) { // && webcamStream.available()) {
			//System.out.println("got webcamstream");
			if (webcamStream.available() == true) {
				webcamStream.read();
			}
				out().beginDraw();
				//out.scale(1.0f); //-1,-1);
				out().pushMatrix();
				//out.scale(-1.0f, 1.0f);
				//out().image(webcamStream,0,0,sc.w,sc.h);
				out().set(0, 0,  webcamStream);
				out().popMatrix();
				out().endDraw();
			
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
