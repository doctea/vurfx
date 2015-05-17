package vurfeclipse.filters;


import java.io.File;
import java.util.ArrayList;

import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.scenes.Scene;
import codeanticode.glgraphics.GLTexture;
import codeanticode.gsvideo.GSMovie;
import codeanticode.gsvideo.*;

public class VideoPlayer extends Filter {
  transient GSMovie stream;
  String filename;
  
  transient GLTexture tex;
  
  transient GLTexture newTex;
  
  int mode = 0;
  
  public ArrayList<String> videos = new ArrayList<String>();
  
  public void nextMode () {
/*    long maxMode = 50;//(long)stream.length()/50;
    mode++;
    if (mode>=maxMode) mode = 0;
    if (!stream.isSeeking()) {
      long seek = mode * stream.length()/maxMode;
      System.out.println("currently at " + stream.frame() + ", seeking to mode " + mode + "/" + maxMode + " at " + seek);
      stream.jump((int)seek);
    }*/
    /*String[] videos = new String[] {
      "video/129-Probe 7 - Over and Out(1)-00.mkv",
      "tworld84.dv.ff.avi",
      "video/129-Probe 7 - Over and Out(1)-10.mkv",      
      
      "video/129-Probe 7 - Over and Out(1)-01.mkv",      
      "video/129-Probe 7 - Over and Out(1)-02.mkv",      
      "video/129-Probe 7 - Over and Out(1)-03.mkv",      
      "video/129-Probe 7 - Over and Out(1)-04.mkv",      
      "video/Life of Brian(XviD).avi",
      //"video/Wilfred.US.S01E02.HDTV.XviD-FQM.Trust"
    };*/
      
    //changeVideo(videos[(int)random(0,videos.length)]);
	  changeVideo(videos.get((int) random(0,videos.size())));
    
    
  }
  
  public VideoPlayer(Scene sc, String filename) {
    super(sc);
    //this.filename = filename;
    if (filename!="") videos.add(filename);
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
    if (filename=="") return;
    this.filename = filename;
    changing = true;
    final VideoPlayer self = this;
    new Thread () {
      public void start () {
        super.start();
      }
      public void run () {
        println("Loaded new.." + filename);
        GSMovie newStream;
        newStream = new GSMovie(APP.getApp(),filename);
        //newTex = new GLTexture(APP, sc.w, sc.h);
        newStream.setPixelDest(tex, true);
        newStream.volume(0);
        println("Set volume and pixeldest..");
        if (!((VurfEclipse)APP.getApp()).exportMode)
          newStream.loop();
        //println("about to do ")
        while (!newStream.available()) 
          try { sleep(50); } catch (Exception e) {}
        newStream.read();
        println("read from newStream");
        //newStream.setPixelDest(tex, true);
        self.newStream = newStream;
        
          
        GSMovie oldStream = stream;
        //GLTexture oldTex = tex;
        self.stream = newStream;
        println("Swapping streams to " + filename);
        self.setFilterLabel(getFilterName() + filename);  
        //tex = newTex;
        oldStream.stop();
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
  
  public void loadDirectory() {
	  loadDirectory("");
  }
  public void loadDirectory(String directory) {
	  //String directory = ""; // dummy
	  String path = APP.getApp().sketchPath("bin/data/" + directory);	// ffs need this on Windows..
	  //String path = APP.getApp().dataPath("image-sources/" + directory);		// ffs but seem to need this on tohers
	  //String path = Paths.get("bin/").toAbsolutePath().toString() + "/data/image-sources/" + directory;
	  //String path = Paths.get("").toAbsolutePath().toString() + "/data/image-sources/" + directory; // applet mode doesnt need bin
	  File folder = new File(path);
	  println(this + "#loadDirectory() got path " + path);
	  int count = 0;
	  for (final File fileEntry : folder.listFiles()) {
		  if (fileEntry.isDirectory()) {
			  // skip; maybe recurse tho in future
		  } else {
			  String fn = fileEntry.getName();
			  if (fn.contains(".ogg"))
				  videos.add(path + fileEntry.getName());
			  //if (count>=numBlobs) break;
		  }
	  }
  }
  
  public boolean initialise() {
    /*try {
      quicktime.QTSession.open();
    } catch (quicktime.QTException qte) { 
      qte.printStackTrace();
    }*/
    
    tex = new GLTexture(APP.getApp(),sc.w,sc.h);
    
    loadDirectory();
    filename = videos.get(0);
    
    this.setFilterLabel("VideoPlayer - " + filename);  
    
    println("Loading video " + filename);
    try {
       stream = new GSMovie(APP.getApp(),filename);
      //stream = new GSMovie(APP,"U:\\videos\\Tomorrows World - sinclair c5\\tworld84.dv.ff.avi");
      //stream.setPixelDest(out.getTexture());
      stream.setPixelDest(tex, true);
      stream.volume(0);
      if (!((VurfEclipse)APP.getApp()).exportMode) 
        stream.loop();
    } catch (Exception e) {
      println("got error " + e + " loading " + filename);
    }      //webcamStream = new Capture(APP, sc.w, sc.h, 30);
    //webcamStream.start();
    
    return true;
  }
  
  public synchronized boolean  applyMeatToBuffers() {
    if (((VurfEclipse)APP.getApp()).exportMode) {
      //seek to the correct position based on the current frame number ..
      println("jumping to frameCount " + ((VurfEclipse)APP.getApp()).frameCount);
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
  
        stream.read();
        //println("got gsvideo stream read");
        
        /*stream.loadPixels();
        out.loadPixels();
        //out.pixels = webcamStream.pixels;
        arrayCopy(stream.pixels, out.pixels);
        out.updatePixels();*/
        
        
        
        //out.getTexture().putPixelsIntoTexture();
        if (tex.putPixelsIntoTexture()) {
          out.beginDraw();
          if ((int)((VurfEclipse)APP.getApp()).random(100)<20)  println("VideoPlayer>>>video writing to " + out);
          out.image(tex,0,0,sc.w,sc.h);
          out.endDraw();
          return true;
        }
        
        //out.image(stream,0,0,sc.w,sc.h);
        
        /*out.loadPixels();
        out.pixels = webcamStream.pixels;
        out.updatePixels();*/
        return false;
      } else if (!stream.isSeeking()) {  // available
    	out.beginDraw();
        out.image(tex,0,0,sc.w,sc.h);
        out.endDraw();
        return true;
      }
    } else {
      println("Stream is null!");
    }
    return false; // no new frame available to draw
  }  
  
  
  public void beginDraw () {
    /*if (!pixelMode)
      out.loadPixels();
    else*/
      //out.beginDraw();
  }
  public void endDraw () {
    /*if (!pixelMode)
      out.updatePixels();
    else {*/
      //out.endDraw();
      //out.loadPixels();
      //out.loadTexture();
    //}
  }
}
