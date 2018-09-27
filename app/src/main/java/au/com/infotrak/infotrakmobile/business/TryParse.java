package au.com.infotrak.infotrakmobile.business;

public class TryParse {
    /*
    Will attempt to pass a given string into a double. Returns 0 if it fails.
     */
    public static Double tryParseDouble(Object obj) {
        Double returnVal;
        try {
            returnVal = Double.parseDouble((String) obj);
        } catch (Exception e) {
            returnVal = 0.0;
        }
        return returnVal;
    }

    /*
    Will attempt to pass a given string into an int. Returns 0 if it fails.
     */
    public static Integer tryParseInt(Object obj) {
        Integer returnVal;
        try {
            returnVal = Integer.parseInt((String) obj);
        } catch (Exception e) {
            returnVal = 0;
        }
        return returnVal;
    }
}
