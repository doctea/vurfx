package vurfeclipse.filters;

import java.util.HashMap;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;
import vurfeclipse.*;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.scenes.Scene;

import surface.*;

public class ShapeDrawer extends SpiralDrawer {

	Surface s;
	PShape t;
    private static PShader colorShader = APP.getApp().loadShader("surface-colorfrag.glsl", "surface-colorvert.glsl");;
	
	public Filter nextMode () {
		b.nextShape();
		this.changeParameterValue("shape", b.getShape());//(Integer)this.getParameterValue("shape")+1);
		return this;
	}

	public ShapeDrawer(Scene sc) {
		super(sc);
	}

	public ShapeDrawer(Scene sc, int ov_w, int ov_h) {
		super(sc, ov_w, ov_h);
	}

	public boolean initialise () {
		//b.setInput(src.getTexture());
		//b.loadSVG("output/ds2014/dseye.svg");
		return super.initialise();
	}

	public ShapeDrawer setImage(String fn) {
		this.b.setImage(fn);
		ImageRepository.IR.cacheLoad(fn);
		return this;
	}

	@Override
	synchronized public void setParameterDefaults () {
		//this.setParameterValue("radius", 10.0);
		//this.setParameterValue("rotation", 0.0);
		super.setParameterDefaults();
		this.addParameter("edged", new Boolean(true));
		this.addParameter("shape", new Integer(0), 0, b.shapesCount);
		//this.addParameter("colour",new Integer(255), new Integer(0), new Integer(2^32)); //APP.color(APP.random(255),APP.random(255),APP.random(255),128)));
		this.addParameter("colour1", VurfEclipse.makeColour(255, 128, 64, 255));
		this.addParameter("colour2", VurfEclipse.makeColour(255, 128, 64, 255));
		//this.addParameter("colour3", VurfEclipse.makeColour(255, 128, 64));
		//this.addParameter("colour4", VurfEclipse.makeColour(255, 128, 64));
		

		//this.addParameter("spiralCenter", new PVector(this.w/2, this.h/2));		// duplicate, already specified in superclass?

		//this.addParameter("startRadius")

		//this.addParameter("radius", 0.5, 0.01, 20.0);
	}
	
	@Override
	public boolean applyMeatToBuffers() {
		//if (true) return true;
		if ((int)Parameter.castAs(this.getParameterValue("shape"),Integer.class)==b.SH_TEXTURE) { //|| (int)this.getParameterValue("shape")==b.SH_FLOWER) {
			return this.old____applyMeatToBuffers();
		} else {
			return super.applyMeatToBuffers();
		}
	}

	@Override
	synchronized public void updateParameterValue(String paramName, Object value) {
			//System.out.println("setting " + paramName + " to " + value);
			/*if(paramName.equals("radius"))
	        b.setRadius((Integer)value);
	    else if(paramName.equals("rotation"))
	        b.setRotation((Integer)value);
	    else*/ 
		
		if (paramName.equals("edged")) {
			value = Boolean.parseBoolean(value.toString());
	    	b.setEdge((Boolean)value);
		} else if(paramName.equals("tint")) {
			value = (int)Float.parseFloat(value.toString());
	    	b.setTint((Integer)value);
		} else if(paramName.equals("shape")) {   // there goes my hero <3
			value = (int)Float.parseFloat(value.toString());
	    	if ((int)value!=b.getShape()) {
	    		if (in()==null) setAlias_in("pix0"); //src = sc.getCanvas("pix0").getSurf();
	    		if (in()!=null) b.setInput(in());
	    		b.setShape((Integer)value);
	    		this.clearList = true;//list.clear();
			}
	    } else if(paramName.equals("colour")) {
	    	value = (int)Float.parseFloat(value.toString());
	    	//println("got colour change to" + value);
	    	b.setColour((Integer)value);
	    } else {
	    	super.updateParameterValue(paramName, value);
	    }
	}

	public void setColour(int r, int g, int b2, int a) {
		b.setColour(r,g,b2,a);
	}
	public void setColour(int r, int g, int b2) {
		b.setColour(r,g,b2);
	}

	public void loadSVG(String filename) {
		b.loadSVG(filename);
	}

	int colourSwitchCount = 0;
	int colourSwitchEvery = 50; // frames

	PShape shapeCache;
	@Override
	synchronized public PShape collectActualObject(float currRadius, float currentRadian) {
	      //b = new Blob();
	      //b.setTint(tint);
	      //b.setColour(c);
	      
	      /*PShape p = b.getShapePolygon(4);
	      p.scale(new_w,new_h);
	      p.rotate(currentRadian);*/
	      
		/*b.setShape((Integer)getParameterValue("shape"));
		b.setInput(sc.getCanvas("src").getSurf());//.getTexture()); // was "temp3" ..
		b.setRadius(currRadius);// * (int)random(5));
		b.setRotation((int)Math.toRadians((currentRadian)));*/
		
		//return b.collectShape(in());
		//return shapeCache;
		//return b.collectShape(in());
		//if (s==null) {
			s = new surface.Horn(APP.getApp(),20,20);
			//s = new surface.MoebiusStrip(APP.getApp(),20,20);
			//s = new surface.Torus(APP.getApp(), 20, 20, 5, 5);
	        s.setScale(1);
	        s.initColors(APP.getApp().color(255, 125, 0));
	        //s.setTexture(in());
	        t = s.getSurface();
	        //t.fill(APP.getApp().color(255, 125, 0));
	        //t.scale(w/3);
	        //t.rotateY((Float)getParameterValue("zRotate"));
		//}
		
		t = s.getSurface();
		
		return t;
	}

	
	@Deprecated //sort of, is only way to do the old method of texturing blobs 
	synchronized public void drawActualObject(PGraphics out, float currRadius, float currentRadian) {
		b.setShape((Integer)getParameterValue("shape"));
		b.setInput(sc.getCanvas("src").getSurf());//.getTexture()); // was "temp3" ..
		b.setRadius(currRadius);// * (int)random(5));
		b.setRotation((int)Math.toRadians((currentRadian)));
		b.draw(out,in());
	}



	@Override
	public HashMap<String,Object> collectFilterSetup() {	// for saving snapshots, save setup of filter
		HashMap<String,Object> output = super.collectFilterSetup();
		output.put("canvas_pix0", "pix0");
		return output;
	}

}
