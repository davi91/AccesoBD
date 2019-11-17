package acceso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import main.BDApp;

/**
 * Controlador principal para el acceso a datos con procedimientos
 * @author David Fernández Nieves
 *
 */
public class ProcController implements Initializable {

	private BDApp app;
	
	// FXML : View
	//-------------------------------------------------------
	
	@FXML
	private VBox view;
	
	@FXML
	private TableView<Estancia> tablaEstancias;
	
	@FXML
	private Button goBackBt, insertBt, queryBt, timeBt, consultarBt;
	
	@FXML
	private TextField dniTxt;
	
	//-------------------------------------------------------
	
	// Model
	//-------------------------------------------------------
	
	private ListProperty<Estancia> estanciasList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<Estancia>()));
	
	private StringProperty dniProperty = new SimpleStringProperty();
	
	//-------------------------------------------------------
	
	public ProcController(BDApp app) throws IOException {
		
		this.app = app;
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DBUIProc.fxml"));
		loader.setController(this);
		loader.load();
		
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		dniProperty.bind(dniTxt.textProperty());
		tablaEstancias.itemsProperty().bind(estanciasList);
		
		consultarBt.setOnAction( evt -> onConsultaEstanciaS());
		insertBt.setOnAction( evt-> onInsertarResidencia() );
		queryBt.setOnAction( evt -> onConsutarUniversidad() );
		timeBt.setOnAction( evt -> onConsultarTiempoEstancias());
		goBackBt.setOnAction( evt -> onGoBack());
	}
	
	/**
	 * Volvemos a la pantalla "sin procedimientos"
	 */
	private void onGoBack() {
		app.launchNormalWindow();
	}
	
	/**
	 * Consulta del tiempo en estancias de un estudiante según su DNI
	 */
	private void onConsultarTiempoEstancias() {
		
		if( dniProperty.get() != null && !dniProperty.get().isEmpty()) {
			
		  int tiempoEstancias = app.getDBManager().consultaTiempoEstancias(dniProperty.get());
		  
		  if( tiempoEstancias != -1) {
			  
			  if( tiempoEstancias > 0 ) {
				  Alert alert = new Alert(AlertType.INFORMATION);
				  alert.setTitle("Estancias");
				  alert.setHeaderText("Tiempo en estancias del estudiante\n con DNI \"" + dniProperty.get() + "\"");
				  alert.setContentText("El estudiante ha estado un total de " + tiempoEstancias + " meses");
				  alert.showAndWait();
			  }
			  
			  else {
				  
				  Alert alert = new Alert(AlertType.WARNING);
				  alert.setTitle("Estancias");
				  alert.setHeaderText("Tiempo en estancias del estudiante\n con DNI \"" + dniProperty.get() + "\"");
				  alert.setContentText("No se han encontrado estancias asociadas a este estudiante");
				  alert.showAndWait(); 
			  }
		  }
		  
		  
		} else {
			sendNoDNISelectedWarning();
		}
	}
	
	/**
	 * Consulta de residencias asociadas a una universidad
	 */
	private void onConsutarUniversidad() {
		
		ConultaUniversidadDialog dialog = new ConultaUniversidadDialog(app.getUniversidades());
		
		Optional<Pair<String, Float>> result = dialog.showAndWait();
		
		if( result.isPresent() ) {
			
			String nombre = result.get().getKey();
			float precio = result.get().getValue();
			
			Map<String, Integer> consulta = app.getDBManager().getResidenciasPorUniversidad(nombre,precio);
			
			// Precio
			int cantResi = consulta.get("CANT_RESIDENCIAS");
			int cantResiPrecio = consulta.get("CANT_RESIDENCIAS_PRECIO");
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Residencias");
			alert.setHeaderText("Residencias encontradas");
			alert.setContentText("La universidad \"" + nombre + "\" tiene asociadas\n"
								+ cantResi + " residencias y de ellas " + cantResiPrecio + " tienen\n"
								+ "un precio menor a " + precio);
			
			alert.showAndWait();
		}
	}
	
	/**
	 * Consultamos las estancias asociadas al estudiante con el DNI seleccionado
	 */
	private void onConsultaEstanciaS() {
		
		if( dniProperty.get() != null && !dniProperty.get().isEmpty()) {
			
			ArrayList<Estancia> estancias = app.getDBManager().getEstanciasValues(dniProperty.get());
			
			if( estancias.size() <= 0 ) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Estancias");
				alert.setHeaderText("Estancias no encontradas");
				alert.setContentText("El alumno con DNI \"" + dniProperty.get() + "\" \nno tiene ninguna estancia asociada" );
				alert.showAndWait();
				return;
			}
			
			estanciasList.addAll(estancias);
		} else {
			sendNoDNISelectedWarning();
		}
	}
	
	/**
	 * Insertamos una nueva residencia
	 */
	public void onInsertarResidencia() {
		
		InsertResiDialog dialog = new InsertResiDialog(app.getUniversidades());
		
		Optional<Residencia> resiOp = dialog.showAndWait();
		
		if( resiOp.isPresent() ) {
			
			try {
				
				// Insertamos el código de la residencia
				resiOp.get().setCodUniversidad(app.getUniMap().get(resiOp.get().getNombreUniversidad()));
				
				// Simplemente lo añadimos a la base de datos, ya que aquí no hay un listado de residencias
				Map<String,Boolean> results = app.getDBManager().proc_insertarResidencia(resiOp.get());
				
				// Analizamos los resultados de la sentencia
				if( !results.get("UNIVERSIDAD_OK")) { // Aunque debido al ComboBox esto no podría pasar
					
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Residencia");
					alert.setHeaderText("La universidad introducida no existe");
					alert.showAndWait();
					
				} else if( !results.get("RESIDENCIA_OK")) {
					
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Residencia");
					alert.setHeaderText("La residencia no pudo ser insertada");
					alert.showAndWait();
				}
				
				else {
					
					// Si todo sale bien
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Confirmación");
					alert.setHeaderText("La residencia ha sido introducida con éxito");
					alert.showAndWait();
				}
				
				
			} catch( RuntimeException e) {
				// No se ha podido insertar
			}
			
		}
	}
	
	/**
	 * Alerta de no DNI introducido
	 */
	public void sendNoDNISelectedWarning() {
		
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Consulta DNI");
		alert.setHeaderText("No ha introducido ningún DNI válido");
		alert.showAndWait();
	}
	
	public VBox getRootView() {
		return view;
	}

}
