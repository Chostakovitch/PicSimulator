package Main;
import Model.Pic;
import View.PicWithUI;
import sim.display.Console;

/**
 * Classe de lancement de la simulation
 */
public class PicSimulatorMain {
    public static void main(String[] args) {
        runUI();
    }

    private static void runUI() {
        Pic model = new Pic(System.currentTimeMillis());
        PicWithUI gui = new PicWithUI(model);
        Console console = new Console(gui);
        
        //Contrôle du délai, à ajuster pour vos besoins
        console.setPlaySleep(75);
        
        console.setVisible(true);
    }
}