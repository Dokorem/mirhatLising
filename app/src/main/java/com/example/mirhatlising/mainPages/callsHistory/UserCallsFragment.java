package com.example.mirhatlising.mainPages.callsHistory;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mirhatlising.MainActivity;
import com.example.mirhatlising.databinding.FragmentProfileBinding;
import com.example.mirhatlising.databinding.FragmentUserCallsBinding;
import com.example.mirhatlising.mainPages.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UserCallsFragment extends Fragment {

    private FragmentUserCallsBinding binding;
    private static final String linkToDatabase = "https://mirhatlising-de6b2-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserCallsBinding.inflate(inflater, container, false);

        getCalls();

        binding.backToProfilePage.setOnClickListener(v-> {
            ((MainActivity) requireActivity()).replaceFragment(new ProfileFragment());
        });

        return binding.getRoot();
    }

    private List<Call> getCalls() {
        List<Call> callsList = new ArrayList<>();

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance(linkToDatabase)
                .getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String calls = Objects.requireNonNull(snapshot.child("Users").child(uid).child("calls").getValue()).toString();
                        String[] callsIds = calls.split(",");


                        for(String callId : callsIds) {
                            if(!callId.isEmpty()) {
                                DataSnapshot callSnapshot = snapshot.child("Calls").child(callId);
                                System.out.println(callId);

                                String callNumber = Objects.requireNonNull(callSnapshot.child("callNumber").getValue()).toString();
                                String dateOfCall = Objects.requireNonNull(callSnapshot.child("dateOfCall").getValue()).toString();
                                String typeOfTruck = Objects.requireNonNull(callSnapshot.child("typeOfTruck").getValue()).toString();

                                Call call = new Call(callNumber, dateOfCall, typeOfTruck);
                                callsList.add(call);
                            }
                        }

                        CallAdapter adapter = new CallAdapter(getActivity().getApplicationContext(), callsList);
                        binding.userCallsList.setAdapter(adapter);
                        binding.userCallsList.setLayoutManager(new LinearLayoutManager(getContext()));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        return callsList;
    }
}