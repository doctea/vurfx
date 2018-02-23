package vurfeclipse.scenes;

import vurfeclipse.filters.Filter;
import vurfeclipse.projects.Project;

public class SimpleScene extends Scene {
  //Filter[] filters;// = new Filter[filterCount];
  //BUF_OUT = 0;
  //BUF_MAX = 1;
  
  public SimpleScene(Project host, int w, int h) {
    super(host, w,h);
    
    //setSceneName(this.getClass().getCanonicalName());
    
    //this.filters = new Filter[filterCount];
  }
  
  public boolean setupFilters () {
    /*
    //super.initialise();
    //System.out.println("SimpleScene bufmax is " + BUF_MAX);
    filters = new Filter[filterCount];
    //System.out.println("DemoScene initialised " + this + " - filtercount is " + filterCount);
    
    int i = 0;

    filters[i] = new WebcamFilter(this);
    filters[i].setBuffers(buffers[BUF_SRC], buffers[BUF_SRC]);
    filters[i].initialise();
    

    filters[++i] = new GLColourFilter(this);
    filters[i].setBuffers(buffers[BUF_TEMP], buffers[BUF_SRC]);
    filters[i].initialise();
    
    filters[++i] = new PlainDrawer(this);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_SRC]);
    filters[i].initialise();
    

    filters[++i] = new PointDrawer(this);  // PointDrawer works ..
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_TEMP]);
    filters[i].initialise();
    
    
    filters[++i] = new MirrorFilter(this);  // doesnt work .. // PointDrawer also doesn't work here (but works in DemoScene..)
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_TEMP]); //works... writing from the temp buffer to output..
    //filters[i].setBuffers(buffers[BUF_TEMP], buffers[BUF_OUT]); 
    filters[i].initialise();
    
    
    filters[++i] = new DebugDrawer(this);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].initialise();
    */
    return true;
  }
  
}
