package ltd.akhbod.omclasses;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import ltd.akhbod.omclasses.ExternalLibrarbyClasses.FileUtil;
import ltd.akhbod.omclasses.ModalClasses.ProfileDetails;

public class UploadActivity extends AppCompatActivity {


    //Activity variables
    private static final int PICK_IMAGE_REQUEST = 1;
    private File actualImage;
    private File compressedImage;
    private String selectedStanderd,selectedDuration;

    //layout variables
    private EditText mNameEditText,mAddressEditText,mSchoolEditText,mMobNoEditText;
    private ImageView mImage;
    private TextView mOriginalImageText,mProcessesImageText,mDurationText;
    private Button mUploadBtn;
    private Spinner mStanderedSpinner;
    private String pushId,studentPhotoUrl = "X";

    //firebase variables
    private DatabaseReference ref;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        int year= Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        int nextYear=year+1;
        selectedDuration=year+"-"+nextYear;
        ref= FirebaseDatabase.getInstance().getReference();

        mImage=findViewById(R.id.upload_image);
        mNameEditText=findViewById(R.id.upload_name);
        mAddressEditText=findViewById(R.id.upload_address);
        mStanderedSpinner=findViewById(R.id.upload_standeredSpinner);
        mDurationText=findViewById(R.id.upload_durationText);
        mSchoolEditText=findViewById(R.id.upload_school);
        mMobNoEditText=findViewById(R.id.upload_mobNo);

        mUploadBtn=findViewById(R.id.upload_uploadBtn);
        mOriginalImageText=findViewById(R.id.upload_originalKB);
        mProcessesImageText=findViewById(R.id.upload_processedKB);


        mDurationText.setText(selectedDuration);

        ArrayAdapter standerdAdapter=ArrayAdapter.createFromResource(this,R.array.class_name,android.R.layout.simple_spinner_dropdown_item);
        mStanderedSpinner.setAdapter(standerdAdapter);
        mStanderedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
             selectedStanderd=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gintent = new Intent();
                gintent.setType("image/*");
                gintent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(
                        Intent.createChooser(gintent, "Select Picture"),PICK_IMAGE_REQUEST);
            }});


        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mNameText,mAddressText,mSchoolText,mMobNoText,mImageUrl="2n38u1e",mId=pushId;

                mNameText=mNameEditText.getText().toString();
                mAddressText=mAddressEditText.getText().toString();
                mSchoolText=mSchoolEditText.getText().toString();
                mMobNoText=mMobNoEditText.getText().toString();

                //validate data entry
                if(mNameText.isEmpty()){
                    mNameEditText.setError("Please enter name");
                    return;
                }else if(mAddressText.isEmpty()){
                    mAddressEditText.setError("Please enter address");
                    return;
                }else if(mSchoolText.isEmpty()){
                    mSchoolEditText.setError("Please enter school");
                    return;
                }else if(mMobNoText.isEmpty() || mMobNoText.length()<10){
                    mMobNoEditText.setError("Please enter valid mobile number");
                    return;
                }

                ProfileDetails obj=new ProfileDetails(mNameText,mAddressText,mSchoolText,studentPhotoUrl,mMobNoText,pushId);
                uploadData(obj,pushId);


            }});
    }

    private void uploadData(ProfileDetails obj, String pushId) {


        ref.child(selectedStanderd+"("+selectedDuration+")").child("profile").child(pushId).setValue(obj).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"uploaded successfully!!!",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"uploaded failed!!!",Toast.LENGTH_SHORT).show();
                Log.d("guddi","error="+e.getMessage());
            }
        });
    }


    public void customCompressImage() {


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
        mImage.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
        mProcessesImageText.setText(String.format("Size : %s", getReadableFileSize(compressedImage.length())));

     //   Toast.makeText(this, "Compressed image save in " + compressedImage.getPath(), Toast.LENGTH_LONG).show();
        Log.d("Compressor", "Compressed image save in " + compressedImage.getPath());

        Bitmap studentPhoto = BitmapFactory.decodeFile(compressedImage.getAbsolutePath());
        mImage.setImageBitmap(studentPhoto);
        pushId=ref.push().getKey().toString();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Uri file = Uri.fromFile(compressedImage);
        StorageReference storageRef = storage.getReference();
        StorageReference studentPhotoRef = storageRef.child(selectedStanderd+"("+selectedDuration+")/"+pushId);
        UploadTask uploadTask = studentPhotoRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(),"Photo upload failed !",Toast.LENGTH_SHORT).show();
                studentPhotoUrl = "X";
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                studentPhotoUrl = taskSnapshot.getDownloadUrl().toString();
                Toast.makeText(getApplicationContext(),"Success"+studentPhotoUrl,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }




    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(),"Failed to open picture!",Toast.LENGTH_SHORT).show();
                return;
            }
            try {

                actualImage = FileUtil.from(this, data.getData());
                mImage.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                mOriginalImageText.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));

                //starts compressing
                customCompressImage();


            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Failed to read picture data!",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    }
}
