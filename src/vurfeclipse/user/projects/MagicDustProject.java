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

public class MagicDustProject extends Project implements Serializable {

  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");

  public MagicDustProject(int w, int h, String gfx_mode) {
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

    final VideoPlayer au1 = new VideoPlayer(video1,"Magic Dust Mix_02 (master).wav");
    au1.setOutputCanvas("/temp0");
    //vp1.loadDirectory("video-sources/");
    //vp2.setOutputCanvas("/pix1"); // think this should also be "pix1" ... ?
    video1.addFilter(au1);

    final VideoPlayer vp1 = new VideoPlayer(video1,"video-sources/you're all the same.ogg");
    vp1.setOutputCanvas("/pix0");
    //vp1.loadDirectory("video-sources/");
    //vp2.setOutputCanvas("/pix1"); // think this should also be "pix1" ... ?
    video1.addFilter(vp1);

    final VideoPlayer vp2 = new VideoPlayer(video1,"video-sources/Socio Suki - Magic Dust (live at The Bridge Inn, Worcester - 25th January 14)-sw6u78no5f4.ogg");
    vp2.setOutputCanvas("/pix1");
    //vp2.loadDirectory("video-sources/");
    //vp2.setOutputCanvas("/pix1"); // think this should also be "pix1" ... ?
    video1.addFilter(vp2);

    /*video1.addSequence("next", new ShowSceneSequence(video1, 0) {
			@Override public void setValuesForNorm(double pc, int iteration) { super.setValuesForNorm(pc, iteration);}
			@Override public void onStop() { super.onStop(); }

			@Override
			public void onStart() {
				super.onStart();
				vp2.nextMode();
			}
		});*/

    final PlainDrawer pd = (PlainDrawer)new PlainDrawer(video1).setFilterName("Source Switcher").setInputCanvas("/pix0");
    video1.addFilter(pd);
    video1.addSequence("select_pix0", new ShowSceneSequence(video1, 0) {
			@Override public void setValuesForNorm(double pc, int iteration) { super.setValuesForNorm(pc, iteration);}
			@Override public void onStop() { super.onStop(); }

			@Override
			public void onStart() {
				super.onStart();
				//vp2.nextMode();
				video1.setMuted(false);
				vp1.nextMode();
				pd.setInputCanvas("/pix0");
			}
    });
    video1.addSequence("select_pix1", new ShowSceneSequence(video1, 0) {
			@Override public void setValuesForNorm(double pc, int iteration) { super.setValuesForNorm(pc, iteration);}
			@Override public void onStop() { super.onStop(); }

			@Override
			public void onStart() {
				super.onStart();
				//vp2.nextMode();
				pd.setInputCanvas("/pix1");
			}
    });

    this.addSceneOutputCanvas(
        video1,
        "/out"
      );

    //switcher.bindAll(video1.getSequences());

    switcher.addListSequence("select_pix0", new ChainSequence(500).addSequence(video1.getSequence("select_pix0")));
    switcher.addListSequence("select_pix0", new ChainSequence(5000).addSequence(video1.getSequence("select_pix0")));
    switcher.addListSequence("select_pix1", new ChainSequence(2000).addSequence(video1.getSequence("select_pix1")));



    // MIDDLE LAYER: FX




    // OUTPUT FILTERS


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
