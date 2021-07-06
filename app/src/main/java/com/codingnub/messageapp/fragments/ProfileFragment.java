package com.codingnub.messageapp.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codingnub.messageapp.R;
import com.codingnub.messageapp.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private CircleImageView profile_image;
    private TextView txtEdit, username;

    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private int IMAGE_REQUEST = 1;

    private Uri imgUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profile_image = view.findViewById(R.id.profile_image);
        txtEdit = view.findViewById(R.id.txtEdit);
        username = view.findViewById(R.id.username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("profile");

        //show info
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                username.setText(user.getName());

                if (user.getImgUrl().equals("default")) {
                    profile_image.setImageResource(R.drawable.img_placeholder_profile);
                } else {
                    Glide.with(getContext()).load(user.getImgUrl()).into(profile_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImage();

            }
        });

        return view;
    }

    private void openImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            imgUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {

                Toasty.warning(getContext(), "upload not possible right now!", Toast.LENGTH_SHORT).show();

            } else {
                uploadImage();
            }

        }

    }

    private String getUriExtension(Uri uri) {

        ContentResolver resolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));

    }

    private void uploadImage() {

        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading picture...");
        pd.show();

        if (imgUri != null) {

            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getUriExtension(imgUri));

            uploadTask = fileRef.putFile(imgUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(firebaseUser.getUid());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imgUrl", mUri);

                        databaseReference.updateChildren(map);

                        Toasty.success(getContext(), "Profile Image updated.", Toast.LENGTH_SHORT).show();

                    } else {
                        Toasty.error(getContext(), "Profile Image not updated.Try again.", Toast.LENGTH_SHORT).show();
                    }
                    pd.dismiss();
                }
            });


        } else {
            Toasty.error(getContext(), "something went wrong!", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

    }


}

















