package com.appsfy.gs_store;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ResultActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentToolbar();
        setContentView(R.layout.activity_result);

        String barcode = getIntent().getStringExtra("barcode");

        db.collection("products")
                .whereEqualTo("product_number", barcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    loadData(document);
                                    Log.d("doc", "document found ");
                                } else {
                                    Toast.makeText(ResultActivity.this, "No product found", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } else {
                            Log.d("F-TAG", "get failed with  ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("F-TAG", e.toString());
                    }
                });
        TextView productNumber = findViewById(R.id.product_number);
        productNumber.setText(barcode);

    }

    public void loadData(QueryDocumentSnapshot documentSnapshot) {
        TextView product_name, product_price, product_date, product_notes;
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        product_date = findViewById(R.id.product_date);
        product_notes = findViewById(R.id.product_notes);

        product_name.setText(documentSnapshot.get("product_name").toString());
        product_price.setText(documentSnapshot.get("product_price").toString());
        product_date.setText(documentSnapshot.get("product_date").toString());
        product_notes.setText(documentSnapshot.get("product_description").toString());

    }


    public void transparentToolbar() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}