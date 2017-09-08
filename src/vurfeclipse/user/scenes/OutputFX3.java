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

public class OutputFX3 extends SimpleScene {
	public OutputFX3(Project pr, int w, int h) {
		super(pr, w, h);
		setSceneName("OutputFX3 Instance");
	}

	public boolean setupFilters() {
	    //this.addFilter(new ShaderFilter(this,"Edges.xml").setFilterName("Edges").setCanvases(this.getCanvasMapping("out"), this.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));
	    //this.addFilter(new ShaderFilter(this,"Toon.xml").setFilterName("Toon").setCanvases(this.getCanvasMapping("out"), this.getCanvasMapping("out"))); //setBuffers(ss.buffers[ss.BUF_OUT],ss.buffers[ss.BUF_SRC]));


	    final SimpleScene os2 = this;//(SimpleScene) new SimpleScene(this,w,h).setSceneName("OutputShader2");
	    //os2.setCanvas("out", "/out");
	    //os2.setCanvas("pix0","/pix0");
	    //os2.setCanvas("blendresult", "/blendresult");

	    os2.addFilter(new ShaderFilter(os2,"SyncEffect.xml")
		    	.setFilterName("SyncEffect").setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("out"))
					.addParameter("step_x", 2.5f, 1.0f, 16.0f)
					.addParameter("step_y", 2.5f, 1.0f, 16.0f)
					.addParameter("offset_x", 2.0f, -4.0f, 4.0f)
					.addParameter("offset_y", 2.0f, -4.0f, 4.0f)
					.addParameter("sin_mode", new Integer(0), new Integer(0), new Integer(1))
		    );
	    
	    //os2.addFilter(new ShaderFilter(os2,"BadTV.xml").addParameter("iTime", new Float(1.0f), new Float(1.0f), new Float(10000.0f)).setCanvases(os2.getCanvasMapping("out"), os2.getCanvasMapping("out")));

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
			sequences.put("show_sync", new SyncSequence(this, 5000)); //.addSequence(this.getFilter("SyncEffect").getSequence("horizontal_sin")
			sequences.put("show_sync2", new SyncSequence2(this, 5000)); //.addSequence(this.getFilter("SyncEffect").getSequence("horizontal_sin")
			//sequences.put("show_sync3", new SyncSequence3(this, 5000)); //.addSequence(this.getFilter("SyncEffect").getSequence("horizontal_sin")
			//sequences.put("show_sync4", new SyncSequence4(this, 5000)); //.addSequence(this.getFilter("SyncEffect").getSequence("horizontal_sin")
			
			for (int i = 2 ; i < 10 ; i+=2) {
				sequences.put("show_gridsync_"+i, new GridSyncSequence(this, 5000, i));
			}
    }
    

		class SyncSequence extends Sequence {
			public SyncSequence(OutputFX3 outputFX2, int i) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
			}

			@Override
			public void setValuesForNorm(double pc, int iteration) {
				getFilter("SyncEffect").changeParameterValueFromSin("step_x", Math.abs((float)Math.sin(pc)));
				getFilter("SyncEffect").changeParameterValueFromSin("step_y", (float)Math.sin(pc));
			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_x", 0);
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_y", 0);
			}

			@Override
			public void onStop() {
				// TODO Auto-generated method stub

			}
		}

		class SyncSequence2 extends Sequence {
			public SyncSequence2(OutputFX3 outputFX2, int i) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
			}

			@Override
			public void setValuesForNorm(double pc, int iteration) {
				getFilter("SyncEffect").changeParameterValueFromSin("step_x", (float)Math.sin(pc/3.0*10));
				getFilter("SyncEffect").changeParameterValueFromSin("step_y", (float)Math.sin(pc/3.0*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_x", (float)Math.sin(pc/3.0*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_y", (float)Math.sin(pc/3.0*10));

			}

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_x", 0);
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_y", 0);
			}

			@Override
			public void onStop() {
				// TODO Auto-generated method stub

			}
		}

		class SyncSequence3 extends Sequence {
			public SyncSequence3(OutputFX3 outputFX2, int i) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
			}

			@Override
			public void setValuesForNorm(double pc, int iteration) {
				getFilter("SyncEffect").changeParameterValueFromSin("step_x", (float)Math.sin(pc/3.0*10));
				getFilter("SyncEffect").changeParameterValueFromSin("step_y", -(float)Math.sin(pc/3.0*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_x", (float)Math.sin(pc/3.0*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_y", -(float)Math.sin(pc/3.0*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_x", (float)Math.sin(pc/3.0*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_y", (float)Math.sin(pc/3.0*10));

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
		
		class SyncSequence4 extends Sequence {
			public SyncSequence4(OutputFX3 outputFX2, int i) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
			}

			@Override
			public void setValuesForNorm(double pc, int iteration) {
				getFilter("SyncEffect").changeParameterValueFromSin("step_x", (float)Math.sin(pc/6*10));
				getFilter("SyncEffect").changeParameterValueFromSin("step_y", -(float)Math.sin(pc/6*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_x", (float)Math.sin(pc/1.5*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_y", -(float)Math.sin(pc/1.5*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_x", (float)Math.sin(pc/3.0*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_y", (float)Math.sin(pc/3.0*10));

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
		
		class GridSyncSequence extends Sequence {
			private int grid;

			public GridSyncSequence(OutputFX3 outputFX2, int i, int grid) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
				this.grid = grid;
			}

			@Override
			public void setValuesForNorm(double pc, int iteration) {
				getFilter("SyncEffect").changeParameterValue("step_x", (float)grid); //(float)Math.sin(pc/1.5*10));
				getFilter("SyncEffect").changeParameterValue("step_y", (float)grid); //-(float)Math.sin(pc/1.5*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_x", (float)Math.sin(pc/1.5*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_y", -(float)Math.sin(pc/1.5*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_x", (float)Math.sin(pc/3.0*10));
				//getFilter("SyncEffect").changeParameterValueFromSin("offset_y", (float)Math.sin(pc/3.0*10));

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
