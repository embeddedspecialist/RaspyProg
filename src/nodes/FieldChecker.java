/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package nodes;

import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *Questa classe contiene alcuni metodi statici per il controllo di diversi tipi di campi
 * @author amirrix
 */
public class FieldChecker {

    @SuppressWarnings("unchecked")
    static public boolean checkSerialNumber(String deviceName, String serialNumber){

        HashMap nameFamilyMap = new HashMap();

        try {    
            //Costruisco la mappa dispositivi/numeri di serie
            nameFamilyMap.put("DS18S20", "10");
            nameFamilyMap.put("DS18B20", "28");
            nameFamilyMap.put("DS2438", "26");
            nameFamilyMap.put("DS2890", "2C");
            nameFamilyMap.put("DS2408", "29");
            nameFamilyMap.put("DS2751", "51");
        }
        catch (Exception ex){
            return false;
        }

        if (serialNumber.length() != 16){
            JOptionPane.showMessageDialog(null,"Lunghezza numero di serie errata", "Errore",JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!checkValidHexString(serialNumber)){
            JOptionPane.showMessageDialog(null,
                    "Il nuemro di Serie NON e' esadecimale", "Errore",JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //Controllo se driver e numero di famiglia corrispondono
        try {
            if ((deviceName.equals("DS18S20")) || (deviceName.equals("DS18B20")) ) {
                if ((!serialNumber.substring(14).equals("10")) &&
                    (!serialNumber.substring(14).equals("28")) ) {
                    JOptionPane.showMessageDialog(null,
                        "Il numero della famiglia del dispositivo non corrisponde al tipo", "Errore",JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            else if (!nameFamilyMap.get(deviceName).equals(serialNumber.toUpperCase().substring(14))) {
                JOptionPane.showMessageDialog(null,
                        "Il numero della famiglia del dispositivo non corrisponde al tipo", "Errore",JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        catch (Exception ex){
            JOptionPane.showMessageDialog(null,
                        "Il dispositivo "+deviceName+" non e' riconosciuto", "Errore",JOptionPane.ERROR_MESSAGE);
                return false;
        }

        return true;
    }

    public static boolean checkValidHexString(String stringToCheck){
        char []myString = stringToCheck.toCharArray();

        for (int i =0; i < myString.length; i++) {
            char current_char = myString[i];
            boolean is_hex_char = (current_char >= '0' && current_char <= '9') ||
                           (current_char >= 'a' && current_char <= 'f') ||
                           (current_char >= 'A' && current_char <= 'F');

            if (!is_hex_char) {
                return false;
            }
        }

        return true;
    }

    public static boolean checkValidAddress(String address){
        if (address.length() == 0){
            JOptionPane.showMessageDialog(null,
                        "Indirizzo nullo", "Errore",JOptionPane.ERROR_MESSAGE);
                return false;
        }

        if (Integer.parseInt(address) < 0){
            JOptionPane.showMessageDialog(null,
                        "L'indirizzo deve essere maggiore o uguale a 0", "Errore",JOptionPane.ERROR_MESSAGE);
                return false;
        }

        return true;
    }

    public static boolean checkStringIsNumeric(String stringToCheck){
        try {
            Float.parseFloat(stringToCheck);
            return true;
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(null,
                        "Inserire un numero", "Errore",JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static boolean checkStringIsNumeric(String stringToCheck, String errorMessage){
        try {
            Float.parseFloat(stringToCheck);
            return true;
        }
        catch(Exception ex){
            JOptionPane.showMessageDialog(null,
                        errorMessage, "Errore",JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    static public boolean isValidIP(String ipStringToCheck, String errorMessage) {
        boolean correctFormat = ipStringToCheck.matches("^[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}$");

        if (correctFormat) {
            boolean validIp = true;

            String[] values = ipStringToCheck.split("\\.");
            for (int k = 0; k < values.length; ++k) {
                short v = Short.valueOf(values[k]).shortValue();
                if ((v < 0) || (v > 255)) {
                    validIp = false;
                    break;
                }
            }

            if (!validIp){
                JOptionPane.showMessageDialog(null, errorMessage,"Errore!",JOptionPane.ERROR_MESSAGE);
            }
            
            return validIp;
        }
        else {
            JOptionPane.showMessageDialog(null, errorMessage,"Errore!",JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    static public boolean isValidIP(String ipStringToCheck) {
        boolean correctFormat = ipStringToCheck.matches("^[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}$");

        if (correctFormat) {
            boolean validIp = true;

            String[] values = ipStringToCheck.split("\\.");
            for (int k = 0; k < values.length; ++k) {
                short v = Short.valueOf(values[k]).shortValue();
                if ((v < 0) || (v > 255)) {
                    validIp = false;
                    break;
                }
            }

            return validIp;
        }

        return false;
    }

}
