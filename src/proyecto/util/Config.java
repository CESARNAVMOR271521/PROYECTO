package proyecto.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String CONFIG_FILE = "barberia_config.properties";
    private static Properties props = new Properties();

    static {
        load();
    }

    public static void load() {
        File f = new File(CONFIG_FILE);
        if (f.exists()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                props.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Defaults
            set("nombre_negocio", "CHUPIRULES");
            set("direccion", "Calle Principal #123");
            set("telefono", "555-0000");
            set("mensaje_ticket", "¡Gracias por su preferencia!");
        }
    }

    public static String get(String key) {
        return props.getProperty(key, "");
    }
    
    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static void set(String key, String value) {
        props.setProperty(key, value);
        save();
    }

    private static void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Configuracion Barberia System");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
