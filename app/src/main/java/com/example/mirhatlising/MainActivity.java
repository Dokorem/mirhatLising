package com.example.mirhatlising;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mirhatlising.databinding.ActivityMainBinding;
import com.example.mirhatlising.enterPages.LoginActivity;
import com.example.mirhatlising.mainPages.ProfileFragment;
import com.example.mirhatlising.mainPages.TruckInfoFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new TruckInfoFragment());

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        binding.truckPageButton.setOnClickListener(v-> {
            replaceFragment(new TruckInfoFragment());
        });

        binding.profilePageButton.setOnClickListener(v-> {
            replaceFragment(new ProfileFragment());
        });

        binding.menuButtonsHolder.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int bottomInset = insets.getSystemWindowInsetBottom();
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.menuButtonsHolder.getLayoutParams();
                layoutParams.bottomMargin = bottomInset;
                binding.menuButtonsHolder.setLayoutParams(layoutParams);

                return insets;
            }
        });

    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentLayout, fragment).commit();
    }



}