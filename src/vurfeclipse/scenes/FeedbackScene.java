package vurfeclipse.scenes;

import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.DebugDrawer;
import vurfeclipse.filters.DiskWriterFilter;
import vurfeclipse.filters.Filter;
import vurfeclipse.filters.FilterChain;
import vurfeclipse.filters.GLColourFilter;
import vurfeclipse.filters.KaleidoFilter;
import vurfeclipse.filters.MirrorFilter;
import vurfeclipse.filters.PlainDrawer;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.filters.WebcamFilter;
import vurfeclipse.streams.*;

class FeedbackScene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  
  FeedbackScene(Project host, int w, int h) {
    super(host, w, h);
    
    this.filterCount = 32;
  }
  
  public boolean setupFilters () {
    //super.initialise();
    filters = new Filter[filterCount];
    
    /*Stream stream = new Stream("Test Stream");
    BeatStream beatStream = new BeatStream("Beat Stream", 125.0);
    this.addStream("test", stream);
    this.addStream("beat", beatStream);*/
    Stream stream = host.getStream("test");
    BeatStream beatStream = (BeatStream) host.getStream("beat");
       
    //println("DemoScene initialised " + this + " - filtercount is " + filterCount);
    int i = 0;
    filters[i] = new BlankFilter(this);
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    //filters[i].setCanvases(getCanvas("out"),getCanvas("out"));
    filters[i].setCanvases(getCanvasMapping("out"),getCanvasMapping("out"));
    filters[i].initialise();
    
    filters[++i] = new FilterChain(this, "Webcam 1 Image into BUF_SRC");
    //filters[i].setBuffers(buffers[BUF_SRC], buffers[BUF_SRC]);
    filters[i].setCanvases(getCanvasMapping("src"),getCanvasMapping("src"));    
    ((FilterChain)filters[i]).addFilterDefaults(new WebcamFilter(this,1));
    ((FilterChain)filters[i]).addFilterDefaults(new MirrorFilter(this)); /// hmmm including this here gives mad mirror-flicker... wonder if this is because the webcam is slower than the draw() loop..?
    //((FilterChain)filters[i]).addFilter((new PlainDrawer(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    //((FilterChain)filters[i]).addFilter((new ColourFilter(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    //filters[i].setMute(true);
    filters[i].initialise();   
    
    /*filters[++i] = new FilterChain(this, "Webcam 0 Image into BUF_SRC2");
    filters[i].setBuffers(buffers[BUF_SRC2], buffers[BUF_SRC2]);
    ((FilterChain)filters[i]).addFilterDefaults(new WebcamFilter(this,0));
    ((FilterChain)filters[i]).addFilterDefaults(new MirrorFilter(this)); /// hmmm including this here gives mad mirror-flicker... wonder if this is because the webcam is slower than the draw() loop..?
    //((FilterChain)filters[i]).addFilter((new PlainDrawer(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    //((FilterChain)filters[i]).addFilter((new ColourFilter(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]));
    filters[i].initialise();   */
    
    /*filters[++i] = new VideoPlayer(this, "tworld84.dv.ff.avi");
    //filters[++i] = new VideoPlayer(this, "station.mov");
    filters[i].setBuffers(buffers[BUF_SRC], buffers[BUF_TEMP]);
    filters[i].initialise();*/    
    
    //filters[++i] = (new GLColourFilter(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]); //copy from SRC to TEMP
    filters[++i] = (new GLColourFilter(this)).setCanvases(getCanvasMapping("temp"),getCanvasMapping("src")); //copy from SRC to TEMP
    filters[i].setFilterName("GLColourFilter BUF_SRC -> BUF_TEMP");
    //filters[++i] = (new ColourFilter(this)).setBuffers(buffers[BUF_OUT],buffers[BUF_SRC]); //copy from SRC to TEMP
    //filters[++i] = (new PlainDrawer(this)).setBuffers(buffers[BUF_TEMP],buffers[BUF_SRC]); //copy from SRC to TEMP
    //((ColourFilter)filters[i]).autoChange = true;
    final Filter tgl = filters[i];
    beatStream.registerEventListener("beat_1", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("Handler: call(" + value + ")");
          //cf.setParameterValue("test", value); 
          //t.toggleMute();
          tgl.nextMode();
        }
      }
    );   
    filters[i].initialise();
    
    
    filters[++i] = new PlainDrawer(this);
    filters[i].setFilterName("PlainDrawer BUF_TEMP -> BUF_OUT");
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_TEMP]); 
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("temp"));
    filters[i].initialise();

    
    filters[++i] = new PlainDrawer(this);
    filters[i].setFilterName("PlainDrawer BUF_SRC -> BUF_OUT");
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_SRC]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("src"));    
    filters[i].initialise();
    

    filters[++i] = new BlendDrawer(this);//, 0, 0, w/4, h/4);
    filters[i].setFilterName("BlendDrawer BUF_SRC2 -> BUF_OUT");
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_SRC2]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("inp2"));
    /*final Filter fa = filters[i];
    beatStream.registerEventListener("bar_2", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("Handler: call(" + value + ")");
          //cf.setParameterValue("test", value); 
          //t.toggleMute();
          //tgl.nextMode();
          fa.toggleMute();
        }
      }
    );*/
    filters[i].initialise();


    //filters[++i] = new BlendDrawer(this,0,0,w/2,h/4,15);//, 0, 0, w/4, h/4);
    filters[++i] = new BlendDrawer(this,0,0,w/2,h/2,35);//, 0, 0, w/4, h/4);
    filters[i].setFilterName("FEEDBACK: Rotated BlendDrawer BUF_SRC2 -> BUF_TEMP3");
    //filters[i].setBuffers(buffers[BUF_TEMP3], buffers[BUF_SRC2]);
    filters[i].setCanvases(getCanvasMapping("temp3"), getCanvasMapping("src2"));
    ((BlendDrawer)filters[i]).setBlendMode(12);
    filters[i].initialise();
    
    filters[++i] = new KaleidoFilter(this);
    //filters[i].setBuffers(buffers[BUF_TEMP3], buffers[BUF_TEMP3]);
    filters[i].setCanvases(getCanvasMapping("temp3"), getCanvasMapping("temp3"));
    filters[i].initialise();
    
    filters[++i] = new BlendDrawer(this);
    //filters[++i] = new PlainDrawer(this);
    //((BlendDrawer)filters[i]).setBlendMode(12);
    filters[i].setFilterName("FEEDBACK: PlainDrawer BUF_TEMP2 -> BUF_OUT");
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_TEMP3]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("temp3"));
    filters[i].initialise();


    /*filters[++i] = new FilterChain(this, "Stored ImageListDrawer/PlainDrawer Chain (BUF_SRC2 -> BUF_OUT)");
    filters[i].setMute(true);
    filters[i].setBuffers(buffers[BUF_SRC2], buffers[BUF_SRC]);
    ((FilterChain)filters[i]).addFilterDefaults(new ImageListDrawer(this));
    ((FilterChain)filters[i]).addFilter(new PlainDrawer(this).setBuffers(buffers[BUF_OUT], buffers[BUF_SRC2]));
    filters[i].initialise();   */

    
    
    
    /*filters[++i] = new PointDrawer(this);    
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_SRC]);
    filters[i].initialise();*/

/*
    filters[++i] = new PointDrawer(this);    
    ((PointDrawer)filters[i]).setXYOffset(w,h);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_SRC]);
    filters[i].initialise();
    
    filters[++i] = new PointDrawer(this);    
    ((PointDrawer)filters[i]).setXYOffset(w/2,h);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_SRC]);
    filters[i].initialise();

*/

    filters[++i] = new MirrorFilter(this);
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("out"));    
    filters[i].setMuted(true);
    final Filter t = filters[i];
    /*beatStream.registerEventListener("beat_8", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("Handler: call(" + value + ")");
          //cf.setParameterValue("test", value); 
          t.toggleMute();
        }
      }
    );*/
    filters[i].initialise();   


    //filters[++i] = new ShaderFilter(this,"pulsatingEmboss-Compiled.xml"); 
    filters[++i] = new ShaderFilter(this,"Feedback.xml"); 
    filters[i].setFilterName("Feedback");
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("out"));    
    filters[i].setMuted(true);
    final Filter fbk = filters[i];
    /*beatStream.registerEventListener("beat_1", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("beat_2 kaleidoscope: call(" + value + ")");
          //cf.setParameterValue("test", value); 
          fbk.setParameterValue("amp",random(.1,6.));
          fbk.setParameterValue("radio",radians(random(60)));
        }
      }
    );*/
    
    filters[i].initialise();    


    //filters[++i] = new ShaderFilter(this,"AngularKaleidoScope.xml"); 
    filters[++i] = new ShaderFilter(this,"Edges.xml"); 
    filters[i].setFilterName("Edges");
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("out"));    
    filters[i].setMuted(true);
    filters[i].initialise();    

    /*filters[++i] = new ImageWriter(this);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_TEMP]); 
    filters[i].initialise();*/

    filters[++i] = new KaleidoFilter(this);
    //filters[++i] = new ShaderFilter(this,"KaleidoScope.xml"); 
    //filters[i].setBuffers(buffers[BUF_SRC], buffers[BUF_SRC]);
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("out"));    
    filters[i].setMuted(true);
    final Filter tk = filters[i];
    /*beatStream.registerEventListener("bar_1", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("beat_2 kaleidoscope: call(" + value + ")");
          //cf.setParameterValue("test", value); 
          tk.nextMode();
        }
      }
    );*/
/*    beatStream.registerEventListener("bar_3", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("Handler: call(" + value + ")");
          //cf.setParameterValue("test", value); 
          tk.toggleMute();
        }
      }
    );*/
    filters[i].initialise();


/*
    filters[++i] = new FilterChain(this, "Text Outputter"); //buffers[BUF_TEMP], buffers[BUF_TEMP2]);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_TEMP2]);
    //((FilterChain)filters[i]).addFilter(new TextDrawer(this, "Texty").setBuffers(buffers[BUF_TEMP2],buffers[BUF_TEMP2]));
    final TextDrawer ftd = (TextDrawer)new TextDrawer(this, "Texty").setBuffers(buffers[BUF_TEMP2],buffers[BUF_TEMP2]);
    final FilterChain fc = (FilterChain)filters[i];
    beatStream.registerEventListener("beat_1",
      new ParameterCallback () {
        public void call(Object value) {
          //ftd.setText(value.toString());
          String values[] = {
            ":)", "MDMA", "LSD-25", "DMT", ">.=.<", "dANCe", "rob0t", "ZX", "(:", "(c)", "doctea", "guru", "Crack\nZombie", "vurf", "greetz",
            "::vurf::", "V:U:R:F", "\\//U/R/F/", "b3r7", "SLiPs", "MMXIII", "MCMLXXX", "Sandoz", "cola", "acid", "acidtest", "electric", "kool-aid",
            "RAVE", "rave", "XtC", "SocioSuki", "science", "drop acid", "make tea", "art", "peace", "free", "<3 echo", "<3", (new Date()).toString()
          };
          ftd.setText(values[(int)random(0,values.length)]);
        }
      }
    );
    beatStream.registerEventListener("beat_2",
      new ParameterCallback () {
        public void call(Object value) {
          //println("got beat_2 of " + value + " " + Integer.parseInt(value.toString())%2);
          int v = Integer.parseInt(value.toString());
          if (v%2==1) {
            //ftd.toggleMute();
            //println("toggle");
            //fc.toggleMute();
            fc.setMute(false);
          } else {
            fc.setMute(true);
          }
          if (v%8==0) {
            ftd.setColour((int)random(64,255));
          }

        }
      }
    );
    ((FilterChain)filters[i]).addFilter(ftd);
    BlendDrawer bd = (BlendDrawer) new BlendDrawer(this).setBuffers(buffers[BUF_OUT],buffers[BUF_TEMP2]);
    bd.setBlendMode(9);
    ((FilterChain)filters[i]).addFilter(bd);
    filters[i].initialise();*/


    filters[++i] = new DiskWriterFilter(this,"testoutput.ogg"); 
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("out"));    
    //filters[i].setMute(true);
    filters[i].initialise();     


    /*    
    //filters[++i] = new FrameCatcher(this);
    filters[++i] = new GLDelayBlenderFilter(this);
    filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_TEMP]); // think this does good fx ?
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_OUT]); // take buffer in at this stage and output back to output
    filters[i].setMute(true);
    filters[i].initialise();   
    */
    
    filters[++i] = new DebugDrawer(this);
    final Filter cf = filters[i];
    stream.registerEventListener("test", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("Handler: call(" + value + ")");
          cf.setParameterValue("test", value); 
        }
      }
    );
    beatStream.registerEventListener("beat", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("beat: call(" + value + ")");
          cf.setParameterValue("beat", value); 
        }
      }
    );
    beatStream.registerEventListener("bar", 
      new ParameterCallback () {
        public void call(Object value) {
          //println("bar: call(" + value + ")");
          cf.setParameterValue("bar", value); 
        }
      }
    );
    //filters[i].setBuffers(buffers[BUF_OUT], buffers[BUF_SRC]);
    filters[i].setCanvases(getCanvasMapping("out"), getCanvasMapping("src"));    
    filters[i].initialise();
    return true;
  }
  
}
