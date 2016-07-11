package com.example.picasso.sql_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.picasso.sql_model.Movierecord;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 3;

	// Database Name
	private static final String DATABASE_NAME = "myMovieDB3";

	// Table Names
	private static final String TABLE_MOVIES = "movies";

	// Common column names
	private static final String KEY_ID = "id";

	// NOTES Table - column nmaes
	private static final String KEY_TITLE = "title";
	private static final String KEY_POSTER = "poster";
	private static final String KEY_DESC = "desc";
	private static final String KEY_RATING = "rating";
	private static final String KEY_POP = "pop";
	private static final String KEY_RELEASE = "release";
	private static final String KEY_TAG = "tag";


	// Table Create Statements
	// Movierecord table create statement
	private static final String CREATE_TABLE_MOVIES = "CREATE TABLE "
			+ TABLE_MOVIES + "("
			+ KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_TITLE + " TEXT,"
			+ KEY_DESC + " TEXT,"
			+ KEY_POSTER + " TEXT,"
			+ KEY_RATING + " DOUBLE,"
			+ KEY_POP + " DOUBLE,"
			+ KEY_RELEASE + " TEXT,"
			+ KEY_TAG + " TEXT" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_MOVIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);

		// create new tables
		onCreate(db);
	}

	// ------------------------ "todos" table methods ----------------//

	/*
	 * Creating a movierecord
	 */
	public long createMovieRecord(Movierecord movierecord, long[] tag_ids) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, movierecord.gettitle());
		values.put(KEY_DESC, movierecord.getdesc());
		values.put(KEY_POSTER, movierecord.getposter());
		values.put(KEY_RATING, movierecord.getrating());
		values.put(KEY_POP, movierecord.getpop());
		values.put(KEY_RELEASE, movierecord.getreleased());
		values.put(KEY_TAG, movierecord.gettag());


		// insert row
		long title_id = db.insert(TABLE_MOVIES, null, values);


		return title_id;
	}

	/*
	 * get single todo
	 */
	public Movierecord getTodo(long title_id) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_MOVIES + " WHERE "
				+ KEY_ID + " = " + title_id;

		Log.e(LOG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Movierecord td = new Movierecord();
		td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		td.settitle((c.getString(c.getColumnIndex(KEY_TITLE))));
		td.setdesc((c.getString(c.getColumnIndex(KEY_DESC))));
		td.setposter((c.getString(c.getColumnIndex(KEY_POSTER))));
		td.setrating((c.getDouble(c.getColumnIndex(KEY_RATING))));
		td.setpop((c.getDouble(c.getColumnIndex(KEY_POP))));
		td.setreleased((c.getString(c.getColumnIndex(KEY_RELEASE))));
		td.settag((c.getString(c.getColumnIndex(KEY_TAG))));

		return td;
	}

	/**
	 * getting all todos
	 * */
	public List<Movierecord> getAllToDos() {
		List<Movierecord> movierecords = new ArrayList<Movierecord>();
		String selectQuery = "SELECT  * FROM " + TABLE_MOVIES;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Movierecord td = new Movierecord();
				td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				td.settitle((c.getString(c.getColumnIndex(KEY_TITLE))));
				td.setdesc((c.getString(c.getColumnIndex(KEY_DESC))));
				td.setposter((c.getString(c.getColumnIndex(KEY_POSTER))));
				td.setrating((c.getDouble(c.getColumnIndex(KEY_RATING))));
				td.setpop((c.getDouble(c.getColumnIndex(KEY_POP))));
				td.setreleased((c.getString(c.getColumnIndex(KEY_RELEASE))));
				td.settag((c.getString(c.getColumnIndex(KEY_TAG))));

				// adding to todo list
				movierecords.add(td);
			} while (c.moveToNext());
		}

		return movierecords;
	}


	/*
	 * getting todo count
	 */
	public int getToDoCount() {
		String countQuery = "SELECT  * FROM " + TABLE_MOVIES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/*
	 * Updating a movierecord
	 */
	public int updateToDo(Movierecord movierecord) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_TITLE, movierecord.gettitle());
		values.put(KEY_DESC, movierecord.getdesc());
		values.put(KEY_POSTER, movierecord.getposter());
		values.put(KEY_RATING, movierecord.getrating());
		values.put(KEY_POP, movierecord.getpop());
		values.put(KEY_RELEASE, movierecord.getreleased());
		values.put(KEY_TAG, movierecord.gettag());

		// updating row
		return db.update(TABLE_MOVIES, values, KEY_ID + " = ?",
				new String[] { String.valueOf(movierecord.getId()) });
	}

	/*
	 * Deleting a todo
	 */
	public void deleteToDo(long tado_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MOVIES, KEY_ID + " = ?",
				new String[] { String.valueOf(tado_id) });
	}


	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}
