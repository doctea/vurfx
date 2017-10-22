package vurfeclipse.scenes;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import processing.core.PFont;
import processing.core.PImage;
import vurfeclipse.APP;
import vurfeclipse.ImageRepository;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.Filter;
import vurfeclipse.filters.ImageListDrawer;
import vurfeclipse.filters.TextDrawer;
import vurfeclipse.projects.Project;
import vurfeclipse.sequence.ChangeParameterSequence;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.ShowSceneSequence;
import vurfeclipse.streams.ParameterCallback;

public class ImageListScene extends SimpleScene {
  //int filterCount = 2;

  //Filter[] filters;// = new Filter[filterCount];
  int numBlobs = 60;
	
  ArrayList<String> filenames; // = new ArrayList<String>();
  String src_file, directory;
  
  int current_image_index;

  PImage p;



  @Override
  public boolean setupFilters () {
    int BLOBCOUNT = 5; //20; // set to 50 for production, 5 makes for quick loading!

    this.addFilter(new ImageListDrawer(this).setFilenames(this.getFilenames()).setCurrentIndex(5).setNumBlobs(BLOBCOUNT/*200*/).setFilterName("ImageListDrawer")).setOutputCanvas(this.getCanvasMapping("out")); //.nextMode());
    //this.addFilter(new BlendDrawer(this).setCanvases(this.getCanvasMapping("out"), this.getCanvasMapping("pix0")).setOutputCanvas(getCanvasMapping("out")));
   
    return true;
  }

  
  
  public ImageListScene setCurrentIndex (int index) {
    this.current_image_index = index;
    return this;
  }

  public ImageListScene setFileList(String src_file) {
    this.src_file = src_file;
    return this;
  }

  public ImageListScene setDirectory(String directory) {
	  this.directory = directory;
	  return this;
  }

  public ArrayList<String> getFilenames() {
  	if (filenames == null) {
  		this.loadDirectory();
  	}
  	return this.filenames;
  }
  
  public void loadDirectory() {
	  loadDirectory(this.directory);
  }
  public void loadDirectory(String directory) {
  	if (filenames==null) filenames = new ArrayList<String>();
	  //String path = APP.getApp().sketchPath("bin/data/image-sources/" + directory);	// ffs need this on Windows..
  	  //String path = APP.getApp().sketchPath("data/image-sources/" + directory);	// ffs need this on Windows..
	  //String path = APP.getApp().dataPath("image-sources/" + directory);		// ffs but seem to need this on tohers
	  //String path = Paths.get("bin/").toAbsolutePath().toString() + "/data/image-sources/" + directory;
	  //String path = Paths.get("").toAbsolutePath().toString() + "/data/image-sources/" + directory; // applet mode doesnt need bin
  	  String path = "data/image-sources/" + directory;
	  File folder = new File(path);
	  println(this + "#loadDirectory() got path " + path);
	  int count = 0;
	  if (!folder.exists()) return;
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
  }
  
	public void chooseImageIndex(int imageIndex) {
		this.setCurrentIndex(imageIndex);
		p = getCurrentImage(ImageRepository.IR);
		((ImageListDrawer)this.getObjectForPath("/ImageListDrawer")).setSlide(p);
		//this.getCanvas(getCanvasMapping("pix0")).getSurf().image(p,0,0);
	}

  public PImage getCurrentImage (ImageRepository IR) {
    println(this + "#getCurrentImage for current_image_index: [" + current_image_index + "/" + filenames.size() + "]");
    //PImage p = getImageForFilename(filenames.get(current_image_index));
    //if (!images.containsKey(filenames.get(current_image_index))) {
    /*if(!IR.hasCached(filenames.get(current_image_index), sc.w, sc.h)) {
     current_image_index = 0;
     }*/
    if (filenames.size()==0)
      return null;//current_image_index)
    //return IR.getImageForFilename(filenames.get(current_image_index), sc.w, sc.h);
    if (current_image_index>=filenames.size()) return null;
    return IR.getImageForFilename(filenames.get(current_image_index), this.w, this.h);

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
    println(this + "#nextImage [" + current_image_index + "/" + filenames.size() + "]");
    current_image_index++;
    //IR.precache(filenames.get(current_image_index), sc.w, sc.h);
    if (current_image_index >= filenames.size())
      current_image_index = 0;
    else if (!ImageRepository.IR.hasCached(filenames.get(current_image_index), this.w, this.h)) {
      println(this + "#nextImage: haven't got cached " + filenames.get(current_image_index));
      current_image_index++;// = 0;//current_image_index--;
    }

    //IR.precache(filenames.get(current_image_index), sc.w, sc.h);#
    //p = IR.cacheLoad(filenames.get(current_image_index), sc.w, sc.h);
    p = getCurrentImage(ImageRepository.IR);
    //if (!images.containsKey(filenames.get(current_image_index))) current_image_index-=1; //cacheImage(filenames.get(current_image_index));
  }  
  
  
  
  public void setupCallbackPresets () {
    super.setupCallbackPresets();
    final Scene self = this;
    //println("adding callback 'spin'");
  }

  public ImageListScene(Project host, int w, int h) {
    super(host,w,h);
  }



  	public void setupSequences() {
  		super.setupSequences();
  		int i = 0;
  		
  		for (String file : (this.getFilenames())) {
  			println ("#setupSequences() adding sequence #" + i + "/" + this.getFilenames().size());
  			sequences.put("choose_" + i, new ChooseImageSequence(this, 0, i));
  			i++;
  		}
  	}


	class ChooseImageSequence extends Sequence {
		private int imageIndex;
		public ChooseImageSequence(ImageListScene fx, int sequenceLengthMillis, int imageIndex) {
			super(fx,sequenceLengthMillis);
			this.imageIndex = imageIndex;
		}
		/*@Override
		public ArrayList<Mutable> getMutables() {
			return new ArrayList<Mutable>();
		}*/
		@Override
		public void setValuesForNorm(double norm, int iteration) {
			//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
			//if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
			//host.getFilter("BlendDrawer1").changeParameterValue("Opacity", (float)norm);
		}
		@Override public void onStart() {
			/*((TextDrawer)(host.host.getSceneForPath(getPath()).getFilter("BlendDrawer"))).setText("FeralFest");
	   		if (random(0f,1.0f)>=0.5f)
	   			((BlendDrawer)host.host.getSceneForPath(getPath()).getFilter("BlendDrawer")).setBlendMode((Integer)getRandomArrayElement(new Integer[] { 3, 4, 8, 8, 8, 9, 12 }));
    		//((BlendDrawer)host.host.getSceneForPath(getPath()).getFilter("BlendDrawer")).setMuted((random(0f,1.0f)>=0.25f));
    		 * 
    		 */
			//((ImageListDrawer)host.getFilter("ImageListDrawer")).chooseImageIndex(this.imageIndex);
			println(" changing to image index " + this.imageIndex);
			((ImageListScene)host).chooseImageIndex(this.imageIndex);
		}
		@Override public void onStop() {	}
	}

	@Override
  public boolean initialise() {
		super.initialise();
    // set up inital variables or whatevs
    //image_src = new GLTexture(APP,sc.w,sc.h);
    //PImage im = loadImage(fileName); //"image48173.40247.jpg");
    //image_src.putImage(im);

		if (src_file!=null && !src_file.equals(""))
			this.getFilenames();
		else
			this.loadDirectory();
	    this.initTextures(ImageRepository.IR);
	    
    //image_src.loadTexture(fileName);
    //fileChanged = true;

    return true;
  }
	

  public void initTextures (ImageRepository IR) {
    IR.preload(filenames, this.w, this.h);
  }


}
