package vurfeclipse.user.scenes;

//import javafx.scene.canvas.Canvas;
import java.util.ArrayList;

import vurfeclipse.APP;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.PlainDrawer;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import vurfeclipse.scenes.SimpleScene;
import vurfeclipse.sequence.Sequence;

public class BlenderFX1 extends SimpleScene {
    class BlendSequence1 extends Sequence {
    	public BlendSequence1() {}
		public BlendSequence1(BlenderFX1 blenderFX1, int i) {
			// TODO Auto-generated constructor stub
			super(blenderFX1,i);
		}
		public void setValuesForNorm(double norm, int iteration) {
			//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
			if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
			host.getFilter("BlendDrawer1").changeParameterValue("Opacity", (float)norm);
		}
		@Override public void onStart() {
			//this.setLengthMillis((int)random(1,5) * 500);
			/*for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
				host.host.getSceneForPath("/ImageListScene1").getFilter("ImageListDrawer1").nextMode();
			for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
				host.host.getSceneForPath("/ImageListScene2").getFilter("ImageListDrawer2").nextMode();*/
		}
		@Override public void onStop() {	return; }
		
		/*@Override public ArrayList<Mutable> getMutables() {
			return this.mutables; //new ArrayList<Mutable>;
		}*/
	}

	class BlendSequence2 extends Sequence {
		public BlendSequence2() {}
		public BlendSequence2(BlenderFX1 blenderFX1, int i) {
			// TODO Auto-generated constructor stub
			super(blenderFX1,i);
		}
		@Override public void onStart() {
			host.getFilter("BlendDrawer1").nextMode();
		}
		@Override
		public void setValuesForNorm(double pc, int iteration) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onStop() {
			// TODO Auto-generated method stub
			
		}	
		
		/*@Override public ArrayList<Mutable> getMutables() {
			return this.mutables; //new ArrayList<Mutable>;
		}*/
	}

	public BlenderFX1(Project host, int w, int h) {
		super(host,w,h);
		setSceneName("BlenderFX1: autonumbered " + this.host.getGUID(this.getClass().getSimpleName()));
	}
	public BlenderFX1(Project host, String name, int w, int h) {
		super(host, w, h);
		// TODO Auto-generated constructor stub
		setSceneName("BlenderFX1: " + name);
	}
    
    /*public vurfeclipse.Canvas setCanvas(String a, String b) {
    	return super.setCanvas(a, b);
    }*/
    
	@Override
    public boolean setupFilters() {
    	System.out.println(this+"#setupFilters()");
    	Scene bl1 = this;
	    //bl1.setOutputCanvas("/out");
    	//setCanvas("out","/out");
	    bl1.addFilter(new PlainDrawer(bl1).setCanvases(getCanvasMapping("out"),getCanvasMapping("pix0")));//getCanvasMapping("pix0"))); //"/pix0"));
	    //bl1.addFilter(new PlainDrawer(bl1).setInputCanvas(getCanvasMapping("pix1")));//getCanvasMapping("pix0"))); //"/pix0"));
	    bl1.addFilter(new BlendDrawer(bl1).setCanvases(getCanvasMapping("out"),getCanvasMapping("pix1")).setFilterName("BlendDrawer1")); //getCanvasMapping("pix1"))); //"/pix1"));
	    //bl1.addFilter(new MirrorFilter(bl1).setFilterName("Mirror").changeParameterValue("mirror_y", true)).setInputCanvas(bl1.getCanvasMapping("out"));
	    //System.exit(0);
	    return true;
    }
    
	@Override
    public void setupSequences() {
			sequences.put("preset 1", new BlendSequence1(this, 2000));
			sequences.put("nomute_preset 1", new BlendSequence1(this, 2000).disableHostMute());
			sequences.put("preset 2_next_", new BlendSequence2(this, 2000));
			sequences.put("nomute_preset 2_next_", new BlendSequence2(this, 2000).disableHostMute());
    }
    
}
