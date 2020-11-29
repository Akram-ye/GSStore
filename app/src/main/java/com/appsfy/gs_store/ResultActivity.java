package com.appsfy.gs_store;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ResultActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView product_number, product_name, product_price, product_date, product_notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentToolbar();
        setContentView(R.layout.activity_result);

        String barcode = getIntent().getStringExtra("barcode");

        product_number = findViewById(R.id.product_number);
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        product_date = findViewById(R.id.product_date);
        product_notes = findViewById(R.id.product_notes);

        getDataFromFirebase(barcode);

    }

    public void getDataFromFirebase(String barcode){
        db.collection("products")
                .whereEqualTo("product_number", barcode)
                .get()
                .addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentSnapshot = task.getResult();
                            if (!documentSnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    loadData(document);
                                }
                            } else {
                                showMsg("No product found!");
                            }
                        } else {
                            showMsg("Get failed with \n" + task.getException());
                        }
                    }
                });
    }
    public void loadData(QueryDocumentSnapshot documentSnapshot) {
        // Set TextView values
        product_number.setText(documentSnapshot.get("product_number").toString());
        product_name.setText(documentSnapshot.get("product_name").toString());
        product_price.setText(documentSnapshot.get("product_price").toString());
        product_date.setText(documentSnapshot.get("product_date").toString());
        product_notes.setText(documentSnapshot.get("product_description").toString());

    }


    public void showMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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