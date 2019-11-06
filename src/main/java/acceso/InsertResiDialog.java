package acceso;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class InsertResiDialog extends Dialog<Residencia> {

	private class DialogCheckBinding extends BooleanBinding {

		private StringExpression nombre;
		private StringExpression cod;
		private StringExpression precio;
		
		public DialogCheckBinding(StringExpression nombre, StringExpression cod, StringExpression precio) {
			
			this.nombre = nombre;
			this.cod = cod;
			this.precio = precio;
			
			bind(this.nombre, this.cod, this.precio);
		}
		
		@Override
		protected boolean computeValue() {
			return checkFieldsValid();
		}
		
		private boolean checkFieldsValid() {
			
			if( nombre.get() == null || cod.get() == null  ) 
				return false;
			
			if( nombre.get().equals("") || cod.get().equals(""))
				return false;
			
			// Comprobamos que el código de la universidad tiene 6 caracteres
			
			char[] codArray = cod.get().toCharArray();
			if( codArray.length != 6 ) 
				return false;
			
			if(  precio.get() != null && !precio.get().equals("") && !checkIsNumber(precio.get()))
				return false;
			
			return true;
		}
		
		private boolean checkIsNumber(String expr) {
			
			try {
				
				@SuppressWarnings("unused")
				float precioN = Float.parseFloat(expr);
				return true;
				
			} catch(NumberFormatException e) {
				
			}
			
			return false;
		}
		
		
	}
	// Los campos nombre y código son obligatorios
	private TextField nombreTxt;
	private TextField codTxt;
	
	// El precio debe ser numérico
	private TextField precioTxt;
	
	private Node insertBt;
	
	public InsertResiDialog() {
		
		setTitle("Insertar residencia");
		setHeaderText("Insertar nueva residencia");
		setContentText("* Los campos nombre y Cod. universidad son obligatorios\n"
					+  "* El código de la universidad debe tener 6 caracteres");
		
		ImageView resiIcon = new ImageView(getClass().getResource("/images/resiIcon.png").toString());
		resiIcon.setFitWidth(48.0f);
		resiIcon.setFitHeight(48.0f);
		setGraphic(resiIcon);
		
		GridPane root = new GridPane();
		root.setHgap(5);
		root.setVgap(5);
		
		Label infoLabel = new Label(getContentText());
		infoLabel.setWrapText(true);
		root.addRow(0, infoLabel);
		GridPane.setColumnSpan(infoLabel, 2);
		
		Label nombreLbl = new Label("Nombre:");
		nombreTxt = new TextField();
		nombreTxt.setPromptText("Nombre residencia");
		root.addRow(1,  nombreLbl, nombreTxt);
		
		Label codLbl = new Label("Cod. universidad:");
		codTxt = new TextField();
		codTxt.setPromptText("Código universidad");
		root.addRow(2,  codLbl, codTxt);
		
		Label precioLbl = new Label("Precio");
		precioTxt = new TextField();
		precioTxt.setPromptText("Precio residencia");
		root.addRow(3, precioLbl, precioTxt);
		
		Label comedorLbl = new Label("Comedor:");
		CheckBox comedorCheck = new CheckBox();
		root.addRow(4,  comedorLbl, comedorCheck);
		
		ButtonType okButton = new ButtonType("Insertar", ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
		getDialogPane().setContent(root);
		
		// El botón no estará disponible  hasta que se hayan introducido los datos correcto
		insertBt = getDialogPane().lookupButton(okButton);
		insertBt.disableProperty().bind(new DialogCheckBinding( nombreTxt.textProperty(), codTxt.textProperty(), precioTxt.textProperty()).not());
		
		setResultConverter( bt -> {
			
			if( bt.getButtonData() == ButtonData.OK_DONE ) {
				
				Residencia residencia;
				
				if( precioTxt.getText() != null && !precioTxt.getText().equals("")) {
					residencia = new Residencia(nombreTxt.getText(), codTxt.getText().toCharArray(), Float.parseFloat(precioTxt.getText()), comedorCheck.isSelected());
				} else {
					residencia = new Residencia(nombreTxt.getText(), codTxt.getText().toCharArray(), comedorCheck.isSelected());
				}
				
				return residencia;
				
				}
			
			return null;
		});
		
		
	}
	
	
	
	
}
