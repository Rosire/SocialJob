package arbol.demo.data.pojo;

public class CargoArbol {
	private final String nombre;
	private final String supervisor;
	private final String foto;
	private final int id;

	public CargoArbol(String nombre, int id) {
		this.nombre = nombre;
		this.foto = null;
		this.supervisor = null;
		this.id = id;
	}

	public CargoArbol(String nombre, String foto, String supervisor, int id) {
		this.nombre = nombre;
		this.foto = foto;
		this.supervisor = supervisor;
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getSupervisor() {
		return supervisor;
	}

	public String getFoto() {
		return foto;
	}

	public int getId() {
		return id;
	}

}
