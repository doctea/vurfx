package vurfeclipse.filters;


import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;
import codeanticode.glgraphics.GLTexture;
import codeanticode.gsvideo.GSMovie;

public class VideoPlayer extends Filter {
  transient GSMovie stream;
  String filename;
  
  transient GLTexture tex;
  
  transient GLTexture newTex;
  
  int mode = 0;
  
  public void nextMode () {
/*    long maxMode = 50;//(long)stream.length()/50;
    mode++;
    if (mode>=maxMode) mode = 0;
    if (!stream.isSeeking()) {
      long seek = mode * stream.length()/maxMode;
      System.out.println("currently at " + stream.frame() + ", seeking to mode " + mode + "/" + maxMode + " at " + seek);
      stream.jump((int)seek);
    }*/
    String[] videos = new String[] {
      "video/129-Probe 7 - Over and Out(1)-00.mkv",
      "tworld84.dv.ff.avi",
      "video/129-Probe 7 - Over and Out(1)-10.mkv",      
      
      "video/129-Probe 7 - Over and Out(1)-01.mkv",      
      "video/129-Probe 7 - Over and Out(1)-02.mkv",      
      "video/129-Probe 7 - Over and Out(1)-03.mkv",      
      "video/129-Probe 7 - Over and Out(1)-04.mkv",      
      "video/Life of Brian(XviD).avi",
      //"video/Wilfred.US.S01E02.HDTV.XviD-FQM.Trust"
    };
      
    changeVideo(videos[(int)random(0,videos.length)]);
    
    
  }
  
  public VideoPlayer(Scene sc, String filename) {
    super(sc);
    this.filename = filename;
  }
  
  public void setMuted(boolean on) {
    super.setMuted(on);
    if (!on) {
      stream.loop();
    } else {
      stream.pause();
    }
  }
  
  //Thread loader =  new Thread() {
     
  
  transient GSMovie newStream;
  boolean changing = false;
  public synchronized void changeVideo(String fn) {
    if (changing) return;
    final String filename = fn;
    this.filename = filename;
    changing = true;
    final VideoPlayer self = this;
    new Thread () {
      public void start () {
        super.start();
      }
      public void run () {
        System.out.println("Loaded new..");
        GSMovie newStream;
        newStream = new GSMovie(APP.getApp(),filename);
        //newTex = new GLTexture(APP, sc.w, sc.h);
        newStream.setPixelDest(tex, true);
        newStream.volume(0);
        if (!((VurfEclipse)APP.getApp()).exportMode)
          newStream.loop();
        while (!newStream.available()) 
          try { sleep(50); } catch (Exception e) {}
        newStream.read();
        System.out.println("read from newStream");
        //newStream.setPixelDest(tex, true);
        self.newStream = newStream;
        
          
        GSMovie oldStream = stream;
        //GLTexture oldTex = tex;
        self.stream = newStream;
        System.out.println("Swapping streams to " + filename);
        self.setFilterLabel(getFilterName() + filename);  
        //tex = newTex;
        oldStream.delete();
        //oldTex.delete();
        self.newStream = null;
        //newTex = null;      
        self.changing = false;
        
        /*GSMovie tempStream = stream;
        stream = newStream;
        System.out.println("Swapping stream..");
        stream.delete();
        stream = null;*/
        //changing = false;
      }
    }.start();
  }
  
  
  public boolean initialise() {
    /*try {
      quicktime.QTSession.open();
    } catch (quicktime.QTException qte) { 
      qte.printStackTrace();
    }*/
    
    tex = new GLTexture(APP.getApp(),sc.w,sc.h);
    
    this.setFilterLabel("VideoPlayer - " + filename);  
    
    System.out.println("Loading video " + filename);
    try {
       stream = new GSMovie(APP.getApp(),filename);
      //stream = new GSMovie(APP,"U:\\videos\\Tomorrows World - sinclair c5\\tworld84.dv.ff.avi");
      //stream.setPixelDest(out.getTexture());
      stream.setPixelDest(tex, true);
      stream.volume(0);
      if (!((VurfEclipse)APP.getApp()).exportMode) 
        stream.loop();
    } catch (Exception e) {
      System.out.println("got error " + e + " loading " + filename);
    }      //webcamStream = new Capture(APP, sc.w, sc.h, 30);
    //webcamStream.start();
    
    return true;
  }
  
  public synchronized boolean  applyMeatToBuffers() {
    if (((VurfEclipse)APP.getApp()).exportMode) {
      //seek to the correct position based on the current frame number ..
      System.out.println("jumping to frameCount " + ((VurfEclipse)APP.getApp()).frameCount);
      //stream.jump(frameCount * (global_fps/stream.getSourceFrameRate()));  
      //stream.jump(timeMillis * (global_fps/stream.getSourceFrameRate()));  
      stream.volume(0);
      stream.play();
      stream.jump((float)((VurfEclipse)APP.getApp()).timeMillis/1000);
      stream.pause();
    }
/*    if (changing && newStream!=null) {
      GSMovie oldStream = stream;
      //GLTexture oldTex = tex;
      stream = newStream;
      System.out.println("Swapping streams");
      //tex = newTex;
      oldStream.delete();
      //oldTex.delete();
      newStream = null;
      //newTex = null;      
      changing = false;
      this.setFilterLabel("VideoPlayer - " + filename);  
    }*/
    if (stream!=null ) { 
      if (!stream.isSeeking() && stream.available()) {
        stream.volume(0);
  
        //System.out.println("got webcamstream");
        stream.read();
        
        /*stream.loadPixels();
        out.loadPixels();
        //out.pixels = webcamStream.pixels;
        arrayCopy(stream.pixels, out.pixels);
        out.updatePixels();*/
        
        
        
        //out.getTexture().putPixelsIntoTexture();
        if (tex.putPixelsIntoTexture()) {
          out.image(tex,0,0,sc.w,sc.h);
          return true;
        }
        
        //out.image(stream,0,0,sc.w,sc.h);
        
        
        /*out.loadPixels();
        out.pixels = webcamStream.pixels;
        out.updatePixels();*/
        return false;
      } else if (!stream.isSeeking()) {  // available
        out.image(tex,0,0,sc.w,sc.h);
        return true;
      }
    } else {
      System.out.println("Stream is null!");
    }
    return false; // no new frame available to draw
  }  
}
