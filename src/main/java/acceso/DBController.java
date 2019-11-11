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

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;
import main.BDApp;

public class DBController implements Initializable {

	// FXML : View
    //-----------------------------------------------------------------
	
	@FXML
	private VBox view;
	
	@FXML
	private TableView<Residencia> tablaResidencias;
	
	@FXML
	private Button addBt, delBt, modBt, procBt;
	
	@FXML
	private TextField idTxt;
	
	//-----------------------------------------------------------------
	
	// Model
	//-----------------------------------------------------------------
	
	private ListProperty<Residencia> residenciasList;
	
	private IntegerProperty id = new SimpleIntegerProperty();
	
	//-----------------------------------------------------------------
	
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
		residenciasList = new SimpleListProperty<Residencia>(FXCollections.observableArrayList(app.getDBManager().getResidenciaValues()));
		tablaResidencias.itemsProperty().bind(residenciasList);
		tablaResidencias.getColumns().get(0).setSortType(SortType.ASCENDING);
		
		idTxt.textProperty().bindBidirectional(id, new NumberStringConverter());
		
		// Listener para cada vez que cambia un elemento en la lista
		tablaResidencias.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> onIDChanged(nv));
		
		// Eventos
		addBt.setOnAction( evt -> onInsertResidencia() );
		modBt.setOnAction( evt -> onModifyResidencia() );
		delBt.setOnAction( evt -> onDeleteResidencia() );
	}

	/**
	 * Modificamos los datos de una residencia
	 */
	private void onModifyResidencia() {
		
		
	}
	
	/**
	 * Cada vez que cambia el elemento seleccionado 
	 * @param nv La residencia seleccionada
	 */
	private void onIDChanged(Residencia nv) {
		
		if( nv != null )
			id.set(nv.getId());
	}
	
	/**
	 * Al eliminar una residencia, tenemos que comprobar varios 
	 * elementos, como si el ID introducido ( si se introduce manualmente )
	 * existe y si está en la tabla estancias.
	 */
	private void onDeleteResidencia() {
		
		// Primero vamos a ver si el ID insertado se encuentra disponible
		Residencia myResi = getElementByIndex(id.get());
		
		if( myResi == null ) {
			
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Eliminar residencia");
			alert.setHeaderText("Residencia no encontrada");
			alert.setContentText("El ID seleccionado no se encuentra en la tabla residencias");
			
			alert.showAndWait();
			return;
		}
		
		// Para eliminar una residencia, tenemos que tener cuidado porque puede estar en la tabla estancias.
		
		// Primero consultamos si tenemos estancias
		if( app.getDBManager().consultarEstancias(id.get()) > 0 ) {
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Eliminar residencia");
			alert.setHeaderText("Estancias encontradas");
			alert.setContentText("¿Está seguro de eliminar esta residencia\n con todas sus estancias?");
			
			Optional<ButtonType> check = alert.showAndWait();
			
			if( check.get() == ButtonType.OK) {
				
				// Procedemos a eliminar las estancias
				app.getDBManager().eliminarEstancias(id.get());
				app.getDBManager().deleteResidencia(id.get());	 // Eliminamos residencia
				residenciasList.remove(myResi); // Actualizamos lista
				
			} else {
				
				// El usuario no quiere seguir borrando datos
				return; 
			}
			
		} else {
			
			// Simplemente le preguntamos si está seguro de eliminar esta residencia
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Eliminar residencia");
			alert.setHeaderText("Confirmación de eliminación");
			alert.setContentText("¿Está seguro de eliminar la residencia con ID " + id.get() + "?");
			
			Optional<ButtonType> check = alert.showAndWait();
			
			
			if( check.get() == ButtonType.OK) {
				// Procedemos entonces
				app.getDBManager().deleteResidencia(id.get());		
				
				// Actualizamos la lista
				residenciasList.remove(myResi);
			} 	
		}
		

		
	}
	
	private Residencia getElementByIndex(int i) {

		for( Residencia r : residenciasList) {
			if( r.getId() == i ) {
				return r;
			}
		}
		
		return null;
	}
	
	private void onInsertResidencia() {
		
		InsertResiDialog dialog = new InsertResiDialog();
		
		Optional<Residencia> resiOp = dialog.showAndWait();
		
		// Introducimos nuestra nueva residencia
		if( resiOp.isPresent() && resiOp.get() != null ) {
			
			try {
				
				// Añadimos la residencia
				app.getDBManager().insertarResidencia(resiOp.get());
				
				// Para actualizar la lista, necesitamos añadir algunos datos que no están en la clase principal
				
				// Para el ID necesitamos el último de la tabla residencias para no estar de nuevo conectando con la base de datos
				resiOp.get().setId( residenciasList.get(residenciasList.getSize()-1).getId()+1);
				
				// Obtenemos el nombre de la universidad
				resiOp.get().setNombreUniversidad(app.getDBManager().consultarNombreUniversidad(resiOp.get().getCodUniversidad()));
				
				// Añadimos a la lista
				residenciasList.add(resiOp.get());
			
			} catch(RuntimeException e ) {
				// No se ha podido insertar la residencia, algún campo no es válido, así no lo añadimos a la lista
			}
		}
		
	}
	public VBox getRootView() {
		return view;
	}
}
