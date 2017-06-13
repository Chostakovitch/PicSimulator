package View;

import static State.StudentState.POOR;
import static State.StudentState.WALKING_TO_EXIT;
import static State.StudentState.WALKING_TO_WAITING_LINE;

import java.awt.Color;
import java.awt.Graphics2D;

import Agent.Student;
import Model.Pic;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;

/**
 * Implémentation des représentations avec mise à l'échelle pour les permanenciers.
 * 
 * Un cercle intérieur est de plus dessiné en fonction de l'état de l'étudiant courant.
 */
public class StudentPortrayal extends ScalablePortrayal<Student> {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Modèle de la simulation
	 */
	Pic pic;
	
	public StudentPortrayal(SimState state) {
		super(state);
		
		//Les étudiants sont verts
		paint = Color.GREEN;
		
		//Paramétrage de la classe
		entityType = Student.class;
	}
	
	@Override
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		//Dessin de base
		super.draw(object, graphics, info);
		
		if(object instanceof Student) {
			Student student = (Student) object;
            //Largeur d'un éventuel cercle intérieur
        	int secondaryWidth = (int)(effectiveWidth / 3);
        	
            //L'étudiant a une bière : cercle orange
            if(!student.getCup().isEmpty()) {
            	graphics.setColor(Color.orange);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            }
            //L'étudiant s'en va : cercle vert
            else if(student.getStudentState() == WALKING_TO_EXIT) {
            	graphics.setColor(Color.green);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            }
            //L'étudiant va chercher une bière : cercle bleu
            else if(student.getStudentState() == WALKING_TO_WAITING_LINE) {
            	graphics.setColor(Color.blue);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            }
            //L'étudiant n'a plus assez d'argent : cercle rouge
            else if(student.getStudentState() == POOR) {
            	graphics.setColor(Color.red);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            }
		}
	}
}
