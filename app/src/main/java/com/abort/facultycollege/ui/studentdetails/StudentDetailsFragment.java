package com.abort.facultycollege.ui.studentdetails;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abort.facultycollege.Common.Common;
import com.abort.facultycollege.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class StudentDetailsFragment extends Fragment {

    private StudentDetailsViewModel mViewModel;
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
    @BindView(R.id.details_roll)
    TextView details_roll;
    @BindView(R.id.details_department)
    TextView details_department;
    @BindView(R.id.details_class)
    TextView details_class;
    @BindView(R.id.details_parent_name)
    TextView details_parent_name;
    @BindView(R.id.details_parent_mobile)
    TextView details_parent_mobile;
    @BindView(R.id.details_address)
    TextView details_address;
    @BindView(R.id.btn_reset_password)
    Button btn_reset_password;
    public static StudentDetailsFragment newInstance() {
        return new StudentDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.student_details_fragment, container, false);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        unbinder = ButterKnife.bind(this,root);

        getProfiledata();

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        return root;
    }
    private void resetPassword() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());

        //Setting message manually and performing action on button click
        builder.setTitle("Rest Password ").setMessage("sure want to reset Password ") .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        dialog.show();
                        FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                                .child(Common.currentUserModel.getClassid())
                                .child(Common.studentSelected.getId())
                                .child("password")
                                .setValue(Common.defaultPasswrod).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                dialogg.dismiss();
                                Toast.makeText(getContext(), "Reset Failed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                dialogg.dismiss();
                                Toast.makeText(getContext(), "Reset Success", Toast.LENGTH_SHORT).show();

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
    private void getProfiledata() {
        if(Common.studentSelected.getName()!=null)
            details_name.setText(Common.studentSelected.getName());
        if(Common.studentSelected.getEmail()!=null)
            details_email.setText("Email : "+Common.studentSelected.getEmail());
        if(Common.studentSelected.getMobile()!=null)
            details_mobile.setText("Mobile : "+Common.studentSelected.getMobile());
        if(Common.studentSelected.getAddress()!=null)
            details_address.setText("Address : "+Common.studentSelected.getAddress());
        if(Common.studentSelected.getClassid()!=null)
            details_class.setText("Class ID : "+Common.studentSelected.getClassid());
        if(Common.studentSelected.getDepartment()!=null)
            details_department.setText("Department : "+ Common.studentSelected.getDepartment());
        if(Common.studentSelected.getRollno()!=null)
            details_roll.setText("Roll No : "+Common.studentSelected.getRollno());
        if(Common.studentSelected.getParentname()!=null)
            details_parent_name.setText("Parent Name : "+ Common.studentSelected.getParentname());
        if(Common.studentSelected.getParentmobile()!=null)
            details_parent_mobile.setText("Parent Number : "+Common.studentSelected.getParentmobile());


        if(Common.studentSelected.getProfile()!=null)
            Glide.with(getContext()).load(Common.studentSelected.getProfile())
                    .into(profile);
        else
            Glide.with(getContext()).load(R.drawable.student)
                    .into(profile);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StudentDetailsViewModel.class);
        // TODO: Use the ViewModel
    }

}