package Own.Student;

import Util.Beer;
import Util.Constant;

/**
 * Représente le contenu du verre d'un étudiant.
 * Pour le moment, ne traite que des bières.
 */
//TODO implémenter les autres boissons (interface ?)
public class Drink {
	/**
	 * Type de bière dans le verre, null si sans objet
	 */
	private Beer beerType;
	
	/**
	 * Nombre de centilitres que peut contenir le verre
	 */
	private int capacity;
	
	/**
	 * Nombre de centilitres que contient effectivement le verre
	 */
	private int quantity;
	
	public Drink() {
		beerType = null;
		capacity = Constant.CUP_CAPACITY;
		quantity = 0;
	}

	public void fillCup(Beer beer) {
		capacity = Constant.CUP_CAPACITY;
		beerType = beer;
	}
	
	public boolean isEmpty() {
		return quantity == 0;
	}
	
	public void drink(int quantity) {
		if(isEmpty()) throw new IllegalStateException("Nothing in the cup!");
		this.quantity -= (this.quantity < quantity) ? this.quantity : quantity;
	}
}
