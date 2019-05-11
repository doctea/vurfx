package vurfeclipse.filters;

import java.util.HashMap;

import de.dfki.km.text20.browserplugin.services.sessionrecorder.events.GeometryEvent;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
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
		//b.nextShape();
		this.changeParameterValue("shape", ((Integer)this.getParameterValue("shape")+1) % 8);
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
		
		this.parameters.remove("numSections");
	    addParameter("numSections", 60.0f, 2.0f, 60.0f);	// override parent with more suitable range
		
		this.addParameter("edged", new Boolean(true));
		this.addParameter("shape", new Integer(0), 0, b.shapesCount);
		//this.addParameter("colour",new Integer(255), new Integer(0), new Integer(2^32)); //APP.color(APP.random(255),APP.random(255),APP.random(255),128)));
		this.addParameter("colour1", VurfEclipse.makeColour(255, 128, 64, 255));
		this.addParameter("colour2", VurfEclipse.makeColour(255, 128, 64, 255));
		//this.addParameter("colour3", VurfEclipse.makeColour(255, 128, 64));
		//this.addParameter("colour4", VurfEclipse.makeColour(255, 128, 64));
		
		this.addParameter("phi", new Integer(10), 0, 30);
		this.addParameter("theta", new Integer(10), 0, 30);
		
		this.addParameter("shape_f1", new Float(1f), 0f, 2f);
		this.addParameter("shape_f2", new Float(1f), 0f, 2f);
		this.addParameter("shape_f3", new Float(1f), 0f, 2f);

		//this.addParameter("spiralCenter", new PVector(this.w/2, this.h/2));		// duplicate, already specified in superclass?

		//this.addParameter("startRadius")

		//this.addParameter("radius", 0.5, 0.01, 20.0);
	}
	
	@Override
	public boolean applyMeatToBuffers() {	// override blobdrawer behaviour of reverting to old drawing method for textured blobs SH_TEXTURE / Shape number 5
		return applyMeatToBuffers_spiral();
	}
	
	private void applyMeatToBuffers_shader(PGraphics out) {
		out.shader(this.colorShader);		
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
		} else if(paramName.equals("shape")
				) {   // there goes my hero <3
			value = (int)Float.parseFloat(value.toString());
	    	if ((int)value!=b.getShape()) {
	    		//if (in()==null) setAlias_in("pix0"); //src = sc.getCanvas("pix0").getSurf();
	    		//if (in()!=null) b.setInput(in());
	    		//b.setShape((Integer)value);
	    		
	    		//s = this.getShapeIndex((Integer)value);
	    		
	    		this.clearList = true;//list.clear();
				if ((int)this.getParameterValue("shape")==7) {
					this.max_numsections = 3.0f;
				} else {
					this.max_numsections = 100f;
				}
			}
		} else if(paramName.equals("shape_f1") 
				|| paramName.equals("shape_f2")
				|| paramName.equals("shape_f3")
				|| paramName.equals("phi")
				|| paramName.equals("theta")) {
			if ((int)this.getParameterValue("shape")==7) {
				//println("not clearing list because we've got geometry instead of shape");
			} else {
				this.clearList = true;
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
	private PShape[] shape = new PShape[1];
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
		if ((int) this.getParameterValue("shape")<7) {
			s = this.getShapeIndex((int) this.getParameterValue("shape")); //)new surface.Horn(APP.getApp(),20,20);
			//s = new surface.MoebiusStrip(APP.getApp(),20,20);
			//s = new surface.Torus(APP.getApp(), 20, 20, 5, 5);
	        //s.setScale(1);
	        s.initColors(APP.getApp().color(255, 125, 0));
	        s.initColors((int)this.getParameterValue("colour1"),(int)this.getParameterValue("colour2"));
	        //s.setTexture(in());
	        //t = s.getSurface();
	        //t.fill(APP.getApp().color(255, 125, 0));
	        //t.scale(w/3);
	        //t.rotateY((Float)getParameterValue("zRotate"));
			//}
			
			t = s.getSurface();
		} else {
			//this.shape[(int)this.getParameterValue("shape")-7] = null;
			//this.shape = new PShape[1];
				// once object is loaded this is ok until collectobject ruins things :/
			if ((this.shape[(int)this.getParameterValue("shape")-7]==null)) {
				println("loading head");
				//PShape list = APP.getApp().createShape(APP.getApp().GROUP);
				PShape p = APP.getApp().loadShape("head.obj");
				//println("got shape " + p);
				p.scale(20f);
				
				// clone the shape before use?
				/*t = APP.getApp().createShape(PShape.GEOMETRY);
				t.beginShape();
				for ( int i = 0 ; i < p.getVertexCount() ; i++ ) {
					PVector v = p.getVertex(i);
					t.vertex(v.x, v.y);
				}
				t.endShape();*/
				//t.setKind(p.getKind());
				
				//list.addChild(p);
				//t = APP.getApp().copyShape(list);
				//t = list;

				this.shape[(int)this.getParameterValue("shape")-7] = p;//list; //list; //APP.getApp().loadShape("head.obj");
				//this.shape[(int)this.getParameterValue("shape")-7].scale(1.0f);
			}
			
			/*// clone the object
			t = APP.getApp().createShape(PShape.GEOMETRY);
			for ( int i = 0 ; i < t.getVertexCount() ; i++ ) {
				PVector v = t.getVertex(i);
				t.vertex(v.x, v.y);
			}			*/
			
			//t = APP.getApp().copyShape(this.shape[(int)this.getParameterValue("shape")-7]);
			t = this.shape[(int)this.getParameterValue("shape")-7];
			//t.resetMatrix();
			//t.scale(0.000001f);
			println("got shape " + t);
			//t.resetMatrix();
			//println("shape is " + t.getName());
			//this.shape[(int)this.getParameterValue("shape")-7].scale(0.0000000001f);
		}
		//t.setTexture(in());
		//t.setTextureUV(0, -0.5f, 0.1f);
		//t.scale(10f);
		
		return t;
	}

	
	private Surface getShapeIndex(int parameterValue) {
		Surface s = null;
		switch (parameterValue) {
			case 0:
				s = new surface.Horn(APP.getApp(), 
						3+(Integer)getParameterValue("phi"), 
						2+(Integer)getParameterValue("theta")
						);
				//s.setScale(100f * (Float)getParameterValue("shape_f1"));
				//s.setScale(10f);
				break;
			case 1:
				s = new surface.MoebiusStrip(APP.getApp(), 
						3+(Integer)getParameterValue("phi"), 
						3+(Integer)getParameterValue("theta"), 
						1f * (Float)getParameterValue("shape_f1"));
				break;
			case 2:
				float f = 0.1f + (5f * (Float)getParameterValue("shape_f1"))+1f;
				float f2 = 0.5f + (f + (2.5f+(Float)getParameterValue("shape_f2") * f + (1f+(Float)getParameterValue("shape_f3"))));
				float f3 = (1f+(Float)getParameterValue("shape_f3"));

				s = new surface.Torus(APP.getApp(), 
						4+(Integer)getParameterValue("phi"), 
						3+(Integer)getParameterValue("theta"),  
						//0.1f + (f * (Float)getParameterValue("shape_f2")),
						//10f * (Float)getParameterValue("shape_f2") * f,
						f2,
						f3);
				s.setScale(0.5f);
				break;
			case 3:
				s = new surface.Spring(APP.getApp(),
						4+(Integer)getParameterValue("phi"), 
						4+(Integer)getParameterValue("theta"), 
						-150f + (150.0f * (Float)getParameterValue("shape_f1")), 
						-60f + (60.0f * (Float)getParameterValue("shape_f2")), 
						-30f + (30.0f * (Float)getParameterValue("shape_f3")));
				s.setScale(0.1f);
				break;
			case 4:
				s = new surface.SphereHarmonics(APP.getApp(),
						4+(Integer)getParameterValue("phi"), 
						4+(Integer)getParameterValue("theta")
						);
				break;
			case 5:
				s = new surface.SuperEllipsoid(APP.getApp(),
						4+(Integer)getParameterValue("phi"), 
						4+(Integer)getParameterValue("theta"), 
						(5.0f * (Float)getParameterValue("shape_f1")), 
						(10.0f * (Float)getParameterValue("shape_f2")));
				s.setScale(2.0f);
				break;
			case 6:
				s = new surface.JetSurface(APP.getApp(),
						4+(Integer)getParameterValue("phi"), 
						4+(Integer)getParameterValue("theta")
						/*(5.0f * (Float)getParameterValue("shape_f1")), 
						(5.0f * (Float)getParameterValue("shape_f2"))*/);
				break;
		}
		return s;
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
