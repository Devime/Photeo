package com.example.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {

    List<Cell> allfillespath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery2);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1000);
        }else{
            showimages();
        }


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Gallery.this, MainActivity2.class);
        startActivity(intent);
        ActivityCompat.finishAffinity(Gallery.this);
    }

    private void showimages() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        System.out.println("****************************************************************************"+path);

        allfillespath = new ArrayList<>();
        allfillespath = listAllFiles(path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }
        System.out.println(allfillespath.size()+"+++++++++++++++++++++++++++++++++++++++++++");

        RecyclerView recyclerView = findViewById(R.id.gallerie);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Cell> cells =prepaData();
        MyAdapter adapter = new MyAdapter(getApplicationContext(),cells);
        recyclerView.setAdapter(adapter);
    }

    private ArrayList<Cell>prepaData(){
        ArrayList<Cell> allimages = new ArrayList<>();
        for(Cell c : allfillespath){
            Cell cell = new Cell();
            cell.setTitle(c.getTitle());
            cell.setPath(c.getPath());
            allimages.add(cell);
        }
        return allimages;
    }

    private List<Cell> listAllFiles(String pathName) {
        List<Cell> allFiles = new ArrayList<>();
        File file = new File(pathName);
        File[] files = file.listFiles();
        if (files != null) {
            for(File f :files){
                Cell cell = new Cell();
                cell.setTitle(f.getName());
                cell.setPath(f.getAbsolutePath());
                allFiles.add(cell);
            }
        }
        return allFiles;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1000){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                showimages();
            }else{
                //nothing
                finish();
            }
        }
    }
}