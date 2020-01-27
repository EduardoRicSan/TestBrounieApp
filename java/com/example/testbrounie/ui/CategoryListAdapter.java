package com.example.testbrounie.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.testbrounie.R;
import com.example.testbrounie.models.ModelCategory;

import java.util.ArrayList;

public class CategoryListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<ModelCategory> categoryArrayList;

    public CategoryListAdapter(Context context, int layout, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.layout = layout;
        this.categoryArrayList = categoryArrayList;
    }

    @Override
    public int getCount() {
        return categoryArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private class ViewHolder{
        ImageView ivIcon;
     //   TextView tvNameSub;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
          //  holder.tvNameSub = row.findViewById(R.id.tvNameSub);
            holder.ivIcon = row.findViewById(R.id.ivIcon);
            row.setTag(holder);
        }else{
            holder = (ViewHolder)row.getTag();
        }
        ModelCategory modelCategory = categoryArrayList.get(i);

       // holder.tvNameSub.setText(modelCategory.getName());
        byte[] recordImage = modelCategory.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage,0, recordImage.length);
        holder.ivIcon.setImageBitmap(bitmap);

        return row;

    }
}
