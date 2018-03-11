package vurfeclipse.projects;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.*;
import vurfeclipse.scenes.*;

import java.io.Serializable;

import vurfeclipse.filters.OpenNIFilter;
import vurfeclipse.scenes.DebugScene;
import vurfeclipse.scenes.SimpleScene;
import vurfeclipse.scenes.WebcamScene;
import vurfeclipse.streams.*;

public class SimpleProject extends Project implements Serializable {

  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");

  boolean enableSequencer = true;

protected SimpleProject(int w, int h, String gfx_mode) {
    super(w,h);
  }

  public boolean initialiseBuffers() {
    addCanvas("/out", Canvas.makeCanvas(w,h,gfx_mode,"out"));
    addCanvas("/inp0", Canvas.makeCanvas(w,h,gfx_mode,"inp0"));
    addCanvas("/inp1", Canvas.makeCanvas(w,h,gfx_mode,"inp1"));
    addCanvas("/temp1", Canvas.makeCanvas(w,h,gfx_mode,"temp1"));
    addCanvas("/temp2", Canvas.makeCanvas(w,h,gfx_mode,"temp2"));
/*

    buffers[BUF_OUT] = createGLBuffer(w,h,gfx_mode);
    buffers[BUF_INP0] = createGLBuffer(w,h,gfx_mode);
    buffers[BUF_INP1] = createGLBuffer(w,h,gfx_mode);
    buffers[BUF_TEMP1] = createGLBuffer(w,h,gfx_mode);
    buffers[BUF_TEMP2] = createGLBuffer(w,h,gfx_mode);
    System.out.println(this + " BUF_OUT buffer is " + buffers[BUF_OUT]);
    System.out.println(this + " BUF_INP0 buffer is " + buffers[BUF_INP0]);*/
    return true;
  }

  public boolean setupStreams () {
    Stream stream = new Stream("Test Stream");
    BeatStream beatStream = new BeatStream("Beat Stream", 130.0, ((VurfEclipse)APP.getApp()).millis());
    this.getSequencer().addStream("test", stream);
    this.getSequencer().addStream("beat", beatStream);

    NumberStream numberStream = new NumberStream("Number Stream", (float) 130.0, 69, ((VurfEclipse)APP.getApp()).millis());
    this.getSequencer().addStream("number", numberStream);

    return true;
  }

  public boolean setupScenes () {
    // need a way to specify which scenes are running (selected only at first?)
    // need a way to blend between scenes ...
    // need a way for scenes to write to a shared buffer ..
      // set output buffer on Webcam Scene to a Project buffer
      // set input buffer on following Scenes to that buffer

    //this.addScene(new WebcamScene(this,w,h,0));

    /*this.addSceneOutputCanvas(
      new WebcamScene(this,w,h,0),
      //buffers[BUF_INP0]
      //"/inp0"
      "/out"
    );
    this.addSceneOutputCanvas(
      new WebcamScene(this,w,h,1),
      //buffers[BUF_INP1]#
      //"inp1"
      "/out"
    );*/
    /*this.addSceneOutput(
      new VideoScene(this,w,h,"video/129-Probe 7 - Over and Out(1)-05.mkv"),
      buffers[BUF_INP0]
    );*/


    /*this.addScene(
      new TextFlashScene(this,w,h).setInputBuffer(buffers[BUF_INP0])
    );*/

    /*this.addSceneInputOutput(
      new PlainDrawer(this,w,h),
      buff*/

    SimpleScene ss = new SimpleScene(this,w,h);
    ss.setCanvas("src", "/inp1");
    ss.setCanvas("out", "/out");
    /*ss.addFilter(new PlainDrawer(ss).setCanvases("/out",ss.getCanvasMapping("src"))); //getCsetBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    ss.addFilter(new KaleidoFilter(ss).setCanvases("/out",ss.getCanvasMapping("src"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));*/

    //ss.addFilter(new dlibKinectFilter(ss)); //.setCanvases("/out","/out"));//ss.getCanvasMapping("out")));
    ss.addFilter(new OpenNIFilter(ss));

    this.addScene(ss);
    /*this.addSceneOutput(
      ss,
      buffers[BUF_OUT]
    );*/

    /*this.addSceneInputOutputCanvas(
      new SpiralScene(this,w,h,"/inp0")
            //.registerCallbackPreset("beat", "beat_8", "spin")
            .registerCallbackPreset("beat", "beat_4", "unwind")
            .registerCallbackPreset("beat", "beat_1", "zoom")
            ,
      "/out",
      "/out"
      //buffers[BUF_OUT],
      //buffers[BUF_OUT]
    );*/

    /*this.addSceneInputOutputCanvas(
      new TextFlashScene(this,w,h),
      "/out",
      "/out"
//      buffers[BUF_OUT],
//      buffers[BUF_OUT]
    );*/

    /*this.addSceneInputOutput(
      new SimpleScene(this,w,h).addFilter(
        new PlainDrawer(this,w,h),
      buffers[BUF_INP1],
      buffers[BUF_OUT]
    );*/

    /*this.addSceneInputOutput(
      new DebugScene(this,w,h),
      buffers[BUF_INP0],
      // need to set these buffers BEFORE we initialise the filters (which are initalised in the Project initialise functions... after this setupScenes function) !!!!
      //... so we only initialise the filters when we initialise scenes..
      //... need to change Scene initialisation functions (pain in the arse but need to do anyway)..
      //... make Project call initialiseFilters() on the Scene, which calls initialise() on Filter, which then buffers appropriately
      buffers[BUF_OUT]
    );*/

    this.addSceneOutputCanvas(
      new DebugScene(this,w,h),
      "/out"
      //buffers[BUF_OUT]
    );

    /*this.addScene(
      new TimeScene(this,w,h)
    );*/

    //in.loop();

    return true;
  }

@Override
public void initialiseStreams() {
	// TODO Auto-generated method stub
	
}


}
