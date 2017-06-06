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

import org.hibernate.annotations.Type;

@Entity
@Table(name = "tarearepetitiva", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class TareaRepetitiva implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.tarearepetitiva_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "idtarea", referencedColumnName = "id")
	private Tarea tarea;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idprioridad", referencedColumnName = "id")
	private Prioridad prioridad;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idcargo", referencedColumnName = "id")
	private Cargo cargo;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idfrecuencia", referencedColumnName = "id")
	private Frecuencia frecuencia;

	@Column(name = "indicaciones", nullable = false)
	private String indicaciones;

	@Column(name = "vecesrepetir", nullable = false)
	private int vecesRepetir;

	@Column(name = "fechainicio", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicio;

	@Column(name = "expiracion", nullable = false)
	@Type(type = "boolean")
	private boolean expiracion;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatus", referencedColumnName = "id")
	private Estatus estatus;

	public TareaRepetitiva() {
		super();
	}

	public TareaRepetitiva(Tarea tarea, Prioridad prioridad, Cargo cargo,
			Frecuencia frecuencia, String indicaciones, int vecesRepetir,
			Date fechaInicio, boolean expiracion, Estatus estatus) {
		super();
		this.tarea = tarea;
		this.prioridad = prioridad;
		this.cargo = cargo;
		this.frecuencia = frecuencia;
		this.indicaciones = indicaciones;
		this.vecesRepetir = vecesRepetir;
		this.fechaInicio = fechaInicio;
		this.expiracion = expiracion;
		this.estatus = estatus;
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

	public Prioridad getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(Prioridad prioridad) {
		this.prioridad = prioridad;
	}

	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Frecuencia getFrecuencia() {
		return frecuencia;
	}

	public void setFrecuencia(Frecuencia frecuencia) {
		this.frecuencia = frecuencia;
	}

	public String getIndicaciones() {
		return indicaciones;
	}

	public void setIndicaciones(String indicaciones) {
		this.indicaciones = indicaciones;
	}

	public int getVecesRepetir() {
		return vecesRepetir;
	}

	public void setVecesRepetir(int vecesRepetir) {
		this.vecesRepetir = vecesRepetir;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public boolean isExpiracion() {
		return expiracion;
	}

	public void setExpiracion(boolean expiracion) {
		this.expiracion = expiracion;
	}

	public Estatus getEstatus() {
		return estatus;
	}

	public void setEstatus(Estatus estatus) {
		this.estatus = estatus;
	}

}
