package Own.Student;

import Enum.Beer;
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
		quantity = Constant.CUP_CAPACITY;
		beerType = beer;
	}
	
	public void emptyCup(){
		quantity = 0;
		beerType = null;
	}
	
	public boolean isEmpty() {
		return quantity == 0;
	}
	
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Boit une gorgée de bière
	 * @param amount Centilitres bus par la gorgée
	 * @throws IllegalStateException Si la bière ne contient pas assez de liquide
	 */
	public void drink(int amount) throws IllegalStateException {
		if(isEmpty()) throw new IllegalStateException("Nothing in the cup!");
		quantity -= (amount < quantity) ? amount : quantity;
	}
}
