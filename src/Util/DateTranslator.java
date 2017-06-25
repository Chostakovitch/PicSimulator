package Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire de traduction de jours de la semaine en anglais
 */
public class DateTranslator {
	private static Map<String, String> dates = new HashMap<String, String>();

	private static void fillMap(){
		dates.put("lundi", "monday");
		dates.put("mardi", "tuesday");
		dates.put("mercredi", "wednesday");
		dates.put("jeudi", "thursday");
		dates.put("vendredi", "friday");
	}
	
	public static String[] translateArray(String[] datesToTranslate) {
		String[] translatedDates = new String[datesToTranslate.length];
		if(dates.isEmpty()) fillMap();
		for(int i = 0 ; i < datesToTranslate.length ; i++) {
			String day = datesToTranslate[i].trim();
			if(dates.containsKey(day)) {
				translatedDates[i] = dates.get(day).toUpperCase();
			}
		}
		return translatedDates;
	}
}