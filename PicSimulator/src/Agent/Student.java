package Agent;

import Model.Pic;
import Util.Constant;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Agent dynamique représentant un étudiant (pas nécessairement au sein du Pic).
 */
public class Student implements Steppable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Indique si l'étudiant est dans le Pic.
	 */
	private boolean inside;
	
	/**
	 * Indique si l'étudiant est précédemment entré dans le Pic.
	 */
	private boolean hasBeenInside;

    public Student() {
    	inside = false;
    	hasBeenInside = false;
    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;
        //L'étudiant n'est pas encore dans le Pic et doit y entrer
        if(!inside && mustEnterPic()) {
        	//L'étudiant arrive à l'entrée du Pic
        	pic.getModel().setObjectLocation(this, Constant.PIC_ENTER);
        	pic.incrStudentsInside();
        	inside = true;
        	hasBeenInside = true;
        }
        
        //L'étudiant est dans le pic et doit en sortir
        else if(inside && mustLeavePic()) {
        	pic.getModel().remove(this);
        	pic.decStudentsInside();
        	inside = false;
        }
        
        //L'étudiant était déjà dans le pic : il décide de l'action à effectuer
        else if(inside) {
        	
        }
    }
    
    /**
     * Indique si l'étudiant doit entrer dans le Pic
     * @return Booléen
     * @throws IllegalStateException si l'étudiant est déjà dans le Pic
     */
    private boolean mustEnterPic() throws IllegalStateException {
    	if(inside) throw new IllegalStateException("Student is already inside Pic");
    	double prob = Math.random();
    	if(hasBeenInside)
    		return prob < 0.1;
    	return prob < 0.6; 
    }
    
    /**
     * Indique si l'étudiant doit sortir du Pic
     * @return Booléen
     * @throws IllegalStateException si l'étudiant n'est pas dans le Pic
     */
    private boolean mustLeavePic() throws IllegalStateException {
    	if(!inside) throw new IllegalStateException("Student is not inside Pic");
    	return Math.random() < 0.4;    	
    }
}