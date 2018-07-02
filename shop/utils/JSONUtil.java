package shop.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 *
 * @author Justin
 */
public class JSONUtil {

    public String toJSON(Object o) {
        String s = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            s = mapper.writeValueAsString(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

}
