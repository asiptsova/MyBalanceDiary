package com.application.mybalancediary.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.application.mybalancediary.R;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

    private interface PictuteListener {
        void onProfilePictureUpdated();
    }

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private PictuteListener pictureListener;
    private final int PICK_IMAGE = 100;
    ImageView image;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView name = root.findViewById(R.id.user_name);
        final TextView user_age = root.findViewById(R.id.user_age);
        final TextView user_gender = root.findViewById(R.id.user_gender);
        final TextView user_bmi = root.findViewById(R.id.user_bmi);
        final TextView weight = root.findViewById(R.id.weight);
        final TextView height = root.findViewById(R.id.height);
        final TextView calories = root.findViewById(R.id.calories);
        final TextView edit_goal = root.findViewById(R.id.goals);
        final TextView edit_workout = root.findViewById(R.id.Workout);
        image =root.findViewById(R.id.profile_picture);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();
StorageReference profRef=storageReference.child("Users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
        profRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                image.setBackground(null);
                Picasso.get().load(uri).into(image);

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);
            }
        });


        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String Name = "" + ds.child("name").getValue();
                    String age = "" + ds.child("age").getValue();
                    String gender = "" + ds.child("gender").getValue();
                    String Weight = "" + ds.child("weight").getValue();
                    String Height = "" + ds.child("height").getValue();
                    String BMI = "" + ds.child("bmi").getValue();
                    String Calories = "" + ds.child("bmr").getValue();
                    String Work = "" + ds.child("workout").getValue();
                    String Goal = "" + ds.child("goals").getValue();
                    name.setText(Name);
                    user_age.setText(age);
                    weight.setText(Weight);
                    height.setText(Height);
                    user_gender.setText(gender);
                    user_bmi.setText(BMI);
                    calories.setText(Calories);
                    edit_goal.setText(Goal);
                    edit_workout.setText(Work);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return root;
    }
    @Override
   public void  onActivityResult(int requestCode,int resultCode,@androidx.annotation.Nullable Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1000)
        {
            Uri imageUri=data.getData();
           // image.setImageURI(imageUri);
            uploadImageToFirebase(imageUri);
        }
    }
    public void uploadImageToFirebase (Uri imageUri)
    {
     StorageReference fileRef=storageReference.child("Users/"+firebaseAuth.getCurrentUser().getUid()+"/profile.jpg");
     fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
         @Override
         public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
             fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                 @Override
                 public void onSuccess(Uri uri) {
                     Picasso.get().load(uri).into(image);
                 }
             });
         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {

             Toast.makeText(getActivity(),"Failed",Toast.LENGTH_SHORT).show();
         }
     });
    }
}

