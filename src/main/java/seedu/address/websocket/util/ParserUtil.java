package seedu.address.websocket.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Parser Utilities
 */
public class ParserUtil {

    /**
     * Converts a String into a JSONObject.
     *
     * @param s String to be converted
     * @return JSONObject
     */
    public static JSONObject parseStringToJsonObject(String s) {
        JSONObject obj = null;
        try {
            JSONParser parser = new JSONParser();
            obj = (JSONObject) parser.parse(s);
        } catch (ParseException pe) {
            System.out.println("Failed to parse JSON object");
        }
        return obj;
    }
}