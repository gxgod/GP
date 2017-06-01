package com.example.gxgod.graduationproject.entry;

/**
 * Created by Administrator on 2017/5/28 0028.
 */
public class ReportedEntry {
    private AlbumEntry albumEntry;
    private int reportId;
    private String reporterName;

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public AlbumEntry getAlbumEntry() {
        return albumEntry;
    }

    public void setAlbumEntry(AlbumEntry albumEntry) {
        this.albumEntry = albumEntry;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }
}
