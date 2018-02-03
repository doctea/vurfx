package vurfeclipse.ui;

import java.util.HashMap;
import java.util.Map.Entry;

import controlP5.Accordion;
import controlP5.ControlP5;
import controlP5.Group;
import vurfeclipse.sequencers.Sequencer;
import vurfeclipse.streams.Stream;

public class StreamEditor extends Group {
	Accordion editor;
	
	public StreamEditor(ControlP5 theControlP5, String theName) {
		super(theControlP5, theName);
		
	}

	public Accordion setupStreamEditor(ControlFrame cf, HashMap<String,Stream> streams) {
		if (editor!=null) editor.remove();
		editor = cf.control().addAccordion("streams_editor").setWidth(2 * (cf.sketchWidth()/3)).moveTo(this);

		//Scene n;
		for (Entry<String, Stream> i : streams.entrySet()) {
			//String tabName = "["+c+"] " + n.getSceneName(); //getClass();
			//ControlP5 cp5 = ((VurfEclipse)APP.getApp()).getCP5();
			//Tab tab = cp5.addTab(tabName);

			String streamName = i.getKey();
			Group g = cf.control().addGroup(streamName);
			g.setWidth(2 * (cf.sketchWidth()/3));

			//println("added tab " + tabName);
			//ControllerInterface[] controls = ((Scene)i.next()).getControls();
			//cp5.begin(10,40);
			//((Scene)n).setupControls(cf,g);//tab);
			i.getValue().setupControls(cf,g);
			//println("done setupControls for " + i.getValue());
			//cp5.end();

			editor.addItem(g);

						/*for (int n = 0 ; n < controls.length ; n++) {
			    cp5.getTab("Scene " + c).add(controls[n]).moveTo("Scene " + c);
			    //cp5.addSlider(controls[n]).moveTo("Scene " + c);
			  }*/
			//c++;
			//((Scene)i).setupControls(cp5);
		}		
		
		editor.open().setCollapseMode(Accordion.MULTI);
		
		return editor;
	}	
}
