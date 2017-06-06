package controlador;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import modelo.dao.EmpresaDAO;
import modelo.dao.EstatusDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.Empresa;
import modelo.dto.Estatus;
import modelo.dto.Usuario;
import modelo.util.Util;

import org.zkoss.bind.BindUtils;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorEditarEmpresa extends SelectorComposer<Component> {

	// Wire DatosEmpleado
	@Wire
	private Image imagenEmpresa;
	@Wire
	private Media media;
	@Wire
	private Textbox lblNombre;
	@Wire
	private Textbox lblTwitter;
	@Wire
	private Textbox lblMision;
	@Wire
	private Textbox lblVision;
	@Wire
	private Textbox lblEmail;
	@Wire
	private Textbox lblDireccion;
	@Wire
	private Longbox lblTelefono;
	@Wire
	private Textbox lblRif;

	@Wire
	private Window windowsEditarEmpresa;
	@Wire
	private Label labelRif;

	@Wire
	private Button btnCargarFoto;

	private Session miSession = Sessions.getCurrent();
	private Usuario usuario = (Usuario) miSession.getAttribute("usuario");

	private boolean nuevaFoto = false;
	private EmpresaDAO empresaDAO = new EmpresaDAO();

	public ControladorEditarEmpresa() {
		super();
	}

	@Listen("onUpload = #btnCargarFoto")
	public void processFoto(UploadEvent event) throws IOException {
		nuevaFoto = true;
		media = event.getMedia();
		if (media instanceof org.zkoss.image.Image) {
			imagenEmpresa.setContent((org.zkoss.image.Image) media);
		} else {
			media = null;
			Messagebox.show("Not an image: " + media, "Error", Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Listen("onClick=#btnCancelarModificacion")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Cancelar Modificación",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowsEditarEmpresa.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onClick=#btnGuardarModificacion; onOK=#btnGuardarModificacion")
	public void modificarUsuario() {
		Empresa empresaAnterior = usuario.getEmpresa();

		String rif = lblRif.getValue().toString();

		String nombre = lblNombre.getValue().toString();

		String direccion = lblDireccion.getValue().toString();

		String telefono = lblTelefono.getValue().toString();

		String mision = lblMision.getValue().toString();

		String vision = lblVision.getValue().toString();

		String correo = lblEmail.getValue().toString();

		String twitter = lblTwitter.getValue().toString();

		String logo;

		if (nuevaFoto) {
			if (media == null) {
				showNotify("Debe ingresar La foto del Empleado", btnCargarFoto);
				btnCargarFoto.focus();
				return;
			} else {
				Util.borrar(empresaAnterior.getLogo());
				if (!Util.uploadFile(media, rif)) {
					showNotify("Error al Subir Foto del Empleado",
							imagenEmpresa);
					btnCargarFoto.focus();
					return;
				}
			}
			String typeFoto = media.getContentType();
			String extension = typeFoto.substring(typeFoto.indexOf("/") + 1);
			String fileName = rif + "." + extension;
			logo = "/uploads/" + fileName;
		} else {
			logo = empresaAnterior.getLogo();
		}

		Empresa empresaModificada = new Empresa(rif, nombre, direccion,
				telefono, mision, vision, correo, twitter, logo, usuario);

		if (!empresaAnterior.equals(null)) {
			empresaModificada.setId(empresaAnterior.getId());
			usuario.setEmpresa(empresaModificada);
			empresaModificada.setUsuario(usuario);
			empresaDAO.actualizarEmpresa(empresaModificada);
			showNotifyInfo("Usuario Modificado Exitosamente",
					windowsEditarEmpresa);
			windowsEditarEmpresa.onClose();
			Session miSession = Sessions.getCurrent();
			miSession.setAttribute("usuario", usuario);
			BindUtils.postGlobalCommand(null, null, "actualizarEmpresa", null);
			BindUtils.postGlobalCommand(null, null,
					"actualizarInformacionUsuario", null);
			BindUtils.postGlobalCommand(null, null, "actualizarUsuario", null);

		} else {
			showNotify("Error al almacenar Empleado", windowsEditarEmpresa);
		}

	}

	@Listen("onChange = #lblRif")
	public void validacionCedula() {
		String rif = lblRif.getValue().toString();
		if (rif.equalsIgnoreCase("")) {
			showNotify("Debe ingresar la cedula del empleado", lblRif);
			lblRif.focus();
		}
		Empresa empresa = empresaDAO.obtenerEmpresa(rif);

		if ((empresa != null)
				&& !(empresa.getRif().equals(labelRif.getValue().toString()))) {
			showNotify("Empleado Registrado en Base de Datos", lblRif);
			lblRif.focus();
		}
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 5000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
