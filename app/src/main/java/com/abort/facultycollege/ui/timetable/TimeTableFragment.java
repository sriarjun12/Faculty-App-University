package com.abort.facultycollege.ui.timetable;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.abort.facultycollege.Common.Common;
import com.abort.facultycollege.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class TimeTableFragment extends Fragment {

    private TimeTableViewModel mViewModel;
    Unbinder unbinder;
    AlertDialog dialog;
    @BindView(R.id.time_table)
    ImageView time_table;
    @BindView(R.id.btn_update)
    Button btn_update;
    private Uri imageUrl=null;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1234;
    public static TimeTableFragment newInstance() {
        return new TimeTableFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.time_table_fragment, container, false);
        unbinder = ButterKnife.bind(this,root);
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        FirebaseDatabase.getInstance().getReference(Common.TIMETABLEREF)
                .child(Common.currentUserModel.getClassid())
                .child("timetable")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()!=null)
                            Glide.with(getContext()).load(snapshot.getValue().toString())
                                    .into(time_table);
                        else
                            Glide.with(getContext()).load(R.drawable.timetable)
                                    .into(time_table);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        time_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            updateData.put("timetable",uri.toString());
                            FirebaseDatabase.getInstance().getReference(Common.TIMETABLEREF)
                                    .child(Common.currentUserModel.getClassid())
                                    .updateChildren(updateData)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getContext(), "Update Success", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
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
                    return;
                    //addCategory(categoryModel);
                }
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TimeTableViewModel.class);
        // TODO: Use the ViewModel
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST &&  resultCode== Activity.RESULT_OK){
            if (data != null && data.getData() != null ){
                imageUrl=data.getData();
                time_table.setImageURI(imageUrl);
            }
        }
    }
}