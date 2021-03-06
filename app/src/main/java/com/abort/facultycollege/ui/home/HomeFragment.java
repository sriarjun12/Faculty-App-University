package com.abort.facultycollege.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.abort.facultycollege.Common.Common;
import com.abort.facultycollege.EventBus.NotesClick;
import com.abort.facultycollege.EventBus.ProfileClick;
import com.abort.facultycollege.EventBus.TimeTableClick;
import com.abort.facultycollege.Model.BannerModel;
import com.abort.facultycollege.R;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    Unbinder unbinder;
    @BindView(R.id.image_sliders)
    ImageSlider mainSlider;
    @BindView(R.id.edt_announce)
    TextView edt_announce;
    @BindView(R.id.notes_id)
    LinearLayout notes_id;
    @BindView(R.id.time_table_id)
    LinearLayout time_table_id;
    @BindView(R.id.profile_id)
    LinearLayout profile_id;
    @BindView(R.id.attendence_id)
    LinearLayout attendence_id;

    AlertDialog dialog;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this,root);
        checkAnnouncement();
        updateBanner();
        notes_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new NotesClick(true,Common.currentUserModel));
            }
        });
        time_table_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new TimeTableClick(true,Common.currentUserModel));
            }
        });
        profile_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new ProfileClick(true,Common.currentUserModel));
            }
        });
        attendence_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return root;
    }

    private void updateBanner(){
        final List<SlideModel> remoteImages = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child(Common.BANNERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data:snapshot.getChildren())
                        {
                            BannerModel bannerModel=new BannerModel();
                            bannerModel.setImage(data.child("image").getValue().toString());
                            bannerModel.setKey(data.getKey());

                            remoteImages.add(new SlideModel(data.child("image").getValue().toString(),"", ScaleTypes.CENTER_CROP));
                        }
                        mainSlider.setImageList(remoteImages,ScaleTypes.CENTER_CROP);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
    private void checkAnnouncement() {
        FirebaseDatabase.getInstance().getReference(Common.ANNOUNCEMENT)
                .child("text")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.getValue()!=null){
                            edt_announce.setText(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        updateBanner();
        checkAnnouncement();
    }
    @Override
    public void onResume() {
        super.onResume();
        updateBanner();
        checkAnnouncement();
    }

}