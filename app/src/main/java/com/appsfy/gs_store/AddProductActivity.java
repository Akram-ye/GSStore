package com.appsfy.gs_store;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddProductActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView mProductNumber;
    private EditText mProductName, mProductPrice, mProductDate, mProductNotes;
    private Button mAddProductBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        String barcode = getIntent().getStringExtra("code");
        init(barcode);

        mAddProductBtn.setOnClickListener(v -> {
            String p_no = mProductNumber.getText().toString();
            String p_name = mProductName.getText().toString();
            String p_price = mProductPrice.getText().toString();
            String p_date = mProductDate.getText().toString();
            String p_notes = mProductNotes.getText().toString();

            if (p_name.isEmpty() && p_price.isEmpty() && p_date.isEmpty()){
                Toast.makeText(this, "قم بتعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
            }else {
                addProduct(p_no, p_name, p_price, p_date, p_notes);
            }
        });
    }


    public void init(String barcode) {


        mProductNumber = findViewById(R.id.add_product_number);
        mProductName = findViewById(R.id.product_name_et);
        mProductPrice = findViewById(R.id.product_price_et);
        mProductDate = findViewById(R.id.product_date_et);
        mProductNotes = findViewById(R.id.product_notes_et);
        mAddProductBtn = findViewById(R.id.add_product_btn);

        mProductNumber.setText(barcode);


    }

    public void addProduct(String p_number, String p_name, String p_price, String p_date, String p_notes) {

        Map<String, Object> product = new HashMap<>();

        product.put("product_number", p_number);
        product.put("product_name", p_name);
        product.put("product_price", p_price);
        product.put("product_date", p_date);
        product.put("product_description", p_notes);
        product.put("createdAt",FieldValue.serverTimestamp());


        db.collection("products")
                .add(product)
                .addOnCompleteListener(this,new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            finish();
                            Toast.makeText(AddProductActivity.this, "تم إضافة المنتج بنجاح ✔", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AddProductActivity.this, "error with " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}