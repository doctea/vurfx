package vurfeclipse.filters;

import vurfeclipse.scenes.PlasmaScene;
import vurfeclipse.scenes.Scene;

public class MandlebulbDrawer extends ShaderFilter {
	/**
	 * 
	 */
	private final PlasmaScene plasmaScene;
	public MandlebulbDrawer(PlasmaScene plasmaScene) {
		super(null,"mandlebulb.glsl");
		this.plasmaScene = plasmaScene;
	}
	public MandlebulbDrawer(PlasmaScene plasmaScene, Scene sc) {
		super(sc,"mandlebulb.glsl");
		this.plasmaScene = plasmaScene;
	}
	@Override
	public void setParameterDefaults() {
		super.setParameterDefaults();
		/*
		#define cameraPos vec3(cameraPos_x, cameraPos_y, cameraPos_z)
		#define cameraLookat vec3(cameraLookat_x, cameraLookat_y, cameraLookat_z)
		#define lightDir vec3(lightDir_x, lightDir_y, lightDir_z)
		#define lightColour vec3(lightColor_x, lightColor_y, lightColor_z)
		
		uniform float specular;
		uniform float specularHardness;
		//uniform vec3 diffuse;
		#define diffuse vec3(diffuse_x, diffuse_y, diffuse_z)
		uniform float ambientFactor;
		uniform bool ao;
		uniform bool shadows;
		uniform bool rotateWorld;
		uniform bool antialias;*/
		this.addParameter("cameraPos_x", new Float(0.0f), -5000.0f, 5000.0f);
		this.addParameter("cameraPos_y", new Float(0.0f), -5000.0f, 5000.0f);
		this.addParameter("cameraPos_z", new Float(0.0f), -5000.0f, 5000.0f);
		
		this.addParameter("cameraLookat_x", new Float(0.0f), -5000.0f, 5000.0f);
		this.addParameter("cameraLookat_y", new Float(0.0f), -5000.0f, 5000.0f);
		this.addParameter("cameraLookat_z", new Float(0.0f), -5000.0f, 5000.0f);
		
		this.addParameter("lightDir_x", new Float(0.0f), -500000.0f, 500000.0f);
		this.addParameter("lightDir_y", new Float(0.0f), -500000.0f, 500000.0f);
		this.addParameter("lightDir_z", new Float(0.0f), -500000.0f, 500000.0f);
		
		this.addParameter("lightColour_x", new Float(0.0f), -50.0f, 50.0f);
		this.addParameter("lightColour_y", new Float(0.0f), -50.0f, 50.0f);
		this.addParameter("lightColour_z", new Float(0.0f), -50.0f, 50.0f);
	
		this.addParameter("diffuse_x", new Float(0.0f), -50.0f, 50.0f);
		this.addParameter("diffuse_y", new Float(0.0f), -50.0f, 50.0f);
		this.addParameter("diffuse_z", new Float(0.0f), -50.0f, 50.0f);
		
		this.addParameter("specular", 5.0f);
		this.addParameter("specularHardness", 5.0f);
		
		this.addParameter("ambientFactor", 5.0f);
		
		this.addParameter("rotateWorld", new Boolean(true));
		this.addParameter("ao", new Boolean(true));
		this.addParameter("antialias", new Boolean(true));
		this.addParameter("shadows", new Boolean(true));
		
	}
	@Override
	public Filter nextMode() {
		changeParameterValue("colourMode", new Integer(((Integer)getParameterValue("colourMode")+1)));
		//if ((Integer)getParameterValue("colourMode")>this.colourModeCount) {
		//	changeParameterValue("colourMode", new Integer(0));
		//}
		return this;
	}
}