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
    public BlenderFX1(Project host, String name, int w, int h) {
		super(host, w, h);
		// TODO Auto-generated constructor stub
		setSceneName("BlenderFX1: " + name);
	}
    
    /*public vurfeclipse.Canvas setCanvas(String a, String b) {
    	return super.setCanvas(a, b);
    }*/
    
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
    
    public void setupSequences() {
		sequences.put("preset 1", new BlendSequence1(this, 2000));
    }
    
}

class BlendSequence1 extends Sequence {
	public BlendSequence1(BlenderFX1 blenderFX1, int i) {
		// TODO Auto-generated constructor stub
		super(blenderFX1,i);
	}
	@Override public ArrayList<Mutable> getMutables() {
		ArrayList<Mutable> muts = new ArrayList<Mutable>();
		muts.add(host);//.getFilter("BlendDrawer1"));
		return muts;
	}
	public void setValuesForNorm(double norm, int iteration) {
		//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
		if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
		host.getFilter("BlendDrawer1").changeParameterValue("Opacity", (float)norm);
	}
	@Override public void onStart() {
		this.setLengthMillis((int)APP.getApp().random(1,5) * 500);
		/*for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
			host.host.getSceneForPath("/ImageListScene1").getFilter("ImageListDrawer1").nextMode();
		for (int i = 0 ; i < APP.getApp().random(2,10) ; i++) 
			host.host.getSceneForPath("/ImageListScene2").getFilter("ImageListDrawer2").nextMode();*/
	}
	@Override public void onStop() {	}
}