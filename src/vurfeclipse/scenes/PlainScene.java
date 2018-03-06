package vurfeclipse.scenes;

import vurfeclipse.filters.Filter;
import vurfeclipse.filters.PlainDrawer;
import vurfeclipse.projects.Project;

public class PlainScene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  
  public PlainScene(Project host, int w, int h) {
    super(host, w, h);
    
  }
  
  public boolean setupFilters () {
    //super.initialise();
    //filters = new Filter[filterCount];
    
    int i = 0;
    
    addFilter(new PlainDrawer(this).setFilterName("PlainDrawer BUF_SRC -> BUF_OUT").setAliases("out","src")).initialise();

    return true;
  }
  
}
