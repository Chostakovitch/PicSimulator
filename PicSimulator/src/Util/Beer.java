package Util;

public enum Beer {
    CidreLoicRaison("Cidre Loic Raison", 1.5),
    CarolusTriple("Carolus Triple", 1.8),
    ChimayBleue("Chimay Bleue", 1.8),
    Duvel("Duvel", 1.7),
    GrandCruStFeuillien("Grand Cru St Feuillien", 1.95),
    Watou("Watou(att)", 1.35),
    WestmalleTriple("Westmalle Triple", 1.75),
    BarbarBok("Barbar Bok", 1.6),
    ChimayDoree("Chimay Dorée", 1.8), //Todo prix random, pas trouvé sur le site
    ChouffeSoleil("Chouffe Soleil", 1.75),
    CuveeDesTrolls("Cuvée Des Trolls", 1.75),
    DeliriumTremens("Delirium Tremens", 1.80),
    GauloiseRouge("Gauloise Rouge", 1.90),
    Kwak("Kwak", 1.95),
    McChouffe("Mc Chouffe", 1.95),
    BarbarBlonde("Barbar Blonde", 1.6);

    private String name = "";
    private double price = 0.0;

    //Constructeur
    Beer(String name, double price){
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String toString(){
        return name + " , price : " + price;
    }
}
