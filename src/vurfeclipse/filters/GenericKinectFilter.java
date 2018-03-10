package vurfeclipse.filters;

import processing.core.PGraphics;
import vurfeclipse.scenes.Scene;

public abstract class GenericKinectFilter extends Filter {

	int cameraNumber = 0;
	//Kinect kinect;
	
	public GenericKinectFilter(Scene sc) {
		super(sc);
	}
	public GenericKinectFilter(Scene sc, int cameraNumber) {
		this(sc);
		this.cameraNumber = cameraNumber;
	}
	
	@Override
	  public void setParameterDefaults () {
		    super.setParameterDefaults();
		    this.addParameter("rgb", new Boolean(true));//, 1.0f, 5.0f);
		    this.addParameter("depth", new Boolean(true)); //(0), -sc.w/2, sc.w/2);
		  }
	
	@Override
	public boolean applyMeatToBuffers() {
		// TODO Auto-generated method stub
		
		  //v1 getDepthImage
		  //kinect.activateDevice(this.device);
		  //out.image(kinect.getDepthImage(), 0, 424, 512, 424);
		  //out.image(kinect.getVideoImage(), 512*2, 424, 512, 424);
		  if ((Boolean)this.getParameterValue("rgb")==true) {
			  this.drawVideoImage(out(), 0, 0, sc.w, sc.h);
		  }

		  if ((Boolean)this.getParameterValue("depth")==true) {
			  this.drawDepthImage(sc.getCanvas("depth").getSurf(), 0, 0, sc.w, sc.h);
		  }
		
		return false;
	}
	
	@Override
	public boolean initialise () {
		  return super.initialise();
	}
	
	abstract public void drawVideoImage(PGraphics out, int x, int y, int w, int h); /*{
		out.beginDraw();
		out.image(kinect.getVideoImage(), x, y, w, h);
		out.endDraw();
	}*/
	abstract public void drawDepthImage(PGraphics out_depth, int x, int y, int width, int height); /*{
		 //kinect.initDepth();
	    //PGraphics out_depth = sc.getCanvas("depth").getSurf();
	    out_depth.beginDraw();
	    out_depth.image(kinect.getDepthImage(), 0, 0, width, height);  //drawDepth();
	    //out_depth.image(kinect.getVideoImage(), 0, 0, sc.w, sc.h);  //drawDepth();
	    //out_depth.rect(random(255), random(255), random(255), random(255)); //debug
	    out_depth.endDraw();
	}*/

}