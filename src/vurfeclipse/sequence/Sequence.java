package vurfeclipse.sequence;

import java.awt.Color;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.ScrollableList;

import java.util.Map.Entry;

import processing.core.PApplet;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.connectors.XMLSerializer;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import vurfeclipse.streams.Stream;
import vurfeclipse.ui.SequenceEditor;
import vurfeclipse.filters.Filter;

abstract public class Sequence implements Serializable, Mutable {
	//Scene sc;
	
	public Random rng = new Random(); //1337);
	long seed = rng.nextLong();

	int startTimeMillis;
	private int lengthMillis = 2000;

	public int iteration;

	transient protected ArrayList<Mutable> mutables;// = new ArrayList<Mutable>();
	protected HashMap<String, HashMap<String,Object>> scene_parameters;
	private ArrayList<String> mutableListToLoad;

	public Object clone () {
		Sequence newSequence = Sequence.makeSequence(this.getClass().getName(), this.host);
		
		newSequence.loadParameters(this.collectParameters());
		
		return newSequence;
	}
	
	static public Sequence makeSequence(String classname, Scene host) {
		System.out.println("got classname " + classname);
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
			System.err.println("Caught " + e + ": Didn't manage to instantiate " + classname + " might be missing constructor?");
			e.printStackTrace();
		}
		return null;
	}
	
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = new HashMap<String,Object> ();
		params.put("class", this.getClass().getCanonicalName());
		if (this.host!=null) params.put("hostPath", this.host.getPath());
		params.put("seed", this.getSeed());
		params.put("lengthMillis", this.lengthMillis);
		
		// actually, what we want to do here is only collect scene parameters from the host.host if this is the currently active sequence, otherwise it means nothing
		// instead, need to save the local scene_parameters if they exist
		// if it is active sequence then update the scene_parameters with the host.host's collectSceneParameters, though
		params.put("scene_parameters", this.getSceneParameters());
		
		//params.put("current_sequence_name", APP.getApp().dateStamp());
		
		ArrayList<String> mutableUrls = new ArrayList<String> ();
		for (Mutable m : getMutables()) {
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
	
	public void loadParameters(HashMap<String,Object> params) {
		if (params.containsKey("seed")) this.seed = (Long) params.get("seed"); //Long.parseLong((String)params.get("seed"));
		if (params.containsKey("lengthMillis")) this.setLengthMillis((Integer) params.get("lengthMillis")); //Integer.parseInt((String)params.get("lengthMillis"));
		if (params.containsKey("scene_parameters")) this.scene_parameters = (HashMap<String, HashMap<String, Object>>) params.get("scene_parameters");
		if (params.containsKey("mutableUrls")) {
			this.mutableListToLoad = (ArrayList<String>)params.get("mutableUrls");
		}
	}

	protected Scene host;		// TODO: 2017-08-18: this todo was from a long time ago... this structure definitely needs looking at but not so sure this is a simple problem?  if host points to scene then scenes can operate at different timescales which is good... (old todo follows:---) host should be a Project rather than a Scene - its only a Scene because its first used to getScene() from a SwitcherScene ..
	private boolean disableHostMute;
	public Sequence (Scene host, int sequenceLengthMillis) {
		this(sequenceLengthMillis);
		this.host = host;
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
		println("random(" + max + ") returning " + ret);
		return ret;
	}
	public float random(float min, float max) {
		float ret = min + (this.rng.nextFloat()*max);
		println("random(" + min + "f, " + max + "f returning " + ret + "f");
		return ret;
	}
	public int random(int min, int max) {
		int random = this.rng.nextInt((1+max-min));
		int ret = min + random;
		println("random(" + min + ","+max+ ") returning " + ret);
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
			if (host!=null && !this.disableHostMute) muts.add((Mutable) host);
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
		Iterator<Mutable> it = getMutables().iterator(); //this.mutables.iterator();
		while(it.hasNext()) {
			Mutable n = it.next();
			if (n==null) {
				println ("caught null Mutable in " + this + " hosted by " + this.host + "!!");
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
	private float current_pc;

	public void println(String text) {		// debugPrint, printDebug -- you get the idea
		if (outputDebug) System.out.println("Q " + (text.contains((this.toString()))? text : this+": "+text));
	}



	public void start() {
		this.rng.setSeed(seed);

		setMuted(false);
		iteration = 0;
		startTimeMillis = APP.getApp().millis();
		
		if (this.scene_parameters!=null) {
			for (Entry<String,HashMap<String,Object>> e : scene_parameters.entrySet()) {
				Scene s = (Scene) this.host.host.getObjectForPath(e.getKey());
				println(this + " about to set scene_parameters on " + s + "!");
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
	}


	public boolean readyToChange(int max_i) {
		return Math.abs(iteration)>=max_i;
	}

	public double getPCForElapsed(double elapsed) {
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
	}
	
	public void setValuesForTime() {
		//if (lengthMillis==0) return;	// skip if this Sequence doesn't last any time //TODO: reconsider how to avoid this /zero error as some subclasses might like to set values even if the length is
		int now = APP.getApp().millis();
		double scale = ( (null!=this.host) ? this.host.getTimeScale() : 1.0d );
		scale /= 10.0d;
		//now = (int) (((double)now) * scale);
		double elapsed = now - startTimeMillis;
		
		//elapsed = elapsed * Math.sin(now);
		
		elapsed *= scale;
		
		//elapsed += Math.sin(now)*100.0;
		
		//println(this + " iteration " + iteration + " | pc: " + ((int)(100*pc)) + "% (diff " + diff + "/" + lengthMillis + ", scale " + scale +")");
		double pc = getPCForElapsed(elapsed);
		//pc *= (100.0*(Math.sin(pc)-0.5f));	// TODO: timewarping effect, for later explration when i've figured out how to refactor this... probably change setValuesForTime to utilise Streams instead of going off timemillis, and then have that link go through a 'filter'
		setValuesForNorm(pc,iteration);
		this.current_pc = (float) pc;
	}

	public void setValuesForNorm(double pc) {
		setValuesForNorm(pc,0);
	}
	abstract public void setValuesForNorm(double pc, int iteration);

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
	  	if (true) return mixColors(new Color(origin), new Color(dest), norm).getRGB();
	  	
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

		  return APP.getApp().color(outr,outg,outb);
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

	  	if (host.hasPalette()) {
	  		//return 255 * 255 * 255;
	  		return (Integer)this.getRandomArrayElement(host.getPalette());
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

		public HashMap<String, HashMap<String, Object>> getSceneParameters() {
			return this.scene_parameters;
		}
		public void setSceneParameters(HashMap<String, HashMap<String, Object>> scene_parameters) {
			this.scene_parameters = scene_parameters;
		}

		public void clearSceneParameters() {
			this.scene_parameters = null;			
		}

		public float getPositionPC() {
			return this.current_pc;
		}

		public int getPositionIteration() {
			// TODO Auto-generated method stub
			return iteration;
		}

		public void saveSequencePreset(String filename) {
			if (!filename.endsWith(".xml")) filename += ".xml";
			filename = filename.replace(':', '_');

			Sequence toSave = this;//.getActiveSequence();
			HashMap<String,Object> output; //= new HashMap<String,Object>();
			output = toSave.collectParameters();
			try {
				XMLSerializer.write(output, filename);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println("Caught " + e.toString() + " trying to save sequence of class " + toSave.getClass().getSimpleName() + " to '" + filename + "'");
				e.printStackTrace();
			}
		}

		synchronized public SequenceEditor makeControls(ControlP5 cp5, String name) {
			SequenceEditor seq = new SequenceEditor(cp5, name);
			seq.setWidth(cp5.controlWindow.papplet().width);

			cp5.addLabel(name + "_label").setValue(this.getClass().getSimpleName() + ": " + name).setPosition(80,10).moveTo(seq);
			
			if (host!=null) { 
				//cp5.addLabel(name + "_host").setValue(host.getPath()).setPosition(80,30).moveTo(seq);

				  CallbackListener toFront = new CallbackListener() {
					    public void controlEvent(CallbackEvent theEvent) {
					        theEvent.getController().bringToFront();
					        ((ScrollableList)theEvent.getController()).open();
					    }
					  };

					  CallbackListener close = new CallbackListener() {
					    public void controlEvent(CallbackEvent theEvent) {
					        ((ScrollableList)theEvent.getController()).close();
					    }
					  };
				
					ScrollableList lstTarget = cp5.addScrollableList(name + "_hostpath")
							//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
							.setLabel(this.host.getPath()) //((FormulaCallback)c).targetPath)
							.addItems(APP.getApp().pr.getSceneUrls()) //.toArray(new String[0])) //.getTargetURLs().keySet().toArray(new String[0]))
							.setPosition(80, 30)
							.setWidth((cp5.papplet.width/6))
							.setBarHeight(15)
							.setItemHeight(15)
							.moveTo(seq)
							.onLeave(close)
							.onEnter(toFront)
							.close();
					
			}
			
			cp5.addNumberbox(name + "_length", "length").setValue(getLengthMillis()).setPosition(0,10).moveTo(seq);
			
			//seq.setHeight(30);
			seq.setBackgroundHeight(50);
			
			return seq;			
		}

		public Group makeControlList(ControlP5 cp5, String name) {
			return null;
		}
		
		

		@Override
		public String toString() {
			return super.toString() + " " + this.host!=null?this.host.getPath():"[no host]";
		}

}
