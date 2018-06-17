package ltd.akhbod.omclasses.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ltd.akhbod.omclasses.R;

/**
 * Created by ibm on 14-06-2018.
 */

public class StudentScorecard_record extends RecyclerView.ViewHolder {
    View mView;
    TextView mDate, mSubject, mPresenty, mMarks;

    public StudentScorecard_record(View itemView) {
        super(itemView);
        mView = itemView;

        mDate = mView.findViewById(R.id.record_singlestudent_date);
        mSubject = mView.findViewById(R.id.record_singlestudent_subject);
        mPresenty = mView.findViewById(R.id.record_singlestudent_presenty);
        mMarks = mView.findViewById(R.id.record_singlestudent_marks);

    }

    public void setSingleRecord(String date, String subject, String presenty, String marks) {

        mDate.setText(date);
        mSubject.setText(subject);
        mPresenty.setText(presenty);
        mMarks.setText(marks);
    }
}