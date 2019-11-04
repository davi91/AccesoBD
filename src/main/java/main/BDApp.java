package main;
import java.util.Optional;

import acceso.DBController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

public class BDApp extends Application {

	// Nuestras bases de datos
	//---------------------------------------------------------
	public static final String DB_MYSQL = "MySQL";
	public static final String DB_SQL = "SqlServer";
	public static final String DB_ACCESS = "Access";
	//---------------------------------------------------------
	
	DBController dbRoot = new DBController();
	
	// Serán dos ventanas independientes las que usen esta propiedad
	private ObjectProperty<String> db = new SimpleObjectProperty<>();
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Antes de iniciar nada esperemos a que el usuario introduzca una base de datos correcta
		initChoice();
		
		// Ahora podemos cargar nuestra ventana, la primera es sin usar procedimientos
		
	}
	
	/**
	 * Nos encargamos que el usuario pueda elegir entre las diferentes bases de datos.
	 * @param rootController
	 */
	public void initChoice() {
				
		ChoiceDialog<String> dialog = new ChoiceDialog<>(DB_MYSQL);
		dialog.setTitle("Base de datos");
		dialog.setContentText("Seleccione una base de datos con la que trabajar");
		dialog.getItems().addAll(DB_MYSQL, DB_SQL, DB_ACCESS);
	
		Optional<String> dbType = dialog.showAndWait();
		
		if( dbType.isPresent() ) {	
			// Ajustamos nuestro modelo
			setDb(dbType.get());
			
		} else {
			Platform.exit(); // Entonces hemos acabado
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public final ObjectProperty<String> dbProperty() {
		return this.db;
	}
	


	public final String getDb() {
		return this.dbProperty().get();
	}
	


	public final void setDb(final String db) {
		this.dbProperty().set(db);
	}

}
