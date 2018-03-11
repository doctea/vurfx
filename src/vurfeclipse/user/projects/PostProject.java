package vurfeclipse.user.projects;

import java.io.Serializable;

import processing.core.PApplet;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;
import vurfeclipse.streams.*;

public class PostProject extends Project implements Serializable {
  
  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");
  
  boolean enableSequencer = true;

public PostProject(int w, int h, String gfx_mode) {
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
    /*Stream stream = new Stream("Test Stream");
    BeatStream beatStream = new BeatStream("Beat Stream", 120.0, APP.millis());
    this.addStream("test", stream);
    this.addStream("beat", beatStream);
    
    BeatStream beatStream2 = new BeatStream("Beat Stream 2", 60.0, APP.millis());    
    this.addStream("beatII", beatStream);

    BeatStream beatStream3= new BeatStream("Beat Stream 3", 90.0, APP.millis());    
    this.addStream("beatIII", beatStream);    
    
    BeatStream beatStream4 = new BeatStream("Beat Stream 4", 180.0, APP.millis());    
    this.addStream("beatIV", beatStream);        */

    BeatStream beatStreamMaster = new BeatStream("Beat Stream Master", 90.0, ((VurfEclipse)APP.getApp()).millis());    
    this.getSequencer().addStream("beatMaster", beatStreamMaster);    
    
    /*NumberStream numberStream = new NumberStream("Number Stream", 130.0, 69, APP.millis());
    this.addStream("number", numberStream);
    */
    MidiStream midiStream = new MidiStream("Midi Stream", 5, true);
    this.getSequencer().addStream("midi", midiStream);
    
    return true;
  }
  
  public boolean setupScenes () {  

    
    final SimpleScene ils1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene1");//.setOutputBuffer(getCanvas("inp0").surf);
    final SimpleScene ils2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene2");//.setOutputBuffer(getCanvas("inp1").surf);
    
    //ils1.addFilter(new ImageListDrawer(ils1).setFileList("files_ds2014_6.lst").setCurrentIndex(7).setNumBlobs(40).setFilterName("ImageListDrawer"));
    //ils2.addFilter(new ImageListDrawer(ils2).setFileList("files4.txt").setNumBlobs(40).setFilterName("ImageListDrawer")); 
    ils1.addFilter(new ImageListDrawer(ils1).setFileList("festival.lst").setCurrentIndex(7).setNumBlobs(200).setFilterName("ImageListDrawer"));
    //ils1.addFilter(new MirrorFilter(ils1).setFilterName("mirror").setInputCanvas(ils1.getCanvasMapping("out")).setOutputCanvas(ils1.getCanvasMapping("out")));
    ils2.addFilter(new ImageListDrawer(ils2).setFileList("festival.lst").setNumBlobs(200).setFilterName("ImageListDrawer")); 
    //ils2.addFilter(new MirrorFilter(ils2).setFilterName("mirror").setInputCanvas(ils2.getCanvasMapping("out")).setOutputCanvas(ils2.getCanvasMapping("out")));
 
        
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
    

        
    SimpleScene ss = (SimpleScene) new SimpleScene(this,w,h).setSceneName("Kaleidoscope");  
    ss.setCanvas("out", "/out");
    ss.setCanvas("inp0","/inp0");
    ss.addFilter(new PlainDrawer(ss).setFilterName("PlainDrawer").setCanvases(ss.getCanvasMapping("out"), ss.getCanvasMapping("inp0"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    ss.addFilter(new KaleidoFilter(ss).setFilterName("KaleidoFilter").setCanvases(ss.getCanvasMapping("out"), ss.getCanvasMapping("inp0"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    this.addSceneOutputCanvas(
      ss,
      "/out"
      //buffers[BUF_OUT]
    );
    
    this.addSceneInputOutputCanvas(
      new SpiralScene(this,w,h,"/inp0").setSceneName("SpiralScene") //buffers[BUF_INP0])
            //.registerCallbackPreset("beatII", "beat_4", "unwind")
            //.registerCallbackPreset("beatII", "beat_1", "zoom")
            //.registerCallbackPreset("beatIV", "beat_4", "radius")
            //.registerCallbackPreset("beatII","beat_16","spin_forward")
            ,
      "/temp",//buffers[BUF_OUT],
      "/out"//buffers[BUF_OUT]
    );
    
    SimpleScene daves2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlendDave2");
    daves2.setCanvas("out", "/out");
    daves2.setCanvas("inp0","/inp1");
    daves2.addFilter(new KaleidoFilter(daves2).setFilterName("Kaleido").setCanvases(daves2.getCanvasMapping("temp0"), daves2.getCanvasMapping("inp0"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    daves2.addFilter(new BlendDrawer(daves2).setFilterName("Blend").setCanvases(daves2.getCanvasMapping("out"),daves2.getCanvasMapping("temp0")).setParameterValue("Opacity",0.25f));
    this.addSceneInputOutputCanvas(
      daves2,
      "/inp1",
      "/out"
      //buffers[BUF_OUT]
    );        

    this.addSceneInputOutputCanvas(
      new TextFlashScene(this,w,h, new String[] {
        "Boars Head", "PsychNight",
        "Socio Suki", "Phullopium\nDude", "Retinal\nCircus", "DAVE\nSIMPSON", 
        "vurf",
        "=)", ":)", "(:", "(=", ";)"
      }).setSceneName("TextFlash")
        //.registerCallbackPreset("beatIII","beat_4","random")
        //.registerCallbackPreset("beatIII","beat_2","rotate")
        ,
      "/out",//buffers[BUF_OUT],
      "/out"//buffers[BUF_OUT]
    );
    
    SimpleScene bd = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out");
    bd.addFilter(new BlobDrawer(bd).setImage("ds2014/dseye.png").setFilterName("BlobDrawer").setCanvases(bd.getCanvasMapping("temp2"),bd.getCanvasMapping("inp0"))); //.setCanvases(bd.getCanvasMapping("out"), bd.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    //os.addFilter(new KaleidoFilter(os).setFilterName("Kaleido").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    bd.addFilter(new BlendDrawer(bd).setFilterName("BlendDrawer").setCanvases(bd.getCanvasMapping("out"),bd.getCanvasMapping("temp2")));
    this.addSceneInputOutputCanvas(
      bd,
        //.setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0")))
      //os,
      "/out",
      "/out"
      //buffers[BUF_OUT]
    );    
    
    
    SimpleScene os = (SimpleScene) new SimpleScene(this,w,h).setSceneName("OutputShader");
    os.setCanvas("out", "/out");
    os.setCanvas("inp0","/out");
    os.addFilter(new ShaderFilter(os,"Feedback.xml").setFilterName("Edges").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //inp0"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    os.addFilter(new KaleidoFilter(os).setFilterName("Kaleido").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    os.addFilter(new GLColourFilter(os).setFilterName("GLColourFilter"));
    
    //os.addFilter(new ShaderFilter(os,"vhs.xml").setFilterName("vhs")); //.setCanvases(os.getCanvasMapping("out"),os.getCanvasMapping("out")));
    
    os.addFilter(new BlendDrawer(os).setFilterName("PlainDrawer").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0")).setParameterValue("BlendMode",9));
    
    this.addSceneInputOutputCanvas(
      os,
      "/out",
      "/out"
      //buffers[BUF_OUT]
    );    
    
        
    SimpleScene daves = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlendDave");
    daves.setCanvas("out", "/out");
    daves.setCanvas("inp0","/inp0");
    //os.addFilter(new ShaderFilter(os,"Feedback.xml").setFilterName("Edges").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    daves.addFilter(new KaleidoFilter(daves).setFilterName("Kaleido").setCanvases(daves.getCanvasMapping("temp0"), daves.getCanvasMapping("inp0"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    daves.addFilter(new BlendDrawer(daves).setFilterName("Blend").setCanvases(daves.getCanvasMapping("out"),daves.getCanvasMapping("temp0")));
    
    //daves.addFilter(new PatternDrawer(daves).setFilterName("Pattern"));
    
    //os.addFilter(new GLColourFilter(os).setFilterName("GLColourFilter"));
    this.addSceneInputOutputCanvas(
      daves,
      "/inp0",
      "/out"
      //buffers[BUF_OUT]
    );    
    
    this.addSceneInputOutputCanvas(
      new SpiralScene(this,w,h,"/out").setSceneName("SpiralScene2") //buffers[BUF_INP0])
            //.registerCallbackPreset("beat", "beat_8", "spin_forward")
            //.registerCallbackPreset("beatII", "beat_2", "zoom")
            //.registerCallbackPreset("beatMaster", "beat_32", "radius")
            ,
      "/temp",//buffers[BUF_OUT],
      "/out"//buffers[BUF_OUT]
    );    
    
    
    
    this.addSceneOutputCanvas(
      new DebugScene(this,w,h),
      "/out"
    );
    
    
///////////////////////////////
/////////////////////////////
//////////////////////////////
//////// STREAMS

    final String[] togglables = {
          "/TextFlash",
          "/Kaleidoscope",
          "/BlobScene",
          "/BlobScene",          
          "/SpiralScene",
          "/SpiralScene2",
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

    //getStream("midi").registerEventListener("note", new ParameterCallback () {
    this.getSequencer().getStream("beatMaster").registerEventListener("beat_1", new ParameterCallback () {
        synchronized public void call(Object value) {
          int v = (Integer) value;
          v = v % togglables.length;
          ((Mutable) getObjectForPath(togglables[v])).toggleMute();
          
          if (((Integer)value%10)==0) {
        	  getSequencer().getStream("beatMaster").registerEventListener("beat_32", new DieAfterParameterCallback (359) {
              synchronized public void call(Object value) {
                super.call(value);
                int v = 100 * ((Integer)value - (Integer)initial);
                ((Filter)getObjectForPath("/SpiralScene2/SpiralDrawer")).setParameterValue("rotation", (float)PApplet.radians(v));
              }
            });
          }
          
        }
      }
    );
    
    this.getSequencer().getStream("beatMaster").registerEventListener("bar_1", new ParameterCallback () {
      synchronized public void call (Object value) {    
        ((Filter)getObjectForPath("/ImageListScene1/ImageListDrawer")).nextMode();
        ((Filter)getObjectForPath("/ImageListScene2/ImageListDrawer")).nextMode();        
      }
    });
    
/*

    getStream("beat").registerEventListener("beat_32", new ParameterCallback () {
      public void call (Object value) {
        int v = (Integer)value/10;
        //System.out.println("called " + v);        
        //((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValue("totalRotate", radians(v/10));
        //((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValue("rotation", -radians(v/10/2));
        
        if((v/10)%4==0) {
          ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValueFromSin("zRotate", sin(radians(v)));
          ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValueFromSin("radius", sin(radians(v)));
        } else {
          ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValueFromSin("zRotate", 0.0);          
          ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValueFromSin("radius", 1.0);
        }  
      } 
    });    

    getStream("beat").registerEventListener("beat_32", new ParameterCallback () {
      public void call (Object value) {
        int v = (Integer)value * 100;
        //System.out.println("called " + v);        
        if ((v/10)%10>5) {
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("totalRotate", radians(v/10));
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("rotation", -radians(v/10/2));
        } else {
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("totalRotate", -radians(v/10));
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("rotation", radians(v/10/2));
        }          
        
        if((v/10)%4==0) {
          ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValueFromSin("zRotate", radians(v));
        }        
      } 
    });


    getStream("beatMaster").registerEventListener("bar_4",
      new ParameterCallback() {
        //int counter = 0;
        public void call(Object value) {            
            System.out.println("Changing speed..");
            //float bpm = 60 + (abs(sin((Integer)value%2)) * 300);
            ((BeatStream)getStream("beatII")).toggleBPM(); //setBPM(60);
            if ((Integer)value%2==0)
              ((BeatStream)getStream("beatIII")).toggleBPM(); //setBPM(60);            
            if ((Integer)value%3==0)              
              ((BeatStream)getStream("beat")).toggleBPM();
            //if ((Integer)value%2==0) ((BeatStream)getStream("beatII")).toggleBPM(); //setBPM(60);            
            //if ((Integer)value%4==0) ((BeatStream)getStream("beatIII")).setSpeed(APP.random(1.0,4.0)); //toggleBPM(); //setBPM(60);                        
            //System.out.println("setting BPM to " + bpm); //" + 60 + abs(sin((Integer)value))) * 300);
          }
        }
    );
    

    
/*
    getStream("midi").registerEventListener("interval",
      new ParameterCallback () {
        public void call(Object value) {
          println("Callback got " + value + "!");
          //((Scene)getObjectForPath("/OutputShader")).toggleMute();
          scenes.get((Integer)value).toggleMute();
        }
      }
    );    
          
    getStream("beat").registerEventListener("bar_1", 
      new ParameterCallback () {
        public void call(Object value) {
          ils1.getFilter(0).nextMode();
        }
      }
    );    
    getStream("beatII").registerEventListener("bar_1", 
      new ParameterCallback () {
        public void call(Object value) {
          ils2.getFilter(0).nextMode();
        }
      }
    );            
    */
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
    
/*
    bl1.host.getStream("beatII").registerEventListener("beat_16",    
      new ParameterCallback() {
        int mode = 0;        
        public void call(Object value) {
          float v =(Integer)value/1000f;
          //float v2 = map(v,0.0,1.0f,0.2f,1.0f);
          //bl1.getFilter(0).setParameterValue("Opacity", map(abs(sin(v)),0.0f,1.0f,0.2f,0.8f));//(Float)value/1000.0f));
          //bl1.getFilter(1).setParameterValue("Opacity", 1.0-map(abs(sin(v)),0.0f,1.0f,0.0f,0.8f));//(Float)value/1000.0f));
          bl1.getFilter(1).setParameterValue("Opacity", 1.0-map(abs(sin(v)),0.0f,1.0f,0.0f,0.8f));//(Float)value/1000.0f));
          
          //bl1.getFilter(0).setParameterValueFromSin("tint",abs(sin(v)));
          //bl1.getFilter(1).setParameterValueFromSin("tint",1.0-abs(sin(v)));
          //bl1.getFilter(2).setParameterValueFromSin("tint",1.0-abs(sin(v/2)));
          
          //bl1.getFilter(0).setParameterValueFromSin("rotation", abs(sin(v)));
          
          //ils1.getFilter(0).setParameterValueFromSin("translate_x", abs(sin(v)));
          ils1.getFilter(0).setParameterValueFromSin("scale", abs(sin(v)));
          ils2.getFilter(0).setParameterValueFromSin("scale", 1.0-abs(sin(v)));         
           
           
          mode = ((int)v/1000)%4;
          GLColourFilter cf = ((GLColourFilter)getObjectForPath("/OutputShader/GLColourFilter")); 
          if (mode==0) cf.setRedGreenBlueShift(abs(sin(v/10)), 1.0, 1.0);
          if (mode==1) cf.setRedGreenBlueShift(1.0, abs(sin(v/10)), 1.0);        
          if (mode==2) cf.setRedGreenBlueShift(1.0, 1.0, abs(sin(v/10)));        
          if (mode==3) cf.setRedGreenBlueShift(1.0, 1.0, 1.0); //abs(sin(v/10)));                  
    
          if ((v/10)%2==0) {
            ((Filter)getObjectForPath("/TextFlash/TextDrawer")).setParameterValueFromSin("zrotation", sin(radians(v%360)));
          } else {
            ((Filter)getObjectForPath("/TextFlash/TextDrawer")).setParameterValue("zrotation", 0); 
          }
          if ((v/10)%3==0) {
            ((Filter)getObjectForPath("/SpiralScene2/SpiralDrawer")).setParameterValueFromSin("zRotate", sin(v%360));
          } else {
            ((Filter)getObjectForPath("/SpiralScene2/SpiralDrawer")).setParameterValue("zRotate", 0.0); 
          }          
          
          
        }
      }
    );
        
    
    
    getStream("beatII").registerEventListener("beat_2",
      new ParameterCallback() {
        public void call (Object value) {
          int v = (Integer)value;
          
          //((BlobDrawer)getObjectForPath("/BlobScene/BlobDrawer")).setColour((int)APP.random(255),(int)APP.random(255),(int)APP.random(255),(int)APP.random(255));
          
          if (v%2==0) {
            //System.out.println("next mode on textflash blenddrawer ?");
            ((BlendDrawer)getObjectForPath("/TextFlash/BlendDrawer")).setBlendMode((int)APP.random(0,12));
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
    
    getStream("beatMaster").registerEventListener("beat_4",
      new ParameterCallback() {
        public void call (Object value) {
          int v = (Integer)value;
          
          ((Filter)getObjectForPath("/OutputShader/PlainDrawer")).setParameterValue("Opacity", (float)v%255); //(float)APP.random(255));
          
          if(v%2==0) {
            //getSceneForPath("/SpiralScene2").toggleMute();
            
            //((Filter)getObjectForPath("/SpiralScene2/BlendDrawer")).nextMode();
            ((Filter)getObjectForPath("/SpiralScene2/SpiralDrawer")).nextMode();
          }
          if((v/10)%3==0) {
            ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValue("rotation", (float)v);
          } else if((v/10)%3==1) {
            ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValue("rotation", (float)-v);            
          }
          ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValueFromSin("colour", sin(radians(v))); //*((2^24)<<8));
            
          //System.out.println("setting rotation " + v%360);
          //((TextDrawer)getObjectForPath("/TextFlash/TextDrawer")).setParameterValue("rotation", -v);
          
          ((Filter)getObjectForPath("/BlobScene/BlendDrawer")).setParameterValueFromSin("Opacity", sin(radians(v/10)));
          
          ((Filter)getObjectForPath("/BlendDave2/Blend")).setParameterValueFromSin("Opacity", sin(radians(v/10)));
          
        }      
      }
    );  

    getStream("beat").registerEventListener("bar_1",
      new ParameterCallback() {
        public void call (Object value) {
          int v = (Integer)value;

          
          if (v%2==0) {
          }          
          if (v%4==0) {          
            //getSceneForPath("/Kaleidoscope").toggleMute();
            ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValue("shape",APP.random(0,10)>5?7:4);            
          }
          if (v%5==0) {
            ((Filter)getObjectForPath("/Kaleidoscope/KaleidoFilter")).nextMode();
            ((Filter)getObjectForPath("/BlobScene/BlendDrawer")).nextMode();            
          }
          if (v%6==0) {
            ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).nextMode();
          }         
          if (v%7==0) {
            ((Filter)getObjectForPath("/BlendScene/BlendDrawer1")).nextMode();
          }      
          if (v%9==0) {
            ((Filter)getObjectForPath("/BlobScene/BlobDrawer")).setParameterValue("edged",APP.random(0,1)>0.5?true:false);
          }                      
          if (v%12==0) {
            //((Filter)getObjectForPath("/EyeScene/BlobDrawer")).nextMode();             
            //((Filter)getObjectForPath("/OutputShader/Kaleido")).toggleMute();
          }       
          if (v%13==0) {
            //((Scene)getObjectForPath("/BlobScene")).toggleMute();
          }
        }
      }
    );        

    
    
    */
    
    
    
    /*this.addScene(
      new TimeScene(this,w,h)
    );*/
    
    //in.loop();
    
    return true;
  }
  
  
}
