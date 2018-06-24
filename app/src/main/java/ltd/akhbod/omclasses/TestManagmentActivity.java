package ltd.akhbod.omclasses;

import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ltd.akhbod.omclasses.ExternalLibrarbyClasses.ClickatellHttp;
import ltd.akhbod.omclasses.ModalClasses.ProfileDetails;
import ltd.akhbod.omclasses.ModalClasses.RecordDetails;
import ltd.akhbod.omclasses.ModalClasses.SearchByDateDetails;

import ltd.akhbod.omclasses.ViewHolders.PresentyList_testManagemnet;

public class TestManagmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {



    //layout variables
    TextView mDateText;
    Spinner mSubjectSpinner,mClassSpinner;
    String mSelectedSubject,mSelectedClass,currentDate;
    RecyclerView recyclerView;
    Button mUpload;


    //Activity variables
    DatePickerDialog datePickerDialog;
    ArrayList<String> presentArray=new ArrayList<>();
    ArrayList<String> studentIdArray=new ArrayList<>();
    ArrayList<String> studentNamesArray=new ArrayList<>();
    ClickatellHttp httpApi;
    String MESSAGE_ID=null;

    //firebase variables
    DatabaseReference ref;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_manament);

        // Initialize the clickatell object:
        ref= FirebaseDatabase.getInstance().getReference();
      //  httpApi = new ClickatellHttp("Abhijit_click", "F0Z8U9xLQqm2yTgoUhCFDw== ", "Abhijit@click");
      //  getAuth();
        currentDate= new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());



        mDateText=findViewById(R.id.TestManagment_dateText);
        mSubjectSpinner=findViewById(R.id.TestManagment_subjectSpinner);
        mClassSpinner=findViewById(R.id.TestManagment_classSpinner);
        mUpload=findViewById(R.id.TestManagment_upload);


        mDateText.setText(currentDate);

        ArrayAdapter subjectAdapter=ArrayAdapter.createFromResource(this,R.array.subject_names,android.R.layout.simple_spinner_dropdown_item);
        mSubjectSpinner.setAdapter(subjectAdapter);
        mSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) mSelectedSubject="physics";
                else if(position==1) mSelectedSubject="chemistry";
                else
                    mSelectedSubject="maths";
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
                setFirebaseAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSelectedClass="11th";

        recyclerView=findViewById(R.id.TestManagment_recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);



        datePickerDialog = new DatePickerDialog(
                TestManagmentActivity.this, TestManagmentActivity.this, 2018,0,
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
             uploadDetails();
            }});

    }

    @Override
    protected void onStart() {
        super.onStart();

        setFirebaseAdapter();
    }

    private void setFirebaseAdapter() {
        studentIdArray.clear();
        studentNamesArray.clear();
        presentArray.clear();
        FirebaseRecyclerAdapter<ProfileDetails,PresentyList_testManagemnet> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<ProfileDetails, PresentyList_testManagemnet>(

               ProfileDetails.class,
               R.layout.management_singleitem_layout,
                PresentyList_testManagemnet.class,
               ref.child(mSelectedClass+"(2018-2019)").child("profile")

       ) {
           @Override
           protected void populateViewHolder(final PresentyList_testManagemnet viewHolder, final ProfileDetails model, final int position) {

               Log.d("abhi",""+position);

           viewHolder.setDetails(getApplicationContext(),model,position);

           viewHolder.mSendMessage.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  // viewHolder.progressBar.setVisibility(View.VISIBLE);
                   viewHolder.mSendMessage.setBackgroundResource(R.color.grey);
                   viewHolder.mSendMessage.setImageResource(R.drawable.ic_done_black_24dp);
                   sendMessage(model.getMobNo(),model.getName());
                   // sendSingleMessage("+917775971543","Hii this is Om Class.");
                   //GetCoverage("+918668737792");
               }});
               studentIdArray.add(position,model.getId());
               studentNamesArray.add(position,model.getName());
               presentArray.add(position,"yes");

               viewHolder.mPresenetLayout.setOnClickListener(new View.OnClickListener() {
                   @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                   @Override
                   public void onClick(View v) {
                       viewHolder.mPresenetLayout.setBackgroundResource(R.color.green);
                       viewHolder.mAbsentLayout.setBackground(null);
                       presentArray.set(position,"yes");
                   }});

               viewHolder.mAbsentLayout.setOnClickListener(new View.OnClickListener() {
                   @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                   @Override
                   public void onClick(View v) {
                       viewHolder.mAbsentLayout.setBackgroundResource(R.color.red);
                       viewHolder.mPresenetLayout.setBackground(null);
                       presentArray.set(position,"no");
                   }});


           }};
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void sendMessage(String mobNo, String name) {

        SmsManager manager = SmsManager.getDefault();
        String test = "आपला पाल्य "+name+" दि."+currentDate+" ला झालेल्या " +mSelectedSubject +" च्या टेस्ट ला अनुपस्थित होता. ओम क्लासेस";
        ArrayList<String> parts = manager.divideMessage(test);
        manager.sendMultipartTextMessage("+917775971543",null,parts,null,null);
    }


    private void uploadDetails() {

        //making combine string

                int count=0;
                final StringBuffer totalPresent=new StringBuffer();
                while ( count < presentArray.size()){
                      String temp;

                    if(presentArray.get(count).equals("no")){count++; continue; }

                      if(count == 0){   temp=studentNamesArray.get(count)+"="+studentIdArray.get(count);}
                      else
                          temp=","+studentNamesArray.get(count)+"="+studentIdArray.get(count) ;

                      totalPresent.append(temp);
                      count++;
                }

        ///uploading combined string

        SearchByDateDetails obj2=new SearchByDateDetails(totalPresent.toString());
        ref.child(mSelectedClass+"(2018-2019)").child("search").child(currentDate+"-"+mSelectedSubject).setValue(obj2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "" + "data uploaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "" + " data uploaded failed", Toast.LENGTH_SHORT).show();
                Log.d("data",e.getMessage());
            }});



        //uploading test data in stduent id one by one

                count=0;
               while (count < presentArray.size()){

                    RecordDetails obj = new RecordDetails(mSelectedSubject,presentArray.get(count), "00");
                    ref.child(mSelectedClass+"(2018-2019)").child("record").child(studentIdArray.get(count)).child(currentDate+"-"+mSelectedSubject).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                   count++;
                }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        int i=month+1;
        String date=dayOfMonth+"-"+i+"-"+year;
        mDateText.setText(date);
    }


    /**
     * This tests for authentication details, to make sure they are correct.
     * Created a toast message on success, or error.
     */

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



    /**
     * This sends the given message and displays the output. The output is also shown via a toast.
     *
     * @param number  The number to send to. Should be in international format.
     * @param content The message the will be sent.
     */
    private void sendSingleMessage(final String number, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClickatellHttp.Message result = httpApi.sendMessage(number, content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                            String[] temp=result.toString().split(": ");
                            MESSAGE_ID=temp[1];

                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }




    /**
     * This attempts to stop the message of the message ID. It then displays the status of the message in a toast, and on the UI,
     *
     *
     */
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



    /**
     * This get the status of the given message id. The status will get shown as a toast and in the card.
     *
     * The message ID to do the lookup on.
     */
    private void GetMessageStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int result = httpApi.getMessageStatus(MESSAGE_ID);
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

                            if (result < 0) {

                                Toast.makeText(getApplicationContext(), "Message Cannot be Routed", Toast.LENGTH_LONG).show();
                            } else {

                                Toast.makeText(getApplicationContext(), "Message can be routed, and it could cost as little as: " + result + " credits", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
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
