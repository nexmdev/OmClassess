package ltd.akhbod.omclasses;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ltd.akhbod.omclasses.ModalClasses.RecordDetails;

import java.util.ArrayList;
import ltd.akhbod.omclasses.Adapter.RecordTest;


public class RecordTestActivity extends AppCompatActivity {

    //layout variables
    private ListView mListView;
    private ProgressBar mProgressBar;


    //Activity variables
    private String key=null;
    private String totalPresent;
    private String mSelectedStanderd,Subject;
    private String durationText;
    private int outOfMarks;
    private ArrayList<String> marksArray=new ArrayList<>();
    private final ArrayList<String> studentIdArray=new ArrayList<>();
    private final ArrayList<String> studentNamesArray=new ArrayList<>();
    private final ArrayList<Boolean> smsSendArray=new ArrayList<>();
    private final ArrayList<String> mobNoArray=new ArrayList<>();

    //Firebase variables
    private DatabaseReference ref;
    private DatabaseReference ref2;
    private RecordTest adapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_test);


        Intent intent = getIntent();
        mSelectedStanderd = intent.getStringExtra("class");
        durationText=intent.getStringExtra("duration");
        key = intent.getStringExtra("key");
        totalPresent=intent.getStringExtra("totalPresent");
        outOfMarks= Integer.parseInt(intent.getStringExtra("outofmarks"));
        Subject = intent.getStringExtra("subject");

        if(mSelectedStanderd.matches("11th")||mSelectedStanderd.matches("12th")){
            ref = FirebaseDatabase.getInstance().getReference().child(mSelectedStanderd+durationText);
            ref2 = FirebaseDatabase.getInstance().getReference().child(mSelectedStanderd+durationText);
        }else{
            ref = FirebaseDatabase.getInstance().getReference().child(mSelectedStanderd+durationText).child(Subject);
            ref2 = FirebaseDatabase.getInstance().getReference().child(mSelectedStanderd+durationText).child(Subject);
        }

        ref.keepSynced(true);
        getSupportActionBar().setTitle(mSelectedStanderd + " / " + key);

        TextView totalMarksTextView = findViewById(R.id.record_test_total_marks_textView);
        totalMarksTextView.setText("  Total Marks : "+outOfMarks);

        mProgressBar=findViewById(R.id.record_test_progressBar);
        mListView = findViewById(R.id.record_test_listView);
        Button mUploadMarks = findViewById(R.id.record_test_uploadBtn);


        mUploadMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMarks();
            }
        });

        seperateId_Name();

    }


    /*
    * totalPresent:
    * "Abhijit Mahesh Bodulwar=-LEPPz23Z4HtIFNIzde7,Shubham Suresh Pawar=-LEPQ5C4KEa24I8HGeB1,Akshay Bhoyar=-LEPyx4bWc92bhieIwMD"
    *
    */

    private void seperateId_Name() {

        mProgressBar.setVisibility(View.VISIBLE);

        String[] id_name=new String[totalPresent.length()];

        if(totalPresent.length()== 1){ id_name[0]=totalPresent; }
        else
        id_name=totalPresent.split(",");


        int count=0;
        while ( count < id_name.length )
        {

            String[] temp=id_name[count].split("=");
            studentNamesArray.add(count,temp[0]);
            studentIdArray.add(count,temp[1]);

            final int finalCount = count;
            final String[] finalId_name = id_name;

            ref.child("record").child(studentIdArray.get(finalCount)+"/"+key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    RecordDetails obj=dataSnapshot.getValue(RecordDetails.class);

                    if (!obj.getMarks().equals("00"))
                        marksArray.add(finalCount, obj.getMarks());                                 //getting marks and isSmsSent for each present id
                    else
                        marksArray.add(finalCount, "");

                     smsSendArray.add(finalCount,obj.getIsSmsSent());

                    ref2.child("profile").child(studentIdArray.get(finalCount)).child("mobNo")                     //getting mfor each present id
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists()){
                                        mobNoArray.add(finalCount, dataSnapshot.getValue().toString());
                                    }


                                    if (studentNamesArray.size() == finalId_name.length){
                                        mProgressBar.setVisibility(View.GONE);
                                        setAdapter();                                      //after splitiing totalPresent the setAdapter() will get called
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {  }});




            count++;
        }


    }



    /*
    *
    *   uploading only marks which are inserted in editText
    *
    */

    private void uploadMarks() {

        mProgressBar.setVisibility(View.VISIBLE);
        marksArray=adapter.getMarks();
        String[] nosToUpload = adapter.getNosToUpload();
        int count=0;

        while (count < studentIdArray.size() )
        {

            if(nosToUpload[count].equals("yes")){

                final int finalCount = count;
                ref.child("record").child(studentIdArray.get(count) + "/" + key + "/" + "marks")
                                 .setValue(marksArray.get(count))


                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {


                            if((finalCount+1) == studentIdArray.size()){
                                mProgressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Upload Successful - "+finalCount+1, Toast.LENGTH_SHORT).show();
                            }

                    }})


                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), finalCount+" uploading failed", Toast.LENGTH_SHORT).show();

                    }});
            }

            count++;

        }

    }


    private void setAdapter() {
        adapter=new RecordTest(getApplicationContext(),marksArray,studentIdArray,
                studentNamesArray,key,studentNamesArray.size(),smsSendArray,mobNoArray,mSelectedStanderd,durationText,outOfMarks,Subject);
        mListView.setAdapter(adapter);
    }




}
