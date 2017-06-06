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
@Table(name = "notificaciontareaasignada", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class NotificacionTareaAsignada implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.notificaciontareaasignada_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@Column(name = "descripcion", nullable = false)
	private String descripcion;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempleado", referencedColumnName = "id")
	private Usuario empleado;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idbitacoratareaasignada", referencedColumnName = "id")
	private BitacoraTareaAsignada bitacora;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatusnotificacion", referencedColumnName = "id")
	private EstatusNotificacion estatusNotificacion;

	public NotificacionTareaAsignada() {
		super();
	}

	public NotificacionTareaAsignada(String descripcion, Usuario empleado,
			BitacoraTareaAsignada bitacoraTareaAsignada,
			EstatusNotificacion estatusNotificacion) {
		super();
		this.descripcion = descripcion;
		this.empleado = empleado;
		this.bitacora = bitacoraTareaAsignada;
		this.estatusNotificacion = estatusNotificacion;
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

	public Usuario getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Usuario empleado) {
		this.empleado = empleado;
	}

	public BitacoraTareaAsignada getBitacora() {
		return bitacora;
	}

	public void setBitacoraT(BitacoraTareaAsignada bitacoraTareaAsignada) {
		this.bitacora = bitacoraTareaAsignada;
	}

	public EstatusNotificacion getEstatusNotificacion() {
		return estatusNotificacion;
	}

	public void setEstatusNotificacion(EstatusNotificacion estatusNotificacion) {
		this.estatusNotificacion = estatusNotificacion;
	}

}
