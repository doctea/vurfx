package vurfeclipse.projects;
import vurfeclipse.APP;
import vurfeclipse.Canvas;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.*;
import vurfeclipse.scenes.*;
import processing.core.PApplet;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.BlobDrawer;
import vurfeclipse.filters.GLColourFilter;
import vurfeclipse.filters.SpiralDrawer;
import vurfeclipse.scenes.DebugScene;
import vurfeclipse.scenes.Demo2Scene;
import vurfeclipse.scenes.DemoScene;
import vurfeclipse.scenes.SimpleScene;
import vurfeclipse.scenes.SpiralScene;
import vurfeclipse.scenes.TextFlashScene;
import vurfeclipse.scenes.WebcamScene;
import vurfeclipse.streams.*;

public class TestProject extends Project {
  
  //AudioPlayer in = minim.loadFile("data/audio/funky probe 7_35.mp3");
  
  public TestProject(int w, int h, String gfx_mode) {
    super(w,h,gfx_mode);
  }
  
  public boolean initialiseBuffers() {
    addCanvas("/out", Canvas.makeCanvas(w,h,gfx_mode,"output"));
    addCanvas("/inp0",Canvas.makeCanvas(w,h,gfx_mode,"input0"));
    addCanvas("/inp1", Canvas.makeCanvas(w,h,gfx_mode,"input1"));
    addCanvas("/inp2", Canvas.makeCanvas(w,h,gfx_mode,"input2"));
    addCanvas("/temp1", Canvas.makeCanvas(w,h,gfx_mode,"temp1"));
    addCanvas("/temp2", Canvas.makeCanvas(w,h,gfx_mode,"temp2"));
    
    return true;
  }
  
  public boolean setupStreams () {
    Stream stream = new Stream("Test Stream");
    BeatStream beatStream = new BeatStream("Beat Stream", 130.0, ((VurfEclipse)APP.getApp()).millis());
    this.addStream("test", stream);
    this.addStream("beat", beatStream);
    
    NumberStream numberStream = new NumberStream("Number Stream", 130.0f, 69, ((VurfEclipse)APP.getApp()).millis());
    this.addStream("number", numberStream);
    
    return true;
  }
  
  public boolean setupScenes () {  
    // need a way to specify which scenes are running (selected only at first?)
    // need a way to blend between scenes ...
    // need a way for scenes to write to a shared buffer .. 
      // set output buffer on Webcam Scene to a Project buffer 
      // set input buffer on following Scenes to that buffer
    
    //this.addScene(new WebcamScene(this,w,h,0));

    this.addSceneOutputCanvas(
      new WebcamScene(this,w,h,0),
      //buffers[BUF_INP0]
      "/inp0"
    );
    this.addSceneOutputCanvas(
      new WebcamScene(this,w,h,1),
      "/inp1"
      //buffers[BUF_INP1]
    );
    this.addSceneOutputCanvas(
      new VideoScene(this,w,h,"video/129-Probe 7 - Over and Out(1)-05.mkv"),
      //buffers[BUF_INP0]
      "/inp0"
    );
    
    /*this.addSceneInputOutput(
      new PlainScene(this,w,h),
      buffers[BUF_INP0],
      buffers[BUF_OUT]
    );*/
    
    
    //final DemoScene ds = new DemoScene(this,w,h);
    final Demo2Scene ds2 = new Demo2Scene(this,w,h);
    //ds2.setBuffer(ds2.BUF_SRC2, buffers[BUF_INP0]);
    //ds2.setBuffer(ds2.BUF_SRC3, buffers[BUF_INP1]); // INP1
    ds2.setCanvas("src2", "/inp0");
    ds2.setCanvas("src3", "/inp1");
    this.addSceneInputOutputCanvas(
      ds2,
      "/inp0",
      "/temp1"
      /*buffers[BUF_INP0],
      //buffers[BUF_TEMP1]
      buffers[BUF_OUT]*/
    );
    
    
    final DemoScene ds = new DemoScene(this,w,h);
    //ds.setBuffer(ds.BUF_SRC2, buffers[BUF_INP0]);
    //ds.setBuffer(ds.BUF_SRC3, buffers[BUF_INP1]); // INP1
    ds2.setCanvas("src2", "/inp0");
    ds2.setCanvas("src3", "/inp1");
    this.addSceneInputOutputCanvas(
      ds,
      "/inp1",
      "/out"
      //"out
      //buffers[BUF_INP1], // INP1
      //buffers[BUF_OUT]
      //buffers[BUF_TEMP1]
      //buffers[BUF_TEMP2]
    );
    
    
        /*filters[++i] = new VideoPlayer(this, "tworld84.dv.ff.avi");
    //filters[++i] = new VideoPlayer(this, "station.mov");
    filters[i].setBuffers(buffers[BUF_SRC], buffers[BUF_TEMP]);
    filters[i].initialise();*/

    /*SimpleScene vid = new SimpleScene(this,w,h);
    vid.initialise();
    vid.addFilter(new VideoPlayer(vid, "tworld84.dv.ff.avi").setOutputBuffer(vid.buffers[vid.BUF_TEMP]));
    vid.addFilter(new MirrorFilter(vid).setInputBuffer(vid.buffers[vid.BUF_TEMP]).setOutputBuffer(vid.buffers[vid.BUF_TEMP])); //setBuffers(vid.buffers[vid.BUF_OUT], vid.buffers[vid.BUF_TEMP]));
    vid.addFilter(new KaleidoFilter(vid).setInputBuffer(vid.buffers[vid.BUF_TEMP]));
    this.addSceneOutput(vid, buffers[BUF_INP2]);*/


    SimpleScene s = new SimpleScene(this,w,h);
    s.initialise();
    final SpiralDrawer sd = (SpiralDrawer) new SpiralDrawer(s)
      //.setBuffers(s.buffers[s.BUF_TEMP],buffers[BUF_INP2]) //OUT]) //TEMP1])
      .setCanvases(s.getCanvasMapping("temp"), "/inp2")
      //.registerCallbackPreset("beat", "beat_16", "spin"))
      ;        
    s.addFilter(sd);
    //s.addFilter(new BlendDrawer(s).setBuffers(s.buffers[s.BUF_TEMP],s.buffers[s.BUF_TEMP]));
    final BlobDrawer bd = (BlobDrawer) new BlobDrawer(s)
      //.setBuffers(s.buffers[s.BUF_TEMP2],s.buffers[s.BUF_TEMP1])
      .setOutputCanvas(s.getCanvasMapping("temp2"))
      .setInputCanvas("/inp1")
      //.setOutputBuffer(s.buffers[s.BUF_TEMP2])
      //.setInputBuffer(buffers[BUF_INP1])
      .changeParameterValue("xRadianMod", -1.0)
      ;
    final BlobDrawer bd2 = (BlobDrawer) new BlobDrawer(s)
      //.setBuffers(s.buffers[s.BUF_TEMP2],s.buffers[s.BUF_TEMP1])
      //.setOutputBuffer(s.buffers[s.BUF_TEMP3])
      //.setInputBuffer(buffers[BUF_INP1])
      .setOutputCanvas(s.getCanvasMapping("temp3"))
      .setInputCanvas("/inp1")
      //.setInputBuffer(s.buffers[s.BUF_TEMP2])
      .changeParameterValue("yRadianMod", -1.0)
      .changeParameterValue("shape", 5)
      ;
    getStream("beat").registerEventListener("beat_16",new ParameterCallback() {
          public void call(Object value) {
            int i = (Integer)value;
            //bd.setParameterValue("xRadianMod",map(i%64,0,64,-1.0,1.0));
            bd.changeParameterValue("xRadianMod",PApplet.sin(PApplet.radians(i))); //map(i%64,0,64,-1.0,1.0));
            bd.changeParameterValue("yRadianMod",PApplet.cos(PApplet.radians(i))); //map(i%64,0,64,-1.0,1.0));

            //bd2.changeParameterValue("yRadianMod",cos(radians(i/4)));//*(abs(sin(radians(i))))))); //map(i%64,0,64,-1.0,1.0));
            
            //bd2.changeParameterValue("xRadianMod",1-cos(radians(i)));
            //bd2.changeParameterValue("yRadianMod",1-sin(radians(i)));
            
            bd.changeParameterValue("radius",PApplet.sin(PApplet.radians(i/2)));
            
            bd.changeParameterValue("tint",(int)(255*(PApplet.abs(PApplet.sin(PApplet.radians(i))))));
            bd2.changeParameterValue("tint",(int)(255-(255*(PApplet.abs(PApplet.sin(PApplet.radians(i*3)))))));
            
            bd.changeParameterValue("rotation",PApplet.radians(i*10));
            bd2.changeParameterValue("rotation",PApplet.radians(i*100));

            
            bd.changeParameterValue("totalRotate",PApplet.radians(i));
            bd2.changeParameterValue("totalRotate",PApplet.radians(i-90));
            //sd.changeParameterValue("xRadianMod",-sin(radians(i)));
            //bd.setParameterValue("yRadianMod",map(i%16,0,8,-1.0,1.0));
          }
      });
    getStream("beat").registerEventListener("beat_16",new ParameterCallback() {
          public void call(Object value) {
            int i = (Integer)value;
            sd.changeParameterValue("totalRotate",PApplet.radians(i*10));
            
          }
    });
    s.addFilter(bd);
    s.addFilter(bd2);
    s.addFilter(new GLColourFilter(s).setCanvases(s.getCanvasMapping("temp3"),s.getCanvasMapping("temp3"))); //setBuffers(s.buffers[s.BUF_TEMP3],s.buffers[s.BUF_TEMP3]));
    s.addFilter(((BlendDrawer)new BlendDrawer(s).setInputCanvas(s.getCanvasMapping("temp3")))); //.setInputBuffer(s.buffers[s.BUF_TEMP3]))); //.setParameterValue("BlendMode",4));//BlendMode(6));
    s.addFilter(new BlendDrawer(s).setInputCanvas(s.getCanvasMapping("temp2")).changeParameterValue("Opacity", 0.5f));
      //setInputBuffer(s.buffers[s.BUF_TEMP2]).changeParameterValue("Opacity", 0.5));//.setParameterValue("BlendMode",9));
    s.addFilter(new BlendDrawer(s).setInputCanvas(s.getCanvasMapping("temp"))); //Buffer(s.buffers[s.BUF_TEMP]));//.setParameterValue("BlendMode",4));
    //this.addSceneOutput(s, buffers[BUF_OUT]);
    this.addSceneOutputCanvas(s, "/out");
    
    this.addSceneOutputCanvas(
      new SpiralScene(this,w,h,"/out") //buffers[BUF_OUT])
        .registerCallbackPreset("beat", "beat_16", "spin_back")
        .registerCallbackPreset("beat", "beat_16", "unwind"), 
      //buffers[BUF_OUT]
      "/out"
    ).setMuted(true);

    
    this.addSceneInputOutputCanvas(
      new TextFlashScene(this,w,h)
        .registerCallbackPreset("beat", "beat_2", "random")
        .registerCallbackPreset("beat", "beat_4", "toggle")
        .registerCallbackPreset("beat", "beat_8", "swivel")
         ,
      //buffers[BUF_INP0],
      //buffers[BUF_OUT]
      "/inp0",
      "/out"
    ).setMuted(true);
    
    
    /*this.addSceneInputOutput(
      new KaleidoScene(this,w,h),
      buffers[BUF_OUT],
      buffers[BUF_OUT]
    );*/
    
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
  
  
}
