package ltd.akhbod.omclasses;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("");
        getSupportActionBar().hide();

        //FirebaseAuth auth = FirebaseAuth.getInstance();
       // auth.signInAnonymously();
        loginDialog();

        ImageView mLogoImage = findViewById(R.id.imageView2);
        Button mUploadBtn = findViewById(R.id.main_upload);
        Button mRecordBtn = findViewById(R.id.main_record);
        Button mTestManagment = findViewById(R.id.main_testManagmnt);

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

    private void loginDialog() {

        final Dialog dialog;

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.login_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       // dialog.setCanceledOnTouchOutside(true);

        final Button ok = dialog.findViewById(R.id.login_ok_button);
        final TextView prompt = dialog.findViewById(R.id.login_wrong_propmt);
        final ProgressBar progress = dialog.findViewById(R.id.login_progressbar);
        final EditText user = dialog.findViewById(R.id.login_username);
        final EditText password = dialog.findViewById(R.id.login_password);
        final SharedPreferences preferences = getSharedPreferences("USER",MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        final String[] savedName = {preferences.getString("Name", null)};
        if(savedName[0] != null){
            user.setText(savedName[0]);
        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                prompt.setVisibility(View.GONE);
                FirebaseAuth auth = FirebaseAuth.getInstance();

                if(savedName[0] == null){
                    savedName[0] = user.getText().toString();
                    editor.putString("Name", savedName[0]);
                    editor.apply();
                }
                String pass = password.getText().toString();
                if(pass.isEmpty()){
                    password.setError("Please enter password !");
                    progress.setVisibility(View.GONE);
                    return;
                }
                auth.signInWithEmailAndPassword(savedName[0],pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progress.setVisibility(View.GONE);
                        if(task.isSuccessful()){

                            dialog.dismiss();


                        }else{
                            prompt.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    @ Override
    public void onBackPressed(){
        this.finish();
        super.onBackPressed();

    }
}
