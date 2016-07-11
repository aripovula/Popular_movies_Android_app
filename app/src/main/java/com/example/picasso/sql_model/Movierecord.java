package com.example.picasso.sql_model;

public class Movierecord {

	int id;
	String title;
	String released;
	String desc;
	String poster;
	String tag;
	double rating;
	double pop;


	// constructors
	public Movierecord() {
	}

	public Movierecord(String title, String desc, String poster, double rating, double pop, String released, String tag) {
		this.title = title;
		this.desc = desc;
		this.poster = poster;
		this.rating = rating;
		this.pop = pop;
		this.released = released;
		this.tag = tag;
	}

	public Movierecord(int id, String title, String desc, String poster, double rating, double pop, String released, String tag) {
		this.id = id;
		this.title = title;
		this.desc = desc;
		this.poster = poster;
		this.rating = rating;
		this.pop = pop;
		this.released = released;
		this.tag = tag;
	}

	// setters
	public void setId(int id) {
		this.id = id;
	}

	public void settitle(String title) {
		this.title = title;
	}

	public void setposter(String poster) {
		this.poster = poster;
	}

	public void setdesc(String desc) {
		this.desc = desc;
	}

	public void setreleased(String released) {
		this.released = released;
	}

	public void settag(String tag) {
		this.tag = tag;
	}

	public void setrating(double rating) {
		this.rating = rating;
	}

	public void setpop(double pop) {
		this.pop = pop;
	}

	// getters
	public long getId() {
		return this.id;
	}

	public String gettitle() {
		return this.title;
	}

	public String getposter() {
		return this.poster;
	}

	public String getdesc() {
		return this.desc;
	}

	public String getreleased() {
		return this.released;
	}

	public String gettag() {
		return this.tag;
	}

	public double getrating() {
		return this.rating;
	}

	public double getpop() {
		return this.pop;
	}

}
