package com.example.picasso;

/**
 * Created by Ulugbek Aripov on 07.07.2016.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.example.picasso.sql_helper.DatabaseHelper;
import com.example.picasso.sql_model.Movierecord;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DetailText extends Activity {

	EditText details;
	DatabaseHelper db;
	String outp="";
	String tagdisp[]= {"Most popular movies","Highest rated movies","Now playing","Upcoming movies"};
	String tag[]= {"popular","hirated","playing","upcoming"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailtext);

		db = new DatabaseHelper(getApplicationContext());

		details = (EditText) findViewById(R.id.details);

		List<Movierecord> allToDos = db.getAllToDos();

		Collections.sort(allToDos, new Comparator<Movierecord>() {
			@Override
			public int compare(Movierecord c1, Movierecord c2) {
				return Double.compare(c1.getrating(), c2.getrating());
			}
		});
		Collections.reverse(allToDos);


		for(int i = 0; i <= 3; i++) {
			int ord=0;
			String atag=tag[i];
			String atagdisp=tagdisp[i];
			outp=outp+"\n\n\n"+atagdisp+":\n________________";

			for (Movierecord movierecord : allToDos) {

				if (movierecord.gettag().equals(atag)) {
					ord++;
					Log.d("SQL match", movierecord.gettitle() + "-" + movierecord.gettag() + "-" + tag[i]+"-");
					outp = outp + "\n\n" +
							ord +
							".  Title - " + movierecord.gettitle() +
							"\n\n - Rating - " + movierecord.getrating() +
							"\n - Popularity - " + movierecord.getpop() +
							"\n - Release - " + movierecord.getreleased() +
							"\n\n - Overview - " + movierecord.getdesc();
				}

				Log.d("SQL ToDo", movierecord.gettitle() + "-" + movierecord.gettag() + "-" + tag[i]+"-");
			}
		}
		details.setText(outp);
	}
}
