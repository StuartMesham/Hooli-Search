package sample.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import sample.model.SearchResult;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class ListCellController extends ListCell<SearchResult> {
	
	public ListCellController(ListView<SearchResult> listView) {
		super();
		
		prefWidthProperty().bind(listView.widthProperty().subtract(18));
		setMaxWidth(Control.USE_PREF_SIZE);
	}
	
	@FXML
	private Label label1;
	
	@FXML
	private Hyperlink link;
	
	private FXMLLoader loader;
	
	@FXML
	private void handleLinkClicked() {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(getItem().getUrl()));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void updateIndex(int i) {
		super.updateIndex(i);
	}
	
	@Override
	protected void updateItem(SearchResult item, boolean empty) {
		super.updateItem(item, empty);
		
		if (empty || item == null) {
			if (loader != null) {
				label1.setText("");
				link.setText("");
				link.setVisible(false);
			}
		} else {
			if (loader == null) {
				loader = new FXMLLoader(getClass().getResource("../view/ResultListCell.fxml"));
				loader.setController(this);
				
				try {
					loader.load();
					setGraphic(loader.getRoot());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
			if (item.getUrl() == null) {
				try {
					item.fetchURL();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			label1.setText(item.getScore() + "");
			link.setVisible(true);
			link.setText(item.getUrl() + "");
		}
	}
}
