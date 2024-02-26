package com.example.emotionapp.adapter;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.emotionapp.R;
import com.example.emotionapp.roomdb.Quotes;
import com.example.emotionapp.viewmodel.MyViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    static Uri myuri;

    //final long ONE_MEGABYTE = 1024 * 1024;
    public MyAdapter(ArrayList<Quotes> quotesArrayList, Context context) {
        this.quotesArrayList = quotesArrayList;
        this.context = context;
    }

    private ArrayList<Quotes> quotesArrayList;
    private String image_url;



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotes_cards, parent, false);
        return new MyViewHolder(view);
    }

    public void loadimage(MyViewHolder holder){

        // I KNOW MESSY, I ALWAYS GET SOME NONSENSE MISTAKES WHEN DOWNLOAD IMAGES
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference pathReference = storage.getReference().child(image_url);
        // var gsreference = storage.getReferenceFromUrl(pathReference.toString());

        Log.d("TAGKAAN","ONBINDVIEW"+"----"+ pathReference.toString()+"---"+pathReference.getPath().toString());

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                myuri = uri;
                Log.e("TAGKAAN","YES I LOADED "+uri.toString()+" //  "+myuri);
                Glide.with(context)
                        .load(myuri)
                        .into(holder.imageView);
                Log.e("TAGKAAN","YES I LOADED FROM LOAD METHOD "+myuri);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("TAGKAAN","FAIL");
            }
        });

    }

    @Override
    public synchronized void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Quotes quotes = quotesArrayList.get(position);
        String[] quotesarray = quotes.getQuote().split(";");
        image_url = quotes.getImage_url();
        loadimage(holder);

        holder.textView1.setText(quotesarray[0]);
        holder.textView2.setText(quotesarray[1]);
        holder.textView3.setText(quotesarray[2]);



       /*Glide.with(context)
                .load(gsreference)
               .fitCenter()
               .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);*/


        /*pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                holder.imageView.setImageResource(bytes[0]);
                Log.w("TAGKAAN","ONBINDVIEWINLOOP"+bytes+"---"+bytes[0]);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.imageView.setImageResource(R.drawable.ic_launcher_background);
            }
        });*/



    }

    @Override
    public int getItemCount() {
        if (quotesArrayList != null){
            return quotesArrayList.size();
        }
        else{
            return 0;
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView textView1, textView2, textView3;
        private ImageView imageView;

        // I did not use Data Binding. Instead of Data Binding, this way is easier for this application
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.imageViewquotescards);
            this.textView1 = itemView.findViewById(R.id.textView1);
            this.textView2 = itemView.findViewById(R.id.textView2);
            this.textView3 = itemView.findViewById(R.id.textView3);
        }
    }
}

