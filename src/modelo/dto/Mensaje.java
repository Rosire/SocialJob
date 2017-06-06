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
@Table(name = "mensaje", schema = "public")
@PrimaryKeyJoinColumn(name = "id")
public class Mensaje implements Serializable {

	@Id
	@Column(name = "id", length = 50, nullable = false)
	@SequenceGenerator(name = "SeqId", sequenceName = "public.mensaje_id_seq", allocationSize = 1)
	@GeneratedValue(generator = "SeqId")
	private int id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idusuarioremitente", referencedColumnName = "id")
	private Usuario usuarioRemitente;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idusuariodestinatario", referencedColumnName = "id")
	private Usuario usuarioDestinatario;

	@Column(name = "asunto", nullable = false)
	private String asunto;

	@Column(name = "texto", nullable = false)
	private String texto;

	@Column(name = "fecha", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;

	@ManyToOne(optional = false)
	@JoinColumn(name = "idestatusmensaje", referencedColumnName = "id")
	private EstatusMensaje estatusMensaje;

	public Mensaje() {
		super();
	}

	public Mensaje(Usuario usuarioRemitente, Usuario usuarioDestinatario,
			String asunto, String texto, Date fecha,
			EstatusMensaje estatusMensaje) {
		super();
		this.usuarioRemitente = usuarioRemitente;
		this.usuarioDestinatario = usuarioDestinatario;
		this.asunto = asunto;
		this.texto = texto;
		this.fecha = fecha;
		this.estatusMensaje = estatusMensaje;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuarioRemitente() {
		return usuarioRemitente;
	}

	public void setUsuarioRemitente(Usuario usuarioRemitente) {
		this.usuarioRemitente = usuarioRemitente;
	}

	public Usuario getUsuarioDestinatario() {
		return usuarioDestinatario;
	}

	public void setUsuarioDestinatario(Usuario usuarioDestinatario) {
		this.usuarioDestinatario = usuarioDestinatario;
	}

	public String getAsunto() {
		return asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public EstatusMensaje getEstatusMensaje() {
		return estatusMensaje;
	}

	public void setEstatusMensaje(EstatusMensaje estatusMensaje) {
		this.estatusMensaje = estatusMensaje;
	}
}
