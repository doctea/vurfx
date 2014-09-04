package vurfeclipse.sequence;

import vurfeclipse.APP;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;
import vurfeclipse.scenes.SwitcherScene;

abstract public class Sequence {
	//Scene sc;
	
	int startTimeMillis;
	int lengthMillis = 2000;
	
	int iteration;
	
	protected Scene host;		// TODO: host should be a Project rather than a Scene - its only a Scene because its first used to getScene() from a SwitcherScene ..	
	public Sequence (Scene host, int sequenceLengthMillis) {
		this(sequenceLengthMillis);
		this.host = host;
	}
	
	public Sequence() {
		
	}
	public Sequence(int sequenceLengthMillis) {
		lengthMillis = sequenceLengthMillis;
	}
		
	public void start() {
		onStart();		
		iteration = 0;
		startTimeMillis = APP.getApp().millis();
	}
	
	public boolean readyToChange(int max_i) {
		return iteration>=max_i;
	}
	
	public void setLengthMillis(int length) {
		lengthMillis = length;
	}
	
	public void setValuesForTime() {
		int now = APP.getApp().millis();
		
		int diff = now - startTimeMillis;
		
		//System.out.println("got diff " + diff);
				
		iteration = diff/lengthMillis;
		if (diff>=lengthMillis) diff = diff % lengthMillis;	// if we've gone past one loop length, reset it
				
		// what percent is A diff of B lengthMillis ?
		
		double pc = APP.getApp().constrain((float) ((double)diff / (double)lengthMillis), 0.0001f, 0.9999f);
		//System.out.println("adjusted diff " + diff + "length millis is " + lengthMillis + " and pc is " + pc);		
		setValuesForNorm(pc,iteration);
	}
	
	public void setValuesForNorm(double pc) {
		setValuesForNorm(pc,0);
	}
	abstract public void setValuesForNorm(double pc, int iteration);
	
	abstract public void onStart();
	
	public Object getArrayElementForNorm(double pc, Object[] array) {
		return array[(int)(pc * (array.length-1))];
	}
	public Object getRandomArrayElement(Object[] array) {
		return array[(int)APP.getApp().random(0,array.length-1)];
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
		  //System.out.println("diff r is " + diff);
		  
		  diff = (int)((Math.max(og,dg)-Math.min(og, dg)) * norm);	  
		  outg = Math.min(og, dg) + diff;
		  
		  diff = (int)((Math.max(ob,db)-Math.min(ob, db)) * norm);
		  outb = Math.min(ob, db) + diff;
		  
		  /*System.out.println("Blending between (" + or +","+og+","+ob+") and (" + dr + "," + dg + "," + db + ")");
		  System.out.println("--got (" + outr + "," + outg + "," + outb + ")");*/
		  
		  return APP.getApp().color(outr,outg,outb);
		  
	  }
	  
	  public int randomColorMinimum(int minimum) {
		  int tot = 0;
		  int r=0,g=0,b=0;
		  while (tot<64) {
			  r = (int)APP.getApp().random(16,255);//, (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);;
			  g = (int)APP.getApp().random(16,255);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			  b = (int)APP.getApp().random(16,255);//APP.getApp().color((int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255), (int)APP.getApp().random(32,255)); //255(int) APP.getApp().random(2^32);
			  tot = r+g+b;
		  }
		  
		  return APP.getApp().color(r,g,b);
	  }	
	
}
