package vurfeclipse.streams;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.ScrollableList;
import controlP5.Textfield;
import controlP5.Textlabel;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.Filter;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.ui.ControlFrame;

public class FormulaCallback extends ParameterCallback {

	String targetPath;
	//String sourcePath;
	
	String expression;
	
	com.udojava.evalex.Expression e;

	public FormulaCallback() {
		e = APP.getApp().makeEvaluator(expression);
	}
	

	private int outputMode;

	
	public int getOutputMode() {
		return outputMode;
	}

	public void setOutputMode(int outputMode) {
		this.outputMode = outputMode;
	}
	
	public FormulaCallback setExpression(String expression) {
		System.out.println(this + " setting expression to '" + expression + "'");
		this.expression = expression;
		e = APP.getApp().makeEvaluator(expression);
		
		//e.setVariable("MAX", (BigDecimal) ((Parameter)APP.getApp().pr.getObjectForPath(this.targetPath)).getMax());	// doesn't work, may need to cast all the different datatypes?
		//e.setVariable("MIN", (BigDecimal) ((Parameter)APP.getApp().pr.getObjectForPath(this.targetPath)).getMin());
		
		return this;
	}
	public String getExpression () {
		return expression;
	}
	
	public FormulaCallback setTargetPath(String targetPath) {
		this.targetPath = targetPath;
		return this;
	}
	
	@Override
	public HashMap<String,Object> collectParameters () {
		HashMap<String,Object> params = super.collectParameters();
		params.put("expression", this.expression);
		params.put("targetPath", this.targetPath);
		params.put("outputMode",  this.getOutputMode());
		return params;
	}
	
	@Override
	public void readParameters(Map<String, Object> input) {
		super.readParameters(input);
		this.setTargetPath((String) input.get("targetPath"));
		this.setExpression((String) input.get("expression"));
		if (input.containsKey("outputMode")) this.setOutputMode((int)Float.parseFloat(input.get("outputMode").toString()));
	}

	int count = 0;

	private Textlabel lblInputValue;
	private Textlabel lblOutputValue;

	private ScrollableList lstOutputMode;
	
	@Override
	public void call(Object value) {
		//System.out.println("in value " + value);
		//e = APP.getApp().makeEvaluator(expression);
		
		//if (latching_value==null) latching_value = new BigDecimal(0);
		count++;
		//System.out.println ("FormuaCallback called with " + value);
		
		if (count%10==0) this.updateGuiInputValue(value.toString());
				
		if (value instanceof Float || value instanceof Double) {
			if (((Float)value).isNaN()) {
				System.out.println("caught the goblin");
			}
			e.setVariable("input", BigDecimal.valueOf(Double.parseDouble(value.toString())));
		} else if (value instanceof Integer || value instanceof Long) {
			e.setVariable("input", BigDecimal.valueOf(Integer.parseInt(value.toString()))); //.valueOf((Integer)value));
		} 
		Targetable target = (Targetable) APP.getApp().pr.getObjectForPath(targetPath);
		//Targetable source = (Targetable) APP.getApp().pr.getObjectForPath(sourcePath);
		
		if (target==null) {			
			System.err.println("Caught a null target for path " + targetPath + " in " + this + "!");
			//target = (Targetable) APP.getApp().pr.getObjectForPath(targetPath);
		}
		
		Float floatValue = e.eval().floatValue();
		//lblOutputValue.setValueLabel(floatValue.toString());
		if (count%10==0) this.updateGuiOutputValue(floatValue.toString());
		
		if (value instanceof Float || value instanceof Double ||
			value instanceof Integer || value instanceof Long
				) {
			//target.target(targetPath, floatValue);
			//System.out.println(this.getStreamSource() + " processing Float|Double " + value + ": setting " + targetPath + " to " + e.eval().floatValue());
			if (this.getOutputMode()==Parameter.OUT_ABSOLUTE) {
				target.target(targetPath, floatValue);
			} else if (this.getOutputMode()==Parameter.OUT_NORMAL) {
				target.target(targetPath.replace("/pa/", "/pn/"), floatValue);			
			} else if (this.getOutputMode()==Parameter.OUT_SIN) {
				target.target(targetPath.replace("/pa/", "/ps/"), floatValue);			
			}
		}
		/*} else if (value instanceof Integer || value instanceof Long) {
			target.target(targetPath, e.eval().intValue());
			System.out.println(this.getStreamSource() + " processing Integer|Long " + value + ": setting targetPath to " + e.eval().intValue());
		}*/ else if (value instanceof Boolean) {
			target.target(targetPath, floatValue==1.0f);
		} else {
			System.out.println(this + " call for unhandled datatype " + value.getClass() + " for " + this.targetPath);
		}
		//System.out.println("set " + value);
	}
	


	private void updateGuiOutputValue(String value) {
		if (lblOutputValue!=null) APP.getApp().getCF().queueUpdate(new Runnable() {
			@Override
			public void run() {
				if (lblOutputValue!=null)
					lblOutputValue.setValueLabel(value);
			}
		});		
	}

	private void updateGuiInputValue(String value) {
		if (lblInputValue!=null) APP.getApp().getCF().queueUpdate(new Runnable() {
			@Override
			public void run() {
				if (lblInputValue!=null)
					lblInputValue.setValueLabel(value);
			}
		});		
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+ " { expression => '"+expression+"', targetPath => '"+targetPath+"' } ";
	}

	@Override
	public Group makeControls(ControlFrame cf, String name) {
		//Group 
		g = super.makeControls(cf, name);
		
		int margin_x = 15, margin_y = 15, pos_y = 0, pos_x = 0;

		ParameterCallback self = this;

		ParameterCallback fc = (ParameterCallback) self; 
		
		// set up the Target dropdown
		SortedSet<String> keys = new TreeSet<String>(APP.getApp().pr.getTargetURLs().keySet());
		String[] targetUrls = keys.toArray(new String[0]);
		keys.clear();
		keys = null;
		//String[] targetUrls = new String[] {};
		
		ScrollableList lstTarget = cf.control().addScrollableList(name + self.getStreamSource() + "_" /* n +*/ + "_Target URL")
				//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
				.setLabel(((FormulaCallback)self).targetPath)
				.addItems(targetUrls)
				.moveTo(g)
				//.setPosition(margin_x * 10, pos_y)
				.setPosition(pos_x, pos_y)
				.setWidth((cf.sketchWidth()/3))
				.onLeave(cf.close).onEnter(cf.toFront)
				.setBarHeight(20).setItemHeight(20)
				.close();
		
		pos_x += lstTarget.getWidth() + margin_x;

		g.add(lstTarget);
		
		// set up the Expression textfield
		
		Textfield expression = cf.control().addTextfield(name + self.getStreamSource() + "_" /*+ n */+ "_Expression_" + self.toString())
				.setText(((FormulaCallback)self).getExpression())
				.moveTo(g)
				.setPosition(pos_x, pos_y)
				.setLabel("Expression")
				.setAutoClear(false); 
		
		pos_x += expression.getWidth() + margin_x;
		
		CallbackListener setExpression = new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				//((ScrollableList)theEvent.getController()).close();
				setExpression(((Textfield)theEvent.getController()).getText());
				((Textfield)theEvent.getController()).setValueLabel(((FormulaCallback) self).getExpression());
			}
		};		
		
		expression.addListenerFor(Textfield.ACTION_BROADCAST, setExpression);

		g.add(expression);

		CallbackListener setTargetListener = new CallbackListener () {
			@Override
			public void controlEvent(CallbackEvent theEvent) {
					Map<String, Object> s = ((ScrollableList) theEvent.getController()).getItem((int)lstTarget.getValue());
					//s.entrySet();
					((FormulaCallback) fc).setTargetPath((String) s.get("text"));
			}				
		};
		lstTarget.addListenerFor(ScrollableList.ACTION_BROADCAST, setTargetListener);
		
		CallbackListener pasteTargetListener = new CallbackListener () {
			@Override
			public void controlEvent(CallbackEvent theEvent) {
				if (cf.control().papplet.mouseButton==(VurfEclipse.MOUSE_RIGHT)) {		// 'paste' copied target path to this item
					((FormulaCallback) fc).setTargetPath((String) APP.getApp().pr.getSequencer().getSelectedTargetPath());
					lstTarget.setLabel(APP.getApp().pr.getSequencer().getSelectedTargetPath());
				}
			}
		};
		expression.addListenerFor(ScrollableList.ACTION_RELEASE, pasteTargetListener);
		
		g.setBarHeight(0).setLabel("").hideBar().hideArrow();
		
		lstOutputMode = cf.control().addScrollableList(name + "_output mode")
				.setLabel(Parameter.getOutputModeName(this.getOutputMode()))
				.addItems(new String[] { "abs", "0-1", "sin" })
				.setPosition(pos_x, pos_y)
				.setWidth(margin_x * 3)
				.setBarHeight(20)
				.moveTo(g)
				.onLeave(cf.close)
				.onEnter(cf.toFront)
				.close();		
		
		pos_x += lstOutputMode.getWidth() + margin_x/2;
		
		lstOutputMode.addListenerFor(ScrollableList.ACTION_BROADCAST, new CallbackListener() {
			@Override
			public void controlEvent(CallbackEvent theEvent) {
				setOutputMode((int)((ScrollableList)theEvent.getController()).getValue());	// assumes that abs, 0-1, sin enumerated to 0, 1, 2
			}
		});
		
		
		
		lblInputValue = cf.control().addLabel(name + "_input_indicator").setPosition(lstOutputMode.getPosition()[0] + margin_x + lstOutputMode.getWidth(), pos_y-5).moveTo(g);
		lblOutputValue = cf.control().addLabel(name + "_output_indicator").setPosition(lblInputValue.getPosition()[0], pos_y+5).moveTo(g);
		
		// done, return group

		return g;
	}
	
	@Override
	public boolean notifyRemoval(Targetable newf) {
		boolean relevant = super.notifyRemoval(newf);
		if (newf.getTargetURLs().containsKey(this.targetPath)) {
			this.setEnabled(false);
			return true;
		}
		return false; //relevant;
	}

}
