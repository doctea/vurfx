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

public class MinimalProject extends Project implements Serializable {

  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");
	
	float tempo = 150.0f; //10.0f; //150.0f;

  public MinimalProject(int w, int h, String gfx_mode) {
    super(w,h,gfx_mode);
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

  public boolean setupStreams () {
    BeatStream beatStream = new BeatStream("Beat Stream", tempo, APP.getApp().millis());
    this.addStream("beat", beatStream);

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
	  							0.1d
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

  	this.addBlankerScene("/out");


    // SWITCHER  //////////////////////////
    //final SwitcherScene switcher = (SwitcherScene) this.addSceneOutputCanvas(new SwitcherScene(this, w, h), "/out");
    SequenceSequencer switcher = (SequenceSequencer)sequencer;

    final Scene blendScene = new BlenderFX1(this,"pix1 BlenderFX", w, h).setOutputCanvas("/out");//.setInputCanvas("/pix1");
    blendScene.setCanvas("pix0","/pix0");	//NOZ KINECT ENABLE
    blendScene.setCanvas("pix1","/pix1");	// NOZ KINECT ENABLE
    
    //blendScene.addFilter(((OpenNIFilter) new OpenNIFilter(blendScene,1).setOutputCanvas("/pix0").setFilterName("kinect0")));//.setDepthOutputCanvasName("pix1"));	// NOZ KINECT ENABLE
    blendScene.addFilter(((OpenNIFilter) new OpenNIFilter(blendScene,0).setOutputCanvas("/pix0").setFilterName("kinect1")));
    blendScene.setCanvas("depth", "/pix1"); // NOZ KINECT ENABLE
    
    /*blendScene.addSequence("_next_camera", new SimpleSequence() {
    	int camera = 0;
    	int max_camera = 2;
			@Override
			public void onStart() {
				super.onStart();
				int current_camera = camera;
				
				OpenNIFilter old = (OpenNIFilter)blendScene.getFilter("kinect"+camera);
				//old.setCanvases("depth", "/NULL").setOutputCanvas("/NULL"); //setMuted(true);
				old.changeParameterValue("depth", new Boolean(false));
				old.changeParameterValue("rgb", new Boolean(false));
				camera++;
				if (camera>=max_camera) camera = 0;
				//blendScene.getFilter("kinect"+camera).setOutputsetMuted(false);
				old = (OpenNIFilter)blendScene.getFilter("kinect"+camera);
				old.changeParameterValue("depth", new Boolean(true));
				old.changeParameterValue("rgb", new Boolean(true));
				
			}
			
			public void onStop() {
				//blendScene.getFilter("kinect"+camera).setMuted(true);
			}
			
			@Override
			public boolean readyToChange(int max_i) {
				return true;				
			}
    });*/
    
    //switcher.bindScene("blend scene", "preset 1", blendScene);
    this.addScene(blendScene);
    switcher.bindSequence("blend", blendScene, "preset 1", 100).setLengthMillis(1000);
    switcher.bindSequence("blend2_next_", blendScene, "preset 2_next_", 100).setLengthMillis(0);
    //switcher.bindSequence("_next_camera", blendScene, "_next_camera", 50);


    Scene blobScene = new BlobFX1(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out").setInputCanvas("/pix0");
    this.addScene(blobScene);
    switcher.bindSequence("blob1_1", blobScene, "preset 1");
    switcher.bindSequence("blob1_2", blobScene, "preset 2");
    switcher.bindSequence("blob1_3", blobScene, "preset 3");
    switcher.bindSequence("blob1_4", blobScene, "preset 4");

    Scene blobScene2 = new BlobFX1(this,w,h).setSceneName("BlobScene2").setOutputCanvas("/out").setInputCanvas("/out");
    this.addScene(blobScene2);
    switcher.bindSequence("blob2_1", blobScene2, "preset 1");
    switcher.bindSequence("blob2_2", blobScene2, "preset 2");
    switcher.bindSequence("blob2_3", blobScene2, "preset 3");
    switcher.bindSequence("blob2_4", blobScene2, "preset 4");



    // BLOB SPIRAL SCENE
    /*final Scene blobScene =  switcher.bindSequence("blob drawer",   new BlobFX1(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out").setInputCanvas("/pix0"), "preset 1");
    final Scene blobScene2 = switcher.bindSequence("blob drawer 2", new BlobFX1(this,w,h).setSceneName("BlobScene2").setOutputCanvas("/out"), "preset 2");
    final Scene blobScene3 = switcher.bindSequence("blob drawer 3", new BlobFX1(this,w,h).setSceneName("BlobScene3").setOutputCanvas("/out").setInputCanvas("/pix0"), "preset 3");
    final Scene blobScene4 = switcher.bindSequence("blob drawer 4", new BlobFX1(this,w,h).setSceneName("BlobScene4").setOutputCanvas("/out"), "preset 4");*/

    // event listener to switch the switcher.
    /*getStream("beat").registerEventListener("bar_1", new ParameterCallback() {
    	 public void call (Object value) {
    		 if (switcher.readyToChange(2)) switcher.randomScene();
    	 }
    });*/

    // OUTPUT FILTERS

    PlasmaScene plasmaScene = (PlasmaScene)(new PlasmaScene(this,w,h).setSceneName("PlasmaScene"));
    plasmaScene.setCanvas("out", "/out");

    addScene(plasmaScene);
    //plasmaScene.setupFilters();

    plasmaScene.registerCallbackPreset(getStream("beat"), "beat_8", "warp");
    //this.addSceneOutputCanvas(plasmaScene, "/out");
    switcher.bindSequence("plasma_1", plasmaScene, "preset 1",10);
    switcher.bindSequence("plasma_2", plasmaScene, "preset 2",10);
    switcher.bindSequence("plasma_3", plasmaScene, "preset 3",10);
    //switcher.bindSequence("plasma_4", plasmaScene, "preset 4",10);

    /// END PLASMA SCENE
    
    /// START Quasicrystal SCENE
    QuasicrystalScene quasicrystalScene = (QuasicrystalScene)(new QuasicrystalScene(this,w,h).setSceneName("QuasicrystalScene"));
    quasicrystalScene.setCanvas("out", "/out");

    addScene(quasicrystalScene);
    //plasmaScene.setupFilters();

    quasicrystalScene.registerCallbackPreset(getStream("beat"), "beat_8", "warp");
    //this.addSceneOutputCanvas(plasmaScene, "/out");
    switcher.bindSequence("quasicrystal_1", quasicrystalScene, "preset 1", 1000);
		
    /// END Quasicrystal SCENE

    this.addSceneInputOutputCanvas(
      //os,
      new OutputFX1(this,w,h).setSceneName("OutputShader").setCanvas("pix0", "/pix0").setCanvas("pix1", "/pix1"),
      "/out",
      "/out"
    );

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



    /*((TextFlashScene)*/this.addSceneInputOutputCanvas(
  	      new TextFlashScene(this,w,h  /*, new String[] {
  	        //"Nozstock", "Nozstock: the Hidden Valley",
  	        "Vurf",
  	        "Boars Head",
  	        ":)",
  	        ":D"
  	      }*/
  	    		  )/*.setFonts(new String[] {
  	    	"Caveman-128.vlw", "Dinosaur-512.vlw", "DinosBeeline-512.vlw", "LostWorld-128.vlw", "DinosaurJrPlane-256.vlw", "DinosaurSkin-128.vlw"
  	      })  */
  	        .setSceneName("TextFlash")
  	        //.getSequencesForWords
  	        //.registerCallbackPreset("beat","beat_1", "random")
  	        //.registerCallbackPreset("beat","beat_8", "rotate")
  	        .registerCallbackPreset("beat","beat_16","swivel")
  	        ,
  	      "/out",
  	      "/out"
  	    ).setMuted(false);
  	//).addSequencesForWords(new String[] { "test", "bob", "alice" } )
  	/**/
  	;

    //switcher.setBindToRandom(true);
    //switcher.setRandomMode(true);


		Sequence doubleSequence = new ChainSequence(2000)
			.addSequence(getSceneForPath("/sc/BlobScene"),  "preset 1")
			//.addSequence(getSceneForPath("/sc/PlasmaScene"), "preset 1")
			.addSequence(blendScene, "preset 1")
		;
    switcher.bindSequence("d1:", doubleSequence, 10);
    
  	Sequence doubleSequence2 = new ChainSequence(2000)
  			//.addSequence(getSceneForPath("/sc/BlobScene"),  "preset 1")
  			.addSequence(getSceneForPath("/sc/PlasmaScene"), "preset 2")
  			.addSequence(blendScene, "preset 1")
  			//.addSequence(getSceneForPath("/sc/PlasmaScene"), "preset 3")
  			//.addSequence(blendScene, "preset 1")
  		;
  	switcher.bindSequence("d2:", doubleSequence2, 5);
  	
  	Sequence doubleSequence3 = new ChainSequence(2000)
  			.addSequence(getSceneForPath("/sc/BlobScene2"), "preset 1")
  			.addSequence(blendScene, "preset 1")
  	;
  	switcher.bindSequence("d2:", doubleSequence3, 5);
  	
  	//switcher.bindSequence("blend:",  blendScene, "preset 1", 10);


    Sequence cSequence = new ChainSequence(0)
    	.addSequence(getSceneForPath("/sc/TextFlash"), 	   "preset 1")
    	//.addSequence(getSceneForPath("/sc/OutputShader"),  "preset 1")
    	//.addSequence(getSceneForPath("/sc/OutputShader2"), "preset 1")
    ;


    //switcher.bindSequence("outputModeChange1", cSequence, 4);
    /*switcher.bindSequence("outputModeChange5", opSequence);
    switcher.bindSequence("outputModeChange6", opSequence);
    switcher.bindSequence("outputModeChange7", opSequence);
    switcher.bindSequence("outputModeChange8", opSequence);*/
    TunnelScene ts1 =  (TunnelScene) this.addSceneInputOutputCanvas(
    		new TunnelScene(this, w, h).setCanvas("temp", "/temp2")
			//.addFilter(new BlendDrawer()))
		, "/pix0", "/out"
	);
    int tunnel_weight = 1;
    switcher.bindSequence("tunnel_1_blob_pulse_1", new ChainSequence(2000).addSequence(ts1, "preset 2").addSequence(blobScene, "preset 2"), tunnel_weight);
    switcher.bindSequence("tunnel_1_blob_preset_2_pulse_preset1", new ChainSequence(2000).addSequence(ts1, "preset 3").addSequence(blobScene, "preset 2"), tunnel_weight);
    switcher.bindSequence("tunnel_1_blob_pulse_preset1", new ChainSequence(2000).addSequence(ts1, "preset 2").addSequence(blobScene, "preset 3"), tunnel_weight);
    switcher.bindSequence("tunnel_1_blob_wobble_preset3",new ChainSequence(2000).addSequence(ts1, "preset 3").addSequence(blobScene, "preset 3"), tunnel_weight);
    switcher.bindSequence("tunnel_1_blend_wobble_preset2",new ChainSequence(2000).addSequence(ts1, "preset 1").addSequence(blendScene, "preset 1"), tunnel_weight);
    
    switcher.bindSequence("tunnel_1_blend_angled_2",new ChainSequence(2000).addSequence(ts1, "f2 angled 60").addSequence(blendScene, "preset 1"), tunnel_weight);
    
    /*switcher.bindSequence(
        	"tunnel_2_pulse",
        	*/
    TunnelScene ts2 = (TunnelScene) this.addSceneInputOutputCanvas(
	    		new TunnelScene(this, w, h).setCanvas("temp", "/temp3")
	    			//.addFilter(new BlendDrawer()))

	    		, "/out", "/out"
	);
    tunnel_weight = 5;
    switcher.bindSequence("tunnel_2_plasma_pulse_1", new ChainSequence(2000).addSequence(ts2, "preset 3").addSequence(plasmaScene, "preset 1"), tunnel_weight);
    switcher.bindSequence("tunnel_2_plasma_pulse_2", new ChainSequence(2000).addSequence(ts2, "preset 2").addSequence(plasmaScene, "preset 2"), tunnel_weight);
    switcher.bindSequence("tunnel_2_blob_pulse_1",   new ChainSequence(2000).addSequence(ts2, "preset 3").addSequence(blobScene2, "preset 1"), tunnel_weight);
    switcher.bindSequence("tunnel_2_blob_pulse_2",   new ChainSequence(2000).addSequence(ts2, "preset 3").addSequence(blobScene2, "preset 2"), tunnel_weight);
    switcher.bindSequence("tunnel_2_double_pulse_1", new ChainSequence(2000).addSequence(ts2, "preset 2").addSequence(doubleSequence), tunnel_weight/5);
    switcher.bindSequence("tunnel_2_blend_pulse_1",  new ChainSequence(2000).addSequence(ts2, "preset 3").addSequence(blendScene, "preset 1"), tunnel_weight);
    switcher.bindSequence("tunnel_2_blob_wobble_1",  new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(blobScene, "preset 1"), tunnel_weight);
    switcher.bindSequence("tunnel_2_blob_wobble_2",  new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(blobScene, "preset 2"), tunnel_weight/5);
    switcher.bindSequence("tunnel_2_blob_wobble_3_fade", new ChainSequence(2000).addSequence(ts2, "preset 2").addSequence(blobScene, "preset 3").addSequence(getSceneForPath("/sc/BlankerScene"), "fade"), tunnel_weight*2);

    
    
    //switcher.bindSequence("d1:", new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(blobScene2, "preset 1"), 50);
    //switcher.bindSequence("d1:", new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(blobScene2, "preset 1"), 50);
    //switcher.bindAndPermute("d1:", new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(blobScene2, "preset 1"), getSceneForPath("/sc/OutputShader2"), 5000);

    /*switcher.bindSequence("tunnel_2_blob_wobble_3_fade_kaleido", new ChainSequence(2000)
    			.addSequence(ts2, "preset 3")
    			.addSequence(blobScene, "preset 4")
    			.addSequence(getSceneForPath("/sc/BlankerScene"), "fade")
    			.addSequence(getSceneForPath("/sc/OutputShader2"), "show_kaleido")
    			//.addSequence(getSceneForPath("/sc/OutputShader2"), "show_feedback")
    		, 50
    );*/
	//), "preset 1", 20);

    switcher.bindAndPermute("t1:", "tunnel_1_", getSceneForPath("/sc/OutputShader"), 5000);
    switcher.bindAndPermute("t1:", "tunnel_1_", getSceneForPath("/sc/OutputShader2"), 5000);
    switcher.bindAndPermute("t2:", "tunnel_2_", getSceneForPath("/sc/OutputShader"), 5000);
    switcher.bindAndPermute("t2:", "tunnel_2_", getSceneForPath("/sc/OutputShader2"), 5000);

    //switcher.bindAndPermute("t2:", "tunnel_2_", "t1:", 5000);

    switcher.bindAndPermute("t3_blanker", "t1:", getSceneForPath("/sc/BlankerScene"), 5000);
    switcher.bindAndPermute("t3_blanker", "t2:", getSceneForPath("/sc/BlankerScene"), 5000);

    //switcher.bindAndPermute("t4", "t3_blanker", getSceneForPath("/sc/BlankerScene"), 5000);

    switcher.bindAndPermute("d1:", doubleSequence, getSceneForPath("/sc/OutputShader"), 5000);
    switcher.bindAndPermute("d1:", doubleSequence, getSceneForPath("/sc/OutputShader2"), 5000);
    switcher.bindAndPermute("d2:", doubleSequence2, getSceneForPath("/sc/OutputShader"), 5000);
    switcher.bindAndPermute("d2:", doubleSequence2, getSceneForPath("/sc/OutputShader2"), 5000);
    
    //switcher.bindAndPermute("t1:", "tunnel_1_", getSceneForPath("/sc/OutputShader3"), 5000);
    //switcher.bindAndPermute("t3:", "d", getSceneForPath("/sc/OutputShader3"), 5000);

    
    /*switcher.bindAndPermute("e1:", "d1:", getSceneForPath("/sc/TextFlash"), 5000);
    switcher.bindAndPermute("e2:", "t",   getSceneForPath("/sc/TextFlash"), 5000);*/


    ((TextFlashScene)getSceneForPath("/sc/TextFlash")).addSequencesForWords(new String[] {
    		//"end is near",
    		//"the blunders"
    		//"  FolkTheSystem  ",
    		//"  DubTheEarth  "
    		//"etc",
    		/*"BABAL",
    		"Glowpeople",
    		"Socio Suki",
    		"what about the pig",
    		"hold back!",
    		"lied to me",
    		"identity",
    		"dapper little man",
    		"lazy",
    		"bad rabbit",
    		"take trips",
    		"magic dust",
    		"merry xmas"*/
    }, 0);
    switcher.setBindToRandom(false);
    switcher.bindSequences("text", getSceneForPath("/sc/TextFlash"));
    //switcher.setBindToRandom(true);


    for (int l = 1 ; l < 3 ; l ++ ) {
    	//switcher.bindAndPermute("wat1_", "d", ts1, 50*(10*l^2));
    	//switcher.bindAndPermute("t3", "d", ts1, 50*(10*l^2));
    	/*switcher.bindAndPermute("wat2_", "d", ts2, 75*l);
    	switcher.bindAndPermute("wat3_", "t", ts1, 250*l);
    	switcher.bindAndPermute("wat3_", "t", ts2, 500*l);*/
    	switcher.bindAndPermute("vd1"+l+":", "d1", getSceneForPath("/sc/OutputShader3"), 2000*(l*l));
    	switcher.bindAndPermute("vd2_"+l+":", "d2", getSceneForPath("/sc/OutputShader3"), 2000*(l*l));
    	switcher.bindAndPermute("vt1_"+l+":", "t1:", getSceneForPath("/sc/OutputShader3"), 2000*(l*l));
    	switcher.bindAndPermute("vt2_"+l+":", "t2:", getSceneForPath("/sc/OutputShader3"), 2000*(l*l));
    	
    	switcher.bindAndPermute("bv3_"+l+":", "blob", getSceneForPath("/sc/OutputShader3"), 2000*(l*l));
    	switcher.bindAndPermute("bv2_"+l+":", "blob", getSceneForPath("/sc/OutputShader2"), 2000*(l*l));
    	switcher.bindAndPermute("bb2_"+l+":", "blend", getSceneForPath("/sc/OutputShader2"), 2000*(l*l));
    	switcher.bindAndPermute("bb3_"+l+":", "blend", getSceneForPath("/sc/OutputShader"), 2000*(l*l));
    }

    //switcher.setRandomMode(false);
    this.addSceneOutputCanvas(
    		new BadTVScene(this,w,h).registerCallbackPreset(getStream("beat"), "beat_8", "warp"),
    		"/out"
    );

    this.addSceneOutputCanvas(
      new DebugScene(this,w,h),
      "/out"
    );

    //in.loop();

    return true;
  }

  public void setupExposed() {
	  	//rsConn.expose("/seq/changeTo/" + "text_word_:)");
	  	//rsConn.expose("/seq/changeTo/" + "text_word_:D");
	  	/*rsConn.expose("/seq/changeTo/" + "text_word_BABAL");
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