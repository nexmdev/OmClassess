package ltd.akhbod.omclasses.ViewHolders;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ltd.akhbod.omclasses.R;

/**
 * Created by ibm on 14-06-2018.
 */

public class StudentScorecard_record extends RecyclerView.ViewHolder {
    final View mView;
    public final TextView mDate;
    public final TextView mSubject;
    public final TextView mPresenty;
    public final TextView mMarks;

    public StudentScorecard_record(View itemView) {
        super(itemView);
        mView = itemView;

        mDate = mView.findViewById(R.id.record_singlestudent_date);
        mSubject = mView.findViewById(R.id.record_singlestudent_subject);
        mPresenty = mView.findViewById(R.id.record_singlestudent_presenty);
        mMarks = mView.findViewById(R.id.record_singlestudent_marks);

    }

    public void setSingleRecord(Context context,String date, String subject, String presenty) {

        mDate.setText(date);
       //mSubject.setText(subject);
        mPresenty.setText(presenty);
        if(presenty.matches("yes")){
            mPresenty.setTextColor(ContextCompat.getColor(context,R.color.green));
        }else{
            mPresenty.setTextColor(Color.RED);
        }
    }
}