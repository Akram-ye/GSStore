package com.appsfy.gs_store;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText mProductName, mProductPrice, mProductDate, mProductNotes;
    private Button mEditProductBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        init();
        String barCode = getIntent().getStringExtra("product_code");


        loadProductData(barCode);

        mEditProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p_name = mProductName.getText().toString();
                String p_price = mProductPrice.getText().toString();
                String p_date = mProductDate.getText().toString();
                String p_notes = mProductNotes.getText().toString();

                if (p_name.isEmpty() && p_price.isEmpty() && p_date.isEmpty()) {
                    Toast.makeText(EditProductActivity.this, "قم بتعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
                } else {
                    editProduct(barCode, p_name, p_price, p_date, p_notes);
                }
            }
        });

    }


    private void editProduct(String barCode, String p_name, String p_price, String p_date, String p_notes) {

        Map<String, Object> product = new HashMap<>();

        product.put("product_name", p_name);
        product.put("product_price", p_price);
        product.put("product_date", p_date);
        product.put("product_description", p_notes);
        product.put("createdAt", FieldValue.serverTimestamp());


        db.collection("products")
                .whereEqualTo("product_number", barCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String docId = documentSnapshot.getId();
                                db.collection("products")
                                        .document(docId)
                                        .update(product)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(EditProductActivity.this, "تم تعديل المنتج بنجاح ✔", Toast.LENGTH_SHORT).show();
                                                } else
                                                    Toast.makeText(EditProductActivity.this, "فشل تعديل المنتج  ❌", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        } else {
                            Toast.makeText(EditProductActivity.this, "ERROR: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void loadProductData(String barcode) {
        db.collection("products")
                .whereEqualTo("product_number", barcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentSnapshot = task.getResult();
                            if (!documentSnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    loadData(document);
                                }
                            } else {
                                Toast.makeText(EditProductActivity.this, "لا يوجد منتج مرتبط", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(EditProductActivity.this, "ERROR: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void loadData(QueryDocumentSnapshot document) {
        mProductName.setText(document.get("product_name").toString());
        mProductPrice.setText(document.get("product_price").toString());
        mProductDate.setText(document.get("product_date").toString());
        mProductNotes.setText(document.get("product_description").toString());
    }

    public void init() {
        mProductName = findViewById(R.id.edit_product_name_et);
        mProductPrice = findViewById(R.id.edit_product_price_et);
        mProductDate = findViewById(R.id.edit_product_date_et);
        mProductNotes = findViewById(R.id.edit_product_notes_et);

        mEditProductBtn = findViewById(R.id.edit_product_btn);
    }
}