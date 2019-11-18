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

import main.BDApp;

/**
 * Esta clase será la encargada de gestionar las entradas y salidas
 * de las consultas SQL
 * @author David Fernández Nieves
 *
 */
public class DBManager {

	private Connection connection;
	private String bdServer;
	
	public DBManager(Connection appCon, String bdServer) {
		this.connection = appCon;
		this.bdServer = bdServer;
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
			
			CallableStatement stmt = null;
			
			if( bdServer == BDApp.DB_MYSQL ) {
				stmt = connection.prepareCall("call sp_estuEstancias (?)");
			} else if( bdServer == BDApp.DB_SQL ) {
				stmt = connection.prepareCall("exec sp_estuEstancias (?)");
			}
			
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
	 * Obtener los datos de las universidades
	 * @return Un "Map" con todas las universidades
	 */
	public Map<String, String> consultarUniversidades() {
		
		try {
			
			PreparedStatement st = connection.prepareStatement("select codUniversidad, nomUniversidad from universidades"
															  + " order by nomUniversidad");
			
			ResultSet result = st.executeQuery();
			
			Map<String, String> uniMap = new HashMap<>();
			
			while( result.next() ) {
				uniMap.put(result.getString("nomUniversidad"), result.getString("codUniversidad"));
			}
			
			return uniMap;
			
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
			
			PreparedStatement st = connection.prepareStatement("insert into residencias (nomResidencia, codUniversidad, precioMensual, comedor )"
																+ "	values ( ?, ?, ?, ?)");
			
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
			
			CallableStatement st = null;
			
			// Iniciamos la sentencia
			if( bdServer == BDApp.DB_MYSQL ) {
				st = connection.prepareCall("call sp_insertResidencia( ?, ?, ?, ?, ?, ?)");
			} else if( bdServer == BDApp.DB_SQL ){
				st = connection.prepareCall("exec sp_insertResidencia( ?, ?, ?, ?, ?, ?)");
			}
			
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
			
			CallableStatement stmt = null;
			
			if( bdServer == BDApp.DB_MYSQL ) {
				stmt = connection.prepareCall("call sp_cuentaResidencias ( ?, ?, ?, ? )");
			} else if( bdServer == BDApp.DB_SQL ){
				stmt = connection.prepareCall("exec sp_cuentaResidencias ( ?, ?, ?, ? )");
			}
			
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

	public int consultaTiempoEstancias( String dni ) {
		
		try {
			PreparedStatement st = connection.prepareStatement("select fn_tiempoResidencias(?)");
			
			st.setString(1, dni);
			
			ResultSet result = st.executeQuery();
			result.next();
			
			// La función solo devuelve un valor
			return result.getInt(1);
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.getMessage(), false);
		}
		
		return -1;
		
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
			result.next();
			
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
