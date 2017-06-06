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
@Table(name = "notificacionsolicitud", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class NotificacionSolicitud implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.notificacionsolicitud_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@Column(name = "descripcion", nullable = false)
	private String descripcion;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatusnotificacion", referencedColumnName = "id")
	private EstatusNotificacion estatusNotificacion;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idbitacorasolicitud", referencedColumnName = "id")
	private BitacoraSolicitud bitacora;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempleado", referencedColumnName = "id")
	private Usuario empleado;

	public NotificacionSolicitud() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NotificacionSolicitud(String descripcion,
			EstatusNotificacion estatusNotificacion,
			BitacoraSolicitud bitacoraSolicitud, Usuario empleado) {
		super();
		this.descripcion = descripcion;
		this.estatusNotificacion = estatusNotificacion;
		this.bitacora = bitacoraSolicitud;
		this.empleado = empleado;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public EstatusNotificacion getEstatusNotificacion() {
		return estatusNotificacion;
	}

	public void setEstatusNotificacion(EstatusNotificacion estatusNotificacion) {
		this.estatusNotificacion = estatusNotificacion;
	}

	public BitacoraSolicitud getBitacora() {
		return bitacora;
	}

	public void setBitacora(BitacoraSolicitud bitacoraSolicitud) {
		this.bitacora = bitacoraSolicitud;
	}

	public Usuario getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Usuario empleado) {
		this.empleado = empleado;
	}

}
