package com.example.eds.pointsofinterest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MapView mv;
    ItemizedIconOverlay<OverlayItem> items;
    ItemizedIconOverlay.OnItemGestureListener<OverlayItem> markerGestureListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this,
                PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_main);
        mv = (MapView) findViewById(R.id.map1);
        mv.getController().setCenter(new GeoPoint(50.90, -1.40));
        mv.setBuiltInZoomControls(true);
        mv.getController().setZoom(16);
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
        Button load = (Button) findViewById(R.id.load);
        load.setOnClickListener(this);


        markerGestureListener=new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>()

        {
            public boolean onItemLongPress ( int i, OverlayItem item){
                Toast.makeText(MainActivity.this, item.getSnippet(), Toast.LENGTH_SHORT).show();
                return true;
            }

            public boolean onItemSingleTapUp(int i, OverlayItem item) {
                Toast.makeText(MainActivity.this, item.getSnippet(), Toast.LENGTH_SHORT).show();
                return true;
            }

        };

        items = new ItemizedIconOverlay<>(this, new
                ArrayList<OverlayItem>(), markerGestureListener);
        mv.getOverlays().add(items);

    }

    public void onClick (View view) {


        if (view.getId() == R.id.save)
        {

            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Environment
                        .getExternalStorageDirectory().getAbsolutePath() + "/file.txt")));
                for (int i = 0; i < items.size(); i++) {
                    OverlayItem item = items.getItem(i);
                    pw.println(item.getTitle() + "," + item.getSnippet() + "," + item.getPoint() + "");

                }
                pw.flush();
                pw.close();

            } catch (IOException e) {
                System.out.println("I/O Error" + e);
            }
            Toast.makeText(MainActivity.this,"Marker(s) Saved to file",
                    Toast.LENGTH_SHORT).show();

        }

        else if (view.getId() == R.id.load){
            try{
                BufferedReader reader = new BufferedReader(new FileReader (Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/file.txt"));
                String line;

                while ((line = reader.readLine()) !=null){
                    String[] components = line.split(",");
                    if (components.length==5){
                        OverlayItem Item = new OverlayItem(components[0],  components[1],
                                new GeoPoint(Double.parseDouble(components[2]),
                                        Double.parseDouble(components[3])));
                        items.addItem(Item);
                        mv.invalidate();



                    }
                }

            }
            catch (IOException e){
                new AlertDialog.Builder(this).setMessage("I/O Error" + e);
            }
            Toast.makeText(MainActivity.this,"Marker(s) successfully loaded from file",
                    Toast.LENGTH_SHORT).show();
        }

    }






    public void onResume() {
        super.onResume();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext());



        String file = preferences.getString("File", "none");

        if (file.equals("Save")) {
            Toast.makeText(MainActivity.this,"Working(Saved)",
                    Toast.LENGTH_SHORT).show();

        }
        if (file.equals("NoSave")) {
            Toast.makeText(MainActivity.this,"Working(No save)",
                    Toast.LENGTH_SHORT).show();

        }
        mv.getOverlays().add(items);


    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;

    }
    public boolean onOptionsItemSelected(MenuItem item){

        if (item.getItemId() == R.id.addaninterest){
            Intent intent = new Intent(this,AddInterest.class);
            startActivityForResult(intent, 1);
            return true;
        }
        if (item.getItemId() == R.id.prefs){
            Intent intent = new Intent(this,MyPrefsActivity.class);
            startActivityForResult(intent, 2);
            return true;
        }
        if(item.getItemId() == R.id.loadWeb){
            new loadFile().execute("");
            Toast.makeText(MainActivity.this,"Markers Successfully Loaded from Web",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;

    }







    public void onActivityResult (int requestCode, int resultCode,Intent intent) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {


                Bundle extras = intent.getExtras();


                String name = extras.getString("com.example.tx1");
                String type = extras.getString("com.example.tx2");
                String des = extras.getString("com.example.tx3");




                double latitude =  mv.getMapCenter().getLatitude();
                double longitude = mv.getMapCenter().getLongitude();


                OverlayItem Item = new OverlayItem(name, des,
                        new GeoPoint(latitude, longitude));


                items.addItem(Item);
                mv.getOverlays().add(items);
                mv.invalidate();




            }

        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Environment
                    .getExternalStorageDirectory().getAbsolutePath() + "/file.txt")));
            for (int i = 0; i < items.size(); i++) {
                OverlayItem Item = items.getItem(i);
                pw.println(Item.getTitle() + ","  +Item.getSnippet()  + "," + Item.getPoint() + "");

            }
            pw.close();
        } catch (IOException e) {
            new AlertDialog.Builder(this).setMessage("ERROR" + e);
        }

        finish();
    }

    private class loadFile extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String...params){
            try{
                HttpURLConnection connection;
                URL url = new URL ("http://www.free-map.org.uk/course/mad/ws/get.php?year=17&username=user022&format=csv");
                connection = (HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();
                if (connection.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line;
                    while((line = reader.readLine()) != null)
                    {
                        String [] data = line.split(",");
                        if (data.length == 5){
                            OverlayItem item = new OverlayItem (data[0],
                                    data[2], new GeoPoint(Double.parseDouble (data[4])
                                    ,Double.parseDouble(data[3])));
                            items.addItem(item);

                        }
                    }
                }
            }
            catch(IOException e){
                System.out.println("Error" + e.toString());
            }
            return null;
        }

    }


}

