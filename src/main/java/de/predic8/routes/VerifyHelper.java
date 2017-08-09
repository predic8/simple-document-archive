package de.predic8.routes;

import org.apache.camel.Handler;

public class VerifyHelper {

    private static VerifyHelper instance = new VerifyHelper();

    private boolean blocked = true;
    private boolean isValid;
    private String filename;

    public void reset() {
        this.blocked = true;
    }

    private VerifyHelper() {}

    public static VerifyHelper getInstance() {
        return instance;
    }

    public boolean isBlocked() {
        return blocked;
    }

    @Handler
    public void receiveData(String filename) {
        this.blocked = false;
        System.out.println("called with: ");
        System.out.println("fileName: " + filename);
        this.isValid = filename.isEmpty() ? true : false;
        System.out.println("isvalid?: " + isValid);
        this.filename = filename;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getFilename() {
        return filename;
    }
}
