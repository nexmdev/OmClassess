package ltd.akhbod.omclasses;

import android.*;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.mvc.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import ltd.akhbod.omclasses.ExternalLibrarbyClasses.FileUtil;
import ltd.akhbod.omclasses.ModalClasses.ProfileDetails;
import ltd.akhbod.omclasses.ModalClasses.RecordDetails;
import ltd.akhbod.omclasses.ViewHolders.StudentScorecard_record;

public class RecordActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    //layout variables
    private TextView mName, mSchool, mAddress, mMobNo,fees,feeDetails;
    private LinearLayout mIndex, subjectAttendenceIndex,lables_layout1;
    private TextView physicsAttendence, chemistryAttendence, mathsAttendence;
    private TextView phyPercentage,chemPercentage,mathsPercentage;
    private CircularProgressBar cp1,cp2,cp3;
    private RecyclerView mRecyclerView;
    private ImageView studentPhoto;
    private File actualImage;
    private File compressedImage;
    //Activity variables

    private String sName;
    private String nSchool;
    private String nAddress;
    private String nMobNo;
    private String studentID;
    private String photoUrl,Subject;
    private String finalMessage,standard,durationText,paidFee,feeDetailsString;
    private int marksObtained = 0,totalMarks=0;

    //firebase variables
    private DatabaseReference ref,ref1;
    private FirebaseRecyclerAdapter<RecordDetails, StudentScorecard_record> firebaseRecyclerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        getSupportActionBar().setTitle("Student Record");
        Intent intent = getIntent();
        sName = intent.getStringExtra("name");
        nSchool = intent.getStringExtra("schoolname");
        nAddress = intent.getStringExtra("address");
        nMobNo = intent.getStringExtra("mobile");
        studentID = intent.getStringExtra("id");
       // String durationText = intent.getStringExtra("duration");
        standard = intent.getStringExtra("class");
        photoUrl = intent.getStringExtra("url");
        durationText = intent.getStringExtra("duration");
        paidFee = intent.getStringExtra("fee");
        feeDetailsString = intent.getStringExtra("feeDetails");
        Subject = intent.getStringExtra("subject");

        if(standard.matches("11th")||standard.matches("12th")){
            ref = FirebaseDatabase.getInstance().getReference().child(standard + durationText);
        }else{
            ref = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child(Subject);
        }

        ref.keepSynced(true);


        mName = findViewById(R.id.record_name);
        mSchool = findViewById(R.id.record_school);
        mAddress = findViewById(R.id.record_address);
        mMobNo = findViewById(R.id.record_phoneno);
        fees = findViewById(R.id.record_fee_textview);
        feeDetails = findViewById(R.id.fee_details_textView);

        if(paidFee.matches("Not paid")){
            fees.setText("Not paid");
        }else{
            fees.setText("₹."+paidFee);
        }
        if(feeDetailsString.matches("x")){
            feeDetails.setVisibility(View.GONE);
        }else{
            feeDetails.setVisibility(View.VISIBLE);
            feeDetails.setText(feeDetailsString);
        }


        setProfileDetails();

        Button mGetScorecard = findViewById(R.id.record_getScoreCard);
        mIndex = findViewById(R.id.record_linearlayout);
        subjectAttendenceIndex = findViewById(R.id.record_alternate_layout);
        physicsAttendence = findViewById(R.id.physics_text);
        mathsAttendence = findViewById(R.id.maths_text);
        chemistryAttendence = findViewById(R.id.chemistry_text);
        phyPercentage = findViewById(R.id.physics_percentage);
        chemPercentage = findViewById(R.id.chemistry_percentage);
        mathsPercentage = findViewById(R.id.maths_percentage);
        lables_layout1 = findViewById(R.id.lables_layout);
        cp1 = findViewById(R.id.physics_cp);
        cp2 = findViewById(R.id.chemistry_cp);
        cp3 = findViewById(R.id.maths_cp);
        mRecyclerView = findViewById(R.id.record_recyclerview);
        studentPhoto = findViewById(R.id.record_student_image_view);

        Button smsParent = findViewById(R.id.student_record_sms_button);
        Button callParent = findViewById(R.id.student_record_call_button);

        smsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSMSDlg();
            }
        });
        //setting up recyclerview

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(manager);

        attachFirebaseRecycler();
        mGetScorecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIndex.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                subjectAttendenceIndex.setVisibility(View.VISIBLE);
                lables_layout1.setVisibility(View.VISIBLE);
                attendenceQueryEachSubject();

            }
        });

    }

    private void setFee() {
        ref1 = FirebaseDatabase.getInstance().getReference().child("Fees")
                .child(standard).child(studentID);
        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String feeString;
                if(dataSnapshot.exists()){
                    feeString = dataSnapshot.getValue().toString();
                    feeDetails.setText(feeString);
                    feeDetails.setVisibility(View.VISIBLE);
                    String[] entries = feeString.split(",");
                    int count=0;
                    int totalFee = 0;
                    while (count< entries.length){
                        String[] fee = entries[count].split("=");
                        totalFee = totalFee + Integer.parseInt(fee[1]);
                        count++;
                    }
                    feeString = "₹. "+String.valueOf(totalFee);
                }else{
                    feeString = "Fees Not Paid";
                    fees.setTextSize(14);
                }
                fees.setText(feeString);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setProfileDetails() {
        mName.setText(sName);
        mSchool.setText(nSchool);
        mAddress.setText(nAddress);
        mMobNo.setText("+91" + nMobNo);
        Glide
                .with(getApplicationContext())
                .load(photoUrl)
                .asBitmap()
                .error(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_message_black_24dp))
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
            case 0:
                subject = "maths";
                makeQuery(subject);
            case 1:
                subject = "physics";
                makeQuery(subject);
            case 2:
                subject = "chemistry";
                makeQuery(subject);
            case 3:
                subject = "science";
                makeQuery(subject);
        }

    }

    private void makeQuery(final String subject) {

        Query query = ref.child("record").child(studentID).orderByChild("subject").equalTo(subject);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    int TOTAL_PRESENT = 0;

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {

                        if (issue.getValue(RecordDetails.class).getPresenty().equals("yes")) {
                            TOTAL_PRESENT++;
                        }
                    }
                    TOTAL_PRESENT = TOTAL_PRESENT * 100;
                    int percentage = (int) (TOTAL_PRESENT / dataSnapshot.getChildrenCount());
                    //percentage = percentage *100;
                    TOTAL_PRESENT = TOTAL_PRESENT / 100 ;
                    String str = TOTAL_PRESENT + "/" + dataSnapshot.getChildrenCount();
                    TextView label = findViewById(R.id.physics_lable);
                   // LinearLayout label2 = findViewById(R.id.record_chemistry_label);
                    switch (subject) {
                        case "maths":
                            mathsAttendence.setText(str);
                            mathsPercentage.setText(percentage+"%");
                            cp3.setProgress(percentage);
                            break;
                        case "physics":
                            physicsAttendence.setText(str);
                            phyPercentage.setText(percentage+"%");
                            cp1.setProgress(percentage);
                            label.setText("Physics");
                           // label2.setVisibility(View.VISIBLE);
                            break;
                        case "science":
                            cp1.setProgress(percentage);
                            phyPercentage.setText(percentage+"%");
                            physicsAttendence.setText(str);
                            label.setText("Science");
                            //label2.setVisibility(View.GONE);
                            break;
                        default:
                            chemistryAttendence.setText(str);
                            chemPercentage.setText(percentage+"%");
                            cp2.setProgress(percentage);
                            break;
                    }
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
                ref.child("record").child(studentID)

        ) {
            @Override
            protected void populateViewHolder(final StudentScorecard_record viewHolder, final RecordDetails model, final int position) {
                String date = firebaseRecyclerAdapter.getRef(position).getKey();

                viewHolder.setSingleRecord(getApplicationContext(), date, model.getSubject(), model.getPresenty());
                ref.child("search").child(date).child("outOfMarks").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.mMarks.setText(model.getMarks() + "/" + dataSnapshot.getValue());

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

       // firebaseRecyclerAdapter.
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    private void launchSMSDlg() {
        final Dialog dialog;

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.record_dialog_layout);
        dialog.setCanceledOnTouchOutside(true);

        final Button b1 = dialog.findViewById(R.id.record_dialog_sms_button_one);
        final Button b2 = dialog.findViewById(R.id.record_dialog_sms_button_two);
        final Button b3 = dialog.findViewById(R.id.record_dialog_sms_button_three);
        final Button bfinal = dialog.findViewById(R.id.record_dialog_sms_button_send);

        final EditText sms1 = dialog.findViewById(R.id.record_dialog_sms_edittext_one);
        final EditText sms2 = dialog.findViewById(R.id.record_dialog_sms_edittext_two);
        final EditText sms3 = dialog.findViewById(R.id.record_dialog_sms_edittext_three);

        String message1 = "आपला पाल्य " + sName + " मागिल -- दिवसांपासुन टयूशन ला अनुपस्थित आहे. ओम क्लासेस";
        sms1.setText(message1);
        String message2 = "आपला पाल्य " + sName + " टयूशन च्या टेस्ट्स ला सतत अनुपस्थित आहे. ओम क्लासेस";
        sms2.setText(message2);

        String message3 = "आपला पाल्य " + sName + " ची टयूशन फि दि.---- पर्यंत जमा करावी. ओम क्लासेस";
        sms3.setText(message3);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finalMessage = sms1.getText().toString();
                ((Button) v).setText("Selected !");
                toggleButton(v, b2, b3);

            }
        });


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finalMessage = sms2.getText().toString();
                ((Button) v).setText("Selected !");
                toggleButton(v, b1, b3);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                finalMessage = sms3.getText().toString();
                ((Button) v).setText("Selected !");
                toggleButton(v, b2, b1);
            }
        });
        bfinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalMessage == null) {
                    Toast.makeText(getApplicationContext(), "Select Message", Toast.LENGTH_SHORT).show();


                } else {
                    SmsManager manager = SmsManager.getDefault();
                    ArrayList<String> parts = manager.divideMessage(finalMessage);
                    manager.sendMultipartTextMessage(nMobNo, null, parts, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent successfully", Toast.LENGTH_SHORT).show();
                    ((Button) v).setText("Successfully sent !");
                    v.setBackgroundResource(R.color.grey);
                    ((Button) v).setTextColor(Color.BLUE);

                    v.setEnabled(false);
                    // dialog.dismiss();
                }

            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    private void toggleButton(View v1, View v2, View v3) {

        v2.setBackgroundResource(R.color.colorPrimary);
        ((Button) v2).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        ((Button) v2).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        v2.setEnabled(true);
        ((Button) v2).setText("Select Message");

        v3.setBackgroundResource(R.color.colorPrimary);
        ((Button) v3).setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        ((Button) v3).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        v3.setEnabled(true);
        ((Button) v3).setText("Select Message");

        v1.setBackgroundResource(R.color.grey);
        ((Button) v1).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_done_black_24dp), null);
        ((Button) v1).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        v1.setEnabled(false);
    }


    public void zoomStudentPhoto(View view) {


    }

    public void callParents(View view) {
        String toDial = "tel:" + mMobNo.getText().toString();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(toDial)));
    }

    public void AddFee(View view) {
        final Dialog dialog;

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_fee_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        final EditText amount = dialog.findViewById(R.id.add_fee_dialog_amount_edittext);
        final Button ok = dialog.findViewById(R.id.add_fee_dialog_ok_button);
        final ImageView calender = dialog.findViewById(R.id.addfee_calender);
        String currentDate= new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        final TextView dateText = dialog.findViewById(R.id.addfee_setDate);
        dateText.setText(currentDate);
        final int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        final int currentDay= Integer.parseInt(new SimpleDateFormat("dd", Locale.getDefault()).format(new Date()));
        int currentMonth= Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(new Date()));
        currentMonth--;
        final int finalCurrentMonth = currentMonth;
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog =new DatePickerDialog(RecordActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                month++;
                                String MONTH,DAY;
                                if(month<10){
                                    MONTH="0"+(month);
                                }
                                else
                                    MONTH=""+(month);
                                if(dayOfMonth<10){
                                    DAY="0"+(dayOfMonth);
                                }else {
                                    DAY = ""+dayOfMonth;
                                }

                                String date=DAY+"-"+MONTH+"-"+year;
                                dateText.setText(date);
                            }
                        }, currentYear, finalCurrentMonth, currentDay);
                datePickerDialog.show();

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                final String fee = amount.getText().toString();
                if(fee.isEmpty() || fee.matches("00")){
                    amount.setError("Enter amount");
                }else{
                    view.setEnabled(false);
                    view.setBackgroundColor(Color.GRAY);
                    ((Button)view).setTextColor(Color.WHITE);
                    ((Button)view).setText("Processing...");
                    final String currentDate= dateText.getText().toString();
                    if(feeDetailsString.matches("x")){
                        addFeeToProfile(currentDate+"="+fee +",",view,fee);
                    }else{
                        addFeeToProfile(feeDetailsString+"\n"+currentDate+"="+fee +",",view,fee);
                    }



                   /* ref1 = FirebaseDatabase.getInstance().getReference().child("Fees")
                            .child(standard).child(studentID);
                    //final String currentDate= new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String fee_string = dataSnapshot.getValue().toString();
                                fee_string = fee_string+"\n"+currentDate+"="+fee +",";
                                final String finalFee_string = fee_string;
                                ref1.setValue(fee_string).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"Fee added successfully",Toast.LENGTH_SHORT).show();
                                            ((Button)view).setText("Fee added successfully");
                                           // addFeeToProfile(finalFee_string);
                                            setFee();

                                        }else{
                                            Toast.makeText(getApplicationContext(),"Fee addition unsuccessfull !!!",Toast.LENGTH_SHORT).show();
                                            view.setEnabled(true);
                                            view.setBackgroundColor(Color.GREEN);
                                            ((Button)view).setText("Ok");
                                        }
                                    }
                                });
                            }else{
                                ref1.setValue(currentDate+"="+fee +",").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"Fee added successfully",Toast.LENGTH_SHORT).show();
                                           // addFeeToProfile(currentDate+"="+fee +",");
                                            setFee();
                                            ((Button)view).setText("Fee added successfully");

                                        }else{
                                            Toast.makeText(getApplicationContext(),"Fee addition unsuccessfull !!!",Toast.LENGTH_SHORT).show();
                                            view.setEnabled(true);
                                            view.setBackgroundColor(Color.GREEN);
                                            ((Button)view).setText("Ok");
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/

                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();


    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month++;
        String MONTH,DAY;
        if(month<10){
            MONTH="0"+(month);
        }
        else
            MONTH=""+(month);
        if(dayOfMonth<10){
            DAY="0"+(dayOfMonth);
        }else {
            DAY = ""+dayOfMonth;
        }

        String date=DAY+"-"+MONTH+"-"+year;

    }

    private void addFeeToProfile(final String finalFee_string,final View view,final String enteredfee) {
        String[] entries = finalFee_string.split(",");
        int count=0;
        int totalFee = 0;
        while (count< entries.length){
            String[] fee = entries[count].split("=");
            totalFee = totalFee + Integer.parseInt(fee[1]);
            count++;
        }
        final String feeString = String.valueOf(totalFee);
        if(standard.matches("11th")||standard.matches("12th")){
            ref1 = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child("profile")
                    .child(studentID).child("fee");
        }else{
            ref1 = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child(Subject).child("profile")
                    .child(studentID).child("fee");
        }

        ref1.setValue(feeString).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(standard.matches("11th")||standard.matches("12th")){
                        ref1 = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child("profile")
                                .child(studentID).child("feedetails");
                    }else{
                        ref1 = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child(Subject).child("profile")
                                .child(studentID).child("feedetails");
                    }

                    ref1.setValue(finalFee_string).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Fee added successfully",Toast.LENGTH_SHORT).show();
                                ((Button)view).setText("Fee added successfully");
                                fees.setText("₹."+feeString);
                                feeDetails.setVisibility(View.VISIBLE);
                                feeDetails.setText(finalFee_string);
                                if(standard.matches("11th")||standard.matches("12th")){
                                    ref1 = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child("totalFee");
                                }else{
                                    ref1 = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child(Subject).child("totalFee");
                                }

                                //ref1.child(standard + durationText).child("totalFee");
                                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            int totalFee = Integer.parseInt(dataSnapshot.getValue().toString());
                                            totalFee = totalFee+Integer.parseInt(enteredfee);
                                            ref1.setValue(String.valueOf(totalFee)) ;
                                        }else{
                                            ref1.setValue(enteredfee) ;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }else{
                                Toast.makeText(getApplicationContext(),"Fee addition unsuccessful !!!",Toast.LENGTH_SHORT).show();
                                view.setEnabled(true);
                                view.setBackgroundColor(Color.GREEN);
                                ((Button)view).setText("Ok");
                            }
                        }
                    });
                }else{
                    Toast.makeText(getApplicationContext(),"Fee addition unsuccessful !!!",Toast.LENGTH_SHORT).show();
                    view.setEnabled(true);
                    view.setBackgroundColor(Color.GREEN);
                    ((Button)view).setText("Ok");
                }
            }
        });

    }

    public void editProfile(View view) {
        final Dialog dialog;

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_dialog_layout);
        dialog.setCanceledOnTouchOutside(true);

        final EditText name = dialog.findViewById(R.id.edit_name_editText);
        final EditText address = dialog.findViewById(R.id.edit_address_editText);
        final EditText school = dialog.findViewById(R.id.edit_school_editText);
        final EditText moNo = dialog.findViewById(R.id.edit_moNo_editText);
        final TextView update = dialog.findViewById(R.id.edit_update_button);
        final TextView mstandard = dialog.findViewById(R.id.edit_std_textView);
        final TextView fee = dialog.findViewById(R.id.edit_fee_textView);

        if(standard.matches("11th")||standard.matches("12th")){
            mstandard.setText(standard + " - "+ durationText);
        }else{
            mstandard.setText(standard + " - "+ durationText +" - "+Subject);
        }

        fee.setText(fees.getText());
        name.setText(sName);
        address.setText(nAddress);
        school.setText(nSchool);
        moNo.setText(nMobNo);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TextView)view).setText("Processing...");
                DatabaseReference reference =FirebaseDatabase.getInstance().getReference();
                if(standard.matches("11th")||standard.matches("12th")){
                    reference = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child("profile")
                            .child(studentID);
                }else{
                    reference = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child(Subject).child("profile")
                            .child(studentID);
                }
                if(!(name.getText().toString()).matches(sName)){
                    reference.child("name").setValue(name.getText().toString());
                }
                if(!(address.getText().toString()).matches(nAddress)){
                    reference.child("address").setValue(address.getText().toString());
                }
                if(!(school.getText().toString()).matches(nSchool)){
                    reference.child("school").setValue(school.getText().toString());
                }
                if(!(moNo.getText().toString()).matches(nMobNo)){
                    reference.child("mobNo").setValue(moNo.getText().toString());
                }

                ((TextView)view).setText("Update Successful !");
                mName.setText(name.getText().toString());
                mSchool.setText(school.getText().toString());
                mAddress.setText(address.getText().toString());
                mMobNo.setText("+91" + moNo.getText().toString());
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    public void editPhoto(View view) {
        checkPermission();
    }
    private void checkPermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    ImagePicker.pickImage(RecordActivity.this, "Select your image:");
                }

            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        if (bitmap != null) {
            studentPhoto.setImageBitmap(bitmap);
            try {
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Title", null);
                actualImage = FileUtil.from(this,Uri.parse(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // mOriginalImageText.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));

            //starts compressing
            customCompressImage();
        }


    }
    private void customCompressImage() {


        if (actualImage == null) {
            Toast.makeText(getApplicationContext(), "Please choose an image!", Toast.LENGTH_SHORT).show();
        } else {
            // Compress image in main thread using custom Compressor
            try {
                compressedImage = new Compressor(this)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(5)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(actualImage);


                setCompressedImage();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    private void setCompressedImage() {
        studentPhoto.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
        //  mProcessesImageText.setText(String.format("Size : %s", getReadableFileSize(compressedImage.length())));

        //   Toast.makeText(this, "Compressed image save in " + compressedImage.getPath(), Toast.LENGTH_LONG).show();


        Bitmap studentPhoto1 = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());
        studentPhoto.setImageBitmap(studentPhoto1);
        uploadPhoto();

    }

    private void uploadPhoto() {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            Uri file = Uri.fromFile(compressedImage);
            StorageReference storageRef = storage.getReference();
            StorageReference studentPhotoRef = storageRef.child(standard+durationText+"/"+studentID);
            UploadTask uploadTask = studentPhotoRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(),"Photo upload failed !",Toast.LENGTH_SHORT).show();


                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String studentPhotoUrl = taskSnapshot.getDownloadUrl().toString();
                    if(studentPhotoUrl != null){
                        DatabaseReference reference ;
                        if(standard.matches("11th")||standard.matches("12th")){
                            reference = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child("profile")
                                    .child(studentID);
                        }else{
                            reference = FirebaseDatabase.getInstance().getReference().child(standard + durationText).child(Subject).child("profile")
                                    .child(studentID);
                        }

                            reference.child("imageUrl").setValue(studentPhotoUrl);

                    }
                    Toast.makeText(getApplicationContext(),"Photo upload Successful !",
                            Toast.LENGTH_SHORT).show();


                }
            });

    }


}
