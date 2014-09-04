package vurfeclipse.scenes;

import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;

public class WebcamScene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  int cameraNumber = 0;
  
  public WebcamScene(Project host, int w, int h) {
    super(host, w, h);
    
    this.filterCount = 32;
  }
  public WebcamScene(Project host, int w, int h, int cameraNumber) {
    this(host,w,h);
    this.cameraNumber = cameraNumber;
  }
  
  public boolean setupFilters () {
    //super.initialise();
    filters = new Filter[filterCount];
           
    int i = 0;
    /*
    filters[i] = new BlankFilter(this);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].initialise();*/
    
    /*filters[++i] = new BlankFilter(this);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].initialise();*/

    /*
    filters[++i] = new WebcamFilter(this);
    filters[i].setBuffers(buffers[BUF_SRC], buffers[BUF_SRC]);
    filters[i].initialise();
    */
    /*
    filters[++i] = new FilterChain(this, "Webcam 1 Image into BUF_SRC");
    filters[i].setBuffers(buffers[BUF_SRC], buffers[BUF_SRC]);
    ((FilterChain)filters[i]).addFilterDefaults(new WebcamFilter(this,1));
    ((FilterChain)filters[i]).addFilterDefaults(new MirrorFilter(this)); /// hmmm including this here gives mad mirror-flicker... wonder if this is because the webcam is slower than the draw() loop..?
    //((FilterChain)filters[i]).addFilter((new PlainDrawer(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    //((FilterChain)filters[i]).addFilter((new ColourFilter(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    filters[i].initialise();   
    */
    
    filters[i] = new FilterChain(this, "Webcam 0 Image into " + getCanvasMapping("out"));
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].setOutputCanvas(getCanvasMapping("out"));
    ((FilterChain)filters[i]).addFilterDefaults(new WebcamFilter(this,cameraNumber));
    ((FilterChain)filters[i]).addFilterDefaults(new MirrorFilter(this)); /// hmmm including this here gives mad mirror-flicker... wonder if this is because the webcam is slower than the draw() loop..?
    //((FilterChain)filters[i]).addFilter((new PlainDrawer(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    //((FilterChain)filters[i]).addFilter((new ColourFilter(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    //filters[i].initialise();   

    highestFilter = i;
    return true;
  }
  
}
