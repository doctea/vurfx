package vurfeclipse.user.scenes;
import java.util.ArrayList;

import vurfeclipse.APP;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.KaleidoFilter;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.ChainSequence;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.ShowFilterSequence;
import vurfeclipse.user.scenes.OutputFX1.OutputSequence1;
 
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

	    os2.addFilter(new ShaderFilter(os2,"Feedback.xml").setFilterName("Feedback").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    //os.addFilter(new ShaderFilter(os,"CrossHatch.xml").setFilterName("CrossHatch").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));    

	    os2.addFilter(new KaleidoFilter(os2).setFilterName("Kaleido").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("out"))); //buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
	    //os.addFilter(new GLColourFilter(os).setFilterName("GLColourFilter"));

	    os2.addFilter(new BlendDrawer(os2).setFilterName("BlendDrawer pix0 to out").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("pix0")).setParameterValue("BlendMode",8));
	    //os.addFilter(new BlendDrawer(os).setFilterName("BlendDrawer inp0 to out").setCanvases(os.getCanvasMapping("out"), os.getCanvasMapping("inp0")).setParameterValue("BlendMode",9));
		
	    os2.addFilter(new ShaderFilter(os2,"SyncEffect.xml")
	    		.setFilterName("SyncEffect").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("out"))
				.addParameter("step_x", 0.0f, -0.5f, 0.5f)
				.addParameter("step_y", 0.0f, -0.5f, 0.5f)
	    );

	    
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
		sequences.put("show_kaleido", new ShowFilterSequence(this, 0, getPath()+"/fl/Kaleido"));
		sequences.put("show_feedback", new ChainSequence(2000)
			//.addSequence(getSequence("show_feedback"))
			.addSequence(host.getSceneForPath("/sc/BlankerScene").getSequence("feedback"))
			.addSequence(new ShowFilterSequence(this, 0, getPath()+"/fl/Feedback")));
		
		sequences.put("show_blend", new ShowFilterSequence(this, 0, getPath()+"/fl/BlendDrawer pix0 to out"));		
		
		//sequences.put("show_blend", new ShowFilterSequence(this, 0, host.getSceneForPath(getPath()).getFilter("Kaleido")));
		
		sequences.put("show_sync", new SyncSequence(this, 2000)); //.addSequence(this.getFilter("SyncEffect").getSequence("horizontal_sin")
    }
	

	class OutputSequence1 extends Sequence {
		public OutputSequence1(OutputFX2 outputFX2, int i) {
			super(outputFX2,i);
		}
		@Override
		public ArrayList<Mutable> getMutables() {
			return new ArrayList<Mutable>();
		}
		@Override
		public void setValuesForNorm(double norm, int iteration) {
			//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
			//if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
			//host.getFilter("BlendDrawer1").changeParameterValue("Opacity", (float)norm);
		}
		@Override public void onStart() {
	   		/*if (APP.getApp().random(0f,1.0f)>=0.5f) {
    			host.host.getSceneForPath(getPath()).getFilter("Feedback").setMuted(false);
    			host.host.getSceneForPath("/sc/BlankerScene")
    				.getFilter("BlankFilter")
    				.changeParameterValue("alpha", (int)16);
    		} else {
    			host.host.getSceneForPath(getPath()).getFilter("Feedback").setMuted(true);
    			host.host.getSceneForPath("/sc/BlankerScene")
    				.getFilter("BlankFilter")
    				.changeParameterValue("alpha", (int)255);			
    		}*/
    		//if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath(getPath()).getFilter("Kaleido").toggleMute();
    		
    		//if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath("/sc/OutputShader2").getFilter("BlendDrawer pix0 to out").toggleMute();
    		host.host.getSceneForPath(getPath()).getFilter("BlendDrawer pix0 to out").setMuted((APP.getApp().random(0f,1.0f)>=0.25f));
    		
    		//if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath("/sc/OutputShader2").getFilter("BlendDrawer pix0 to out").changeParameterValue("BlendMode", getRandomArrayElement(new Integer[] { 3, 4, 8, 8, 8, 9, 12 } ));
    		if (APP.getApp().random(0f,1.0f)>=0.5f) ((BlendDrawer)host.host.getSceneForPath(getPath()).getFilter("BlendDrawer pix0 to out")).setBlendMode((Integer)getRandomArrayElement(new Integer[] { 3, 4, 8, 8, 8, 9, 12 } ));

    		host.host.getSceneForPath(getPath()).getFilter("Kaleido").nextMode();    			
		}
		@Override public void onStop() {	}
	}
	
	class FeedbackSequence1 extends Sequence {
		public FeedbackSequence1(OutputFX2 outputFX2, int i) {
			// TODO Auto-generated constructor stub
			super(outputFX2, i);
		}

		@Override
		public void setValuesForNorm(double pc, int iteration) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStop() {
			// TODO Auto-generated method stub
			
		}		
	}
	
	class SyncSequence extends Sequence {
		public SyncSequence(OutputFX2 outputFX2, int i) {
			// TODO Auto-generated constructor stub
			super(outputFX2, i);
		}

		@Override
		public void setValuesForNorm(double pc, int iteration) {
			// TODO Auto-generated method stub
			getFilter("SyncEffect").setParameterValueFromSin("step_x", pc);
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStop() {
			// TODO Auto-generated method stub
			
		}		
	}	
	
}
