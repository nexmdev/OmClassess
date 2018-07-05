package ltd.akhbod.omclasses.Adapter;

import android.content.Context;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import ltd.akhbod.omclasses.R;

/**
 * Created by ibm on 14-06-2018.
 */

public class RecordTest extends BaseAdapter {

    Context c;

    String key=null;
    int TotalStudentCount=0;

    public ArrayList<String> marksArray=new ArrayList<>();
    public String[] noToUpload;
    ArrayList<String> studentIdArray=new ArrayList<>();
    ArrayList<String> studentNamesArray=new ArrayList<>();
    ArrayList<Boolean> smsSendArray=new ArrayList<>();
    ArrayList<String> mobNoArray=new ArrayList<>();

    String dateOfTest,subjectOfTest,mSelectedStanderd,durationText;


    public RecordTest(Context c, ArrayList<String> marksArray, ArrayList<String> studentIds, ArrayList<String> studentNames,
                      String key, int totalCount, ArrayList<Boolean> smsSendArray, ArrayList<String> mobNoArray, String mSelectedStanderd, String durationText){
        this.c=c;
        this.key=key;
        this.noToUpload=new String[totalCount];
        this.TotalStudentCount=totalCount;
        this.mSelectedStanderd= mSelectedStanderd;
        this.durationText= durationText;

        this.marksArray=marksArray;
        this.studentIdArray=studentIds;
        this.studentNamesArray=studentNames;
        this.smsSendArray=smsSendArray;
        this.mobNoArray=mobNoArray;


        String[] parts=key.split("-");
        dateOfTest=parts[0]+"/"+parts[1]+"/"+parts[2]+"/";
        subjectOfTest=parts[3];
    }

    @Override
    public int getCount() {
        return TotalStudentCount;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.recordtest_singlelayout,parent,false);


        final TextView mPosition=row.findViewById(R.id.recordtest_single_postion);
        final  TextView mName=row.findViewById(R.id.recordtest_single_name);
        final ImageButton mSend=row.findViewById(R.id.recordtest_single_massageBtn);
        final ProgressBar mPreogressBar=row.findViewById(R.id.recordtest_single_row_progressbar);
        final EditText mEditMarks=row.findViewById(R.id.recordtest_single_editMarksEdit);


        if(smsSendArray.get(position))
        {
            mSend.setBackgroundResource(R.drawable.ic_message_black_24dp_grey);                      //setting sms view
            mSend.setEnabled(false);
            mSend.setImageResource(R.drawable.ic_done_black_24dp);
        }

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSend.setBackgroundResource(R.drawable.ic_message_black_24dp_grey);
                mSend.setEnabled(false);
                mSend.setImageResource(R.drawable.ic_done_black_24dp);
                String marks = marksArray.get(position);
                if (marks.isEmpty()){
                    Toast.makeText(c,"Please fill marks first !",Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(marks,mobNoArray.get(position),studentNamesArray.get(position),position);
                }


            }
        });

        if(!marksArray.get(position).isEmpty())                                                      //setting marks view
        {
            mEditMarks.setText(marksArray.get(position));
            mEditMarks.setEnabled(false);
            noToUpload[position]="no";

        }
        else
            {
                noToUpload[position]="";
            mEditMarks.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {

                    noToUpload[position]="yes";
                    marksArray.set(position, s.toString());
                }
            });
        }

        mPosition.setText(position+1+")");
        mName.setText(studentNamesArray.get(position));

        return row;
    }

    private void sendMessage(String marks,String mobNo, String name, int position) {

        SmsManager manager = SmsManager.getDefault();

        String test = "आपला पाल्य "+name+" ला दि."+dateOfTest+" ला झालेल्या " +subjectOfTest +" च्या टेस्टमध्ये "+marks+" मार्कस मिळाले. ओम क्लासेस";
        ArrayList<String> parts = manager.divideMessage(test);
        manager.sendMultipartTextMessage("+917775971543",null,parts,null,null);

        FirebaseDatabase.getInstance().getReference().child(mSelectedStanderd+durationText).child("record")
                .child(studentIdArray.get(position) + "/" + key + "/" + "isSmsSent")
                .setValue(true);

    }

   
    public ArrayList<String> getMarks(){
        return marksArray;
    }
    public String[] getNosToUpload(){return noToUpload;}

}