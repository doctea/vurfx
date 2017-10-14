package vurfeclipse.scenes;

import java.util.ArrayList;

import vurfeclipse.APP;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.Filter;
import vurfeclipse.filters.KaleidoFilter;
import vurfeclipse.filters.MirrorFilter;
import vurfeclipse.filters.PhaseRGBFilter;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.ShowSceneSequence;
import vurfeclipse.streams.ParameterCallback;
import vurfeclipse.user.scenes.BlenderFX1;

public class QuasicrystalScene extends Scene {
  //int filterCount = 2;

  //Filter[] filters;// = new Filter[filterCount];

  final int colourModeCount = 8;

  public QuasicrystalScene(Project host, int w, int h) {
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
							//Float limit = (float) (100 * 12.0*Math.PI);
							Float adjusted = new Float(((Integer) value).floatValue()); //%(int)(float)limit);
	
							self.getFilter("Quasicrystal").changeParameterValue("time",
			    					//value
			    					adjusted/200
			    		);
			    	}
		    	}
	    });
  }

  public boolean setupFilters () {
	    //// START PLASMA SCENE
	    //plasmaScene.setCanvas("pix0","/pix0");
	    //os2.setCanvas("blendresult", "/blendresult");
	    addFilter(new ShaderFilter(this,"Quasicrystal.glsl") {
	    	@Override
	    	public void setParameterDefaults() {
	    		super.setParameterDefaults();
	    		/*addParameter("colourMode", new Integer(0), 0, colourModeCount);

	    		addParameter("width", new Integer(w/16), 0, w*2);
	    		addParameter("height", new Integer(h/16), 0, h*2);*/
	    		addParameter("time", new Float(0.0f),0.0f, 100000.0f);
	    		//addParameter("iGlobalTime", new Integer(0), 0, 1000000);
	    		//addParameter("time", new Float(10.0f), 0.0f, 1000000.0f);
	    		//addParameter("u_time_2", new Integer(10), 0, 1000000);

	    	}
	    	/*@Override
	    	public Filter nextMode() {
	    		changeParameterValue("colourMode", new Integer(((Integer)getParameterValue("colourMode")+1)));
	    		if ((Integer)getParameterValue("colourMode")>colourModeCount) {
	    			changeParameterValue("colourMode", new Integer(0));
	    		}
	    		return this;
	    	}*/

	    }.setFilterName("Quasicrystal").setCanvases("/temp1", this.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    //os.addFilter(new ShaderFilter(os,"CrossHatch.xml").setFilterName("CrossHatch").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));

	    addFilter(new BlendDrawer(this).setFilterName("BlendDrawer").setInputCanvas("/temp1").setOutputCanvas(this.getCanvasMapping("out")));

	    addFilter(new PhaseRGBFilter(this).setFilterName("PhaseRGB").setInputCanvas(getCanvasMapping("out")).setOutputCanvas(getCanvasMapping("out")));

	    return true;
  }



  public void setupSequences() {
		sequences.put("preset 1", new ShowSceneSequence(this, 2000));
		/*sequences.put("preset 2", new RGBFilterSequence2(this, 3000));
		sequences.put("preset 3", new RGBFilterSequence3(this, 4000));*/
  }

}
