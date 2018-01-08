package vurfeclipse.sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ScrollableList;
import vurfeclipse.APP;
import vurfeclipse.filters.Filter;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import vurfeclipse.streams.FormulaCallback;
import vurfeclipse.ui.SequenceEditor;

public class ChangeParameterSequence extends Sequence {
	String filterPath, parameterName;
	Object value;
	
	public ChangeParameterSequence() { super(); }
	
	public ChangeParameterSequence(Scene host, String filterPath, String parameterName, Object value, int length) {
		//super(host,length);
		super(host,length);
		this.filterPath = filterPath;
		this.parameterName = parameterName;
		this.value = value;
	}
	
	@Override
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();
		params.put("filterPath",  filterPath);
		params.put("parameterName", parameterName);
		params.put("value", this.value);
		return params;
	}
	
	@Override
	public void loadParameters(HashMap<String,Object> params) {
		super.loadParameters(params);
		if (params.containsKey("filterPath")) this.filterPath = (String) params.get("filterPath");
		if (params.containsKey("parameterName")) this.parameterName = (String) params.get("parameterName");
		if (params.containsKey("value")) this.value = params.get("value");
	}

	@Override public ArrayList<Mutable> getMutables () {
		if (this.mutables==null) this.mutables = super.getMutables();//new ArrayList<Mutable> ();
		/*try {
			System.out.println(host.host.getObjectForPath(this.filterPath).toString());
			this.mutables.add((Mutable)host.host.getObjectForPath(this.filterPath));
			System.out.println(host.host.getObjectForPath(this.filterPath).toString());
		} catch (Exception e) {
			System.out.println("caught " + e + " for " + this.filterPath);
			System.exit(1);
		}*/
		//this.mutables.add(host);			
		return this.mutables;
	}
	
	@Override
	public void setValuesForNorm(double pc, int iteration) {
		// TODO Auto-generated method stub
		((Filter)host.host
				.getObjectForPath(filterPath))
				.changeParameterValueFromSin(parameterName, (float)Math.sin(pc));		
	}

	@Override
	public void onStart() {
		((Filter)host.host
			.getObjectForPath(filterPath))
			.changeParameterValue(parameterName, value);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		((Filter)host.host
				.getObjectForPath(filterPath))
				.changeParameterValue(parameterName, value);
	}
	
	@Override
	public SequenceEditor makeControls(ControlP5 cp5, String name) {
		// add an accordion to hold the sub-sequences and recurse
		SequenceEditor sequenceEditor = super.makeControls(cp5, name);

		int pos_y = 60, margin_x = 10;
		SequenceEditor g = sequenceEditor;

		/*ScrollableList lstParam = cp5.addScrollableList(name + "_target").setPosition(0, pos_y);
		lstParam.moveTo(g).close().setLabel("source");
		lstParam.addItems(this.getStreamParams());//addItem(i.getKey(), i.getKey())
		g.add(lstParam);*/
		
		//println ("adding gui for " + c);
		//if (c instanceof FormulaCallback) {
			//g.add(cp5.addTextfield(name + "_Expression").setText(((FormulaCallback)c).getExpression()).setPosition(margin_x * 2, pos_y).moveTo(g).setLabel("Expression"));
			
			//final FormulaCallback fc = (FormulaCallback) c; 
			
			ScrollableList lstTarget = cp5.addScrollableList(name + "_Target URL")
					//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
					.setLabel(this.getParameterPath()) //((FormulaCallback)c).targetPath)
					.addItems(APP.getApp().pr.getTargetURLs().keySet().toArray(new String[0]))
					.setPosition(80, pos_y)
					.setWidth((cp5.papplet.width/6))
					.moveTo(g)
					//.onLeave(close)
					//.onEnter(toFront)
					.close();
			
			//lstTarget.setValue(targetPath);
			
			/*lstTarget.addListenerFor(ScrollableList.ACTION_CLICK, new CallbackListener () {
				@Override
				public void controlEvent(CallbackEvent theEvent) {
					// TODO Auto-generated method stub
					Map<String, Object> s = ((ScrollableList) theEvent.getController()).getItem((int)lstTarget.getValue());
					//s.entrySet();
					((FormulaCallback) fc).setTargetPath((String) s.get("text"));
				}				
			});*/
			
			g.add(lstTarget);		
		//seq.setHeight(30);
		g.setBackgroundHeight(g.getBackgroundHeight() + 30);
			
		//sequenceEditor.setBackgroundHeight(sequenceEditor.getBackgroundHeight() + y);

		
		return sequenceEditor;
	}

	private String getParameterPath() {
		return this.filterPath + "/pa/" + this.parameterName;
	}
	
	@Override
	public String toString() {
		return super.toString() + " " + this.getParameterPath();
	}

}
