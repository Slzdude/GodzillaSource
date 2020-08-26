package core.ui.component.model;

public class DbInfo {
    private String dbType = "";
    private String dbHost = "";
    private int dbPort = 0;
    private String dbUserName = "";
    private String dbPassword = "";

    public DbInfo() {
    }

    public String getDbType() {
        return this.dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbHost() {
        return this.dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public int getDbPort() {
        return this.dbPort;
    }

    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbUserName() {
        return this.dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return this.dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String toString() {
        return "DbInfo [dbType=" + this.dbType + ", dbHost=" + this.dbHost + ", dbPort=" + this.dbPort + ", dbUserName=" + this.dbUserName + ", dbPassword=" + this.dbPassword + "]";
    }
}
