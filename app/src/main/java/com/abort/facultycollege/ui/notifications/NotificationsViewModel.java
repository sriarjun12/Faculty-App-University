package com.abort.facultycollege.ui.notifications;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.abort.facultycollege.CallBacks.IStudentCallBacksListener;
import com.abort.facultycollege.Common.Common;
import com.abort.facultycollege.Model.StudentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsViewModel extends ViewModel implements IStudentCallBacksListener {
    private MutableLiveData<List<StudentModel>> studentListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IStudentCallBacksListener studentCallBacksListener;

    public NotificationsViewModel() {
        studentCallBacksListener =this;
    }
    public MutableLiveData<List<StudentModel>> getStudentListMultable() {
        if(studentListMutable == null)
        {
            studentListMutable = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadStudent();
        }
        return studentListMutable;
    }

    public void loadStudent() {
        List<StudentModel> tempList = new ArrayList<>();
        DatabaseReference facultyref = FirebaseDatabase.getInstance().getReference(Common.STUDENTREF).child(Common.currentUserModel.getClassid());

        facultyref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot itemSnapShot : dataSnapshot.getChildren()) {
                        StudentModel studentModel = itemSnapShot.getValue(StudentModel.class);
                        tempList.add(studentModel);
                    }
                    if (tempList.size()>0)
                        studentCallBacksListener.onStudentLoadSuccess(tempList);
                    else
                        studentCallBacksListener.onStudentLoadFailed("Student Error!");
                }
                else{
                    studentCallBacksListener.onStudentLoadFailed("Student not exits !");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                studentCallBacksListener.onStudentLoadFailed(databaseError.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }
    @Override
    public void onStudentLoadSuccess(List<StudentModel> studentModelList) {
        studentListMutable.setValue(studentModelList);
    }

    @Override
    public void onStudentLoadFailed(String message) {
        messageError.setValue(message);
    }
}