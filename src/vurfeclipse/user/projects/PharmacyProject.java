package vurfeclipse.user.projects;
import vurfeclipse.APP;
import vurfeclipse.Blob;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;

import java.io.Serializable;
import java.util.*;

import processing.core.PApplet;
import processing.core.PVector;
import vurfeclipse.filters.*;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.ChainSequence;
import vurfeclipse.sequence.ChangeParameterSequence;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.SceneSequencer;
import vurfeclipse.sequence.SequenceSequencer;
import vurfeclipse.sequence.ShowSceneSequence;
import vurfeclipse.sequence.SimpleSequence;
import vurfeclipse.streams.*;
import vurfeclipse.user.scenes.BlenderFX1;
import vurfeclipse.user.scenes.BlobFX1;
import vurfeclipse.user.scenes.OutputFX1;
import vurfeclipse.user.scenes.OutputFX2;
import vurfeclipse.user.scenes.OutputFX3;
import vurfeclipse.user.scenes.TunnelScene;

public class PharmacyProject extends Project implements Serializable {

  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");

  public PharmacyProject(int w, int h, String gfx_mode) {
    super(w,h,gfx_mode);
  }

  public boolean initialiseBuffers() {
    addCanvas("/out",   Canvas.makeCanvas(w,h,gfx_mode,"output"));
    addCanvas("/pix0",  Canvas.makeCanvas(w,h,gfx_mode,"input1"));
    addCanvas("/pix1",  Canvas.makeCanvas(w,h,gfx_mode,"input2"));
    addCanvas("/temp1", Canvas.makeCanvas(w,h,gfx_mode,"temp1"));
    addCanvas("/temp2", Canvas.makeCanvas(w*2,h*2,gfx_mode,"temp2"));	//w*2,h*2 etc for oversize
    addCanvas("/temp3", Canvas.makeCanvas(w,h,gfx_mode,"temp3"));

    //addCanvas("/blendresult", Canvas.makeCanvas(w,h,gfx_mode,"temp2"));

    return false;
  }

  public boolean setupStreams () {
    BeatStream beatStream = new BeatStream("Beat Stream", 120.0f, APP.getApp().millis());
    this.addStream("beat", beatStream);

    return true;
  }

  public boolean setupSequencer() {
	  //this.sequencer = new SceneSequencer(this,w,h);
	  this.sequencer = new SequenceSequencer(this,w,h);
	  return true;
  }

  public boolean setupScenes () {

  	this.addBlankerScene("/out");
    SequenceSequencer switcher = (SequenceSequencer)sequencer;
    switcher.setRandomMode(false);

  	/// INPUT SCENES
    final SimpleScene video1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("VideoScene1");//.setOutputBuffer(getCanvas("inp0").surf);

    final VideoPlayer vp1 = new VideoPlayer(video1,"video-sources/pharmacy stripped.ogg");
    vp1.setOutputCanvas("/pix0");
    //vp2.loadDirectory("video-sources/");
    //vp2.setOutputCanvas("/pix1"); // think this should also be "pix1" ... ?
    video1.addFilter(vp1);

    final PlainDrawer pd = (PlainDrawer)new PlainDrawer(video1).setFilterName("Source Switcher").setInputCanvas("/pix0");
    video1.addFilter(pd);
    video1.addSequence("select_pix0", new ShowSceneSequence(video1, 0) {

    	boolean alreadyRan = false;

			@Override public void setValuesForNorm(double pc, int iteration) { super.setValuesForNorm(pc, iteration);}
			@Override public void onStop() {
				//super.onStop();
				if (this.alreadyRan) return;
				video1.setMuted(false);
			}

			@Override
			public void onStart() {
				if (this.alreadyRan) return;
				this.alreadyRan = true;
				super.onStart();
				//vp2.nextMode();
				video1.setMuted(false);
				video1.setMuted(false);
				vp1.setMuted(false);

				vp1.nextMode();
				//pd.setInputCanvas("/pix0");
			}
    });
    video1.addSequence("stop_pix0", new ShowSceneSequence(video1, 0) {
			@Override public void setValuesForNorm(double pc, int iteration) { super.setValuesForNorm(pc, iteration);}
			@Override public void onStop() {
				//super.onStop();
				video1.setMuted(true);
			}

			@Override
			public void onStart() {
				super.onStart();
				//vp2.nextMode();
				video1.setMuted(true);
				video1.setMuted(true);
				vp1.setMuted(true);

				//vp1.nextMode();
				//pd.setInputCanvas("/pix0");
			}
    });
		video1.setMuted(false);

    this.addSceneOutputCanvas(
        video1,
        "/out"
      ).setMuted(false);

    //switcher.bindAll(video1.getSequences());



    // MIDDLE LAYER: FX




    // OUTPUT FILTERS


    PlasmaScene plasmaScene = (PlasmaScene)(new PlasmaScene(this,w,h).setSceneName("PlasmaScene"));
    plasmaScene.setCanvas("out", "/out");
    plasmaScene.setMuted();

    addScene(plasmaScene);
    //plasmaScene.setupFilters();

    plasmaScene.registerCallbackPreset(getStream("beat"), "beat_8", "warp");
    //this.addSceneOutputCanvas(plasmaScene, "/out");
    switcher.bindSequence("plasma_1", plasmaScene, "preset 1");
    switcher.bindSequence("plasma_2", plasmaScene, "preset 2");
    switcher.bindSequence("plasma_3", plasmaScene, "preset 3");

    /// END PLASMA SCENE


    this.addSceneInputOutputCanvas(
      //os,
      new OutputFX1(this,w,h).setSceneName("OutputShader"),
      "/out",
      "/out"
    ).setMuted();


    // OUTPUT FILTER 2
    this.addSceneInputOutputCanvas(
    		new OutputFX2(this,w,h).setSceneName("OutputShader2").setCanvas("pix0", "/pix0"),
    		"/out",
    		"/out"
    ).setMuted();

    // OUTPUT FILTER 2
    this.addSceneInputOutputCanvas(
    		new OutputFX3(this,w,h).setSceneName("OutputShader3").setCanvas("pix0", "/pix0"),
    		"/out",
    		"/out"
    ).setMuted();

    TunnelScene ts1 =  (TunnelScene) this.addSceneInputOutputCanvas(
    		new TunnelScene(this, w*2, h*2).setSceneName("TunnelScene").setCanvas("temp", "/temp2") ///pix0")
    		//.addFilter(new BlendDrawer()))
    		, "/pix0", "/out"
    );

    TunnelScene ts2 = (TunnelScene) this.addSceneInputOutputCanvas(
	    		new TunnelScene(this, w*2, h*2).setSceneName("TunnelScene2").setCanvas("temp", "/temp2")
	    			//.addFilter(new BlendDrawer()))
	    		, "/out", "/out"
    );
    ts1.setMuted();
    ts2.setMuted();


    // sequencer



    ChainSequence start = new ChainSequence(5000).addSequence(video1.getSequence("select_pix0"));
    //start.addSequence(plasmaScene.getSequence("preset 1"));
    switcher.addListSequence("start", start);

    //Sequence fx = this.getSceneForPath("/sc/OutputShader2").getSequence("preset 1");
    ChainSequence fx = new ChainSequence(2000).addSequence(ts1, "p1 angled 60");
    fx.setLengthMillis(2000);
    switcher.addListSequence("fx", fx);

    ChainSequence fx2 = new ChainSequence(2000).addSequence(ts2, "f2 angled 30");
    switcher.addListSequence("fx2", fx2);

    ChainSequence fx3 = new ChainSequence(10000).addSequence(this.getSceneForPath("/sc/OutputShader3"), "show_sync").addSequence(
    		new SimpleSequence() {
    			boolean on = true;

    			@Override public void onStart() {
    				getSceneForPath("/sc/PlasmaScene").nextFilterMode();
    			}
					@Override
					public void setValuesForNorm(double pc, int iteration) {
							//getSceneForPath("/sc/VideoScene1").setMuted(false);
							getSceneForPath("/sc/PlasmaScene").setMuted((int)(pc*1000.0)%2==0);
							getSceneForPath("/sc/TunnelScene").setMuted((int)(pc*1000.0)%2!=0);
							getSceneForPath("/sc/OutputShader").setMuted((int)(pc*1000.0)%4==0);
							getSceneForPath("/sc/OutputShader2").setMuted((int)(pc*1000.0)%8!=0);

							getSceneForPath("/sc/TunnelScene").getFilter("Blend_1").changeParameterValueFromSin("Rotate", (float)Math.sin(pc));

					}
    		}
    );
    fx3.setLengthMillis(90000);
    switcher.addListSequence("fx3", fx3);


    ChainSequence stop = new ChainSequence(5000).addSequence(video1.getSequence("stop_pix0"));
    //start.addSequence(plasmaScene.getSequence("preset 1"));
    switcher.addListSequence("stop", stop);

    //LIST OF THINGS TO PERMUTE


    this.addSceneOutputCanvas(
      new DebugScene(this,w,h),
      "/out"
    );

    //in.loop();

    return true;
  }

  public void setupExposed() {
/*	  	rsConn.expose("/seq/changeTo/" + "text_word_:)");
	  	rsConn.expose("/seq/changeTo/" + "text_word_:D");
	  	rsConn.expose("/seq/changeTo/" + "text_word_BABAL");
	  	rsConn.expose("/seq/changeTo/" + "text_word_Glowpeople");
	  	rsConn.expose("/seq/changeTo/" + "text_word_Socio Suki");
	    rsConn.expose("/seq/changeTo/" + "text_word_what about the pig");
	    rsConn.expose("/seq/changeTo/" + "text_word_hold back!");
	    rsConn.expose("/seq/changeTo/" + "text_word_lied to me");
	    rsConn.expose("/seq/changeTo/" + "text_word_identity");
	    rsConn.expose("/seq/changeTo/" + "text_word_dapper little man");
	    rsConn.expose("/seq/changeTo/" + "text_word_lazy");
	    rsConn.expose("/seq/changeTo/" + "text_word_bad rabbit");
	    rsConn.expose("/seq/changeTo/" + "text_word_take trips");
	    rsConn.expose("/seq/changeTo/" + "text_word_magic dust");
	    rsConn.expose("/seq/changeTo/" + "text_word_merry xmas");*/
  }

}
