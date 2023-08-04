package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.NasaApdater;
import com.example.myapplication.api.Apiserver;
import com.example.myapplication.model.DataServer;
import com.example.myapplication.model.HackNasa;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Getdata_activity extends AppCompatActivity {

    private List<HackNasa> listHackNasa;
    private NasaApdater adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_from_my_server);
        initViews();
    }

    private void initViews() {
        listHackNasa = new ArrayList<>();
        adapter = new NasaApdater(this);
//        ImageView imgxoa = findViewById(R.id.imgxoa);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getData();
    }

    private void getData() {
        Apiserver.apiService.getData().enqueue(new Callback<DataServer>() {
            @Override
            public void onResponse(Call<DataServer> call, Response<DataServer> response) {
                listHackNasa = response.body().getData();
                adapter.setData(listHackNasa);
                RecyclerView rcv = findViewById(R.id.rcv);
                rcv.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<DataServer> call, Throwable t) {
                // Xử lý lỗi khi không lấy được dữ liệu
            }
        });
    }

}
