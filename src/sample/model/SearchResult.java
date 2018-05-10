package sample.model;

import sample.fileio.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchResult implements Comparable<SearchResult>{
	private int id;
	private String url;
	private int score;
	
	public SearchResult(int id, String url, int score) {
		this.id = id;
		this.url = url;
		this.score = score;
	}
	
	public void fetchURL() throws SQLException, ClassNotFoundException {
		Database database = new Database("database(old).db", Database.TYPE_SQLITE);
		ResultSet rs = database.query("SELECT url FROM internet WHERE id = ?", id);
		rs.next();
		url = rs.getString(1);
		rs.close();
		database.close();
	}
	
	public int getId() {
		return id;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "SearchResult{" +
				"id=" + id +
				", url='" + url + '\'' +
				", score=" + score +
				'}';
	}
	
	@Override
	public int compareTo(SearchResult searchResult) {
		return -Integer.compare(score, searchResult.score);
	}
}
