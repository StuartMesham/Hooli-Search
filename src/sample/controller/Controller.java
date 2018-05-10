package sample.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import sample.fileio.Database;
import sample.model.SearchManager;
import sample.model.SearchResult;

import java.net.URL;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class Controller implements Initializable{
	@FXML
	private TextField searchTextField;
	
	@FXML
	private ListView<SearchResult> resultsListView;
	
	private ObservableList<SearchResult> resultsList;
	
	private Service<LinkedList<SearchResult>> searchService;
	
	private SearchManager searchManager;
	
	@FXML
	private void handleSearchEvent() {
		long start = System.nanoTime();
		LinkedList<SearchResult> searchResults = searchManager.query(searchTextField.getText());
		long end = System.nanoTime();
//		System.out.println("Search results");
//		for (SearchResult searchResult : searchResults) {
//			System.out.println(searchResult);
//		}
		System.out.printf("Search time: %.2f milliseconds%n", ((double)end - start)/1000000.0);
		resultsList = FXCollections.observableList(searchResults);
		resultsListView.setItems(resultsList);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LoadIndexTask loadIndexTask = new LoadIndexTask();
		
		loadIndexTask.setOnSucceeded(event -> {
			searchManager = new SearchManager();
			searchManager.setRootNode((SearchManager.Node) event.getSource().getValue());
			System.out.println("Index loaded");
		});
		
		System.out.println("Loading index");
		Thread loadIndexThread = new Thread(loadIndexTask);
		loadIndexThread.setDaemon(true);
		loadIndexThread.start();
		
		resultsListView.setCellFactory(param -> new ListCellController(resultsListView));
	}
	
	private static class LoadIndexTask extends Task<SearchManager.Node> {
		
		@Override
		protected SearchManager.Node call() throws Exception {
			SearchManager.Node rootNode = new SearchManager.Node();
			
			Database database = new Database("database(old).db", Database.TYPE_SQLITE);
			
			ResultSet rs = database.query("SELECT word, id, position FROM indeks ORDER BY id, position ASC");
			
			char[] letters;
			SearchManager.Node currentNode;
			
			while (rs.next() && !isCancelled()) {
				
				letters = rs.getString(1).toLowerCase().toCharArray();
				
				currentNode = rootNode;
				
				for (int j = 0; j < letters.length && !isCancelled(); j++) {
					if (currentNode.getChildNodeForLetter(letters[j]) == null) {
						currentNode.addChildNodeForLetter(letters[j]);
					}
					
					currentNode = currentNode.getChildNodeForLetter(letters[j]);
				}
				
				if (currentNode.occurrances == null) {
					currentNode.occurrances = new LinkedList<>();
				}
				
				currentNode.occurrances.add(new SearchManager.TermOccurrence(rs.getInt(2), rs.getInt(3)));
			}
			
			rs.close();
			database.close();
			return rootNode;
		}
	}
}
