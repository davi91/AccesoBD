package main;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import acceso.DBController;
import acceso.DBManager;
import acceso.ProcController;
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
	// MySQL
	//---------------------------------------------------------
	private static final String DBUSER = "root";
	private static final String DBPASS = null; // De moemnto no hay contraseña
	private static final String CON_MYSQL = "jdbc:mysql://localhost:3306/bdresidenciasescolares";
	//---------------------------------------------------------

	//---------------------------------------------------------
	// SQL
	private static final String DBUSER_SQL = "sad";
	private static final String DBPASS_SQL = "sad";
	private static final String CON_SQL = "jdbc:sqlserver://localhost;database=bdresidenciasescolares";
	//---------------------------------------------------------
	
	// Necesitamos el listado de univesidades actual
	private ArrayList<String> universidades = new ArrayList<>();
	
	// Hacemos un Map de universidades, para no tener que estar consultando en la base de datos continuamente
	private Map<String,String> uniMap = new HashMap<>();

	/**
	 * Precio de la residencia, no puede ser menor de 900
	 */
	private static final int minPrecioResidencia = 900;
	
	// El controlador de la vista "sin procedimientos"
	DBController dbRoot;
	
	// El controlador de la vista "con procedimientos"
	ProcController dbProcRoot;
	
	// Serán dos ventanas independientes las que usen la base de datos seleccionada.
	private String bd;
	
	// La conexión con la base de datos
	private Connection dbCon;
	
	// Nuestro gestor de conexión 
	DBManager dbManager;
	
	// Una referencia a nuestra ventana
	Stage mainStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Antes de iniciar nada esperemos a que el usuario introduzca una base de datos correcta
		initChoice();
		
		try {
			initBD();
			
		} catch (SQLException | ClassNotFoundException e) {
			sendConnectionError(e.toString(), true);
		}
		
		// Cargamos el gestor
		dbManager = new DBManager(getdbCon(), getBd());
		
		// 	Antes de iniciar los controladores establecemos las universidades disponibles
		//----------------------------------------------------------
		
		setMapUniversidades();
		
		ArrayList<String> nombreUnis = new ArrayList<>();
		
		uniMap.forEach( (name,cod) -> {
			nombreUnis.add(name);
		});
		
		Collections.sort(nombreUnis); // Como el map nos lo desordena un poco, lo ordenamos
		setUniversidades(nombreUnis);
		
		
		//----------------------------------------------------------
		
		// Cargamos la vista, en este caso, la primera es la que no usa procedimientos
		mainStage = primaryStage;
		launchNormalWindow();
		mainStage.show();
	}
	
	public Map<String, String> getUniMap() {
		return uniMap;
	}

	/**
	 * Método para iniciar la conexión con la base de datoss
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public void initBD() throws ClassNotFoundException, SQLException {
		
		switch( getBd() ) {
		case DB_MYSQL:
			// Cargamos la clase
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			dbCon = DriverManager.getConnection(CON_MYSQL, DBUSER, DBPASS);
			
			if( dbCon == null ) {
				throw new SQLException("Conexión no valida");
			}
			
			break;
		
		
		case DB_SQL:
			
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); 
			
			dbCon = DriverManager.getConnection(CON_SQL,DBUSER_SQL,DBPASS_SQL); 
			
			if( dbCon == null ) {
				throw new SQLException("Conexión de váida");
			}
			break;
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

	/**
	 * Obtenemos el "manager" principal de la base de datos
	 * @return El "manager" de la base de datos
	 */
	public DBManager getDBManager() {
		return dbManager;
	}
	
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		// Siempre nos aseguramos de cerrar la conexión con la base de datos al salir
		dbCon.close();
	}

	public static void sendConnectionError(String msg, boolean bExit) {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Base de datos");
		alert.setHeaderText(msg);
		alert.showAndWait();
		
		if( bExit )
			Platform.exit();
	}
	
	/**
	 * Lanzamos el contenido "sin procedimientos"
	 */
	public void launchNormalWindow() {
		
		try {
			dbRoot = new DBController(this);
			Scene scene = new Scene(dbRoot.getRootView(), 640, 480);
			mainStage.setScene(scene);
			
		} catch (IOException e) {
			sendConnectionError(e.toString(), true);
		}
	}
	
	/**
	 * Cargamos el contenido "con procedimientos"
	 */
	public void launchProcWindow() {
		
		try {
			dbProcRoot = new ProcController(this);
			Scene scene = new Scene(dbProcRoot.getRootView(), 640, 480);
			mainStage.setScene(scene);
		} catch(IOException e ) {
			sendConnectionError(e.toString(), true);
		}
	}
	
	/**
	 * "Mapeamos" las universidades
	 */
	public void setMapUniversidades() {
		uniMap = getDBManager().consultarUniversidades();
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

	public static int getMinprecioresidencia() {
		return minPrecioResidencia;
	}
	
	public ArrayList<String> getUniversidades() {
		return universidades;
	}

	public void setUniversidades(ArrayList<String> universidades) {
		this.universidades = universidades;
	}

}
