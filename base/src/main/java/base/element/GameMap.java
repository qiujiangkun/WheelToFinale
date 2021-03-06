package base.element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class GameMap {
    private Map<String, Object> values;
    private String mapname;

    private GameMap() {
    }
    public static GameMap getEmptyMap(String mapname, int height, int width) {
        GameMap gameMap = new GameMap();
        gameMap.mapname = mapname;
        gameMap.values.put("height", height);
        gameMap.values.put("width", width);
        return gameMap;
    }


    public static GameMap loadMap(String filename) throws IOException {
        File file = new File(filename);
        InputStream inputStream = new FileInputStream(file);
//        byte[] bytes = inputStream.readAllBytes();
//        GameMap map = SerializeUtil.deserializer(bytes);
        return (GameMap) null;
    }


    public Cities getCity() {
        return (Cities) values.get("cities");
    }

    public Heroes getHeroes() {
        return (Heroes) values.get("heroes");
    }

    public Landscape getLandscape() {
        return (Landscape) values.get("landscape");
    }

    public String getMapname() {
        return mapname;
    }

    public GameMap setMapname(String mapname) {
        this.mapname = mapname;
        return this;
    }


}
