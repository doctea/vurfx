package vurfeclipse.user.scenes;

import vurfeclipse.filters.BlendDrawer;
import vurfeclipse.projects.Project;
import vurfeclipse.scenes.Scene;
import vurfeclipse.scenes.SimpleScene;

public class TunnelScene extends SimpleScene {

	public TunnelScene(Project p, int w, int h) {
		// TODO Auto-generated constructor stub
		super(p,w,h);
	}

	@Override
	public boolean setupFilters() {
		super.setupFilters();
		//super.setupFilters();
		// TODO Auto-generated method stub
		
		/*BlendDrawer bl1 = (BlendDrawer) new BlendDrawer(this).setFilterName("Blend_1");
		bl1.setInputCanvas(getCanvasMapping("out"));
		bl1.setOutputCanvas(getCanvasMapping("out"));
		bl1.changeParameterValue("Zoom", new Float(0.5f));
		bl1.changeParameterValue("Opacity", new Float(0.5f));
		bl1.changeParameterValue("X", new Float(0.25f));
		bl1.changeParameterValue("Y", new Float(0.33f));
		this.addFilter(bl1);*/
				
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_1").setInputCanvas("/pix0").setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(2.0f))
					.changeParameterValue("Opacity", new Float(0.05f))
					//.changeParameterValue("X", new Float(0.5f))
					//.changeParameterValue("Y", new Float(0.5f))
		);		
		
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_2").setInputCanvas("/pix0").setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(1.5f))
					.changeParameterValue("Opacity", new Float(0.1f))
					//.changeParameterValue("X", new Float(0.25f))
					//.changeParameterValue("Y", new Float(0.25f))
		);
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_3").setInputCanvas("/pix0").setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(0.75f))
					.changeParameterValue("Opacity", new Float(0.3f))
		);
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_4").setInputCanvas("/pix0").setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(0.5f))
					.changeParameterValue("Opacity", new Float(0.5f))
		);		
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_5").setInputCanvas("/pix0").setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(0.25f))
					.changeParameterValue("Opacity", new Float(0.6f))
		);		
		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_6").setInputCanvas("/pix0").setOutputCanvas(getCanvasMapping("temp"))
					.changeParameterValue("Scale", new Float(0.1f))
					.changeParameterValue("Opacity", new Float(0.8f))
		);				

		this.addFilter(
				new BlendDrawer(this).setFilterName("Blend_7").setInputCanvas(getCanvasMapping("temp")).setOutputCanvas(getCanvasMapping("out")));

		
		return true;
	}

}
