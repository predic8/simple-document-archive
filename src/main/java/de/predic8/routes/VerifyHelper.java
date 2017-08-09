package de.predic8.routes;

import org.apache.camel.Handler;

public class VerifyHelper {

    private static VerifyHelper instance = new VerifyHelper();

    private String status = "waiting";
    private boolean blocked = true;

    private boolean isValid;
    private String filename;

    public void reset() {
        this.status = "waiting";
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
        this.isValid = filename.isEmpty() ? true : false;
        this.filename = filename;
        this.status = "hashError";
    }

    public String getStatus() {
        return status;
    }

    public void fileNotFound(String filename) {
        this.blocked = false;
        this.status = "fileNotFound";
        this.filename = filename;
        this.isValid = false;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getFilename() {
        return filename;
    }
}
