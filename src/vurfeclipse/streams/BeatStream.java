package vurfeclipse.streams;

import java.io.Serializable;
import java.util.HashMap;

import processing.core.PApplet;
import vurfeclipse.APP;

public class BeatStream extends Stream implements Serializable { 
	private float bpm = 125.0f;

	private int tolerance = 50;

	int startTime;


	private float stepDivisions[] = {
			/*128.0, 64.0,*/32.0f, 16.0f, 8.0f, 4.0f, 3.0f, 2.0f, 1.0f,  //MAKE SURE TO CHANGE stepMarker value below if edit this line!!
			0.5f, 0.25f, 0.125f, 0.0625f, 0.03125f, 0.015625f
	};
	private int stepMarker = 7; // the number of >=1 values in above array
	int lastDealtStepTime[] = new int[stepDivisions.length]; //stepCount];
	float stepLengths[] = new float[stepDivisions.length]; //stepCount];

	int stepCounter[] = new int[stepDivisions.length];

	//int lastDealtBeatTime;

	float beatLength;

	int generatedMessages = 0;

	BeatStream(String streamName, float bpm, int timeMillis) { 
		super(streamName);
		this.bpm = bpm;

		startTime = timeMillis;//millis();
		//lastDealtBeatTime = startTime;

		setupStepLengths(bpm);

		/*
    quarterBeatLength = beatLength / 4;
    twoBeatLength = beatLength*2;*/

	}
	public BeatStream(String streamName, double d, int millis) {
		// TODO Auto-generated constructor stub
		this(streamName,(float)d,millis);
	}
	public BeatStream() {
		this.startTime = APP.getApp().millis();		
	}


	int toggleMode = 1;
	public void toggleBPM() {
		toggleMode++;
		if (toggleMode>8) 
			toggleMode = 1;

		float speed = PApplet.map(PApplet.abs(4-toggleMode),1.0f,8.0f,0.5f,2.0f)+0.25f;
		System.out.println("toggling steps for toggleMode " + toggleMode + " speed is " + speed);    
		setSpeed(speed);
	}

	public void setBPM(float bpm) {
		this.bpm = bpm;
		this.setupStepLengths(bpm);
		stepCounter = new int[stepDivisions.length];
		lastDealtStepTime = new int[stepDivisions.length];
		startTime = APP.getApp().millis();
	}    

	float speed = 1.0f;
	public void setSpeed(float speed) {
		this.speed = speed;
		//stepCounter = new int[stepDivisions.length];
		//lastDealtStepTime = new int[stepDivisions.length];    
	}


	void setupStepLengths(float bpm) {
		beatLength = (60 / bpm) * 1000;    
		for (int n = 0 ; n < stepDivisions.length ; n++) {
			stepLengths[n] =  (beatLength / stepDivisions[n]);
			lastDealtStepTime[n] = 0;//startTime; //0;
		}
		stepLengths[stepMarker] = beatLength * 2;
		stepLengths[stepMarker+1] = beatLength * 4;
		stepLengths[stepMarker+2] = beatLength * 8;
		stepLengths[stepMarker+3] = beatLength * 16;
	}    

	int skippedCount = 0;

	public void processEventMeat(int time) {
		// detect if we're near a beat.
		// time - startTime > (length of beat) +/- ws
		// (length of beat) is 60/120 ? 0.5 second * 1000 = 500 millis
		/*if (time - lastDealtBeatTime > beatLength) {
      lastDealtBeatTime = time;
      addEvent("beat", "BEAT AT " + time);
    }    */
		if (debug) {
			System.out.println("BeatStream speed is " + speed);
			System.out.println("BeatStream time is " + time);
		}

		// detect if any of the step divisions should trigger
		//System.out.println("FRAME--");
		for (int n = 0 ; n < stepDivisions.length ; n++) {
			//System.out.println("n is " + n + ", stepLength is " + stepLengths[n]);
			double gap = time - lastDealtStepTime[n];
			//System.out.println("gap is " + gap + " (for stepLength " + stepLengths[n]);
			String eventName = //stepDivisions[n]<1 ? 
					n > stepMarker ?
							"bar_" + (n-stepMarker)
							:
								("beat_" + (int)stepDivisions[n]);
							if ((double)gap*(double)speed > (double)stepLengths[n]/(double)speed) {


								/*if (skippedCount++>toggleMode) {  //////////////////////// really fucking poisonous
          skippedCount = 1;
        } else {
          return;                //////////// early return !!!!!!!!!!!
        }*/


								//stepCounter[n]++;
								float stepCount = (time / stepLengths[n]);
								stepCounter[n] = (int)stepCount;

								//System.out.println("Triggering " + eventName);
								//System.out.println("triggering for " + n + " stepDivisions[n] beat_" + stepDivisions[n]);
								//lastDealtStepTime[n] = time;
								lastDealtStepTime[n] = (int)(stepCounter[n]*stepLengths[n]);
								//System.out.println("beat_" + stepDivisions[n]);
								//if (gap < tolerance) { // dont trigger too old events if htey're outside tolerance
								//if (usedEvents.contains(eventName)) {
								clearEvents(eventName);  // old beats aren't interesting
								//addEvent(eventName, stepCounter[n] + "th " + eventName + " at " + time);
								addEvent(eventName, stepCounter[n]/2);// + "th " + eventName + " at " + time);
								//addEvent((stepDivisions[n]<1?"bar":"beat"), eventName + "@" + time);
								addEvent((n>stepMarker?"bar":"beat"), stepCounter[n] + "th " + eventName + "@" + time);

								generatedMessages++;
								//}
								//}
							}
		}
		//System.out.println("FRAME END--");

		//int timeTaken = (millis() - startTime) / 1000;
		//System.out.println(generatedMessages + " messages in " + timeTaken + "("+ generatedMessages/timeTaken + "mps)");

	} 


	
	@Override 
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();
		params.put("tempo", this.bpm);
		return params;
	}
	
	@Override
	public void readParameters(HashMap<String, Object> input) {
		super.readParameters(input);
		this.setBPM((float) input.get("tempo"));
		
		/*HashMap<String, HashMap<String,Object>> callbacks = (HashMap<String, HashMap<String,Object>>) input.get("callbacks");
		for (Entry<String, HashMap<String, Object>> i : callbacks.entrySet()) {
			this.registerEventListener(paramName, ParameterCallback.createParameterCallback(i.getValue().get("class")));
		}*/
		//callbacks = input.
	}

	@Override
	public String[] getEmitterNames() {	
		return new String[] {
				"beat_1","beat_2", "beat_3", "beat_4", "beat_5", "beat_6", "beat_7", "beat_8", "beat_9", "beat_10", "beat_11", "beat_12", "beat_13", "beat_14", "beat_15", "beat_16",
				"bar_1","bar_2", "bar_3", "bar_4", "bar_5", "bar_6", "bar_7", "bar_8", "bar_9", "bar_10", "bar_11", "bar_12", "bar_13", "bar_14", "bar_15", "bar_16",
		};
	}
	
}
