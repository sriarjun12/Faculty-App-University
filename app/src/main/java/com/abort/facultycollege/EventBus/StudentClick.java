package com.abort.facultycollege.EventBus;

import com.abort.facultycollege.Model.StudentModel;
import com.abort.facultycollege.Model.UserModel;

public class StudentClick {
    private boolean success;
    private StudentModel userModel;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public StudentModel getUserModel() {
        return userModel;
    }

    public void setUserModel(StudentModel userModel) {
        this.userModel = userModel;
    }

    public StudentClick(boolean success, StudentModel userModel) {
        this.success = success;
        this.userModel = userModel;
    }
}
