package modelo.dto;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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

import modelo.dao.CargoEmpleadoDAO;

import org.w3c.dom.ls.LSInput;

@Entity
@Table(name = "usuario", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class Usuario {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.usuario_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@Column(name = "cedula", nullable = false)
	private String cedula;

	@Column(name = "contrasenna", nullable = false)
	private String contrasenna;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatususuario", referencedColumnName = "id")
	private Estatus estatusUsuario;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idtipousuario", referencedColumnName = "id")
	private TipoUsuario tipoUsuario;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idempresa", referencedColumnName = "id")
	private Empresa empresa;

	@Column(name = "nombres", nullable = false)
	private String nombres;

	@Column(name = "apellidos", nullable = false)
	private String apellidos;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "direccion", nullable = false)
	private String direccion;

	@Column(name = "sexo", nullable = false)
	private String sexo;

	@Column(name = "telefono", nullable = false)
	private String telefono;

	@Column(name = "foto", nullable = false)
	private String foto;

//	@OneToMany(cascade = CascadeType.ALL)
//	@JoinColumn(name = "id")
//	private List<CargoEmpleado> cargosEmpleado;

	public Usuario() {
		super();
	}

	public Usuario(String cedula, String contrasenna, Estatus estatusUsuario,
			TipoUsuario tipoUsuario, Empresa empresa, String nombres,
			String apellidos, String email, String direccion, String sexo,
			String telefono, String foto) {
		super();
		this.cedula = cedula;
		this.contrasenna = contrasenna;
		this.estatusUsuario = estatusUsuario;
		this.tipoUsuario = tipoUsuario;
		this.empresa = empresa;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.email = email;
		this.direccion = direccion;
		this.sexo = sexo;
		this.telefono = telefono;
		this.foto = foto;
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getContrasenna() {
		return contrasenna;
	}

	public void setContrasenna(String contrasenna) {
		this.contrasenna = contrasenna;
	}

	public Estatus getEstatusUsuario() {
		return estatusUsuario;
	}

	public void setEstatusUsuario(Estatus estatusUsuario) {
		this.estatusUsuario = estatusUsuario;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

//	public List<CargoEmpleado> getCargosEmpleado() {
//		return cargosEmpleado;
//	}
//
//	public void setCargosEmpleado(List<CargoEmpleado> cargosEmpleado) {
//		this.cargosEmpleado = cargosEmpleado;
//	}

}
