package com.nadeemai.socialsites.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import android.content.Context;
import android.widget.Toast;

import com.nadeemai.socialsites.R;
import com.nadeemai.socialsites.model.Model;
import com.squareup.picasso.Picasso;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView RV;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter <Model, MainActivity.ItemViewHolder> RVAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        if (isConnected());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/JosefinSans-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mDatabase.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s){}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError){}
            });


        RV = (RecyclerView) findViewById(R.id.myRecycleView);
        DatabaseReference lsRef = FirebaseDatabase.getInstance().getReference();
        Query lsQuery = lsRef.orderByKey();



        RV.hasFixedSize();
        RV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Model>().setQuery(lsQuery, Model.class).build();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        RV.setLayoutManager(gridLayoutManager);
        RVAdapter = new FirebaseRecyclerAdapter<Model, MainActivity.ItemViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(MainActivity.ItemViewHolder holder, final int position, final Model model) {
                holder.setTitle(model.getTitle());
                holder.setImage(getBaseContext(), model.getImage());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String url = model.getUrl();
                        final String title = model.getTitle();
                        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                        intent.putExtra("id", url);
                        intent.putExtra("tit", title);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public MainActivity.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.circular_image_text, parent, false);

                return new MainActivity.ItemViewHolder(view);
            }
        };

        RV.setAdapter(RVAdapter);
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            progressBar.setVisibility(View.GONE);
            Toast toast=Toast.makeText(getApplicationContext(),"No Internet Connection...",Toast.LENGTH_LONG);
            toast.show();
            return true;
        }
    }


    @Override
    public void onStart() {
      super.onStart();
      RVAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        RVAdapter.stopListening();

    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ItemViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(image).into(post_image);
        }

    }

}
