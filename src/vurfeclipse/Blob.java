package vurfeclipse;

import java.io.Serializable;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
//import codeanticode.glgraphics.*;

public class Blob implements Serializable {
	public float x = 0;

	public float y = 0;

	float r = 10.0f;

	int c;
	int tint = 255;

	private int shape = 1;
	public static int SH_CIRCLE = 0;
	public static int SH_RECT = 1;
	public static int SH_POLY = 2;
	public static int SH_FLOWER = 3;
	public static int SH_COMPOUND = 4;	// compound and texture were previously flipped
	public static int SH_TEXTURE = 5;
	public static int SH_SVG = 6;
	public static int SH_IMAGE = 7;  
	public int shapesCount = 7; //8;
	public int SH_RANDOM = shapesCount;

	float rot = 0;

	private int strokeSize = 3;

	int numSides = 6;

	private boolean scaleRelative = false;

	boolean edge = false;

	//transient GLTexture src;
	transient PImage src;
	String imageName;

	public void setInput(PImage t) {
		this.src = t;
	}


	public void setImage(String imageName) {
		this.imageName = imageName;
	}

	public Blob() {

	}

	public Blob(int shape, float x, float y, float rot) {
		//this.shape = shape;
		this();
		this.setShape(shape);
		this.setXY(x,y);
		this.setRotation(rot);
	}

	public Blob(int sH_RECT2) {
		// TODO Auto-generated constructor stub
	}

	PShape svg;
	public void loadSVG(String fn) {
		svg = APP.getApp().loadShape(fn); 
	}

	public void setShape (int s) {
		this.shape = PApplet.constrain(s,0,shapesCount);
		if (this.shape==SH_TEXTURE && src==null) {
			System.out.println("Blob: SH_TEXTURE but src is null :(");
			nextShape();//setShape(s+1);
		}
	}

	public int nextShape() {
		setShape(getShape() + 1);
		if (getShape()>=shapesCount) setShape(0);
		return getShape();
	}

	public void setXY(float x2, float y2) {
		this.x = x2;
		this.y = y2;
	}

	public void setRadius(float r) {
		this.r = r;
	}

	public void setColour(int c) {
		this.c = c;
	}
	public void setColour(int r , int g , int b, int a) {
		this.c = a | r<<24 | g << 16 | b << 8;// | a;
	}
	public void setColour(int r, int g, int b) {
		this.c = APP.getApp().color(r,g,b);
	}


	public void setTint(int c) {
		this.tint = c;
	}

	public void setRotation(float rot) {
		this.rot = rot;
	}

	public void setEdge (boolean edge) {
		this.edge = edge;
	}

	public void setSides(int sides) {
		this.numSides = sides;
	}
	public int getSides() {
		return numSides;
	}


	//void draw(PGraphics out) {
	public void draw(PGraphics out, PGraphics src) {
		this.src = src;//.getTexture();
		draw(out);
	}
	public void draw(PGraphics out) {
		
		//if (true) return;
		
		out.pushStyle();
		if (tint!=255) {
			out.fill(c, tint);
		} else {
			out.noTint();
			out.fill(c);
		}
		//System.out.println("blob drawing with colour " + c);
		if (!edge) {
			out.noStroke();
		} else {
			out.stroke(0);
			out.strokeWeight(this.getStrokeSize());
		}
		//out.noTint();
		//out.ellipse(x-r,y-r,r/2,r/2); 

		out.pushMatrix();
		out.translate(x,y);

		if (getShape()!=0 && rot>0) {
			//out.rotate(c<<24);
			//out.rotate(random(rot));
			//out.rotate(rot%(((VurfEclipse)APP.getApp()).millis()/1000)%60);
			out.rotate(rot%60);
		}

		//if (true) return;

		
		float R = isScaleRelative() ? 
				(float)((VurfEclipse)APP.getApp()).brightness(c)/8 : r;
				/*          if (doRelative) {
        blobs[b].setRadius((int)hue(pix)/8+((int)random(r)));//(int)random(r));
      }*/

		//System.out.println("draw shape radius " + R + " shape " + shape);
		int oldShape = getShape();
		//if (oldShape==SH_RANDOM) setShape((int)((VurfEclipse)APP.getApp()).random(SH_RANDOM-3));
		if (false) {
			
		} else 
		if (getShape()==SH_CIRCLE) {
			//out.ellipse(x,y,r,r);
			out.ellipse(0,0,R,R);
		} else if (getShape()==SH_RECT) {
			//out.rect(x-r,y-r,r/2,r/2);
			out.rectMode(PApplet.RADIUS);
			//out.rect(-R,-R,R,R);
			out.rect(0,0,R/2.0f,R/2.0f);
			//out.rect(0,0,R,R);
			//polygon(out, /*doPolyRand?(int)random(3,8):*/4, 0, 0, R, R, 0);
		} else if (getShape()==SH_POLY) {
			//System.out.println("doing poly?");
			polygon(out, /*doPolyRand?(int)random(3,8):*/numSides, 0, 0, R, R, 0);
		} else if (getShape()==SH_FLOWER) {
			this.polygonFlower(out, 0, 0, R);
		} else if (getShape()==SH_TEXTURE) {
			float units_w = 1.0f, units_h = 0.75f;
			float new_w = units_w * R/2.0f;///*sc.w * (sc.w/*/units_w*R;///4;//);  
			float new_h = units_h * R/2.0f;///*sc.h * (sc.h/*/units_h*R;///4;//);

			out.pushMatrix();
			//out.translate(-new_w/2.0f,-new_h/2.0f);
			out.imageMode(APP.getApp().CENTER);
			out.image(src,0,0,new_w,new_h);//sc.w/100*theta,sc.h/100*theta);
			//;;if ((int)((VurfEclipse)APP.getApp()).random(100)==0) System.out.println("BLOB>>>drawing texture " + this.src);
			//out.image(src.getTexture(),0,0,new_w,new_h);
			out.popMatrix();
			//out.rect(0,0,R/2,R/2);

		} else if (getShape()==SH_COMPOUND) {
			drawCompoundBlob(out, x, y, R, R, 0);
		} else if (getShape()==SH_SVG && svg!=null) {
			System.out.println("SVG Draw!");
			drawSVG(out, x, y, R, R, 0);
		} else if (getShape()==SH_IMAGE && imageName!=null && !imageName.equals("")) {
			drawImage(out, x, y, R, R, 0);
		}
		setShape(oldShape);


		out.popMatrix();

		out.popStyle();

	}


	private void polygonFlower(PGraphics out, int cx, int cy, float R) {
		boolean newMode = false;//true;//false;
		if (!newMode) {
			polygon(out, /*doPolyRand?(int)random(3,8):*/numSides, 0, 0, R, R, 0);
			for (int i = 0; i < 360; i+=20) {
				//PShape ring = this.getShapePolygon(6);
				
				float x = PApplet.sin(PApplet.radians(i)) * R/2.0f;// * R/2.0f;
				float y = PApplet.cos(PApplet.radians(i)) * R/2.0f;// * R/2.0f;
				polygon(out, numSides, x, y, R/4.0f,R/4.0f,0);						
			}
		} else {
			PShape flower = 
				getShapePolygon(6); 
				//this.getShapeFlower();
			
			//flower.setFill(out.fillColor);
			//flower.setFill(out.fillColor);
			//flower.setTint(tint);
			flower.setStrokeWeight(strokeSize);
			flower.setFill(APP.getApp().color(this.c, tint)); 
			//flower.setFill(true);
			//flower.draw(out);
			out.resetMatrix();
			out.shape(flower, cx, cy, R, R);
			out.text("test",  0, 0, R, R);

		}
	}

	PShape flowerShape;
	private PShape getShapeFlower() {
		if (true|| flowerShape==null) {
			flowerShape = APP.getApp().createShape(PShape.GROUP);
					
			PShape body = this.getShapePolygon(3);
			body.resetMatrix();
			//body.scale(4);
			//out.rotate(rot*2);
			//flowerShape.beginShape();
			for (int i = 0; i < 360; i+=20) {
				
				PShape ring = this.getShapePolygon(6);
				//PShape ring = APP.getApp().createShape(PShape.RECT); //, 6);
				//PShape ring = APP.getApp().createShape(RE)
				
				float x = PApplet.sin(PApplet.radians(i)) * 0.4f;// * R/2.0f;
				float y = PApplet.cos(PApplet.radians(i)) * 0.4f;// * R/2.0f;
				
				ring.translate(x, y);
				
				ring.setFill(APP.getApp().color(this.c, tint));
				ring.setStroke(true);//APP.getApp().color(this.c, tint));
				
				//body.addChild(ring);
				ring.setStroke(true);
				//out.pushMatrix();
				//out.translate(x,y);
				//out.rotate(rot*4.0f);
				//polygon(out, numSides, 0, 0, R/4.0f, R/4.0f, 0);
				//out.popMatrix();
				//polygon(out, numSides, x, y, R/4, R/4, 0);
			}
			flowerShape.setFill(APP.getApp().color(this.c, tint)); 
			flowerShape.addChild(body);
			body.setStroke(true);//APP.getApp().color(this.c, tint));

			//flowerShape.endShape();
		}
		return flowerShape;
	}



	//void polygon(PGraphics out, int n, float cx, float cy, float r)
	void polygon(PGraphics out, int n, float cx, float cy, float r)
	{
		polygon(out, n, cx, cy, r * 2.0f, r * 2.0f, 0.0f);
	}

	static float TWO_PI = PApplet.TWO_PI;
	static float[] twopi_lookup = new float[] { 3, TWO_PI/1, TWO_PI/2, TWO_PI/3, TWO_PI/4, TWO_PI/5, TWO_PI/6, TWO_PI/7, TWO_PI/8, TWO_PI/9, TWO_PI/10, TWO_PI/11, TWO_PI/12, TWO_PI/13, TWO_PI/14, TWO_PI/15, TWO_PI/16 };

	static PShape[] polygons = new PShape[20];
	


	public PShape getShapePolygon(int n) {
		if (true||polygons[n]==null) {
			//System.out.println("Blob: generating polygon with " + n + " sides");
	
			PShape newshape = APP.getApp().createShape();
			//PShape newshape = Blob.makeShape();
			
			newshape.beginShape();//.beginShape();
	
			float angle = twopi_lookup[n];
	
			for (int i = 0; i < n; i++)
			{
				float calc = angle * i ; //startAngle + angle * (float)i;
				//newshape.fill(this.c); //(int) (Math.random()*255));
				//newshape.setStrokeWeight(this.strokeSize);
				//newshape.setStroke(edge);
				newshape.vertex(
						//cx + w *  
						//1.0f * 
						((VurfEclipse)APP.getApp()).cos(calc),
						//cy + h * 
						//1.0f * 
						((VurfEclipse)APP.getApp()).sin(calc)
						);
			}
			newshape.endShape(PApplet.CLOSE);

			//newshape.setFill(255);
			//newshape.setStrokeWeight(strokeSize);
			polygons[n] = newshape;
			//newshape.scale((float) (r/100.0));
			//return newshape;
		}
		polygons[n].resetMatrix();
		return polygons[n];
	}
	


	//void polygon(PGraphics out, int n, float cx, float cy, float w, float h, float startAngle)
	void polygon(PGraphics out, int n, float cx, float cy, float w, float h, float startAngle)
	{
		if (n > 2)
		{
			boolean newMode = false; //false;
			if (newMode) {	// slower fps (really shit), but doesn't thrash the memory (https://forum.processing.org/two/discussion/367/drawing-many-object-is-really-slow)
				// this is attempt to use PShapes to draw the polygons instead of drawing vertexes in direct mode -- works, but actually seems slower..?
				PShape s = this.getShapePolygon(6); //polygons[n];
				
				out.pushMatrix();
				out.rotate(startAngle);
				out.translate(cx, cy);
				out.scale(w/2.0f,h/2.0f);
				//s.setFill(out.fillColor);
				//s.setTint(255);
				//s.setFill(64);

				//out.shape(s);//, 0, cy, w, h);
				//out.shape(s, cx, cy, w/2.0f, h/2.0f);
				//out.shape(s, cx + w,cy + h, w/2.0f,h/2.0f);
				out.shape(s, 0, 0,300,300);
				//out.scale(w, h);
				//out.text("test", 0,0,0);
				out.popMatrix();

			} else {	// faster fps, but does thrash the memory :/	
				//float angle = TWO_PI/n;
				float angle = twopi_lookup[n];

				/* The horizontal "radius" is one half the width;
	       the vertical "radius" is one half the height */
				w = w / 2.0f;
				h = h / 2.0f;

				boolean newmode2 = true;
				if (!newmode2) {	// thrashes memory but is fastest? 
					out.beginShape();
					for (int i = 0; i < n; i++)
					{
						float calc = startAngle + angle * (float)i;
						out.vertex(cx + w * ((VurfEclipse)APP.getApp()).cos(calc),
								cy + h * ((VurfEclipse)APP.getApp()).sin(calc));
					}
					out.endShape(PApplet.CLOSE);
				} else { // speed not great but better than 'newmode', only thrashes memory half as bad as fastest !'newmode2'
					PShape s = this.getShapePolygon(n);
					out.pushMatrix();
					out.rotate(startAngle);
					out.scale(w,h);
					s.setFill(out.fillColor);
					//s.setStroke(this.strokeSize);
					//s.setStroke(!edge);
					s.setStrokeCap(10);//this.strokeSize);
					//s.setstr
					
					out.shape(s);
					out.popMatrix();
				}
			}
		}
	}

	void drawImage(PGraphics out, float cx, float cy, float w, float h, float startAngle) {
		if (imageName!=null) {
			out.pushMatrix();
			out.rotate(startAngle);
			out.image(ImageRepository.IR.getImageForFilename(imageName), cx, cy, w, h);
			out.popMatrix(); 
		}
	}

	void drawSVG(PGraphics out, float cx, float cy, float w, float h, float startAngle) {
		if (svg!=null) {
			out.pushMatrix();
			out.rotate(startAngle);
			out.shape(svg, cx, cy, w, h);
			out.popMatrix();
		}
	}

	Blob compoundBlob[];/* = {
	         new Blob(SH_RECT),//,x,y),//,(float)startAngle),
	         new Blob(SH_POLY),//,x,y),+(float)step,(float)startAngle),
	         new Blob(SH_RECT),//,x,y),+(float)step+10f,(float)startAngle),
	         new Blob(SH_POLY),//,x,y),+(float)step*2f,(float)startAngle),
	         new Blob(SH_POLY),//,x,y),+(float)step*3f,(float)startAngle),
	         //new Blob(SH_TEXTURE,x,y+(int)step*4,(int)startAngle),
	         new Blob(SH_POLY),//,x-15),,y+(float)step*5f,(float)startAngle),
	         new Blob(SH_POLY)//,x+15),,y+(float)step*5f,(float)startAngle),
  };*/
	void drawCompoundBlob(PGraphics out, float cx, float cy, float w, float h, float startAngle) {

		float step = (float)h/8;
		//this.compoundBlob[0].x = x; this.compoundBlob.y = y;
		if(this.compoundBlob==null) {
			this.compoundBlob = new Blob[] {
					new Blob(SH_RECT,x,y,(float)startAngle),
					new Blob(SH_POLY,x,y+(float)step,(float)startAngle),
					new Blob(SH_RECT,x,y+(float)step+10f,(float)startAngle),
					new Blob(SH_POLY,x,y+(float)step*2f,(float)startAngle),
					new Blob(SH_POLY,x,y+(float)step*3f,(float)startAngle),
					//new Blob(SH_TEXTURE,x,y+(int)step*4,(int)startAngle),
					new Blob(SH_POLY,x-15,y+(float)step*5f,(float)startAngle),
					new Blob(SH_POLY,x+15,y+(float)step*5f,(float)startAngle)
			};
		}

			compoundBlob[0].x = x; 	compoundBlob[0].y = y; 				compoundBlob[0].rot = startAngle;
			compoundBlob[1].x = x; 	compoundBlob[1].y = y+step; 		compoundBlob[1].rot = startAngle;
			compoundBlob[2].x = x; 	compoundBlob[2].y = y+step+10f; 	compoundBlob[2].rot = startAngle;
			compoundBlob[3].x = x; 	compoundBlob[3].y = y+step*2f; 		compoundBlob[3].rot = startAngle;
			compoundBlob[4].x = x; 	compoundBlob[4].y = y+step*3f; 		compoundBlob[4].rot = startAngle; 
			compoundBlob[5].x = x-15; compoundBlob[5].y = y+step*5f; 	compoundBlob[5].rot = startAngle; 
			compoundBlob[6].x = x+15; compoundBlob[6].y = y+step*5f; 	compoundBlob[6].rot = startAngle;
			//};

			int numSpokes = 3;
			for (int r = 0 ; r < 360 ; r+=(360/numSpokes)) {
				out.pushMatrix();
				out.rotate(PApplet.radians(r));
				for (int i = 0 ; i < compoundBlob.length ; i++) {
					compoundBlob[i].setEdge(edge);
					//compoundBlob[i].setColour(color(random((i/numSpokes)*255.0),random(255.0),random(255.0),255));
					compoundBlob[i].setColour(this.c); ///1 * color(random(255.0),random(255.0),random(255.0),255));
					//System.out.println("in drawcompoundblob setting colour to " + c);
					compoundBlob[i].setTint(255);
					compoundBlob[i].setRadius(((float)i*(float)w/12.0f));
					compoundBlob[i].setSides(((VurfEclipse)APP.getApp()).constrain(8+i/3,3,12));
					compoundBlob[i].setInput(src);//out.getTexture());//src
					if (compoundBlob[i].getShape() == SH_COMPOUND) compoundBlob[i].setShape(SH_POLY);
					//compoundBlob[i].setShape(i%shapesCount-2);
					compoundBlob[i].draw(out);
				}
				out.popMatrix();
			}
		}


		public boolean isScaleRelative() {
			return scaleRelative;
		}


		public void setScaleRelative(boolean scaleRelative) {
			this.scaleRelative = scaleRelative;
		}


		public int getStrokeSize() {
			return strokeSize;
		}


		public void setStrokeSize(int strokeSize) {
			this.strokeSize = strokeSize;
		}


		public int getShape() {
			return shape;
		}


		public PShape collectShape(PGraphics in) {
			
			float R = isScaleRelative() ? 
					(float)((VurfEclipse)APP.getApp()).brightness(c)/8 : r;
			
			int oldShape = getShape();
			//if (oldShape==SH_RANDOM) setShape((int)((VurfEclipse)APP.getApp()).random(SH_RANDOM-3));
			
			PShape s = null;// = new PShape();			
			if (false) {
				
			} else 
			if (getShape()==SH_CIRCLE) {
				//out.ellipse(x,y,r,r);
				//out.ellipse(0,0,R,R);
				s = APP.getApp().createShape(APP.getApp().ELLIPSE,0,0,1,1);
				//s = getShapePolygon(0);
				s.scale(R/2);
			} else if (getShape()==SH_RECT) {
				//out.rectMode(PApplet.RADIUS);
				//out.rect(0,0,R/2.0f,R/2.0f);
				//polygon(out, /*doPolyRand?(int)random(3,8):*/4, 0, 0, R, R, 0);
				//s = APP.getApp().createShape(APP.getApp().RECT);
				s = getShapePolygon(4);
				s.scale(R/2);
			} else if (getShape()==SH_POLY) {
				//System.out.println("doing poly?");
				//polygon(out, /*doPolyRand?(int)random(3,8):*/numSides, 0, 0, R, R, 0);
				//s = APP.getApp().
				s = getShapePolygon(numSides);
				s.scale(R/4);
			} else if (getShape()==SH_FLOWER) {
				//this.polygonFlower(out, 0, 0, R);
				s = this.getShapeFlower();
				s.scale(R/4);
			} else if (getShape()==SH_TEXTURE) {	// THIS SECTION ISN'T CALLED RIGHT NOW - USES OLD METHOD INSTEAD SINCE TEXTURES DONT THRASH MEMORY -2018-09-27
				float units_w = 1.0f, units_h = 0.75f;
				float new_w = units_w * R/2.0f;///*sc.w * (sc.w/*/units_w*R;///4;//);  
				float new_h = units_h * R/2.0f;///*sc.h * (sc.h/*/units_h*R;///4;//);

				/*out.pushMatrix();
				out.translate(-new_w/2.0f,-new_h/2.0f);
				out.image(src,0,0,new_w,new_h);//sc.w/100*theta,sc.h/100*theta);
				//;;if ((int)((VurfEclipse)APP.getApp()).random(100)==0) System.out.println("BLOB>>>drawing texture " + this.src);
				//out.image(src.getTexture(),0,0,new_w,new_h);
				out.popMatrix();
				//out.rect(0,0,R/2,R/2);*/
				
				s = getShapePolygon(4);
				s.setTexture(src);
				//s.scale(new_w, new_h);
				
				//s = new PGraphics(src);

			} else if (getShape()==SH_COMPOUND) {
				s = collectCompoundBlob(x, y, R, R, 0);
				s.scale(R/4);
				///// collect co
				System.out.println("TODO: COMPOUND BLOB COLLECTION FOR SINGLE-PASS RENDERING OF SHAPES IN BLOBDRAWER ETC");
			} else if (getShape()==SH_SVG && svg!=null) {
				System.out.println("SVG Draw!");
				//drawSVG(out, x, y, R, R, 0);
			} else if (getShape()==SH_IMAGE && imageName!=null && !imageName.equals("")) {
				//drawImage(out, x, y, R, R, 0);
			}
			
			if (s==null) {
				System.out.println("NULL SHAPE CAUGHT !");
				return s;
			}

			setShape(oldShape);
			
			s.setFill(APP.getApp().color(this.c, tint)); //(int) (Math.random()*255));
			//s.setStroke(false); //this.strokeSize>0);
			//s.setStrokeWeight(0.0001f);
			//s.setStrokeCap(this.strokeSize/10);
			//s.setTint(this.tint);
			//s.scale(this.r);

			
			//out.pushStyle();
			/*if (tint==255) {
				s.fill(c, tint/255);
			} else {
				//s.noTint();
				s.fill(c,tint/255);
			}
			//System.out.println("blob drawing with colour " + c);
			if (!edge) {
				s.noStroke();
			} else {
				s.stroke(0);
				s.strokeWeight(this.getStrokeSize());
			}*/
			//out.noTint();
			//out.ellipse(x-r,y-r,r/2,r/2); 

			//s.pushMatrix();
			
			
			//s.translate(x,y); //<---this might be needed ! 

			if (getShape()!=0 && rot>0) {
				//out.rotate(c<<24);
				//out.rotate(random(rot));
				//out.rotate(rot%(((VurfEclipse)APP.getApp()).millis()/1000)%60);
				s.rotate(rot%60);
			}
			return s;

			//if (true) return;

					/*          if (doRelative) {
	        blobs[b].setRadius((int)hue(pix)/8+((int)random(r)));//(int)random(r));
	      }*/


		}


		private PShape collectCompoundBlob(float cx, float cy, float w, float h, float startAngle) {

			float step = (float)h/8;
			//this.compoundBlob[0].x = x; this.compoundBlob.y = y;
			if(this.compoundBlob==null) {
				this.compoundBlob = new Blob[] {
						new Blob(SH_RECT,x,y,(float)startAngle),
						new Blob(SH_POLY,x,y+(float)step,(float)startAngle),
						new Blob(SH_RECT,x,y+(float)step+10f,(float)startAngle),
						new Blob(SH_POLY,x,y+(float)step*2f,(float)startAngle),
						new Blob(SH_POLY,x,y+(float)step*3f,(float)startAngle),
						//new Blob(SH_TEXTURE,x,y+(int)step*4,(int)startAngle),
						new Blob(SH_POLY,x-15,y+(float)step*5f,(float)startAngle),
						new Blob(SH_POLY,x+15,y+(float)step*5f,(float)startAngle)
				};
			}

				compoundBlob[0].x = x; 	compoundBlob[0].y = y; 				compoundBlob[0].rot = startAngle;
				compoundBlob[1].x = x; 	compoundBlob[1].y = y+step; 		compoundBlob[1].rot = startAngle;
				compoundBlob[2].x = x; 	compoundBlob[2].y = y+step+10f; 	compoundBlob[2].rot = startAngle;
				compoundBlob[3].x = x; 	compoundBlob[3].y = y+step*2f; 		compoundBlob[3].rot = startAngle;
				compoundBlob[4].x = x; 	compoundBlob[4].y = y+step*3f; 		compoundBlob[4].rot = startAngle; 
				compoundBlob[5].x = x-15; compoundBlob[5].y = y+step*5f; 	compoundBlob[5].rot = startAngle; 
				compoundBlob[6].x = x+15; compoundBlob[6].y = y+step*5f; 	compoundBlob[6].rot = startAngle;
				//};
				PShape g = APP.getApp().createShape(APP.getApp().GROUP);

				int numSpokes = 3;
				for (int r = 0 ; r < 360 ; r+=(360/numSpokes)) {
					//out.pushMatrix();
					//out.rotate(PApplet.radians(r));
					for (int i = 0 ; i < compoundBlob.length ; i++) {
						compoundBlob[i].setEdge(edge);
						//compoundBlob[i].setColour(color(random((i/numSpokes)*255.0),random(255.0),random(255.0),255));
						compoundBlob[i].setColour(this.c); ///1 * color(random(255.0),random(255.0),random(255.0),255));
						//System.out.println("in drawcompoundblob setting colour to " + c);
						compoundBlob[i].setTint(255);
						compoundBlob[i].setRadius(((float)i*(float)w/12.0f));
						compoundBlob[i].setSides(((VurfEclipse)APP.getApp()).constrain(8+i/3,3,12));
						compoundBlob[i].setInput(src);//out.getTexture());//src
						if (compoundBlob[i].getShape() == SH_COMPOUND) compoundBlob[i].setShape(SH_POLY);
						//compoundBlob[i].setShape(i%shapesCount-2);
						//compoundBlob[i].draw(out);
						g.addChild(compoundBlob[i].getShapePolygon(6));
					}
				}
				return g; 
		}


	}
