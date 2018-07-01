package ltd.akhbod.omclasses.ModalClasses;

/**
 * Created by ibm on 07-06-2018.
 */

public class RecordDetails {

    String subject,presenty,marks;
    Boolean isSmsSent;

    public RecordDetails(){

    }

    public RecordDetails(String subject, String presenty, String marks,Boolean isSmsSent) {
        this.subject = subject;
        this.presenty = presenty;
        this.marks = marks;
        this.isSmsSent=isSmsSent;
    }

    public Boolean getIsSmsSent() {
        return isSmsSent;
    }

    public void setIsSmsSent(Boolean smsSent) {
        this.isSmsSent = smsSent;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPresenty() {
        return presenty;
    }

    public void setPresenty(String presenty) {
        this.presenty = presenty;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }
}
