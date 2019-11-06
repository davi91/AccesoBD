package acceso;

public class Residencia {

	private String nombre;
	private char[] codUniversidad = new char[6];
	private float precio;
	private boolean comedor;
	
	public Residencia( String nombre, char[] codUniversidad, float precio, boolean comedor ) {
		
		setNombre(nombre);
		setCodUniversidad(codUniversidad);
		setPrecio(precio);
		setComedor(comedor);
	}
	
	public Residencia( String nombre, char[] codUniversidad, boolean comedor ) {
		
		setNombre(nombre);
		setCodUniversidad(codUniversidad);
		setComedor(comedor);
	}

	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public char[] getCodUniversidad() {
		return codUniversidad;
	}

	public void setCodUniversidad(char[] codUniversidad) {
		this.codUniversidad = codUniversidad;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public boolean isComedor() {
		return comedor;
	}

	public void setComedor(boolean comedor) {
		this.comedor = comedor;
	}
}
