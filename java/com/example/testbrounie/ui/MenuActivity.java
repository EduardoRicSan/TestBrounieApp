package com.example.testbrounie.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.testbrounie.R;

public class MenuActivity extends AppCompatActivity {

    private ListView lvCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_menu);
        fillArray();
    }

    private void fillArray() {
        String[] categories = getResources().getStringArray(R.array.categories);
        lvCategories = findViewById(R.id.lvCategories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        lvCategories.setAdapter(adapter);


        lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               // Object o = lvCategories.getItemAtPosition(i);
                String itemSelected = (String) lvCategories.getItemAtPosition(i);
                Intent intCategory = new Intent(MenuActivity.this, CategoryActivity.class );
                intCategory.putExtra("itemSelected", itemSelected);
                startActivity(intCategory);
            }
        });

    }
}
