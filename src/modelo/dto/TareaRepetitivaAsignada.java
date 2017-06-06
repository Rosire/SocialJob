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
@Table(name = "tarearepetitivaasignada", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class TareaRepetitivaAsignada implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.tarearepetitivaasignada_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idtarearepetitiva", referencedColumnName = "id")
	private TareaRepetitiva tareaRepetitiva;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempleado", referencedColumnName = "id")
	private Usuario empleado;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatustareaasignada", referencedColumnName = "id")
	private EstatusTareaAsignada estatusTareaAsignada;

	@Column(name = "fecha", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;

	@Column(name = "fechainicio", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicio;

	@Column(name = "fechafinalizacion", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFinalizacion;

	@Column(name = "progreso", nullable = false)
	private int progreso;

	@Column(name = "calificacion", nullable = true)
	private int calificacion;

	@Column(name = "duracion", nullable = false)
	private long duracion;

	@Column(name = "eficiencia", nullable = true)
	private long eficiencia;

	public TareaRepetitivaAsignada() {
		super();
	}

	public TareaRepetitivaAsignada(TareaRepetitiva tareaRepetitiva,
			Usuario empleado, EstatusTareaAsignada estatusTareaAsignada,
			Date fecha, Date fechaInicio, Date fechaFinalizacion, int progreso,
			int calificacion, long duracion, long eficiencia) {
		super();
		this.tareaRepetitiva = tareaRepetitiva;
		this.empleado = empleado;
		this.estatusTareaAsignada = estatusTareaAsignada;
		this.fecha = fecha;
		this.fechaInicio = fechaInicio;
		this.fechaFinalizacion = fechaFinalizacion;
		this.progreso = progreso;
		this.calificacion = calificacion;
		this.duracion = duracion;
		this.duracion = duracion;
		this.eficiencia = eficiencia;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public TareaRepetitiva getTareaRepetitiva() {
		return tareaRepetitiva;
	}

	public void setTareaRepetitiva(TareaRepetitiva tareaRepetitiva) {
		this.tareaRepetitiva = tareaRepetitiva;
	}

	public Usuario getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Usuario empleado) {
		this.empleado = empleado;
	}

	public EstatusTareaAsignada getEstatusTareaAsignada() {
		return estatusTareaAsignada;
	}

	public void setEstatusTareaAsignada(
			EstatusTareaAsignada estatusTareaAsignada) {
		this.estatusTareaAsignada = estatusTareaAsignada;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
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
