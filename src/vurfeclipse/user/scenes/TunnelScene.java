package vurfeclipse.user.scenes;

import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;
import vurfeclipse.scenes.SimpleScene;
import vurfeclipse.sequence.Sequence;

public class TunnelScene extends SimpleScene {

	public TunnelScene(Project p, int w, int h) {
		// TODO Auto-generated constructor stub
		super(p,w,h);
	}
	
	@Override
	public void setupSequences() {
		//HashMap<String,Sequence> a = super.getSequences();//new HashMap<String,Sequence> ();
		sequences.put("preset 1", new TunnelPulseSequence(this, 2000));
	}
	
	class TunnelPulseSequence extends Sequence {

		public TunnelPulseSequence(TunnelScene tunnelScene, int i) {
			// TODO Auto-generated constructor stub
			super(tunnelScene,i);
		}

		@Override
		public void setValuesForNorm(double pc, int iteration) {
			// TODO Auto-generated method stub
			for (int i = 1 ; i < 6 ; i++) {
			//for (float f = 0.1f ; f < 2.0f ; f+)
				getFilter("Blend_"+i)
					.changeParameterValueFromSin("Scale", (float)Math.sin(pc*(2.0f/(float)i))/2.0f)
					.changeParameterValue("Opacity", 1.0f/i)//(float)Math.sin(pc*(1.0/(float)i))-0.5f) //(float)pc*i)
				;
					//(float)(1.0f - (1f/(float)i)));
			}
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

	@Override
	public boolean setupFilters() {
		super.setupFilters();
		//super.setupFilters();
		// TODO Auto-generated method stub
		
		/*BlendDrawer bl1 = (BlendDrawer) new BlendDrawer(this).setFilterName("Blend_1");
		bl1.setInputCanvas(getCanvasMapping("out"));
		bl1.setOutputCanvas(getCanvasMapping("out"));
		bl1.changeParameterValue("Zoom", new Float(0.5f));
		bl1.changeParameterValue("Opacity", new Float(0.5f));
		bl1.changeParameterValue("X", new Float(0.25f));
		bl1.changeParameterValue("Y", new Float(0.33f));
		this.addFilter(bl1);*/
				
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_6").setInputCanvas(getCanvasMapping("src")).setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(2.0f))
					.changeParameterValue("Opacity", new Float(0.05f))
					.changeParameterValue("BlendMode", new Integer(2))
					//.changeParameterValue("X", new Float(0.5f))
					//.changeParameterValue("Y", new Float(0.5f))
		);		
		
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_5").setInputCanvas(getCanvasMapping("src")).setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(1.5f))
					.changeParameterValue("Opacity", new Float(0.1f))
					.changeParameterValue("BlendMode", new Integer(8))
					//.changeParameterValue("X", new Float(0.25f))
					//.changeParameterValue("Y", new Float(0.25f))
		);
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_4").setInputCanvas(getCanvasMapping("src")).setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(0.75f))
					.changeParameterValue("Opacity", new Float(0.3f))
					.changeParameterValue("BlendMode", new Integer(3))
		);
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_3").setInputCanvas(getCanvasMapping("src")).setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(0.5f))
					.changeParameterValue("Opacity", new Float(0.5f))
					.changeParameterValue("BlendMode", new Integer(8))
					.changeParameterValue("BlendMode", new Integer(5))
		);		
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_2").setInputCanvas(getCanvasMapping("src")).setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(0.25f))
					.changeParameterValue("Opacity", new Float(0.6f))
		);		
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_1").setInputCanvas(getCanvasMapping("src")).setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(0.1f))
					.changeParameterValue("Opacity", new Float(0.6f))
					.changeParameterValue("BlendMode", new Integer(8))
		);				

		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_7").setInputCanvas(getCanvasMapping("temp")).setOutputCanvas(getCanvasMapping("out"))
				.changeParameterValue("Opacity", new Float(0.5f)));

		
		return true;
	}

}
