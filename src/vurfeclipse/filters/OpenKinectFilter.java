package vurfeclipse.filters;

import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;

import org.openkinect.processing.*;

import processing.core.PGraphics;


public class OpenKinectFilter extends GenericKinectFilter {

	private int device;
	Kinect kinect;

	public OpenKinectFilter(Scene sc) {
		super(sc);
		// TODO Auto-generated constructor stub
	}
	public OpenKinectFilter(Scene sc, int device) {
		super(sc, device);
	}

	/*public OpenKinectFilter(Scene sc, String filterLabel, int device) {
		super(sc, device);
		this.device = device;
		// TODO Auto-generated constructor stub
	}*/
	
	/*@Override
	  public void setParameterDefaults () {
		    //this.setParameterValue("radius", 10.0);
		    //this.setParameterValue("rotation", 0.0);
		    super.setParameterDefaults();
		    this.addParameter("rgb", new Boolean(true));//, 1.0f, 5.0f);
		    this.addParameter("depth", new Boolean(true)); //(0), -sc.w/2, sc.w/2);
		    
		    //this.addParameter("pointCloud", new Boolean(false)); //(0), -sc.w/2, sc.w/2);
		    
		    //this.addParameter("pc_rot_x", new Integer(0), new Integer(0), new Integer(360));
		    //this.addParameter("pc_rot_y", new Integer(0), new Integer(0), new Integer(360));
		    //this.addParameter("pc_rot_z", new Integer(0), new Integer(0), new Integer(360));
		    //this.addParameter("pc_zoom", new Integer(-1600), new Integer(-10000), new Integer(10000));
		    
		    //this.addParameter("ir", new Boolean(false)); //(0), -sc.w/2, sc.w/2);
		    //this.addParameter("translate_y", new Integer(0), -sc.h/2, sc.h/2);
		     
		    //this.addParameter("tint", new Integer(128), 0, 255);//new Integer(128));
		     //this.addParameter("shape", new Integer(0), 0, b.shapesCount);
		     //this.addParameter("colour", color(random(255),random(255),random(255),128));
		    //this.addParameter("radius", 0.5, 0.01, 20.0);
		  }
		  */

	@Override
	public boolean initialise () {
		super.initialise();

		kinect = new Kinect(APP.getApp());
		kinect.activateDevice(this.device);
		kinect.initDepth();
		kinect.initVideo();
		kinect.enableColorDepth(true);

		return true;
	}

	@Override
	public void drawVideoImage(PGraphics out, int x, int y, int w, int h) {
	out.beginDraw();
	out.image(kinect.getVideoImage(), x, y, w, h);
	out.endDraw();
}
	@Override
	public void drawDepthImage(PGraphics out_depth, int x, int y, int width, int height) {
		 //kinect.initDepth();
	    //PGraphics out_depth = sc.getCanvas("depth").getSurf();
	    out_depth.beginDraw();
	    out_depth.image(kinect.getDepthImage(), 0, 0, width, height);  //drawDepth();
	    //out_depth.image(kinect.getVideoImage(), 0, 0, sc.w, sc.h);  //drawDepth();
	    //out_depth.rect(random(255), random(255), random(255), random(255)); //debug
	    out_depth.endDraw();
	}

}
