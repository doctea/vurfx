package vurfeclipse.scenes;

import java.util.HashMap;
import processing.core.PFont;
import vurfeclipse.APP;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.filters.Filter;
import vurfeclipse.filters.TextDrawer;
import vurfeclipse.projects.Project;
import vurfeclipse.sequence.ChangeParameterSequence;
import vurfeclipse.sequence.Sequence;
import vurfeclipse.sequence.ShowSceneSequence;
import vurfeclipse.streams.FormulaCallback;
import vurfeclipse.streams.ParameterCallback;

public class TextFlashScene extends Scene {
	//int filterCount = 2;

	//Filter[] filters;// = new Filter[filterCount];

	String values[];

	HashMap<String,PFont> fonts = new HashMap<String,PFont>();

	public TextFlashScene setTextValues(String[] values) {
		this.values = values;
		return this;
	}
	public TextFlashScene setFonts(String[] fonts) {
		for (int i = 0 ; i < fonts.length ; i++) {
			this.fonts.put(fonts[i], ((VurfEclipse)APP.getApp()).loadFont(fonts[i]));
		}
		return this;
	}
	public void changeFont(int v) {
		if (fonts.size()>0) {
			v = v % fonts.size();
			Object[] pf = fonts.values().toArray();
			((TextDrawer)getObjectForPath("TextDrawer")).setFont((PFont)pf[v]);
		}
	}

	public void randomWord() {
		String letter = values[(int)((VurfEclipse)APP.getApp()).random(0,values.length)];
		//println("got word '" + letter + "'");
		//((TextDrawer)filters[0]).setText(values[(int)random(0,values.length)]);
		//((TextDrawer)self.filters[0]).changeParameterValue("text",values[(int)APP.random(0,values.length)]);
		changeFilterParameterValue(0,"text",letter);
		//ftd.setRotation(i%360);//(int)random(i));
		//fbd.setMute(false);
	}
	public void setWordByNorm(double norm) {
		int index = (int)(values.length * norm);
		if (index>=values.length) index = values.length-1;
		if (index<0) index = 0;
		changeFilterParameterValue(0,"text",values[index]);
	}



	public void setupCallbackPresets () {
		super.setupCallbackPresets();
		final Scene self = this;
		//println("adding callback 'spin'");

		this.callbacks.put("cycle", new TextCycleCallback().setup());

		this.callbacks.put("random", new RandomWordCallback());

		//this.callbacks.put("swivel", new SwivelCallback());
		this.callbacks.put("swivel", new FormulaCallback()
				.setExpression("(input%360)-180")
				.setTargetPath(
						this.getFilter("TextDrawer")
						.getParameter("zrotation")
						.getPath())
		);

		//this.callbacks.put("rotate", new RotateCallback());
		this.callbacks.put("rotate", new FormulaCallback()
				.setExpression("input%360")
				.setTargetPath(
						this.getFilter("TextDrawer")
						.getParameter("rotation")
						.getPath())
		);

		//this.callbacks.put("toggle", new ToggleCallback(self));
		this.callbacks.put("toggle", new FormulaCallback()
				.setExpression("input%2==0")
				.setTargetPath(this.getFilter("BlendDrawer").getPath()+"/mute")
		);
	}

	public TextFlashScene(Project host, int w, int h, String[] values) {
		super(host,w,h);
		this.values = values;
	}

	public TextFlashScene(Project host, int w, int h) {
		this(host, w, h, new String[] {
				";)", ":P", "MDMA", "LSD-25", "DMT", "2c-b", "2c-c", "2c-d", "4homet", "delysid", "MDA", "303", "909", "808",

				//"take trips", "magic dust", "Socio Suki",

				"nice", "dANCe", "rob0t", "ZX", "(:", "#!", "???", "!!", "!!!", "!", "dont panic",

				"(c)", "doctea", "Crack Zombie", "vurf", "gwrx", "greetz",
				"::vurf::", "V:U:R:F", "SLiPs", "MMXIV", "MCMLXXX", "Sandoz", "cola", "acid", "acidtest", "electric", "kool-aid",
				"RAVE", "rave", "XtC", "science", "drop acid", "make tea", "art?", "peace", "free", "<3 echo", "<3", "mwilk"
		}
				);
	}

	public boolean setupFilters () {
		//super.initialise();
		//filters = new Filter[filterCount];
		//println("DemoScene initialised " + this + " - filtercount is " + filterCount);
		//int i = 0;

		
		
		addFilter(new TextDrawer(this).setFilterName("TextDrawer").setAliases("temp","out"));//setBuffers(buffers[BUF_TEMP],buffers[BUF_TEMP]);
		//final TextDrawer ftd = (TextDrawer) filters[i];

		final BlendDrawer fbd = (BlendDrawer) addFilter(new BlendDrawer(this).setFilterName("BlendDrawer").setAliases("out","temp")); //setBuffers(buffers[BUF_OUT],buffers[BUF_TEMP]);
		//final BlendDrawer fbd = (BlendDrawer) filters[i];
		fbd.setBlendMode(9);

		//filters[++i] = new ShaderFilter(this,"negateFrag.glsl").setCanvases(getCanvasMapping("out"), getCanvasMapping("temp"));//.addParameter("Opacity", new Float(0.5f), 0f, 1f);

		/*host.getStream("beat").registerEventListener("beat_8",
      new ParameterCallback () {
        public void call(Object value) {
          //ftd.setText(value.toString());
          int i = Integer.parseInt(value.toString());
          ftd.setText(values[(int)random(0,values.length)]);
          //ftd.setRotation(i%360);//(int)random(i));
          fbd.setMute(false);
        }
      }
    );*/
		return true;
	}




	public void setupSequences() {
		sequences.put("preset 1", new TextFlashSequence1(this, 0));
		sequences.put("on", 	  new ShowSceneSequence(this,1000));
	}


	public TextFlashScene addSequencesForWords(String[] words, int length) {
		for (int i = 0 ; i < words.length ; i++) {
			sequences.put("word_" /* + i + "_" */ + words[i],
					this.makeChainSequenceFrom("preset 1",
							new ChangeParameterSequence(this, getPath()+"/fl/TextDrawer", "text", words[i], length)
							));
		}
		return this;
	}

	public class ToggleCallback extends ParameterCallback {
		private final Scene self;

		public ToggleCallback(Scene self) {
			this.self = self;
		}

		public void call(Object value) {
			int i = Integer.parseInt(value.toString());
			if (i%2==0) {
				//this.toggleMute();
				//changeFilterParameterValue(
				filters.get(1).setMuted(false);
			} else {
				filters.get(1).setMuted(true);
			}
		}
	}

	public class RotateCallback extends ParameterCallback {
		//String[] values = getParameterValue("values");
		public void call(Object value) {
			int i = Integer.parseInt(value.toString());
			//((TextDrawer)filters[0]).setText(values[(int)random(0,values.length)]);
			changeFilterParameterValue(0,"rotation",(i%360)); //values[(int)random(0,values.length)]);
			//ftd.setRotation(i%360);//(int)random(i));
			//fbd.setMute(false);
		}
	}

	public class SwivelCallback extends ParameterCallback {
		//String[] values = getParameterValue("values");
		public void call(Object value) {
			int i = Integer.parseInt(value.toString());
			//((TextDrawer)filters[0]).setText(values[(int)random(0,values.length)]);
			changeFilterParameterValue(0,"zrotation",(i%360)-180); //values[(int)random(0,values.length)]);
			//ftd.setRotation(i%360);//(int)random(i));
			//fbd.setMute(false);
		}
	}

	public class RandomWordCallback extends ParameterCallback {
		//String[] values = getParameterValue("values");
		public void call(Object value) {
			int i = Integer.parseInt(value.toString());
			randomWord();
		}
	}

	public class TextCycleCallback extends ParameterCallback {
		int count = 0;
		int totalLength;
		String[] chars;
		HashMap<Integer,String> wordBreaks = new HashMap<Integer,String>();

		public ParameterCallback setup () {
			int tot = 0;
			for (int i = 0 ; i < values.length ; i++) {
				wordBreaks.put(tot,values[i]);
				println("adding boundary at " + tot + " for " + values[i]);
				tot += values[i].length();
			}
			totalLength = tot;

			chars = new String[totalLength];
			int pointer = 0;
			for (int i = 0 ; i < values.length ; i++) {
				for (int x = 0 ; x < values[i].length() ; x++) {
					chars[pointer++] = new Character(values[i].charAt(x)).toString();
					println("added " + chars[pointer-1]);
				}
			}

			return this;
		}

		public void call(Object value) {
			int step = ((Integer)value)%totalLength;
			changeFilterParameterValue(0,"text",chars[step]);
			if (step==0) { //changeFont(count++);
				//if (wordBreaks.containsKey(new Integer(step))) {
				println("found boundary at " + step);
				changeFont(count++);
			}
		}
	}

	public class TextFlashSequence1 extends Sequence {
		public TextFlashSequence1(TextFlashScene fx, int i) {
			// TODO Auto-generated constructor stub
			super(fx,i);
		}
		/*@Override
		public ArrayList<Mutable> getMutables() {
			return new ArrayList<Mutable>();
		}*/
		@Override
		public void __setValuesForNorm(double norm, int iteration) {
			//System.out.println(this+"#setValuesForNorm("+norm+","+iteration+"): BlendSequence1 " + norm);
			//if (iteration%2==0) norm = 1.0f-norm;	// go up and down again
			//host.getFilter("BlendDrawer1").changeParameterValue("Opacity", (float)norm);
		}
		@Override public void onStart() {
			((TextDrawer)(getHost().host.getSceneForPath(getPath()).getFilter("TextDrawer"))).setText("FeralFest");
			if (random(0f,1.0f)>=0.5f)
				((BlendDrawer)getHost().host.getSceneForPath(getPath()).getFilter("BlendDrawer")).setBlendMode((Integer)getRandomArrayElement(new Integer[] { 3, 4, 8, 8, 8, 9, 12 }));
			//((BlendDrawer)host.host.getSceneForPath(getPath()).getFilter("BlendDrawer")).setMuted((random(0f,1.0f)>=0.25f));
		}
		@Override public void onStop() {	}
		@Override
		public boolean notifyRemoval(Filter newf) {
				if (newf==getFilter("TextDrawer") || newf==getFilter("BlendDrawer")) {
					this.setEnabled(false);
					return true;
				}
			return false;
		}
		@Override
		public void removeSequence(Sequence self) {
			// TODO Auto-generated method stub
			
		}
	}

}
