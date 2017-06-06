package modelo.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "bitacoratarearepetitivaasignada", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class BitacoraTareaRepetitivaAsignada implements Serializable {
	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.bitacoratarearepetitivaasignada_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idtarearepetitivaasignada", referencedColumnName = "id")
	private TareaRepetitivaAsignada tareaRepetitivaAsignada;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempleado", referencedColumnName = "id")
	private Usuario empleado;

	@Column(name = "fecha", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;

	@Column(name = "descripcion", nullable = false)
	private String descripcion;

	@Column(name = "accion", nullable = false)
	private String accion;

	public BitacoraTareaRepetitivaAsignada() {
		super();
	}

	public BitacoraTareaRepetitivaAsignada(
			TareaRepetitivaAsignada tareaRepetitivaAsignada, Usuario empleado,
			Date fecha, String descripcion, String accion) {
		super();
		this.tareaRepetitivaAsignada = tareaRepetitivaAsignada;
		this.empleado = empleado;
		this.fecha = fecha;
		this.descripcion = descripcion;
		this.accion = accion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TareaRepetitivaAsignada getTareaRepetitivaAsignada() {
		return tareaRepetitivaAsignada;
	}

	public void setTareaRepetitivaAsignada(
			TareaRepetitivaAsignada tareaRepetitivaAsignada) {
		this.tareaRepetitivaAsignada = tareaRepetitivaAsignada;
	}

	public Usuario getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Usuario empleado) {
		this.empleado = empleado;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

}
