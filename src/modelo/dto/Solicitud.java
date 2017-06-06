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
@Table(name = "solicitud", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class Solicitud implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.solicitud_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idtarea", referencedColumnName = "id")
	private Tarea tarea;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempleadosolicitud", referencedColumnName = "id")
	private Usuario empleadoSolicitud;

	@ManyToOne(optional = true)
	@JoinColumn(name = "idempleadotarea", referencedColumnName = "id", nullable = true)
	private Usuario empleadoTarea;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idcargotarea", referencedColumnName = "id")
	private Cargo cargoTarea;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatussolicitud", referencedColumnName = "id")
	private EstatusSolicitud estatusSolicitud;

	@Column(name = "fechasolicitud", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaSolicitud;

	@Column(name = "fechatarea", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaTarea;

	public Solicitud() {
		super();
	}

	public Solicitud(Tarea tarea, Usuario empleadoSolicitud,
			Usuario empleadoTarea, Cargo cargoTarea,
			EstatusSolicitud estatusSolicitud, Date fechaSolicitud,
			Date fechaTarea) {
		super();
		this.tarea = tarea;
		this.empleadoSolicitud = empleadoSolicitud;
		this.empleadoTarea = empleadoTarea;
		this.cargoTarea = cargoTarea;
		this.estatusSolicitud = estatusSolicitud;
		this.fechaSolicitud = fechaSolicitud;
		this.fechaTarea = fechaTarea;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Tarea getTarea() {
		return tarea;
	}

	public void setTarea(Tarea tarea) {
		this.tarea = tarea;
	}

	public Usuario getEmpleadoSolicitud() {
		return empleadoSolicitud;
	}

	public void setEmpleadoSolicitud(Usuario empleadoSolicitud) {
		this.empleadoSolicitud = empleadoSolicitud;
	}

	public Usuario getEmpleadoTarea() {
		return empleadoTarea;
	}

	public void setEmpleadoTarea(Usuario empleadoTarea) {
		this.empleadoTarea = empleadoTarea;
	}

	public Cargo getCargoTarea() {
		return cargoTarea;
	}

	public void setCargoTarea(Cargo cargoTarea) {
		this.cargoTarea = cargoTarea;
	}

	public EstatusSolicitud getEstatusSolicitud() {
		return estatusSolicitud;
	}

	public void setEstatusSolicitud(EstatusSolicitud estatusSolicitud) {
		this.estatusSolicitud = estatusSolicitud;
	}

	public Date getFechaSolicitud() {
		return fechaSolicitud;
	}

	public void setFechaSolicitud(Date fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}

	public Date getFechaTarea() {
		return fechaTarea;
	}

	public void setFechaTarea(Date fechaTarea) {
		this.fechaTarea = fechaTarea;
	}

}
