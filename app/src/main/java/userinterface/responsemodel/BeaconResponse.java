package userinterface.responsemodel;

public class BeaconResponse {


    private String nickname;
    private String UUID;
    private String major;
    private String minor;
    private String description;
    private String datacreation;
    private String dataupdate;
    private String status;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatacreation() {
        return datacreation;
    }

    public void setDatacreation(String datacreation) {
        this.datacreation = datacreation;
    }

    public String getDataupdate() {
        return dataupdate;
    }

    public void setDataupdate(String dataupdate) {
        this.dataupdate = dataupdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
