package com.example.harsh.mypaint;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

import static com.example.harsh.mypaint.paintView.bitmap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    paintView paintView ;
    public int currentColor;
    int eraserSize;
    public int strokeWidth;
    SeekBar seekBar;
    static int backgroundColor;
    public static Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentColor=Color.BLACK;
        backgroundColor=Color.WHITE;
        strokeWidth=20;
        eraserSize=20;
        paintView=new paintView(this);
        RelativeLayout relativeLayout= (RelativeLayout) findViewById(R.id.relativeView);
        relativeLayout.addView(paintView);
        paint = new Paint();
        //set colour
        paint.setColor(currentColor);
        //smooths out the edges
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        //set Width
        paint.setStrokeWidth(strokeWidth);
        paint.setDither(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    void saveImage(){

        byte[] bytes=dbUtil.getBytes(bitmap);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");

        FileOutputStream outputStream;
        try{
            destination.createNewFile();
            outputStream=new FileOutputStream(destination);
            outputStream.write(bytes);
            outputStream.close();
            Toast.makeText(this, "Saved Successful", Toast.LENGTH_SHORT).show();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length >0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
                saveImage();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.colorPicker) {
            AmbilWarnaDialog ambilWarnaDialog=new AmbilWarnaDialog(this,currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog){
                    //do nothing
                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    currentColor=color;
                    paint.setColor(currentColor);
                }
            });
            ambilWarnaDialog.show();

        } else if (id == R.id.nav_width) {
            seekBar=new SeekBar(this);
            seekBar.setMax(200);
            seekBar.setProgress(strokeWidth);
           new AlertDialog.Builder(this)
                   .setCancelable(true)
                   .setMessage("Select size of brush : ")
                   .setTitle("Stroke Size")
                   .setView(seekBar)
                   .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           strokeWidth=seekBar.getProgress();
                           paint.setStrokeWidth(strokeWidth);
                       }
                   })
                   .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {

                       }
                   })

                   .show();

        }
        else if(id==R.id.colorbackground){

            AmbilWarnaDialog ambilWarnaDialog=new AmbilWarnaDialog(this,currentColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog){
                    //do nothing
                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    backgroundColor=color;
                    bitmap.eraseColor(backgroundColor);
                }
            });
            ambilWarnaDialog.show();
        }
         else if (id == R.id.eraser) {
            if(paint.getColor()==backgroundColor){
                paint.setColor(currentColor);
                paint.setStrokeWidth(strokeWidth);
               NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu menu = navigationView.getMenu();

                MenuItem currentItem=menu.findItem(id);
                currentItem.setTitle("Eraser");


            }
            else{
                paint.setColor(backgroundColor);
                paint.setStrokeWidth(eraserSize);
               NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                Menu menu = navigationView.getMenu();

                MenuItem currentItem=menu.findItem(id);
                currentItem.setTitle("Go back to paint");

            }
        }
        else if(id==R.id.erasersize){

            seekBar=new SeekBar(this);
            seekBar.setMax(200);
            seekBar.setProgress(eraserSize);
            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setMessage("Size of Eraser : ")
                    .setTitle("Eraser Size")
                    .setView(seekBar)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            eraserSize=seekBar.getProgress();
                            paint.setStrokeWidth(eraserSize);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })

                    .show();


        }
        else if(id==R.id.clearall){

            new AlertDialog.Builder(this)
                    .setTitle("Are you sure?")
                    .setMessage("Drawing would be lost.")
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            bitmap.eraseColor(backgroundColor);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setIcon(R.drawable.warning)
                    .show();
        }

        else if(id==R.id.saveimage){

            if(Build.VERSION.SDK_INT < 23){
                saveImage();
            }
            else {

                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    saveImage();
                }
                else{
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                }


            }

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
