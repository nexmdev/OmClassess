package ltd.akhbod.omclasses;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mvc.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
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
    private EditText mNameEditText,mAddressEditText,mSchoolEditText,mMobNoEditText,mDurationText;
    private ImageView mImage;
    private TextView mOriginalImageText,mProcessesImageText;
    private Button mUploadBtn;
    private Spinner mStanderedSpinner;
    private String pushId,studentPhotoUrl = "X";
    int currentYear;

    //firebase variables
    private DatabaseReference ref,garbageRef;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);


        ref= FirebaseDatabase.getInstance().getReference();
        garbageRef=FirebaseDatabase.getInstance().getReference().child("DataManage").child("isMigrated_Deleted");
        garbageRef.keepSynced(false);
        currentYear= Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        int nextYear=currentYear+1;
        selectedDuration=currentYear+"-"+nextYear;

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

        selectedStanderd="11th";
        
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }});


        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mNameText,mAddressText,mSchoolText,mMobNoText,mImageUrl="2n38u1e",mId=pushId;
                pushId=ref.push().getKey().toString();

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

                checkMigrationDeletion(obj,pushId);
            }});

    }


    /*
    *
    *   uploading profile data
    *
    */

    private void uploadData(final ProfileDetails obj, final String pushId) {

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);

                if (connected) {                                                                    //when online


                    ref.child(selectedStanderd+"("+mDurationText.getText()+")").child("profile").child(pushId).setValue(obj)


                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"uploaded successfully",Toast.LENGTH_SHORT).show();
                            cleanUpPage();
                        }})


                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"uploaded failed!!!",Toast.LENGTH_SHORT).show();
                            Log.d("guddi","error="+e.getMessage());

                        }});
                }

                else                                                                                //when offline
                    {

                    Toast.makeText(getApplicationContext(),"offline uploaded successfully",Toast.LENGTH_SHORT).show();

                    ref.child(selectedStanderd+"("+mDurationText.getText()+")").child("profile").child(pushId).setValue(obj)


                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {}})                      //sucess and failure body never excute


                            .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {}});

                        cleanUpPage();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }});





    }


    /*
    *   reset all infomation on pages to blank
    *
    */

    private void cleanUpPage() {

        mNameEditText.setText("");
        mAddressEditText.setText("");
        mMobNoEditText.setText("");
        studentPhotoUrl="";

    }




    /*
    *   check if migrating and deleting classes are present or not,
    *   if yes migrate 11th and delete 12th prevoius batches
    *
    */

    private void checkMigrationDeletion(final ProfileDetails obj, final String pushId) {

          String[] parts=mDurationText.getText().toString().split("-");
          final String keyToCheck="("+(Integer.parseInt(parts[0])-1)+"-"+(Integer.parseInt(parts[0]))+")".replace(" ","");



          garbageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int temp=0;
                for (  DataSnapshot snap : dataSnapshot.getChildren()) {

                    Log.d("snap",snap.getKey());
                    String[] parts=snap.getKey().split("th");

                    if(keyToCheck.equals(parts[1])) {

                        if (parts[0].equals("11") || parts[0].equals("12")) {
                            migrateAndDeleteDialog(snap.getKey());}
                            break;
                    }

                    else if(temp==0){                       //will true only when there is no migrate or detected at temp=0
                           uploadData(obj, pushId);}        //trigeering uploadData() at temp>0 will have anologous behaviour

                    temp++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}});
    }



    /*
    *   Alert Dialog for:
    *   1.Deleting previous 11th batch node and creating new 12th batch node of next year having same profile node.
    *   2.Deleting previous 12th batch node and making sepeate only profile node in garbage node.
    *
   */

    private void migrateAndDeleteDialog(final String keyToMigrate) {

        final String keyToDelete=keyToMigrate.replace("11","12");

        AlertDialog.Builder builder=new AlertDialog.Builder(UploadActivity.this);
        builder.setTitle("Migration & Deletion");
        builder.setMessage("1. "+keyToMigrate+" will migrate to next year\n2. "+keyToDelete+" will be delete \n"+
                            "(  Note:students profile data will be added to recycle node)");
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                final String newNodeKey12="12th"+"("+mDurationText.getText().toString()+")";
                final String newNodeKey11="11th"+"("+mDurationText.getText().toString()+")";


                ref.child(keyToMigrate).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {                              ////Migrating

                        ref.child(newNodeKey12).setValue(dataSnapshot.getValue());

                        dataSnapshot.getRef().setValue(null);

                        ref.child("DataManage/isMigrated_Deleted").child(keyToMigrate).setValue(null);
                        ref.child("DataManage/isMigrated_Deleted").child(newNodeKey12).setValue("no");
                        ref.child("DataManage/isMigrated_Deleted").child(newNodeKey11).setValue("no");
                        Toast.makeText(getApplicationContext(),"Migrating "+keyToMigrate+" to "+newNodeKey11
                                +" successfull",Toast.LENGTH_SHORT).show();

                        ref.child(keyToDelete).addListenerForSingleValueEvent(new ValueEventListener() {    ////Deleting
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                                {
                                    if(snapshot.getKey().equals("profile"))
                                    {
                                        ref.child("DataManage/recyclebin").child(keyToDelete).child("profile")  //creating node in garbage
                                                .setValue(snapshot.getValue());

                                    }

                                    snapshot.getRef().setValue(null);
                                }

                                ref.child("DataManage/isMigrated_Deleted").child(keyToDelete).setValue(null);  //deleting othe rnodes
                                Toast.makeText(getApplicationContext(),"Deleting "+keyToDelete+" to successfull",Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}});

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}});

            }});

        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}});

        Dialog dialog=builder.create();
        dialog.show();


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
        StorageReference studentPhotoRef = storageRef.child(selectedStanderd+"("+mDurationText.getText()+")/"+pushId);
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



    private void checkPermission() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()==true){
                    ImagePicker.pickImage(UploadActivity.this, "Select your image:");
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
            mImage.setImageBitmap(bitmap);
            try {
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "Title", null);
                actualImage = FileUtil.from(this,Uri.parse(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOriginalImageText.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));

            //starts compressing
            customCompressImage();
        }


    }
}
