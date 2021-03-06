package com.mdb.training.katharine.mdbsocials;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateNewSocial extends AppCompatActivity implements View.OnClickListener {

    private EditText name;
    private EditText date;
    private EditText description;
    private Button choosePic;
    private Button createSocial;
    private Uri selectedImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private String firebasePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_social);

        name = (EditText) findViewById(R.id.name);
        date = (EditText) findViewById(R.id.date);
        description = (EditText) findViewById(R.id.description);
        choosePic = (Button) findViewById(R.id.pic);
        createSocial = (Button) findViewById(R.id.create);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        createSocial.setOnClickListener(this);
        choosePic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create:
                createSocial();
                break;
            case R.id.pic:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 0);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK && requestCode==0) {
            int numSocials = getIntent().getExtras().getInt("numSocials") + 1;
            firebasePath = "socialPics/" + numSocials + ".jpg";
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference().child(firebasePath);
            selectedImage = imageReturnedIntent.getData();
            UploadTask uploadTask = storageRef.putFile(selectedImage);
        }
    }

    /* Creates a social and returns to the FeedActivity after creation */
    public void createSocial() {
        String n = name.getText().toString();
        String d = date.getText().toString();
        String descrip = description.getText().toString();
        String author = mAuth.getCurrentUser().getEmail();
        ArrayList<String> interested = new ArrayList<>();
        Map<String, Object> post = new HashMap<>();
        post.put("name", n);
        post.put("author", author);
        post.put("description", descrip);
        post.put("date", d);
        post.put("interested", interested);
        post.put("path", firebasePath);
        if (n.length() == 0 || d.length() == 0 || descrip.length() == 0
                || firebasePath.length() == 0) {
            Toast.makeText(getApplicationContext(), "Social not created. Please fill in all " +
                    "fields and upload a picture.", Toast.LENGTH_LONG).show();
        } else {
            mDatabase.child("Socials").push().setValue(post);
            Toast.makeText(getApplicationContext(), "Social created.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, FeedActivity.class));
        }

    }
}
