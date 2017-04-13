package de.predic8.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFile {

    private static PropertyFile INSTANCE = null;
    private Properties properties;

    protected PropertyFile() throws IOException {
        properties = new Properties();
        properties.load(getClass().getResourceAsStream("application.properties"));
    }

    public static PropertyFile getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new PropertyFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return INSTANCE;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
