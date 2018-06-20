package ltd.akhbod.omclasses;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button mUploadBtn,mRecordBtn,mTestManagment;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("");
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        auth.signInAnonymously();
        mUploadBtn=findViewById(R.id.main_upload);
        mRecordBtn=findViewById(R.id.main_record);
        mTestManagment=findViewById(R.id.main_testManagmnt);

        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),UploadActivity.class);
                startActivity(intent);
            }});

        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                startActivity(intent);
            }});

        mTestManagment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),TestManagmentActivity.class);
                startActivity(intent);
            }});
    }
}
