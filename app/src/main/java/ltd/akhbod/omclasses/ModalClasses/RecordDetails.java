package ltd.akhbod.omclasses.ModalClasses;

/**
 * Created by ibm on 07-06-2018.
 */

public class RecordDetails {

    String subject,presenty,marks;

    public RecordDetails(){

    }

    public RecordDetails(String subject, String presenty, String marks) {
        this.subject = subject;
        this.presenty = presenty;
        this.marks = marks;
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
