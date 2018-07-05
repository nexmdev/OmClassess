package ltd.akhbod.omclasses.ModalClasses;

/**
 * Created by ibm on 09-06-2018.
 */

public class SearchByDateDetails {

    String totalPresent;
    int outOfMarks;


    public SearchByDateDetails() {

    }

    public SearchByDateDetails(String totalPresent,int outOfMarks) {
        this.totalPresent = totalPresent;
        this.outOfMarks = outOfMarks;
    }

    public int getOutOfMarks() {
        return outOfMarks;
    }

    public void setOutOfMarks(int outOfMarks) {
        this.outOfMarks = outOfMarks;
    }

    public String getTotalPresent() {
        return totalPresent;
    }

    public void setTotalPresent(String totalPresent) {
        this.totalPresent = totalPresent;
    }
}
