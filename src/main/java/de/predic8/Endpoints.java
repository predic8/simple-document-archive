package de.predic8;

public interface Endpoints {

    /**
     * Smtp endpoint
     */
    String smtp = "smtp://{{email_smtp}}?password={{email_password}}&username={{email_username}}&to={{email_recipient}}&from={{email_username}}";

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
