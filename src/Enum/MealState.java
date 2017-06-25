package Enum;

/**
 * État de la consommation de nourriture par l'étudiant
 */
public enum MealState {
    NO_MEAL (0), 
    REPAS (0.5), 
    SNACK (0.2), 
    MENU (0.4);
    
    private double alcoholLevelInfluence ;
    
    private MealState(double alcoholLevelInfluence){
    	this.alcoholLevelInfluence = alcoholLevelInfluence;
    }
    
    public double getAlcoholLevelInfluence(){
    	return this.alcoholLevelInfluence;
    }   
}