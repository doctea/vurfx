package vurfeclipse.user.projects;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;

import java.io.Serializable;
import java.util.*;

import processing.core.PApplet;
import vurfeclipse.filters.*;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.SceneSequencer;
import vurfeclipse.streams.*;
import vurfeclipse.user.scenes.OutputFX1;

public class AboveBoardsProject extends Project implements Serializable {
  
  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");
  
  public AboveBoardsProject(int w, int h, String gfx_mode) {
    super(w,h,gfx_mode);
  }
  
  public boolean initialiseBuffers() {
    addCanvas("/out",   Canvas.makeCanvas(w,h,gfx_mode,"output"));
    addCanvas("/inp0",  Canvas.makeCanvas(w,h,gfx_mode,"input1"));
    addCanvas("/inp1",  Canvas.makeCanvas(w,h,gfx_mode,"input2"));
    addCanvas("/temp1", Canvas.makeCanvas(w,h,gfx_mode,"temp1"));
    addCanvas("/temp2", Canvas.makeCanvas(w,h,gfx_mode,"temp2"));
    
    return false;
  }
  
  public boolean setupStreams () {
    BeatStream beatStream = new BeatStream("Beat Stream", 120.0f, APP.getApp().millis());
    this.addStream("beat", beatStream);
       
    return true;
  }
  
  public boolean setupScenes () {      
	  
	/// INPUT SCENES
	  
    final SimpleScene ils1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene1");//.setOutputBuffer(getCanvas("inp0").surf);
    final SimpleScene ils2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene2");//.setOutputBuffer(getCanvas("inp1").surf);
    
    ils1.addFilter(new ImageListDrawer(ils1).setFileList("aboveboards-1.lst").setCurrentIndex(5).setNumBlobs(200).setFilterName("ImageListDrawer1"));
    //ils1.addFilter(new ImageListDrawer(ils1).setFileList("doctea.lst").setCurrentIndex(5).setNumBlobs(200).setFilterName("ImageListDrawer1"));
    //ils1.addFilter(new MirrorFilter(ils1).setParameterValue("mirror_y", true)).setInputCanvas("/inp0");
    ils2.addFilter(new ImageListDrawer(ils2).setFileList("aboveboards-2.lst").setNumBlobs(200).setFilterName("ImageListDrawer2"));
    //ils2.addFilter(new ImageListDrawer(ils2).setFileList("doctea.lst").setCurrentIndex(5).setNumBlobs(200).setFilterName("ImageListDrawer2"));
    //ils2.addFilter(new MirrorFilter(ils2).setParameterValue("mirror_y", true)).setInputCanvas("/inp1");
 
    /*getStream("beat").registerEventListener("bar_1", 
      new ParameterCallback () {
        public void call(Object value) {
          ils1.getFilter(0).nextMode();
        }
      }
    );*/    
    this.addSceneOutputCanvas(
      ils1,
      "/inp0"
    );
    this.addSceneOutputCanvas(
      ils2,
      "/inp1"
    );
    
    
    // MIDDLE LAYER: FX

    Scene textFlashScene = this.addSceneInputOutputCanvas(
      new TextFlashScene(this,w,h, new String[] {
        //"Nozstock", "Nozstock: the Hidden Valley",
    		 //"社会スキ",
    		  //"社会", "スキ", 
    		  //"સામાજિક",
    		  /*"החברתי סוקי",
    		   "סוקי",*/
        "Above Boars",
        "BHG",
        "Boars Head"
      }).setFonts(new String[] { "LucidaSansUnicode-96.vlw" })/*.setFonts(new String[] {
    	"Caveman-128.vlw", "Dinosaur-512.vlw", "DinosBeeline-512.vlw", "LostWorld-128.vlw", "DinosaurJrPlane-256.vlw", "DinosaurSkin-128.vlw"
      })  */    
        .setSceneName("TextFlash")
        //.registerCallbackPreset("beatIII","beat_4","cycle")
        //.registerCallbackPreset("beatIII","beat_2","rotate"),
        ,
      "/out",
      "/out"
    );
    textFlashScene.setMuted(true);
    
    
    SimpleScene bd = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out");
    bd.addFilter(new BlobDrawer(bd)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer").setCanvases(bd.getCanvasMapping("temp2"),bd.getCanvasMapping("inp0")));
    bd.addFilter(new BlobDrawer(bd)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer2").changeParameterValue("shape", 2).setCanvases(bd.getCanvasMapping("temp3"),bd.getCanvasMapping("temp2")));
    bd.addFilter(new BlendDrawer(bd).setFilterName("BlendDrawer").setCanvases(bd.getCanvasMapping("out"),bd.getCanvasMapping("temp2")));
    bd.addFilter(new BlendDrawer(bd).setFilterName("BlendDrawer2").setCanvases(bd.getCanvasMapping("out"),bd.getCanvasMapping("temp3")));
    bd.setMuted(true);
    this.addSceneInputOutputCanvas(
      bd,
      "/out",
      "/out"
    );    
    
    SimpleScene bd2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out");
    bd2.setCanvas("inp0", "/inp0");
    bd2.addFilter(new BlendDrawer(bd2).setFilterName("BlendBG").setCanvases(bd2.getCanvasMapping("temp2"),bd2.getCanvasMapping("inp1")));
    bd2.addFilter(new BlobDrawer(bd2)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer").setCanvases(bd2.getCanvasMapping("temp2"),bd2.getCanvasMapping("inp0")));
    bd2.addFilter(new BlobDrawer(bd2)/*.setImage("ds2014/dseye.png")*/.setFilterName("BlobDrawer2").changeParameterValue("shape", 2).setCanvases(bd2.getCanvasMapping("temp3"),bd.getCanvasMapping("inp1")));
    bd2.addFilter(new BlendDrawer(bd2).setFilterName("BlendDrawer").setCanvases(bd2.getCanvasMapping("out"),bd2.getCanvasMapping("temp2")));
    bd2.addFilter(new BlendDrawer(bd2).setFilterName("BlendDrawer2").setCanvases(bd.getCanvasMapping("out"),bd2.getCanvasMapping("temp3")));
    bd2.setMuted(true);
    this.addSceneInputOutputCanvas(
      bd2,
      "/out",
      "/out"
    );        
    
    /*getStream("beat").registerEventListener("beat_32", new ParameterCallback () {
      public void call (Object value) {
        int v = (Integer)value * 100;
        //System.out.println("called " + v);        
        if ((v/10)%10>5) {
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("totalRotate", PApplet.radians(v/10));
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("rotation", -PApplet.radians(v/10/2));
        } else {
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("totalRotate", -PApplet.radians(v/10));
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("rotation", PApplet.radians(v/10/2));
        }          
        
        if((v/10)%4==0) {
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValueFromSin("zRotate", PApplet.radians(v));
        }        
      } 
    });*/
    
    final SimpleScene bl1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlendScene");
    bl1.setOutputCanvas("/out");
    bl1.addFilter(new PlainDrawer(bl1).setInputCanvas("/inp0"));
    bl1.addFilter(new BlendDrawer(bl1).setFilterName("BlendDrawer1").setInputCanvas("/inp1"));
    bl1.addFilter(new MirrorFilter(ils2).setFilterName("Mirror").changeParameterValue("mirror_y", true)).setInputCanvas(bd.getCanvasMapping("out"));    
    bl1.setMuted(true);
    this.addSceneOutputCanvas(bl1,"/out");//getCanvas("/out"));
    
    

    // SWITCHER  //////////////////////////
    
    //final SwitcherScene switcher = (SwitcherScene) this.addSceneOutputCanvas(new SwitcherScene(this, w, h), "/out");
    final SceneSequencer switcher = (SceneSequencer)sequencer;
    
    // BLEND SCENE
    final Scene blendScene = switcher.addScene("blend scene", bl1);
    switcher.bindSequence("blend scene", new Sequence(5000) {
    	public void setValuesForNorm(double norm, int iteration) {
    		if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
    		switcher.getScene("blend scene").getFilter("BlendDrawer1").setParameterValue("Opacity", (float)norm);
    	}
		@Override public void onStart() {
			for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
				getSceneForPath("/ImageListScene1").getFilter("ImageListDrawer1").nextMode();
			for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
				getSceneForPath("/ImageListScene2").getFilter("ImageListDrawer2").nextMode();
		}
    });
    
    // TEXT FLASH SCENE
    final Scene textScene = switcher.addScene("Text Flash Scene", textFlashScene);
    switcher.bindSequence("Text Flash Scene", new Sequence(500) {
    	public void setValuesForNorm(double norm, int iteration) {
    		//((Filter)switcher.getObjectForPath("~Text Flash Scene/TextDrawer")).changeParameterValue("text", ""+norm);
    		//switcher.getScene("Text Flash Scene").getFilter("TextDrawer").changeParameterValue("text", iteration+":"+(int)(norm*100));
    		//((TextFlashScene)switcher.getScene("Text Flash Scene")).randomWord(); //getFilter("TextDrawer")).
    		((TextFlashScene)switcher.getScene("Text Flash Scene")).setWordByNorm(norm); //getFilter("TextDrawer")).
    		//System.out.println("did set text " + norm);
    	}
    	public void onStart() {}
    });
    
    
    // BLOB SPIRAL SCENE
    final Scene blobScene = switcher.addScene("blob drawer", bd);    
    switcher.bindSequence("blob drawer", new Sequence(2000) {
    	int colour1, colour2, colour3, colour4;    	
    	public void setValuesForNorm(double norm, int iteration) {
    		//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
    		double inv_norm = (iteration%2==0) ? norm : APP.getApp().constrain((float)(1.0f-norm),0.0f,1.0f); //1.0f-norm : norm;// : norm;
    		//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);

    		//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;
    		
    		switcher.getScene("blob drawer").getFilter("BlobDrawer").setParameterValue("totalRotate", (float)norm*360.0f); //PApplet.radians((float)norm*360));
    		switcher.getScene("blob drawer").getFilter("BlobDrawer").setParameterValue("rotation", (float)-norm*180.0f);
    		switcher.getScene("blob drawer").getFilter("BlobDrawer2")
    			.setParameterValue("totalRotate", (float)norm*360.0f) // was 720
    			.setParameterValueFromSin("radius", APP.getApp().sin((float)(inv_norm))) ///2f)))
    		;
    		
    		switcher.getScene("blob drawer").getFilter("BlobDrawer2").setParameterValueFromSin("numofCircles", /*APP.getApp().sin(*/(float)inv_norm/*)*/); //0.2f+APP.getApp().sin(iteration_warp/2));
    		
    		//if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
    		//norm = inv_norm;
    			
    		//((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer")).setColour((int)(norm*255.0f)+64,(int)(255.0f-(inv_norm*200.0f))+64,(int)(norm*255.0f)+64, 255);//,255);
    		//((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer2")).setColour((int)(282-(inv_norm*255.0f)),(int)(norm*255.0f)+32,(int)(282-(norm*255.0f)), 255);//,255);
    		//((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer")).nextMode();
    		
    		int col1 = lerpcolour(colour1, colour2, inv_norm);
    		int col2 = lerpcolour(colour3, colour4, inv_norm);
    		
    		((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer")).setColour(
    				(int)APP.getApp().red(col1),
    				(int)APP.getApp().green(col1),
    				(int)APP.getApp().blue(col1));
    				/*(col1 >> 24) & 0xFF,
    				(col1 >> 16) & 0xFF,
    				(col1 >> 8) & 0xFF,*
    				255);
    		/*
    				(int)(inv_norm*128.0f)+128,
    				(int)((1.0f-inv_norm)*128.0f)+128,
    				(int)(1.0f-inv_norm*128.0f)+128,    						
    				//(int)(255.0f-(inv_norm*200.0f))+64,
    				//(int)(norm*255.0f)+64, 
    		255);//,255);*/
    		((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer2")).setColour(
    				(int)APP.getApp().red(col2),
    				(int)APP.getApp().green(col2),
    				(int)APP.getApp().blue(col2));
    		/*
    				(int)((1.0f-inv_norm)*128.0f)+128,
    				(int)(inv_norm*128.0f)+128,
    				(int)(inv_norm*128.0f)+128,   						
    		255);//,255);*/


    		
            //((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("totalRotate", (float)norm*360.0f); //PApplet.radians((float)norm*360));
            //((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("rotation", -PApplet.radians((float)-norm*180.0f)); //v/10/2));
    		
    		
    	}
    	public void onStart() {
    		System.out.println("onStart()!");
    		colour1 = randomColorMinimum(196);// APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
    		colour2 = randomColorMinimum(96);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
    		colour3 = randomColorMinimum(96); //APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
    		colour4 = randomColorMinimum(196); //APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
    		/*colour1 = (int) APP.getApp().random(Integer.MAX_VALUE);//APP.getApp().color(255, 255, 0, 255); //255(int) APP.getApp().random(2^32);
    		colour2 = (int) APP.getApp().random(Integer.MAX_VALUE);//APP.getApp().color(255, 0, 0, 255); //255(int) APP.getApp().random(2^32);
    		colour3 = (int) APP.getApp().random(Integer.MAX_VALUE);//APP.getApp().color(0, 255, 0, 255); //255(int) APP.getApp().random(2^32);
    		colour4 = (int) APP.getApp().random(Integer.MAX_VALUE);//APP.getApp().color(255, 255, 0, 255); //255(int) APP.getApp().random(2^32);*/
    		
    		((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { 0, 1, 2, 3, /*4, 5, 6, 7 */} ));
    		((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { 0, 1, 2, 3, 4, /*5, 6,*/ 7 } ));
    		
    		((BlendDrawer)switcher.getScene("blob drawer").getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { /*2, 4, 8, */4} ));
    		//((BlendDrawer)switcher.getScene("blob drawer").getFilter("BlendDrawer")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 3, 7, 12, 0, 1 } ));
    		//((BlendDrawer)switcher.getScene("blob drawer").getFilter("BlendDrawer")).nextMode();
    		((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",APP.getApp().random(0f,1f));
    		((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer2")).setParameterValueFromSin("yRadianMod",APP.getApp().random(0f,1f));
    		
    		if (APP.getApp().random(0f,1.0f)>=0.5f) switcher.host
    			.getSceneForPath("/OutputShader")
    			.getFilter("Toon").toggleMute();
    		//switcher.host.getSceneForPath("/OutputShader").getFilter("pulsatingEmboss").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
    		//switcher.host.getSceneForPath("/OutputShader").getFilter("CrossHatch").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
    		if (APP.getApp().random(0f,1.0f)>=0.5f) switcher.host.getSceneForPath("/OutputShader").getFilter("Edges").toggleMute();
    		if (APP.getApp().random(0f,1.0f)>=0.5f) switcher.host.getSceneForPath("/OutputShader2").getFilter("Feedback").toggleMute();
    		if (APP.getApp().random(0f,1.0f)>=0.5f) switcher.host.getSceneForPath("/OutputShader2").getFilter("Kaleido").toggleMute(); 
    		switcher.host.getSceneForPath("/OutputShader2").getFilter("Kaleido").nextMode();    		
    	}
    });
    

    final Scene blobScene2 = switcher.addScene("blob drawer 2", bd2);    
    switcher.bindSequence("blob drawer 2", new Sequence(1000) {
    	int colour1, colour2, colour3, colour4;    	
    	public void setValuesForNorm(double norm, int iteration) {
    		//double inv_norm = (iteration%2==0) ? 1.0f-norm : norm;
    		double inv_norm = (iteration%2==0) ? norm : APP.getApp().constrain((float)(1.0f-norm),0.0f,1.0f); //1.0f-norm : norm;// : norm;
    		//System.out.println("norm: " + norm + ", inv_norm: " + inv_norm);

    		//float iteration_warp = (float)(1.0f/(float)iteration)*(float)norm;
    		
    		switcher.getScene("blob drawer 2").getFilter("BlobDrawer").setParameterValue("totalRotate", (float)-norm*360.0f); //PApplet.radians((float)norm*360));
    		switcher.getScene("blob drawer 2").getFilter("BlobDrawer").setParameterValue("rotation", (float)-norm*180.0f);
    		switcher.getScene("blob drawer 2").getFilter("BlobDrawer2")
    			.setParameterValue("totalRotate", (float)norm*180.0f) // was 720
    			.setParameterValueFromSin("radius", APP.getApp().sin((float)(inv_norm))) ///2f)))
    		;
    		
    		//switcher.getScene("blob drawer").getFilter("BlobDrawer2").setParameterValueFromSin("numofCircles", /*APP.getApp().sin(*/(float)inv_norm/*)*/); //0.2f+APP.getApp().sin(iteration_warp/2));
    		
    		//if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
    		//norm = inv_norm;
    			
    		//((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer")).setColour((int)(norm*255.0f)+64,(int)(255.0f-(inv_norm*200.0f))+64,(int)(norm*255.0f)+64, 255);//,255);
    		//((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer2")).setColour((int)(282-(inv_norm*255.0f)),(int)(norm*255.0f)+32,(int)(282-(norm*255.0f)), 255);//,255);
    		//((BlobDrawer)switcher.getScene("blob drawer").getFilter("BlobDrawer")).nextMode();
    		
    		int col1 = lerpcolour(colour1, colour2, inv_norm);
    		int col2 = lerpcolour(colour3, colour4, inv_norm);
    		
    		((BlobDrawer)switcher.getScene("blob drawer 2").getFilter("BlobDrawer")).setColour(
    				(int)APP.getApp().red(col1),
    				(int)APP.getApp().green(col1),
    				(int)APP.getApp().blue(col1));
    				/*(col1 >> 24) & 0xFF,
    				(col1 >> 16) & 0xFF,
    				(col1 >> 8) & 0xFF,*
    				255);
    		/*
    				(int)(inv_norm*128.0f)+128,
    				(int)((1.0f-inv_norm)*128.0f)+128,
    				(int)(1.0f-inv_norm*128.0f)+128,    						
    				//(int)(255.0f-(inv_norm*200.0f))+64,
    				//(int)(norm*255.0f)+64, 
    		255);//,255);*/
    		((BlobDrawer)switcher.getScene("blob drawer 2").getFilter("BlobDrawer2")).setColour(
    				(int)APP.getApp().red(col2),
    				(int)APP.getApp().green(col2),
    				(int)APP.getApp().blue(col2));
    		/*
    				(int)((1.0f-inv_norm)*128.0f)+128,
    				(int)(inv_norm*128.0f)+128,
    				(int)(inv_norm*128.0f)+128,   						
    		255);//,255);*/


    		
            //((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("totalRotate", (float)norm*360.0f); //PApplet.radians((float)norm*360));
            //((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("rotation", -PApplet.radians((float)-norm*180.0f)); //v/10/2));
    		
    		
    	}
    	public void onStart() {
    		System.out.println("onStart()!");
    		colour1 = randomColorMinimum(196);// APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
    		colour2 = randomColorMinimum(96);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
    		colour3 = randomColorMinimum(96); //APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
    		colour4 = randomColorMinimum(196); //APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
    		/*colour1 = (int) APP.getApp().random(Integer.MAX_VALUE);//APP.getApp().color(255, 255, 0, 255); //255(int) APP.getApp().random(2^32);
    		colour2 = (int) APP.getApp().random(Integer.MAX_VALUE);//APP.getApp().color(255, 0, 0, 255); //255(int) APP.getApp().random(2^32);
    		colour3 = (int) APP.getApp().random(Integer.MAX_VALUE);//APP.getApp().color(0, 255, 0, 255); //255(int) APP.getApp().random(2^32);
    		colour4 = (int) APP.getApp().random(Integer.MAX_VALUE);//APP.getApp().color(255, 255, 0, 255); //255(int) APP.getApp().random(2^32);*/
    		
    		this.setLengthMillis(500 * (int)(APP.getApp().random(1,10)));
    		
    		((BlobDrawer)switcher.getScene("blob drawer 2").getFilter("BlobDrawer")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { 0, 1, /*2, 3,*/ 4, 5/*, 6, 7 */} ));
    		((BlobDrawer)switcher.getScene("blob drawer 2").getFilter("BlobDrawer2")).changeParameterValue("shape",getRandomArrayElement(new Integer[] { 0, 1, /*2, 3,*/ 4, 5/*, 6, 7*/ } ));
    		
    		((BlendDrawer)switcher.getScene("blob drawer 2").getFilter("BlendDrawer2")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 2, /*, 4,*/ 8, /*, */4} ));
    		//((BlendDrawer)switcher.getScene("blob drawer").getFilter("BlendDrawer")).changeParameterValue("BlendMode",getRandomArrayElement(new Integer[] { 3, 7, 12, 0, 1 } ));
    		//((BlendDrawer)switcher.getScene("blob drawer").getFilter("BlendDrawer")).nextMode();
    		((BlobDrawer)switcher.getScene("blob drawer 2").getFilter("BlobDrawer2")).setParameterValueFromSin("xRadianMod",APP.getApp().random(0f,1f));
    		//((BlobDrawer)switcher.getScene("blob drawer 2").getFilter("BlobDrawer2")).setParameterValueFromSin("yRadianMod",APP.getApp().random(0f,1f));
    		
    		if (APP.getApp().random(0f,1.0f)>=0.5f) switcher.host
    			.getSceneForPath("/OutputShader")
    			.getFilter("Toon").toggleMute();
    		//switcher.host.getSceneForPath("/OutputShader").getFilter("pulsatingEmboss").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
    		//switcher.host.getSceneForPath("/OutputShader").getFilter("CrossHatch").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
    		if (APP.getApp().random(0f,1.0f)>=0.5f) switcher.host.getSceneForPath("/OutputShader").getFilter("Edges").toggleMute();
    		if (APP.getApp().random(0f,1.0f)>=0.5f) switcher.host.getSceneForPath("/OutputShader2").getFilter("Feedback").toggleMute();
    		if (APP.getApp().random(0f,1.0f)>=0.5f) switcher.host.getSceneForPath("/OutputShader2").getFilter("Kaleido").toggleMute(); 
    		//switcher.host.getSceneForPath("/OutputShader2").getFilter("Kaleido").nextMode();    		
    	}
    });    
    
    
    // event listener to switch the switcher.
    /*getStream("beat").registerEventListener("bar_1", new ParameterCallback() {
    	 public void call (Object value) {
    		 if (switcher.readyToChange(2)) switcher.randomScene();
    	 }
    });*/

    
    // OUTPUT FILTERS
    
    /*SimpleScene os = (SimpleScene) new SimpleScene(this,w,h).setSceneName("OutputShader");    
    
    os.setCanvas("out", "/out");
    os.setCanvas("inp0","/out");
    //os.setCanvas("blank","/blank");
    //os.addFilter(new PlainDrawer(os).setFilterName("BlankDrawer").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("blank")));

    //os.addFilter(new BlankFilter(os).setOutputCanvas("/out"));    
    

    os.addFilter(new ShaderFilter(os,"Edges.xml").setFilterName("Edges").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    os.addFilter(new ShaderFilter(os,"Toon.xml").setFilterName("Toon").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    //os.addFilter(new ShaderFilter(os,"pulsatingEmboss.xml").setFilterName("pulsatingEmboss").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    */
    this.addSceneInputOutputCanvas(
      //os,
      new OutputFX1(this,w,h).setSceneName("OutputShader"),
      "/out",
      "/out"
    );    

    
    // OUTPUT FILTER 2
    SimpleScene os2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("OutputShader2");    
    
    os2.setCanvas("out", "/out");
    os2.setCanvas("inp0","/out");

    os2.addFilter(new ShaderFilter(os2,"Feedback.xml").setFilterName("Feedback").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    //os.addFilter(new ShaderFilter(os,"CrossHatch.xml").setFilterName("CrossHatch").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    

    os2.addFilter(new KaleidoFilter(os2).setFilterName("Kaleido").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("out"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    //os.addFilter(new GLColourFilter(os).setFilterName("GLColourFilter"));
    
    
    //os.addFilter(new BlendDrawer(os).setFilterName("BlendDrawer inp0 to out").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0")).setParameterValue("BlendMode",9));
    //os.addFilter(new BlendDrawer(os).setFilterName("BlendDrawer inp0 to out").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0")).setParameterValue("BlendMode",9));
        
    this.addSceneInputOutputCanvas(
      os2,
      "/out",
      "/out"
    );    
    
    
    
    this.addSceneOutputCanvas(
      new DebugScene(this,w,h),
      "/out"
    );
    
    //in.loop();
    
    return true;
  }
  
  
}
