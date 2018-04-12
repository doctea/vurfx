package vurfeclipse.projects;

import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;

import java.io.Serializable;
import vurfeclipse.sequence.ChainSequence;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequencers.SequenceSequencer;
import vurfeclipse.streams.*;
import vurfeclipse.user.scenes.BlenderFX1;
import vurfeclipse.user.scenes.BlobFX1;
import vurfeclipse.user.scenes.OutputFX1;
import vurfeclipse.user.scenes.OutputFX2;
import vurfeclipse.user.scenes.OutputFX3;
import vurfeclipse.user.scenes.TunnelScene;

public class TestProject extends Project implements Serializable {

	//AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");

	float tempo = 150.0f; //10.0f; //150.0f;
	boolean enableSequencer = true;

	public TestProject(int w, int h) {
		super(w,h);
	}

	public boolean initialiseBuffers() {
		addCanvas("/out",   Canvas.makeCanvas(w,h,gfx_mode,"output"));
		addCanvas("/pix0",  Canvas.makeCanvas(w,h,gfx_mode,"input1"));
		addCanvas("/pix1",  Canvas.makeCanvas(w,h,gfx_mode,"input2"));
		addCanvas("/temp1", Canvas.makeCanvas(w,h,gfx_mode,"temp1"));
		addCanvas("/temp2", Canvas.makeCanvas(w,h,gfx_mode,"temp2"));
		addCanvas("/temp3", Canvas.makeCanvas(w,h,gfx_mode,"temp3"));

		//addCanvas("/blendresult", Canvas.makeCanvas(w,h,gfx_mode,"temp2"));

		return false;
	}

	@Override
	public boolean setupStreams () {
		BeatStream beatStream = new BeatStream("Beat Stream", tempo, APP.getApp().millis());
		this.getSequencer().addStream("beat", beatStream);

		return true;
	}
	
	@Override
	public boolean setupSequencer() {
		//this.sequencer = new SceneSequencer(this,w,h);
		this.sequencer = new SequenceSequencer((Project)this,w,h);
		return true;
	}
	
	public boolean setupScenes () {  
    // need a way to specify which scenes are running (selected only at first?)
    // need a way to blend between scenes ...
    // need a way for scenes to write to a shared buffer .. 
      // set output buffer on Webcam Scene to a Project buffer 
      // set input buffer on following Scenes to that buffer
    
	this.addScene(new BlankerScene(this, w, h));

    SimpleScene s = new SimpleScene(this,w,h);
    this.addScene(s);
    s.initialise();
    
    SimpleScene s2 = new SimpleScene(this,w,h);
    s2.initialise();
    this.addScene(s2);

    
    this.addSceneInputOutputCanvas(
      new DebugScene(this,w,h),
      "/inp0",
      "/out"
      //buffers[BUF_INP0],  
      // need to set these buffers BEFORE we initialise the filters (which are initalised in the Project initialise functions... after this setupScenes function) !!!! 
      //... so we only initialise the filters when we initialise scenes..
      //... need to change Scene initialisation functions (pain in the arse but need to do anyway)..
      //... make Project call initialiseFilters() on the Scene, which calls initialise() on Filter, which then buffers appropriately
      //buffers[BUF_OUT]
    );
        
    //in.loop();
    
    return true;
  }

	@Override
	public void initialiseStreams() {
		// TODO Auto-generated method stub
		
	}
  
  
}
