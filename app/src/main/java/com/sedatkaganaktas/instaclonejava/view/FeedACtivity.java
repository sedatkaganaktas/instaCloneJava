package com.sedatkaganaktas.instaclonejava.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sedatkaganaktas.instaclonejava.adapter.FeedRecyclerAdapter;
import com.sedatkaganaktas.instaclonejava.databinding.ActivityFeedBinding;
import com.sedatkaganaktas.instaclonejava.model.Post;
import com.sedatkaganaktas.instaclonejava.R;

import java.util.ArrayList;
import java.util.Map;

public class FeedACtivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    FeedRecyclerAdapter feedRecyclerAdapter;
    private ActivityFeedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        postArrayList = new ArrayList<Post>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getDataFromFirestore();


        //RecyclerView

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        feedRecyclerAdapter = new FeedRecyclerAdapter(postArrayList);
        binding.recyclerView.setAdapter(feedRecyclerAdapter);

        auth= FirebaseAuth.getInstance();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()== R.id.add_post){
            //upload activity

            Intent intentToUpload=new Intent(FeedACtivity.this, UploadActivity.class);
            startActivity(intentToUpload);

        } else if (item.getItemId()==R.id.add_locations){
            Intent intentToAddLocation=new Intent(FeedACtivity.this, MapsActivity.class);
            startActivity(intentToAddLocation);

        }else if (item.getItemId()==R.id.sign_out) {
            //signout
            auth.signOut(); //tekrar dönmemek için.

            Intent intentToMain=new Intent(FeedACtivity.this, MainActivity2.class);
            startActivity(intentToMain);
            finish();

        }else if (item.getItemId()==R.id.my_locations){
            Intent intentToMyLocations=new Intent(FeedACtivity.this, MyLocations.class);
            startActivity(intentToMyLocations);
            finish();
        }else if (item.getItemId()==R.id.weather){
            Intent intentToMyLocations=new Intent(FeedACtivity.this, WeatherActivity.class);
            startActivity(intentToMyLocations);
            finish();
}

        return super.onOptionsItemSelected(item);
}
    public void getDataFromFirestore() {

        CollectionReference collectionReference = firebaseFirestore.collection("Posts");

        collectionReference.orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(FeedACtivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null) {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                        Map<String,Object> data = snapshot.getData();

                        //Casting
                        String comment = (String) data.get("comment");
                        String userEmail = (String) data.get("usermail");
                        String downloadUrl = (String) data.get("downloadurl");
                        System.out.println(downloadUrl);

                        Post post = new Post(userEmail,comment,downloadUrl);

                        postArrayList.add(post);

                    }
                    feedRecyclerAdapter.notifyDataSetChanged();

                }

            }
      });



}}