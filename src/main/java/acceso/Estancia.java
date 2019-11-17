package acceso;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Estancia {

	
	private StringProperty nombreUniversidad = new SimpleStringProperty();
	private StringProperty nombreResidencia = new SimpleStringProperty();
	private StringProperty fechaInicio = new SimpleStringProperty();
	private StringProperty fechaFin = new SimpleStringProperty();
	private FloatProperty precioPagado = new SimpleFloatProperty();
	
	public Estancia(String nombreUniversidad, String nombreResidencia, String fechaInicio, String fechaFin, float precioPagado) {
	
			setNombreUniversidad(nombreUniversidad);
			setNombreResidencia(nombreResidencia);
			setFechaInicio(fechaInicio);
			setFechaFin(fechaFin);
			setPrecioPagado(precioPagado);
	}

	
	public final StringProperty nombreResidenciaProperty() {
		return this.nombreResidencia;
	}
	
	public final String getNombreResidencia() {
		return this.nombreResidenciaProperty().get();
	}
	
	public final void setNombreResidencia(final String nombreResidencia) {
		this.nombreResidenciaProperty().set(nombreResidencia);
	}
	
	public final StringProperty fechaInicioProperty() {
		return this.fechaInicio;
	}
	
	public final String getFechaInicio() {
		return this.fechaInicioProperty().get();
	}
	
	public final void setFechaInicio(final String fechaInicio) {
		this.fechaInicioProperty().set(fechaInicio);
	}
	
	public final StringProperty fechaFinProperty() {
		return this.fechaFin;
	}
	
	public final String getFechaFin() {
		return this.fechaFinProperty().get();
	}
	
	public final void setFechaFin(final String fechaFin) {
		this.fechaFinProperty().set(fechaFin);
	}
	
	public final FloatProperty precioPagadoProperty() {
		return this.precioPagado;
	}
	
	public final float getPrecioPagado() {
		return this.precioPagadoProperty().get();
	}
	
	public final void setPrecioPagado(final float precioPagado) {
		this.precioPagadoProperty().set(precioPagado);
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
