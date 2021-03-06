package vurfeclipse.sequence;

import java.awt.Color;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import controlP5.Button;
import controlP5.CColor;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Numberbox;
import controlP5.ScrollableList;
import controlP5.Toggle;

import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import processing.core.PApplet;
import vurfeclipse.APP;
import vurfeclipse.connectors.XMLSerializer;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequencers.SequenceSequencer;
import vurfeclipse.ui.ControlFrame;
import vurfeclipse.ui.SequenceEditor;
import vurfeclipse.filters.Filter;
import vurfeclipse.projects.ClassJsonConverter;

abstract public class Sequence implements Serializable, Mutable {
	
	static int COLOR = new Color(212,212,212).getRGB();	// gui background colour 
	
	//Scene sc;
	private boolean debug = false;
	
	public Random rng = new Random(); //1337);
	long seed = rng.nextLong();

	//int startTimeMillis;
	private int lengthMillis = 2000;

	private int iteration;
	
	public boolean enabled = true;

	transient protected ArrayList<Mutable> mutables;// = new ArrayList<Mutable>();
	protected Map<String, Map<String, Object>> scene_parameters = new HashMap<String, Map<String,Object>>();
	private ArrayList<String> mutableListToLoad;

	protected HashMap<String, Object> lastLoadedParams;

	public Object clone () {
		Sequence newSequence = Sequence.makeSequence(this.getClass().getName(), this.getHost());
		
		newSequence.loadParameters(this.collectParameters());
		
		return newSequence;
	}
	
	static public Sequence makeSequence(String classname, Scene host) {
		//System.out.println("Sequence#makeSequence(): got classname '" + classname + "'");
		try {
			Sequence seq;
			try {
				Class<?> clazz = Class.forName(classname); //Class.forName(classname); host.getClass();//
			} catch (ClassNotFoundException e) {
				// assume its an inner class and that the last '.' should be turned into a '$' so that it can be loaded
				classname = classname.substring(0, classname.lastIndexOf('.')) + "$" + classname.substring(classname.lastIndexOf('.')+1,classname.length()); 
			}
			if (classname.contains("$")) {
				String[] spl = classname.split("\\$");
				Class<?> clazz = Class.forName(spl[0]); //Class.forName(classname); host.getClass();//
				Class<?> inner = Class.forName(classname);
				//System.out.println (clazz.getConstructors());
				//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
				Constructor<?> ctor = inner.getConstructor(clazz); //Scene.class,Integer.TYPE);
				//Object seq = ctor.newInstance(); //(Scene)null, 0);
				ctor.setAccessible(true);
				seq = (Sequence) ctor.newInstance(host); //(Scene)null, (int)0);				
			} else {
				Class<?> clazz = Class.forName(classname);
				//System.out.println (clazz.getConstructors());
				//Constructor<?> ctor = clazz.getConstructors()[0]; //[0]; //Scene.class, Integer.class);
				Constructor<?> ctor = clazz.getConstructor(); //Scene.class,Integer.TYPE);
				//Object seq = ctor.newInstance(); //(Scene)null, 0);
				seq = (Sequence) ctor.newInstance(); //(Scene)null, (int)0);
			}
			seq.setHost(host);
			return (Sequence) seq;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Sequence#makeSequence caught " + e + ": Didn't manage to instantiate " + classname + " might be missing constructor?");
			e.printStackTrace();
		}
		return null;
	}
	
	public Map<String, Object> collectParameters() {
		HashMap<String,Object> params = new HashMap<String,Object> ();
		params.put("class", this.getClass().getCanonicalName());
		if (this.getHost()!=null) params.put("hostPath", this.getHost().getPath());
		params.put("seed", this.getSeed());
		params.put("lengthMillis", this.lengthMillis);
		
		// actually, what we want to do here is only collect scene parameters from the host.host if this is the currently active sequence, otherwise it means nothing
		// instead, need to save the local scene_parameters if they exist
		// if it is active sequence then update the scene_parameters with the host.host's collectSceneParameters, though
		// 2018-09-29 - disable saving scene's canvas information in the sequence
		Map<String, Map<String, Object>> a = this.getSceneParameters();
		if (a!=null) a.remove(this.getHost().getPath() + "/canvases");
		params.put("scene_parameters", a);
				
		params.put("enabled", this.isEnabled());
		
		//params.put("current_sequence_name", APP.getApp().dateStamp());
		
		ArrayList<String> mutableUrls = new ArrayList<String> ();
		if (getMutables()!=null) for (Mutable m : getMutables()) {
			if (m instanceof Scene) {
				mutableUrls.add(((Scene) m).getPath());
			} else if (m instanceof Filter) {
				mutableUrls.add(((Filter)m).getPath());
			} else {
				println("Unhandled Mutable type " + m.getClass());
			}
		}
		params.put("mutableUrls", mutableUrls);
		
		return params;
	}
	
	public void loadParameters(Map<String, Object> input) {
		if (input.containsKey("seed")) this.seed = (long) Double.parseDouble((String)input.get("seed").toString());
		if (input.containsKey("lengthMillis")) this.setLengthMillis((int) Double.parseDouble((input.get("lengthMillis").toString()))); //Integer.parseInt((String)params.get("lengthMillis"));
		if (input.containsKey("scene_parameters")) this.scene_parameters = (Map<String, Map<String, Object>>) input.get("scene_parameters");
		if (input.containsKey("mutableUrls")) {
			this.mutableListToLoad = (ArrayList<String>)input.get("mutableUrls");
		}
		if (input.containsKey("enabled")) {
			this.setEnabled((Boolean) input.get("enabled"));
		}
		
		//this.lastLoadedParams = (HashMap<String, Object>) params.clone();
		//this.lastLoadedParams.remove("scene_parameters");
	}

	private Scene host;		// TODO: 2017-08-18: this todo was from a long time ago... this structure definitely needs looking at but not so sure this is a simple problem?  if host points to scene then scenes can operate at different timescales which is good... (old todo follows:---) host should be a Project rather than a Scene - its only a Scene because its first used to getScene() from a SwitcherScene ..
	private boolean disableHostMute;

	public Sequence (Scene host, int sequenceLengthMillis) {
		this(sequenceLengthMillis);
		this.setHost(host);
		this.setup();
	}
	/*public Sequence(Scene sc, int sequenceLengthMillis) {
		this(sequenceLengthMillis);
		this.host = sc.host;
	}*/
	
	public void setup() {
		//
	}

	public Sequence() {
		lengthMillis = 0;
	}
	public Sequence(int sequenceLengthMillis) {
		lengthMillis = sequenceLengthMillis;
	}

	public void setHost(Scene host) {
		this.host = host;
	}

	public int getLengthMillis() {
		return lengthMillis;
	}
	public void setLengthMillis(int length) {
		lengthMillis = length;
	}


	public float random(float max) {
		float ret = (float) (this.rng.nextDouble()*max);
		if (debug) println("random(" + max + ") returning " + ret);
		return ret;
	}
	public float random(float min, float max) {
		float ret = min + (this.rng.nextFloat()*max);
		if (debug) println("random(" + min + "f, " + max + "f returning " + ret + "f");
		return ret;
	}
	public int random(int min, int max) {
		int random = this.rng.nextInt((1+max-min));
		int ret = min + random;
		if (debug) println("random(" + min + ","+max+ ") returning " + ret);
		return ret;
	}



	/*public Sequence addMutable(Mutable mut) {
		if (mut==null) {
			println("passed a null mut");
		}
		this.mutables.add(mut);
		return this;
	}
	public ArrayList<Mutable> getMutables() {
		return mutables;
	}*/
	public Sequence disableHostMute() {
		this.disableHostMute = true;
		return this;
	}
	
	//abstract public ArrayList<Mutable> getMutables();
	public ArrayList<Mutable> getMutables() {
		if (this.mutableListToLoad!=null) {				// if we previously saved a copy of mutables, use them now
			this.mutables = new ArrayList<Mutable>();
			for (String url : this.mutableListToLoad) {
				Mutable m = (Mutable) APP.getApp().pr.getObjectForPath(url);
				if (m!=null) { 
					if (!this.mutables.contains(m)) this.mutables.add(m);
				} else {
					println("Couldn't find a Mutable to add for " + url +"!");
				}
			}
			this.mutableListToLoad = null;	// and reset it so we don't load it again
		}
		if (this.mutables==null) {
			ArrayList<Mutable> muts = new ArrayList<Mutable>();
			if (getHost()!=null && !this.disableHostMute) muts.add((Mutable) getHost());
			this.mutables = muts;
		} 
		return this.mutables;
	}


	@Override
	public void setMuted() {
		this.setMuted(false);
	}
	@Override
	public void setMuted(boolean muted) {
		if (getMutables()==null) {
			println("null mutables for " + this);
			return;
		}
		Iterator<Mutable> it = getMutables().iterator(); //this.mutables.iterator();
		while(it.hasNext()) {
			Mutable n = it.next();
			if (n==null) {
				println ("caught null Mutable in " + this + " hosted by " + this.getHost() + "!!");
			} else {
				n.setMuted(muted);
			}
		}
	}


	@Override public boolean isMuted() {	// PROBABLY BUGGY AND WONT DO WHAT YOU EXPECT
		println("Known dodgy isMuted() called in sequence " + this);
		Iterator<Mutable> mit = getMutables().iterator();
		while(mit.hasNext()) {
			if (mit.next().isMuted()) return true;
		}
		return false;
	}
	@Override public void toggleMute() {	// PROBABLY BUGGY AND WONT DO WHAT YOU EXPECT
		this.setMuted(!this.isMuted());
	}

	boolean outputDebug = true;
	private Object[] palette;
	private float current_pc = 0.0f;

	public void println(String text) {		// debugPrint, printDebug -- you get the idea
		if (outputDebug) System.out.println("Q " + (text.contains((this.toString()))? text : this+": "+text));
	}



	public void start() {
		setMuted(false);
		setIteration(0);
		//startTimeMillis = APP.getApp().millis();
		current_pc = 0.01f;
		last = 0;

		this.restart();	// set initial parameters
	}
	
	// sets parameters to initial position but doesn't reset timer
	public void restart() {
		this.rng.setSeed(seed);
		if (this.lastLoadedParams!=null) {
			this.loadParameters(lastLoadedParams);
		} 
		
		if (this.scene_parameters!=null) {
			for (Entry<String, Map<String, Object>> e : scene_parameters.entrySet()) {
				Scene s = (Scene) this.getHost().host.getObjectForPath(e.getKey());
				if (debug) println(this + " about to set scene_parameters on " + s + "!");
				if(s==null) {
					System.err.println ("Couldn't find a targetable for key '"+e.getKey()+"'!!!");
				} else {
					s.loadParameters(e.getValue());
				}
			}
		} else {
			onStart();
		}
	}

	public void stop() {
		this.setMuted(true);
		this.onStop();
		this.setIteration(0);
	}


	public boolean readyToChange(int max_i) {
		return Math.abs(getIteration())>=max_i;
	}

	/*public double getPCForElapsed(double elapsed) {
		double pc;
		
		//println("got diff " + diff);
		if (lengthMillis==0) {
			pc = 0.5f;
			iteration++;
		} else {
			iteration = (int) ((int)elapsed/lengthMillis);
			if (((int)elapsed)>=(lengthMillis)) 
				elapsed = elapsed % lengthMillis;	// if we've gone past one loop length, reset it

			// what percent is A diff of B lengthMillis ?

			pc = PApplet.constrain((float) ((double)(elapsed) / (double)lengthMillis), 0.000000001f, 0.999999999f);
			//println("adjusted diff " + diff + "length millis is " + lengthMillis + " and pc is " + pc);
		}
		
		return pc;
	}*/
	


	@Deprecated
	private double getPCForDelta(double d) {
		double pc;
		/*if (lengthMillis==0) {
			pc = 0.5f;
			iteration++;
		} else {*/
			//iteration = (int) ((int)d/lengthMillis);
			//if (((int)d)>=(lengthMillis)) 
			//	d = d % lengthMillis;	// if we've gone past one loop length, reset it

			// what percent is A diff of B lengthMillis ?
			
			//d = d % (float)iteration;
			//d = d - (iteration * lengthMillis);

			//current_pc = 5000;
			pc = PApplet.constrain(
					current_pc + (float) ((double)(d) / 1000.0d), //(double)lengthMillis), 
					0.000000001f, 1.0f //0.999999999f
			);

			//println("adjusted diff " + diff + "length millis is " + lengthMillis + " and pc is " + pc);
		//}
				
		return pc;
	}
	
	int last = 0;
	public void setValuesForTime(int ticks) {
		//if (!this.isEnabled()) return;
		
		//if (lengthMillis==0) return;	// skip if this Sequence doesn't last any time //TODO: reconsider how to avoid this /zero error as some subclasses might like to set values even if the length is
		int now = ticks; //APP.getApp().millis();
		
		if (last==0) last = now;
		
		double delta = now - last;
		if (debug) println("got delta " + delta + " because now is " + now + " and last is " + last);
		
		last = now;
				
		double scale = ( (null!=this.getHost()) ? this.getHost().getTimeScale() : 1.0d );
		//scale /= 20.0d;
		//now = (int) (((double)now) * scale);
		/*
		double elapsed = now - startTimeMillis;
		elapsed *= scale;
		//println(this + " iteration " + iteration + " | pc: " + ((int)(100*pc)) + "% (diff " + diff + "/" + lengthMillis + ", scale " + scale +")");
		double pc = getPCForElapsed(elapsed);
		*/
		//pc *= (100.0*(Math.sin(pc)-0.5f));	// TODO: timewarping effect, for later explration when i've figured out how to refactor this... probably change setValuesForTime to utilise Streams instead of going off timemillis, and then have that link go through a 'filter'
		double pc;// = getPCForDelta(((double)delta * scale));
		
		if (lengthMillis==0) {
			println ("lengthMillis is 0 in " + this.getClass() + " for " + this + ", so setting pc to 0.5");	// avoiding division by zero and NaN
			pc = lengthMillis;
		} else {
			pc = 
			//pc = PApplet.constrain(
				current_pc + (float) ((double)(delta * scale) / lengthMillis)
				//, //(double)lengthMillis), 
				//0.000000000f, 1.0f //0.999999999f
			//)
			;
		}
		
		if (pc>1.0f) { 
			current_pc = 0.0f; //0.000001f;b
			pc = current_pc; //0.01f;
			last = 0;
			iteration++;	
		} else if (pc<0.000000f) {
			current_pc = 1.0f; //0.999999f;
			pc = current_pc; //0.999999f;
			last = 0;
			iteration--;
		}
		
		if (debug) println("so got pc " + pc);
		
		setValuesForNorm(pc, getIteration());
	}

	public void setValuesForNorm(double pc) {
		setValuesForNorm(pc, getIteration());
	}
	public void setValuesForNorm(double pc, int iteration) {
		if (enabled) {
			try {
				/*if (this.host.getPath().equals("/sc/BlobScene")) {
					println("badger");
				}*/
				if (pc>1.0f) { 
					current_pc = 0.0f; //0.000001f;
					pc = current_pc; //0.01f;
					last = 0;
					setIteration(getIteration()+1); //iteration++;
					if (debug) println("set iteration + to " + iteration);
				} else if (pc<0.000000f) {
					current_pc = 1.0f; //0.999999f;
					pc = current_pc; //0.999999f;
					last = 0;
					//iteration--;
					setIteration(getIteration()-1);
					if (debug) println("set iteration - to " + iteration);
				}			
				if (debug) println(pc + " setting with iteration " + iteration);
				__setValuesForNorm(pc, iteration);
				
			} catch (Exception e) {
				println("Caught " + e + " while trying to setValuesForNorm in " + this);
				//if (debug) 
					e.printStackTrace(System.err);
			}
			this.current_pc = (float) pc;
		}
	}
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	abstract public void __setValuesForNorm(double pc, int iteration);

	abstract public void onStart();
	abstract public void onStop();

	public Object getArrayElementForNorm(double pc, Object[] array) {
		return array[(int)(pc * (array.length-1))];
	}
	public Object getRandomArrayElement(Object[] array) {
		if (array.length==1) return array[0];
		return array[random(0,array.length-1)];	// -1 ?
	}
	
	public Color mixColors(Color color1, Color color2, double percent){
		//percent *= 10.0;
	  percent = APP.getApp().constrain((float) percent, 0f, 1f);//.abs((float) percent); // fix if percent goes out of range
	  double inverse_percent = 1.0 - percent;
	  /*int redPart = (int) ((color1.getRed()*percent) + (color2.getRed()*inverse_percent));
	  int greenPart = (int) ((color1.getGreen()*percent) + (color2.getGreen()*inverse_percent));
	  int bluePart = (int) ((color1.getBlue()*percent) + (color2.getBlue()*inverse_percent));*/
		int bluePart = (int)((color1.getBlue() + (color2.getBlue() - color1.getBlue()) * percent));
		int redPart = (int)((color1.getRed() + (color2.getRed() - color1.getRed()) * percent));
		int greenPart = (int)((color1.getGreen() + (color2.getGreen() - color1.getGreen()) * percent));
		//(int)(255 * (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * percent))

	  return new Color(redPart, greenPart, bluePart, 255); //, 255);*/
		//percent /= 100;
		/*println("got percent " + percent);
		return new Color(
				(int)(255 * (color1.getAlpha() + (color2.getAlpha() - color1.getAlpha()) * percent)),
				(int)(255 * (color1.getRed() + (color2.getRed() - color1.getRed()) * percent)),
				(int)(255 * (color1.getGreen() + (color2.getGreen() - color1.getGreen()) * percent)),
				(int)(255 * (color1.getBlue() + (color2.getBlue() - color1.getBlue()) * percent))
		);*/
	}
	
	  public int lerpcolour (int origin, int dest, double norm) {
	  	//if (true) return origin;
	  	//if (true) 
	  		return mixColors(new Color(origin), new Color(dest), norm).getRGB();
	  	/*
		  int or,og,ob,oa;
		  int dr,dg,db,da;
		  or = (int)APP.getApp().red(origin);//(origin>>24) & 0xFF;//(int)APP.getApp().red(origin);
		  og = (int)APP.getApp().green(origin);//(origin>>16) & 0xFF;//(int)APP.getApp().green(origin);
		  ob = (int)APP.getApp().blue(origin);//(origin>>8) & 0xFF;//(int)APP.getApp().blue(origin);

		  dr = (int)APP.getApp().red(dest);
		  dg = (int)APP.getApp().green(dest);
		  db = (int)APP.getApp().blue(dest);


		  int outr, outg, outb;

		  int diff; 
		  //diff = (int)((Math.max(or,dr)-Math.min(or, dr)) * norm);
		  diff = (int) ((or-dr) * norm);
		  //outr = Math.min(or,dr) + diff;
		  outr = Math.abs(or + diff);
		  println("diff r is " + diff);

		  //diff = (int)((Math.max(og,dg)-Math.min(og, dg)) * norm);
		  diff = (int) ((og-dg) * norm);
		  //outg = Math.min(og,dg) + diff;
		  outg = Math.abs(og + diff);
		  println("diff g is " + diff);

		  //diff = (int)((Math.max(ob,db)-Math.min(ob, db)) * norm);
		  diff = (int) ((ob-db) * norm);
		  //outb = Math.min(ob, db) + Math.abs(diff);
		  outb = Math.abs(ob + diff);
		  println("diff b is " + diff);

		  println("Blending between (" + or +","+og+","+ob+") and (" + dr + "," + dg + "," + db + ") @ " + norm + " ::: got (" + outr + "," + outg + "," + outb + ")");

		  return APP.getApp().color(outr,outg,outb);*/
	  }

	  public int randomColorMinimum(int minimum) {
		  /*int tot = 0;
		  int r=0,g=0,b=0;
		  while (tot<minimum) {
			  r = (int)random(16,255);//, (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);;
			  g = (int)random(16,255);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			  b = (int)random(16,255);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			  tot = r+g+b;
		  }
		  return APP.getApp().color(r,g,b);
		  */
  		//if (true) return -255 * 65546; //255 * 256 * 256;

	  	if (getHost().hasPalette()) {
	  		//return 255 * 255 * 255;
	  		return (Integer)this.getRandomArrayElement(getHost().getPalette());
	  	}	  	
		  
	  	//to get rainbow, pastel colors
		  //Random random = new Random();
		  final float hue = this.random(1.0f);
		  final float saturation = this.random(0.5f,0.9f);//1.0 for brilliant, 0.0 for dull
		  final float luminance = this.random(0.6f,0.8f); //1.0 for brighter, 0.0 for black
		  int rgb = Color.HSBtoRGB(hue, saturation, luminance); //Color.getHSBColor(hue, saturation, luminance).getRGB();
		  //int rgb = 0xFFFF;
		  //int rgb = Color.
		  return rgb;
	  }

		private boolean hasPalette() {
			// TODO Auto-generated method stub
			return this.palette!=null;
		}

		public long getSeed() {
			return seed;
		}
		public void setSeed(long seed) {
			this.seed = seed;
		}

		public Map<String, Map<String, Object>> getSceneParameters() {
			return this.scene_parameters;
		}
		public void setSceneParameters(Map<String, Map<String, Object>> map) {
			this.scene_parameters = map;
		}

		public void clearSceneParameters() {
			this.scene_parameters = null;			
		}

		public float getPositionPC() {
			return this.current_pc;
		}

		public int getPositionIteration() {
			// TODO Auto-generated method stub
			return getIteration();
		}

		public void saveSequencePreset(String filename) {
			if (!filename.endsWith(".xml")) filename += ".xml";
			//filename = filename.replace(':', '_');

			filename = APP.getApp().sketchOutputPath(filename); //+ ".xml");
			filename = filename.replace(".xml", ".json");
			filename = filename.replace(":\\", "{DRIVE}").replace(":","_").replace("{DRIVE}",":\\");
			//filename.replaceFirst("/output", "");
			println("saving sequence to file " + filename);
			
			Sequence toSave = this;//.getActiveSequence();
			Map<String, Object> output; //= new HashMap<String,Object>();
			output = toSave.collectParameters();
			/*
			 * try { XMLSerializer.write(output, actual); } catch (Exception e) { // TODO
			 * Auto-generated catch block System.err.println("Caught " + e.toString() +
			 * " trying to save sequence of class " + toSave.getClass().getSimpleName() +
			 * " to '" + filename + "'"); e.printStackTrace(); }
			 */
	
			try {
				try (Writer writer = new FileWriter(filename)) {
					GsonBuilder builder = new GsonBuilder().setPrettyPrinting(); // .create()
					builder.registerTypeAdapter(Class.class, new ClassJsonConverter());
	
					Gson gson = builder.create();
					gson.toJson(output, writer);
					writer.close();
				}
			} catch (Exception e) {
				println("CAUGHT ERROR SAVING!");
				System.err.println("Caught " + e.toString() + " trying to save to '" + filename + "'");
				e.printStackTrace();
			}
		}
		
		public SequenceEditor seq;

		synchronized public SequenceEditor makeControls(ControlFrame cf, String name) {
			try {
				ControlP5 cp5 = cf.control();
				
				//SequenceEditor seq;
				//if (cf.sequenceEditor==null) {
					//cf.sequenceEditor =
				if (seq!=null) {
					//if (true) return seq;
					seq.removeControllers();
					seq.removeListeners();
					seq.remove();
				} else				
					seq = new SequenceEditor(cp5, name);
				/*} else {
					seq = cf.sequenceEditor;
				}*/
				//SequenceEditor seq = APP.getApp().pr.getSequencer().seq
								
				Sequence self = this;
				seq.setWidth((2 * cp5.controlWindow.papplet().width/3) - 40); // 40 is fudge factor to stop nested editors overlapping with sequence history 
	
				int pos_x = 0, pos_y = 0;
				int margin_x = 30, margin_y = 15;
				
				/*cp5.addLabel(name + "_label").setValue(this.getClass().getSimpleName() + ": " + name)
					.setPosition(80,10).moveTo(seq);*/
				
				Toggle tglEnabled = cp5.addToggle(name + "_enabled")
					.changeValue(this.isEnabled()?1.0f:0.0f)/*.setLabel("enabled")*/
					.setLabel("On")
					.setPosition(pos_x, pos_y)
					.moveTo(seq)
					.addListenerFor(cp5.ACTION_BROADCAST,  new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							self.setEnabled(theEvent.getController().getValue()==1.0f); //!self.isEnabled());
						}
					})
				;
				pos_x += tglEnabled.getWidth() + margin_x;
				
				Button btnRemove = cp5.addButton(name + "_remove")
						.setLabel("[x]")
						.setPosition((cp5.controlWindow.papplet().width/3) - 20, pos_y)
						.moveTo(seq)
						.setColor(new CColor(255,0,0,0,0))
						.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
							@Override
							public void controlEvent(CallbackEvent theEvent) {
								//TODO: remove the sequence from the chain here!
								cf.queueUpdate(new Runnable() {
									@Override
									public void run() {
										System.out.println("Told to remove " + self + "!");
										((SequenceSequencer) APP.getApp().pr.getSequencer()).getGrpSequenceEditor().removeSequence(self);
										((SequenceSequencer) APP.getApp().pr.getSequencer()).getGrpSequenceEditor().refreshControls();//.removeSequence(self);										
									}
								});
							}
				});
				
	
				Numberbox nmbLength = cp5.addNumberbox(name + "_length", "length")
					.setLabel("length")
					.setRange(0.1f, 300.0f)
					.setDecimalPrecision(4)
					.setScrollSensitivity(0.1f)
					.setSensitivity(0.1f)
					.setValue((float)getLengthMillis()/1000.0f)
					.setPosition(pos_x, pos_y)
					.moveTo(seq)
					.addListenerFor(cp5.ACTION_BROADCAST, new CallbackListener() {
	
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						self.setLengthMillis((int) (theEvent.getController().getValue()*1000.0f));					
					}
				});
				pos_x += nmbLength.getWidth() + margin_x;
	
				if (getHost()!=null) { 
					//cp5.addLabel(name + "_host").setValue(host.getPath()).setPosition(80,30).moveTo(seq);
	
					ScrollableList lstTarget = cp5.addScrollableList(name + "_hostpath")
							//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
							.setLabel(""+this.getHost().getPath()) //((FormulaCallback)c).targetPath)
							.addItems(APP.getApp().pr.getSceneUrls()) //.toArray(new String[0])) //.getTargetURLs().keySet().toArray(new String[0]))
							.setPosition(pos_x, pos_y)
							.setWidth((cp5.papplet.width/6))
							.setBarHeight(20)
							.setItemHeight(15)
							.moveTo(seq)
							.onLeave(cf.close)
							.onEnter(cf.toFront)
							.close();
	
					pos_x += lstTarget.getWidth() + margin_x;
				}
				
				pos_x += (cp5.papplet.width/6) + margin_x;
				
				//seq.setHeight(30);
				seq.setBackgroundHeight(pos_y + margin_y); // + margin_y); //50);
				
				return seq;		
			} catch (Exception e) {
				System.err.println("Caught exception " + e + " during " + this.getClass() + "makeControls!");
				e.printStackTrace(System.err);
			}
			return null;
		}

		

		@Override
		public String toString() {
			try {
				return super.toString() + " " + 
						this.getHost()!=null?
								this.getHost().getPath()
								:"[no host]";
			} catch (Exception e) {
				return ("exception getting name!");
			}
		}

		abstract
		public boolean notifyRemoval(Filter newf);/* {
			// detect if passed Filter is used by this Sequence
			// if so then disable this Sequence
			return false;
		}*/

		public void preserveCurrentParameters() {
			//this.lastLoadedParams = this.collectParameters();
			//this.lastLoadedParams.remove("scene_parameters");
		}

		public int getIteration() {
			return iteration;
		}

		public void setIteration(int iteration) {
			println("setting iteration to " + iteration);
			this.iteration = iteration;
		}

		public abstract void removeSequence(Sequence self);

		public void setValuesAbsolute(double value, int iteration) {
			__setValuesAbsolute(value, iteration);
		};

		public abstract void __setValuesAbsolute(double pc, int iteration);

		public int getGuiColour() {
			// TODO Auto-generated method stub
			return new Color(212,212,212).getRGB();
		}

		public Scene getHost() {
			return host;
		}
}
