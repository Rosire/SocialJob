package modelo.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "cargo", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class Cargo implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.cargo_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idcargosuperior", referencedColumnName = "id")
	private Cargo cargoSuperior;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatuscargo", referencedColumnName = "id")
	private Estatus estatusCargo;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempresa", referencedColumnName = "id")
	private Empresa empresa;

	
	public Cargo() {
		super();
	}

	public Cargo(String nombre, Cargo cargoSuperior, Estatus estatusCargo,
			Empresa empresa) {
		super();
		this.nombre = nombre;

		this.estatusCargo = estatusCargo;
		this.empresa = empresa;
		if (cargoSuperior != null) {
			this.cargoSuperior = cargoSuperior;
		} else {
			this.cargoSuperior = this;
		}
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

	public Cargo getCargoSuperior() {
		return cargoSuperior;
	}

	public void setCargoSuperior(Cargo cargoSuperior) {
		this.cargoSuperior = cargoSuperior;
	}

	public Estatus getEstatusCargo() {
		return estatusCargo;
	}

	public void setEstatusCargo(Estatus estatusCargo) {
		this.estatusCargo = estatusCargo;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
}
