package vurfeclipse.scenes;

import vurfeclipse.filters.Filter;
import vurfeclipse.filters.KaleidoFilter;
import vurfeclipse.filters.MirrorFilter;
import vurfeclipse.projects.Project;

class KaleidoScene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  
  KaleidoScene(Project host, int w, int h) {
    super(host, w, h);
    
    this.filterCount = 3;
  }
  
  public boolean setupFilters () {
    //super.initialise();
    filters = new Filter[filterCount];
    int i = 0;
    
    /*filters[++i] = new BlankFilter(this);
    filters[i].setBuffers(buffers[BUF_OUT],buffers[BUF_OUT]);
    filters[i].initialise();*/
    
    //filters[i] = new MirrorFilter(this).setBuffers(buffers[BUF_OUT],buffers[BUF_SRC]);
    filters[i] = new MirrorFilter(this).setFilterName("MirrorFilter").setCanvases(getCanvasMapping("out"),getCanvasMapping("src")); //buffers[BUF_OUT],buffers[BUF_SRC]);    
    filters[i].setMute(true);
    
    filters[++i] = new KaleidoFilter(this).setFilterName("KaleidoFilter").setCanvases(getCanvasMapping("out"),getCanvasMapping("out"));//buffers[BUF_OUT],buffers[BUF_OUT]);
    
    println ("just created kaleido " + filters[i].getFilterName());
    //System.exit(0);
    
    
    final Filter cf = filters[i];
    
    highestFilter = i;
    return true;
  }
  
}
