package Agent;

import java.util.ArrayList;
import java.util.List;

import Model.Pic;
import Util.Constant;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;

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
	
	/**
	 * Distance maximale de déplacementl
	 */
	private int walkCapacity;

    public Student() {
    	inside = false;
    	hasBeenInside = false;
    	walkCapacity = Constant.STUDENT_WALK_CAPACITY;
    }

    @Override
    public void step(SimState state) {
        Pic pic = (Pic) state;
        //L'étudiant n'est pas encore dans le Pic et doit y entrer
        if(!inside && mustEnterPic()) {
        	//L'étudiant arrive à l'entrée du Pic, il bouge immédiatement sur une position valide
        	pic.getModel().setObjectLocation(this, Constant.PIC_ENTER);
        	pic.incrStudentsInside();
        	inside = true;
        	hasBeenInside = true;
        	justMoveIt(pic);
        }
        
        //L'étudiant est dans le pic et doit en sortir
        else if(inside && mustLeavePic()) {
        	pic.getModel().remove(this);
        	pic.decStudentsInside();
        	inside = false;
        }
        
        //L'étudiant était déjà dans le pic : il décide de l'action à effectuer
        else if(inside) {
        	//L'étudiant doit effectuer un déplacement
        	if(mustWalk()) justMoveIt(pic);
        }
    }
    
    /**
     * Déplace l'étudiant courant à un point aléatoire
     * @param pic État de la simulation
     */
    private void justMoveIt(Pic pic)  {
    	//Position courante
    	Int2D currentPos = pic.getModel().getObjectLocation(this);
    	
    	//Positions possibles
    	List<Int2D> possiblePos = pic.getSquareValidLocations(currentPos, walkCapacity);
    			
    	//Sélection d'une position aléatoire
    	Int2D selectedPos = possiblePos.get(pic.random.nextInt(possiblePos.size()));
    	
    	//Mise à jour de la position
    	pic.getModel().setObjectLocation(this, selectedPos);
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
    
    /**
     * Indique si l'étudiant doit effectuer un déplacement
     * @return Booléean
     * @throws IllegalStateException si l'étudiant n'est pas dans le Pic
     */
    private boolean mustWalk() throws IllegalStateException {
    	if(!inside) throw new IllegalStateException("Student is not inside Pic");
    	return Math.random() < 0.5;
    }
}