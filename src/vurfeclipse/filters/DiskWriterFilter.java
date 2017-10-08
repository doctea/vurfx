package vurfeclipse.filters;


import processing.core.PApplet;
import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;
import codeanticode.gsvideo.GSMovieMaker;


public class DiskWriterFilter extends Filter {

  int writeLimit = 2000;
  int writeCount = 0;

  boolean recording = false;

  String base_filename;
  String filename;

  GSMovieMaker mm;

  int quality = GSMovieMaker.HIGH; //(all quality settings are WORST, LOW, MEDIUM, HIGH and BEST):
  int enctype = GSMovieMaker.THEORA;
  //int fps = 30;
  int fps = ((VurfEclipse)APP.getApp()).global_fps;

  int[] pixels;

  /*
    Example/settings at : http://gsvideo.sourceforge.net/examples/MovieMaker/DrawingMovie/DrawingMovie.pde
  */

  String dateStamp () {
    return ((VurfEclipse)APP.getApp()).year() + "-" + ((VurfEclipse)APP.getApp()).month() + "-" + ((VurfEclipse)APP.getApp()).day() + "-" + ((VurfEclipse)APP.getApp()).hour() + "-" + ((VurfEclipse)APP.getApp()).minute() + "-" + ((VurfEclipse)APP.getApp()).second();
  }

  public DiskWriterFilter(Scene sc, String filename) {
    super(sc);
    this.base_filename = filename; //= "output/" + dateStamp() + "_" + filename;
    //this.filename = filename;
    //System.out.println("got output filename '" + this.filename + "'");
  }

  public boolean initialise() {
    pixels = new int[sc.w*sc.h];

    return true;
  }

  public void startNewRecording (String filename) {
    System.out.println("Starting recording to " + filename);
    writeCount = 0;
    // set up inital variables or whatevs
    mm = new GSMovieMaker(APP.getApp(), sc.w, sc.h, filename, this.enctype, this.quality, this.fps);
    //mm = new GSMovieMaker(APP, sc.w, sc.h, filename);

    mm.setQueueSize(50,50);
    mm.start();
    recording = true;
  }

  public void stopRecording() {
    System.out.println("queued frame count: " + mm.getQueuedFrames());
    System.out.println("dropped frame count: " + mm.getDroppedFrames());
    mm.finish();
    mm.delete();
    mm = null;
    System.out.println("Stopped recording.");
    recording = false;
  }

  public Filter nextMode () {
    if (!recording) {
      // start new capture
      startNewRecording("output/" + dateStamp() + "_" + base_filename);
    } else {
      // stop old capture
      stopRecording();
      //mm.finish();
    }

    //recording = !recording;
    //mm.finish();

    //if (recording) mm.start(); else mm.finish();
    return this;
  }

  public boolean applyMeatToBuffers() {
    if (recording && writeCount<writeLimit) {
      //pixels = new int[sc.w*sc.h];

      //System.out.println("pixelBufferUse : " + src.getTexture().getPixelBufferUse());

      //src.getTexture().getBuffer(pixels); //, ARGB);
      src.loadPixels();
      pixels = src.pixels;
      //src.getBuffer(pixels);
      mm.addFrame(pixels);
      //src.getTexture().loadPixels();
      //mm.addFrame(src.getTexture().pixels);

      //System.out.println("Added pixels for frame " + writeCount);
      writeCount++;
      if (writeCount>=writeLimit) {
        System.out.println("Finishing file because limit of " + writeLimit + " reached.");
        mm.finish();
      }
    } else {
      //System.out.println("Not adding frame (writeCount is " + writeCount + ").");
    }

    return true;
  }

  public void beginDraw() {
    //src.loadPixels();
    //out.loadPixels();
    //out.beginDraw();
  }

  public void endDraw() {
    //out.updatePixels();
    //out.endDraw();
  }

  public void dispose() {
    //super ();
    //mm.finish();
    if (recording) stopRecording();
  }

}
