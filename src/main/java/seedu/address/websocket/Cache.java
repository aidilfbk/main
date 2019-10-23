package seedu.address.websocket;

import static java.util.Objects.requireNonNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import seedu.address.commons.core.AppSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.commons.util.SimpleJsonUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.module.AcadCalendar;
import seedu.address.model.module.Holidays;
import seedu.address.model.module.Module;
import seedu.address.model.module.ModuleId;
import seedu.address.model.module.ModuleList;
import seedu.address.model.module.ModuleSummaryList;

/**
 * Cache class handles whether to get external data from storage data or api.
 */
public class Cache {
    private static final String writablePath;
    private static final Logger logger = LogsCenter.getLogger(Cache.class);
    private static NusModsApi api = new NusModsApi(AppSettings.DEFAULT_ACAD_YEAR);

    static {
        final URL url = Cache.class.getResource(CacheFileNames.CACHE_FOLDER_PATH);
        if (url.getProtocol().equals("jar")) {
            // we're running from a JAR
            writablePath = Paths.get(System.getProperty("java.io.tmpdir"), "timebook").toString();
        } else {
            writablePath = "src/main/resources/";
        }
        logger.info("Storing cached data in " + writablePath);
    }

    /**
     * Save json to file in cache folder.
     * @param obj obj to save
     * @param pathName file name to save the obj as
     */
    private static void save(Object obj, String pathName) {
        requireNonNull(obj);
        requireNonNull(pathName);

        Path fullPath = Path.of(writablePath, pathName);
        try {
            FileUtil.createIfMissing(fullPath);
            JsonUtil.saveJsonFile(obj, fullPath);
        } catch (IOException e) {
            logger.warning("Failed to save file : " + StringUtil.getDetails(e));
        }
    }

    /**
     * This method is used to save all the API response to a particular JSON file. Only for JSON
     * Object
     * @param key
     * @param value
     * @param filePath
     */
    public static void saveToJson(String key, Object value , String filePath) {
        requireNonNull(key);
        requireNonNull(value);
        requireNonNull(filePath);

        JSONParser parser;
        parser = new JSONParser();

        filePath = Path.of(writablePath, filePath).toString();

        try (Reader reader = new FileReader(filePath)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            if (jsonObject.containsKey(key)) {
                jsonObject.remove(key);
            }
            jsonObject.put(key, value);

            try (FileWriter file = new FileWriter(filePath)) {
                file.write(jsonObject.toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static Reader loadFromCacheBeforeResources(final String path) {
        Reader reader;
        final String cachePath = Path.of(writablePath, path).toString();

        try {
            reader = new FileReader(cachePath);
        } catch (FileNotFoundException e) {
            final InputStream resourceStream = Cache.class.getResourceAsStream(path);
            if (resourceStream == null) {
                return null;
            }

            reader = new InputStreamReader(resourceStream);
        }
        return reader;
    }

    /**
     * This method is used to load a previously called API response
     * @param key
     * @param filePath
     * @return
     */
    public static String loadFromJson(String key, String filePath) {
        requireNonNull(key);
        requireNonNull(filePath);

        JSONParser parser;
        parser = new JSONParser();
        Object result = null;
        Reader reader = loadFromCacheBeforeResources(filePath);
        if (reader == null) {
            return result.toString();
        }

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            result = jsonObject.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * Load json from file in resources
     * @param path file name to load from
     * @return an Optional containing a JSONObject or empty.
     */
    private static Object loadFullPath(String path) {
        requireNonNull(path);
        Object response = null;
        JSONParser parser;
        parser = new JSONParser();

        Reader reader = loadFromCacheBeforeResources(path);
        if (reader == null) {
            return response;
        }

        try {
            response = parser.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Save json to file in resources
     * @param path file name to load from
     */
    private static void saveFullPathJsonArray(String path, JSONArray jsonObject) {
        path = Path.of(writablePath, path).toString();

        try (FileWriter file = new FileWriter(path)) {
            file.write(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load json from file in cache folder
     * @param pathName file name to load from
     * @return an Optional containing a JSONObject or empty.
     */
    private static Optional<Object> load(String pathName) {
        requireNonNull(pathName);
        return Optional.ofNullable(loadFullPath(pathName));
    }

    /**
     * Load holidays from cache, if failed, call api, then save results to cache folder.
     * If api fails too, return empty.
     * @return an Optional containing a Holidays object or empty.
     */
    public static Optional<Holidays> loadHolidays() {
        Optional<Object> objOptional = load(CacheFileNames.HOLIDAYS);
        if (objOptional.isPresent()) {
            return Optional.of(NusModsParser.parseHolidays((JSONArray) objOptional.get()));
        }

        logger.info("Holidays not found in cache, getting from API...");
        Optional<JSONArray> arrOptional = api.getHolidays();
        if (arrOptional.isPresent()) {
            save(arrOptional.get(), CacheFileNames.HOLIDAYS);
            return Optional.of(NusModsParser.parseHolidays(arrOptional.get()));
        }

        logger.warning("Failed to get holidays from API! Adding NUSMods data will not be holiday-aware.");
        return Optional.empty();
    }

    /**
     * Load holidays from cache, if failed, call api, then save results to cache folder.
     * If api fails too, return empty.
     * @return an Optional containing a Holidays object or empty.
     */
    public static Optional<AcadCalendar> loadAcadCalendar() {
        Optional<Object> objOptional = load(CacheFileNames.ACADEMIC_CALENDAR);
        if (objOptional.isPresent()) {
            return Optional.of(NusModsParser.parseAcadCalendar((JSONObject) objOptional.get()));
        }

        logger.info("Academic calendar not found in cache, getting from API...");
        Optional<JSONObject> jsonObjOptional = api.getAcademicCalendar();
        if (jsonObjOptional.isPresent()) {
            save(jsonObjOptional.get(), CacheFileNames.ACADEMIC_CALENDAR);
            return Optional.of(NusModsParser.parseAcadCalendar(jsonObjOptional.get()));
        }

        logger.severe("Failed to get academic calendar from API! Will not be able to add mods to schedules.");
        return Optional.empty();
    }

    /**
     * Load ModuleSummaryList from cache, if failed, return empty.
     * @return an Optional containing a ModuleSummaryList object or empty.
     */
    public static Optional<ModuleSummaryList> loadModuleSummaryList() {
        Optional<Object> objOptional = load(CacheFileNames.MODULES_SUMMARY);
        ModuleSummaryList moduleSummaryList = new ModuleSummaryList();

        JSONObject modulesSummariesJson = new JSONObject();
        if (objOptional.isPresent()) {
            JSONObject moduleSummariesJson = (JSONObject) objOptional.get();
            JSONArray moduleSummariesSingleYear = (JSONArray) moduleSummariesJson.get(api.getAcadYear().toString());
            return Optional.of(NusModsParser.parseModuleSummaryList(moduleSummariesSingleYear, api.getAcadYear()));
        }

        logger.info("Module summaries not found in cache, getting from API...");
        Optional<JSONArray> arrOptional = api.getModuleList();
        if (arrOptional.isPresent()) {
            modulesSummariesJson.put(api.getAcadYear(), arrOptional.get());
            save(modulesSummariesJson, CacheFileNames.MODULES_SUMMARY);
            return Optional.of(NusModsParser.parseModuleSummaryList(arrOptional.get(), api.getAcadYear()));
        }

        logger.severe("Failed to module summaries from API!");
        return Optional.empty();
    }

    /**
     * Load modulelist from cache, if failed, return empty.
     * @return an Optional containing a ModuleList object or empty.
     */
    public static Optional<ModuleList> loadModuleList() {
        Optional<Object> objOptional = load(CacheFileNames.MODULES);
        ModuleList moduleList = new ModuleList();

        if (objOptional.isPresent()) { // found cached file
            JSONObject modulesJson = (JSONObject) objOptional.get();
            for (Object key : modulesJson.keySet()) {
                JSONObject moduleJson = (JSONObject) modulesJson.get(key);
                Module module = NusModsParser.parseModule(moduleJson);
                moduleList.addModule(module);
            }
            return Optional.of(moduleList);
        }

        logger.warning("No modules in cache. Will be calling from API for each module.");
        return Optional.empty();
    }

    /**
     * Load a module from cache, if failed, call api, then save results to cache folder.
     * If api fails too, return empty.
     * @return an Optional containing a Module object or empty.
     */
    public static Optional<Module> loadModule(ModuleId moduleId) {
        Optional<Object> objOptional = load(CacheFileNames.MODULES);

        JSONObject modulesJson = new JSONObject();
        if (objOptional.isPresent()) { // found cached file
            modulesJson = (JSONObject) objOptional.get();
            if (modulesJson.containsKey(moduleId.toString())) { // found moduleId in cached file
                JSONObject moduleJson = (JSONObject) modulesJson.get(moduleId.toString());
                return Optional.of(NusModsParser.parseModule(moduleJson));
            }
        }

        logger.info("Module " + moduleId + " not found in cache, getting from API...");
        Optional<JSONObject> jsonObjFromApiOptional = api.getModule(moduleId.getModuleCode());
        if (jsonObjFromApiOptional.isPresent()) { // found module from API
            Module module = NusModsParser.parseModule(jsonObjFromApiOptional.get());
            modulesJson.put(module.getModuleId(), jsonObjFromApiOptional.get());
            save(modulesJson, CacheFileNames.MODULES);
            return Optional.of(module);
        }

        logger.severe("Failed to get module from API! Unable to add mod to schedules.");
        return Optional.empty();
    }

    /**
     * Load a module from cache, if failed, call api, then save results to cache folder.
     * If api fails too, return empty.
     * @return an Optional containing a Module object or empty.
     */
    public static JSONArray loadVenues() {
        JSONArray venues = (JSONArray) loadFullPath(CacheFileNames.VENUES_FULL_PATH);

        if (venues != null) {
            return venues;
        } else {
            logger.info("Module not found in cache, getting from API...");
            venues = api.getVenues("/1").orElse(new JSONArray());
            saveFullPathJsonArray(CacheFileNames.VENUES_FULL_PATH, venues);
        }

        return venues;
    }
}
