package ltd.akhbod.omclasses;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

import ltd.akhbod.omclasses.ModalClasses.RecordDetails;
import ltd.akhbod.omclasses.ViewHolders.StudentScorecard_record;

public class RecordActivity extends AppCompatActivity {

    //layout variables
    private TextView mName,mSchool,mAddress,mMobNo;
    private Button mGetScorecard,smsParent,callParent;
    private LinearLayout mIndex,subjectAttendenceIndex;
    private TextView physicsAttendence,chemistryAttendence,mathsAttendence;
    private RecyclerView mRecyclerView;
    private ImageView studentPhoto;

    //Activity variables

     private String sName,nSchool,nAddress,nMobNo,studentID,
            mSelectedStanderd,photoUrl,finalMessage,durationText;

    //firebase variables
    private DatabaseReference ref;
    private FirebaseRecyclerAdapter<RecordDetails,StudentScorecard_record> firebaseRecyclerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getSupportActionBar().setTitle("Student Record");
        Intent intent=getIntent();
        sName=intent.getStringExtra("name");
        nSchool=intent.getStringExtra("schoolname");
        nAddress=intent.getStringExtra("address");
        nMobNo=intent.getStringExtra("mobile");
        studentID=intent.getStringExtra("id");
        mSelectedStanderd=intent.getStringExtra("class");
        photoUrl = intent.getStringExtra("url");
        durationText=intent.getStringExtra("duration");

        ref=FirebaseDatabase.getInstance().getReference().child(mSelectedStanderd+durationText).child("record");

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

        smsParent = findViewById(R.id.student_record_sms_button);
        callParent = findViewById(R.id.student_record_call_button);

        smsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSMSDlg();
            }
        });
        //setting up recyclerview

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        attachFirebaseRecycler();
        mGetScorecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             mIndex.setVisibility(View.VISIBLE);
             mRecyclerView.setVisibility(View.VISIBLE);
             subjectAttendenceIndex.setVisibility(View.VISIBLE);

             attendenceQueryEachSubject();

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
    private void launchSMSDlg() {
        final Dialog dialog;

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.record_dialog_layout);
        dialog.setCanceledOnTouchOutside(true);

        Button b1 = dialog.findViewById(R.id.record_dialog_sms_button_one);
        Button b2 = dialog.findViewById(R.id.record_dialog_sms_button_two);
        Button bfinal = dialog.findViewById(R.id.record_dialog_sms_button_send);

        final EditText sms1 = dialog.findViewById(R.id.record_dialog_sms_edittext_one);
        final EditText sms2 = dialog.findViewById(R.id.record_dialog_sms_edittext_two);

        String message1 = "आपला पाल्य "+sName+" मागिल -- दिवसांपासुन टयूशन ला अनुपस्थित आहे. ओम क्लासेस";
        sms1.setText(message1);
        String message2 = "आपला पाल्य "+sName+" टयूशन च्या टेस्ट्स ला सतत अनुपस्थित आहे. ओम क्लासेस";
        sms2.setText(message2);



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finalMessage = sms1.getText().toString();
                ((Button)v).setText("Selected !");
                v.setBackgroundResource(R.color.grey);
                v.setEnabled(false);

            }
        });


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finalMessage = sms2.getText().toString();
                ((Button)v).setText("Selected !");
                v.setBackgroundResource(R.color.grey);
                v.setEnabled(false);
            }
        });
        bfinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalMessage==null){
                    Toast.makeText(getApplicationContext(),"Select Message",Toast.LENGTH_SHORT).show();


                }else{
                    SmsManager manager = SmsManager.getDefault();
                    ArrayList<String> parts = manager.divideMessage(finalMessage);
                    manager.sendMultipartTextMessage("+917775971543",null,parts,null,null);
                    Toast.makeText(getApplicationContext(),"SMS sent successfully",Toast.LENGTH_SHORT).show();
                    ((Button)v).setText("Successfully sent !");
                    v.setBackgroundResource(R.color.grey);
                    ((Button) v).setTextColor(Color.BLUE);

                    v.setEnabled(false);
                    // dialog.dismiss();
                }

            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }





}
