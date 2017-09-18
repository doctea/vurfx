package vurfeclipse.user.projects;

import vurfeclipse.filters.Filter;
import vurfeclipse.projects.SimpleProject;
import vurfeclipse.scenes.Scene;
import vurfeclipse.scenes.SimpleScene;

public class NewJourneyProject extends SimpleProject {

	class Doobie extends Filter {
		Doobie(Scene sc) {
			super(sc);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean applyMeatToBuffers() {
			// TODO Auto-generated method stub
			out.fill((int) (Math.random()*255));
			out.rect(50, 50, 50, 50);
			out.background((int) (Math.random()*255));
			return false;
		}

	}


	public NewJourneyProject(int w, int h, String gfx_mode) {
		super(w, h, gfx_mode);
	// TODO Auto-generated constructor stub

	}

	@Override
	public boolean setupScenes() {
		Scene s = new SimpleScene(this, w, h);
		s.addFilter(new Doobie(s));
		this.addScene(s);
		
		//((SequenceSequencer)this.sequencer)
		
		return true;
	}


}
