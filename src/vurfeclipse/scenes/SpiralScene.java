package vurfeclipse.scenes;

import processing.core.PApplet;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.Filter;
import vurfeclipse.filters.SpiralDrawer;
import vurfeclipse.projects.Project;
import vurfeclipse.streams.ParameterCallback;

public class SpiralScene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  
  //GLGraphicsOffScreen inp;
  
  public SpiralScene(Project host, int w, int h, String canvas) { //GLGraphicsOffScreen inp) {
    super(host, w, h);
    
    //this.inp = inp;
    setCanvas("inp0", canvas);
    
    this.filterCount = 4;
  }
  
  
  public void setupCallbackPresets () {
    super.setupCallbackPresets();
    final Scene self = this;
    //println("adding callback 'spin'");
    this.callbacks.put("spin_forward", new ParameterCallback() {
       public void call(Object value) {
         //println("spin call " + value);
         int i = Integer.parseInt(value.toString());
         //((SpiralDrawer)self.filters[0]).setParameterValue("rotation", (float)i%360);
         //if(filters[0]!=null) ((SpiralDrawer)self.filters[0]).setParameterValue("totalRotate", (float)i%360);
         changeFilterParameterValue(0,"totalRotate",(float)(i%360));
       }
    });
    //println("adding callback 'spin'");
    this.callbacks.put("spin_back", new ParameterCallback() {
       public void call(Object value) {
         //println("spin call " + value);
         int i = Integer.parseInt(value.toString());
         changeFilterParameterValue(0,"totalRotate",(float)360-(i%360));
         //((SpiralDrawer)self.filters[0]).setParameterValue("rotation", (float)360-(i%360));
         //if(filters[0]!=null) ((SpiralDrawer)self.filters[0]).setParameterValue("totalRotate", (float)360-(i%360));
       }
    });
    
    this.callbacks.put("unwind", new ParameterCallback() {
      float sections = 30;
      float sectionStep = 0.1f;
      public void call(Object value) {
        int i = Integer.parseInt(value.toString());
        //if(filters[0]!=null) ((SpiralDrawer)self.filters[0]).setParameterValue("numSections",sections);
        changeFilterParameterValue(0,"numSections",sections);
        sections+=sectionStep;
        if (sections<2 || sections>120) sectionStep*=-1.0;
      }
    });      
    
    this.callbacks.put("zoom", new ParameterCallback() {
      //float sections = 30;
      //float sectionStep = 0.1;
      float minRadius = 0.0f, maxRadius = w/2f;
      float r = 300;
      public void call(Object value) {
        int i = Integer.parseInt(value.toString());
        changeFilterParameterValue(0,"endRadius",(float)((i%r)));
        //if(filters[0]!=null) ((SpiralDrawer)self.filters[0]).setParameterValue("endRadius",(float)((i%r)));
      }
    });      
    
    this.callbacks.put("radius", new ParameterCallback() {
      //float sections = 30;
      //float sectionStep = 0.1;
      float minRadius = 0.0f, maxRadius = w/4;
      float r = 300;
      public void call(Object value) {
        int i = Integer.parseInt(value.toString())/100;
        //changeFilterParameterValueFromSin(0,"radius",0.25f+abs(sin((Integer)value%maxRadius))); //(Integer)value%w/2APP.random(-1.0f,1.0f)); //map(abs(sin((Integer)value)), 0.0, 1.0, 20, maxRadius));

        changeFilterParameterValueFromSin(0,"radius",0.5f+(PApplet.sin(PApplet.radians(i))/2)); //(Integer)value%w/2APP.random(-1.0f,1.0f)); //map(abs(sin((Integer)value)), 0.0, 1.0, 20, maxRadius));
        
        //w%(Integer)value); //"radius",(float)((i%r)));
        //if(filters[0]!=null) ((SpiralDrawer)self.filters[0]).setParameterValue("endRadius",(float)((i%r)));
      }
    });          
    
    
  }
  
  public boolean setupFilters () {
    //super.initialise();
    filters = new Filter[filterCount];
    //println("DemoScene initialised " + this + " - filtercount is " + filterCount);
    int i = 0;
    
    //filters[i] = new TextDrawer(this).setBuffers(buffers[BUF_TEMP],buffers[BUF_TEMP]);
    //final TextDrawer ftd = (TextDrawer) filters[i];
    
    //filters[i] = new ShaderFilter(this, "Porthole.xml").setBuffers(buffers[BUF_TEMP],inp);
    
    filters[i] = new SpiralDrawer(this).setFilterName("SpiralDrawer").setAliases("temp","inp0"); //setBuffers(buffers[BUF_TEMP], inp);
    //filters[++i] = new SpiralDrawer(this).setBuffers(buffers[BUF_TEMP], buffers[BUF_TEMP]);
    

    filters[++i] = new BlendDrawer(this).setFilterName("BlendDrawer").setAliases("out","temp"); //setBuffers(buffers[BUF_OUT],buffers[BUF_TEMP]);
    final BlendDrawer fbd = (BlendDrawer) filters[i];
    ((BlendDrawer)filters[i]).setBlendMode(9);
    
    /*
    host.getStream("beat").registerEventListener("beat_8",
      new ParameterCallback () {
        public void call(Object value) {
          //ftd.setText(value.toString());
          int i = Integer.parseInt(value.toString());
          ftd.setText(values[(int)random(0,values.length)]);
          //ftd.setRotation(i%360);//(int)random(i));
          fbd.setMute(false);
        }
      }
    );
    */    
    
    highestFilter = i;
    return true;
  }
  
}
