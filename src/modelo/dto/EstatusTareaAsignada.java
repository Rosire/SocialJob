package modelo.dto;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "estatustareaasignada", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class EstatusTareaAsignada implements Serializable {
	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.estatustareaasignada_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	public EstatusTareaAsignada() {
		super();
	}

	public EstatusTareaAsignada(String nombre) {
		super();
		this.nombre = nombre;
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

}
