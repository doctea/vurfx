package vurfeclipse.user.scenes;
import java.util.ArrayList;

import vurfeclipse.filters.Filter;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.filters.ToonFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.ChainSequence;
import vurfeclipse.sequence.ChangeParameterSequence;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.ShowFilterSequence;

public class OutputFX1 extends SimpleScene {
	public OutputFX1(Project pr, int w, int h) {
		super(pr, w, h);
		setSceneName("OutputFX1 Instance");
	}

	public boolean setupFilters() {

	    this.addFilter(new ShaderFilter(this,"Pixelate.glsl")
	    		.addParameter("pixel_size", new Float(25.0f),new Float(0.0f),new Float((float)this.w/8))
	    		.setFilterName("Pixelate")
	    		.setAliases("out","src")
	    ); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
	    this.addFilter(new ToonFilter(this)//,"ToonVert.glsl")
	    		.setFilterName("Toon")
	    		.setAliases("out","src")
	    ); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));

	    return true;
	}


   // OUTPUT FILTERS

    /*SimpleScene os = (SimpleScene) new SimpleScene(this,w,h).setSceneName("OutputShader");

    os.setCanvas("out", "/out");
    os.setCanvas("inp0","/out");
    //os.setCanvas("blank","/blank");
    //os.addFilter(new PlainDrawer(os).setFilterName("BlankDrawer").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("blank")));

    //os.addFilter(new BlankFilter(os).setOutputCanvas("/out"));

    os.addFilter(new ShaderFilter(os,"Edges.xml").setFilterName("Edges").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    os.addFilter(new ShaderFilter(os,"Toon.xml").setFilterName("Toon").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
    //os.addFilter(new ShaderFilter(os,"pulsatingEmboss.xml").setFilterName("pulsatingEmboss").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));

    this.addSceneInputOutputCanvas(
      os,
      "/out",
      "/out"
    );*/


    public void setupSequences() {
			sequences.put("preset 1", new OutputSequence1(this, 0));
	
			sequences.put("show_toon",  new ShowFilterSequence(this, 0, getPath()+"/fl/Toon"));
			sequences.put("show_edges", new ShowFilterSequence(this, 0, getPath()+"/fl/Pixelate"));
	
			sequences.put("show_toonandedges", new ChainSequence(0).addSequence(sequences.get("show_toon")).addSequence(sequences.get("show_edges")));
			
			sequences.put("pixel_size",  new ChainSequence(2000)
					//.addSequence(new ShowFilterSequence(this, 0, getPath()+"/fl/Toon"))
					.addSequence(new ShowFilterSequence(this, 0, getPath()+"/fl/Pixelate"))
					.addSequence(new ChangeParameterSequence(this, getPath()+"/fl/Pixelate", "pixel_size", 64.0f, 2000))
			);
	
			//.addSequence(getSequence("show_feedback"))
				//.addSequence(host.getSceneForPath("/sc/BlankerScene").getSequence("feedback"))
				//.addSequence(new ShowFilterSequence(this, 0, getPath()+"/fl/Feedback")));
    }


	class OutputSequence1 extends Sequence {
		public OutputSequence1() {}
		public OutputSequence1(OutputFX1 outputFX1, int i) {
			super(outputFX1,i);
		}
		@Override
		public ArrayList<Mutable> getMutables() {
			return new ArrayList<Mutable>();
		}
		@Override
		public void __setValuesForNorm(double norm, int iteration) {
			//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
			if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
			//host.getFilter("BlendDrawer1").changeParameterValue("Opacity", (float)norm);
		}
		@Override public void onStart() {
			if (random(0f,1.0f)>=0.5f) getHost().host.getSceneForPath(getPath()).getFilter("Toon").toggleMute();
			//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("pulsatingEmboss").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
			//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("CrossHatch").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
			if (random(0f,1.0f)>=0.5f) getHost().host.getSceneForPath(getPath()).getFilter("Pixelate").toggleMute();
		}
		@Override public void onStop() {	}
		@Override
		public boolean notifyRemoval(Filter newf) {
			if (	getHost().host.getSceneForPath(getPath()).getFilter("Toon") == newf || 
					getHost().host.getSceneForPath(getPath()).getFilter("Pixelate")==newf
				) {
				this.setEnabled(false);
				return true;
			}
			return false;
		}
		@Override
		public void removeSequence(Sequence self) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void __setValuesAbsolute(double pc, int iteration) {
			// TODO Auto-generated method stub
			
		}
	}
}

