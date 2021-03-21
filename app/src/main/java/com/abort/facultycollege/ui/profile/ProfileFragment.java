package com.abort.facultycollege.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abort.facultycollege.Common.Common;
import com.abort.facultycollege.MainActivity;
import com.abort.facultycollege.Model.StudentModel;
import com.abort.facultycollege.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    Unbinder unbinder;
    AlertDialog dialog;
    @BindView(R.id.profile)
    ImageView profile;
    @BindView(R.id.details_name)
    TextView details_name;
    @BindView(R.id.details_email)
    TextView details_email;
    @BindView(R.id.details_mobile)
    TextView details_mobile;
    @BindView(R.id.details_tutor)
    TextView details_tutor;
    @BindView(R.id.details_designation)
    TextView details_designation;
    @BindView(R.id.details_class)
    TextView details_class;
    @BindView(R.id.details_subject)
    TextView details_subject;
    @BindView(R.id.details_address)
    TextView details_address;
    @BindView(R.id.btn_change_password)
    Button btn_change_password;
    @BindView(R.id.btn_edt)
    Button btn_edt;
    @BindView(R.id.sign_out)
    Button sign_out;

    private Uri imageUrl=null;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1234;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.profile_fragment, container, false);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        unbinder = ButterKnife.bind(this,root);
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        getProfiledata();
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changepasswordDialog();
            }
        });
        btn_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sowEditDialog();
            }
        });
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignoutdialog();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
            }
        });
        return root;
    }

    private void getProfiledata() {
        if(Common.currentUserModel.getName()!=null)
            details_name.setText(Common.currentUserModel.getName());
        if(Common.currentUserModel.getEmail()!=null)
            details_email.setText("Email : "+Common.currentUserModel.getEmail());
        if(Common.currentUserModel.getMobile()!=null)
            details_mobile.setText("Mobile : "+Common.currentUserModel.getMobile());
        if(Common.currentUserModel.getAddress()!=null)
            details_address.setText("Address : "+Common.currentUserModel.getAddress());
        if(Common.currentUserModel.getClassid()!=null)
            details_class.setText("Class ID : "+Common.currentUserModel.getClassid());
        if(Common.currentUserModel.getDesignation()!=null)
            details_designation.setText("Designation : "+ Common.currentUserModel.getDesignation());
        if(Common.currentUserModel.getDepartment()!=null)
            details_subject.setText("Subject : "+Common.currentUserModel.getDepartment());
        if(Common.currentUserModel.isTutor())
            details_tutor.setText("Tutor : Yes");
        else
            details_tutor.setText("Tutor : No");

        if(Common.currentUserModel.getProfile()!=null)
            Glide.with(getContext()).load(Common.currentUserModel.getProfile())
                    .into(profile);
        else
            Glide.with(getContext()).load(R.drawable.teacher)
                    .into(profile);

    }

    private void sowEditDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update Faculty");
        builder.setMessage("Please enter Information");
        View itemView =LayoutInflater.from(getContext()).inflate(R.layout.edit_profile_layout,null);
        EditText edt_class_id=(EditText)itemView.findViewById(R.id.edt_class_id);
        EditText edt_designation=(EditText)itemView.findViewById(R.id.edt_designation);
        EditText edt_department=(EditText)itemView.findViewById(R.id.edt_department);
        EditText edt_addresss=(EditText)itemView.findViewById(R.id.edt_addresss);
        RadioButton yes=(RadioButton)itemView.findViewById(R.id.yes) ;
        RadioButton no=(RadioButton)itemView.findViewById(R.id.no) ;
        if(Common.currentUserModel.isTutor()){
            yes.setChecked(true);
        }
        if(Common.currentUserModel.getClassid()!=null) {
            edt_class_id.setText(Common.currentUserModel.getClassid());
            edt_designation.setText(Common.currentUserModel.getDesignation());
            edt_department.setText(Common.currentUserModel.getDepartment());
            edt_addresss.setText(Common.currentUserModel.getAddress());
        }
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {

            if(!TextUtils.isEmpty(edt_class_id.getText().toString())&&!TextUtils.isEmpty(edt_designation.getText().toString())&&!TextUtils.isEmpty(edt_department.getText().toString())&&!TextUtils.isEmpty(edt_addresss.getText().toString())){

                Common.currentUserModel.setClassid(edt_class_id.getText().toString());
                Common.currentUserModel.setDesignation(edt_designation.getText().toString());
                Common.currentUserModel.setDepartment(edt_department.getText().toString());
                Common.currentUserModel.setAddress(edt_addresss.getText().toString());
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("classid",edt_class_id.getText().toString());
                updateData.put("designation",edt_designation.getText().toString());
                updateData.put("address",edt_addresss.getText().toString());
                updateData.put("department",edt_department.getText().toString());
                if(yes.isChecked())
                    updateData.put("tutor",true);
                else
                    updateData.put("tutor",false);
                dialog.show();
                FirebaseDatabase.getInstance().getReference(Common.FACULTYREF)
                        .child(Common.currentUserModel.getId())
                        .updateChildren(updateData)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                                getProfiledata();
                                dialog.dismiss();
                            }
                        });
            }

            else
                Toast.makeText(getContext(), "Enter all Details", Toast.LENGTH_SHORT).show();
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog=builder.create();
        builder.show();
    }

    private void showSignoutdialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        //Setting message manually and performing action on button click
        builder.setTitle("Signout").setMessage("Sure want to Signout") .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        dialog.show();
                        FirebaseDatabase.getInstance().getReference(Common.FACULTYREF)
                                .child(Common.currentUserModel.getId())
                                .child("uid")
                                .setValue(null)
                                .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                dialogg.dismiss();
                                Toast.makeText(getContext(), "Signout Failed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                dialogg.dismiss();

                                Toast.makeText(getContext(), "Signout Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getContext(), MainActivity.class));

                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        //  Action for 'NO' Button
                        dialogg.dismiss();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }
    private void changepasswordDialog()  {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Change Password");
        View itemView =LayoutInflater.from(getContext()).inflate(R.layout.single_text_layout,null);
        EditText edt_password=(EditText)itemView.findViewById(R.id.edt_password);
        EditText edt_password_cfn=(EditText)itemView.findViewById(R.id.edt_password_cfn);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {
            if(edt_password.getText().toString().equals(edt_password_cfn.getText().toString())){
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("password",edt_password.getText().toString());
                FirebaseDatabase.getInstance().getReference(Common.FACULTYREF)
                .child(Common.currentUserModel.getId())
                .updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Password Change Success", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                Toast.makeText(getContext(), "Password Mismatch", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog=builder.create();
        builder.show();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST &&  resultCode== Activity.RESULT_OK){
            if (data != null && data.getData() != null ){
                imageUrl=data.getData();
                updateprofileimage();
            }
        }
    }

    private void updateprofileimage() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        //Setting message manually and performing action on button click
        builder.setTitle("Profile").setMessage("Sure want to Update Profile") .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        dialog.show();
                        if(imageUrl!=null){
                            dialog.setMessage("Uploading ...");
                            dialog.show();
                            String unique_name= UUID.randomUUID().toString();
                            StorageReference imageFloder=storageReference.child("image/"+unique_name);
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUrl);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                            byte[] data = baos.toByteArray();

                            imageFloder.putBytes(data)
                                    .addOnFailureListener(e -> {
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }).addOnCompleteListener(task -> {
                                dialog.dismiss();
                                imageFloder.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Map<String, Object> updateData = new HashMap<>();
                                    updateData.put("profile",uri.toString());
                                    Common.currentUserModel.setProfile(uri.toString());
                                    FirebaseDatabase.getInstance().getReference(Common.FACULTYREF)
                                            .child(Common.currentUserModel.getId())
                                            .updateChildren(updateData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getContext(), "Update Success", Toast.LENGTH_SHORT).show();
                                                    getProfiledata();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                                            getProfiledata();
                                        }
                                    });
                                });

                            }).addOnProgressListener(taskSnapshot -> {
                                int progress=(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());

                                dialog.setMessage(new StringBuilder("Uploading : ").append(progress).append("%"));

                            });
                        }
                        else{
                            Toast.makeText(getContext(), "Select Image", Toast.LENGTH_SHORT).show();
                            getProfiledata();
                            return;
                            //addCategory(categoryModel);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        //  Action for 'NO' Button
                        dialogg.dismiss();
                        getProfiledata();

                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }
}