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
@Table(name = "cargoempleado", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class CargoEmpleado implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.cargoempleado_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempleado", referencedColumnName = "id")
	private Usuario empleado;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idcargo", referencedColumnName = "id")
	private Cargo cargo;

	@Column(name = "fechaasignacion", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaAsignacion;

	public CargoEmpleado() {
		super();
	}

	public CargoEmpleado(Usuario empleado, Cargo cargo, Date fechaAsignacion) {
		super();
		this.empleado = empleado;
		this.cargo = cargo;
		this.fechaAsignacion = fechaAsignacion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Usuario empleado) {
		this.empleado = empleado;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Date getFechaAsignacion() {
		return fechaAsignacion;
	}

	public void setFechaAsignacion(Date fechaAsignacion) {
		this.fechaAsignacion = fechaAsignacion;
	}

}
