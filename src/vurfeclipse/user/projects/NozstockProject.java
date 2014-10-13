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

public class NozstockProject extends Project implements Serializable {
  
  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");
  
  public NozstockProject(int w, int h, String gfx_mode) {
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
    Stream stream = new Stream("Test Stream");
    BeatStream beatStream = new BeatStream("Beat Stream", 120.0f, APP.getApp().millis());
    this.addStream("test", stream);
    this.addStream("beat", beatStream);
    
    BeatStream beatStream2 = new BeatStream("Beat Stream 2", 60.0f, APP.getApp().millis());    
    this.addStream("beatII", beatStream);

    BeatStream beatStream3= new BeatStream("Beat Stream 3", 90.0f, APP.getApp().millis());    
    this.addStream("beatIII", beatStream);    
    
    BeatStream beatStream4= new BeatStream("Beat Stream 4", 180.0f, APP.getApp().millis());    
    this.addStream("beatIV", beatStream);        

    BeatStream beatStreamMaster = new BeatStream("Beat Stream Master", 40.0f, APP.getApp().millis());    
    this.addStream("beatMaster", beatStream);    
    
    NumberStream numberStream = new NumberStream("Number Stream", 130.0f, 69.0f, APP.getApp().millis());
    this.addStream("number", numberStream);
    
    //MidiStream midiStream = new MidiStream("Midi Stream", true);
    //this.addStream("midi", midiStream);
    
    return true;
  }
  
  public boolean setupScenes () {      
    final SimpleScene ils1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene1");//.setOutputBuffer(getCanvas("inp0").surf);
    final SimpleScene ils2 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("ImageListScene2");//.setOutputBuffer(getCanvas("inp1").surf);
    
    /*ils1.addFilter(new ImageListDrawer(ils1).setFileList("nozdino.lst").setCurrentIndex(50).setNumBlobs(200).setFilterName("ImageListDrawer"));
    ils2.addFilter(new ImageListDrawer(ils2).setFileList("nozdino.lst").setNumBlobs(200).setFilterName("ImageListDrawer")); */
    ils1.addFilter(new ImageListDrawer(ils1).setFileList("doctea.lst").setCurrentIndex(5).setNumBlobs(200).setFilterName("ImageListDrawer"));
    ils2.addFilter(new ImageListDrawer(ils2).setFileList("doctea.lst").setNumBlobs(200).setFilterName("ImageListDrawer"));    
 
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
          ((Filter)getObjectForPath("/Kaleidoscope/Kaleido")).nextMode();
          if ((Integer)value%10==0)((Filter)getObjectForPath("/Kaleidoscope/Mirror")).toggleParameterValue("mirror_y");
          if ((Integer)value% 5==0)((Filter)getObjectForPath("/Kaleidoscope/Mirror")).toggleParameterValue("mirror_x");          
          //((TextFlashScene)getObjectForPath("/TextFlash")).changeFont((int) value);
        }
      }
    );        
        
    this.addSceneOutputCanvas(
      ils1,
      "/inp0"
    );
    this.addSceneOutputCanvas(
      ils2,
      "/inp1"
    );
    
    
    final SimpleScene bl1 = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlendScene");
    bl1.setOutputCanvas("/out");
    bl1.addFilter(new PlainDrawer(bl1).setInputCanvas("/inp0"));
    bl1.addFilter(new BlendDrawer(bl1).setFilterName("BlendDrawer1").setInputCanvas("/inp1"));
    this.addSceneOutputCanvas(bl1,"/out");//getCanvas("/out"));
    
    
    SimpleScene ss = (SimpleScene) new SimpleScene(this,w,h).setSceneName("Kaleidoscope");
    ss.setCanvas("out", "/out");
    ss.setCanvas("inp0","/inp0");
    //ss.addFilter(new PlainDrawer(ss).setFilterName("PlainDrawer").setCanvases(ss.getCanvasMapping("out"), ss.getCanvasMapping("inp0")));
    ss.addFilter(new MirrorFilter(ss).setFilterName("Mirror").setCanvases(ss.getCanvasMapping("out"), ss.getCanvasMapping("out")));    
    ss.addFilter(new KaleidoFilter(ss).setFilterName("Kaleido").setCanvases(ss.getCanvasMapping("out"), ss.getCanvasMapping("out")));
    this.addSceneOutputCanvas(
      ss,
      "/out"
    );
    
    

    this.addSceneInputOutputCanvas(
      new TextFlashScene(this,w,h, new String[] {
        //"Nozstock", "Nozstock: the Hidden Valley",
        "NOZSTOCK",
        "NOZSTOCK THE HIDDEN VALLEY",
        "123456789+-"
      })/*.setFonts(new String[] {
    	"Caveman-128.vlw", "Dinosaur-512.vlw", "DinosBeeline-512.vlw", "LostWorld-128.vlw", "DinosaurJrPlane-256.vlw", "DinosaurSkin-128.vlw"
      })  */    
        .setSceneName("TextFlash")
        .registerCallbackPreset("beatIII","beat_4","cycle")
        //.registerCallbackPreset("beatIII","beat_2","rotate"),
        ,
      "/out",
      "/out"
    );
    
    SimpleScene bd = (SimpleScene) new SimpleScene(this,w,h).setSceneName("BlobScene").setOutputCanvas("/out");
    bd.addFilter(new BlobDrawer(bd).setImage("ds2014/dseye.png").setFilterName("BlobDrawer").setCanvases(bd.getCanvasMapping("temp2"),bd.getCanvasMapping("inp0"))); 
    bd.addFilter(new BlendDrawer(bd).setFilterName("BlendDrawer").setCanvases(bd.getCanvasMapping("out"),bd.getCanvasMapping("temp2")));
    this.addSceneInputOutputCanvas(
      bd,
      "/out",
      "/out"
    );    
    getStream("beat").registerEventListener("beat_32", new ParameterCallback () {
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
    });
    
    SimpleScene eyescene = (SimpleScene) new SimpleScene(this,w,h).setSceneName("EyeScene").setOutputCanvas("/out");
    BlobDrawer eyedrawer = (BlobDrawer) new BlobDrawer(eyescene).setImage("ds2014/dseye.png").setFilterName("BlobDrawer").setCanvases(eyescene.getCanvasMapping("temp2"),"/inp1"); //eyescene.getCanvasMapping("inp0"));
    eyedrawer.setParameterValue("shape", 7).setParameterValue("numSections",45.0f).setParameterValue("numofCircles",1.5f);
    eyescene.addFilter(eyedrawer); 
    eyescene.addFilter(new BlendDrawer(eyescene).setFilterName("BlendDrawer").setCanvases(eyescene.getCanvasMapping("out"),eyescene.getCanvasMapping("temp2")));
    this.addSceneInputOutputCanvas(
      eyescene,
      "/out",
      "/out"
    );    
    getStream("beat").registerEventListener("beat_32", new ParameterCallback () {
      public void call (Object value) {
        int v = (Integer)value/10;
        if((v/10)%4==0) {
          ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValueFromSin("zRotate", PApplet.sin(PApplet.radians(v)));
          ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValueFromSin("radius", PApplet.sin(PApplet.radians(v)));
        } else {
          ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValueFromSin("zRotate", 0.0f);          
          ((Filter)getObjectForPath("/EyeScene/BlobDrawer")).setParameterValueFromSin("radius", 1.0f);
        }  
      } 
    });    
    
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
    );    
    
    
    this.addSceneInputOutputCanvas(
      new SpiralScene(this,w,h,"/out").setSceneName("SpiralScene2") 
            .registerCallbackPreset("beat", "beat_8", "spin_forward")
            .registerCallbackPreset("beatII", "beat_2", "zoom")
            .registerCallbackPreset("beatMaster", "beat_32", "radius")
            ,
      "/temp",
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
