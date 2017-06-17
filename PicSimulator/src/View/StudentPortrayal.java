package View;

import java.awt.Color;
import java.awt.Graphics2D;

import Agent.Student;
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
			StudentState studentState = student.getStudentState();
            //Largeur d'un éventuel cercle intérieur
        	int secondaryWidth = (int)(effectiveWidth / 3);
        	
            //L'étudiant a une bière : cercle orange mais qu'il n'est pas arrêté pour la boire
            if(!student.getCup().isEmpty() && studentState != DRINKING_WITH_FRIENDS) {
            	graphics.setColor(Color.ORANGE);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            }
            //L'étudiant n'a plus assez d'argent : cercle rouge
            else if(student.isVeryPoor() || studentState == POOR) {
            	graphics.setColor(Color.RED);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            	//État supérieur aux autres
            	return;
            }
            //L'étudiant s'en va : cercle rose
            else if(studentState == WALKING_TO_EXIT) {
            	graphics.setColor(Color.PINK);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            }
            //L'étudiant va chercher une bière : cercle bleu
            else if(studentState == WALKING_TO_WAITING_LINE || studentState == CHOOSING_WAITING_LINE) {
            	graphics.setColor(Color.BLUE);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            } 
            else if(studentState == StudentState.WALKING) {
            	graphics.setColor(Color.BLACK);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            }
            //L'étudiant attend d'être servi
            else if(studentState == WAITING_IN_QUEUE || studentState == WAITING_FOR_BEER) {
            	graphics.setColor(Color.YELLOW);
            	graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
            }
            //L'étudiant est en train de boir avec ses amis
            else if(studentState == DRINKING_WITH_FRIENDS) {
				graphics.setColor(Color.MAGENTA);
				graphics.fillOval(x + secondaryWidth, y + secondaryWidth, secondaryWidth, secondaryWidth);
			}
		}
	}
}
