package de.predic8;

import de.predic8.util.PropertyFile;

public interface Endpoints {

    /**
     * Smtp endpoint
     */
    String smtp = String.format("smtp://%s?password=%s&username=%s&to=%s&from=%s"
            , PropertyFile.getInstance("email_smtp")
            , PropertyFile.getInstance("email_password")
            , PropertyFile.getInstance("email_username")
            , PropertyFile.getInstance("email_recipient")
            , PropertyFile.getInstance("email_username"));

    /**
     * Files and Folder enpoints
     */
    String archiveFolder = "file:document-archive/archive?fileName=${property.fileName}";
    String logFile = "file:document-archive/logs?fileExist=Append&fileName=log.txt";
    String notifyFile = "file:document-archive/notify?fileExist=Append&fileName=new_files.txt";

    /**
     * Freemarker endpoints
     */
    String dailyMailFM = "freemarker:/email-templates/daily_report.ftl";
    String fileNotFoundFM = "freemarker:/email-templates/file_not_found.ftl";
    String verifyFailedFM = "freemarker:/email-templates/verify_fail.ftl";
    String verifyOkFM = "freemarker:/email-templates/verify_ok.ftl";
}
