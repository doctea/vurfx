package vurfeclipse.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
//import com.google.common.io.Paths;
import java.util.*;
import java.util.Map.Entry;

import processing.core.PGraphics;
import processing.core.PImage;
import vurfeclipse.*;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.scenes.Scene;



public class ImageListDrawer extends Filter {

  int image_window_size = 5;
  transient HashMap<String, PImage> image_cache = new HashMap<String, PImage>(); //PImage[image_window_size];

  int numBlobs = 60;
  int current_image_index;

  int offsetx=0, offsety=0;

  //GLTexture[] image_srcs;
  transient PImage[] image_srcs;
  //String fileName = "output/image48173.40247.jpg";
  //String fileNames[] = new String[]();
  //String[] filenames;

  String src_file, directory;

  //boolean fileChanged = true;

  public ImageListDrawer(Scene sc) {
    super(sc);
  }
  ImageListDrawer(Scene sc, String src_file) {
    this(sc);
    this.src_file = src_file;
  }

  
  public ImageListDrawer setDirectory(String directory) {
	  this.directory = directory;
	  return this;
  }

  @Deprecated
  public ImageListDrawer setFileList(String src_file) {
    this.src_file = src_file;
    return this;
  }

  
  public ImageListDrawer setCurrentIndex (int index) {
    this.current_image_index = index;
    this.changeParameterValue("current_image_index", index);
    return this;
  }

  /*public void setXYOffset(int x, int y) {
   this.offsetx = x;
   this.offsety = y;
   }*/

  //HashMap<String,PImage> images = new HashMap<String,PImage>();
  
  ArrayList<String> filenames = new ArrayList<String>();
  
  public ImageListDrawer setFilenames(ArrayList<String> filenames) {
	  this.filenames = filenames;
	  return this;
  }
  
  
  public ArrayList<String> getFilenames() {
  	if (filenames == null) {
  		this.loadDirectory();
  	}
  	return this.filenames;
  }

  
  public void loadFilenames() {
    //filenames = new String[numBlobs];
    //image_srcs = new GLTexture[numBlobs];
    //image_srcs = new PImage[numBlobs];
    String l;

    BufferedReader reader = ((VurfEclipse)APP.getApp()).createReader(src_file);
    boolean end = false;
    int c = 0;
    while (!end) {
      if (c>=numBlobs) {
        end = true;
        break;
      }
      try {
        l = reader.readLine();
        if (l!=null && !l.equals("")) {
          filenames.add(l);
          //filenames[c] = l;
          c++;
        }
      }
      catch (IOException e) {
        l = null;
      }
      if (l==null) {
        end = true;
      }
    }
  }

  
  public ImageListDrawer loadDirectory() {
	  loadDirectory(this.directory);
	  return this;
  }

  
  public ImageListDrawer loadDirectory(String directory) {
	  //String path = APP.getApp().sketchPath("bin/data/image-sources/" + directory);	// ffs need this on Windows..
	  //String path = APP.getApp().sketchPath("../bin/data/image-sources/" + directory);
	  String path = APP.getApp().dataPath("image-sources/" + directory);		// ffs but seem to need this on tohers
	  //String path = Paths.get("bin/").toAbsolutePath().toString() + "/data/image-sources/" + directory;
	  //String path = Paths.get("").toAbsolutePath().toString() + "/data/image-sources/" + directory; // applet mode doesnt need bin
	  File folder = new File(path);
	  println("#loadDirectory() got path " + path);
	  int count = 0;
	  if (!folder.exists()) return this;
	  for (final File fileEntry : folder.listFiles()) {
		  if (fileEntry.isDirectory()) {
			  // skip; maybe recurse tho in future
		  } else {
			  if (fileEntry.getName().endsWith(".png") || fileEntry.getName().endsWith(".jpg") || fileEntry.getName().endsWith(".jpeg") || fileEntry.getName().endsWith(".png"))
			  	if (!fileEntry.getName().contains("_small")) {
			  		count++;
					  filenames.add(directory + "/" + fileEntry.getName());
			  	}
			  if (count>=numBlobs) break;
		  }
	  }
	  return this;
  }

  public void initTextures (ImageRepository IR) {
    IR.preload(filenames, sc.w, sc.h);
  }


  /*
   PImage p = images.get(fn);
   if (null==p) {*/
  /*new Thread() {
   public void run() {*/
  //cacheImage(fn);
  /*}
   }.start();*/
  /*      p = images.get(fn);
   }
   return p;//images.get(fn);
   }*/

  
  public PImage getCurrentImage (ImageRepository IR) {
    if (debug) println(this + "#getCurrentImage for current_image_index: [" + current_image_index + "/" + filenames.size() + "]");
    current_image_index = (Integer)getParameter("current_image_index").intValue();//.cast(payload)Integer.parseInt(this.getParameterValue("current_image_index").toString());	// do parseInt in case anyone sends us a Float etc
    //PImage p = getImageForFilename(filenames.get(current_image_index));
    //if (!images.containsKey(filenames.get(current_image_index))) {
    /*if(!IR.hasCached(filenames.get(current_image_index), sc.w, sc.h)) {
     current_image_index = 0;
     }*/
    if (filenames.size()==0)
      return null;//current_image_index)
    //return IR.getImageForFilename(filenames.get(current_image_index), sc.w, sc.h);
    if (current_image_index>=filenames.size() || current_image_index<0) return null;
    return IR.getImageForFilename(filenames.get(current_image_index), sc.w, sc.h);

    /*if (this.image_srcs[current_image_index]==null)
     current_image_index = 1; //return this.image_srcs[1];*/

    /*PImage c = this.image_srcs[current_image_index];
     int portX = (int)random(0, c.width);
     int portY = (int)random(0, c.height);
     int portW = (int)random(20, c.width-portX);
     int portH = (int)random(20, c.height-portY);
     current_image.copy(c, portX,portY,portW,portH,0,0,sc.w,sc.h);
     return current_image;*/
    //return this.image_srcs[current_image_index];
  }
  
  public void nextImage () {
	  if (debug) println(this + "#nextImage [" + current_image_index + "/" + filenames.size() + "]");
    current_image_index = (Integer)getParameterValue("current_image_index");
    current_image_index++;
    //IR.precache(filenames.get(current_image_index), sc.w, sc.h);
    if (current_image_index >= filenames.size()-1)
      current_image_index = 0;
    else if (!ImageRepository.IR.hasCached(filenames.get(current_image_index), sc.w, sc.h)) {
      println(this + "#nextImage: haven't got cached " + filenames.get(current_image_index));
      current_image_index++;// = 0;//current_image_index--;
    }
    
    this.changeParameterValue("current_image_index", new Integer(current_image_index));

    //IR.precache(filenames.get(current_image_index), sc.w, sc.h);#
    //p = IR.cacheLoad(filenames.get(current_image_index), sc.w, sc.h);
    setSlide(getCurrentImage(ImageRepository.IR));
    //if (!images.containsKey(filenames.get(current_image_index))) current_image_index-=1; //cacheImage(filenames.get(current_image_index));
  }

  public ImageListDrawer setNumBlobs (int numBlobs) {
    this.numBlobs = numBlobs;
    return this;
  }

  public void setParameterDefaults () {
    //this.setParameterValue("radius", 10.0);
    //this.setParameterValue("rotation", 0.0);
    super.setParameterDefaults();
    this.addParameter("translate_x", new Integer(0), -sc.w/2, sc.w/2);
    this.addParameter("translate_y", new Integer(0), -sc.h/2, sc.h/2);
    
    this.addParameter("scale", new Float(1.0f), 1.0f, 5.0f);
    
    this.addParameter("current_image_index", new Integer(0), 0, this.filenames.size()>0?this.filenames.size()-1:0);	
    /*this.addParameter("tint", new Integer(128), 0, 255);//new Integer(128));
     this.addParameter("shape", new Integer(0), 0, b.shapesCount);
     this.addParameter("colour", color(random(255),random(255),random(255),128));*/
    //this.addParameter("radius", 0.5, 0.01, 20.0);
  }


  public boolean initialise() {
	  super.initialise();
    // set up inital variables or whatevs
    //image_src = new GLTexture(APP,sc.w,sc.h);
    //PImage im = loadImage(fileName); //"image48173.40247.jpg");
    //image_src.putImage(im);

	if (src_file!=null && !src_file.equals(""))
		this.loadFilenames();
	else
		this.loadDirectory();
    this.initTextures(ImageRepository.IR);

    //image_src.loadTexture(fileName);
    //fileChanged = true;

    return true;
  }

  public Filter nextMode () {
    nextImage();
    /*
    current_image_index++;
     if (current_image_index>=numBlobs) current_image_index = 0;
     //current_image = image_srcs[++current_image_index];
     System.out.println("loading image from " + image_srcs[current_image_index] + " (index " + current_image_index + ")");
     current_image.putImage(image_srcs[current_image_index]);
     System.out.println("Set current_image to " + current_image + " for index " + current_image_index);*/
    return this;
  }

  transient private PImage p;
  public boolean applyMeatToBuffers() {
    //System.out.println("in applymeattobuffers in ImageListDrawer (" + this + "), src is " + src + " and out is " + out);
    //if (fileChanged) {
    //image draw mode

    //out.pushMatrix();
    /*out.translate(
     sc.w - (sc.w/2 * map((Float)getParameterValue("scale"),1.0f,5.0f,0.0f,1.0f)),
     sc.h - (sc.h/2 * map((Float)getParameterValue("scale"),1.0f,5.0f,0.0f,1.0f))
     );*/
    /*      out.translate(
     sc.w*0.5f+(map(5.0-(Float)getParameterValue("scale"),1.0f,5.0f,-0.5f,0.5f)),
     sc.h*0.5f+(map(5.0-(Float)getParameterValue("scale"),1.0f,5.0f,-0.5f,0.5f))
     );      */
    //out.translate((Integer)getParameterValue("translate_x"), (Integer)getParameterValue("translate_y"));
    //out.scale((Float)getParameterValue("scale"));//random(0,100));
	  
	  //println("drawing to " + out + " " + canvas_out);

    //p = getCurrentImage(IR);
    if (getSlide()!=null) {
    	PGraphics out = out();
      out.pushMatrix();
      out.imageMode(out.CENTER);
      //out.image(getSlide(), -0.5f, -0.5f, 0.5f, 0.5f); //, 0, 0, sc.w, sc.h);//sc.w*map((Float)getParameterValue("scale"),1.0,5.0,0.0,1.0),sc.h*map((Float)getParameterValue("scale"),1.0,5.0,0.0,1.0));
      //if (getSlide().isModified())
    	  //out.background(getSlide());	// background is death to performance apparently?
      //out.imageMode(out.);
      //out.image(getSlide(),0-sc.w/2,0-sc.h/2,sc.w,sc.h);
      //out.image(getSlide(), -sc.w/2, -sc.h/2, sc.w/2, sc.h/2);
      out.translate(sc.w/2, sc.h/2);
      out.scale((Float)getParameterValue("scale"));
      out.image(getSlide(),0,0);
      
      //out.scale(-1,0);
      //out.translate((Integer)getParameterValue("translate_x"), (Integer)getParameterValue("translate_y"));
      
      if ((Float)Parameter.castAs(getParameterValue("translate_x"), Float.class) < 0.0f) {
        out.image(getSlide(), sc.w, 0, sc.w, sc.h);
      }
      else if ((Float)Parameter.castAs(getParameterValue("translate_x"), Float.class) >= 0.0f) {
        out.image(getSlide(), 0-sc.w, 0, sc.w, sc.h);
      }
      out.popMatrix();
    }
    //setSlide(null);
    //out.popMatrix();
    //fileChanged = false;
    //}


    // pixel copy mode
    //arrayCopy(src.pixels, out.pixels);
    return true;
  }
  
	public void chooseImageIndex(int imageIndex) {
		this.setCurrentIndex(imageIndex);
		setSlide(getCurrentImage(ImageRepository.IR));
	}
	public PImage getSlide() {
		return p;
	}
	public void setSlide(PImage p) {
		this.p = p;
	}
	
	@Override
	synchronized public void updateParameterValue(String paramName, Object value) {
		//if (!this.parameters.containsKey(paramName)) this.addParameter(paramName, value);
		//if (paramName!="current_image_index") {
			super.updateParameterValue(paramName, value);
		//} else {
			if (paramName.equals("current_image_index"))  
				setSlide(getCurrentImage(ImageRepository.IR));
		//}

	  /*public void beginDraw() {
	   //src.loadPixels();
	   //out.loadPixels();
	   out.beginDraw();
	   }
	
	   public void endDraw() {
	   //out.updatePixels();
	   out.endDraw();
	   }*/
	}
	
	@Override
	public HashMap<String, Object> collectFilterSetup() {
		HashMap<String, Object> params = super.collectFilterSetup();
		
		params.put("directory", this.directory);
		return params;
	}
	
	@Override
	public ImageListDrawer readSnapshot(Map<String, Object> input) {
		super.readSnapshot(input);	// TODO: a conundum; if we don't do this on first load then a bunch of stuff doesn't get set up properly; if we do do this then ImageListDrawer doesn't appear to output to the correct canvas..?
		if (input.containsKey("directory")) {
			this.directory = (String) input.get("directory");
			this.loadDirectory();
		}
		
		return this;
	}
}

