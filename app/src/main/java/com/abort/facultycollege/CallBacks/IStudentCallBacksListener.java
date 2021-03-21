package com.abort.facultycollege.CallBacks;

import com.abort.facultycollege.Model.StudentModel;

import java.util.List;

public interface IStudentCallBacksListener {
    void onStudentLoadSuccess(List<StudentModel> studentModelList);
    void onStudentLoadFailed(String message);
}
