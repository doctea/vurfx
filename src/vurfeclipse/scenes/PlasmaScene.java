package vurfeclipse.scenes;

import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.Filter;
import vurfeclipse.filters.KaleidoFilter;
import vurfeclipse.filters.MirrorFilter;
import vurfeclipse.filters.PhaseRGBFilter;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.streams.ParameterCallback;

public class PlasmaScene extends Scene {
  //int filterCount = 2;
  
  //Filter[] filters;// = new Filter[filterCount];
  
  public PlasmaScene(Project host, int w, int h) {
    super(host, w, h);
    
    this.filterCount = 3;
  }
  
  public void setupCallbackPresets () {
	    super.setupCallbackPresets();
	    final Scene self = this;
	    //println("adding callback 'spin'");
	    this.callbacks.put("warp", new ParameterCallback() {
	    	@Override
	    	  public void call(Object value) {
	    		if (value instanceof Integer) {
	    			//os2.getFilter("Plasma").changeParameterValue("u_time_2", (Integer)((Integer)value%(int)(Math.PI*12)));
					Float limit = (float) (100 * 12.0*Math.PI);
					Integer adjusted = ((Integer)value%(int)(float)limit);
	    			
					self.getFilter("Plasma").changeParameterValue("u_time_2", 
	    					//value
	    					adjusted
	    			);
	    		}
	    	  }    	    	
	    });
  }
  
  public boolean setupFilters () {
	    //// START PLASMA SCENE       
	    
	    //plasmaScene.setCanvas("pix0","/pix0");
	    //os2.setCanvas("blendresult", "/blendresult");
	    addFilter(new ShaderFilter(this,"Plasma.xml") {
	    	@Override
	    	public void setParameterDefaults() {
	    		super.setParameterDefaults();
	    		addParameter("width", new Integer(w/16), 0, w*2);
	    		addParameter("height", new Integer(h/16), 0, h*2);
	    		addParameter("u_time_2", new Integer(10), 0, 1000000);
	    	}

	    }.setFilterName("Plasma").setCanvases("/temp1", this.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    //os.addFilter(new ShaderFilter(os,"CrossHatch.xml").setFilterName("CrossHatch").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
	    
	    addFilter(new BlendDrawer(this).setFilterName("BlendDrawer").setInputCanvas("/temp1").setOutputCanvas(this.getCanvasMapping("out")));
	    
	    
	    addFilter(new PhaseRGBFilter(this).setInputCanvas(getCanvasMapping("out")).setOutputCanvas(getCanvasMapping("out")));
	    
	    
	    return true;
  }
  
  public boolean blah_setupFilters () {
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
