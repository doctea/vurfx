package vurfeclipse.filters;

import vurfeclipse.*;

import dLibs.freenect.toolbox.*;
import dLibs.freenect.constants.*;
import dLibs.freenect.interfaces.*;
import dLibs.freenect.*;
import peasy.*; // peasycam
import processing.core.PGraphics;
import processing.core.PImage;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;


public class dlibKinectFilter extends GenericKinectFilter {

  Kinect kinect_;                      // main kinect-object
  KinectFrameVideo kinect_video_;      // video frame
  KinectFrameDepth kinect_depth_;      // depth frame

  Kinect3D k3d_;                       // 3d content

  KinectCalibration calibration_data_; // kinects calibration data

  // get width/height --> actually its always 640 x 480
  int kinectFrame_size_x = VIDEO_FORMAT._RGB_.getWidth();   // width of kinect frame
  int kinectFrame_size_y = VIDEO_FORMAT._RGB_.getHeight();  // height of kinect frame

  //Kinect kinect;

  //PGraphics depth;
  /*PGraphics depth;

  boolean enable_depth = true;
  boolean enable_rgb = true;

  PImage i_img, i_depth;*/

  public dlibKinectFilter(Scene sc) {
    super(sc);
  }
  public dlibKinectFilter(Scene sc, int cameraNumber) {
	  super(sc, cameraNumber);
  }

PeasyCam cam;


  public boolean initialise() {
    kinect_ = new Kinect(cameraNumber);  //create a main kinect instance with index 0

    kinect_video_ = new KinectFrameVideo(VIDEO_FORMAT._RGB_);    // create a video instance
    kinect_depth_ = new KinectFrameDepth(DEPTH_FORMAT._11BIT_);  // create a depth instance

    k3d_ = new Kinect3D(); // generate a 3d instance
    k3d_.setFrameRate(30); // set framerate

    kinect_video_.processPixels(true);
    kinect_depth_.processPixels(true);

    kinect_video_.connect(kinect_);  //connect the created video instance to the main kinect
    kinect_depth_.connect(kinect_);  //connect the created depth instance to the main kinect
    k3d_.connect(kinect_);


    calibration_data_ = new KinectCalibration();
    // second parameter can be null, if the calibration file is in the folder "/library/calibration/"
    // to generate a calibration file, have a look at the README inside this folder
    calibration_data_.fromFile("kinect_calibration_red.yml", null);
    k3d_.setCalibration(calibration_data_);

    //initPeasyCam();


    return true;
  }

  int mode = 0;
  public Filter nextMode() {
    mode++;
    if (mode>3) mode = 0;

    //enableForMode(mode);
    return this;
  }

  /*void initPeasyCam(){
    cam = new PeasyCam(APP, 0, 0, 0, 600);
    cam.setMinimumDistance(1);
    cam.setMaximumDistance(100000);
    cam.setDistance(400);
    cam.setRotations(0,0,0);
  }*/

  PImage video_frame_ = new PImage(640,480);
  Canvas scaled = Canvas.makeCanvas(640,480,((VurfEclipse)APP.getApp()).pr.gfx_mode,"/dlibcanvas");

  void assignPixels(PImage img, Pixelable kinect_dev){
    img.loadPixels();
      img.pixels = kinect_dev.getPixels();  // assign pixels of the kinect device to the image
      //img.pixels[(int)APP.random(100)] = (int)APP.random(255);
    img.updatePixels();
  }

  public boolean applyMeatToBuffers() {
    drawPointCloud();

    /*assignPixels(video_frame_, kinect_video_);
    //assignPixels(scaled.getSurf(), kinect_video_);

    scaled.getSurf().background(video_frame_);
    out.image(video_frame_,0,0,sc.w,sc.h);*/
    //out.image(scaled.getSurf(),0,0,sc.w,sc.h);

/*    //video_frame_.(APP.random(255));
    video_frame_.loadPixels();
    video_frame_.pixels[(int)APP.random(10)] = (int)APP.random(255); //point(random(10),10));
    video_frame_.updatePixels();
    out.image(video_frame_,0,0,sc.w,sc.h);//, 0, 0);*/

    return true;
  }


  //-------------------------------------------------------------------
  void drawPointCloud(){
    // get the kinects 3d-data (by reference)
    KinectPoint3D kinect_3d[] = k3d_.get3D();

    int jump = 2; // resolution, ... use every fifth point in 3d

    int cam_w_ = kinectFrame_size_x;
    int cam_h_ = kinectFrame_size_y;

    out().background(0);

    out().strokeWeight(3);

    out().stroke((int)((VurfEclipse)APP.getApp()).random(255));
    //out.rect((int)APP.random(100),(int)APP.random(100),50,50);


    for(int y = 0; y < cam_h_-jump ; y+=jump){
      for(int x = 0; x< cam_w_-jump*2 ; x+=jump){
        int index1 = y*cam_w_+x;

        if (kinect_3d[index1].getColor() == 0 )
          continue;

        //out.rect((int)APP.random(100),(int)APP.random(100),50,50);

        // do some color mapping, we need a proper calibration file to get good results
        out().stroke(kinect_3d[index1].getColor() ); //get color from video frame

        float cx = kinect_3d[index1].x;
        float cy = kinect_3d[index1].y;
        float cz = kinect_3d[index1].z;
        out().pushMatrix();
        out().translate(sc.w/2+cx*sc.w/2,sc.h/2+cy*sc.h/2,cz);
        out().point(0, 0, 0);
        //out.rect(0,0,5,5);
        out().popMatrix();
        //out.point(cx, cy, cz*100);
        //out.rect(cx,cy,5,5);

      }
    }
  }

	@Override
	public void drawVideoImage(PGraphics out, int x, int y, int w, int h) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void drawDepthImage(PGraphics out_depth, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}

}

