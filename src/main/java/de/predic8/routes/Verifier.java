package de.predic8.routes;

import org.apache.log4j.Logger;

import java.io.File;

public class Verifier {

    final static Logger logger = Logger.getLogger(Verifier.class);

    private String logPath = "document-archive/logs/log.txt";


    private String lastHash = "123";
    private boolean getFirst = true;
    private String corruptedFile = "";

    public Verifier() {

    }

    private boolean logExists() {
        return new File(logPath).exists();
    }

    public void valuateLog() {
        if (logExists()) {

        }
    }

    public static void main(String[] args) {
        Verifier v = new Verifier();
        System.out.println(v.logExists());
    }
}
