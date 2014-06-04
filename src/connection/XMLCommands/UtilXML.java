package connection.XMLCommands;

import java.util.HashMap;

/**
 * Serie di metodi per la gestione delle stringhe XML
 * @author Enrico Strocchi
 */
public class UtilXML {
	
	/**
	 * Ritorna l'oggetto cmd che rappresenta la stringa xml
	 * @param xml Stringa xml
	 * @return oggetto cmd
	 */
	public static Cmd getCmd(String xml){
		xml = xml.trim();
		
		if (!xml.subSequence(0,1).equals("<")) return null;
		if (!xml.substring(xml.length()-2).equals("/>")) return null;
		xml = xml.substring(1);
		xml = xml.substring(0,xml.length()-2);
		xml = xml.trim();
		
		String name = getCommand(xml);
		Cmd cmd = new Cmd(name,getAttribute(xml));	
		
		return cmd;
	}
	
	/**
	 * Controlla se il comando ha lo stesso nome della stringa passata
	 * @param com Comando
	 * @param name Nome
	 * @return true se sono uguale
	 */
	public static boolean cmpCmdName(Cmd com, String name){
		if (com == null || name == null) return false;
		return (com.getCmd().equals(name));
	}
	
	/**
	 * Controlla che un parametro sia presente nel comando e abbia
	 * il giusto valore
	 * @param com Comando
	 * @param key Chiave
	 * @param value Valore
	 * @return true se il parametro e' giusto
	 */
	public static boolean cmpCmdValue(Cmd com, String key, String value){
		if (com == null || key == null | value == null) return false;
		String v = com.getValue(key);
		if (v == null) return false;
		return v.equals(value);
	}
	
	/**
	 * Controlla che un parametro sia presente nel comando
	 * @param com Comando
	 * @param key Chiave
	 * @param value Valore
	 * @return true se il parametro e' giusto
	 */
	public static boolean existsParam(Cmd com, String key){
		if (com == null || key == null) return false;
		String v = com.getValue(key);
		if (v == null) return false;
		else return true;
	}
	
	/**
	 * Come la funzione precedente ma controlla un valore intero
	 * @param com Comando
	 * @param key Chiave
	 * @param value Valore intero
	 * @return true se il parametro e' giusto
	 */
	public static boolean cmpCmdValue(Cmd com, String key, int value){
		if (com == null || key == null) return false;
		String v = com.getValue(key);
		if (v == null) return false;
		int vn = 0;
		try {
			vn = Integer.parseInt(v);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return value == vn;
	}
	
	/**
	 * Ritorna il comando
	 * @param xml Stringa da analizzare
	 * @return comando
	 */
	private static String getCommand(String xml){
		int pos = 0;
		StringBuilder sb = new StringBuilder();
		while(xml.charAt(pos) != ' '){
			sb.append(xml.charAt(pos));
			pos++;
		}
		return sb.toString();
	}
	
	/**
	 * Ritorna un hash che contiene gli attributi del comando xml
	 * @param xml comando
	 * @return Hash con gli attributi
	 */
	private static HashMap<String,String> getAttribute(String xml){
		HashMap<String,String> hm = new HashMap<String,String>();
		
		int pos = 0;
		while(xml.charAt(pos) != ' ') pos++;
		pos++;
		
		while(pos<xml.length()){
			StringBuilder key = new StringBuilder();
			while(xml.charAt(pos) != '=') {
				key.append(xml.charAt(pos));
				pos++;
			}
			while(xml.charAt(pos)!='"') pos++;
			pos++;
			StringBuilder value = new StringBuilder();
			while(xml.charAt(pos) != '"') {
				value.append(xml.charAt(pos));
				pos++;
			}
			pos++;
			hm.put(key.toString().trim(),value.toString().trim());
		}
		
		return hm;
	}

}
