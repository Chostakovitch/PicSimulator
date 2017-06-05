package Util;

public enum Beer {
    CidreLoicRaison("Cidre Loic Raison"),
    CarolusTriple("Carolus Triple"),
    ChimayBleue("Chimay Bleue"),
    Duvel("Duvel"),
    GrandCruStFeuillien("Grand Cru St Feuillien"),
    Watou("Watou(att)"),
    WestmalleTriple("Westmalle Triple"),
    BarbarBok("Barbar Bok"),
    ChimayDoree("Chimay Dorée"),
    ChouffeSoleil("Chouffe Soleil"),
    CuveeDesTrolls("Cuvée Des Trolls"),
    DeliriumTremens("Delirium Tremens"),
    GauloiseRouge("Gauloise Rouge"),
    Kwak("Kwak"),
    McChouffe("Mc Chouffe"),
    BarbarBlonde("Barbar Blonde");

    private String name = "";



    //Constructeur

    Beer(String name){
        this.name = name;
    }



    public String toString(){
        return name;
    }
}
