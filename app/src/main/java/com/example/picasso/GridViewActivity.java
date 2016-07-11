package com.example.picasso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.picasso.sql_helper.DatabaseHelper;
import com.example.picasso.sql_model.Movierecord;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GridViewActivity extends AppCompatActivity {

    private ImageButton imageB1, imageB2, imageB3,imageB4,imageB5,imageB6,imageB7,imageB8,imageB9,imageB10,imageB11,imageB12,imageB13,imageB14,imageB15,imageB16,imageB17,imageB18,imageB19,imageB20;

    Context context;
    int halfWidth;

    DatabaseHelper db;
    protected static final String PREFS_NAME = "MoviePrefsFile";

    private ArrayList<Data> dataList = new ArrayList<Data>();

    private String texts;

    private int viewType;
    String viewttype[]= {"Most popular movies","Highest rated movies","Now playing","Upcoming movies"};
    String tag[]= {"popular","hirated","playing","upcoming"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gridview_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context=this;

        db = new DatabaseHelper(getApplicationContext());

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        viewType = settings.getInt("showMode", 0);

        cleanOld();
        fetchData();

        updatenote();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sample_grid_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //if (id == R.id.action_settings) {
        //    return true;
        //}

        if (id == R.id.action_bypop) {
            viewType=0;
            saveShowMode();
            updatenote();
            cleanOld();
            fetchData();
        }

        if (id == R.id.action_byrat) {
            viewType=1;
            saveShowMode();
            updatenote();
            cleanOld();
            fetchData();
        }

        if (id == R.id.action_byplaying) {
            viewType=2;
            saveShowMode();
            updatenote();
            cleanOld();
            fetchData();
        }

        if (id == R.id.action_byupcoming) {
            viewType=3;
            saveShowMode();
            updatenote();
            cleanOld();
            fetchData();
        }

        if (id == R.id.action_showoffline) {
            Intent i = new Intent(context, DetailText.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

     class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG ="movietags";

        private String[] getWeatherDataFromJson(String forecastJsonStr)
                throws JSONException {

            final String OWM_LIST = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_TITLE = "title";
            final String OWM_VOTE = "vote_average";
            final String OWM_RELD = "release_date";
            final String OWM_POP = "popularity";
            final String OWM_DESCRIPTION = "overview";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            String[] resultStrs = new String[20];

            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
            int Siz=weatherArray.length();
            Log.v(LOG_TAG, "Size="+Siz);

            for(int i = 0; i < weatherArray.length(); i++) {
                String description;

                // Get the JSON object representing a movie
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                String poster = dayForecast.getString(OWM_POSTER);
                String title = dayForecast.getString(OWM_TITLE);
                double vote = dayForecast.getDouble(OWM_VOTE);
                double pop = dayForecast.getDouble(OWM_POP);
                String reld = dayForecast.getString(OWM_RELD);
                description = dayForecast.getString(OWM_DESCRIPTION);

                resultStrs[i] = title;

                Data item = new Data();
                item.ID=i;
                item.title=title;
                item.poster=poster;
                item.desc=description;
                item.released=reld;
                item.rating=vote;
                item.pop=pop;

                dataList.add(item);

                recordtoDB(title, description, poster, vote, pop, reld);

            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie entry: " + s);
            }

            if (viewType!=0) {
                Collections.sort(dataList, new Comparator<Data>() {
                    @Override
                    public int compare(Data c1, Data c2) {
                        return Double.compare(c1.getRating(), c2.getRating());
                    }
                });
                Collections.reverse(dataList);
            } else {
                Collections.sort(dataList, new Comparator<Data>() {
                    @Override
                    public int compare(Data c1, Data c2) {
                        return Double.compare(c1.getPop(), c2.getPop());
                    }
                });
                Collections.reverse(dataList);
            }



            return resultStrs;

        }
        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                final String APIKEY = getResources().getString(R.string.apikey);

                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/movie/popular?api_key="+APIKEY);

                if (viewType==1) builtUri = Uri.parse("http://api.themoviedb.org/3/movie/top_rated?api_key="+APIKEY);
                if (viewType==2) builtUri = Uri.parse("http://api.themoviedb.org/3/movie/now_playing?api_key="+APIKEY);
                if (viewType==3) builtUri = Uri.parse("http://api.themoviedb.org/3/movie/upcoming?api_key="+APIKEY);

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {

                showdb();

                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);
                int screenWidth = size.x;
                halfWidth = (int)(screenWidth *0.5);

                imageB1 = (ImageButton) findViewById(R.id.imageB1);  showi(0,imageB1);
                imageB2 = (ImageButton) findViewById(R.id.imageB2);  showi(1,imageB2);
                imageB3 = (ImageButton) findViewById(R.id.imageB3);  showi(2,imageB3);
                imageB4 = (ImageButton) findViewById(R.id.imageB4);  showi(3,imageB4);
                imageB5 = (ImageButton) findViewById(R.id.imageB5);  showi(4,imageB5);
                imageB6 = (ImageButton) findViewById(R.id.imageB6);  showi(5,imageB6);
                imageB7 = (ImageButton) findViewById(R.id.imageB7);  showi(6,imageB7);
                imageB8 = (ImageButton) findViewById(R.id.imageB8);  showi(7,imageB8);
                imageB9 = (ImageButton) findViewById(R.id.imageB9);  showi(8,imageB9);
                imageB10 = (ImageButton) findViewById(R.id.imageB10);showi(9,imageB10);
                imageB11 = (ImageButton) findViewById(R.id.imageB11);showi(10,imageB11);
                imageB12 = (ImageButton) findViewById(R.id.imageB12);showi(11,imageB12);
                imageB13 = (ImageButton) findViewById(R.id.imageB13);showi(12,imageB13);
                imageB14 = (ImageButton) findViewById(R.id.imageB14);showi(13,imageB14);
                imageB15 = (ImageButton) findViewById(R.id.imageB15);showi(14,imageB15);
                imageB16 = (ImageButton) findViewById(R.id.imageB16);showi(15,imageB16);
                imageB17 = (ImageButton) findViewById(R.id.imageB17);showi(16,imageB17);
                imageB18 = (ImageButton) findViewById(R.id.imageB18);showi(17,imageB18);
                imageB19 = (ImageButton) findViewById(R.id.imageB19);showi(18,imageB19);
                imageB20 = (ImageButton) findViewById(R.id.imageB20);showi(19,imageB20);


                imageB1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(0);
                    }
                });
                imageB2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(1);
                    }
                });
                imageB3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(2);
                    }
                });
                imageB4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(3);
                    }
                });
                imageB5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(4);
                    }
                });
                imageB6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(5);
                    }
                });
                imageB7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(6);
                    }
                });
                imageB8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(7);
                    }
                });
                imageB9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(8);
                    }
                });
                imageB10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(9);
                    }
                });
                imageB11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(10);
                    }
                });
                imageB12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(11);
                    }
                });
                imageB13.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(12);
                    }
                });
                imageB14.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(13);
                    }
                });
                imageB15.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(14);
                    }
                });
                imageB16.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(15);
                    }
                });
                imageB17.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(16);
                    }
                });
                imageB18.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(17);
                    }
                });
                imageB19.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(18);
                    }
                });
                imageB20.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        sendit(19);
                    }
                });
            }

        }

         void showi(int img,ImageButton imageN){
             Data d = dataList.get(img);
             String url="http://image.tmdb.org/t/p/w185/"+d.poster;
             Picasso.with(context).load(url).resize(halfWidth, halfWidth).centerCrop().into(imageN);
         }

         void sendit(int buttonn) {
             Data d = dataList.get(buttonn);

             Intent i = new Intent(context, DetailViewActivity.class);
             i.putExtra("poster", d.poster);
             i.putExtra("title", d.title);
             i.putExtra("voteaverage", d.rating);
             i.putExtra("popularity", d.pop);
             i.putExtra("releasedate", d.released);
             i.putExtra("overview", d.desc);
             startActivity(i);
         }
    }

    private void fetchData() {
        boolean isOnline= isOnline();
        if (isOnline==true) {
            dataList.clear();
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute();
        } else {
            Toast.makeText(GridViewActivity.this, "No connection", Toast.LENGTH_LONG).show();
        }
    }

    protected void saveShowMode(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("showMode", viewType);
        editor.commit();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void updatenote(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, viewttype[viewType] + " - by Ulugbek (ULA)  Aripov\nSource: www.themoviedb.org", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });
    }


    void recordtoDB(String title,String desc,String poster,double rating, double pop, String release) {
        Movierecord movierecord1 = new Movierecord(title, desc, poster, rating, pop, release,tag[viewType]);
        long todo1_id = db.createMovieRecord(movierecord1, new long[]{viewType});

    }

    void cleanOld(){
        if (db.getAllToDos().size()!=0) {
            for (Movierecord movierecord1 : db.getAllToDos()) {
                if (movierecord1.gettag() != null & tag[viewType] != null) {
                    if (movierecord1.gettag().equals(tag[viewType]))
                        db.deleteToDo(movierecord1.getId());
                }
            }
        }
    }

    void showdb(){
        List<Movierecord> allToDos = db.getAllToDos();
        for (Movierecord movierecord : allToDos) {
            Log.d("SQL ToDo", movierecord.gettitle()+"-"+movierecord.getId()+"-"+movierecord.gettag());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }

}

class Data {
    public int ID;
    public String title;
    public String poster;
    public String desc;
    public String released;
    public double rating;
    public double pop;

    double getPop(){
        return pop;
    }

    double getRating(){
        return rating;
    }
}