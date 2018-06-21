package ltd.akhbod.omclasses;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import ltd.akhbod.omclasses.Adapter.RecordTest;

public class RecordTestActivity extends AppCompatActivity {

    private ListView mListView;
    private Button mUploadMarks;

    String key=null,mSelectedStanderd;

    ArrayList<String> marksArray=new ArrayList<>();
    ArrayList<String> studentIdArray=new ArrayList<>();
    ArrayList<String> studentNamesArray=new ArrayList<>();

    DatabaseReference ref;
    RecordTest adapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_test);


        Intent intent=getIntent();
        seperateId_Name(intent.getStringExtra("totalPresent"));

        mSelectedStanderd=intent.getStringExtra("class");
        key=intent.getStringExtra("key");

        getSupportActionBar().setTitle(mSelectedStanderd +" / " +key);

        ref= FirebaseDatabase.getInstance().getReference().child(mSelectedStanderd+"(2018-2019)");

        mListView=findViewById(R.id.record_test_listView);
        mUploadMarks=findViewById(R.id.record_test_uploadBtn);

        mUploadMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            uploadMarks();
            }});
        adapter=new RecordTest(getApplicationContext(),studentIdArray,studentNamesArray,key,studentNamesArray.size());
        mListView.setAdapter(adapter);
    }

    private void uploadMarks() {

        marksArray=adapter.getMarks();
        int tempCount=0;

        while (tempCount < studentIdArray.size() )
        {

            final int finalTempCount = tempCount;
            ref.child("record").child(studentIdArray.get(tempCount)+"/"+key+"/"+"marks").setValue(marksArray.get(tempCount)).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(),"uploaded "+ finalTempCount +" succesfull",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"uploaded "+finalTempCount+" failed",Toast.LENGTH_SHORT).show();
                    Log.d("data",e.getMessage());
                }
            });

            tempCount++;
        }


    }

    //"Abhijit Mahesh Bodulwar+-LEPPz23Z4HtIFNIzde7,Shubham Suresh Pawar+-LEPQ5C4KEa24I8HGeB1,Akshay Bhoyar+-LEPyx4bWc92bhieIwMD"
    private void seperateId_Name(String totalPresent) {


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
            count++;
        }
    }


}
