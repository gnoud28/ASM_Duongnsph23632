package com.example.myapplication.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.api.Apiserver;
import com.example.myapplication.model.HackNasa;


import java.util.Base64;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NasaApdater extends RecyclerView.Adapter<NasaApdater.HackNasaViewHolder> {

    private Context context;
    private List<HackNasa> list;

    public NasaApdater(Context context) {
        this.context = context;
    }

    public void setData(List<HackNasa> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HackNasaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_data, parent, false);
        return new HackNasaViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull HackNasaViewHolder holder, int position) {
        HackNasa obj = list.get(position);
        if (obj == null) {
            return;
        }
        String url = decodeBase64(obj.getUrl());
        Glide.with(context)
                .load(url)
                .error(R.drawable.baseline_error_24)
                .into(holder.img);

        holder.bindData(obj);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HackNasaViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView tvTitle, tvContent;
        private CardView itemSelected;

        public HackNasaViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            itemSelected = itemView.findViewById(R.id.layout_selected);


            ImageView imgxoa = itemView.findViewById(R.id.imgxoa); // Ánh xạ ImageView xóa
            imgxoa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        HackNasa obj = list.get(position);
                        showDeleteConfirmationDialog(position, obj.get_id());
                    }
                }
            });
            ImageView imgupdate=itemView.findViewById(R.id.imgupdate);
            imgupdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        HackNasa obj = list.get(position);
                        showUpdateDialog(obj, position); // Truyền vào vị trí của item
                    }
                }
            });
        }

        public void bindData(HackNasa obj) {
            tvTitle.setText(obj.getTitle());
            tvContent.setEllipsize(TextUtils.TruncateAt.END);
            tvContent.setText(obj.getExplanation());
        }
    }

    private String decodeBase64(String url) {
        String newUrl = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Base64.Decoder base64Decoder = Base64.getUrlDecoder();
                byte[] bytes = base64Decoder.decode(url);
                newUrl = new String(bytes);
            } catch (IllegalArgumentException e) {
                Log.e("DecodeError", "Error decoding Base64: " + e.getMessage());
            }
        }
        return newUrl;
    }
private void showUpdateDialog(HackNasa obj,int position) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update, null);
    builder.setView(dialogView);

    // Tìm các view trong giao diện cửa sổ dialog
    EditText etTitle = dialogView.findViewById(R.id.editTitle);
    EditText etContent = dialogView.findViewById(R.id.editContent);
    ImageView ivImage = dialogView.findViewById(R.id.imgsua);
    Button btnUpdate = dialogView.findViewById(R.id.bntoke);
    Button btnCancel = dialogView.findViewById(R.id.bntcant);

    // Đặt dữ liệu hiện có vào các trường trong dialog
    etTitle.setText(obj.getTitle());
    etContent.setText(obj.getExplanation());
    String url = decodeBase64(obj.getUrl());
    Glide.with(context)
            .load(url)
            .error(R.drawable.baseline_error_24)
            .into(ivImage);

    AlertDialog dialog = builder.create();
    dialog.show();

    // Xử lý sự kiện khi bấm nút Cập nhật
    btnUpdate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String newTitle = etTitle.getText().toString();
            String newContent = etContent.getText().toString();

            // Cập nhật dữ liệu bằng cách gọi API
            updateData(obj.get_id(), newTitle, newContent, position);

            // Đóng dialog
            dialog.dismiss();
        }
    });

    // Xử lý sự kiện khi bấm nút Hủy
    btnCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    });
}

    private void updateData(String id, String newTitle, String newContent, int position) {
        HackNasa updatedData = new HackNasa();
        updatedData.setTitle(newTitle);
        updatedData.setExplanation(newContent);

        // Gọi API để cập nhật dữ liệu
        Apiserver.apiService.updateData(id, updatedData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Xử lý thành công
                    HackNasa obj = list.get(position);
                    obj.setTitle(newTitle);
                    obj.setExplanation(newContent);
                    notifyDataSetChanged();
                } else {
                    // Xử lý khi thất bại
                    Log.e("UpdateError", "Cập nhật thất bại");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ApiCallError", "Lỗi khi gọi API: " + t.getMessage());
            }
        });
    }

    // het
    private void deleteItem(int position, String id) {
        Apiserver.apiService.deleteData(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    list.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Delete success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showDeleteConfirmationDialog(final int position, final String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItem(position, id);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



}

