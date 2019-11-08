package acceso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import main.BDApp;

/**
 * Esta clase será la encargada de gestionar las entradas y salidas
 * de las consultas SQL
 * @author David Fernández Nieves
 *
 */
public class DBManager {

	private Connection connection;

	public DBManager(Connection appCon) {
		this.connection = appCon;
	}
	
	public ArrayList<Residencia> getResidenciaValues() {
		
		try {
			
			// Preparamos la sentencia, en este caso una básica ya que sólo vamos a consultar los datos de una tabla
			Statement st = connection.createStatement();
			
			// Obtenemos los resultados
			ResultSet result =  st.executeQuery("select codResidencia as id, nomResidencia as nombreResidencia, nomUniversidad as nombreUniversidad,"
											+   "precioMensual as precio, comedor from residencias" 
											+	" inner join universidades on universidades.codUniversidad = residencias.codUniversidad");
			
			ArrayList<Residencia> list = new ArrayList<>();
			
			// Rellenamos datos
			while(result.next()) {
				
				int id = result.getInt("id");
				String nombreResidencia = result.getString("nombreResidencia");
				String nombreUniversidad = result.getString("nombreUniversidad");
				Float precio = result.getFloat("precio");
				boolean esComedor = result.getBoolean("comedor");
				
				// Creamos el objeto estancia y lo vamos añadiendo al ArrayList
				list.add(new Residencia( id, nombreResidencia, nombreUniversidad, precio, esComedor));
			}

			return list;
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.toString(), true);
		}
		
		return null;
	}
	
	public void insertarResidencia(Residencia myResi) {
		
		try {
			
			PreparedStatement st = connection.prepareStatement("insert into residencias values (NULL, ?, ?, ?, ?)");
			
			// Empezamos a poner los requisitos
			st.setString(1, myResi.getNombre());
			st.setString(2,  myResi.getCodUniversidad());
			st.setFloat(3,  myResi.getPrecio());
			st.setBoolean(4, myResi.isComedor());
			
			st.execute(); // Si se ha producido algún error se lanzará la SQLException.
			
			// También en MySQL se diseñó un trigger específico para el caso en que no existe la universidad introducida,
			// para evitar hacer una consulta adicional mediante la conexión.
			
			// Si todo sale bien
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Confirmación");
			alert.setHeaderText("La residencia ha sido introducida con éxito");
			alert.showAndWait();
			
		} catch( SQLException e ) {
			BDApp.sendConnectionError(e.getMessage(), false);
		}
	}
}
