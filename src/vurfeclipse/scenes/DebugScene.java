package vurfeclipse.scenes;
import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;

public class DebugScene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  
  public DebugScene(Project host, int w, int h) {
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
    
    final Filter cf = new DebugDrawer(this);
    /*host.getStream("test").registerEventListener("test", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("Handler: call(" + value + ")");
          cf.setParameterValue("test", value); 
        }
      }
    );*/
    /*host.getStream("beat").registerEventListener("beat", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("beat: call(" + value + ")");
          cf.setParameterValue("beat", value); 
        }
      }
    );
    host.getStream("beat").registerEventListener("bar", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("bar: call(" + value + ")");
          cf.setParameterValue("bar", value); 
        }
      }
    );*/
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_SRC]);
    //filters[i].setCanvases(getCanvas("out"), getCanvas("src"));
    //filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("src"));
    //filters[i].setCanvases(getPath()+"/out",getPath()+"/inp0");
    //filters[i].initialise();
    addFilter(cf);
    
    highestFilter = i;
    return true;
  }
  
}
