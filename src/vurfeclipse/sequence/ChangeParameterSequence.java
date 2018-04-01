package vurfeclipse.sequence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.udojava.evalex.Expression.Function;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ScrollableList;
import controlP5.Textfield;
import controlP5.Textlabel;
import vurfeclipse.APP;
import vurfeclipse.Targetable;
import vurfeclipse.VurfEclipse;
import vurfeclipse.filters.Filter;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.parameters.ParameterBuffer;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import vurfeclipse.streams.FormulaCallback;
import vurfeclipse.ui.ControlFrame;
import vurfeclipse.ui.SequenceEditor;

import com.udojava.evalex.AbstractFunction;
import com.udojava.evalex.Expression.*;

public class ChangeParameterSequence extends Sequence {
	
	com.udojava.evalex.Expression evaluator;
	
	//String filterPath, parameterName;
	String expression = "input";
	String targetPath;

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	Object value;
	
	ParameterBuffer paramBuffer;

	private int outputMode;

	
	public int getOutputMode() {
		return outputMode;
	}

	public ChangeParameterSequence setOutputMode(int outputMode) {
		this.outputMode = outputMode;
		return this;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
		if (this.evaluator!=null) this.evaluator = APP.getApp().makeEvaluator(expression);
		//e = new com.udojava.evalex.Expression(expression);
	}

	public ChangeParameterSequence() { 
		super(); 
		this.paramBuffer = new ParameterBuffer(10,50);
	}
	
	public ChangeParameterSequence(Scene host, String filterPath, String parameterName, Object startValue, String expression, int length) {
		//super(host,length);
		super(host,length);
		//this.filterPath = filterPath;
		//this.parameterName = parameterName;
		this.targetPath = filterPath + "/pa/" + parameterName;
		this.value = value;
		this.setExpression(expression);
		
		this.paramBuffer = new ParameterBuffer(10,50);
		
	}
	public ChangeParameterSequence(Scene host, String filterPath, String parameterName, Object value, int length) {	// compatibility constructor
		//super(host,length);
		this(host,filterPath,parameterName,value,"input",length); 
	}
	
	@Override
	public HashMap<String,Object> collectParameters() {
		HashMap<String,Object> params = super.collectParameters();
		//params.put("filterPath",  filterPath);
		//params.put("parameterName", parameterName);
		params.put("targetPath", getTargetPath());
		params.put("value", this.value);
		params.put("expression", this.expression);
		params.put("outputMode", this.getOutputMode());
		return params;
	}
	
	@Override
	public void loadParameters(HashMap<String,Object> params) {
		super.loadParameters(params);
		
		//compatibility
		if (params.containsKey("filterPath"))	{	// for old projects
			this.targetPath = (String) params.get("filterPath") + "/pa/" + (String) params.get("parameterName");
		} else if (params.containsKey("targetPath")) {
			this.targetPath = (String) params.get("targetPath");
		}
		if (params.containsKey("value")) 		this.value = params.get("value");
		if (params.containsKey("expression")) 	this.setExpression((String) params.get("expression"));
		if (params.containsKey("outputMode")) 	this.setOutputMode((Integer) params.get("outputMode"));
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
	public void start() {
		if (host.host.getObjectForPath(targetPath) instanceof Parameter) {
			this.paramBuffer.setCircular((((Parameter)host.host.getObjectForPath(targetPath)).isCircular()));
		}
	}
	
	@Override
	synchronized public void __setValuesForNorm(double pc, int iteration) {
				
		if (((Double)pc).isNaN()) {
			println("Caught NaN in __setValuesForNorm ");
		}
		// evaluate value to pass based on expression
		if (evaluator==null) evaluator = APP.getApp().makeEvaluator(expression);
		
		evaluator.setVariable("input", BigDecimal.valueOf(pc));
		evaluator.setVariable("iteration", BigDecimal.valueOf(iteration));
		BigDecimal value = evaluator.eval();
		if (value==null)
			println("caught null returned from eval(input = " + pc + ", iteration = " + iteration + ")");
		
		this.updateGuiInputValue("iter# " + iteration + " | pc: " + (float)(pc)); //Float.parseFloat(pc));
		
		//println(this + ": got value " + value);
		
		if (paramBuffer!=null) value = (BigDecimal) paramBuffer.getValue(value, false);	// lerp
		
		this.updateGuiOutputValue(value.toString());
		
		/*((Filter)host.host
				.getObjectForPath(filterPath))
				//.changeParameterValueFromSin(parameterName, (float)value.doubleValue());//(float)Math.sin(value.doubleValue()));		
				.changeParameterValue(parameterName, (float)value.doubleValue());*/
		Targetable t = (Targetable) host.host.getObjectForPath(targetPath);
		if (this.getOutputMode()==Parameter.OUT_ABSOLUTE) {
			t.target(targetPath, value.floatValue());
		} else if (this.getOutputMode()==Parameter.OUT_NORMAL) {
			t.target(targetPath.replace("/pa/", "/pn/"), value.floatValue());			
		} else if (this.getOutputMode()==Parameter.OUT_SIN) {
			t.target(targetPath.replace("/pa/", "/ps/"), value.floatValue());			
		}
		
	}

	private void updateGuiOutputValue(final String value) {
		/*APP.getApp().getCF().queueUpdate(new Runnable() {
			@Override
			public void run() {*/
				if (lblOutputValue!=null)
					lblOutputValue.setValueLabel(value);
			/*}
		});		*/
	}

	private void updateGuiInputValue(final String value) {
		/*APP.getApp().getCF().queueUpdate(new Runnable() {
			@Override
			public void run() {*/
				if (lblInputValue!=null)
					lblInputValue.setValueLabel(value);
			/*}
		});		*/
	}

	@Override
	public void onStart() {
		if (value!=null)
			((Targetable)host.host.getObjectForPath(targetPath)).target(targetPath, value);
/*		((Filter)host.host
			.getObjectForPath(filterPath))
			.changeParameterValue(parameterName, value);*/
	}

	@Override
	public void onStop() {
		try {
			if (value!=null)
				((Targetable)host.host.getObjectForPath(targetPath)).target(targetPath, value);

				/*((Filter)host.host
					.getObjectForPath(filterPath))
					.changeParameterValue(parameterName, value);*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Textlabel lblInputValue;
	private Textlabel lblOutputValue;
	private ScrollableList lstOutputMode;
	
	@Override
	public SequenceEditor makeControls(ControlFrame cf, String name) {
		// add an accordion to hold the sub-sequences and recurse
		SequenceEditor sequenceEditor = super.makeControls(cf, name);
		
		final ChangeParameterSequence sequence = this;

		int pos_x = 0, pos_y = 40, margin_x = 10, margin_y = 15;
		SequenceEditor g = sequenceEditor;

		ControlP5 cp5 = cf.control();
		/*ScrollableList lstParam = cp5.addScrollableList(name + "_target").setPosition(0, pos_y);
		lstParam.moveTo(g).close().setLabel("source");
		lstParam.addItems(this.getStreamParams());//addItem(i.getKey(), i.getKey())
		g.add(lstParam);*/
		
		//println ("adding gui for " + c);
		//if (c instanceof FormulaCallback) {
			Textfield txtExpression = cp5.addTextfield(name + "_Expression")
					.setText(this.getExpression())
					.setPosition(pos_x, pos_y)
					.moveTo(g).setLabel("Expression")
					.setAutoClear(false);
			
			pos_x += txtExpression.getWidth() + margin_x;
			
			// TODO: add callback handler to actually set expression

			CallbackListener setExpression = new CallbackListener() {
				public void controlEvent(CallbackEvent theEvent) {
					//((ScrollableList)theEvent.getController()).close();
					println("Setting new expression: " + ((Textfield)theEvent.getController()).getText());
					setExpression(((Textfield)theEvent.getController()).getText());
					((Textfield)theEvent.getController()).setValueLabel(sequence.getExpression());
				}
			};
			txtExpression.addListenerFor(txtExpression.ACTION_BROADCAST,setExpression);
			
			g.add(txtExpression);
			
			
			//tglOutputMode = cp5.addToggle(name + "_outputmode_toggle").setPosition(pos_x, pos_y).setWidth(margin_x).moveTo(g).setValue(this.getOutputMode())
			lstOutputMode = cp5.addScrollableList(name + "_output mode")
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
			
			//final FormulaCallback fc = (FormulaCallback) c; 
				
			final ScrollableList lstTarget = cp5.addScrollableList(name + "_Target URL")
					//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
					.setLabel(this.getTargetPath()) //((FormulaCallback)c).targetPath)
					.addItems(APP.getApp().pr.getTargetURLs().keySet().toArray(new String[0]))
					.setPosition(pos_x, pos_y)
					.setWidth((cp5.papplet.width/3))
					.setBarHeight(20).setItemHeight(16)
					.moveTo(g)
					.onLeave(cf.close)
					.onEnter(cf.toFront)
					.close();
			
			pos_x += lstTarget.getWidth() + margin_x/2;
			
			
			//lstTarget.setValue(targetPath);
			
			lstTarget.addListenerFor(ScrollableList.ACTION_BROADCAST, new CallbackListener () {
				@Override
				synchronized public void controlEvent(CallbackEvent theEvent) {
					// TODO Auto-generated method stub
					Map<String, Object> s = ((ScrollableList) theEvent.getController()).getItem((int)lstTarget.getValue());
					//s.entrySet();
					//TODO: might not be a Parameter,could just be a Targetable
					Parameter p = (Parameter) host.host.getObjectForPath((String)s.get("text"));
					synchronized(sequence) {
						sequence.setHost(((Filter)host.host.getObjectForPath(p.getFilterPath())).sc);
						sequence.setTargetPath(p.getPath());
						//sequence.filterPath = p.getFilterPath();
						//sequence.parameterName = p.getName();//((String) s.get("text")).substring(((String)s.get("text")).lastIndexOf('/')); //)//t.getsetTargetPath((String) s.get("text"));
					}
				}				
			});
			

			CallbackListener pasteTargetListener = new CallbackListener () {
				@Override
				public void controlEvent(CallbackEvent theEvent) {
		
					if (cf.control().papplet.mouseButton==(VurfEclipse.MOUSE_RIGHT)) {
						((ChangeParameterSequence) sequence).setTargetPath((String) APP.getApp().pr.getSequencer().getSelectedTargetPath());
						lstTarget.setLabel(APP.getApp().pr.getSequencer().getSelectedTargetPath());
					}
				}
			};
			txtExpression.addListenerFor(ScrollableList.ACTION_RELEASE, pasteTargetListener);
			
			g.add(lstTarget);		
		//seq.setHeight(30);
		
		//pos_y += lstTarget.getHeight() + margin_x/2;
		

		lblInputValue = cf.control().addLabel(name + "_input_indicator").setPosition(pos_x, pos_y - 5).moveTo(g);
		//pos_x += lblInputValue.getWidth() + margin_x;
		pos_x += 52;
		lblOutputValue = cf.control().addLabel(name + "_output_indicator").setPosition(pos_x, pos_y + 5).moveTo(g);
		
		g.add(lblInputValue);
		g.add(lblOutputValue);
			
		//sequenceEditor.setBackgroundHeight(sequenceEditor.getBackgroundHeight() + y);
		g.setBackgroundHeight(g.getBackgroundHeight());// + 20);

		
		return sequenceEditor;
	}

	/*public String getParameterPath() {
		return this.filterPath + "/pa/" + this.parameterName;
	}*/
	
	/*public void setParameterPath(String path) {
		Parameter p = (Parameter) host.host.getObjectForPath(path);
		parameterName = p.getName();
		filterPath = p.getFilterPath();
	}*/
	

	@Override
	public String toString() {
		return super.toString() + " " + this.getTargetPath();
	}

	@Override
	public boolean notifyRemoval(Filter newf) {
		if (this.getTargetPath().contains(newf.getPath())) { //.equals(this.filterPath)) {
			this.setEnabled(false);
			return true;
		}
		return false;
	}
	

	public void preserveCurrentParameters() {
		super.preserveCurrentParameters();
	}


}
