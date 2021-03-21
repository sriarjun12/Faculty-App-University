package com.abort.facultycollege.ui.notifications;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abort.facultycollege.Adapter.StudentAdapter;
import com.abort.facultycollege.Common.Common;
import com.abort.facultycollege.Common.MySwipeHelper;
import com.abort.facultycollege.Model.StudentModel;
import com.abort.facultycollege.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

import static android.Manifest.permission.CALL_PHONE;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    Unbinder unbinder;
    @BindView(R.id.recycler_student)
    RecyclerView recycler_student;
    AlertDialog dialog;
    StudentAdapter adapter;
    List<StudentModel> studentModels;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        unbinder = ButterKnife.bind(this,root);
        initViews();
        notificationsViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), ""+s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        });
        notificationsViewModel.getStudentListMultable().observe(getViewLifecycleOwner(),studentModelList -> {
            dialog.dismiss();
            studentModels=studentModelList;
            adapter = new StudentAdapter(getContext(),studentModels);
            recycler_student.setAdapter(adapter);
        });
        return root;
    }

    private void initViews() {



        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        //dialog.show();

        //GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_student.setLayoutManager(layoutManager);
        recycler_student.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));
        MySwipeHelper swipeHelper=new MySwipeHelper(getContext(),recycler_student,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {

                buf.add(new MyButton(getContext(),"Delete",35,0, Color.parseColor("#ed3f32"),
                        pos ->{
                            Common.studentSelected=studentModels.get(pos);
                            showDeleteDialog();
                        }));
                buf.add(new MyButton(getContext(),"Update",35,0, Color.parseColor("#88c057"),
                        pos ->{
                            Common.studentSelected=studentModels.get(pos);
                            showUpdateDialog();
                        }));
                buf.add(new MyButton(getContext(),"Call",35,0, Color.parseColor("#88c057"),
                        pos ->{
                            Common.studentSelected=studentModels.get(pos);
                            callStudent();
                        }));
            }
        };

        setHasOptionsMenu(true);
    }

    private void showUpdateDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder=new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update Student");
        builder.setMessage("Please enter Information");
        View itemView =LayoutInflater.from(getContext()).inflate(R.layout.update_layout,null);
        EditText edt_name=(EditText)itemView.findViewById(R.id.edt_name);
        EditText edt_mobile=(EditText)itemView.findViewById(R.id.edt_mobile);
        EditText edt_mail=(EditText)itemView.findViewById(R.id.edt_mail);
        EditText edt_roll=(EditText)itemView.findViewById(R.id.edt_rollno);

        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialog.dismiss());
        builder.setPositiveButton("ADD", (dialogInterface, i) -> {



            if(!TextUtils.isEmpty(edt_mail.getText().toString())&&!TextUtils.isEmpty(edt_roll.getText().toString())&&!TextUtils.isEmpty(edt_name.getText().toString())&&!TextUtils.isEmpty(edt_mobile.getText().toString())){
                StudentModel studentModel=new StudentModel();
                studentModel.setName(edt_name.getText().toString());
                studentModel.setEmail(edt_mail.getText().toString());
                studentModel.setRollno(edt_roll.getText().toString());
                studentModel.setMobile(edt_mobile.getText().toString());
                studentModel.setPassword(Common.defaultPasswrod);
                studentModel.setId("+91"+edt_mobile.getText().toString());
                FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                        .child(studentModel.getId())
                        .setValue(studentModel)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                                        .child(Common.studentSelected.getId())
                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Add Success", Toast.LENGTH_SHORT).show();
                                        edt_name.setText("");
                                        edt_mail.setText("");
                                        edt_mobile.setText("");
                                        Common.studentSelected=studentModel;
                                        notificationsViewModel.loadStudent();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

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

    private void showDeleteDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getContext());
        //Setting message manually and performing action on button click
        builder.setTitle("Delete Student").setMessage("Sure want to Delete this Student") .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogg, int id) {
                        dialog.show();
                        FirebaseDatabase.getInstance().getReference(Common.STUDENTREF)
                                .child(Common.studentSelected.getId())
                                .removeValue().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                dialogg.dismiss();
                                Toast.makeText(getContext(), "Delete Failed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                dialogg.dismiss();
                                Toast.makeText(getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                                notificationsViewModel.loadStudent();
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

    private void callStudent() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+Common.studentSelected.getMobile()));//change the number
        if (ContextCompat.checkSelfPermission(getContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
        } else {
            requestPermissions(new String[]{CALL_PHONE}, 1);
        }
    }
}