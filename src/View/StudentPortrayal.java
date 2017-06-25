package View;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import Agent.Chair;
import Agent.Student;
import Enum.Direction;
import Model.Pic;
import State.StudentState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

import static State.StudentState.*;

/**
 * Implémentation des représentations avec mise à l'échelle pour les permanenciers.
 * 
 * Un cercle intérieur est de plus dessiné en fonction de l'état de l'étudiant courant.
 */
public class StudentPortrayal extends ScalablePortrayal<Student> {
	private static final long serialVersionUID = 1L;
	
	private static final String baseImageName = "student";
	
	private static final String extension = ".png";
	
	/**
	 * Modèle de la simulation
	 */
	Pic pic;
	
	public StudentPortrayal(SimState state) {
		//Le dessin effectif est délayé
		super(state, false, true);
		
		//Paramétrage de la classe
		entityType = Student.class;
		
		pic = (Pic) state;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		//Calcul préalable
		super.draw(object, graphics, info);
		
		if(object instanceof Student) {
			Student student = (Student) object;
			Direction direction = student.getDirection(); 
			List<Chair> chairs = pic.getEntitiesAtLocation(pic.getModel().getObjectLocation(student), Chair.class);
			if(!chairs.isEmpty()) {
				direction = chairs.get(0).getDirection();
			}
			String suffixDir = getDirectionSuffix(direction);
			String suffixState = getStateSuffix(student);
			setBackground(baseImageName + suffixDir + suffixState + extension);
		}
        
        //Dessin effectif
		drawEffectivly();
	}
	
	/**
	 * Obtient un suffixe standardisé d'image en fonction de l'état de l'étudiant
	 * @param student Étudiant
	 * @return Suffixe d'état
	 */
	public String getStateSuffix(Student student) {
    	//L'étudiant est vraiment trop bourré : la couleur prime sur les autres
		 if(student.isDrunk()) return "_drunk";
		 
         //L'étudiant a une bière : changement s'il n'est pas arrêté pour la boire
		 else if(!student.getCup().isEmpty() && student.getStudentState() != DRINKING_WITH_FRIENDS) return "_beer";
		 
         //L'étudiant s'en va
         else if(student.getStudentState() == WALKING_TO_EXIT) return "_out";
		 
         //L'étudiant n'a plus assez d'argent
         else if(student.isVeryPoor() || student.getStudentState()  == POOR) return "_poor";
		 
         //L'étudiant va chercher une bière
         else if(student.getStudentState()  == WALKING_TO_WAITING_LINE || student.getStudentState()  == CHOOSING_WAITING_LINE) return "_line";
		 
         //L'étudiant attend d'être servi
         else if(student.getStudentState()  == WAITING_IN_QUEUE || student.getStudentState()  == WAITING_FOR_BEER) return "_waiting";
		 
         //L'étudiant est en train de boire avec ses amis
         else if(student.getStudentState()  == DRINKING_WITH_FRIENDS) return "_drinking";
		 
		 //On ne devrait pas arriver ici
         else return "_nothing";
	}
}
