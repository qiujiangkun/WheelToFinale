package client.core;

import base.reactor.Reactor;
import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ModLauncher implements ModuleBase {
    private static List<GameModuleInfo> gameModuleInfos = new ArrayList<>();
    private static Map<Class, ModuleBase> enabledModules = new HashMap<>();
    private static String GAME_MOD_PATH = "modules";
    private static Reactor rootReactor = new Reactor();

    private static ModClassLoader loader = new ModClassLoader();

    public ModLauncher() {
        Thread.currentThread().setContextClassLoader(ModLauncher.getLoader());
    }

    public static void generateDefaultJson() throws IOException {
        Gson gson = new Gson();
        FileWriter writer = new FileWriter(joinPath(GAME_MOD_PATH, "default.json"));
        gson.toJson(new GameModuleInfo(), writer);
        writer.close();

    }

    public static void loadModulesList() throws IOException {
        File file = new File(GAME_MOD_PATH);
        File[] modules = file.listFiles(File::isDirectory);
        assert modules != null;
        for(File e : modules){
            GameModuleInfo gameModuleInfo = loadModuleInfo(e.getName());
            if (gameModuleInfo != null) {
                gameModuleInfos.add(gameModuleInfo);
                System.out.println("loaded info of " + gameModuleInfo.getName() + " version " + gameModuleInfo.getVersion());
            }

        }
    }

    public static String joinPath(String... paths) {
        if (paths.length == 0) {
            return ".";
        } else if (paths.length == 1) {
            return paths[0];
        } else {
            StringBuilder path = new StringBuilder(paths[0]);
            for (int i = 1; i < paths.length; i++) {
                String apath = paths[i];
                if (!apath.endsWith("/"))
                    path.append(File.separator);
                path.append(apath);
            }
            return path.toString();
        }

    }

    public static GameModuleInfo loadModuleInfo(String moduleName) throws FileNotFoundException {
        String confPath = joinPath(GAME_MOD_PATH, moduleName, moduleName + ".json");
        FileReader reader = new FileReader(confPath);
        Gson gson = new Gson();
        return gson.fromJson(reader, GameModuleInfo.class);
    }

    public static Class<?> loadClass(GameModuleInfo gameModuleInfo) {
        String jarPath = joinPath(GAME_MOD_PATH, gameModuleInfo.getName());
        return addJarPath(jarPath, gameModuleInfo.getMainClass());
    }


    public static Class<?> addJarPath(String pathname, String processorName) {
        File modulePath = new File(pathname);
        File[] files = modulePath.listFiles((dir, name) -> name.endsWith(".jar"));
        for (File file : files != null ? files : new File[0]) {
            try {
                loader.addURL(file.toURI().toURL());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            return loader.loadClass(processorName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ModuleBase loadMod(GameModuleInfo gameModuleInfo) {
        if (!gameModuleInfo.isEnabled())
            return null;
        Class<?> loadClass;
        try {
            loadClass = Class.forName(gameModuleInfo.getMainClass());
        } catch (ClassNotFoundException e) {
            loadClass = loadClass(gameModuleInfo);
        }
        System.out.println(loadClass);

        ModuleBase instance = enabledModules.get(loadClass);

        if (loadClass != null && instance == null) {
            try {
                instance = (ModuleBase) loadClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                System.out.flush();
                e.printStackTrace();
                return null;
            }
            enabledModules.put(loadClass, instance);
            instance.init(rootReactor);
        }
        return instance;
    }


    public static List<GameModuleInfo> getGameModuleInfos() {
        return gameModuleInfos;
    }

    public static Map<Class, ModuleBase> getEnabledModules() {
        return enabledModules;
    }

    public static Reactor getRootReactor() {
        return rootReactor;
    }

    @Override
    public void init(Reactor reactor) {
        // nothing to do
    }

    public static void loadAllModules() throws IOException {
        loadModulesList();
        List<GameModuleInfo> list = getGameModuleInfos();
        new DependencesSolver(list).getSorted().forEach(ModLauncher::loadMod);

    }

    public static URLClassLoader getLoader() {
        return loader;
    }

    public static void main(String[] args) throws IOException {
        generateDefaultJson();
        loadAllModules();
        getRootReactor().submitEvent(new StartClientEvent());
    }
}
