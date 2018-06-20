package ltd.akhbod.omclasses;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ltd.akhbod.omclasses.ModalClasses.RecordDetails;
import ltd.akhbod.omclasses.ViewHolders.StudentScorecard_record;

public class RecordActivity extends AppCompatActivity {

    //layout variables
    private TextView mName,mSchool,mAddress,mMobNo;
    private Button mGetScorecard;
    private LinearLayout mIndex,subjectAttendenceIndex;
    private TextView physicsAttendence,chemistryAttendence,mathsAttendence;
    private RecyclerView mRecyclerView;
    private ImageView studentPhoto;
    private int recordTest;
    //Activity variables
    private String sName,nSchool,nAddress,nMobNo,studentID,mSelectedStanderd,photoUrl;


    //firebase variables
    private DatabaseReference ref;
    private FirebaseRecyclerAdapter<RecordDetails,StudentScorecard_record> firebaseRecyclerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent=getIntent();
        sName=intent.getStringExtra("name");
        nSchool=intent.getStringExtra("schoolname");
        nAddress=intent.getStringExtra("address");
        nMobNo=intent.getStringExtra("mobile");
        studentID=intent.getStringExtra("id");
        mSelectedStanderd=intent.getStringExtra("class");
        photoUrl = intent.getStringExtra("url");

        ref=FirebaseDatabase.getInstance().getReference().child(mSelectedStanderd+"(2018-2019)").child("record");

        mName=findViewById(R.id.record_name);
        mSchool=findViewById(R.id.record_school);
        mAddress=findViewById(R.id.record_address);
        mMobNo=findViewById(R.id.record_phoneno);

        setProfileDetails();

        mGetScorecard=findViewById(R.id.record_getScoreCard);
        mIndex=findViewById(R.id.record_linearlayout);
        subjectAttendenceIndex=findViewById(R.id.record_subjectattendence);
        physicsAttendence=findViewById(R.id.record_physicsScore);
        mathsAttendence=findViewById(R.id.record_mathsScore);
        chemistryAttendence=findViewById(R.id.record_chemScore);
        mRecyclerView=findViewById(R.id.record_recyclerview);
        studentPhoto = findViewById(R.id.record_student_image_view);

        //setting up recyclerview

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mGetScorecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             mIndex.setVisibility(View.VISIBLE);
             mRecyclerView.setVisibility(View.VISIBLE);
             subjectAttendenceIndex.setVisibility(View.VISIBLE);

             attendenceQueryEachSubject();
             attachFirebaseRecycler();
            }});

    }


    private void setProfileDetails() {
        mName.setText(sName);
        mSchool.setText(nSchool);
        mAddress.setText(nAddress);
        mMobNo.setText("+91"+nMobNo);
        Glide
                .with(getApplicationContext())
                .load(photoUrl)
                .asBitmap()
                .error(getApplicationContext().getResources().getDrawable(R.drawable.ic_message_black_24dp))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        studentPhoto.setImageBitmap(resource);
                    }
                });
    }

    private void attendenceQueryEachSubject() {

        int i = 0;
        String subject;
        switch (i) {
            case 0: subject="maths"; makeQuery(subject);
            case 1: subject="physics"; makeQuery(subject);
            case 2: subject="chemistry"; makeQuery(subject);
        }

    }

    private void makeQuery(final String subject) {

        Query query=ref.child(studentID).orderByChild("subject").equalTo(subject);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("snap","onDataChanged()1");
                if (dataSnapshot.exists()) {
                    Log.d("snap","onDataChanged()");
                    int TOTAL_PRESENT=0;

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {

                        if(issue.getValue(RecordDetails.class).getPresenty().equals("yes"))
                        {
                            TOTAL_PRESENT++;
                        }
                    }
                    String str= TOTAL_PRESENT+"/"+dataSnapshot.getChildrenCount();
                    if(subject.equals("maths")) mathsAttendence.setText(str);
                    else if(subject.equals("physics"))physicsAttendence.setText(str);
                    else
                        chemistryAttendence.setText(str);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void attachFirebaseRecycler() {

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<RecordDetails, StudentScorecard_record>(

                RecordDetails.class,
                R.layout.record_singlerecord_layout,
                StudentScorecard_record.class,
                ref.child(studentID)

        ) {
            @Override
            protected void populateViewHolder(StudentScorecard_record viewHolder, RecordDetails model, int position) {
                String date = firebaseRecyclerAdapter.getRef(position).getKey();
                viewHolder.setSingleRecord(date, model.getSubject(), model.getPresenty(), model.getMarks());
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


}
