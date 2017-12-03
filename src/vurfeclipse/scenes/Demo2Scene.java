package vurfeclipse.scenes;

import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;

public class Demo2Scene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  
  public Demo2Scene(Project host, int w, int h) {
    super(host, w, h);
    
    this.filterCount = 32;
  }
  
  public boolean setupFilters () {
    //super.initialise();
    filters = new Filter[filterCount];
    
       
    //System.out.println("DemoScene initialised " + this + " - filtercount is " + filterCount);
    int i = 0;
    
    
    filters[++i] = new PlainDrawer(this);
    filters[i].setFilterName("PlainDrawer BUF_SRC -> BUF_OUT");
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_SRC]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("src"));
    //filters[i].initialise();
    

    filters[++i] = new MirrorFilter(this);
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("out"));
    filters[i].setMuted(true);
    final Filter t = filters[i];
    
    filters[++i] = new KaleidoFilter(this);
    filters[i].setFilterName("FEEDBACK: KaleidoFilter BUF_TEMP3 -> BUF_TEMP3");
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("out"));
    //filters[i].initialise();
    
/*    filters[++i] = new FilterChain(this, "Stored ImageListDrawer/PlainDrawer Chain (BUF_TEMP4 -> BUF_OUT)");
    final Filter ild = filters[i];
    filters[i].setMute(true);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    ((FilterChain)filters[i]).addFilterDefaults(new ImageListDrawer(this, "files_3.lst"));*/
    
    /*host.getStream("beat").registerEventListener("beat_4", new ParameterCallback() {
      public void call(Object value) {
        ild.nextMode();  
        //System.out.println("got value " +value);
        if (Integer.parseInt(value.toString())%16==0) {
          //System.out.println("toggling imagelistdrawer");
          ild.toggleMute();
        }
      }
    });*/
    //((FilterChain)filters[i]).addFilter(new PlainDrawer(this).setBuffers(buffers[BUF_OUT], buffers[BUF_TEMP4]));
    //filters[i].initialise();   

    
    /*beatStream.registerEventListener("beat_8", 
      new ParameterCallback () {
        public void call(Object value) {
          //System.out.println("Handler: call(" + value + ")");
          //cf.setParameterValue("test", value); 
          t.toggleMute();
        }
      }
    );*/
    //filters[i].initialise();   


    //filters[++i] = new ShaderFilter(this,"pulsatingEmboss-Compiled.xml"); 
    filters[++i] = new ShaderFilter(this,"Feedback.glsl"); 
    filters[i].setFilterName("Feedback");
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("out"));
    filters[i].setMuted(true);
    final Filter fbk = filters[i];
    /*host.getStream("beat").registerEventListener("beat_1", 
      new ParameterCallback () {
        public void call(Object value) {
          //System.out.println("beat_2 kaleidoscope: call(" + value + ")");
          //cf.setParameterValue("test", value); 
          fbk.toggleMute();
          fbk.setParameterValue("amp",random(.1,6.));
          fbk.setParameterValue("radio",radians(random(60)));
        }
      }
    );*/
    
    //filters[i].initialise();    


    
    highestFilter = i;
    return true;
  }
  
}
