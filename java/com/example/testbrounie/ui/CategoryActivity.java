package com.example.testbrounie.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.testbrounie.R;
import com.example.testbrounie.sqlite.SQLiteHelper;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class CategoryActivity extends AppCompatActivity {
    private EditText etNameSubCategory;
    private ImageView ivCat;
    private Button btAddSubCategory, btViewList, btLocation;

    final int REQUEST_CODE_GALLERY = 999;

    public static SQLiteHelper sqLiteHelper;
    public static String db = "CategoriesDB.sqlite";
    public static int version = 1;
    String itemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        etNameSubCategory = findViewById(R.id.etNameSub);
        ivCat = findViewById(R.id.ivPhoto);
        btAddSubCategory = findViewById(R.id.btAddSubCategory);
        btViewList = findViewById(R.id.btViewList);
        btLocation = findViewById(R.id.btLocation);
        //creating database
        sqLiteHelper = new SQLiteHelper(this, db, null, version);


         itemSelected = getIntent().getStringExtra("itemSelected");
        setTitle(itemSelected);

        //creating table


            sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS " + itemSelected + "(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image BLOB)");




        //select image by imageview click
        ivCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //read external storage
                //runtime permission for devices android 6.0 and above
                ActivityCompat.requestPermissions(
                        CategoryActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );


            }
        });

        //add record to sqlite
        btAddSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    sqLiteHelper.insertData(itemSelected,
                            etNameSubCategory.getText().toString().trim(),
                            imageToByte(ivCat)
                    );
                    Toast.makeText(CategoryActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                    //resetData
                    etNameSubCategory.setText("");
                    ivCat.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        //show record list
        btViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intCategoriesList = new Intent(CategoryActivity.this, CategoriesListActivty.class);
                intCategoriesList.putExtra("subCatSelected", itemSelected);
                startActivity(intCategoriesList);

            }
        });

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intLocation = new Intent(CategoryActivity.this, LocationActivity.class);
                startActivity(intLocation);
            }
        });



    }

    public static byte[] imageToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //gallery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(this, "Don´t have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //enable image guidelines
            .setAspectRatio(1,1)//image will be square
            .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                //set image choosed from gallery
                ivCat.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
