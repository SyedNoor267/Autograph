package userinterface.responsemodel;

import java.io.Serializable;

public class UserDetailsResponse implements Serializable {

    private String nickname;
    private String password;
    private String facebookid;
    private String twittered;
    private String linkedinid;
    private String xmppid;
    private String name;
    private String surname;
    private String displayName;
    private String age;
    private String birthDate;
    private String sex;
    private String workid;
    private String email;
    private String phoneNumber;
    private String nationId;
    private String img_link;
    private String img_link_thumb;
    private String datacreation;
    private String dataupdate;
    private String status;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFacebookid() {
        return facebookid;
    }

    public void setFacebookid(String facebookid) {
        this.facebookid = facebookid;
    }

    public String getTwittered() {
        return twittered;
    }

    public void setTwittered(String twittered) {
        this.twittered = twittered;
    }

    public String getLinkedinid() {
        return linkedinid;
    }

    public void setLinkedinid(String linkedinid) {
        this.linkedinid = linkedinid;
    }

    public String getXmppid() {
        return xmppid;
    }

    public void setXmppid(String xmppid) {
        this.xmppid = xmppid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getWorkid() {
        return workid;
    }

    public void setWorkid(String workid) {
        this.workid = workid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNationId() {
        return nationId;
    }

    public void setNationId(String nationId) {
        this.nationId = nationId;
    }

    public String getImg_link() {
        return img_link;
    }

    public void setImg_link(String img_link) {
        this.img_link = img_link;
    }

    public String getImg_link_thumb() {
        return img_link_thumb;
    }

    public void setImg_link_thumb(String img_link_thumb) {
        this.img_link_thumb = img_link_thumb;
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
