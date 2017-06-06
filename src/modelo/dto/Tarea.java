package modelo.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.postgresql.util.PGInterval;

@Entity
@Table(name = "tarea", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class Tarea implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.tarea_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "duracion", nullable = false)
	private long duracion;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatustarea", referencedColumnName = "id")
	private Estatus estatus;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempresa", referencedColumnName = "id")
	private Empresa empresa;

	public Tarea() {
		super();
	}

	public Tarea(String nombre, long duracion, Estatus estatus, Empresa empresa) {
		super();
		this.nombre = nombre;
		this.duracion = duracion;
		this.estatus = estatus;
		this.empresa = empresa;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public long getDuracion() {
		return duracion;
	}

	public void setDuracion(long duracion) {
		this.duracion = duracion;
	}

	public Estatus getEstatus() {
		return estatus;
	}

	public void setEstatus(Estatus estatus) {
		this.estatus = estatus;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

}
