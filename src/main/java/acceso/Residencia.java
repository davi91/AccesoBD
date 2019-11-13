package acceso;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Residencia {
	
	private IntegerProperty id = new SimpleIntegerProperty();
	private StringProperty nombre = new SimpleStringProperty();
	private StringProperty codUniversidad = new SimpleStringProperty();
	private FloatProperty precio = new SimpleFloatProperty(0);
	private BooleanProperty comedor = new SimpleBooleanProperty(false);
	
	// Específicos para la tabla
	private StringProperty comedorStr = new SimpleStringProperty();
	private StringProperty nombreUniversidad = new SimpleStringProperty();
	
	private Residencia() {
		
		comedorStr.bind( Bindings.when(comedor.not())
						 .then( new SimpleStringProperty("Si"))
						 .otherwise( new SimpleStringProperty("No")));
	}
	
	/**
	 * Constructor específico para la tabla de visualización de residencia
	 * @param id : ID de la residencia
	 * @param nombre Nombre de la residencia
	 * @param nomUniversidad El nombre de la universidad
	 * @param precio El precio de la misma
	 * @param comedor Si tiene o no comedor
	 */
	
	public Residencia( int id, String nombre, String nombreUniversidad, float precio, boolean comedor ) {
		
		this();
		setId(id);
		setNombre(nombre);
		setNombreUniversidad(nombreUniversidad);
		setPrecio(precio);
		setComedor(comedor);
		
	}

	
	public final IntegerProperty idProperty() {
		return this.id;
	}
	

	public final int getId() {
		return this.idProperty().get();
	}
	

	public final void setId(final int id) {
		this.idProperty().set(id);
	}
	

	public final StringProperty nombreProperty() {
		return this.nombre;
	}
	

	public final String getNombre() {
		return this.nombreProperty().get();
	}
	

	public final void setNombre(final String nombre) {
		this.nombreProperty().set(nombre);
	}
	

	public final StringProperty codUniversidadProperty() {
		return this.codUniversidad;
	}
	

	public final String getCodUniversidad() {
		return this.codUniversidadProperty().get();
	}
	

	public final void setCodUniversidad(final String codUniversidad) {
		this.codUniversidadProperty().set(codUniversidad);
	}
	

	public final FloatProperty precioProperty() {
		return this.precio;
	}
	

	public final float getPrecio() {
		return this.precioProperty().get();
	}
	

	public final void setPrecio(final float precio) {
		this.precioProperty().set(precio);
	}
	

	public final BooleanProperty comedorProperty() {
		return this.comedor;
	}
	

	public final boolean isComedor() {
		return this.comedorProperty().get();
	}
	

	public final void setComedor(final boolean comedor) {
		this.comedorProperty().set(comedor);
	}
	

	public final StringProperty comedorStrProperty() {
		return this.comedorStr;
	}
	

	public final String getComedorStr() {
		return this.comedorStrProperty().get();
	}
	

	public final void setComedorStr(final String comedorStr) {
		this.comedorStrProperty().set(comedorStr);
	}

	public final StringProperty nombreUniversidadProperty() {
		return this.nombreUniversidad;
	}
	

	public final String getNombreUniversidad() {
		return this.nombreUniversidadProperty().get();
	}
	

	public final void setNombreUniversidad(final String nombreUniversidad) {
		this.nombreUniversidadProperty().set(nombreUniversidad);
	}
	
	

	

}
