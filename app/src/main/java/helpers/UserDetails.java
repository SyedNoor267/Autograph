package helpers;

import android.app.ProgressDialog;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDetails implements Serializable {
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("name")
    private String name;
    @SerializedName("surname")
    private String surname;
    @SerializedName("displayName")
    private String displayName;
    @SerializedName("age")
    private int age;
    @SerializedName("birthDate")
    private String birthDate;
    @SerializedName("sex")
    private String sex;
    @SerializedName("workId")
    private String workid;
    @SerializedName("email")
    private String email;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("nationId")
    private String nationId;
    @SerializedName("image_link")
    private String imageLink;
    @SerializedName("img_link_thumb")
    private String imageLinkThumb;
    @SerializedName("datacreation")
    private String dataCreation;
    @SerializedName("dataupdate")
    private String dataUpdate;
    @SerializedName("status")
    private String status;

    public String getNickname(){
        return nickname;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public String getName(){
        return name;
    }

    public void setName(String lastName){
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageLinkThumb() {
        return imageLinkThumb;
    }

    public void setImageLinkThumb(String imageLinkThumb) {
        this.imageLinkThumb = imageLinkThumb;
    }

    public String getDataCreation() {
        return dataCreation;
    }

    public void setDataCreation(String dataCreation) {
        this.dataCreation = dataCreation;
    }

    public String getDataUpdate() {
        return dataUpdate;
    }

    public void setDataUpdate(String dataUpdate) {
        this.dataUpdate = dataUpdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
