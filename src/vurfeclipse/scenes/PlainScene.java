package vurfeclipse.scenes;

import vurfeclipse.filters.Filter;
import vurfeclipse.filters.PlainDrawer;
import vurfeclipse.projects.Project;

class PlainScene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  
  PlainScene(Project host, int w, int h) {
    super(host, w, h);
    
    this.filterCount = 32;
  }
  
  public boolean setupFilters () {
    //super.initialise();
    filters = new Filter[filterCount];
    
    int i = 0;
    
    filters[i] = new PlainDrawer(this);
    filters[i].setFilterName("PlainDrawer BUF_SRC -> BUF_OUT");
    filters[i].setCanvases(getCanvasMapping("out"),getCanvasMapping("src"));//buffers[BUF_OUT], buffers[BUF_SRC]); 
    filters[i].initialise();

    
    highestFilter = i;
    return true;
  }
  
}
