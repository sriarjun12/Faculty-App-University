package com.abort.facultycollege.EventBus;

import com.abort.facultycollege.Model.UserModel;

public class AttendenceClick {
    private boolean success;
    private UserModel userModel;

    public AttendenceClick(boolean success, UserModel userModel) {
        this.success = success;
        this.userModel = userModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }
}
