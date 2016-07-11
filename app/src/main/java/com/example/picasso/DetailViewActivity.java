package com.example.picasso;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Ulugbek Aripov on 05.07.2016.
 */
public class DetailViewActivity extends AppCompatActivity {

    private EditText details;
    int halfWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        halfWidth = (int)(screenWidth *0.5);

        ImageButton poster = (ImageButton) findViewById(R.id.poster);
        TextView title = (TextView) findViewById(R.id.title);
        TextView releasedate = (TextView) findViewById(R.id.textd3);
        TextView voteaverage = (TextView) findViewById(R.id.textd1);
        TextView popularity = (TextView) findViewById(R.id.textd2);
        TextView overview = (TextView) findViewById(R.id.overview);

        Bundle extras=getIntent().getExtras();
        if (extras!=null) {
            String _poster=extras.getString("poster");
            String _title=extras.getString("title");
            String _overview=extras.getString("overview");
            double _voteaverage=extras.getDouble("voteaverage");
            double _popularity=extras.getDouble("popularity");
            String _releasedate=extras.getString("releasedate");

            String url = "http://image.tmdb.org/t/p/w185/" + _poster;
            Picasso.with(this).load(url).resize(halfWidth, halfWidth).centerCrop().into(poster);

            title.setText(_title);
            releasedate.setText(getResources().getString(R.string.title_reld)+" "+_releasedate);
            voteaverage.setText(getResources().getString(R.string.title_rating)+" "+_voteaverage);
            popularity.setText(getResources().getString(R.string.title_pop)+" "+_popularity);
            overview.setText(_overview);
        }
    }
}

