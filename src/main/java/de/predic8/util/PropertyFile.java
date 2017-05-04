package de.predic8.util;

import java.io.IOException;
import java.util.Properties;

public class PropertyFile {

    private static PropertyFile INSTANCE = null;
    private Properties properties;

    protected PropertyFile() throws IOException {
        properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
    }

    public static String getInstance(String key) {
        if (INSTANCE == null) {
            try {
                INSTANCE = new PropertyFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return INSTANCE.properties.getProperty(key);
    }
}
