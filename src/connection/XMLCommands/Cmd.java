package connection.XMLCommands;

import java.util.*;

public class Cmd {
	
	private String cmd;
	private HashMap<String,String> hm;
	private String add;
	
	/**
	 * Costruttore
	 * @param cmd Nome del comando
	 */
	public Cmd(String cmd){
		this.cmd=cmd;
		hm = new HashMap<String,String>();
		add = "";
	}
	
	/**
	 * Costruttore
	 * @param cmd Nome del tag
	 * @param hm Hash con attributo/Valore
	 */
	public Cmd(String cmd,HashMap<String,String> hm){
		this.cmd=cmd;
		this.hm=hm;
		add = "";
	}
	
	/**
	 * Ritorna il nome del comando
	 * @return Comando
	 */
	public String getCmd(){
		return cmd;
	}
	
	/**
	 * Ritorna il valore di un attributo
	 * @param key Attributo
	 * @return Valore
	 */
	public String getValue(String key){
		return hm.get(key);
	}
	
	/**
	 * Imposta il valore di un attributo
	 * @param key Attributo
	 * @param value Valore
	 */
	public void putValue(String key, String value){
		hm.put(key,value);
	}
	
	/**
	 * Ritorna true se un attributo e' gie' incluso
	 * @param key Attributo
	 * @return true se presente
	 */
	public boolean containsAttribute(String key){
		return hm.containsKey(key);
	}
	
	/**
	 * Ritorna la stringa xml corrispondente a cmd
	 * @return String xml
	 */
	public String getXMLValue(){
		String s = "<"+cmd+" ";
		Iterator it = hm.keySet().iterator();
		while(it.hasNext()){
			String key = (String)it.next();
			s += key+"=\"";
			s += hm.get(key)+"\" ";
		}
		s += add+" ";
		s += "/>";
		return s;
	}
	
	/**
	 * Aggiungo una stringa al comando xml
	 * @param s Stringa da aggiungere
	 */
	public void addPart(String s){
		if (s == null) add = "";
		else add = s;
	}
	
	public String toString(){
		return getXMLValue();
	}

}
