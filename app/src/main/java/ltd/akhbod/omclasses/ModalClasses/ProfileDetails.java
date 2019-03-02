package ltd.akhbod.omclasses.ModalClasses;

/**
 * Created by ibm on 07-06-2018.
 */

public class ProfileDetails {

    String name,address,school,imageUrl,mobNo,id,fee,feedetails;

    public ProfileDetails(){

    }

    public ProfileDetails(String name, String address, String school, String imageUrl, String mobNo, String id,String mFee,String mdetails) {
        this.name = name;
        this.address = address;
        this.school = school;
        this.imageUrl = imageUrl;
        this.mobNo = mobNo;
        this.id = id;
        this.fee = mFee;
        this.feedetails = mdetails;
    }

    public String getFeedetails() {
        return feedetails;
    }

    public void setFeedetails(String feedetails) {
        this.feedetails = feedetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getFee(){return fee;}

    public void setFee(String mfee) {
        this.fee = mfee;
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
