package com.abort.facultycollege.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abort.facultycollege.CallBacks.IRecyclerClickListener;
import com.abort.facultycollege.Common.Common;
import com.abort.facultycollege.EventBus.StudentClick;
import com.abort.facultycollege.Model.StudentModel;
import com.abort.facultycollege.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder>{
    Context context;
    List<StudentModel> studentModelList;

    public StudentAdapter(Context context, List<StudentModel> studentModelList) {
        this.context = context;
        this.studentModelList = studentModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_student_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            holder.faculty_name.setText(new StringBuilder(studentModelList.get(position).getName()));
            holder.faculty_mobile.setText(new StringBuilder(studentModelList.get(position).getMobile()));
            holder.faculty_email.setText(new StringBuilder(studentModelList.get(position).getEmail()));
            //Event

        holder.setListener((view, pos) -> {
            Common.studentSelected = studentModelList.get(pos);
            EventBus.getDefault().postSticky(new StudentClick(true, studentModelList.get(pos)));
        });
    }

    @Override
    public int getItemCount() {
        return studentModelList.size();
    }
    public List<StudentModel> getListStudent() {
        return studentModelList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        Unbinder unbinder;
        @BindView(R.id.faculty_name)
        TextView faculty_name;
        @BindView(R.id.faculty_mobile)
        TextView faculty_mobile;
        @BindView(R.id.faculty_email)
        TextView faculty_email;
        IRecyclerClickListener listener;
        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(v,getAdapterPosition());
        }
    }
}
