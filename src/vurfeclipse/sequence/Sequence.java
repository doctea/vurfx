package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import processing.core.PApplet;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;

abstract public class Sequence implements Mutable {
	//Scene sc;
	
	Random rng = new Random();

	int startTimeMillis;
	private int lengthMillis = 2000;

	int iteration;

	ArrayList<Mutable> mutables = new ArrayList<Mutable>();


	protected Scene host;		// TODO: 2017-08-18: this todo was from a long time ago... this structure definitely needs looking at but not so sure this is a simple problem?  if host points to scene then scenes can operate at different timescales which is good... (old todo follows:---) host should be a Project rather than a Scene - its only a Scene because its first used to getScene() from a SwitcherScene ..
	public Sequence (Scene host, int sequenceLengthMillis) {
		this(sequenceLengthMillis);
		this.host = host;
	}
	/*public Sequence(Scene sc, int sequenceLengthMillis) {
		this(sequenceLengthMillis);
		this.host = sc.host;
	}*/

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
		return (float) (this.rng.nextDouble()*max);
	}
	public int random(int min, int max) {
		return min + this.rng.nextInt((max-min));
	}
	public float random(float min, float max) {
		return min + (this.rng.nextFloat()*max);
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
	//abstract public ArrayList<Mutable> getMutables();
	public ArrayList<Mutable> getMutables() {
		ArrayList<Mutable> muts = new ArrayList<Mutable>();
		if (host!=null) muts.add((Mutable) host);
		return muts;
	}


	@Override
	public void setMuted() {
		this.setMuted(false);
	}
	@Override
	public void setMuted(boolean muted) {
		// TODO Auto-generated method stub
		Iterator<Mutable> it = getMutables().iterator(); //this.mutables.iterator();
		while(it.hasNext()) {
			it.next().setMuted(muted);
		}
	}


	@Override public boolean isMuted() {	// PROBABLY BUGGY AND WONT DO WHAT YOU EXPECT
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
	public void println(String text) {		// debugPrint, printDebug -- you get the idea
		if (outputDebug) System.out.println("Q " + (text.contains((this.toString()))? text : this+": "+text));
	}



	public void start() {
		this.rng.setSeed(0);
		onStart();
		setMuted(false);
		iteration = 0;
		startTimeMillis = APP.getApp().millis();
	}

	public void stop() {
		this.setMuted(true);
		this.onStop();
	}


	public boolean readyToChange(int max_i) {
		return Math.abs(iteration)>=max_i;
	}


	public void setValuesForTime() {
		//if (lengthMillis==0) return;	// skip if this Sequence doesn't last any time //TODO: reconsider how to avoid this /zero error as some subclasses might like to set values even if the length is

		int now = APP.getApp().millis();
		double scale = ( (null!=this.host) ? this.host.getTimeScale() : 1.0d ); 

		int diff = now - startTimeMillis;
		now = (int) (((double)now) * scale);
		double pc;

		diff *= scale;
		
		//println("got diff " + diff);
		if (lengthMillis==0) {
			pc = 0.5f;
			iteration++;
		} else {
			iteration = diff/(lengthMillis);
			if ((diff)>=(lengthMillis)) 
				diff = diff % lengthMillis;	// if we've gone past one loop length, reset it

			// what percent is A diff of B lengthMillis ?

			pc = PApplet.constrain((float) ((double)(diff) / (double)lengthMillis), 0.000000001f, 0.999999999f);
			//println("adjusted diff " + diff + "length millis is " + lengthMillis + " and pc is " + pc);
		}
		//println(this + " iteration " + iteration + " | pc: " + ((int)(100*pc)) + "% (diff " + diff + "/" + lengthMillis + ", scale " + scale +")");
		setValuesForNorm(pc,iteration);
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
		return array[random(0,array.length-1)];
	}


	  public int lerpcolour (int origin, int dest, double norm) {
		  int or,og,ob,oa;
		  int dr,dg,db,da;
		  or = (int)APP.getApp().red(origin);//(origin>>24) & 0xFF;//(int)APP.getApp().red(origin);
		  og = (int)APP.getApp().green(origin);//(origin>>16) & 0xFF;//(int)APP.getApp().green(origin);
		  ob = (int)APP.getApp().blue(origin);//(origin>>8) & 0xFF;//(int)APP.getApp().blue(origin);

		  dr = (int)APP.getApp().red(dest);
		  dg = (int)APP.getApp().green(dest);
		  db = (int)APP.getApp().blue(dest);


		  int outr, outg, outb;

		  int diff = (int)((Math.max(or,dr)-Math.min(or, dr)) * norm);
		  outr = Math.min(or, dr) + diff;
		  //println("diff r is " + diff);

		  diff = (int)((Math.max(og,dg)-Math.min(og, dg)) * norm);
		  outg = Math.min(og, dg) + diff;

		  diff = (int)((Math.max(ob,db)-Math.min(ob, db)) * norm);
		  outb = Math.min(ob, db) + diff;

		  /*println("Blending between (" + or +","+og+","+ob+") and (" + dr + "," + dg + "," + db + ")");
		  println("--got (" + outr + "," + outg + "," + outb + ")");*/

		  return APP.getApp().color(outr,outg,outb);
	  }

	  public int randomColorMinimum(int minimum) {
		  int tot = 0;
		  int r=0,g=0,b=0;
		  while (tot<minimum) {
			  r = (int)random(16,255);//, (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);;
			  g = (int)random(16,255);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			  b = (int)random(16,255);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			  tot = r+g+b;
		  }

		  return APP.getApp().color(r,g,b);
	  }




}
