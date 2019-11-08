package acceso;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import main.BDApp;

public class DBController implements Initializable {

	// FXML : View
    //-----------------------------------------------------------------
	
	@FXML
	private VBox view;
	
	@FXML
	private TableView<Residencia> tableEstancias;
	
	@FXML
	private Button addBt, delBt, modBt, procBt;
	
	@FXML
	private TextField idTxt;
	
	//-----------------------------------------------------------------
	
	// Model
	private ListProperty<Residencia> estanciasList;
	
	// Necesitamos una referencia a la aplicación padre
	private BDApp app;
	
	public DBController(BDApp app) throws IOException {
		
		this.app = app;
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBUI.fxml"));
		loader.setController(this);
		loader.load();
		
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		
		// Cargamos los datos en la tabla estancias, para ello tenemos que realizar las consultas pertienentes
		estanciasList = new SimpleListProperty<Residencia>(FXCollections.observableArrayList(app.getDBManager().getResidenciaValues()));
		tableEstancias.itemsProperty().bind(estanciasList);
			
		// Eventos
		addBt.setOnAction( evt -> onInsertResidencia() );
	}

	private void onInsertResidencia() {
		
		InsertResiDialog dialog = new InsertResiDialog();
		
		Optional<Residencia> resiOp = dialog.showAndWait();
		
		// Introducimos nuestra nueva residencia
		if( resiOp.isPresent() && resiOp.get() != null ) {
			// Añadimos la residencia
			app.getDBManager().insertarResidencia(resiOp.get());
		}
		
	}
	public VBox getRootView() {
		return view;
	}
}
