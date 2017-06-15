

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by aureliedigeon on 12/06/2017.
 */
public class StudentParser {
    public static void main(String[] args) {
        try {
            CSVReader reader = openCSV("resources/raw-data-students.csv");
            parseData(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CSVReader openCSV(String fileName) throws FileNotFoundException {
        FileReader fr = new FileReader(fileName);
        CSVReader reader = new CSVReader(fr, ',', '"', 1);
        return reader;
    }

    public static void parseData(CSVReader reader) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter("resources/parsed-data-students.csv"));
        String [] nextLine;

        while ((nextLine = reader.readNext()) != null) {
            String[] newLine = parseLine(nextLine);
            writer.writeNext(newLine);
        }
        writer.close();
    }

    private static String[] parseLine(String[] oldLine) {
        String[] newLine = new String[23];
        int result;

        // Si la personne ne va pas au pic ou ne boit pas, elle ne nous intéresse pas.
        if (oldLine[5].equals("Non") || oldLine[6].equals("Non")) return null;

        // GENDER
        if (oldLine[1].equals("Un homme")) newLine[0] = "H";
        else if (oldLine[1].equals("Une femme")) newLine[0] = "F";
        else newLine[0] = "NA";

        // AGE
        // TODO: remplacer par moyenne des ages
        newLine[1] = testNumber(oldLine[2], "21", 15,90 );

        // BRANCHE/TC/HUTECH/PROF etc
        Boolean ok = false;
        List<String> list = Arrays.asList("tronc commun", "branche", "doctorant", "escom", "hutech", "diplomé", "double diplome");
        for(String str: list) {
            if(oldLine[3].toLowerCase().contains(str)) {
                newLine[2] = str;
                ok = true;
                break;
            }
        }
        if (!ok) {
            list = Arrays.asList("professeur", "chercheur", "prof");
            for(String str: list) {
                if(oldLine[3].toLowerCase().contains(str)) {
                    newLine[2] = "professeur-chercheur";
                    ok = true;
                    break;
                }
            }
        }
        if (!ok) {
            list = Arrays.asList("vieux", "con", "ancien");
            for(String str: list) {
                if(oldLine[3].toLowerCase().contains(str)) {
                    newLine[2] = "diplomé";
                    ok = true;
                    break;
                }
            }
        }
        if (!ok) {
            list = Arrays.asList("double-diplome", "double diplome");
            for(String str: list) {
                if(oldLine[3].toLowerCase().contains(str)) {
                    newLine[2] = "double diplome";
                    ok = true;
                    break;
                }
            }
        }
        // TODO: chercher le type majoritaire
        if (!ok) newLine[2] = "branche";

        // Niveau
        if (newLine[2].equals("tronc commun")) {
            if (oldLine[4].matches("TC\\d\\d")) newLine[3] = oldLine[4].toUpperCase();
            else newLine[3] = "TC02";
        } else if (newLine[2].equals("branche")) {
            if (oldLine[4].toUpperCase().matches("(GI|GB|GSU|GM|GSM|GP|IM)\\d\\d")) newLine[3] = oldLine[4].toUpperCase();
            else newLine[3] = "GI04";
        } else if (newLine[2].equals("hutech")) {
            if (oldLine[4].toUpperCase().matches("(HU)\\d\\d")) newLine[3] = oldLine[4].toUpperCase();
            if (oldLine[4].toUpperCase().matches("(HUTECH)\\d\\d")) newLine[3] = oldLine[4].toUpperCase().replace("HUTECH", "HU");
            else newLine[3] = "GI04";
        }
        else if (oldLine[4].toUpperCase().matches("GX\\d\\d")) newLine[3] = oldLine[4].toUpperCase().replace("X", "I");
        else newLine[3] = "";

        // Bières
        String[] beers = {"Cidre Loic Raison","Chimay Bleue","Duvel","Grand Cru St Feuillien","Pêche Mel Bush","Westmalle Triple","Barbar Blonde","Chouffe","Cuvée Des Trolls","Delirium Tremens","Gauloise Rouge"};
        for (int i = 1; i <= 10; i++) {
            String note;
            String avis = oldLine[6+i];
            if (avis.isEmpty() || avis.equals("Jamais bu")) note = "0";
            else if (avis.equals("Je déteste !")) note = "-5";
            else if (avis.equals("Bof...")) note = "-1";
            else if (avis.equals("Pas mal")) note = "2";
            else if (avis.equals("J'adore !")) note = "5";
            else note = "0";
            newLine[3+i] = beers[i-1] + ':' + note;
        }

        // Nombre de bière bu par soir
        newLine[14] = testNumber(oldLine[18], "2", 0,10 );

        // Temps pour boire une bière
        newLine[15] = testNumber(oldLine[19], "20", 0,120 );

        // Heure d'arrivée
        if (oldLine[20].isEmpty()) newLine[16] = "20:00:00";
        else newLine[16] = oldLine[20];

        // Heure de départ
        if (oldLine[21].isEmpty()) newLine[17] = "22:00:00";
        else newLine[17] = oldLine[21];

        // Budget
        newLine[18] = testNumber(oldLine[22], "10", 0,100 );

        // L'étudiant as-il-mangé
        String res;
        switch (oldLine[23]) {
            case "Oui": res= "repas"; break;
            case "Non, je mange un menu au Pic": res= "menu"; break;
            case "Non, je mange des snacks au Pic": res= "snack"; break;
            case "Non, manger c'est tricher":
            case "Non, manger c'est triché": res= ""; break;
            default: res= "";
        }
        newLine[19] = res;

        // Jours où l'étudiant vient au pic
        String[] jours = oldLine[24].split(",");
        for (int i= 0; i < jours.length; i++) jours[i] = jours[i].toLowerCase();
        newLine[20] = String.join(",", jours);

        // Sensibilité à l'alcool
        newLine[21] = testNumber(oldLine[25], "3", 0,5 );

        // Nombre d'amis rencontré lors d'une soirée au pic
        newLine[22] = testNumber(oldLine[26], "3", 0,50 );

        return newLine;
    }

    private static String testNumber(String number, String def, Integer min, Integer max) {
        int result;
        try {
            result = Integer.parseInt(number);
            if (( min != null && result < min) || (max != null && result > max)) return number;
            return def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
