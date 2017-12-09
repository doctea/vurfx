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
import vurfeclipse.streams.*;

public class SocioSukiVideo2Project extends Project implements Serializable {
  
  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");
  
  boolean enableSequencer = true;

public SocioSukiVideo2Project(int w, int h) {
    super(w,h);
  }
  
  /*public boolean initialiseBuffers() {
    buffers[BUF_OUT] = createGLBuffer(w,h,gfx_mode);  
    buffers[BUF_INP0] = createGLBuffer(w,h,gfx_mode);  
    buffers[BUF_INP1] = createGLBuffer(w,h,gfx_mode);  
    buffers[BUF_TEMP1] = createGLBuffer(w,h,gfx_mode);  
    buffers[BUF_TEMP2] = createGLBuffer(w,h,gfx_mode);  
    System.out.println(this + " BUF_OUT buffer is " + buffers[BUF_OUT]);
    System.out.println(this + " BUF_INP0 buffer is " + buffers[BUF_INP0]);
    return true;
  }*/
  
  public boolean initialiseBuffers() {
    addCanvas("/out",   Canvas.makeCanvas(w,h,gfx_mode,"output"));
    addCanvas("/inp0",  Canvas.makeCanvas(w,h,gfx_mode,"input1"));
    addCanvas("/inp1",  Canvas.makeCanvas(w,h,gfx_mode,"input2"));
    addCanvas("/temp1", Canvas.makeCanvas(w,h,gfx_mode,"temp1"));
    addCanvas("/temp2", Canvas.makeCanvas(w,h,gfx_mode,"temp2"));
    
    return false;
  }
  
  public boolean setupStreams () {
    Stream stream = new Stream("Test Stream");
    BeatStream beatStream = new BeatStream("Beat Stream", 120.0f, APP.getApp().millis());
    this.getSequencer().addStream("test", stream);
    this.getSequencer().addStream("beat", beatStream);
    
    BeatStream beatStream2 = new BeatStream("Beat Stream 2", 60.0f, APP.getApp().millis());    
    this.getSequencer().addStream("beatII", beatStream);

    BeatStream beatStream3= new BeatStream("Beat Stream 3", 90.0f, APP.getApp().millis());    
    this.getSequencer().addStream("beatIII", beatStream);    
    
    BeatStream beatStream4= new BeatStream("Beat Stream 4", 180.0f, APP.getApp().millis());    
    this.getSequencer().addStream("beatIV", beatStream);        

    BeatStream beatStreamMaster = new BeatStream("Beat Stream Master", 40.0f, APP.getApp().millis());    
    this.getSequencer().addStream("beatMaster", beatStream);    
    
    NumberStream numberStream = new NumberStream("Number Stream", 130.0f, 69.0f, APP.getApp().millis());
    this.getSequencer().addStream("number", numberStream);
    
    //MidiStream midiStream = new MidiStream("Midi Stream", true);
    //this.getSequencer().addStream("midi", midiStream);
    
    return true;
  }
  
  public boolean setupScenes () {  
    // need a way to specify which scenes are running (selected only at first?)
    // need a way to blend between scenes ...
    // need a way for scenes to write to a shared buffer .. 
      // set output buffer on Webcam Scene to a Project buffer 
      // set input buffer on following Scenes to that buffer
    
    //this.addScene(new WebcamScene(this,w,h,0));

    /*this.addSceneOutput(
      new WebcamScene(this,w,h,0),
      //buffers[BUF_INP0]
      getCanvas("inp0").surf
    );
    this.addSceneOutput(
      new WebcamScene(this,w,h,1),
      //buffers[BUF_INP1]
      getCanvas("inp1").surf
    );*/
    
    /*filters[++i] = new FilterChain(this, "Stored ImageListDrawer/PlainDrawer Chain (BUF_TEMP4 -> BUF_OUT)");
    final Filter ild = filters[i];
    //host.getStream("beat").registerEventListener("beat_32", new ParameterCallback() {
    //  public void call(Object value) {
    //    ild.nextMode();  
    //  }
    //});
    filters[i].setMute(true);
    filters[i].setBuffers(buffers[BUF_TEMP4], buffers[BUF_SRC]);
    ((FilterChain)filters[i]).addFilterDefaults(new ImageListDrawer(this, "files_4.lst"));
    ((FilterChain)filters[i]).addFilter(new PlainDrawer(this).setBuffers(buffers[BUF_OUT], buffers[BUF_TEMP4]));
    //filters[i].initialise();   
    */
    
    /*this.addSceneOutputCanvas(
      new WebcamScene(this,w,h,0),
      //buffers[BUF_INP0]
      //"/inp0"
      "/inp2"
    );*/    
    
    
    final SimpleScene ils1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene1");//.setOutputBuffer(getCanvas("inp0").surf);
    final SimpleScene ils2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene2");//.setOutputBuffer(getCanvas("inp1").surf);
    
    //ils1.addFilter(new ImageListDrawer(ils1).setFileList("files_ds2014_6.lst").setCurrentIndex(7).setNumBlobs(40).setFilterName("ImageListDrawer"));
    //ils2.addFilter(new ImageListDrawer(ils2).setFileList("files4.txt").setNumBlobs(40).setFilterName("ImageListDrawer")); 
    //ils1.addFilter(new ImageListDrawer(ils1).setFileList("files_wp2.lst").setCurrentIndex(7).setNumBlobs(200).setFilterName("ImageListDrawer"));
    //ils1.addFilter(new MirrorFilter(ils1).setFilterName("mirror").setInputCanvas(ils1.getCanvasMapping("out")).setOutputCanvas(ils1.getCanvasMapping("out")));
    //ils2.addFilter(new ImageListDrawer(ils2).setFileList("files_wp.lst").setNumBlobs(200).setFilterName("ImageListDrawer")); 
    //ils2.addFilter(new MirrorFilter(ils2).setFilterName("mirror").setInputCanvas(ils2.getCanvasMapping("out")).setOutputCanvas(ils2.getCanvasMapping("out")));

    VideoPlayer vp = new VideoPlayer(ils1, "");
    vp.loadDirectory("video-sources/");
    ils1.addFilter(vp);
    
    VideoPlayer vp2 = new VideoPlayer(ils2, "");
    vp2.loadDirectory("video-sources/");
    ils2.addFilter(vp2);

    
    /*getStream("midi").registerEventListener("note",
      new ParameterCallback () {
        public void call(Object value) {
          println("Callback got " + value + "!");
          ((Scene)getObjectForPath("/OutputShader")).toggleMute();
        }
      }
    );*/
    
    /*getStream("midi").registerEventListener("interval",
      new ParameterCallback () {
        public void call(Object value) {
          System.out.println("Callback got " + value + "!");
          //((Scene)getObjectForPath("/OutputShader")).toggleMute();
          scenes.get((Integer)value).toggleMute();
        }
      }
    );*/    
          
    this.getSequencer().getStream("beat").registerEventListener("bar_1", 
      new ParameterCallback () {
        public void call(Object value) {
          ils1.getFilter(0).nextMode();
        }
      }
    );    
    this.getSequencer().getStream("beatII").registerEventListener("bar_1", 
      new ParameterCallback () {
        public void call(Object value) {
          ils2.getFilter(0).nextMode();
        }
      }
    );        
        
    this.addSceneOutputCanvas(
      //new FilterChain(this, "Stored ImageListDrawer/PlainDrawer Chain"),
      //new ImageListDrawer(this, "files_3.list"),
      ils1,
      "/inp0"
    );
    this.addSceneOutputCanvas(
      //new FilterChain(this, "Stored ImageListDrawer/PlainDrawer Chain"),
      //new ImageListDrawer(this, "files_3.list"),
      ils2,
      "/inp1"
    );
    
    
    final SimpleScene bl1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlendScene");
    //bl1.setOutputBuffer(getCanvas("/out").surf);
    bl1.setOutputCanvas("/out");
    //bl1.addFilter(new PlainDrawer(bl1).setInputBuffer(getCanvas("/inp0").surf));
    bl1.addFilter(new PlainDrawer(bl1).setInputCanvas("/inp0"));
    //bl1.addFilter(new BlendDrawer(bl1).setInputBuffer(getCanvas("inp0").surf));
    //bl1.addFilter(new PlainDrawer(bl1).setInputBuffer(getCanvas("inp1").surf));
    //bl1.addFilter(new BlendDrawer(bl1).setInputBuffer(getCanvas("/inp1").surf));    
    bl1.addFilter(new BlendDrawer(bl1).setFilterName("BlendDrawer1").setInputCanvas("/inp1"));
    //bl1.addFilter(new PlainDrawer(bl1).setInputBuffer(getCanvas("inp0").surf));
    this.addSceneOutputCanvas(bl1,"/out");//getCanvas("/out"));
    

    /*final SimpleScene bl2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("WebCamBlendScene");
    //bl1.setOutputBuffer(getCanvas("/out").surf);
    bl2.setOutputCanvas("/out");
    //bl1.addFilter(new PlainDrawer(bl1).setInputBuffer(getCanvas("/inp0").surf));
    bl2.addFilter(new PlainDrawer(bl1).setInputCanvas("/inp2"));
    //bl1.addFilter(new BlendDrawer(bl1).setInputBuffer(getCanvas("inp0").surf));
    //bl1.addFilter(new PlainDrawer(bl1).setInputBuffer(getCanvas("inp1").surf));
    //bl1.addFilter(new BlendDrawer(bl1).setInputBuffer(getCanvas("/inp1").surf));    
    bl2.addFilter(new BlendDrawer(bl1).setFilterName("BlendDrawer1").setInputCanvas("/inp2"));
    //bl1.addFilter(new PlainDrawer(bl1).setInputBuffer(getCanvas("inp0").surf));
    this.addSceneOutputCanvas(bl2,"/out");//getCanvas("/out"));    */
    
    this.getSequencer().getStream("beatMaster").registerEventListener("bar_4",
      new ParameterCallback() {
        //int counter = 0;
        public void call(Object value) {            
            System.out.println("Changing speed..");
            //float bpm = 60 + (abs(sin((Integer)value%2)) * 300);
            ((BeatStream)getSequencer().getStream("beatII")).toggleBPM(); //setBPM(60);
            if ((Integer)value%2==0)
              ((BeatStream)getSequencer().getStream("beatIII")).toggleBPM(); //setBPM(60);            
            if ((Integer)value%3==0)              
              ((BeatStream)getSequencer().getStream("beat")).toggleBPM();
            //if ((Integer)value%2==0) ((BeatStream)getStream("beatII")).toggleBPM(); //setBPM(60);            
            //if ((Integer)value%4==0) ((BeatStream)getStream("beatIII")).setSpeed(APP.random(1.0,4.0)); //toggleBPM(); //setBPM(60);                        
            //System.out.println("setting BPM to " + bpm); //" + 60 + abs(sin((Integer)value))) * 300);
          }
        }
    );
    
        
    bl1.host.getSequencer().getStream("beatII").registerEventListener("beat_16",    
      new ParameterCallback() {
        int mode = 0;        
        public void call(Object value) {
          float v =(Integer)value/1000f;
          //float v2 = map(v,0.0,1.0f,0.2f,1.0f);
          //bl1.getFilter(0).setParameterValue("Opacity", map(abs(sin(v)),0.0f,1.0f,0.2f,0.8f));//(Float)value/1000.0f));
          //bl1.getFilter(1).setParameterValue("Opacity", 1.0-map(abs(sin(v)),0.0f,1.0f,0.0f,0.8f));//(Float)value/1000.0f));
          bl1.getFilter(1).setParameterValue("Opacity", 1.0f-PApplet.map(PApplet.abs(PApplet.sin(v)),0.0f,1.0f,0.0f,0.8f));//(Float)value/1000.0f));
          
          //bl1.getFilter(0).setParameterValueFromSin("tint",abs(sin(v)));
          //bl1.getFilter(1).setParameterValueFromSin("tint",1.0-abs(sin(v)));
          //bl1.getFilter(2).setParameterValueFromSin("tint",1.0-abs(sin(v/2)));
          
          //bl1.getFilter(0).setParameterValueFromSin("rotation", abs(sin(v)));
          
          //ils1.getFilter(0).setParameterValueFromSin("translate_x", abs(sin(v)));
          //ils1.getFilter(0).setParameterValueFromSin("scale", PApplet.abs(PApplet.sin(v)));
          //ils2.getFilter(0).setParameterValueFromSin("scale", 1.0f-PApplet.abs(PApplet.sin(v)));         
           
           
          mode = ((int)v/1000)%4;
          try {
	          GLColourFilter cf = ((GLColourFilter)getObjectForPath("/OutputShader/GLColourFilter")); 
	          if (mode==0) cf.setRedGreenBlueShift(PApplet.abs(PApplet.sin(v/10)), 1.0f, 1.0f);
	          if (mode==1) cf.setRedGreenBlueShift(1.0f, PApplet.abs(PApplet.sin(v/10)), 1.0f);        
	          if (mode==2) cf.setRedGreenBlueShift(1.0f, 1.0f, PApplet.abs(PApplet.sin(v/10)));        
	          if (mode==3) cf.setRedGreenBlueShift(1.0f, 1.0f, 1.0f); //abs(sin(v/10)));
          } catch (Exception e) {
        	  System.out.println("Caught exception " + e + " in rgb shifter snippet");
          }
    
          try {
	          if ((v/10)%2==0) {
	            ((Filter)getObjectForPath("/TextFlash/TextDrawer")).setParameterValueFromSin("zrotation", PApplet.sin(PApplet.radians(v%360)));
	          } else {
	            ((Filter)getObjectForPath("/TextFlash/TextDrawer")).setParameterValue("zrotation", 0); 
	          }
          } catch (Exception e) {
        	  System.out.println("Caught exception " + e + " in textdrawer rotator snippet");
          }
          
          try {
	          if ((v/10)%3==0) {
	            ((Filter)getObjectForPath("/SpiralScene2/SpiralDrawer")).setParameterValueFromSin("zRotate", PApplet.sin(v%360));
	          } else {
	            ((Filter)getObjectForPath("/SpiralScene2/SpiralDrawer")).setParameterValue("zRotate", 0.0f); 
	          }
          } catch (Exception e) {
        	  System.out.println("Caught exception " + e + " in spiraldrawer rotator snippet");
          }        
          
        }
      }
    );
    
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
    
    SimpleScene ss = (SimpleScene) new SimpleScene(this,w,h).setSceneName("Kaleidoscope");
    /*ss.setBuffer(ss.BUF_SRC, buffers[BUF_INP1]);
    ss.setBuffer(ss.BUF_OUT, buffers[BUF_OUT]);*/
    //ss.setBuffer(ss.BUF_SRC, getCanvas("/out").surf); //buffers[BUF_INP1]);
    //ss.setBuffer(ss.BUF_OUT, getCanvas("/out").surf); //buffers[BUF_OUT]);    
    ss.setCanvas("out", "/out");
    ss.setCanvas("inp0","/inp0");
    ss.addFilter(new PlainDrawer(ss).setFilterName("PlainDrawer").setCanvases(ss.getCanvasMapping("out"), ss.getCanvasMapping("inp0"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    //ss.addFilter(new BlankFilter(ss).setOutputBuffer(ss.buffers[ss.BUF_OUT]));
    //ss.addFilter(new PlainDrawer(ss).setInputBuffer(buffers[BUF_INP1]));
    //ss.addFilter(new PlainDrawer(ss).setBuffers(ss.buffers[BUF_OUT],buffers[BUF_INP1]));
    ss.addFilter(new KaleidoFilter(ss).setFilterName("KaleidoFilter").setCanvases(ss.getCanvasMapping("out"), ss.getCanvasMapping("inp0"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    //this.addScene(ss);
    this.addSceneOutputCanvas(
      ss,
      "/out"
      //buffers[BUF_OUT]
    );
    
    this.addSceneInputOutputCanvas(
      new SpiralScene(this,w,h,"/inp0").setSceneName("SpiralScene") //buffers[BUF_INP0])
            //.registerCallbackPreset("beat", "beat_8", "spin")
            .registerCallbackPreset("beatII", "beat_4", "unwind")
            .registerCallbackPreset("beatII", "beat_1", "zoom")
            //.registerCallbackPreset("beatIII","beat_4", "unwind")
            .registerCallbackPreset("beatIV", "beat_4", "radius")
            .registerCallbackPreset("beatII","beat_16","spin_forward")
            ,
      "/temp",//buffers[BUF_OUT],
      "/out"//buffers[BUF_OUT]
    );
    
    SimpleScene os = (SimpleScene) new SimpleScene(this,w,h).setSceneName("OutputShader");
    os.setCanvas("out", "/out");
    os.setCanvas("inp0","/out");
    os.addFilter(new ShaderFilter(os,"Feedback.xml").setFilterName("Edges").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    os.addFilter(new KaleidoFilter(os).setFilterName("Kaleido").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    os.addFilter(new GLColourFilter(os).setFilterName("GLColourFilter"));
    
    os.addFilter(new BlendDrawer(os).setFilterName("PlainDrawer").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0")).setParameterValue("BlendMode",9));
    
    this.addSceneInputOutputCanvas(
      os,
      "/out",
      "/out"
      //buffers[BUF_OUT]
    );    
    
    final String[] togglables = {
          "/TextFlash",
          "/Kaleidoscope",
          "/BlobScene",
          "/BlobScene",          
          "/SpiralScene",
          "/SpiralScene2",
          "/EyeScene",
          "/OutputShader/Edges",
          "/OutputShader/Kaleido",
          "/OutputShader/GLColourFilter",          
          "/OutputShader/PlainDrawer",          
          //"/OutputShader",
          //"/ImageListScene2",
          "/BlendDave",
          "/BlendDave2",          
          //"/ImageListScene1/mirror",
          //"/ImageListScene2/mirror",
          "/BlendDave/Kaleido",
          "/BlendDave2/Kaleido",          
          "/BlendDave2/Kaleido"                    
        };
        
    /*getStream("midi").registerEventListener("note",
      new ParameterCallback () {
        public void call(Object value) {
          int v = (Integer) value;
          v = v % togglables.length;
          ((Mutable) getObjectForPath(togglables[v])).toggleMute();        
        }
      }
    );*/
    /*getStream("beat").registerEventListener("bar_1",
      new ParameterCallback() {

        public int getNumberActive() {
          int count = 0;
          for (int i = 0 ; i < togglables.length ; i++) {
            Mutable o = (Mutable) getObjectForPath(togglables[i]);
            //System.out.println("getting " + togglables[i] + ", muted: " + (o.isMuted()?"yes":"no"));
            if (o.isMuted()) count++;
          }
          return count;
        }
        public void toggle(String r) {
          Mutable o = (Mutable) getObjectForPath(r);
          o.toggleMute();
        }        
        public void call (Object value) {
          int flipCount;
          if((Integer)value%10==0)
            flipCount = togglables.length;
          else
            flipCount = togglables.length/3;
            
          for (int i = 0 ; i < flipCount ; i++) {
            int r = (int)APP.random(0,togglables.length);
            toggle(togglables[r]);
          }
          if (getNumberActive()<3) {
            System.out.println("less than 3 active..");
          }
          if (((Integer)value/10)%3==0) {
            ((Filter)getObjectForPath("/BlendDave/Blend")).nextMode();
          }
        }
      }
   );*/
    
    
    this.getSequencer().getStream("beatII").registerEventListener("beat_2",
      new ParameterCallback() {
        public void call (Object value) {
          int v = (Integer)value;
          
          //((BlobDrawer)getObjectForPath("/BlobScene/BlobDrawer")).setColour((int)APP.random(255),(int)APP.random(255),(int)APP.random(255),(int)APP.random(255));
          
          if (v%2==0) {
            //System.out.println("next mode on textflash blenddrawer ?");
            ((BlendDrawer)getObjectForPath("/TextFlash/BlendDrawer")).setBlendMode((int)((VurfEclipse)APP.getApp()).random(0,12));
            //((BlendDrawer)getObjectForPath("/TextFlash/BlendDrawer")).toggleMute();
            //getSceneForPath("/TextFlash").toggleMute();
          }          
          if (v%3==0) {
            //getSceneForPath("/SpiralScene").toggleMute();
          }
          if(v%12==0) {
          }
          if(v%13==0) {
          }          
          if(v%14==0) {
          }
          if(v%15==0) {
          }
        }
      }
    );
    
    
    this.addSceneInputOutputCanvas(
      new SpiralScene(this,w,h,"/out").setSceneName("SpiralScene2") //buffers[BUF_INP0])
            .registerCallbackPreset("beat", "beat_8", "spin_forward")
            //.registerCallbackPreset("beatII", "beat_4", "unwind")
            .registerCallbackPreset("beatII", "beat_2", "zoom")
            //.registerCallbackPreset("beatIII", "beat_4", "unwind")
            .registerCallbackPreset("beatMaster", "beat_32", "radius")
            ,
      "/temp",//buffers[BUF_OUT],
      "/out"//buffers[BUF_OUT]
    );    
    
    
    
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
    );
    
    /*this.addScene(
      new TimeScene(this,w,h)
    );*/
    
    //in.loop();
    
    return true;
  }

public boolean isSequencerEnabled() {
	// TODO Auto-generated method stub
	return this.enableSequencer;
}
  
  
}
