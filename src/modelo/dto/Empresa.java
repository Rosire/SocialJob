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
@Table(name = "empresa", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class Empresa implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.empresa_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@Column(name = "rif", length = 50, nullable = false)
	private String rif;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "direccion", nullable = false)
	private String direccion;

	@Column(name = "telefono", nullable = false)
	private String telefono;

	@Column(name = "mision", nullable = false)
	private String mision;

	@Column(name = "vision", nullable = false)
	private String vision;

	@Column(name = "correo", nullable = false)
	private String correo;

	@Column(name = "twitter", nullable = false)
	private String twitter;

	@Column(name = "logo", nullable = false)
	private String logo;

	@ManyToOne(optional = true)
	@JoinColumn(name = "idadministrador", referencedColumnName = "id", nullable = true)
	private Usuario usuario;

	public Empresa() {
		super();
	}

	public Empresa(String rif, String nombre, String direccion,
			String telefono, String mision, String vision, String correo,
			String twitter, String logo, Usuario usuario) {
		super();
		this.rif = rif;
		this.nombre = nombre;
		this.direccion = direccion;
		this.telefono = telefono;
		this.mision = mision;
		this.vision = vision;
		this.correo = correo;
		this.twitter = twitter;
		this.logo = logo;
		this.usuario = usuario;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRif() {
		return rif;
	}

	public void setRif(String rif) {
		this.rif = rif;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getMision() {
		return mision;
	}

	public void setMision(String mision) {
		this.mision = mision;
	}

	public String getVision() {
		return vision;
	}

	public void setVision(String vision) {
		this.vision = vision;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}
