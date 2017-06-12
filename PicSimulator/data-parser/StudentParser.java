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

        // Si la personne ne va pas au pic ou ne boit pas, elle ne nous intéresse pas.
        if (oldLine[5].equals("Non") || oldLine[6].equals("Non")) return null;

        // GENDER
        if (oldLine[1].equals("Un homme")) newLine[0] = "H";
        else if (oldLine[1].equals("Une femme")) newLine[0] = "F";
        else newLine[0] = "NA";

        // AGE
        // TODO: remplacer par moyenne des ages
        if (!oldLine[2].matches("^-?\\d+$")) newLine[1] = "0";
        else newLine[1] = oldLine[2];

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
        for (int i = 1; i <= 10; i++) {
            String avis = oldLine[6+i];
            if (avis.isEmpty() || avis.equals("Jamais bu")) newLine[3+i] = "0";
            else if (avis.equals("Je déteste !")) newLine[3+i] = "-5";
            else if (avis.equals("Bof...")) newLine[3+i] = "-1";
            else if (avis.equals("Pas mal")) newLine[3+i] = "2";
            else if (avis.equals("J'adore !")) newLine[3+i] = "5";
            else newLine[3+i] = "0";
        }

        // Nombre de bière bu par soir
        if(oldLine[18].matches("^-?\\d+$")) newLine[14] = oldLine[18];
        else newLine[14] = "2";

        // Temps pour boire une bière
        if(oldLine[19].matches("^-?\\d+$")) newLine[14] = oldLine[19];
        else newLine[15] = "20";

        // Heure d'arrivée
        if (oldLine[20].isEmpty()) newLine[16] = "20:00:00";
        else newLine[16] = oldLine[20];

        // Heure de départ
        if (oldLine[21].isEmpty()) newLine[17] = "22:00:00";
        else newLine[17] = oldLine[21];

        // Budget
        if(oldLine[22].matches("^-?\\d+$")) newLine[18] = oldLine[22];
        else newLine[18] = "10";

        // L'étudiant as-il-mangé
        if(oldLine[23].contains("Oui")) newLine[19] = "Oui";
        else newLine[19] = "Non";

        // Jours où l'étudiant vient au pic
        newLine[20] = oldLine[24];

        // Sensibilité à l'alcool
        if(oldLine[25].matches("^-?\\d+$")) newLine[21] = oldLine[25];
        else newLine[21] = "3";

        // Nombre d'amis rencontré lors d'une soirée au pic
        if(oldLine[26].matches("^-?\\d+$")) newLine[22] = oldLine[26];
        else newLine[22] = "4";

        return newLine;
    }
}
