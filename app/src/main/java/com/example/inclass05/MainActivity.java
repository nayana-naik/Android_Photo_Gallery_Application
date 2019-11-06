package com.example.inclass05;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    TextView tv_result;
    Button btn_go;
    ProgressBar pb_loading;
    ImageView iv_Result;
    ImageButton iv_previous;
    ImageButton iv_next;
    private int currentImage=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_result=(TextView)findViewById(R.id.textView_result);
        btn_go=findViewById(R.id.btn_go);
        pb_loading=(ProgressBar)findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.INVISIBLE);
        iv_Result=(ImageView)findViewById(R.id.imageView_showImage);
        iv_previous=(ImageButton)findViewById(R.id.imageButton_previous);
        iv_next=(ImageButton)findViewById(R.id.imageButton_next);

        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isConnected()){
                    Toast.makeText(MainActivity.this,"Internet not connected ",Toast.LENGTH_SHORT).show();
                }
                else{
                    new GetKeywordsAPIAsyncTask().execute("http://dev.theappsdr.com/apis/photos/keywords.php");
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();


                }
            }
        });





    }


    private boolean isConnected(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;

    }

    private void setCurrentImage(String curr){

        new GetImageFromURL(this).execute(curr);
       // pb_loading.setVisibility(View.INVISIBLE);
    }

    private void setImage(final String strings){


        Log.d("URLs",strings);

        //if(listOfURLs.length!=0)
        if(strings!=null||strings.length()>0){
            final String[] listOfURLs=strings.split(" ");
            setCurrentImage(listOfURLs[currentImage]);

            iv_next.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    if(listOfURLs!=null||listOfURLs.length==0){
                        currentImage++;
                        Log.d("Curr",String.valueOf(currentImage));
                        if(currentImage==listOfURLs.length){
                            currentImage=0;
                        }
                        setCurrentImage(listOfURLs[currentImage]);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No Images Found",Toast.LENGTH_SHORT).show();
                    }



                }


            });

            iv_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listOfURLs!=null||listOfURLs.length==0){
                        currentImage--;
                        if(currentImage<0||currentImage==listOfURLs.length){

                            currentImage=listOfURLs.length-1;

                        }
                        setCurrentImage(listOfURLs[currentImage]);
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"No Images Found",Toast.LENGTH_SHORT).show();
                    }




                }
            });

        }

        else {

            Toast.makeText(getApplicationContext(),"No Images Found",Toast.LENGTH_SHORT).show();
        }





    }


    private class GetKeywordsAPIAsyncTask extends AsyncTask<String, Void, String[]> {



        @Override
        protected String[] doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = null;
            String [] alertitems=null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);

                    }
                    result = stringBuilder.toString();
                    alertitems=result.split(";");


                }
            } catch (Exception e) {
                //Handle the exceptions
                Log.d("Error",e.toString());
            } finally {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return alertitems;

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String[] strings) {
            //super.onPostExecute(strings);
            final String[] alertItems=strings;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Choose item");

            builder.setItems(alertItems, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tv_result.setText(alertItems[which]);
                    String URL="http://dev.theappsdr.com/apis/photos/index.php?keyword="+alertItems[which];
                    Log.d("URL",URL);
                    new GetURLImagesAsyncTask().execute(URL);
                    //Toast.makeText(this, "Position: " + which + " Value: " + strings[which], Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private class GetURLImagesAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            pb_loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String strings) {

            if(strings.length()>0){
                currentImage=0;
                setImage(strings);
                pb_loading.setVisibility(View.INVISIBLE);
                iv_next.setEnabled(true);
                iv_previous.setEnabled(true);



            }
            else{
                currentImage=0;
                Toast.makeText(getApplicationContext(),"Please Choose another option",Toast.LENGTH_SHORT).show();
                iv_Result.setImageResource(android.R.color.transparent);
                pb_loading.setVisibility(View.INVISIBLE);
                iv_next.setEnabled(false);
                iv_previous.setEnabled(false);


            }



            }


        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = null;
            String[] listOfURL=null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line+" ");

                    }
                    result = stringBuilder.toString();
                   // listOfURL=result.split(" ");
                    //listOfURL.addAll(Arrays.asList(result.split(" ")));


                }
            } catch (Exception e) {
                //Handle the exceptions
                Log.d("Error",e.toString());
            } finally {
                //Close open connections and reader
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //return listOfURL;

            return result;
        }
    }

}
