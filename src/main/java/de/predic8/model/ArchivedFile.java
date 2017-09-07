package de.predic8.model;

import javax.persistence.*;

@Entity
@Table(name = "Archive")
public class ArchivedFile {

    @Id
    @GeneratedValue
    Long id;

    //@JsonDeserialize(using = LocalDateDeserializer.class)
    //@JsonSerialize(using = LocalDateSerializer.class)
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date")
    //LocalDate date;
    String date;

    @Column(name = "time")
    //LocalTime time;
    String time;

    @Column(name = "filename")
    String fileName;

    @Column(name = "hash")
    String hash;

    @Column(name = "belegnr")
    String belegnr;

    @Column(name = "description")
    String description;

    String path;
    String totalFileName;

    @Override
    public String toString() {
        return "ArchivedFile{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", fileName='" + fileName + '\'' +
                ", hash='" + hash + '\'' +
                ", belegnr='" + belegnr + '\'' +
                ", description='" + description + '\'' +
                ", path='" + path + '\'' +
                ", totalFileName='" + totalFileName + '\'' +
                '}';
    }

    public ArchivedFile() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTotalFileName() {
        return totalFileName;
    }

    public void setTotalFileName(String totalFileName) {
        this.totalFileName = totalFileName;
    }

    public String getBelegnr() {
        return belegnr;
    }

    public void setBelegnr(String belegnr) {
        this.belegnr = belegnr;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
