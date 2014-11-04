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
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.SceneSequencer;
import vurfeclipse.streams.*;
import vurfeclipse.user.scenes.BlenderFX1;
import vurfeclipse.user.scenes.BlobFX1;
import vurfeclipse.user.scenes.OutputFX1;

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
    
    //addCanvas("/blendresult", Canvas.makeCanvas(w,h,gfx_mode,"temp2"));
    
    return false;
  }
  
  public boolean setupStreams () {
    BeatStream beatStream = new BeatStream("Beat Stream", 120.0f, APP.getApp().millis());
    this.addStream("beat", beatStream);
       
    return true;
  }
  
  public boolean setupSequencer() {
	  this.sequencer = new SceneSequencer(this,w,h);
	  return true;
  }
  
  public boolean setupScenes () {      
	  
	this.addBlankerScene("/out");	  
	  
	/// INPUT SCENES
	  
    final SimpleScene ils1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene1");//.setOutputBuffer(getCanvas("inp0").surf);
    final SimpleScene ils2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene2");//.setOutputBuffer(getCanvas("inp1").surf);
    
    ils1.addFilter(new ImageListDrawer(ils1).setDirectory("doctea").setCurrentIndex(5).setNumBlobs(10/*200*/).setFilterName("ImageListDrawer1"));
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
      //"/pix1"
      "/temp1"
    );
    
    // MIDDLE LAYER: FX

    
    // SWITCHER  //////////////////////////
    //final SwitcherScene switcher = (SwitcherScene) this.addSceneOutputCanvas(new SwitcherScene(this, w, h), "/out");
    SceneSequencer switcher = (SceneSequencer)sequencer;
        
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
    
    final Scene blendScene = new BlenderFX1(this,"pix1 BlenderFX", w,h).setOutputCanvas("/out").setInputCanvas("/pix1");
    //blendScene.setCanvas("pix0","/pix0");
    //blendScene.setCanvas("pix1","/pix1");
    //switcher.bindScene("blend scene", "preset 1", blendScene);
    
    
    // BLOB SPIRAL SCENE
    final Scene blobScene =  switcher.bindScene("blob drawer",   "preset 1", new BlobFX1(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out").setInputCanvas("/pix0"));
    final Scene blobScene2 = switcher.bindScene("blob drawer 2", "preset 2", new BlobFX1(this,w,h).setSceneName("BlobScene2").setOutputCanvas("/out"));
    final Scene blobScene3 = switcher.bindScene("blob drawer 3", "preset 3", new BlobFX1(this,w,h).setSceneName("BlobScene3").setOutputCanvas("/out").setInputCanvas("/pix0"));
    final Scene blobScene4 = switcher.bindScene("blob drawer 4", "preset 4", new BlobFX1(this,w,h).setSceneName("BlobScene4").setOutputCanvas("/out"));    
    
    
    // event listener to switch the switcher.
    /*getStream("beat").registerEventListener("bar_1", new ParameterCallback() {
    	 public void call (Object value) {
    		 if (switcher.readyToChange(2)) switcher.randomScene();
    	 }
    });*/

    // OUTPUT FILTERS
    
    PlasmaScene plasmaScene = (PlasmaScene)(new PlasmaScene(this,w,h).setSceneName("PlasmaScene"));
    plasmaScene.setCanvas("out", "/out");
    
    //plasmaScene.setupFilters();
    
    plasmaScene.registerCallbackPreset(getStream("beat"), "beat_8", "warp");
    //this.addSceneOutputCanvas(plasmaScene, "/out");
    switcher.bindScene("plasma", "preset 1", plasmaScene);
    
    
    /// END PLASMA SCENE
    
        

    this.addSceneInputOutputCanvas(
      //os,
      new OutputFX1(this,w,h).setSceneName("OutputShader"),
      "/out",
      "/out"
    );    

    
    
    // OUTPUT FILTER 2
    final SimpleScene os2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("OutputShader2");    
    
    os2.setCanvas("out", "/out");
    os2.setCanvas("pix0","/pix0");
    //os2.setCanvas("blendresult", "/blendresult");

    os2.addFilter(new ShaderFilter(os2,"Feedback.xml").setFilterName("Feedback").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    //os.addFilter(new ShaderFilter(os,"CrossHatch.xml").setFilterName("CrossHatch").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    

    os2.addFilter(new KaleidoFilter(os2).setFilterName("Kaleido").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("out"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    //os.addFilter(new GLColourFilter(os).setFilterName("GLColourFilter"));

    os2.addFilter(new BlendDrawer(os2).setFilterName("BlendDrawer pix0 to out").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("pix0")).setParameterValue("BlendMode",8));
    //os.addFilter(new BlendDrawer(os).setFilterName("BlendDrawer inp0 to out").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0")).setParameterValue("BlendMode",9));
    
    this.addSceneInputOutputCanvas(
      os2,
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
    

    /*SimpleScene bs = new SimpleScene(this,w,h);
    BlobDrawer bd = (BlobDrawer) new BlobDrawer(bs);//.setOutputCanvas("/out");
    bd.loadSVG(APP.getApp().dataPath("image-sources/reindeer.svg"));
    bd.setColour(255, 128, 255);
    bd.changeParameterValue("shape", Blob.SH_SVG);
    bs.addFilter(bd);
    this.addSceneOutputCanvas(bs,"/out");*/
    
    
    this.addSceneOutputCanvas(
      new DebugScene(this,w,h),
      "/out"
    );
    
    //in.loop();
    
    return true;
  }
  
  
}