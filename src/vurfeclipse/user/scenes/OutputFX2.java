package vurfeclipse.user.scenes;
import java.util.ArrayList;

import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.FeedbackFilter;
import vurfeclipse.filters.KaleidoFilter;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.ChainSequence;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.ShowFilterSequence;

public class OutputFX2 extends SimpleScene {
	public OutputFX2(Project pr, int w, int h) {
		super(pr, w, h);
		setSceneName("OutputFX2 Instance");
	}

	public boolean setupFilters() {
	    //this.addFilter(new ShaderFilter(this,"Edges.xml").setFilterName("Edges").setCanvases(this.getCanvasMapping("out"), this.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
	    //this.addFilter(new ShaderFilter(this,"Toon.xml").setFilterName("Toon").setCanvases(this.getCanvasMapping("out"), this.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));


	    final SimpleScene os2 = this;//(SimpleScene) new SimpleScene(this,w,h).setSceneName("OutputShader2");
	    //os2.setCanvas("out", "/out");
	    //os2.setCanvas("pix0","/pix0");
	    //os2.setCanvas("blendresult", "/blendresult");

	    
	    os2.addFilter(new FeedbackFilter(os2).setFilterName("Feedback")
	    		/*ShaderFilter(os2,"Feedback.glsl")
	    		.setFilterName("Feedback")
	    		.addParameter("dirs", new Integer(9), 1, 16)
	    		.addParameter("amp", new Float(4.1f), 0.5f, 10f)
	    		.addParameter("radio", new Float(7.0f), 3.0f, 15f)
	    		.addParameter("distance_r", new Integer(2), 0, 8)
	    		.addParameter("distance_g", new Integer(4), 0, 8)
	    		.addParameter("distance_b", new Integer(6), 0, 8)
	    		.addParameter("distance_a", new Integer(8), 0, 8)*/
	    		.setAliases("out","src")); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    //os.addFilter(new ShaderFilter(os,"CrossHatch.xml").setFilterName("CrossHatch").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));

	    os2.addFilter(new KaleidoFilter(os2).setFilterName("Kaleido").setAliases("out","src")); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
	    //os.addFilter(new GLColourFilter(os).setFilterName("GLColourFilter"));

	    os2.addFilter(new BlendDrawer(os2).setFilterName("BlendDrawer pix0 to out").setAliases("out", "pix0").setParameterValue("BlendMode",8));
	    //os.addFilter(new BlendDrawer(os).setFilterName("BlendDrawer inp0 to out").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0")).setParameterValue("BlendMode",9));

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
			//sequences.put("preset 1", new OutputSequence1(this, 0));
			sequences.put("show_kaleido", new ShowFilterSequence(this, 0, getPath()+"/fl/Kaleido"));
			sequences.put("show_feedback", new ChainSequence(2000)
				//.addSequence(getSequence("show_feedback"))
				.addSequence(host.getSceneForPath("/sc/BlankerScene").getSequence("feedback"))
				.addSequence(new ShowFilterSequence(this, 0, getPath()+"/fl/Feedback")));

			//sequences.put("show_blend", new ShowFilterSequence(this, 0, getPath()+"/fl/BlendDrawer pix0 to out"));

			//sequences.put("show_blend", new ShowFilterSequence(this, 0, host.getSceneForPath(getPath()).getFilter("Kaleido")));
    }



}
