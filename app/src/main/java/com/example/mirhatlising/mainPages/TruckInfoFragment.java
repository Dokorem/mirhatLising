package com.example.mirhatlising.mainPages;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.example.mirhatlising.FileOpener;
import com.example.mirhatlising.R;
import com.example.mirhatlising.databinding.FragmentTruckInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;



public class TruckInfoFragment extends Fragment {

    private FragmentTruckInfoBinding binding;
    private static final String linkToDatabase = "https://mirhatlising-de6b2-default-rtdb.europe-west1.firebasedatabase.app";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTruckInfoBinding.inflate(inflater, container, false);

        initForms();
        initDownloadButtons();

        return binding.getRoot();
    }

    private void initDownloadButtons() {

        Button downloadBidFileButton = binding.truckInfoDownloadBidFile,
                downloadPriceListFileButton = binding.truckInfoDownloadPriceListFile;

        downloadBidFileButton.setOnClickListener(v-> {
            requestStoragePermission();
            downloadBidFile();
        });

        downloadPriceListFileButton.setOnClickListener(v-> {
            requestStoragePermission();
            downloadPriceListFile();
        });

    }

    private void downloadBidFile() {

        String filename = "Форма заявки.docx";
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File dirPath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File bidFile = new File(dirPath, filename);

        try (InputStream is = getResources().openRawResource(R.raw.bid_form);
             OutputStream os = new FileOutputStream(bidFile)) {

            byte[] buffer = new byte[is.available()];
            int length;
            while((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            Toast.makeText(getContext(), "Файл сохранен: " + bidFile.getPath(), Toast.LENGTH_SHORT).show();
            FileOpener.openFile(getContext(), bidFile);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadPriceListFile() {

        String filename = "Прайс лист.docx";
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        File dirPath = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File priceListFile = new File(dirPath, filename);

        try (InputStream is = getResources().openRawResource(R.raw.price_list);
             OutputStream os = new FileOutputStream(priceListFile)) {

            byte[] buffer = new byte[is.available()];
            int length;
            while((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            Toast.makeText(getContext(), "Файл сохранен: " + priceListFile.getPath(), Toast.LENGTH_SHORT).show();
            FileOpener.openFile(getContext(), priceListFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initForms() {

        EditText truckUsernameInputField = binding.truckUsernameInputField,
                truckNumberInputField = binding.truckNumberInputField,
                truckQuestionUsernameInputField = binding.truckQuestionUsernameInputField,
                truckQuestionNumberInputField = binding.truckQuestionNumberInputField;
        Spinner truckSpinner = binding.truckSpinnerTypeOfTruck,
                truckQuestionSpinner = binding.truckQuestionSpinnerTypeOfTruck;

        String[] trucks = getResources().getStringArray(R.array.typesOfTrucks);

        truckSpinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item_with_blue_text, trucks));

        ArrayAdapter<String> truckQuestionAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_with_white_text, trucks) {

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.spinner_item_with_blue_text, parent, false);
                }
                TextView textView = convertView.findViewById(R.id.spinnerText);
                textView.setText(trucks[position]);
                return convertView;
            }

        };
        truckQuestionSpinner.setAdapter(truckQuestionAdapter);

        truckNumberInputField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        truckQuestionNumberInputField.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        binding.truckFirstInfoButton.setOnClickListener(v -> {

            String truckCallNumberValue = truckNumberInputField.getText().toString();
            String truckUsernameValue = truckUsernameInputField.getText().toString();
            String chosenTruck = truckSpinner.getSelectedItem().toString();

            if (!(truckUsernameValue.isEmpty() && truckCallNumberValue.isEmpty())) {

                truckUsernameInputField.setText("");
                truckNumberInputField.setText("");

                Toast.makeText(getContext(), "Ваш звонок отправлен! \nНаш менеджер скоро вам перезвонит!", Toast.LENGTH_SHORT).show();

                addCallToUser(truckCallNumberValue, truckUsernameValue, chosenTruck);

            }
        });

        binding.truckQuestionInfoButton.setOnClickListener(v -> {
            String truckCallNumberValue = truckQuestionNumberInputField.getText().toString();
            String truckUsernameValue = truckQuestionUsernameInputField.getText().toString();
            String chosenTruck = truckQuestionSpinner.getSelectedItem().toString();

            if (!(truckUsernameValue.isEmpty() && truckCallNumberValue.isEmpty())) {

                truckQuestionUsernameInputField.setText("");
                truckQuestionNumberInputField.setText("");

                Toast.makeText(getContext(), "Ваш звонок отправлен! \nНаш менеджер скоро вам перезвонит!", Toast.LENGTH_SHORT).show();

                addCallToUser(truckCallNumberValue, truckUsernameValue, chosenTruck);
            }
        });

    }

    private void addCallToUser(String truckCallNumberValue, String truckUsernameValue, String typeOfTruck) {

        SimpleDateFormat dateOfCallFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateOfCall = dateOfCallFormatter.format(new Date().getTime());

        SimpleDateFormat uidFormatter = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss-SSS");

        String callsUID = uidFormatter.format(new Date());
        String userUID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());


        HashMap<String, String> callInfo = new HashMap<>();
        callInfo.put("callNumber", truckCallNumberValue);
        callInfo.put("dateOfCall", dateOfCall);
        callInfo.put("nameOfCaller", truckUsernameValue);
        callInfo.put("typeOfTruck", typeOfTruck);

        FirebaseDatabase.getInstance(linkToDatabase)
                .getReference()
                .child("Calls")
                .child(callsUID)
                .setValue(callInfo);

        FirebaseDatabase
                .getInstance(linkToDatabase)
                .getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String calls = Objects.requireNonNull(snapshot.child("Users").child(userUID).child("calls").getValue()).toString();

                        FirebaseDatabase.getInstance(linkToDatabase)
                                .getReference()
                                .child("Users")
                                .child(FirebaseAuth
                                        .getInstance()
                                        .getCurrentUser()
                                        .getUid())
                                .child("calls")
                                .setValue(calls + (callsUID + ","));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



    }


}