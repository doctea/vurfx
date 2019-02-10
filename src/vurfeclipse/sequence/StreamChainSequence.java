package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controlP5.Button;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.ScrollableList;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.scenes.Scene;
import vurfeclipse.sequencers.SequenceSequencer;
import vurfeclipse.streams.Callback;
import vurfeclipse.streams.FormulaCallback;
import vurfeclipse.streams.ParameterCallback;
import vurfeclipse.streams.Stream;
import vurfeclipse.ui.ControlFrame;
import vurfeclipse.ui.SequenceEditor;

public class StreamChainSequence extends ChainSequence /*implements Callback*/ implements Targetable {

	private String stream_name;
	private String emitter;
	private Object last_value;
	
	private boolean emitter_activated = false;
	
	final StreamChainSequence self = this;
	
	@Override
	public void onStop() {
		deactivateEmitter();
		super.onStop();
	}
	
	private void deactivateEmitter() {
		if (eventAdapter!=null) {
			Stream s = APP.getApp().pr.getSequencer().getStream(stream_name);
			if (s!=null) {
				println("removing " + eventAdapter + " from " + s);
				s.removeEventListener(eventAdapter);
			}
		}
		this.emitter_activated = false;
	}

	@Override
	public void onStart() {
		//APP.getApp().pr.getSequencer().getStream(stream_name).removeEventListener(eventAdapter);
		activateEmitter(stream_name, emitter);
		super.onStart();
	}
	
	// eventAdapter is registered as an event listener with Stream, so picks up events and passes them to here
	// call alreayd 
	FormulaCallback eventAdapter = (FormulaCallback) new FormulaCallback() { 
			@Override
			public void call(Object value) {
				setExpressionVariable("pc", self.getPositionPC());
				call(value,self);
			}
	}.setTemporary(true);

	public StreamChainSequence() {
		// TODO Auto-generated constructor stub
	}

	public StreamChainSequence(Scene host, int lengthMillis) {
		super(host, lengthMillis);
		// TODO Auto-generated constructor stub
	}

	public StreamChainSequence(int lengthMillis) {
		super(lengthMillis);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	synchronized public Map<String,Object> collectParameters() {
		Map<String,Object> params = super.collectParameters();
		
		params.put("stream", this.stream_name);
		params.put("emitter", this.emitter);
		
		params.put("callback", this.eventAdapter.collectParameters());

		return params;
	}
	
	@Override
	synchronized public void loadParameters(Map<String,Object> params) {
		super.loadParameters(params);
		
		this.stream_name = (String) params.get("stream");
		this.emitter = (String) params.get("emitter");
		
		this.eventAdapter.readParameters((Map<String, Object>) params.get("callback"));
		
	}
	
	@Override
	protected void makeControls_Add(String tabName, ControlFrame cf, SequenceEditor sequenceEditor) {
		super.makeControls_Add(tabName, cf, sequenceEditor);
		position_y += 25;
		this.makeControls_Stream(tabName, cf, sequenceEditor);
		position_y += 25;
	}
	
	private void makeControls_Stream(String tabName, ControlFrame cf, SequenceEditor sequenceEditor) {
		
		int start_x = 0; //0; //200; 
		int margin_y = 25, margin=5;
		
		
		String[] streams = APP.getApp().pr.getAvailableStreams();

		ScrollableList lstStreamSelector = new ScrollableList(cf.control(), tabName + "_stream_selector")
				//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
				.setLabel("[stream]") //((FormulaCallback)c).targetPath)
				.moveTo(sequenceEditor)
				//.addItems(APP.getApp().pr.getSceneUrls()) //.toArray(new String[0])) //.getTargetURLs().keySet().toArray(new String[0]))
				.addItems((String[]) streams)
				.setPosition(start_x, position_y)
				.setWidth(margin * 20)
				.setBarHeight(15)
				.setItemHeight(15)
				.setHeight(5 * 15)
				.onLeave(cf.close)
				.onEnter(cf.toFront)
				.close();
		
		//println("row is " + row);
		
		ChainSequence self = this;
		
		Button btnSelectStream = new Button(cf.control(), tabName + "_select_stream_button")
				.setLabel("select")
				.moveTo(sequenceEditor)
				//.addItems(APP.getApp().pr.getSceneUrls()) //.toArray(new String[0])) //.getTargetURLs().keySet().toArray(new String[0]))
				.setPosition(lstStreamSelector.getWidth() + margin + lstStreamSelector.getPosition()[0], position_y)
				.setWidth(margin * 4).setHeight(15);
			// add callback below after lstEmitterSelector has been initialised
		
		start_x += btnSelectStream.getPosition()[1] + btnSelectStream.getWidth() + (margin_y * 5); 
		
		Stream selected;
		String emitters[] = new String[0]; 
		if (this.stream_name!=null) {
			selected = APP.getApp().pr.getSequencer().getStream(stream_name);
			if (selected!=null) {
				emitters = selected.getEmitterNames();
			}
		}
		
		ScrollableList lstEmitterSelector = new ScrollableList(cf.control(), tabName + "_emitter_selector")
				//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
				.setLabel("[emitter]") //((FormulaCallback)c).targetPath)
				.moveTo(sequenceEditor)
				//.addItems(APP.getApp().pr.getSceneUrls()) //.toArray(new String[0])) //.getTargetURLs().keySet().toArray(new String[0]))
				.addItems((String[]) emitters)
				.setPosition(start_x, position_y)
				.setWidth(margin * 30)
				.setBarHeight(15)
				.setItemHeight(15)
				.setHeight(5 * 15)
				.onLeave(cf.close)
				.onEnter(cf.toFront)
				.close();
		
		//println("row is " + row);
			
		Button btnSelectEmitter = new Button(cf.control(), tabName + "_select_emitter_button")
				.setLabel("select")
				.moveTo(sequenceEditor)
				//.addItems(APP.getApp().pr.getSceneUrls()) //.toArray(new String[0])) //.getTargetURLs().keySet().toArray(new String[0]))
				.setPosition(lstEmitterSelector.getPosition()[0] + lstEmitterSelector.getWidth() + margin, position_y)
				.setWidth(margin * 4).setHeight(15)			
				.addListenerFor(cf.control().ACTION_BROADCAST, new CallbackListener() {
					@Override
					public void controlEvent(CallbackEvent theEvent) {
						int index = (int) lstEmitterSelector.getValue();
						String selected = (String)(
								//(ScrollableList)theEvent.getController())
								lstEmitterSelector
								.getItem(index).get("text")
								);
						
						println("selected emitter is " + selected);
						
						// switch to using that emitter's stream here
						
						APP.getApp().pr.getSequencer().getStream(stream_name).removeEventListener(eventAdapter);
						deactivateEmitter();
						activateEmitter(stream_name,selected);						
					}
				})
				;
		
		
		btnSelectStream.addListenerFor(cf.control().ACTION_BROADCAST, new CallbackListener() {
			@Override
			public void controlEvent(CallbackEvent theEvent) {
				int index = (int) lstStreamSelector.getValue();
				String selected = (String)(
						//(ScrollableList)theEvent.getController())
						lstStreamSelector
						.getItem(index).get("text")
						);
				
				println("..selected stream is " + selected);
				
				stream_name = selected;
				Stream stream;
				String emitters[] = new String[0]; 
				if (stream_name!=null) {
					stream = APP.getApp().pr.getSequencer().getStream(stream_name);
					if (selected!=null) {
						emitters = stream.getEmitterNames();
					}
				}
				
				// update the other gui here
				lstEmitterSelector.setItems(emitters);
										

			}
		})
		;
		
		//position_y += 25;
			
		Group gFormulaControl = this.eventAdapter.makeControls(cf, tabName + "_formula");//.moveTo(cf).setPosition(400, position_y);//.setBackgroundHeight(0);
		//gFormulaControl.moveTo(cf);//.bringToFront();
		gFormulaControl.setPosition(400, position_y);
		gFormulaControl.moveTo(sequenceEditor);	// this is the magic dust that makes controls not 'locked out'
		//sequenceEditor.add(gFormulaControl);
		//.setBackgroundHeight(sequenceEditor.getBackgroundHeight() + gFormulaControl.getBackgroundHeight());
		//gFormulaControl.setBackgroundHeight(position_y + margin_y);
		
	}

	protected void activateEmitter(String stream_name, String emitter) {
		if (!emitter_activated) {
			this.stream_name = stream_name;
			this.emitter = emitter;
			
			Stream stream = APP.getApp().pr.getSequencer().getStream(stream_name);
			if (stream==null)  
				println("No stream for " + stream_name + " in activateEmitter?");
			else if (!stream.getListenerList().contains(this.eventAdapter))
			//else
				stream.registerEventListener(emitter, this.eventAdapter);
			
			this.emitter_activated = true;
		}
	}

	@Override
	public Object target(String path, Object payload) {
		//println(" targeted at path '" + path + "' with payload '" + payload +"'");
		last_value = payload;
		return this;
	}

	@Override
	public HashMap<String, Targetable> getTargetURLs() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	synchronized public boolean readyToChange(int max_i) {
		return false;
	}
	
	@Override
	synchronized public void __setValuesForNorm(double pc, int iteration) {
		//if (debug && iteration>0) 
			//println ("setvaluesfornorm with non-zero iteration " + iteration);
		for (Sequence seq : chain) {
			if (last_value!=null) {
				float d = (float) Parameter.castAs(last_value, Double.class);
				//seq.setValuesForNorm(d,iteration);
				//seq.setValuesForTime((int) d/10);//,iteration);
				seq.setValuesAbsolute(d,iteration);
			}
		}
	}

	/*
	@Override
	public boolean reactsTo(String streamSource) {
		println(this + " reactsTo ('"+ streamSource + "')?");
		return false;
	}

	@Override
	public String getStreamSource() {
		//println(this + " getStreamSourceTo ('"+ streamSource + "')?");
		return this.stream_name;
	}

	@Override
	public void __call(Object v) {
		// TODO Auto-generated method stub
		println(this + "__call with " + v + " - should pass to sub-sequences in this chain as input");
		this.last_value = v;
	}
	


	@Override
	public Callback setStreamSource(String paramName) {
		this.emitter = paramName;
		return this;		
	}

	@Override
	public boolean notifyRemoval(Targetable newf) {
		println(this + " notifyRemoval of ('"+ newf + "')?");
		return false;
	}*/

}
