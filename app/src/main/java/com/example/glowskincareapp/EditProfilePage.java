package com.example.glowskincareapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfilePage extends AppCompatActivity {

    FirebaseUser currentUser;
    ImageView set;
    TextView profilepic, editname, editpassword;
    ProgressDialog pd;
    private static final int CAMERA_PERMISSION_CODE=105;
    private boolean isSeller;

    String profilePhotoString;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_page);

        profilepic = findViewById(R.id.EditProfilePicProfilePage);
        editname = findViewById(R.id.editNameProfilePage);
        set = findViewById(R.id.setting_profile_image);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);

        editpassword = findViewById(R.id.changePasswordProfilePage);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        Intent callingIntent=getIntent();
        set.setImageBitmap(ImageStringOperation.getImage(callingIntent.getStringExtra("Picture")));
        isSeller=callingIntent.getBooleanExtra("isSeller",false);

        editpassword.setOnClickListener(v -> {
            pd.setMessage("Changing Password");
            showPasswordChangeDialog();
        });

        profilepic.setOnClickListener(v -> {
            if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,CAMERA_PERMISSION_CODE)){
                getAndSetImageFromGallery();
                if(isSeller)
                    pd.setMessage("Updating Brand Logo");
                else
                    pd.setMessage("Updating Profile Picture");
            }
        });

        editname.setOnClickListener(v -> {
            pd.setMessage("Updating Name");
            updateNameWithDialog();
        });
        editpassword.setOnClickListener(v -> {
            pd.setMessage("Changing Password");
            showPasswordChangeDialog();
        });
    }

    // We will show an alert box where we will write our old and new password
    private void showPasswordChangeDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_password, null);
        final EditText oldpass = view.findViewById(R.id.oldpasslog);
        final EditText newpass = view.findViewById(R.id.newpasslog);
        Button editpass = view.findViewById(R.id.updatepass);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        editpass.setOnClickListener(v -> {
            String oldp = oldpass.getText().toString().trim();
            String newp = newpass.getText().toString().trim();
            if (TextUtils.isEmpty(oldp)) {
                Toast.makeText(EditProfilePage.this, "Current Password cant be empty", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(newp)) {
                Toast.makeText(EditProfilePage.this, "New Password cant be empty", Toast.LENGTH_LONG).show();
                return;
            }
            dialog.dismiss();
            updatePassword(oldp, newp);
        });
    }

    // Now we will check that if old password was authenticated
    // correctly then we will update the new password
    private void updatePassword(String oldp, final String newp) {
        pd.show();
        AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(currentUser.getEmail()), oldp);
        currentUser.reauthenticate(authCredential)
                .addOnSuccessListener(aVoid -> currentUser.updatePassword(newp)
                        .addOnSuccessListener(aVoid1 -> {
                            pd.dismiss();
                            Toast.makeText(EditProfilePage.this, "Password Changed", Toast.LENGTH_LONG).show();
                        }).addOnFailureListener(e -> {
                            pd.dismiss();
                            Toast.makeText(EditProfilePage.this, "Password Change Failed", Toast.LENGTH_LONG).show();
                        })).addOnFailureListener(e -> {
                            pd.dismiss();
                            Toast.makeText(EditProfilePage.this, "Password Change Failed", Toast.LENGTH_LONG).show();
                        });
    }

    // Updating name
    private void updateNameWithDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.namechangedialog, null);
        EditText firstName = view.findViewById(R.id.firstNameInNameChangeDialog);
        EditText lastName = view.findViewById(R.id.lastNameInNameChangeDialog);
        Button positive = view.findViewById(R.id.positiveButtonInNameChangeDialog);
        Button negative = view.findViewById(R.id.negativeButtonInNameChangeDialog);
        TextView headline = view.findViewById(R.id.headlineInNameChangeDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog=builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            if (isSeller) {
                headline.setText("Update Brand Name");
                firstName.setHint("Enter new Brand Name");
                lastName.setVisibility(View.GONE);
            } else {
                headline.setText("Update Name");
                firstName.setHint("Enter new First Name");
                lastName.setHint("Enter new Last Name");
            }
            positive.setOnClickListener(view12 -> {
                final String fname = firstName.getText().toString().trim();
                final String lname = lastName.getText().toString().trim();
                if (isSeller) {
                    if (TextUtils.isEmpty(fname)) {
                        Toast.makeText(EditProfilePage.this, "Brand Name Cannot be Empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (TextUtils.isEmpty(fname)) {
                        Toast.makeText(EditProfilePage.this, "First Name cannot be Empty", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(lname)) {
                        Toast.makeText(EditProfilePage.this, "Last Name cannot be Empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                pd.show();

                // Here we are updating the new name
                HashMap<String, Object> result = new HashMap<>();
                if (isSeller)
                    result.put("BrandName", fname);
                else {
                    result.put("FirstName", fname);
                    result.put("LastName", lname);
                }
                firestore.collection("Users").document(currentUser.getUid()).update(result).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        Toast.makeText(EditProfilePage.this, "Name Changed", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        pd.dismiss();
                        Toast.makeText(EditProfilePage.this, "Name Change Error", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(EditProfilePage.this, "Name Change Error", Toast.LENGTH_SHORT).show();
                });
            });

            negative.setOnClickListener(view1 -> dialog.dismiss());
        });
        dialog.show();
    }
    public boolean checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
        else {
            return true;
        }
        return false;
    }
    private void getAndSetImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        launchSomeActivity.launch(intent);
    }
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult( new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap;
                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            selectedImageBitmap=ImageStringOperation.getCompressedBitmap(selectedImageBitmap);
                            set.setImageBitmap(selectedImageBitmap);
                            profilePhotoString=ImageStringOperation.getString(selectedImageBitmap);

                            Map<String, Object> map=new HashMap<>();
                            if(isSeller){
                                map.put("BrandLogo",profilePhotoString);
                            }
                            else{
                                map.put("ProfilePic",profilePhotoString);
                            }
                            firestore.collection("Users").document(currentUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        pd.dismiss();
                                        if(isSeller)
                                            Toast.makeText(EditProfilePage.this, "Brand Logo Updated Successfully", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(EditProfilePage.this, "Profile Picture Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        pd.dismiss();
                                        Toast.makeText(EditProfilePage.this, "Changing Picture Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(e -> Toast.makeText(EditProfilePage.this, "Changing Picture Failed", Toast.LENGTH_SHORT).show());
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAndSetImageFromGallery();
            } else {
                Toast.makeText(this, "Error: You need to grant storage permission to upload Image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
