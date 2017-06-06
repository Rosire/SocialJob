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

@Entity
@Table(name = "frecuencia", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class Frecuencia implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.frecuencia_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "periododias", nullable = false)
	private int periodoDias;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempresa", referencedColumnName = "id")
	private Empresa empresa;

	public Frecuencia() {
		super();
	}

	public Frecuencia(String nombre, int periodoDias, Empresa empresa) {
		super();
		this.nombre = nombre;
		this.periodoDias = periodoDias;
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

	public int getPeriodoDias() {
		return periodoDias;
	}

	public void setPeriodoDias(int periodoDias) {
		this.periodoDias = periodoDias;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

}
