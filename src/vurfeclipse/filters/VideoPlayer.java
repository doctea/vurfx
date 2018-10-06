package vurfeclipse.filters;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vurfeclipse.APP;
import vurfeclipse.scenes.Scene;
import processing.core.PGraphics;
import processing.video.*;

public class VideoPlayer extends Filter {
	transient Movie stream;
	String filename;

	transient PGraphics tex;
	transient PGraphics newTex;

	int mode = 0;
  int current_video_index;

	int volume = 255;

	public ArrayList<String> videos = new ArrayList<String>();

	public Filter nextMode () {
		/*    long maxMode = 50;//(long)stream.length()/50;
    mode++;
    if (mode>=maxMode) mode = 0;
    if (!stream.isSeeking()) {
      long seek = mode * stream.length()/maxMode;
      System.out.println("currently at " + stream.frame() + ", seeking to mode " + mode + "/" + maxMode + " at " + seek);
      stream.jump((int)seek);
    }*/
		/*String[] videos = new String[] {
      "video/129-Probe 7 - Over and Out(1)-00.mkv",
      "tworld84.dv.ff.avi",
      "video/129-Probe 7 - Over and Out(1)-10.mkv",

      "video/129-Probe 7 - Over and Out(1)-01.mkv",
      "video/129-Probe 7 - Over and Out(1)-02.mkv",
      "video/129-Probe 7 - Over and Out(1)-03.mkv",
      "video/129-Probe 7 - Over and Out(1)-04.mkv",
      "video/Life of Brian(XviD).avi",
      //"video/Wilfred.US.S01E02.HDTV.XviD-FQM.Trust"
    };*/

		//changeVideo(videos[(int)random(0,videos.length)]);
		//changeVideo(videos.get((int) random(0,videos.size())));
		println("Changing video to " + videos.get((int) this.getParameterValue("current_video_index")%videos.size()));
		if ((Boolean)this.getParameterValue("next")==true) {
			this.changeParameterValue("current_video_index", (int) this.getParameterValue("current_video_index")+1);
		} else {
			changeVideo(videos.get((int) this.getParameterValue("current_video_index")%videos.size()));
		}

		return this;
	}

	public VideoPlayer(Scene sc) {
		super(sc);
		//this(sc,APP.getApp().dataPath("/video-sources/rainbow/cragulon/Chain Reaction.ogv")); //balloon.ogg"));
	}

	public VideoPlayer(Scene sc, String filename) {
		super(sc);
		//this.filename = filename;
		if (filename!="") videos.add(filename);
	}

	public void setMuted(boolean on) {
		super.setMuted(on);
		if (!on) {
			if (!this.started) {
				if (stream!=null && stream.available()) {
					println("starting again");
					if (true==(boolean)this.getParameterValue("loop") )
						this.loop(stream);
					else if (true!=(boolean)this.getParameterValue("pause") ) 
						this.play(stream);  
					println("started");
				}
		}
		} else {
			if (this.started) {
				if (stream!=null && stream.available()) {
					println("pausing");
					this.pause(stream);
					println("paused");
				}
			}
		}
	}

	synchronized void pause(Movie stream) {
		if (this.started) {
			if (stream!=null) {
				println("setting pause");
				stream.pause();
				this.started = false;
			}
		}		
	}

	synchronized private void play(Movie stream) {
		if (!this.started) {
			if (stream!=null) {
				println("setting play");
				this.started = true;
				stream.play();
			}
		}		
	}

	synchronized void loop(Movie stream) {
		if (stream!=null) {
			//println("setting loop");
			stream.loop();
			//this.play(stream);
		}
	}

	//Thread loader =  new Thread() {

	transient Movie oldStream;
	transient Movie newStream;
	Boolean changing = false;
	Thread changerThread;
	private int startDelay;
	private String directory;
	private Float last_speed;
	protected HashMap<String,Movie> movie_cache = new HashMap<String,Movie>();
	public synchronized void changeVideo(String fn) {

		//if (true) return; // dirty hack 2016-12-1? to only play once?!?!?

		//if (changing) return;
		final String filename = fn;
		if (filename.equals("")) return;
		this.filename = filename;
		//changing = true;
		final VideoPlayer self = this;

		println("got changeVideo('"+fn+"'");

  	if (changerThread!=null && !changerThread.isAlive()) {
			changing = new Boolean(false);
			if (newStream!=null) {
				println("about to dispose of newstream that already exists even though changerthread is dead?");
				//newStream.stop();
				newStream.dispose();
				println("disposed");
			}
		};
		
		if (changing && changerThread!=null && changerThread.isAlive()) {
			println("changing is set and changerthread is alive - returning");
			return;
		}
		
		this.changerThread = new Thread () {
			boolean alreadyRun = false;

			public void start () {
				super.start();
			}
			public synchronized void run () {
				//changing = new Boolean(false);
				//synchronized(changing) {
				if (changing) return;
				if (alreadyRun) return;
				alreadyRun = true;
				changing = true;

				println("Loaded new.." + filename);
				//GSMovie newStream;
				oldStream = stream;
				/*if (oldStream!=null) {
	        oldStream.pause();
	        oldStream.stop();
	        oldStream.dispose();
	        oldStream = null;
	        //oldTex.delete();
        }*/
				if (newStream!=null) { 
					newStream.stop(); 
					newStream.dispose(); 
				}
				if (movie_cache.get(filename)==null) {
					newStream = new Movie(APP.getApp().getCF(),filename);
					movie_cache.put(filename, newStream);
				} else {
					newStream = movie_cache.get(filename);
				}
				//newTex = new GLTexture(APP, sc.w, sc.h);
				//newStream.setPixelDest(tex, true);
				//newStream.volume(volume);		//TODO: why doesn't this compile on MacOSX?
				println("Set volume and pixeldest..");
				//if (!((VurfEclipse)APP.getApp()).exportMode)

				//newStream.jump(0.0f);
				//newStream.speed(Math.abs((Float)getParameterValue("speed")));

				//newStream.loop();
				//println("about to do ")
				//while (!newStream.available())
				//try { sleep(50); } catch (Exception e) {}
				//if (newStream.available())
				//	newStream.read();
				println("read from newStream");
				//newStream.setPixelDest(tex, true);
				self.newStream = newStream;

				//GSMovie oldStream = stream;
				//GLTexture oldTex = tex;
				self.stream = newStream;
				println("Swapping streams to " + filename);
				self.setFilterLabel(getFilterName() + filename);
				//tex = newTex;
				if (oldStream!=null) {
					//oldStream.jump(0.0f);
					//oldStream.speed(1.0f);
					//if (started)
					  //oldStream.pause();
					if (started) {
						println("stopping oldstream..");
						oldStream.stop();
						println("stopped.");
					}
					println("disposing of oldstream..");
					//oldStream.dispose();
					println("disposed of oldstream.");
					oldStream = null;
					//oldTex.delete();
				}
				if ((Boolean)getParameterValue("loop")==true) {
					loop(stream);
				}
				play(stream);
				self.newStream = null;
				//newTex = null;
				self.changing = false;
				started = true;

				/*GSMovie tempStream = stream;
        stream = newStream;
        System.out.println("Swapping stream..");
        stream.delete();
        stream = null;*/
				//changing = false;
				return;
				}
			//}
		};
		this.changerThread.start();
		//this.changerThread.run();
	}

	public void loadDirectory() {
		loadDirectory(this.directory);
	}
	public void loadDirectory(String directory) {
		//String directory = ""; // dummy
		//String path = APP.getApp().sketchPath("bin/data/" + directory);	// ffs need this on Windows..
		String path = APP.getApp().dataPath(/*"bin/data/" + */ "video-sources/"+directory);		// ffs but seem to need this on tohers
		//String path = Paths.get("bin/").toAbsolutePath().toString() + "/data/image-sources/" + directory;
		//String path = Paths.get("").toAbsolutePath().toString() + "/data/image-sources/" + directory; // applet mode doesnt need bin
		File folder = new File(path);
		println(this + "#loadDirectory() got path " + path);
		int count = 0;
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				// skip; maybe recurse tho in future
			} else {
				String fn = fileEntry.getName();
				if (fn.contains(".ogg") || fn.contains(".ogv") 
						//|| fn.contains(".mov") || fn.contains(".mp4")
						) {
					videos.add(path + "/" + fileEntry.getName());
					this.println("adding .ogg video " + fileEntry.getName());
				} else {
					this.println("skipping file " + fileEntry.getName());
				}
				//if (count>=numBlobs) break;
			}
		}
		this.getParameter("current_video_index").setMax(new Integer(videos.size()));
	}

	synchronized public void updateParameterValue(String paramname, Object value) {
		super.updateParameterValue(paramname, value);

	  if (paramname.equals("current_video_index")) {
	  	if (stream!=null && !stream.filename.equals(videos.get((int)this.getParameterValue("current_video_index")%videos.size())))
	  		this.changeVideo(videos.get((int)this.getParameterValue("current_video_index")%videos.size()));
	  }
	}	

	public void setParameterDefaults () {
		super.setParameterDefaults();
		//println("setting defaults");
		//this.changeParameterValue("Opacity", 1.0);
		//this.changeParameterValue("BlendMode", 4);
		/*addParameter("camera_number", new Integer(-1), 0, Capture.list().length);
	    getParameter("camera_number").setOptions(Capture.list());
	    addParameter("svga", new Boolean(false));*/
		addParameter("speed", new Float(-1.0f), new Float(-4.0f), new Float(4.0f));
		addParameter("position", new Float(0.0f), new Float(0.0f), new Float(1.0f));
		addParameter("pause", new Boolean(false)); //new Float(0.0f), new Float(0.0f), new Float(1.0f));
		addParameter("loop", new Boolean(false)); //new Float(0.0f), new Float(0.0f), new Float(1.0f));
		addParameter("next",  new Boolean(false));
		
    this.addParameter("current_video_index", new Integer(0), 0, this.videos.size()>0?this.videos.size()-1:0);

		
	}

	public boolean initialise() {
		super.initialise();

		/*try {
      quicktime.QTSession.open();
    } catch (quicktime.QTException qte) {
      qte.printStackTrace();
    }*/

		/*GLTextureParameters params = new GLTextureParameters();
    params.wrappingU = GLTextureParameters.REPEAT;
    params.wrappingV = GLTextureParameters.REPEAT;*/
		tex = this.sc.host.createCanvas(this.getPath()+"/ca/", this.getFilterLabel()).getSurf(); //new GLTexture(APP.getApp(),sc.w,sc.h, params);

		loadDirectory();
		if (videos.size()==0) return true;

		filename = videos.get((int) this.getParameterValue("current_video_index"));

		this.setFilterLabel("VideoPlayer - " + filename);

		println("Loading video " + filename);
		try {
			stream = new Movie(APP.getApp(),filename);
			movie_cache.put(filename, stream);
			//stream = new GSMovie(APP,"U:\\videos\\Tomorrows World - sinclair c5\\tworld84.dv.ff.avi");
			//stream.setPixelDest(out.getTexture());
			//stream.setPixelDest(tex, true);
			//stream.volume(volume);

			if (this.startDelay>0)
				Thread.sleep(startDelay);

			//if (!((VurfEclipse)APP.getApp()).exportMode)
			stream.loop();
		} catch (Exception e) {
			println("got error " + e + " loading " + filename);
		}      //webcamStream = new Capture(APP, sc.w, sc.h, 30);
		//webcamStream.start();

		return true;
	}

	public synchronized boolean  applyMeatToBuffers() {
		/*if (((VurfEclipse)APP.getApp()).exportMode) {
      //seek to the correct position based on the current frame number ..
      println("jumping to frameCount " + ((VurfEclipse)APP.getApp()).frameCount);
      //stream.jump(frameCount * (global_fps/stream.getSourceFrameRate()));
      //stream.jump(timeMillis * (global_fps/stream.getSourceFrameRate()));
      stream.volume(volume);
      stream.play();
      stream.jump((float)((VurfEclipse)APP.getApp()).timeMillis/1000);
      stream.pause();
    }*/
		/*    if (changing && newStream!=null) {
      GSMovie oldStream = stream;
      //GLTexture oldTex = tex;
      stream = newStream;
      System.out.println("Swapping streams");
      //tex = newTex;
      oldStream.delete();
      //oldTex.delete();
      newStream = null;
      //newTex = null;
      changing = false;
      this.setFilterLabel("VideoPlayer - " + filename);
    }*/
		if (stream!=null ) {
			this.setStreamSettings(stream);


			//}

			if (false||
					//!stream.isSeeking() && 
					stream.available()) {
				//stream.volume(volume);

				stream.read();
				//println("got gsvideo stream read");
				//return false;


			} else {	// not available
				//println("not available - starting?");
				//stream.read();
				//stream.play();
			}
			out().beginDraw();
			//if ((int)((VurfEclipse)APP.getApp()).random(100)<20)  println("VideoPlayer>>>video writing to " + out);
			//out().image(tex,0,0,sc.w,sc.h);
			out().pushMatrix();
			out().imageMode(APP.getApp().CORNERS);
			out().image(stream.get(),0,0,sc.w,sc.h);
			out().popMatrix();
			out().endDraw();
			//if (!stream.isLoaded()) return false;

			return true;


		} else {
			//println("Stream is null!");
		}
		return false; // no new frame available to draw
	}


	private synchronized void setStreamSettings(Movie stream2) {
		if ((Boolean)this.getParameterValue("loop")) {
			this.loop(stream2);
		} else {
			stream2.noLoop();
		}
		if ((Boolean)this.getParameterValue("pause")) {
			this.pause(stream2);
		} else {
			if (stream2.time()>0.0f && stream2.time()<stream2.duration())
				this.play(stream2);
		}
		/*if ((Float)this.getParameterValue("speed")!=last_speed) {
	  	stream.speed(Math.abs((Float)this.getParameterValue("speed")));
	  	last_speed = (Float)this.getParameterValue("speed");
		}*/
	}

	public void beginDraw () {
		/*if (!pixelMode)
      out.loadPixels();
    else*/
		//out.beginDraw();
	}
	public void endDraw () {
		/*if (!pixelMode)
      out.updatePixels();
    else {*/
		//out.endDraw();
		//out.loadPixels();
		//out.loadTexture();
		//}
	}

	public void setStartDelay(int i) {
		this.startDelay = i;
	}
	
	@Override
	public HashMap<String, Object> collectFilterSetup() {
		HashMap<String, Object> params = super.collectFilterSetup();
		
		params.put("directory", this.directory);
		params.put("filename", this.filename);
		return params;
	}
	
	@Override
	public VideoPlayer readSnapshot(Map<String, Object> input) {
		super.readSnapshot(input);	// TODO: a conundum; if we don't do this on first load then a bunch of stuff doesn't get set up properly; if we do do this then ImageListDrawer doesn't appear to output to the correct canvas..?
		if (input.containsKey("directory")) {
			this.directory = (String) input.get("directory");
			this.filename = (String) input.get("directory");
			this.loadDirectory();
		}
		
		return this;
	}

  public VideoPlayer setCurrentIndex (int index) {
    this.current_video_index = index;
    this.changeParameterValue("current_video_index", index);
    return this;
  }
    
}
