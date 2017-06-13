package Own.Bartender;

import Agent.Student;
import Util.Beer;

/**
 * Représente une commande, concernant un étudiant
 * et un consommable (ici bière).
 */
public class Order {
	/**
	 * Étudiant à servir
	 */
	private Student student;
	
	/**
	 * Commande
	 */
	private Beer beerType;

	public Order(Student student, Beer beerType) {
		this.student = student;
		this.beerType = beerType;
	}

	public Student getStudent() {
		return student;
	}

	public Beer getBeerType() {
		return beerType;
	}
}
