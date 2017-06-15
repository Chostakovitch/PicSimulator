package Enum;

public enum Beer {
    CidreLoicRaison("Cidre Loic Raison", 1.5),
    CarolusTriple("Carolus Triple", 1.8),
    ChimayBleue("Chimay Bleue", 1.8),
    BarbarBok("Barbar Bok", 1.6),
    Duvel("Duvel", 1.7),
    BarbarBlonde("Barbar Blonde", 1.6),
    GrandCruStFeuillien("Grand Cru St Feuillien", 1.95),
    Watou("Watou(att)", 1.35),
    WestmalleTriple("Westmalle Triple", 1.75),
    ChimayDoree("Chimay Dorée", 1.8), //Todo prix random, pas trouvé sur le site
    ChouffeSoleil("Chouffe Soleil", 1.75),
    CuveeDesTrolls("Cuvée Des Trolls", 1.75),
    DeliriumTremens("Delirium Tremens", 1.80),
    GauloiseRouge("Gauloise Rouge", 1.90),
    Kwak("Kwak", 1.95),
    McChouffe("Chouffe", 1.95),
    PecheMelBush("Pêche Mel Bush", 18.0);

    private String name = "";
    private double price = 0.0;

    //Constructeur
    Beer(String name, double price){
        this.name = name;
        this.price = price;
    }

    public static Beer getCorrespondantEnum(String name) {
        for(Beer beer : Beer.values()) {
            if(beer.getName().equals(name)) return beer;
        }
        return null;
    }

    public double getPrice() {
        return price;
    }

    public String getName() { return name ;}

    @Override
	public String toString(){
        return name + " , price : " + price;
    }
}
