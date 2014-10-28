package vurfeclipse.scenes;

import vurfeclipse.APP;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.Filter;
import vurfeclipse.filters.KaleidoFilter;
import vurfeclipse.filters.MirrorFilter;
import vurfeclipse.filters.PhaseRGBFilter;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.streams.ParameterCallback;
import vurfeclipse.user.scenes.BlenderFX1;

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
	    
	    addFilter(new PhaseRGBFilter(this).setFilterName("PhaseRGB").setInputCanvas(getCanvasMapping("out")).setOutputCanvas(getCanvasMapping("out")));
	    
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

  public void setupSequences() {
		sequences.put("preset 1", new RGBFilterSequence1(this, 2000));
  }
  
}

class RGBFilterSequence1 extends Sequence {
	public RGBFilterSequence1(PlasmaScene plasmaFX1, int i) {
		// TODO Auto-generated constructor stub
		super(plasmaFX1,i);
	}
	public void setValuesForNorm(double norm, int iteration) {
		//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
		if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
		//host.getFilter("BlendDrawer1").setParameterValue("Opacity", (float)norm);
		host.getFilter("PhaseRGB").changeParameterValueFromSin("rshift", (float)Math.sin(norm));
		host.getFilter("PhaseRGB").changeParameterValueFromSin("gshift", (float)Math.sin(norm*norm));
		host.getFilter("PhaseRGB").changeParameterValueFromSin("bshift", (float)Math.sin(-norm/2));
	}
	@Override public void onStart() {
		//this.setLengthMillis((int)APP.getApp().random(1,5) * 500);
		/*for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
			host.host.getSceneForPath("/ImageListScene1").getFilter("ImageListDrawer1").nextMode();
		for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
			host.host.getSceneForPath("/ImageListScene2").getFilter("ImageListDrawer2").nextMode();*/
	}
}