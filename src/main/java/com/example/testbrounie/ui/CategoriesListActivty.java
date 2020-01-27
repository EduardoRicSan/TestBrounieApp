package com.example.testbrounie.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.testbrounie.R;
import com.example.testbrounie.models.ModelCategory;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CategoriesListActivty extends AppCompatActivity {

    String subCategory;

    private ListView lvSubCategory;
    private ArrayList<ModelCategory> modelCategoryArrayList;
    private CategoryListAdapter adapter = null;
    ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subCategory = getIntent().getStringExtra("subCatSelected");
        setTitle("Items of: " +  subCategory);
        setContentView(R.layout.activity_categories_list_activty);

        initComponents();
    }

    private void initComponents() {
        lvSubCategory = findViewById(R.id.lvSubCategory);
        modelCategoryArrayList = new ArrayList<>();
        adapter = new CategoryListAdapter(this, R.layout.row, modelCategoryArrayList);
        lvSubCategory.setAdapter(adapter);

        getAllData();
    }

    /**
     * method to get all data from table of sqlite
     */
    private void getAllData() {
        Cursor cursor = CategoryActivity.sqLiteHelper.getData("SELECT * FROM " + subCategory);
        modelCategoryArrayList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            byte[] image = cursor.getBlob(2);
            //add to list
            modelCategoryArrayList.add(new ModelCategory(id, name, image));
        }
        adapter.notifyDataSetChanged();
        if (modelCategoryArrayList.size() == 0){
            Toast.makeText(this, "No record found...", Toast.LENGTH_SHORT).show();
        }
        lvSubCategory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //alert dialog to diaplay options of update and delete
                final CharSequence[] items = {"Update", "Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(CategoriesListActivty.this);
                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            //update
                            Cursor c = CategoryActivity.sqLiteHelper.getData("SELECT id FROM " + subCategory);
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogUpdate(CategoriesListActivty.this, arrID.get(position));
                         }
                        if (i == 1) {
                            //delete
                            Cursor c = CategoryActivity.sqLiteHelper.getData("SELECT id FROM " + subCategory);
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                            adapter.notifyDataSetChanged();
                        }

                    }
                });
                dialog.show();
                return true;
            }
        });
    }


    private void showDialogDelete(final int idCat){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(CategoriesListActivty.this);
        dialogDelete.setTitle("Alert");
        dialogDelete.setMessage("Are you sure to delete?");
        dialogDelete.setPositiveButton("Sure, Delete!!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    CategoryActivity.sqLiteHelper.deleteData(subCategory, idCat);

                    Toast.makeText(getApplicationContext(), "Delete successfully", Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Log.e("error", e.getMessage());
                }

            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void showDialogUpdate(Activity activity, final int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Update");

        ivIcon = dialog.findViewById(R.id.ivUpdateIcon);
        final EditText etUpdateName = dialog.findViewById(R.id.etUpdateName);
        Button btUpdate = dialog.findViewById(R.id.btUpdate);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels*0.7);

        dialog.getWindow().setLayout(width, height);
        dialog.show();


        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        CategoriesListActivty.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    CategoryActivity.sqLiteHelper.updateData(
                            subCategory,
                            etUpdateName.getText().toString().trim(),
                            CategoryActivity.imageToByte(ivIcon),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Log.e("update error:", e.getMessage());
                }
                updateCategoriesList();

            }
        });

    }

    public void updateCategoriesList(){
        Cursor cursor = CategoryActivity.sqLiteHelper.getData("SELECT * FROM " + subCategory);
        modelCategoryArrayList.clear();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            byte[] image = cursor.getBlob(2);

            modelCategoryArrayList.add(new ModelCategory(id, name, image));
        }
        adapter.notifyDataSetChanged();

    }


    private static byte[] imageToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 888 ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //gallery intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 888);
            } else {
                Toast.makeText(this, "Don´t have permission to access file location", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 888 && resultCode == RESULT_OK){
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
                ivIcon.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
