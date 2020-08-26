package core.ui.component.model;

public class FileOpertionInfo {
    private String srcFileName;
    private String destFileName;
    private Boolean opertionStatus;

    public FileOpertionInfo() {
    }

    public String getSrcFileName() {
        return this.srcFileName;
    }

    public void setSrcFileName(String srcFileName) {
        this.srcFileName = srcFileName;
    }

    public String getDestFileName() {
        return this.destFileName;
    }

    public void setDestFileName(String destFileName) {
        this.destFileName = destFileName;
    }

    public Boolean getOpertionStatus() {
        return this.opertionStatus;
    }

    public void setOpertionStatus(Boolean opertionStatus) {
        this.opertionStatus = opertionStatus;
    }
}
