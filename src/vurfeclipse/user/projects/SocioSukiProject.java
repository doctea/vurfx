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
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.SceneSequencer;
import vurfeclipse.sequence.SequenceSequencer;
import vurfeclipse.streams.*;
import vurfeclipse.user.scenes.BlenderFX1;
import vurfeclipse.user.scenes.BlobFX1;
import vurfeclipse.user.scenes.OutputFX1;
import vurfeclipse.user.scenes.OutputFX2;
import vurfeclipse.user.scenes.TunnelScene;

public class SocioSukiProject extends Project implements Serializable {
  
  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");
  
  public SocioSukiProject(int w, int h, String gfx_mode) {
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
	  
	/// INPUT SCENES
	  
    final SimpleScene ils1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene1");//.setOutputBuffer(getCanvas("inp0").surf);
    final SimpleScene ils2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene2");//.setOutputBuffer(getCanvas("inp1").surf);
    
    ils1.addFilter(new ImageListDrawer(ils1).setDirectory("ds2014").setCurrentIndex(5).setNumBlobs(10/*200*/).setFilterName("ImageListDrawer1"));
    ils2.addFilter(new ImageListDrawer(ils2).setDirectory("doctea").setCurrentIndex(0).setNumBlobs(10/*200*/).setFilterName("ImageListDrawer2"));
    
    //ils2.addFilter(new OpenNIFilter(ils2).setFilterName("kinect"));
    ils1.setCanvas("pix1","/pix1");
    //ils1.addFilter(((OpenNIFilter) new OpenNIFilter(ils1).setFilterName("kinect")).setDepthOutputCanvasName("pix1"));


    this.addSceneOutputCanvas(
      ils1,
      "/pix0"
    );
    this.addSceneOutputCanvas(
      ils2,
      "/pix1"
      //"/temp1"
    );

    

    
    // MIDDLE LAYER: FX

    
    // SWITCHER  //////////////////////////
    //final SwitcherScene switcher = (SwitcherScene) this.addSceneOutputCanvas(new SwitcherScene(this, w, h), "/out");
    SequenceSequencer switcher = (SequenceSequencer)sequencer;
        
/*    // BLEND SCENE    
    final SimpleScene bl1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlendScene");
    bl1.setOutputCanvas("/out");
    bl1.addFilter(new PlainDrawer(bl1).setInputCanvas("/pix0"));
    bl1.addFilter(new BlendDrawer(bl1).setFilterName("BlendDrawer1").setInputCanvas("/pix1"));
    //bl1.addFilter(new MirrorFilter(bl1).setFilterName("Mirror").changeParameterValue("mirror_y", true)).setInputCanvas(bl1.getCanvasMapping("out"));    
    bl1.setMuted(true);
    this.addSceneOutputCanvas(bl1,"/out"); //sblendresult");//out");//getCanvas("/out"));
        
    final Scene blendScene = switcher.addScene("blend scene", bl1);
    switcher.bindSequence("blend scene", new Sequence(5000) {
    	public void setValuesForNorm(double norm, int iteration) {
    		if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
    		switcher.getScene("blend scene").getFilter("BlendDrawer1").setParameterValue("Opacity", (float)norm);
    	}
		@Override public void onStart() {
			System.out.println("Starting blend onStart()..");
			this.setLengthMillis((int)APP.getApp().random(1,5) * 500);
			for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
				getSceneForPath("/ImageListScene1").getFilter("ImageListDrawer1").nextMode();
			for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
				getSceneForPath("/ImageListScene2").getFilter("ImageListDrawer2").nextMode();
		}
    });*/
    
    final Scene blendScene = new BlenderFX1(this,"pix1 BlenderFX", w, h).setOutputCanvas("/out");//.setInputCanvas("/pix1");
    blendScene.setCanvas("pix0","/pix0");
    blendScene.setCanvas("pix1","/pix1");
    //switcher.bindScene("blend scene", "preset 1", blendScene);
    this.addScene(blendScene);
    switcher.bindSequence("blend", blendScene, "preset 1");
    
    
    Scene blobScene = new BlobFX1(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out").setInputCanvas("/pix0");
    this.addScene(blobScene);
    switcher.bindSequence("blob1_1", blobScene, "preset 1");
    switcher.bindSequence("blob1_2", blobScene, "preset 2");
    switcher.bindSequence("blob1_3", blobScene, "preset 3");
    switcher.bindSequence("blob1_4", blobScene, "preset 4");
    
    Scene blobScene2 = new BlobFX1(this,w,h).setSceneName("BlobScene2").setOutputCanvas("/out").setInputCanvas("/pix0");
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
    switcher.bindSequence("plasma_1", plasmaScene, "preset 1");
    switcher.bindSequence("plasma_2", plasmaScene, "preset 2");
    switcher.bindSequence("plasma_3", plasmaScene, "preset 3");
    
    /// END PLASMA SCENE
    

    this.addSceneInputOutputCanvas(
      //os,
      new OutputFX1(this,w,h).setSceneName("OutputShader"),
      "/out",
      "/out"
    );    

    
    
    // OUTPUT FILTER 2
    this.addSceneInputOutputCanvas(
    		new OutputFX2(this,w,h).setSceneName("OutputShader2").setCanvas("pix0", "/pix0"),
    		"/out",
    		"/out"
    );
    
    
    
    
    
    this.addSceneInputOutputCanvas(
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
  	        .registerCallbackPreset("beat","beat_1", "random")
  	        .registerCallbackPreset("beat","beat_8", "rotate")
  	        .registerCallbackPreset("beat","beat_16","swivel")
  	        ,
  	      "/out",
  	      "/out"
  	    );    
    
    
	Sequence doubleSequence = new ChainSequence(2000)
		.addSequence(getSceneForPath("/sc/BlobScene"),  "preset 1")    	
		.addSequence(getSceneForPath("/sc/PlasmaScene"), "preset 1")
	;
    switcher.bindSequence("d1:", doubleSequence, 10);

    Sequence cSequence = new ChainSequence(0)
    	.addSequence(getSceneForPath("/sc/TextFlash"), 	   "preset 1")
    	.addSequence(getSceneForPath("/sc/OutputShader"),  "preset 1")    	
    	.addSequence(getSceneForPath("/sc/OutputShader2"), "preset 1")
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
    switcher.bindSequence("tunnel_1_blob_pulse_1", new ChainSequence(2000).addSequence(ts1, "preset 1").addSequence(blobScene, "preset 1"), 5);
    switcher.bindSequence("tunnel_1_blob_pulse_2", new ChainSequence(2000).addSequence(ts1, "preset 1").addSequence(blobScene, "preset 2"), 5);
    switcher.bindSequence("tunnel_1_blob_pulse_1", new ChainSequence(2000).addSequence(ts1, "preset 1").addSequence(blobScene, "preset 3"), 5);
    switcher.bindSequence("tunnel_1_blob_wobble_1", new ChainSequence(2000).addSequence(ts1, "preset 3").addSequence(blobScene, "preset 3"), 5);
    switcher.bindSequence("tunnel_1_blob_wobble_2", new ChainSequence(2000).addSequence(ts1, "preset 2").addSequence(blendScene, "preset 1"), 25);
    /*switcher.bindSequence(
        	"tunnel_2_pulse",     
        	*/
    TunnelScene ts2 = (TunnelScene) this.addSceneInputOutputCanvas(
	    		new TunnelScene(this, w, h).setCanvas("temp", "/temp3")
	    			//.addFilter(new BlendDrawer()))
	    		
	    		, "/out", "/out"
	);
    switcher.bindSequence("tunnel_2_plasma_pulse_1", new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(plasmaScene, "preset 1"), 5);
    switcher.bindSequence("tunnel_2_plasma_pulse_2", new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(plasmaScene, "preset 2"), 5);
    switcher.bindSequence("tunnel_2_blob_pulse_1",   new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(blobScene2, "preset 1"), 5);
    switcher.bindSequence("tunnel_2_blob_pulse_2",   new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(blobScene2, "preset 2"), 5);
    switcher.bindSequence("tunnel_2_double_pulse_1", new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(doubleSequence), 5);
    switcher.bindSequence("tunnel_2_blend_pulse_1",  new ChainSequence(2000).addSequence(ts2, "preset 1").addSequence(blendScene, "preset 1"), 10);
    switcher.bindSequence("tunnel_2_blob_wobble_1",  new ChainSequence(2000).addSequence(ts2, "preset 2").addSequence(blobScene, "preset 1"), 25);
    switcher.bindSequence("tunnel_2_blob_wobble_2",  new ChainSequence(2000).addSequence(ts2, "preset 2").addSequence(blobScene, "preset 3"), 25);
    switcher.bindSequence("tunnel_2_blob_wobble_3_fade", new ChainSequence(2000).addSequence(ts2, "preset 3").addSequence(blobScene, "preset 4").addSequence(getSceneForPath("/sc/BlankerScene"), "fade"), 50);
    
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
    
    
    //LIST OF THINGS TO PERMUTE
    // fade, show_kaleido, show_feedback
    // 
    
    

    /*SimpleScene bs = new SimpleScene(this,w,h);
    BlobDrawer bd = (BlobDrawer) new BlobDrawer(bs);//.setOutputCanvas("/out");
    bd.loadSVG(APP.getApp().dataPath("image-sources/reindeer.svg"));
    bd.setColour(255, 128, 255);
    bd.changeParameterValue("shape", Blob.SH_SVG);
    bs.addFilter(bd);
    this.addSceneOutputCanvas(bs,"/out");*/
    
 

    
    this.addSceneOutputCanvas(
    	      new VideoScene(this,w,h,"").setCanvas("src","/out").setCanvas("out", "/out"), //,"video/129-Probe 7 - Over and Out(1)-05.mkv"),
      		//new WebcamScene(this, 640, 480, w, h).setCanvas("src","/out").setCanvas("out", "/pix1"),
    	      //buffers[BUF_INP0]
    	      "/pix0"
      );       
    
    
    this.addSceneOutputCanvas(
      new DebugScene(this,w,h),
      "/out"
    );
    
    //in.loop();
    
    return true;
  }
  
  
}
