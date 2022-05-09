package com.example.realtimedatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText edtName;
    private EditText edtEmail;
    private List<Upload> uploads;
    private RecyclerView recyclerView;
    private Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtEmail =findViewById(R.id.edit_email);
        edtName = findViewById(R.id.edit_name);
        Button btnAdd = findViewById(R.id.btn_add);
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        uploads = new ArrayList<>();

        adapter = new Adapter(uploads, new Adapter.IClickListener() {
            @Override
            public void onClickUpdateItem(Upload upload) {
                openDialogUpdate(upload);
            }

            @Override
            public void onClickDeleteItem(Upload upload) {
                onClickDeleteData(upload);
            }
        });
        recyclerView.setAdapter(adapter);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString().trim();
                String name = edtName.getText().toString().trim();
                Upload upload = new Upload(name,email);
                onclickAddUser(upload);


            }
        });
        getListUserDatabase();

    }

    private void onclickAddUser(Upload u) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference name = database.getReference("list_uploads");
        String userId = name.push().getKey();
        u.setId(userId);
        name.child(userId).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this,"upload success",Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void getListUserDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("list_uploads");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploads.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Upload u = dataSnapshot.getValue(Upload.class);
                    uploads.add(u);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"err",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDialogUpdate(Upload upload){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_diaglog);
        Window window =dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        EditText edtupdateName = dialog.findViewById(R.id.edt_updatename);
        EditText edtupdateEmail = dialog.findViewById(R.id.edt_updateemail);
        Button btnupdate = dialog.findViewById(R.id.btn_update_update);
        Button btnupcancel = dialog.findViewById(R.id.btn_update_cancel);

        edtupdateName.setText(upload.getName());
        edtupdateEmail.setText(upload.getEmail());

        btnupcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("list_uploads");
                String name = edtupdateName.getText().toString().trim();
                String email = edtupdateEmail.getText().toString().trim();
                upload.setName(name);
                upload.setEmail(email);
                reference.child(upload.getId()).updateChildren(upload.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this,"Update data succec!!!",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });

            }
        });
        dialog.show();
    }
    private void onClickDeleteData(Upload upload){
        new AlertDialog.Builder(this).setTitle(getString(R.string.app_name))
                .setMessage("Ban co muon xoa?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("list_uploads");
                        reference.child(upload.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MainActivity.this,"Delete data success!!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
    }
}