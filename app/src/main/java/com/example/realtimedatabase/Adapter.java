package com.example.realtimedatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.UploadViewHolder>{
    private List<Upload> uploads;
    private IClickListener iClickListener;

    public interface IClickListener{
        void onClickUpdateItem(Upload upload);
        void onClickDeleteItem(Upload upload);
    }

    public Adapter(List<Upload> uploads, IClickListener iClickListener) {
        this.uploads = uploads;
        this.iClickListener = iClickListener;
    }

    public void setUploads(List<Upload> uploads) {
        this.uploads = uploads;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);

        return new UploadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadViewHolder holder, int position) {
        Upload upload = uploads.get(position);
        if(upload == null){
            return;
        }
        holder.tvName.setText("Name: "+upload.getName());
        holder.tvEmail.setText("Email: "+upload.getEmail());

        holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickListener.onClickUpdateItem(upload);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickListener.onClickDeleteItem(upload);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(uploads != null){
            return uploads.size();
        }
        return 0;
    }

    public class UploadViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private TextView tvEmail;
        private Button btnUpdate;
        private Button btnDelete;
        public UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvName = itemView.findViewById(R.id.tv_name);
            btnUpdate = itemView.findViewById(R.id.btn_update);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
