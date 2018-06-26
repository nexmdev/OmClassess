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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import ltd.akhbod.omclasses.Adapter.RecordTest;

public class RecordTestActivity extends AppCompatActivity {

    //layout variables
    private ListView mListView;
    private Button mUploadMarks;
    private ProgressBar mProgressBar;


    //Activity variables
    String key=null,mSelectedStanderd;
    ArrayList<String> marksArray=new ArrayList<>();
    String[] nosToUpload=null;
    ArrayList<String> studentIdArray=new ArrayList<>();
    ArrayList<String> studentNamesArray=new ArrayList<>();


    //Firebase variables
    DatabaseReference databaseReference;
    RecordTest adapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_test);


        Intent intent = getIntent();
        mSelectedStanderd = intent.getStringExtra("class");
        key = intent.getStringExtra("key");

        databaseReference = FirebaseDatabase.getInstance().getReference().child(mSelectedStanderd + "(2018-2019)").child("record");
        getSupportActionBar().setTitle(mSelectedStanderd + " / " + key);


        mProgressBar=findViewById(R.id.record_test_progressBar);
        mListView = findViewById(R.id.record_test_listView);
        mUploadMarks = findViewById(R.id.record_test_uploadBtn);


        mUploadMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMarks();
            }
        });

        seperateId_Name(intent.getStringExtra("totalPresent"));

    }


    private void uploadMarks() {

        marksArray=adapter.getMarks();
        nosToUpload = adapter.getNosToUpload();
        int count=0;

        while (count < studentIdArray.size() )
        {

            if(nosToUpload[count].equals("yes")){
                final int finalCount = count;
                databaseReference.child(studentIdArray.get(count) + "/" + key + "/" + "marks").setValue(marksArray.get(count)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "uploaded " + finalCount + " succesfull", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "uploaded " + finalCount + " failed", Toast.LENGTH_SHORT).show();
                        Log.d("data", e.getMessage());
                    }
                });
            }

            count++;
        }


    }


    //"Abhijit Mahesh Bodulwar=-LEPPz23Z4HtIFNIzde7,Shubham Suresh Pawar=-LEPQ5C4KEa24I8HGeB1,Akshay Bhoyar=-LEPyx4bWc92bhieIwMD"
    private void seperateId_Name(String totalPresent) {

        mProgressBar.setVisibility(View.VISIBLE);

        String[] id_name=new String[totalPresent.length()];

        if(totalPresent.length()== 1){ id_name[0]=totalPresent; }
        else
        id_name=totalPresent.split(",");

        int count=0;
        while ( count < id_name.length )
        {

            String[] temp=new String[3];
            temp=id_name[count].split("=");
            studentNamesArray.add(count,temp[0]);
            studentIdArray.add(count,temp[1]);

            final int finalCount = count;
            final String[] finalId_name = id_name;


            databaseReference.child(studentIdArray.get(count)+"/"+key+"/"+"marks").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String marks=dataSnapshot.getValue().toString();
                    if(!marks.equals("00"))
                    {
                        marksArray.add(finalCount,marks);
                    }
                    else
                    {
                        marksArray.add(finalCount,"");
                    }
                    if(marksArray.size()== finalId_name.length){     mProgressBar.setVisibility(View.GONE);setAdapter();   }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {}

                });

            count++;
        }

    }


    private void setAdapter() {
        adapter=new RecordTest(getApplicationContext(),marksArray,studentIdArray,studentNamesArray,key,studentNamesArray.size());
        mListView.setAdapter(adapter);
    }

}
