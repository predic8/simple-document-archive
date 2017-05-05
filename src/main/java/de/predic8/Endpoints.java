package de.predic8;

import de.predic8.util.PropertyFile;

public class Endpoints {

    /**
     * Smtp endpoint
     */
    public static final String smtp = String.format("smtp://%s?password=%s&username=%s&to=%s&from=%s"
            , PropertyFile.getInstance("email_smtp")
            , PropertyFile.getInstance("email_password")
            , PropertyFile.getInstance("email_username")
            , PropertyFile.getInstance("email_recipient")
            , PropertyFile.getInstance("email_username"));

    /**
     * Files and Folder enpoints
     */
    public static final String archiveFolder = "file:document-archive/archive?fileName=${property.fileName}";
    public static final String logFile = "file:document-archive/logs?fileExist=Append&fileName=log.txt";
    public static final String notifyFile = "file:document-archive/notify?fileExist=Append&fileName=new_files.txt";

    /**
     * Freemarker endpoints
     */
    public static final String dailyMailFM = "freemarker:/email-templates/daily_report.ftl";
    public static final String fileNotFoundFM = "freemarker:/email-templates/file_not_found.ftl";
    public static final String verifyFailedFM = "freemarker:/email-templates/verify_fail.ftl";
    public static final String verifyOkFM = "freemarker:/email-templates/verify_ok.ftl";
}
