package vurfeclipse.sequence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controlP5.CallbackEvent;
import controlP5.CallbackListener;
import controlP5.ControlP5;
import controlP5.ScrollableList;
import controlP5.Textfield;
import vurfeclipse.APP;
import vurfeclipse.filters.Filter;
import vurfeclipse.parameters.Parameter;
import vurfeclipse.parameters.ParameterBuffer;
import vurfeclipse.scenes.Mutable;
import vurfeclipse.scenes.Scene;
import vurfeclipse.ui.ControlFrame;
import vurfeclipse.ui.SequenceEditor;

public class ChangeParameterSequence extends Sequence {
	
	com.udojava.evalex.Expression e;
	
	String filterPath, parameterName;
	String expression = "input";

	Object value;
	
	ParameterBuffer paramBuffer;
	
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
		e = new com.udojava.evalex.Expression(expression);
	}

	public ChangeParameterSequence() { 
		super(); 
		this.paramBuffer = new ParameterBuffer(10,50);
	}
	
	public ChangeParameterSequence(Scene host, String filterPath, String parameterName, Object startValue, String expression, int length) {
		//super(host,length);
		super(host,length);
		this.filterPath = filterPath;
		this.parameterName = parameterName;
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
		params.put("filterPath",  filterPath);
		params.put("parameterName", parameterName);
		params.put("value", this.value);
		params.put("expression", this.expression);
		return params;
	}
	
	@Override
	public void loadParameters(HashMap<String,Object> params) {
		super.loadParameters(params);
		if (params.containsKey("filterPath"))	this.filterPath = (String) params.get("filterPath");
		if (params.containsKey("parameterName")) this.parameterName = (String) params.get("parameterName");
		if (params.containsKey("value")) 		this.value = params.get("value");
		if (params.containsKey("expression")) 	this.setExpression((String) params.get("expression"));
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
	synchronized public void __setValuesForNorm(double pc, int iteration) {
		
		// evaluate value to pass based on expression
		if (e==null) e = new com.udojava.evalex.Expression(expression);
		e.setVariable("input", BigDecimal.valueOf(pc));
		e.setVariable("iteration", BigDecimal.valueOf(iteration));
		BigDecimal value = e.eval();
		
		//println("got value " + value);
		
		if (paramBuffer!=null)
			value = (BigDecimal) paramBuffer.getValue(value, false);	// lerp
		
		((Filter)host.host
				.getObjectForPath(filterPath))
				//.changeParameterValueFromSin(parameterName, (float)value.doubleValue());//(float)Math.sin(value.doubleValue()));		
				.changeParameterValue(parameterName, (float)value.doubleValue());
	}

	@Override
	public void onStart() {
		((Filter)host.host
			.getObjectForPath(filterPath))
			.changeParameterValue(parameterName, value);
	}

	@Override
	public void onStop() {
		try {
			((Filter)host.host
					.getObjectForPath(filterPath))
					.changeParameterValue(parameterName, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public SequenceEditor makeControls(ControlFrame cf, String name) {
		// add an accordion to hold the sub-sequences and recurse
		SequenceEditor sequenceEditor = super.makeControls(cf, name);
		
		final ChangeParameterSequence sequence = this;

		int pos_y = 60, margin_x = 10;
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
					.setPosition((cp5.papplet.width/3) + margin_x * 10, pos_y)
					.moveTo(g).setLabel("Expression")
					.setAutoClear(false);
			
			// TODO: add callback handler to actually set expression

			CallbackListener setExpression = new CallbackListener() {
				public void controlEvent(CallbackEvent theEvent) {
					//((ScrollableList)theEvent.getController()).close();
					setExpression(((Textfield)theEvent.getController()).getText());
					((Textfield)theEvent.getController()).setValueLabel(sequence.getExpression());
				}
			};
			txtExpression.addListenerFor(txtExpression.ACTION_BROADCAST,setExpression);
			
			g.add(txtExpression);
			
			//final FormulaCallback fc = (FormulaCallback) c; 
				
			final ScrollableList lstTarget = cp5.addScrollableList(name + "_Target URL")
					//.addItem(((FormulaCallback)c).targetPath, ((FormulaCallback)c).targetPath)
					.setLabel(this.getParameterPath()) //((FormulaCallback)c).targetPath)
					.addItems(APP.getApp().pr.getTargetURLs().keySet().toArray(new String[0]))
					.setPosition(80, pos_y)
					.setWidth((cp5.papplet.width/3))
					.setBarHeight(16).setItemHeight(16)
					.moveTo(g)
					.onLeave(cf.close)
					.onEnter(cf.toFront)
					.close();
			
			//lstTarget.setValue(targetPath);
			
			lstTarget.addListenerFor(ScrollableList.ACTION_BROADCAST, new CallbackListener () {
				@Override
				synchronized public void controlEvent(CallbackEvent theEvent) {
					// TODO Auto-generated method stub
					Map<String, Object> s = ((ScrollableList) theEvent.getController()).getItem((int)lstTarget.getValue());
					//s.entrySet();
					Parameter p = (Parameter) host.host.getObjectForPath((String)s.get("text"));
					synchronized(sequence) {
						sequence.setHost(((Filter)host.host.getObjectForPath(p.getFilterPath())).sc);
						sequence.filterPath = p.getFilterPath();
						sequence.parameterName = p.getName();//((String) s.get("text")).substring(((String)s.get("text")).lastIndexOf('/')); //)//t.getsetTargetPath((String) s.get("text"));
					}
				}				
			});
			
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

	@Override
	public boolean notifyRemoval(Filter newf) {
		if (newf.getPath().equals(this.filterPath)) {
			this.setEnabled(false);
			return true;
		}
		return false;
	}

}
