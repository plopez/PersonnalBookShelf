package plo.dbbd.server.helpers;

import org.codehaus.jackson.map.ObjectMapper;

public class JSonMapper {

    public static String generateJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonText = "";
        try {
            jsonText = mapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonText;
    }

}
