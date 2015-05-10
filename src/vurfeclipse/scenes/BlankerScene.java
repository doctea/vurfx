package vurfeclipse.scenes;

import java.util.ArrayList;

import vurfeclipse.APP;
import vurfeclipse.filters.BlankFilter;
import vurfeclipse.projects.Project;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.user.scenes.OutputFX1;

public class BlankerScene extends SimpleScene {

	public BlankerScene(Project host, int w, int h) {
		super(host, w, h);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean setupFilters () {
		BlankFilter bf = (BlankFilter) new BlankFilter(this).setFilterName("BlankFilter").setOutputCanvas(getCanvasMapping("out"));
		this.addFilter(bf);
		return true;
	}
	
	@Override
	public void setupSequences() {
		//HashMap<String,Sequence> a = super.getSequences();//new HashMap<String,Sequence> ();
		sequences.put("standard", new Standard(this, 0));
		sequences.put("feedback", new Feedback(this, 0));
		sequences.put("fade", new Fade(this, 2000));
		//sequences.put("preset 2", new TunnelPulseSequence2(this, 2000, true));
		//sequences.put("preset 3", new TunnelPulseSequence2(this, 2000, false));
	}
	
	abstract class BlankSequence extends Sequence {
		public BlankSequence(BlankerScene sc, int i) {
			super(sc,i);
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
		
	}		
	
	class Standard extends BlankSequence {
		public Standard(BlankerScene sc, int i) {
			super(sc, i);
			// TODO Auto-generated constructor stub
		}
		@Override public void onStart() {
			/*if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath(getPath()).getFilter("Toon").toggleMute();
			//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("pulsatingEmboss").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
			//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("CrossHatch").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
			if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath(getPath()).getFilter("Edges").toggleMute();*/
			getFilter("BlankFilter").changeParameterValue("alpha", 255);
		}
		@Override public void onStop() {	}
	}
	
	class Feedback extends BlankSequence {
		public Feedback(BlankerScene sc, int i) {
			super(sc, i);
			// TODO Auto-generated constructor stub
		}
				
		@Override public void onStart() {
			/*if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath(getPath()).getFilter("Toon").toggleMute();
			//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("pulsatingEmboss").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
			//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("CrossHatch").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
			if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath(getPath()).getFilter("Edges").toggleMute();*/
			getFilter("BlankFilter").changeParameterValue("alpha", 16);
		}

		@Override
		public void onStop() {
			// TODO Auto-generated method stub
			
		}
	}	
	
	class Fade extends BlankSequence {
		public Fade(BlankerScene sc, int i) {
			super(sc, i);
			// TODO Auto-generated constructor stub
		}
		@Override public void setValuesForNorm(double norm, int iteration) {
			getFilter("BlankFilter").changeParameterValue("alpha", 32 + (int) (200 * norm));
		}
		@Override public void onStart() {
			/*if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath(getPath()).getFilter("Toon").toggleMute();
			//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("pulsatingEmboss").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
			//switcher.host.getSceneForPath("/sc/OutputShader").getFilter("CrossHatch").setMute((APP.getApp().random(0f,1.0f)>=0.2f));
			if (APP.getApp().random(0f,1.0f)>=0.5f) host.host.getSceneForPath(getPath()).getFilter("Edges").toggleMute();*/
			//getFilter("BlankFilter").changeParameterValue("alpha", 16);
		}
		@Override public void onStop() {	
			getFilter("BlankFilter").changeParameterValue("alpha", 255);			
		}
	}		
}