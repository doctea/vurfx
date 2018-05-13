package vurfeclipse.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import controlP5.Accordion;
import controlP5.Button;
import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ControllerGroup;
import controlP5.Group;
import vurfeclipse.APP;
import vurfeclipse.streams.OscStream;
import vurfeclipse.streams.Stream;

public class StreamEditor extends Group {
	Accordion editor;
	
	public StreamEditor(ControlP5 theControlP5, String theName) {
		super(theControlP5, theName);
		
	}

	synchronized public ControllerGroup setupStreamEditor(final ControlFrame cf, Map<String, Stream> map) {
		synchronized (map) {
			if (editor!=null) editor.remove();
			Group outer = new Group(cf.cp5, "streams_editor_outer").moveTo(this).hideBar();
			int pos_y = 0, margin_x = 20;
			outer.add(new Button(cf.control(),this.toString() + "_add osc").setLabel("ADD OSC")
					.setPosition(margin_x * 2, pos_y)
					.moveTo(outer)
					.addListenerFor(outer.ACTION_BROADCAST, new CallbackListener() {
						@Override
						public void controlEvent(CallbackEvent theEvent) {
							// and refresh gui
							//cf.stream
							APP.getApp().pr.getSequencer().addStream("OSC", new OscStream("Osc Stream"));
							cf.updateGuiStreamEditor();
						}
					})
				);
			pos_y += 20;
	
			editor = cf.control().addAccordion("streams_editor").setWidth(2 * (cf.sketchWidth()/3)).moveTo(outer).setBarHeight(20).setPosition(2,pos_y);
	
			//Scene n;
			for (Entry<String, Stream> i : map.entrySet()) {
				//String tabName = "["+c+"] " + n.getSceneName(); //getClass();
				//ControlP5 cp5 = ((VurfEclipse)APP.getApp()).getCP5();
				//Tab tab = cp5.addTab(tabName);
	
				String streamName = i.getKey();
				Group g = cf.control().addGroup(streamName);
				g.setWidth(2 * (cf.sketchWidth()/3));
				g.setBarHeight(20);
	
				//println("added tab " + tabName);
				//ControllerInterface[] controls = ((Scene)i.next()).getControls();
				//cp5.begin(10,40);
				//((Scene)n).setupControls(cf,g);//tab);
				i.getValue().setupControls(cf,g);
				//println("done setupControls for " + i.getValue());
				//cp5.end();
				
				//g.setBackgroundHeight(g.getBackgroundHeight()+c.getBackgroundHeight());
	
				editor.addItem(g);
	
							/*for (int n = 0 ; n < controls.length ; n++) {
				    cp5.getTab("Scene " + c).add(controls[n]).moveTo("Scene " + c);
				    //cp5.addSlider(controls[n]).moveTo("Scene " + c);
				  }*/
				//c++;
				//((Scene)i).setupControls(cp5);
			}		
			
			editor.open().setCollapseMode(Accordion.MULTI);
			
			return outer;//editor;
		}
	}	
}
