package ltd.akhbod.omclasses;

import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class StaffActivity extends AppCompatActivity {

    //Layout variabels
    private TextView mMigrateClass,mMigrateFrom,mMigrateTo;
    private TextView mDeleteClassText;
    private Spinner mDeleteClassSpinner;
    private Button mMigrateBtn,mDeleteBtn;
    
    //Activity variables
    ArrayList<String> classList=new ArrayList<>();
    String selectedClass;
    ArrayAdapter classAdapter=null;
    int currentYear;
    
    //Firebase variables
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        

        databaseReference=FirebaseDatabase.getInstance().getReference().child("DataManage").child("isMigrated_Deleted");
        currentYear= Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));


        mMigrateClass=findViewById(R.id.staff_migrateClass);
        mMigrateFrom=findViewById(R.id.staff_migrateFrom);
        mMigrateTo=findViewById(R.id.staff_migrateTo);
        mMigrateBtn=findViewById(R.id.staff_migrateBtn);

        mDeleteClassText=findViewById(R.id.staff_deleteClassText);
        mDeleteClassSpinner=findViewById(R.id.staff_deletSpinner);

        layoutQuery();

    }


    private void layoutQuery() {


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (  DataSnapshot snap : dataSnapshot.getChildren())
                {

                        String[] parts=snap.getKey().split("th");

                        String[] migrates=parts[1].split("-");
                        StringBuffer str=new StringBuffer(migrates[1]);
                        str.deleteCharAt(4);

                        int lastYear=Integer.parseInt(str.toString());
                        String migrateFrom=(lastYear-1)+"-"+lastYear;
                        String migrateTo=lastYear+"-"+(lastYear+1);

                        if(parts[0].equals("11") && (lastYear == currentYear)) {
                            mMigrateClass.setText(parts[0] + "th");
                            mMigrateFrom.setText(migrateFrom);
                            mMigrateTo.setText(migrateTo);
                        }else if(lastYear == currentYear)
                        {
                           classList.add(migrateTo);
                        }

                }
                if(mMigrateClass.getText().toString().isEmpty()){   mMigrateClass.setText("nothing to migrate");    }
                setDeleteClassSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void setDeleteClassSpinner() {

        classAdapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,classList);
        mDeleteClassSpinner.setAdapter(classAdapter);
        mDeleteClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClass=parent.getItemAtPosition(position).toString();
                mDeleteClassText.setText(selectedClass);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


}
