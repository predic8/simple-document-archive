package de.predic8.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OAuth {

    public Properties properties;
    private InputStream input;

    public OAuth() {
        try {
            properties = new Properties();
            input = new FileInputStream(getClass().getClassLoader().getResource("application.properties").getFile());
            properties.load(input);
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
