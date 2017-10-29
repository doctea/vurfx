package vurfeclipse.user.projects;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.*;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.SequenceSequencer;

import java.io.Serializable;

import vurfeclipse.filters.OpenNIFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.streams.*;
import vurfeclipse.user.scenes.BlobFX1;
import vurfeclipse.user.scenes.OutputFX1;
import vurfeclipse.user.scenes.OutputFX2;
import vurfeclipse.user.scenes.OutputFX3;

public class TestProject extends Project implements Serializable {

  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");
	
  float tempo = 130.0f;

  public TestProject(int w, int h) {
    super(w,h);//,gfx_mode);
  }

  public boolean initialiseBuffers() {
    addCanvas("/out", Canvas.makeCanvas(w,h,gfx_mode,"out"));
    addCanvas("/pix0", Canvas.makeCanvas(w,h,gfx_mode,"pix0"));
    addCanvas("/pix1", Canvas.makeCanvas(w,h,gfx_mode,"pix1"));
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
    BeatStream beatStream = new BeatStream("Beat Stream", this.tempo, ((VurfEclipse)APP.getApp()).millis());
    this.addStream("test", stream);
    this.addStream("beat", beatStream);

    NumberStream numberStream = new NumberStream("Number Stream", (float) this.tempo, 0, ((VurfEclipse)APP.getApp()).millis());
    this.addStream("number", numberStream);

    return true;
  }
  

  public boolean setupSequencer() {
	  //this.sequencer = new SceneSequencer(this,w,h);
	  this.sequencer = new SequenceSequencer((Project)this,w,h) {
	  	int count = 1;
	  	int seq_count = 1;
	  	@Override
	  	public void nextSequence() {
	  		count++;
	  		//if (count%8==0) this.setRandomMode(!this.randtrue);//count%8==0);
	  		if (count%16==0) {
	  			super.randomSequence();
	  			return;
	  		}
	  		if ((count%2)==0)
	  			this.host.setTimeScale(
	  					((count%3)==0)?
	  							2.0d:
	  							0.5d
	  		); //getTimeScale()
	  		else
	  			this.host.setTimeScale(1.0f);
	  		if (count>1000) count = 0;
	  		//this.host.setTimeScale(0.01f);
	  		super.nextSequence();
	  	}
	  	@Override
	  	public void runSequences() {
	  		seq_count++;
	  		if (this.getCurrentSequenceName().contains("_next_")) {
	  			println("Fastforwarding sequence " + this.getCurrentSequenceName() + " because it contains '_next_'..");
	  			super.runSequences();
	  			this.nextSequence();
	  		}
	  		/*if ((1+(count%10))>5 && (seq_count%(count+1)<2)) {
	  			this.nextSequence();
	  		}*/
	  		//this.host.setTimeScale(0.1f); // twat
	  		//if (seq_count>10000) seq_count = 0;
	  		super.runSequences();
	  	}
	  };

	  return true;
  }

  public boolean setupScenes () {
    // need a way to specify which scenes are running (selected only at first?)
    // need a way to blend between scenes ...
    // need a way for scenes to write to a shared buffer ..
      // set output buffer on Webcam Scene to a Project buffer
      // set input buffer on following Scenes to that buffer

	  this.addBlankerScene("/out");

	  
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

    //SimpleScene ss = new SimpleScene(this,w,h);
    //ss.setCanvas("src", "/inp1");
    //ss.setCanvas("out", "/out");
    /*ss.addFilter(new PlainDrawer(ss).setCanvases("/out",ss.getCanvasMapping("src"))); //getCsetBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    ss.addFilter(new KaleidoFilter(ss).setCanvases("/out",ss.getCanvasMapping("src"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));*/

    //ss.addFilter(new dlibKinectFilter(ss)); //.setCanvases("/out","/out"));//ss.getCanvasMapping("out")));
    //ss.addFilter(new OpenNIFilter(ss));
    
    //BlobFX1 blobScene = new BlobFX1(this, this.w, this.h);
    /*SpiralScene spiralScene = new SpiralScene(this, this.w, this.h, "/inp0");
    
    this.addSceneInputOutputCanvas(spiralScene, "/src", "/out")
    	.registerCallbackPreset("beat", "beat_1", "spin_forward")
    	.setOutputCanvas("/out");*/

    //this.addScene(ss);
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
	  
    ImageListScene ils1 = (ImageListScene) new ImageListScene(this,w,h).setDirectory("mutante").setOutputCanvas("/pix0");//.setDirectory("mutante");
    //this.addSceneOutputCanvas(ils1, "pix0");
    println("ils1 has output mapping of " + ils1.getCanvasMapping("out"));
    
    ImageListScene ils2 = (ImageListScene) new ImageListScene(this,w,h).setDirectory("mutante").setOutputCanvas("/pix1");
    //this.addSceneOutputCanvas(ils2, "pix1");

    this.addSceneOutputCanvas(
      ils1, //.setCanvas("out", "/pix0"),
      "/pix0"
    );
    this.addSceneOutputCanvas(
      ils2, //.setCanvas("out", "/pix1"),
      "/pix1"
      //"/temp1"
    );

    this.addSceneInputOutputCanvas(
      new TextFlashScene(this,w,h).setCanvas("temp", "/temp1").setCanvas("out",  "/out"),
      "/temp1",
      "/out"
//      buffers[BUF_OUT],
//      buffers[BUF_OUT]
    );
    
    ((SequenceSequencer) sequencer).bindSequence("ils1_choose", ils1.getSequence("choose_0"),250); //, 2+switcher.getSequenceCount()/4);//32);
    ((SequenceSequencer) sequencer).bindSequence("ils2_choose", ils2.getSequence("choose_1"),250); //, 2+switcher.getSequenceCount()/4);//32);
    ((SequenceSequencer) sequencer).bindAll(ils1.getSequences(),250);
    ((SequenceSequencer) sequencer).bindAll(ils2.getSequences(),250);

    this.addSceneInputOutputCanvas(new PlainScene(this, w, h), "/pix0", "/out");
    this.addSceneInputOutputCanvas(new PlainScene(this, w, h), "/pix1", "/out");

    this.addSceneInputOutputCanvas(
    	      //os,
    	      new OutputFX1(this,w,h).setSceneName("OutputShader").setCanvas("pix0", "/pix0").setCanvas("pix1", "/pix1"),
    	      "/out",
    	      "/out"
    	    );
/*
    	    // OUTPUT FILTER 2
    	    this.addSceneInputOutputCanvas(
    	    		new OutputFX2(this,w,h).setSceneName("OutputShader2").setCanvas("pix0", "/pix0").setCanvas("pix1", "/pix1"),
    	    		"/out",
    	    		"/out"
    	    );

    	    this.addSceneInputOutputCanvas(
    	    		new OutputFX3(this,w,h).setSceneName("OutputShader3").setCanvas("pix0", "/pix0"),
    	    		"/out",
    	    		"/out"
    	    ).setMuted();
*/
    
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
      new DebugScene(this,w,h).setCanvas("out", "/out"),
      "/out"
      //buffers[BUF_OUT]
    ).setOutputCanvas("/out");

    /*this.addScene(
      new TimeScene(this,w,h)
    );*/

    //in.loop();

    return true;
  }


}
