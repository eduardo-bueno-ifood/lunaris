package dev.tricht.lunaris.util;

import dev.tricht.lunaris.settings.event.PropertyListener;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class PropertiesManager {

    private static File file;
    private static Properties properties = new Properties();

    public static final String LEAGUE = "LEAGUE";
    public static final String CHARACTER_NAME = "CHARACTER_NAME";
    public static final String POESESSID = "POESESSID";

    private static HashMap<String, PropertyListener> propertyListeners = new HashMap<>();

    public static void load() {
        file = DirectoryManager.getFile("lunaris.properties");
        try {
            file.createNewFile();
        } catch (IOException e) {
            ErrorUtil.showErrorDialogAndExit("Unable to create lunaris.properties file");
        }
        try {
            InputStream in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException e) {
            ErrorUtil.showErrorDialogAndExit("Unable to read lunaris.properties");
        }
        setDefaults();
    }

    private static void setDefaults() {
        if (!PropertiesManager.containsKey("keybinds.price_check")) {
            PropertiesManager.writeProperty("keybinds.price_check", "Alt+D");
        }
        if (!PropertiesManager.containsKey("keybinds.search_trade")) {
            PropertiesManager.writeProperty("keybinds.search_trade", "Alt+Q");
        }
        if (!PropertiesManager.containsKey("keybinds.item_info")) {
            PropertiesManager.writeProperty("keybinds.item_info", "Alt+A");
        }
        if (!PropertiesManager.containsKey("keybinds.hideout")) {
            PropertiesManager.writeProperty("keybinds.hideout", "F5");
        }
        if (!PropertiesManager.containsKey("keybinds.wiki")) {
            PropertiesManager.writeProperty("keybinds.wiki", "Alt+W");
        }
        if (!PropertiesManager.containsKey("keybinds.kick")) {
            PropertiesManager.writeProperty("keybinds.kick", "F4");
        }
        if (!PropertiesManager.containsKey("keybinds.invite_last_whisper")) {
            PropertiesManager.writeProperty("keybinds.invite_last_whisper", "F6");
        }
        if (!PropertiesManager.containsKey("keybinds.enable_stash_scroll")) {
            PropertiesManager.writeProperty("keybinds.enable_stash_scroll", "1");
        }
        if (!PropertiesManager.containsKey("trade_search.pseudo_mods")) {
            PropertiesManager.writeProperty("trade_search.pseudo_mods", "1");
        }
        if (!PropertiesManager.containsKey("trade_search.range_search")) {
            PropertiesManager.writeProperty("trade_search.range_search", "1");
        }
        if (!PropertiesManager.containsKey("trade_search.range_search_percentage")) {
            PropertiesManager.writeProperty("trade_search.range_search_percentage", "20");
        }
        if (!PropertiesManager.containsKey("trade_search.range_search_only_min")) {
            PropertiesManager.writeProperty("trade_search.range_search_only_min", "1");
        }
        if (!PropertiesManager.containsKey("trade_search.poe_ninja")) {
            PropertiesManager.writeProperty("trade_search.poe_ninja", "1");
        }
        if (!PropertiesManager.containsKey("trade_search.poeprices")) {
            PropertiesManager.writeProperty("trade_search.poeprices", "1");
        }
        if (!PropertiesManager.containsKey("map_mod_warnings.ele_refl")) {
            PropertiesManager.writeProperty("map_mod_warnings.ele_refl", "1");
        }
        if (!PropertiesManager.containsKey("map_mod_warnings.phys_refl")) {
            PropertiesManager.writeProperty("map_mod_warnings.phys_refl", "1");
        }
        if (!PropertiesManager.containsKey("map_mod_warnings.no_leech")) {
            PropertiesManager.writeProperty("map_mod_warnings.no_leech", "1");
        }
        if (!PropertiesManager.containsKey("map_mod_warnings.no_regen")) {
            PropertiesManager.writeProperty("map_mod_warnings.no_regen", "1");
        }
        if (!PropertiesManager.containsKey("map_mod_warnings.multi_dmg")) {
            PropertiesManager.writeProperty("map_mod_warnings.multi_dmg", "1");
        }
        if (!PropertiesManager.containsKey("map_mod_warnings.tmp_chains")) {
            PropertiesManager.writeProperty("map_mod_warnings.tmp_chains", "1");
        }
        if (!PropertiesManager.containsKey("map_mod_warnings.low_recovery")) {
            PropertiesManager.writeProperty("map_mod_warnings.low_recovery", "1");
        }
        if (!PropertiesManager.containsKey("map_mod_warnings.avoid_elemental_ailments")) {
            PropertiesManager.writeProperty("map_mod_warnings.avoid_elemental_ailments", "0");
        }
        if (!PropertiesManager.containsKey("map_mod_warnings.avoid_poison_blind_and_bleed")) {
            PropertiesManager.writeProperty("map_mod_warnings.avoid_poison_blind_and_bleed", "0");
        }
    }

    public static HashMap<String, String> getAllPropertiesMatching(String regex) {
        HashMap<String, String> props = new HashMap<>();

        for (Object key : properties.keySet()) {
            if(((String)key).matches(regex)) {
                props.put((String)key, getProperty((String)key));
            }
        }

        return props;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultVal) {
        if (properties.containsKey(key)) {
            return getProperty(key);
        }
        return defaultVal;
    }

    public static void writeProperty(String key, String value) {
        properties.setProperty(key, value);
        try {
            properties.store(new FileOutputStream(file), null);
        } catch (IOException ex) {
            ErrorUtil.showErrorDialogAndExit("Unable to save lunaris.properties");
        }

        for (Map.Entry<String, PropertyListener> listener : propertyListeners.entrySet()) {
            if (key.matches(listener.getKey())) {
                listener.getValue().onPropertyChange();
            }
        }
    }

    public static boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    public static void addPropertyListener(String propMask, PropertyListener listener) {
        propertyListeners.put(propMask, listener);
    }

}
