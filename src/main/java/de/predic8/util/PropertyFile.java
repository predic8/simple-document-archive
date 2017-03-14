package de.predic8.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFile {

    public Properties prop;
    private InputStream input;

    public PropertyFile(String fileName) {
        try {
            prop = new Properties();
            input = new FileInputStream(getClass().getClassLoader().getResource(fileName).getFile());
            prop.load(input);
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
}
