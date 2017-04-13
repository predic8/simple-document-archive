package de.predic8.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFile {

    private static final PropertyFile INSTANCE = new PropertyFile();
    private static final Properties PROPERTIES = new Properties();

    private PropertyFile() {}

    public static PropertyFile getInstance() {
        InputStream input = null;
        if (INSTANCE == null) {
            try {
                input = new FileInputStream(PropertyFile.class.getClassLoader().getResource("application.properties").getFile());
                PROPERTIES.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return INSTANCE;
    }
}
