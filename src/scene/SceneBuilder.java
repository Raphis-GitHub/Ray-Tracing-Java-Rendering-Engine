package scene;

import lighting.AmbientLight;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import primitives.Color;

import java.io.FileReader;
import java.io.IOException;

import static parser.SceneDescriptor.*;

/**
 * JsonScene is a utility class that provides methods to create a Scene object from a JSON file.
 * It parses the JSON data and constructs the corresponding geometries, lights, and other scene elements.
 */
public class SceneBuilder {
    /**
     * Creates a Scene object from a JSON file.
     *
     * @param path the path to the JSON file
     * @return a Scene object constructed from the JSON data
     * @throws IOException    if there is an error reading the file
     * @throws ParseException if there is an error parsing the JSON data
     */
    public static Scene CreateScene(String path) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(path));
        JSONObject sceneObj = (JSONObject) jsonObject.get("scene");

        String name = (String) sceneObj.get("name");
        Scene scene = new Scene(name);
        if (sceneObj.containsKey("background-color"))
            scene.setBackground(parseColor((String) sceneObj.get("background-color")));
        if (sceneObj.containsKey("ambient-light")) {
            JSONObject ambientLightObj = (JSONObject) sceneObj.get("ambient-light");
            Color ambientLight = parseColor((String) ambientLightObj.get("color"));
            scene.setAmbientLight(new AmbientLight(ambientLight));
        }
        if (sceneObj.containsKey("geometries")) {
            JSONArray materials = (JSONArray) sceneObj.get("materials");
            scene.geometries = parseGeometries((JSONArray) sceneObj.get("geometries"), materials);
        }
        if (sceneObj.containsKey("lights"))
            scene.setLightSources(parseLights((JSONArray) sceneObj.get("lights")));

        return scene;
    }
}