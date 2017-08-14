package de.predic8.model;

import org.apache.camel.Handler;

public class VerifyModel {

    private String corruptedFile;
    private boolean isValid;
    private boolean fileIsMissing;

    public String getCorruptedFile() {
        return corruptedFile;
    }

    public void setCorruptedFile(String corruptedFile) {
        this.corruptedFile = corruptedFile;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isFileIsMissing() {
        return fileIsMissing;
    }

    public void setFileIsMissing(boolean fileIsMissing) {
        this.fileIsMissing = fileIsMissing;
    }

    @Handler
    public VerifyModel getVerifyResults(String fileName) {
        VerifyModel model = new VerifyModel();
        model.setCorruptedFile(fileName);
        model.setValid(fileName.isEmpty());
        model.setFileIsMissing(false);
        return model;
    }
}
