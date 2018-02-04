package vurfeclipse.streams;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.Group;
import controlP5.ScrollableList;
import controlP5.Textfield;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.ui.ControlFrame;

public class FormulaCallback extends ParameterCallback {

	String targetPath;
	//String sourcePath;
	
	String expression;
	
	com.udojava.evalex.Expression e;
	
	public FormulaCallback() {
		e = new com.udojava.evalex.Expression(expression);
	}
	
	public FormulaCallback setExpression(String expression) {
		System.out.println(this + " setting expression to '" + expression + "'");
		this.expression = expression;
		e = new com.udojava.evalex.Expression(expression);
		
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
		params.put("expression",  this.expression);
		params.put("targetPath", this.targetPath);
		return params;
	}
	
	public void readParameters(HashMap<String, Object> input) {
		this.setTargetPath((String) input.get("targetPath"));
		this.setExpression((String) input.get("expression"));
	}

	@Override
	public void call(Object value) {
		if (value instanceof Float || value instanceof Double) {
			e.setVariable("input", BigDecimal.valueOf((float)value));
		} else if (value instanceof Integer || value instanceof Long) {
			e.setVariable("input", BigDecimal.valueOf((Integer)value));
		} 
		Targetable target = (Targetable) APP.getApp().pr.getObjectForPath(targetPath);
		//Targetable source = (Targetable) APP.getApp().pr.getObjectForPath(sourcePath);
		
		if (target==null) {
			System.err.println("Caught a null target for path " + targetPath + " in " + this + "!");
		}
		
		if (value instanceof Float || value instanceof Double) {
			target.target(targetPath, e.eval().floatValue());
		} else if (value instanceof Integer || value instanceof Long) {
			target.target(targetPath, e.eval().intValue());
		} else if (value instanceof Boolean) {
			target.target(targetPath, e.eval().floatValue()==1.0f);
		}
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+ " { expression => '"+expression+"', targetPath => '"+targetPath+"' } ";
	}


	@Override
	public Group makeControls(ControlFrame cf, String name) {
		
		Group g = super.makeControls(cf, name);
		
		int margin_x = 5, pos_y = 0;


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

		final FormulaCallback self = this;

		// set up the Expression textfield
		
		Textfield expression = cf.control().addTextfield(name + self.getStreamSource() + "_" /*+ n */+ "_Expression_" + self.toString())
				.setText(((FormulaCallback)self).getExpression())
				.moveTo(g)
				.setPosition((int) margin_x, pos_y)
				.setLabel("Expression")
				.setAutoClear(false); 
		
		CallbackListener setExpression = new CallbackListener() {
			public void controlEvent(CallbackEvent theEvent) {
				//((ScrollableList)theEvent.getController()).close();
				((FormulaCallback) self).setExpression(((Textfield)theEvent.getController()).getText());
				((Textfield)theEvent.getController()).setValueLabel(((FormulaCallback) self).getExpression());
			}
		};		
		
		expression.addListenerFor(Textfield.ACTION_BROADCAST, setExpression);

		g.add(expression);

		final FormulaCallback fc = (FormulaCallback) self; 

		
		// set up the Target dropdown
		
		ScrollableList lstTarget = cf.control().addScrollableList(name + self.getStreamSource() + "_" /* n +*/ + "_Target URL")
				//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
				.setLabel(((FormulaCallback)self).targetPath)
				.addItems(APP.getApp().pr.getTargetURLs().keySet().toArray(new String[0]))
				.moveTo(g)
				//.setPosition(margin_x * 10, pos_y)
				.setPosition(margin_x + expression.getWidth() + margin_x, pos_y)
				.setWidth((cf.sketchWidth()/3))
				.onLeave(close).onEnter(toFront)
				.setBarHeight(expression.getHeight()).setItemHeight(expression.getHeight())
				.close();

		CallbackListener setTargetListener = new CallbackListener () {
			@Override
			public void controlEvent(CallbackEvent theEvent) {
				Map<String, Object> s = ((ScrollableList) theEvent.getController()).getItem((int)lstTarget.getValue());
				//s.entrySet();
				((FormulaCallback) fc).setTargetPath((String) s.get("text"));
			}				
		};
		lstTarget.addListenerFor(ScrollableList.ACTION_BROADCAST, setTargetListener);

		g.add(lstTarget);
		g.setBarHeight(0).setLabel("").hideBar().hideArrow();
		
		// done, return group

		return g;
	}

}
