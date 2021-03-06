package vurfeclipse.scenes;

import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;

public class VideoScene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  String filename;
  
  VideoScene(Project host, int w, int h) {
    super(host, w, h);
    
  }
  public VideoScene(Project host, int w, int h, String filename) {
    this(host,w,h);
    this.filename = filename;
  }
  
  public boolean setupFilters () {
    //super.initialise();
    filters = new Filter[filterCount];
           
    int i = 0;
    
    filters[i] = new FilterChain(this, "VideoPlayer for " + filename); //VideoPlayer(this, filename); //"data/video/129-Probe 7 - Over and Out(1)-00.mkv");
    filters[i].setAliases("out","out");//setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    final VideoPlayer vs = (VideoPlayer) new VideoPlayer(this, filename).setFilterName("video");
    vs.loadDirectory("video-sources/");
    ((FilterChain)filters[i]).addFilterDefaults(vs);
    //((FilterChain)filters[i]).addFilterDefaults(new MirrorFilter(this)); /// hmmm including this here gives mad mirror-flicker... wonder if this is because the webcam is slower than the draw() loop..?
    //((FilterChain)filters[i]).addFilter((new PlainDrawer(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    //((FilterChain)filters[i]).addFilter((new ColourFilter(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    //filters[i].initialise();   

    highestFilter = i;
    return true;
  }
  
}
