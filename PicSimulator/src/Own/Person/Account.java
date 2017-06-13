package Own.Person;

/**
 * Représente un compte avec de l'argent et des capacités de consommation d'argent et de virement.
 */
public abstract class Account {
	/**
	 * Argent sur le compte en euros
	 */
	private double balance;
	
	public Account() {
		this(0);
	}
	
	public Account(double initialAmount) {
		this.balance = initialAmount;
	}
	
	/**
	 * Décrémente l'argent sur le compte
	 * @param amount Montant à décompter
	 */
	public void pay(double amount) {
		if(hasEnough(amount)) throw new IllegalArgumentException("Account does not have enough balance!");
		balance -= amount;
	}
	
	public boolean hasMoney() {
		return balance != 0;
	}
	
	/**
	 * Vérifie les capacités du compte courant
	 * @param amount Montant minimal
	 * @return true si le compte a les capacités demandées
	 */
	public boolean hasEnough(double amount) {
		return amount <= balance;
	}
	
	/**
	 * Transfère de l'argent d'un compte à un autre
	 * @param other Autre compte sur lequel débiter l'argent
	 * @param amount Montant à transférer
	 */
	public void transfer(Account other, double amount) {
		if(!other.hasMoney()) throw new IllegalArgumentException("Account does not have money!");
		if(!other.hasEnough(amount)) throw new IllegalStateException("Account does not have enough balance!");
		other.pay(amount);
		balance += amount;
	}
}
