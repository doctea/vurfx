package vurfeclipse.user.scenes;

import processing.core.PApplet;
import vurfeclipse.filters.*;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;
import vurfeclipse.scenes.SimpleScene;
import vurfeclipse.sequence.ChainSequence;
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
		sequences.put("fixed", new TunnelFixedSequence(this, 2000));		
		sequences.put("fixed 2", new TunnelFixedSequence2(this, 5000));
		sequences.put("angled 60", new ChainSequence(5000).addSequence(new TunnelFixedSequence(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 60)));
		sequences.put("angled 45", new ChainSequence(5000).addSequence(new TunnelFixedSequence(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 45)));
		sequences.put("angled 30", new ChainSequence(5000).addSequence(new TunnelFixedSequence(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 30)));
		sequences.put("angled 15", new ChainSequence(5000).addSequence(new TunnelFixedSequence(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 15)));
		sequences.put("angled 10", new ChainSequence(5000).addSequence(new TunnelFixedSequence(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 10)));
		sequences.put("p1 angled 60", new ChainSequence(5000).addSequence(new TunnelPulseSequence(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 60)));
		sequences.put("p1 angled 45", new ChainSequence(5000).addSequence(new TunnelPulseSequence(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 45)));
		sequences.put("p1 angled 30", new ChainSequence(5000).addSequence(new TunnelPulseSequence(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 30)));
		sequences.put("p2 angled 45", new ChainSequence(5000).addSequence(new TunnelPulseSequence2(this, 5000,false)).addSequence(new TunnelAngledSequence(this, 5000, 45)));
		sequences.put("p2 angled 30", new ChainSequence(5000).addSequence(new TunnelPulseSequence2(this, 5000,true)).addSequence(new TunnelAngledSequence(this, 5000, 30)));
		sequences.put("f2 angled 60", new ChainSequence(5000).addSequence(new TunnelFixedSequence2(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 60)));
		sequences.put("f2 angled 45", new ChainSequence(5000).addSequence(new TunnelFixedSequence2(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 45)));
		sequences.put("f2 angled 30", new ChainSequence(5000).addSequence(new TunnelFixedSequence2(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 30)));
		sequences.put("f2 angled 15", new ChainSequence(5000).addSequence(new TunnelFixedSequence2(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 15)));
		sequences.put("f2 angled 5", new ChainSequence(5000).addSequence(new TunnelFixedSequence2(this, 5000)).addSequence(new TunnelAngledSequence(this, 5000, 5)));
		sequences.put("preset 2", new TunnelPulseSequence2(this, 2000, true));
		sequences.put("preset 3", new TunnelPulseSequence2(this, 2000, false));
	}
	
	class TunnelPulseSequence extends Sequence {

		public TunnelPulseSequence(TunnelScene tunnelScene, int i) {
			// TODO Auto-generated constructor stub
			super(tunnelScene,i);
		}

		@Override
		public void setValuesForNorm(double pc, int iteration) {
			// TODO Auto-generated method stub
			for (int i = 1 ; i <= 6 ; i++) {
			//for (float f = 0.1f ; f < 2.0f ; f+)
				getFilter("Blend_"+i)
					.changeParameterValueFromSin("Scale", (float)Math.sin(pc*(2.0f/(float)i))/2.0f)
					.changeParameterValue("Opacity", (float)(1.0f/i))//(float)Math.sin(pc*(1.0/(float)i))-0.5f) //(float)pc*i)
				;
					//(float)(1.0f - (1f/(float)i)));
			}
		}

		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			for (int i = 1 ; i <= 6 ; i++) {
				getFilter("Blend_"+i).changeParameterValue("Rotate", 0);
				getFilter("Blend_"+i).updateAllParameterValues();
			}
		}

		@Override
		public void onStop() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	class TunnelPulseSequence2 extends TunnelPulseSequence {
		boolean loop = true;
		public TunnelPulseSequence2(TunnelScene tunnelScene, int i, boolean loop) {
			super(tunnelScene, i);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void setValuesForNorm(double pc, int iteration) {
			if (loop) if (iteration%2==0) pc = 1.0f-pc;	// go up and down again
		
			for (int i = 1 ; i <= 6 ; i++) {
			//for (float f = 0.1f ; f < 2.0f ; f+)
				getFilter("Blend_"+i)
					.changeParameterValue("Scale", (float)((2.0f/i) + (pc * i))) //(float)Math.sin(pc*(2.0f/(float)i))/2.0f)
					.changeParameterValue("Opacity", (float)(1.0f/i))//(float)Math.sin(pc*(1.0/(float)i))-0.5f) //(float)pc*i)
				;
					//(float)(1.0f - (1f/(float)i)));
			}
		}
		
	}
	
	class TunnelFixedSequence extends TunnelPulseSequence {
		public TunnelFixedSequence(TunnelScene tunnelScene, int i) {
			super(tunnelScene, i);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void setValuesForNorm(double pc, int iteration) {
			//super.setValuesForNorm(1f, iteration);
		}
		
		@Override
		public void onStart() {
			super.onStart();
			// TODO Auto-generated method stub
			for (int i = 1 ; i <= 6 ; i++) {
			//for (float f = 0.1f ; f < 2.0f ; f+)
				getFilter("Blend_"+i).resetParameters();
				getFilter("Blend_"+i).changeParameterValue("Opacity", 0.1f + ((6-i)*0.125f));
				//getFilter("Blend_"+i).changeParameterValue("Rotate", i); //45 * i-1); //(i-1)*(/6));
				getFilter("Blend_"+i).changeParameterValue("BlendMode", (i%2==0?8:3));
				getFilter("Blend_"+i).updateAllParameterValues();
			}
		}
	}
	
	class TunnelFixedSequence2 extends TunnelPulseSequence {
		public TunnelFixedSequence2(TunnelScene tunnelScene, int i) {
			super(tunnelScene, i);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void setValuesForNorm(double pc, int iteration) {
			//super.setValuesForNorm(1f, iteration);
		}
		
		@Override
		public void onStart() {
			super.onStart();
			// TODO Auto-generated method stub
			for (int i = 1 ; i <= 6 ; i++) {
			//for (float f = 0.1f ; f < 2.0f ; f+)
				getFilter("Blend_"+i).resetParameters();
				getFilter("Blend_"+i).changeParameterValue("Opacity", 0.1f + ((i)*0.125f));
				//getFilter("Blend_"+i).changeParameterValue("Rotate", i); //45 * i-1); //(i-1)*(/6));
				getFilter("Blend_"+i).changeParameterValue("BlendMode", (i%2==0?8:3));
				getFilter("Blend_"+i).updateAllParameterValues();
			}
		}
	}	
	
	class TunnelAngledSequence extends TunnelPulseSequence {
		int angle;
		
		public TunnelAngledSequence(TunnelScene tunnelScene, int i, int angle) {
			super(tunnelScene, i);
			this.angle = angle;
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public void setValuesForNorm(double pc, int iteration) {
			//super.setValuesForNorm(1f, iteration);
		}
		
		@Override
		public void onStart() {
			super.onStart();
			// TODO Auto-generated method stub
			for (int i = 1 ; i <= 6 ; i++) {
			//for (float f = 0.1f ; f < 2.0f ; f+)
				getFilter("Blend_"+i).resetParameters();	
				getFilter("Blend_"+i).changeParameterValue("Rotate", i * angle); //45 * i-1); //(i-1)*(/6));
				getFilter("Blend_"+i).changeParameterValue("BlendMode", (i%2==0?8:3));
				getFilter("Blend_"+i).updateAllParameterValues();
			}
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
					.changeParameterValue("BlendMode", new Integer(8))
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
					.changeParameterValue("BlendMode", new Integer(3))
		);				

		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_7").setInputCanvas(getCanvasMapping("temp")).setOutputCanvas(getCanvasMapping("out"))
				.changeParameterValue("Opacity", new Float(0.5f)));

		
		return true;
	}

}
