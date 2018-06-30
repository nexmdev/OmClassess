package ltd.akhbod.omclasses;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {

    private ImageView mLogoImage;
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

        mLogoImage=findViewById(R.id.imageView2);
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
