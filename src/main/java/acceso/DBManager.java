package acceso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
	
	public ArrayList<Estancia> getEstanciaValues() {
		
		try {
			
			// Preparamos la sentencia, en este caso una básica ya que sólo vamos a consultar los datos de una tabla
			Statement st = connection.createStatement();
			
			// Obtenemos los resultados
			ResultSet result =  st.executeQuery("select nomResidencia as nombreResidencia, nomEstudiante as nombreEstudiante, fechaInicio, fechaFin, preciopagado from estancias\n" + 
												"inner join residencias on residencias.codResidencia = estancias.codResidencia\n" + 
												"inner join estudiantes on estudiantes.codEstudiante = estancias.codEstudiante");
			
			ArrayList<Estancia> list = new ArrayList<>();
			
			// Rellenamos datos
			while(result.next()) {
				
				String nombreEstudiante = result.getString("nombreEstudiante");
				String nombreResidencia = result.getString("nombreResidencia");
				java.sql.Date fInicio = result.getDate("fechaInicio");
				java.sql.Date fFin = result.getDate("fechaFin");
				float precioPagado = result.getFloat("precioPagado");
				
				// Creamos el objeto estancia y lo vamos añadiendo al ArrayList
				list.add(new Estancia( nombreEstudiante, nombreResidencia, fInicio.toString(), fFin.toString(), precioPagado));
			}

			return list;
			
		} catch (SQLException e) {
			BDApp.sendConnectionError(e.toString());
		}
		
		return null;
	}
	
	public void insertarResidencia() {
		
		try {
			
			PreparedStatement st = connection.prepareStatement("insert into residencias values (null, ?, ?, ?, ?, ?)");
			
		} catch( SQLException e ) {
			
		}
	}
}
