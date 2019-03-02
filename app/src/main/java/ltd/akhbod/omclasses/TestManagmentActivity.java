package ltd.akhbod.omclasses;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ltd.akhbod.omclasses.ExternalLibrarbyClasses.ClickatellHttp;
import ltd.akhbod.omclasses.ModalClasses.ProfileDetails;
import ltd.akhbod.omclasses.ModalClasses.RecordDetails;
import ltd.akhbod.omclasses.ModalClasses.SearchByDateDetails;

import ltd.akhbod.omclasses.ViewHolders.PresentyList_testManagemnet;

public class TestManagmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {



    //layout variables
    private TextView mDateText;
    private EditText mOutOfMarksText;
    private String mSelectedSubject;
    private String mSelectedClass;
    private String currentDate;
    private RecyclerView recyclerView;
    private Button mUpload;
    private ProgressBar progressBar;


    //Activity variables
    private final String MY_PREFS_NAME="presenty";
    private final List<String> presentArray=new ArrayList<>();
    private final ArrayList<String> smsSentArray=new ArrayList<>();
    private final ArrayList<String> studentIdArray=new ArrayList<>();
    private final ArrayList<String> studentNamesArray=new ArrayList<>();
    String MESSAGE_ID=null;
    private String keyToSearch,Subject;

    //firebase variables
    private DatabaseReference ref;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_test_manament);


        //checking permissions
        checkPermission();

        // Initialize the clickatell object:
        ref= FirebaseDatabase.getInstance().getReference();
        ClickatellHttp httpApi = new ClickatellHttp("Abhijit_click", "U3zUw3cKSeaViiv_c5C1OA== ", "Abhijit@click");
        currentDate= new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        String durationText = "(" + currentYear + "-" + (currentYear + 1) + ")";

        TextView mRawText = findViewById(R.id.TestManagment_raw);
        mDateText=findViewById(R.id.TestManagment_dateText);
        final Spinner mSubjectSpinner = findViewById(R.id.TestManagment_subjectSpinner);
        Spinner mClassSpinner = findViewById(R.id.TestManagment_classSpinner);
        mOutOfMarksText=findViewById(R.id.TestManagment_outOfMarksText);
        mUpload = findViewById(R.id.TestManagment_upload);
        progressBar = findViewById(R.id.test_managment_progressbar);

        mDateText.setText(currentDate);

        ArrayAdapter subjectAdapter=ArrayAdapter.createFromResource(this,R.array.subject_names,android.R.layout.simple_spinner_dropdown_item);
        mSubjectSpinner.setAdapter(subjectAdapter);
        mSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               /* if(position==0) mSelectedSubject="physics";
                else if(position==1) mSelectedSubject="chemistry";
                else
                    mSelectedSubject="maths";*/
                mSelectedSubject= parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedSubject="Physics";

            }
        });

        ArrayAdapter classAdapter=ArrayAdapter.createFromResource(this,R.array.class_name,android.R.layout.simple_spinner_dropdown_item);
        mClassSpinner.setAdapter(classAdapter);
        mClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedClass=parent.getItemAtPosition(position).toString();
                if(mSelectedClass.matches("11th")||mSelectedClass.matches("12th")){
                    ArrayAdapter subjectAdapter=ArrayAdapter.createFromResource(getApplicationContext(),R.array.subject_names,android.R.layout.simple_spinner_dropdown_item);
                    mSubjectSpinner.setAdapter(subjectAdapter);
                }else{
                    String[] temp = mSelectedClass.split("-");
                    mSelectedClass = temp[0];
                    Subject = temp[1];
                    String[] sub = {Subject};
                    ArrayAdapter<String> aa = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, sub);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSubjectSpinner.setAdapter(aa);
                }
                studentIdArray.clear();
                studentNamesArray.clear();
                presentArray.clear();
                getDurationQuery();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSelectedClass="11th";

        recyclerView=findViewById(R.id.TestManagment_recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                TestManagmentActivity.this, TestManagmentActivity.this, 2018, 0,
                1);

        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //datePickerDialog.show();
                //StopMessage();
            }});

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(TestManagmentActivity.this);
                builder.setMessage("Do you want to upload Test Data ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        uploadDetails();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                android.support.v7.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }});


        getDurationQuery();
    }

    /*
    @Override
    protected void onPause() {
        super.onPause();
        String totalString="";
        int count=0;
        for(String str : presentArray){
            if(count==0){ totalString = totalString + str; count=1;}
            else
                totalString += ","+str;
        }
        Log.d("don","presentArray="+presentArray);
        mRawText.setText(totalString);
        Log.d("don","totalString="+totalString);
    }
    private void retrievePrefs() {
        Log.d("don","Retrive="+presentArray);
        String[] parts=mRawText.getText().toString().split(",");
        for(String str : parts){
            presentArray.add(str);
        }
        Log.d("don","onRetrieve"+presentArray);
    }
    */


    private void getDurationQuery() {

        progressBar.setVisibility(View.VISIBLE);
        ref.child("DataManage").child("isMigrated_Deleted")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Log.d("abhi","onDataChange()"+dataSnapshot.getChildrenCount());


                        int count=0;
                        for (  DataSnapshot snap : dataSnapshot.getChildren()) {

                            if (snap.getKey().contains(mSelectedClass)) {
                                keyToSearch=snap.getKey();
                                recyclerView.setVisibility(View.VISIBLE);
                                setFirebaseAdapter();
                                break;

                            }
                            count++;

                        }
                        if(count == dataSnapshot.getChildrenCount()){
                            Toast.makeText(getApplicationContext(),"No record Found For Given Class",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}});

    }



    private void setFirebaseAdapter() {

       /* presentArray.clear();
        studentIdArray.clear();
        studentNamesArray.clear();*/

        //Log.d("abhi","setFirebaseAdapter()");
        Query query = ref.child(keyToSearch).child("profile").orderByChild("name");
        if(mSelectedClass.matches("11th")||mSelectedClass.matches("12th")){

        }else{
            query = ref.child(keyToSearch).child(Subject).child("profile").orderByChild("name");;
        }





        FirebaseRecyclerAdapter<ProfileDetails, PresentyList_testManagemnet> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ProfileDetails, PresentyList_testManagemnet>(

                        ProfileDetails.class,
                        R.layout.management_singleitem_layout,
                        PresentyList_testManagemnet.class,
                        query

                ) {
                    @Override
                    public void onDataChanged() {
                        if(getItemCount() != 0){
                            progressBar.setVisibility(View.INVISIBLE);
                        }

                    }
                    @Override
                    protected void populateViewHolder(final PresentyList_testManagemnet viewHolder, final ProfileDetails model, final int position) {
                        // final String key=testAdapter.getRef(position).getKey();
                        // Log.d("abhi",""+position);

                        if (presentArray.size() < position + 1) {
                            presentArray.add(position, "no");
                            smsSentArray.add(position,"no");
                            studentIdArray.add(position, model.getId());
                            studentNamesArray.add(position, model.getName());
                        }


                        viewHolder.setDetails(getApplicationContext(), model, position, presentArray.get(position),smsSentArray.get(position));

                        viewHolder.mSendMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // viewHolder.progressBar.setVisibility(View.VISIBLE);
                                smsSentArray.set(position,"yes");
                                viewHolder.mSendMessage.setEnabled(false);
                                viewHolder.mSendMessage.setBackgroundResource(R.drawable.ic_message_black_24dp_grey);
                                viewHolder.mSendMessage.setImageResource(R.drawable.ic_done_black_24dp);

                                sendMessage(model.getMobNo(), model.getName());
                                // sendSingleMessage("+917775971543","Hii this is Om Class.");
                                //GetCoverage("+918668737792");
                            }
                        });


                        viewHolder.mPresenetLayout.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onClick(View v) {
                                viewHolder.mPresenetLayout.setBackgroundResource(R.color.green);
                                viewHolder.mAbsentLayout.setBackground(null);
                                presentArray.set(position, "yes");
                                // viewHolder.mView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.grey));

                                viewHolder.mSendMessage.setBackgroundResource(R.drawable.ic_message_black_24dp_grey);
                                viewHolder.mSendMessage.setEnabled(false);

                            }});


                        viewHolder.mAbsentLayout.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onClick(View v) {
                                viewHolder.mAbsentLayout.setBackgroundResource(R.color.red);
                                viewHolder.mPresenetLayout.setBackground(null);
                                presentArray.set(position, "no");


                                if(smsSentArray.get(position).equals("no")) {
                                    viewHolder.mSendMessage.setBackgroundResource(R.drawable.ic_message_black_24dp);
                                    viewHolder.mSendMessage.setEnabled(true);

                                }}});

                    }};

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    private void sendMessage(String mobNo, String name) {

        SmsManager manager = SmsManager.getDefault();
        String test = "आपला पाल्य "+name+" दि."+currentDate+" ला झालेल्या " +mSelectedSubject +" च्या टेस्ट ला अनुपस्थित होता. ओम क्लासेस";
        ArrayList<String> parts = manager.divideMessage(test);
        manager.sendMultipartTextMessage(mobNo,null,parts,null,null);
    }


    private void uploadDetails() {

        //making combine string

        if((mOutOfMarksText.getText().toString()).isEmpty()){
            mOutOfMarksText.setError("Enter marks");
            Toast.makeText(getApplicationContext(),"Please type Total Marks !",Toast.LENGTH_SHORT).show();
            return;
        }
        int count=0;
        int tempCount=0;
        final StringBuilder totalPresent=new StringBuilder();
        while ( count < presentArray.size()){
            String temp;

            if(presentArray.get(count).equals("no")){count++; continue; }

            if(tempCount == 0){   temp=studentNamesArray.get(count)+"="+studentIdArray.get(count); tempCount++;}
            else
                temp=","+studentNamesArray.get(count)+"="+studentIdArray.get(count) ;


            totalPresent.append(temp);
            count++;
        }

        ///uploading combined string

        SearchByDateDetails obj2=new SearchByDateDetails(totalPresent.toString(),Integer.parseInt(mOutOfMarksText.getText().toString()));
        if(mSelectedClass.matches("11th")||mSelectedClass.matches("12th")){
            ref.child(keyToSearch).child("search").child(currentDate+"-"+mSelectedSubject).setValue(obj2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "" + "data uploaded successfully", Toast.LENGTH_SHORT).show();
                    mUpload.setText("Data uploaded successfully !");
                    mUpload.setEnabled(false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "" + " data uploaded failed", Toast.LENGTH_SHORT).show();

                }});
        }else{
            ref.child(keyToSearch).child(Subject).child("search").child(currentDate+"-"+mSelectedSubject).setValue(obj2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "" + "data uploaded successfully", Toast.LENGTH_SHORT).show();
                    mUpload.setText("Data uploaded successfully !");
                    mUpload.setEnabled(false);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "" + " data uploaded failed", Toast.LENGTH_SHORT).show();

                }});
        }




        //uploading test data in stduent id one by one

        count=0;
        while (count < presentArray.size()){



            RecordDetails obj = new RecordDetails(mSelectedSubject,presentArray.get(count), "00",false);
            if(mSelectedClass.matches("11th")||mSelectedClass.matches("12th")){
                ref.child(keyToSearch).child("record").child(studentIdArray.get(count)).child(currentDate+"-"+mSelectedSubject).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Toast.makeText(getApplicationContext(), "" + "student data uploaded", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Toast.makeText(getApplicationContext(), "" + " failed", Toast.LENGTH_SHORT).show();
                        // Log.d("abhi", "onFailure:" + e.getMessage().toString());
                    }
                });
            }else{
                ref.child(keyToSearch).child(Subject).child("record").child(studentIdArray.get(count)).child(currentDate+"-"+mSelectedSubject).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Toast.makeText(getApplicationContext(), "" + "student data uploaded", Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Toast.makeText(getApplicationContext(), "" + " failed", Toast.LENGTH_SHORT).show();
                        // Log.d("abhi", "onFailure:" + e.getMessage().toString());
                    }
                });
            }

            count++;
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int i=month+1;
        String date=dayOfMonth+"-"+i+"-"+year;
        mDateText.setText(date);
    }


    /*
     * This tests for authentication details, to make sure they are correct.
     * Created a toast message on success, or error.
    private void getAuth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (httpApi.testAuth()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Authentication Succeeded", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }
    /*
     * This sends the given message and displays the output. The output is also shown via a toast.
    private void sendSingleMessage(final String number, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClickatellHttp.Message result = httpApi.sendMessage("918668737792", content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                            String[] temp=result.toString().split(": ");
                            GetMessageStatus(temp[1]);
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }
    /*
     * This attempts to stop the message of the message ID. It then displays the status of the message in a toast, and on the UI,
     *
     *
    private void StopMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int result = httpApi.stopMessage(MESSAGE_ID);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Status: " + result, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }
    /*
     * This get the status of the given message id. The status will get shown as a toast and in the card.
     *
     * The message ID to do the lookup on.
    private void GetMessageStatus(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int result = httpApi.getMessageStatus(s);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "status" + result, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }
    private void GetCoverage(final String number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final double result = httpApi.getCoverage(number);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    });
                } catch (Exception e) {
                            if (result < 0) {
                                Toast.makeText(getApplicationContext(), "Message Cannot be Routed", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Message can be routed, and it could cost as little as: " + result + " credits", Toast.LENGTH_LONG).show();
                            }
                        }
                    ShowException(e);
                }
            }
        }).start();
    }
*/



    private void checkPermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.SEND_SMS
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                   // Toast.makeText(getApplicationContext(), "You can now send SMS", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    private void ShowException(final Exception exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


}