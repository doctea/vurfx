package vurfeclipse.user.scenes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vurfeclipse.filters.Filter;
import vurfeclipse.filters.ShaderFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.*;
import vurfeclipse.sequence.Sequence;

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

	    os2.addFilter(new ShaderFilter(os2,"SyncEffect.glsl")
		    	.setFilterName("SyncEffect").setAliases("out","src")
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
			public SyncSequence() {
				super();
			}
			public SyncSequence(OutputFX3 outputFX2, int i) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
			}
			
			@Override
			public ArrayList<Mutable> getMutables() {
				return new ArrayList<Mutable>();
			}
			
			@Override
			public void __setValuesForNorm(double pc, int iteration) {
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
			@Override
			public boolean notifyRemoval(Filter newf) {
				if (getFilter("SyncEffect")==newf) {
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

		class SyncSequence2 extends SyncSequence {
			public SyncSequence2() {
				super();
			}
			public SyncSequence2(OutputFX3 outputFX2, int i) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
			}

			@Override
			public void __setValuesForNorm(double pc, int iteration) {
				getFilter("SyncEffect").changeParameterValueFromSin("step_x", (float)Math.sin(pc/3.0f*10f));
				getFilter("SyncEffect").changeParameterValueFromSin("step_y", (float)Math.sin(pc/3.0f*10f));
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

		class SyncSequence3 extends SyncSequence {
			public SyncSequence3(OutputFX3 outputFX2, int i) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
			}

			@Override
			public void __setValuesForNorm(double pc, int iteration) {
				getFilter("SyncEffect").changeParameterValueFromSin("step_x", (float)Math.sin(pc/3.0*10f));
				getFilter("SyncEffect").changeParameterValueFromSin("step_y", -(float)Math.sin(pc/3.0*10f));
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
		
		class SyncSequence4 extends SyncSequence {
			public SyncSequence4(OutputFX3 outputFX2, int i) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
			}

			@Override
			public void __setValuesForNorm(double pc, int iteration) {
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
		
		class GridSyncSequence extends SyncSequence {
			private int grid;

			public GridSyncSequence() {
				super();
			}
			public GridSyncSequence(OutputFX3 outputFX2, int i, int grid) {
				// TODO Auto-generated constructor stub
				super(outputFX2, i);
				this.grid = grid;
			}
			
			@Override
			public Map<String, Object> collectParameters() {
				Map<String, Object> params = super.collectParameters();
				params.put("grid", this.grid);
				return params;
			}
			@Override
			public void loadParameters(Map<String,Object> params) {
				super.loadParameters(params);
				if (params.containsKey("grid")) {
					this.grid = (Integer) params.get("grid");
				} else {	// no grid saved in snapshot, so try to assume existing one
					this.grid = (Integer) getFilter("SyncEffect").getParameterValue("step_x");
				}
			}

			@Override
			public void __setValuesForNorm(double pc, int iteration) {
				changeFilterParameterValue("SyncEffect", "step_x", (float)grid); //(float)Math.sin(pc/1.5*10));
				changeFilterParameterValue("SyncEffect", "step_y", (float)grid); //-(float)Math.sin(pc/1.5*10));
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
			@Override
			public boolean notifyRemoval(Filter newf) {
				if (getFilter("SyncEffect")==newf) {
					this.setEnabled(false);
					return true;
				}
				return false;
			}
			@Override
			public void removeSequence(Sequence self) {
				// TODO Auto-generated method stub
				
			}
		}
		
}
