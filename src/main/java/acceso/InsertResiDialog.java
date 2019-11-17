package acceso;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.ImageView;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import utils.NumberBinding;

public class InsertResiDialog extends Dialog<Residencia> {

	// Los campos nombre y código son obligatorios
	private TextField nombreTxt;

	// El precio debe ser numérico
	private TextField precioTxt;
	
	// El botón de inserción
	private Node insertBt;
	
	// El ID de la residencia
	private int resiID = -1;
	
	
	/**
	 * Constructor por defecto, para insertar
	 * @param universidades La lista de universidades para elegir
	 */
	public InsertResiDialog(ArrayList<String> universidades) {
		buildGUI(universidades, "", "", -1, false);
	}
	
	/**
	 * Constructor específico para modificar
	 * @param universidades La lista de universidades para elegir
	 * @param nombre El nombre de la residencia
	 * @param universidad La universidad a la que pertenece
	 * @param precio El precio de la residencia
	 * @param comedor Si tiene o no comedor la residencia
	 * @param id ID de la residencia
	 */
	public InsertResiDialog(ArrayList<String> universidades, String nombre, String universidad, float precio, boolean comedor, int id) {
		resiID = id;
		buildGUI(universidades, nombre, universidad, precio, comedor);
	}
	
	/**
	 * Construimos la interfaz del diálogo
	 * @param nombre El nombre de la residencia
	 * @param universidad La universidad a la que pertenece
	 * @param precio El precio de la residencia
	 * @param comedor Si tiene o no comedor
	 * @param id El ID de la residencia, si no se quiere mostrar entonces será -1.
	 */
	public void buildGUI( ArrayList<String> listaUniversidades, String nombre, String universidad, float precio, boolean comedor ) {
		
		setTitle("Insertar residencia");
		setHeaderText("Insertar nueva residencia");
		setContentText("* El campo nombre es obligatorio\n"
					+  "* El precio debe ser como mínimo 900" );
		
		int currentRow = 0;
		
		ImageView resiIcon = new ImageView(getClass().getResource("/images/resiIcon.png").toString());
		resiIcon.setFitWidth(48.0f);
		resiIcon.setFitHeight(48.0f);
		setGraphic(resiIcon);
		
		GridPane root = new GridPane();
		root.setHgap(5);
		root.setVgap(5);

		Label infoLabel = new Label(getContentText());
		infoLabel.setWrapText(true);
		root.addRow(currentRow++, infoLabel);
		GridPane.setColumnSpan(infoLabel, 2);
		
		if( resiID != -1 ) {
			Label idLbl = new Label("ID:");
			Label idTxt = new Label(String.valueOf(resiID));
			root.addRow(currentRow++, idLbl, idTxt);
		}
		
		Label nombreLbl = new Label("Nombre:");
		nombreTxt = new TextField(nombre);
		nombreTxt.setPromptText("Nombre residencia");
		root.addRow(currentRow++,  nombreLbl, nombreTxt);
		
		Label uniLbl = new Label("Cod. universidad:");
		ComboBox<String> uniCb = new ComboBox<String>();
		uniCb.getItems().addAll(listaUniversidades);
		uniCb.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(uniCb, Priority.ALWAYS);
		if( !universidad.equals("") ) {
			uniCb.getSelectionModel().select(universidad);
		} else {
			uniCb.getSelectionModel().selectFirst();
		}
		
		root.addRow(currentRow++, uniLbl, uniCb);
		
		Label precioLbl = new Label("Precio");
		precioTxt = new TextField((precio != -1 ) ? String.valueOf(precio) : "900");
		precioTxt.setPromptText("Precio residencia");
		root.addRow(currentRow++, precioLbl, precioTxt);
		
		Label comedorLbl = new Label("Comedor:");
		CheckBox comedorCheck = new CheckBox();
		comedorCheck.setSelected(comedor);
		root.addRow(currentRow++,  comedorLbl, comedorCheck);
		
		ButtonType okButton = new ButtonType("Insertar", ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().addAll(okButton, cancelButton);
		getDialogPane().setContent(root);
		
		// El botón no estará disponible  hasta que se hayan introducido los datos correcto
		insertBt = getDialogPane().lookupButton(okButton);
		insertBt.disableProperty().bind(new NumberBinding(precioTxt.textProperty())
										.and(nombreTxt.textProperty().isNotNull()
									    .and(nombreTxt.textProperty().isNotEmpty())).not());

		//insertBt.disableProperty().bind(new DialogCheckBinding( nombreTxt.textProperty(), precioTxt.textProperty()).not());
		
		setResultConverter( bt -> {
			
			if( bt.getButtonData() == ButtonData.OK_DONE ) {
				
				Residencia residencia;
				
				residencia = new Residencia(resiID, nombreTxt.getText(), uniCb.getSelectionModel().getSelectedItem(), Float.parseFloat(precioTxt.getText()), comedorCheck.isSelected());
				
				return residencia;
				
			}
			
			return null;
		});
			
	}
	
		
}
