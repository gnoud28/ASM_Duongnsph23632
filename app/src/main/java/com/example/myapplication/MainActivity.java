package com.example.myapplication;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.myapplication.api.ApiNasa;
import com.example.myapplication.api.Apiiresspot;
import com.example.myapplication.api.Apiserver;
import com.example.myapplication.model.HackNasa;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Spinner spnYear, spnMonth, spnDate;
    private TextView tvTitle, tvDate, tvExplanation, tvNotification;
    private HackNasa hackNasa;
    private static final String API_KEY = "dVNjATYCz98wtEylqmXhCMRGbLFdNOSloHVBXvsR";
    private ApiNasa apiNasa;
    String base64UrlHd;
    String base64url;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    Button button;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }
    private String dateSelected, daySelected, monthSelected, yearSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hackNasa = new HackNasa();
        initViews();
        checkandout();



    }
public void checkandout(){
    mAuthListner = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if (firebaseAuth.getCurrentUser()==null)
            {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        }
    };
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mAuth.signOut();
        }
    });
}

    private void initViews() {
        spnYear = findViewById(R.id.spn_year);
        spnMonth = findViewById(R.id.spn_month);
        spnDate = findViewById(R.id.spn_date);
        tvTitle = findViewById(R.id.tv_title);
        tvDate = findViewById(R.id.tv_date);
        tvExplanation = findViewById(R.id.tv_explanation);
        tvNotification = findViewById(R.id.tv_notification);
        button=findViewById(R.id.btn_logout);
        mAuth = FirebaseAuth.getInstance();

        List<String> days = new ArrayList<>();

        for (int i = 1; i <= 31; i++) {
            days.add(String.valueOf(i));
        }
        List<String> months = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            months.add(String.valueOf(i));
        }
        List<String> years = new ArrayList<>();

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 100; i--) {
            years.add(String.valueOf(i));
        }
        days.add(0, "days");
        months.add(0, "months");
        years.add(0, "years");

        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);

        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnYear.setAdapter(yearsAdapter);
        spnMonth.setAdapter(monthsAdapter);
        spnDate.setAdapter(daysAdapter);

        spnDate.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spnYear.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spnMonth.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        findViewById(R.id.btn_get_nasa).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callApiGetDataFormNasa(API_KEY, dateSelected);
            }
        });

        findViewById(R.id.layout_show_data).setVisibility(View.GONE);

        findViewById(R.id.btn_push_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToServer();
            }
        });

        findViewById(R.id.btn_get_data_form_my_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Getdata_activity.class));
            }
        });

    }

    private void callApiGetDataFormNasa(String api_key, String date) {
        apiNasa = Apiiresspot.getApiNasa();
        apiNasa.getDataFromNasa(api_key, date).enqueue(new Callback<HackNasa>() {
            @Override
            public void onResponse(Call<HackNasa> call, Response<HackNasa> response) {
                hackNasa = response.body();
                findViewById(R.id.layout_show_data).setVisibility(View.VISIBLE);
                tvTitle.setText(hackNasa.getTitle());
                tvDate.setText(hackNasa.getDate());
                tvExplanation.setText(hackNasa.getExplanation());
                ImageView imgHd = findViewById(R.id.img_hd);
                if (hackNasa.getHdurl() != null) {
                    Glide.with(MainActivity.this).load(hackNasa.getHdurl()).error(R.drawable.baseline_error_24).into(imgHd);
                } else {
                    Glide.with(MainActivity.this).load(hackNasa.getUrl()).error(R.drawable.baseline_error_24).into(imgHd);
                }
                tvNotification.setText("get data from Nasa successfully");
                tvNotification.setTextColor(Color.parseColor("#198754"));

                Log.d("callApiGetDataFormNasa", response.body().toString());
            }

            @Override
            public void onFailure(Call<HackNasa> call, Throwable t) {
                findViewById(R.id.layout_show_data).setVisibility(View.GONE);
                Log.d("EEE", t.getMessage());
                tvNotification.setText("get data from Nasa failed");
                tvNotification.setTextColor(Color.RED);
            }
        });
    }

    private void sendDataToServer() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (hackNasa.getHdurl() != null) {
                base64UrlHd = convertUrlToBase64(hackNasa.getHdurl());
            } else {
                base64UrlHd = "";
            }
            base64url = convertUrlToBase64(hackNasa.getUrl());
        }

        hackNasa.setHdurl(base64UrlHd);
        hackNasa.setUrl(base64url);

        Log.d("sendDataToServer", hackNasa.toString());
        Apiserver.apiService.postData(hackNasa).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                tvNotification.setText("push data to my server successfully");
                tvNotification.setTextColor(Color.parseColor("#198754"));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                tvNotification.setText("post data to my server failed");
                tvNotification.setTextColor(Color.RED);
                Log.d("API", t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String convertUrlToBase64(String url) {
        byte[] byteInput = url.getBytes();
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        String encodedString = base64Encoder.encodeToString(byteInput);
        return encodedString;
    }

    private class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            daySelected = spnDate.getSelectedItem().toString();
            monthSelected = spnMonth.getSelectedItem().toString();
            yearSelected = spnYear.getSelectedItem().toString();
            if (!daySelected.equals("days") && !monthSelected.equals("months") && !yearSelected.equals("years")) {
                dateSelected = yearSelected + "-" + monthSelected + "-" + daySelected;
                Log.d("Selected Date", dateSelected);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
