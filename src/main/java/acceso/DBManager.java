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
	
	/**
	 * Obtener el nombre de una universidad a partir de su ID
	 * @param ID de la universidad
	 * @return Nombre de la universidad
	 */
	public String consultarNombreUniversidad(String id) {
		
		try {
			
			// Preparamos la sentencia, en este caso una básica ya que sólo vamos a consultar los datos de una tabla
			PreparedStatement st = connection.prepareStatement("select nomUniversidad as nombreUniversidad from universidades" 
															+	" where universidades.codUniversidad = ?");
			
			// Ponemos el dato de la universidad
			st.setString(1, id);
			
			// Obtenemos los resultados
			ResultSet result = st.executeQuery();
			
			
			if( result.next() ) {
				return result.getString("nombreUniversidad");
			}

			else {
				throw new SQLException("No se encontró el nombre de la universidad");
			}	
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.toString(), false);
		}
		
		return null;	
	}
	
	/**
	 * Insertamos una residencia, en caso de que la universidad no exista lanzamos un error
	 * En particular estaríamos forzando al SQL a lanzar un error, pero sería lo mismo si
	 * consultáramos en la BD, "lo molestaríamos igual"
	 * @param myResi La residencia a añadir
	 * @throws RuntimeException en el caso de insertar nos aseguramos de lanzar un error
	 */
	public void insertarResidencia(Residencia myResi) throws RuntimeException {
		
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
			throw new RuntimeException(e);
		}
	}

	/**
	 * Consultamos las estancias de una determinada residencia.
	 * @param id ID de la residenica
	 * @return Número de estancias localizadas para esa residencia
	 */
	
	public int consultarEstancias(int id) {
		
		try {
			
			// Preparamos la sentencia, en este caso una básica ya que sólo vamos a consultar los datos de una tabla
			PreparedStatement st = connection.prepareStatement("select count(*) from estancias where codResidencia = ?");
			
			// Id de la residencia
			st.setInt(1, id);
			
			// Obtenemos los resultados
			ResultSet result =  st.executeQuery();
			
			// Nos despalazamos
			result.last();
			
			return result.getInt(1);
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.toString(), true);
		}
		
		return 0;
	}
	
	/**
	 * Eliminamos la residencia seleccionada, ya no debe de estar
	 * registrada en la tabla estancias 
	 * @param i ID de la residencia
	 */
	public void deleteResidencia(int id) throws RuntimeException {
		
		try {
			
			// Preparamos la sentencia, en este caso una básica ya que sólo vamos a consultar los datos de una tabla
			PreparedStatement st = connection.prepareStatement("delete from residencias where codResidencia = ?");
			
			// Id de la residencia
			st.setInt(1, id);
			
			// Ejectuamos
			st.execute();
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.toString(), false);
		}	
	}
	
	/**
	 * Eliminamos las estancias relacionadas con esta residencia. 
	 * @param id ID de la residencia
	 */
	public void eliminarEstancias(int id) {
		
		try {
			
			PreparedStatement st = connection.prepareStatement("delete from estancias where codResidencia = ?");
			
			st.setInt(1, id);
			
			st.execute();
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.toString(), false);
		}
	}
}
