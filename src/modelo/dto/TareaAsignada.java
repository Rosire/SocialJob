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

import org.postgresql.util.PGInterval;

@Entity
@Table(name = "tareaasignada", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class TareaAsignada implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.tareaasignada_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idtarea", referencedColumnName = "id")
	private Tarea tarea;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatustareaasignada", referencedColumnName = "id")
	private EstatusTareaAsignada estatusTareaAsignada;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idprioridad", referencedColumnName = "id")
	private Prioridad prioridad;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idcargoempleado", referencedColumnName = "id")
	private CargoEmpleado cargoEmpleado;

	@Column(name = "fecha", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;

	@Column(name = "indicaciones", nullable = false)
	private String indicaciones;

	@Column(name = "progreso", nullable = false)
	private int progreso;

	@Column(name = "calificacion", nullable = true)
	private int calificacion;

	@Column(name = "fechainicio", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicio;

	@Column(name = "fechafinalizacion", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFinalizacion;

	@Column(name = "duracion", nullable = false)
	private long duracion;

	@Column(name = "eficiencia", nullable = true)
	private long eficiencia;

	public TareaAsignada() {
		super();
	}

	public TareaAsignada(Tarea tarea,
			EstatusTareaAsignada estatusTareaAsignada, Prioridad prioridad,
			CargoEmpleado cargoEmpleado, Date fecha, String indicaciones,
			int progreso, int calificacion, Date fechaInicio,
			Date fechaFinalizacion, long duracion, long eficiencia) {
		super();
		this.tarea = tarea;
		this.estatusTareaAsignada = estatusTareaAsignada;
		this.prioridad = prioridad;
		this.cargoEmpleado = cargoEmpleado;
		this.fecha = fecha;
		this.indicaciones = indicaciones;
		this.progreso = progreso;
		this.calificacion = calificacion;
		this.fechaInicio = fechaInicio;
		this.fechaFinalizacion = fechaFinalizacion;
		this.duracion = duracion;
		this.eficiencia = eficiencia;
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

	public EstatusTareaAsignada getEstatusTareaAsignada() {
		return estatusTareaAsignada;
	}

	public void setEstatusTareaAsignada(
			EstatusTareaAsignada estatusTareaAsignada) {
		this.estatusTareaAsignada = estatusTareaAsignada;
	}

	public Prioridad getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(Prioridad prioridad) {
		this.prioridad = prioridad;
	}

	public CargoEmpleado getCargoEmpleado() {
		return cargoEmpleado;
	}

	public void setCargoEmpleado(CargoEmpleado cargoEmpleado) {
		this.cargoEmpleado = cargoEmpleado;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getIndicaciones() {
		return indicaciones;
	}

	public void setIndicaciones(String indicaciones) {
		this.indicaciones = indicaciones;
	}

	public int getProgreso() {
		return progreso;
	}

	public void setProgreso(int progreso) {
		this.progreso = progreso;
	}

	public int getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(int calificacion) {
		this.calificacion = calificacion;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFinalizacion() {
		return fechaFinalizacion;
	}

	public void setFechaFinalizacion(Date fechaFinalizacion) {
		this.fechaFinalizacion = fechaFinalizacion;
	}

	public long getDuracion() {
		return duracion;
	}

	public void setDuracion(long duracion) {
		this.duracion = duracion;
	}

	public long getEficiencia() {
		return eficiencia;
	}

	public void setEficiencia(long eficiencia) {
		this.eficiencia = eficiencia;
	}

}
