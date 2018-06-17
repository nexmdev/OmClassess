package ltd.akhbod.omclasses.ModalClasses;

import android.provider.ContactsContract;

/**
 * Created by ibm on 07-06-2018.
 */

public class ProfileDetails {

    String name,address,school,imageUrl,mobNo,id;

    public ProfileDetails(){

    }

    public ProfileDetails(String name, String address, String school, String imageUrl, String mobNo, String id) {
        this.name = name;
        this.address = address;
        this.school = school;
        this.imageUrl = imageUrl;
        this.mobNo = mobNo;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMobNo() {
        return mobNo;
    }

    public void setMobNo(String mobNo) {
        this.mobNo = mobNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
