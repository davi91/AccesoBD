package acceso;

import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import utils.NumberBinding;

public class ConultaUniversidadDialog extends Dialog<Pair<String,Float>> {

	public ConultaUniversidadDialog(ArrayList<String> universidades) {
		
		setTitle("Consulta universidad");
		setHeaderText("Consulta de residencias de una universidad");
		setContentText("Residencias de la universidad seleccionada y también\n a un precio menor al seleccionado.");
		
		GridPane root = new GridPane();
		root.setHgap(5);
		root.setVgap(5);
		root.setAlignment(Pos.CENTER);
		
		Label contentTxt =  new Label(getContentText());
		contentTxt.setWrapText(true);
		root.addRow(1, contentTxt);
		GridPane.setColumnSpan(contentTxt, 2);
		
		Label precioLbl = new Label("Precio:");
		TextField precioTxt = new TextField();
		precioTxt.setPromptText("Precio");
		root.addRow(2, precioLbl, precioTxt);
		
		Label uniLbl = new Label("Universidad");
		ComboBox<String> uniCB = new ComboBox<>();
		uniCB.getItems().addAll(universidades);
		root.addRow(3, uniLbl, uniCB);
		uniCB.getSelectionModel().selectFirst();
		
		getDialogPane().setContent(root);
		
		ButtonType okButton = new ButtonType("Consultar",ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
		
		getDialogPane().lookupButton(okButton).disableProperty().bind(precioTxt.textProperty().isNotNull()
																	  .and(precioTxt.textProperty().isNotEmpty()).not());
		
		setResultConverter( bt -> {
			
			if( bt.getButtonData() == ButtonData.OK_DONE) {
				return new Pair<String,Float>(uniCB.getSelectionModel().getSelectedItem(), Float.parseFloat(precioTxt.getText()));
			}
			
			return null;
		});
		
	}

}
