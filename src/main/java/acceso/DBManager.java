package acceso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.sql.CallableStatement;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Pair;
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
	
	/**
	 * Obtenemos los datos de las residencias
	 * @return Las residencias también llevan incorporado el código de la universidad internamente.
	 */
	public ArrayList<Residencia> getResidenciaValues() {
		
		try {
			
			// Preparamos la sentencia, en este caso una básica ya que sólo vamos a consultar los datos de una tabla
			Statement st = connection.createStatement();
			
			// Obtenemos los resultados
			ResultSet result =  st.executeQuery("select codResidencia as id, nomResidencia as nombreResidencia, nomUniversidad as nombreUniversidad,"
											+   "precioMensual as precio, comedor, residencias.codUniversidad as codUniversidad from residencias" 
											+	" inner join universidades on universidades.codUniversidad = residencias.codUniversidad");
			
			ArrayList<Residencia> list = new ArrayList<>();
			
			// Rellenamos datos
			while(result.next()) {
				
				int id = result.getInt("id");
				String nombreResidencia = result.getString("nombreResidencia");
				String nombreUniversidad = result.getString("nombreUniversidad");
				String codUniversidad = result.getString("codUniversidad");
				Float precio = result.getFloat("precio");
				boolean esComedor = result.getBoolean("comedor");
				
				// Creamos el objeto estancia y lo vamos añadiendo al ArrayList
				Residencia r = new Residencia( id, nombreResidencia, nombreUniversidad, precio, esComedor);
				r.setCodUniversidad(codUniversidad);
				list.add(r);
			}

			return list;
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.toString(), true);
		}
		
		return null;
	}
	
	/**
	 * Obtenemos los datos de las estancias
	 * @return Las distintas estancias
	 */
	public ArrayList<Estancia> getEstanciasValues(String dni) {
		
		try {
			
			CallableStatement stmt = connection.prepareCall("call sp_estuEstancias (?)");
			stmt.setString(1, dni);
			
			ArrayList<Estancia> estancias = new ArrayList<>();
			
			ResultSet result = stmt.executeQuery();
			
			while( result.next() ) {
				
				Estancia estancia = new Estancia(result.getString("nomUniversidad"), 
												result.getString("nomResidencia"),
												result.getDate("fechaInicio").toString(),
												result.getDate("fechaFin").toString(), 
												result.getFloat("preciopagado"));
				
				estancias.add(estancia);
			}
			
			return estancias;
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.toString(), false);
		}
		
		return null;
	}
	
	/**
	 * A partir del nombre consultamos el código de la universidad
	 * @param Nombre de la universidad
	 * @return Código de la universidad
	 */
	public String consultarCodUniversidad(String nombre) {
		
		try {
			
			// Preparamos la sentencia, en este caso una básica ya que sólo vamos a consultar los datos de una tabla
			PreparedStatement st = connection.prepareStatement("select codUniversidad from universidades" 
															+	" where universidades.nomUniversidad = ?");
			
			// Ponemos el dato de la universidad
			st.setString(1, nombre);
			
			// Obtenemos los resultados
			ResultSet result = st.executeQuery();
			
			
			if( result.next() ) {
				return result.getString("codUniversidad");
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
	 * Obtener el nombre de todas las universidades
	 * @return Una lista con todos los nombres de las universidades
	 */
	public ArrayList<String> consultarUniversidades() {
		
		try {
			
			PreparedStatement st = connection.prepareStatement("select nomUniversidad from universidades");
			
			ResultSet result = st.executeQuery();
			
			ArrayList<String> listaUniversidades = new ArrayList<>();
			while( result.next() ) {
				listaUniversidades.add(result.getString("nomUniversidad"));
			}
			
			return listaUniversidades;
			
		} catch(SQLException e) {
			BDApp.sendConnectionError(e.toString(),  false);
		}
		
		return null;
	}
	
	/**
	 * Insertamos una residencia
	 * @param myResi La residencia a añadir
	 * @throws RuntimeException en el caso de error al insertar nos aseguramos de lanzar un error
	 */
	public void insertarResidencia(Residencia myResi) throws RuntimeException {
		
		try {
			
			PreparedStatement st = connection.prepareStatement("insert into residencias values (NULL, ?, ?, ?, ?)");
			
			// Empezamos a poner los requisitos
			st.setString(1, myResi.getNombre());
			st.setString(2,  myResi.getCodUniversidad());
			st.setFloat(3,  myResi.getPrecio());
			st.setBoolean(4, myResi.isComedor());
			
			st.executeUpdate(); // Si se ha producido algún error se lanzará la SQLException.		
			
		} catch( SQLException e ) {
			BDApp.sendConnectionError(e.getMessage(), false);
			throw new RuntimeException(e); // Avisamos de que no se siga con la inserción
		}
	}
	
	/**
	 * Insertamos una residencia
	 * @param myResi La residencia a añadir
	 * @throws RuntimeException en el caso de error al insertar nos aseguramos de lanzar un error
	 */
	public Map<String, Boolean> proc_insertarResidencia(Residencia myResi) throws RuntimeException {
		
		try {
			
			// Iniciamos la sentencia
			CallableStatement st = connection.prepareCall("call sp_insertResidencia( ?, ?, ?, ?, ?, ?)");
			
			// Empezamos a poner los requisitos
			st.setString(1, myResi.getNombre());
			st.setString(2,  myResi.getCodUniversidad());
			st.setFloat(3,  myResi.getPrecio());
			st.setBoolean(4, myResi.isComedor());
			
			// Parámetros de salida
			st.registerOutParameter(5, Types.BOOLEAN);
			st.registerOutParameter(6, Types.BOOLEAN);
			
			st.execute(); 
			
			// Devolvemos los valores
			Map<String, Boolean> results = new HashMap<>();
			results.put("UNIVERSIDAD_OK", st.getBoolean(5));
			results.put("RESIDENCIA_OK", st.getBoolean(6));
			
			return results;
			
		} catch( SQLException e ) {
			BDApp.sendConnectionError(e.getMessage(), false);
			throw new RuntimeException(e);
		}
	}
	
	public Map<String, Integer> getResidenciasPorUniversidad(String universidad, float precio) {
		
		try {
			
			CallableStatement stmt = connection.prepareCall("call sp_cuentaResidencias ( ?, ?, ?, ?)");
			
			stmt.setString(1, universidad);
			stmt.setFloat(2, precio);
			stmt.registerOutParameter(3, Types.INTEGER);
			stmt.registerOutParameter(4, Types.INTEGER);
			
			stmt.execute();
			
			HashMap<String, Integer> results = new HashMap<>();
			results.put("CANT_RESIDENCIAS", stmt.getInt(3));
			results.put("CANT_RESIDENCIAS_PRECIO",  stmt.getInt(4));
			
			return results;
			
		} catch(SQLException e) {
			BDApp.sendConnectionError(e.getMessage(), false);
		}
		
		return null;
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
			st.executeUpdate();
			
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
			
			st.executeUpdate();
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.toString(), false);
		}
	}
	
	public void modifyResidencia(Residencia resiUpdate) {
		
		try {
			
			PreparedStatement st = connection.prepareStatement("update residencias "
															+ " set nomResidencia = ?,"
															+ " codUniversidad = ?,"
															+ " precioMensual = ?,"
															+ " comedor = ?"
															+ " where codResidencia = ?");
			
			st.setString(1, resiUpdate.getNombre());
			st.setString(2,  resiUpdate.getCodUniversidad());
			st.setFloat(3, resiUpdate.getPrecio());
			st.setBoolean(4, resiUpdate.isComedor());
			st.setInt(5, resiUpdate.getId());
			
			st.executeUpdate();
			
		} catch(SQLException e) {
			BDApp.sendConnectionError(e.toString(),  false);
		}
	}
}
