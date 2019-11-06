package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

import acceso.DBController;
import acceso.DBManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

public class BDApp extends Application {

	// Nuestras bases de datos
	//---------------------------------------------------------
	public static final String DB_MYSQL = "MySQL";
	public static final String DB_SQL = "SqlServer";
	public static final String DB_ACCESS = "Access";
	//---------------------------------------------------------
	
	// Nuestra conexión atendiendo a cada base de datos
	//---------------------------------------------------------
	private static final String DBUSER = "root";
	private static final String DBPASS = null; // De moemnto no hay contraseña
	private static final String CON_MYSQL = "jdbc:mysql://localhost:3306/bdresidenciasescolares";
	//---------------------------------------------------------

	// El controlador de la vista "sin procedimientos"
	DBController dbRoot;
	
	// Serán dos ventanas independientes las que usen la base de datos seleccionada.
	private String bd;
	
	// La conexión con la base de datos
	private Connection dbCon;
	
	// Nuestro gestor de conexión 
	DBManager dbManager;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Antes de iniciar nada esperemos a que el usuario introduzca una base de datos correcta
		initChoice();
		
		try {
			initBD();
			
		} catch (SQLException | ClassNotFoundException e) {
			sendConnectionError(e.toString());
		}
		
		// Cargamos el gestor
		dbManager = new DBManager(getdbCon());
		
		// Cargamos la vista, en este caso, la primera es la que no usa procedimientos
		dbRoot = new DBController(this);
		
		Scene scene = new Scene(dbRoot.getRootView(), 640, 480);
		
		primaryStage.setTitle("Conexión con base de datos " + bd);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public DBManager getSql() {
		return dbManager;
	}
	
	/**
	 * Método para iniciar la conexión con la base de datoss
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public void initBD() throws ClassNotFoundException, SQLException {
		
		// Cargamos la clase
		Class.forName("com.mysql.cj.jdbc.Driver");
		
		dbCon = DriverManager.getConnection(CON_MYSQL, DBUSER, DBPASS);
		
		if( dbCon == null ) {
			throw new SQLException("Conexión no valida");
		}
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
			setBd(dbType.get());
			
		} else {
			Platform.exit(); // Entonces hemos acabado
		}
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		
		// Siempre nos aseguramos de cerrar la conexión con la base de datos al salir
		dbCon.close();
	}

	public static void sendConnectionError(String msg) {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Base de datos");
		alert.setHeaderText(msg);
		alert.showAndWait();
		
		Platform.exit();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	public String getBd() {
		return bd;
	}

	public void setBd(String bd) {
		this.bd = bd;
	}
	
	public Connection getdbCon() {
		return dbCon;
	}

}
