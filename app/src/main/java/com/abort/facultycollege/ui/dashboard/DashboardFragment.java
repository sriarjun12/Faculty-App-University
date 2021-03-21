package com.abort.facultycollege.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abort.facultycollege.Common.Common;
import com.abort.facultycollege.Model.StudentModel;
import com.abort.facultycollege.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    Unbinder unbinder;
    @BindView(R.id.edt_name)
    EditText edt_name;
    @BindView(R.id.edt_mobile)
    EditText edt_mobile;
    @BindView(R.id.edt_mail)
    EditText edt_mail;
    @BindView(R.id.edt_rollno)
    EditText edt_rollno;
    @BindView(R.id.add_student)
    Button add_student;
    AlertDialog dialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        unbinder = ButterKnife.bind(this,root);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        add_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.currentUserModel.getClassid()!=null){
                if(!TextUtils.isEmpty(edt_mail.getText().toString())&&!TextUtils.isEmpty(edt_rollno.getText().toString())&&!TextUtils.isEmpty(edt_name.getText().toString())&&!TextUtils.isEmpty(edt_mobile.getText().toString())) {
                    StudentModel studentModel = new StudentModel();
                    studentModel.setName(edt_name.getText().toString());
                    studentModel.setMobile(edt_mobile.getText().toString());
                    studentModel.setEmail(edt_mail.getText().toString());
                    studentModel.setRollno(edt_rollno.getText().toString());
                    studentModel.setClassid(Common.currentUserModel.getClassid());
                    studentModel.setPassword(Common.defaultPasswrod);
                    addStudent(studentModel);
                }
                else{
                    Toast.makeText(getContext(), "Enter All details", Toast.LENGTH_SHORT).show();
                }}
                else{
                    Toast.makeText(getContext(), "Enter Class ID and Update the profile", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }
    private void addStudent(StudentModel studentModel) {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        //Setting message manually and performing action on button click
        builder.setTitle("Add Student").setMessage("Sure want ot add this Student") .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        dialog.show();
                        studentModel.setId("+91"+studentModel.getMobile());
                        FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                                .child(Common.currentUserModel.getClassid())
                                .child(studentModel.getId())
                                .setValue(studentModel)
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
                                        dialog.dismiss();
                                        Toast.makeText(getContext(), "Add Success", Toast.LENGTH_SHORT).show();
                                        edt_name.setText("");
                                        edt_mail.setText("");
                                        edt_mobile.setText("");
                                        edt_rollno.setText("");
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

}