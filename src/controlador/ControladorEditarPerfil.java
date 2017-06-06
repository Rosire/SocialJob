package controlador;

import java.io.IOException;

import modelo.dao.EstatusDAO;
import modelo.dao.UsuarioDAO;
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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorEditarPerfil extends SelectorComposer<Component> {

	// Wire DatosEmpleado
	@Wire
	private Image imagenEmpleado;
	@Wire
	private Media media;
	@Wire
	private Textbox lblNombres;
	@Wire
	private Textbox lblApellidos;
	@Wire
	private Textbox lblEmail;
	@Wire
	private Textbox lblDireccion;
	@Wire
	private Textbox lblTelefono;
	@Wire
	private Intbox lblCedula;
	@Wire
	private Textbox lblFoto;
	@Wire
	private Datebox lblFechaContratacion;
	@Wire
	private Window windowsEditarPerfil;
	@Wire
	private Div divConsulta;
	@Wire
	private Div divModificar;
	@Wire
	private Label labelCedula;
	@Wire
	private Listbox listaCargos;
	@Wire
	private Button btnCargarFoto;
	@Wire
	private Radiogroup grupoSexo;

	private Session miSession = Sessions.getCurrent();
	private Usuario usuario = (Usuario) miSession.getAttribute("usuario");

	private boolean nuevaFoto = false;
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();

	public ControladorEditarPerfil() {
		super();
	}

	@Listen("onUpload = #btnCargarFoto")
	public void processFoto(UploadEvent event) throws IOException {
		nuevaFoto = true;
		media = event.getMedia();
		if (media instanceof org.zkoss.image.Image) {
			imagenEmpleado.setContent((org.zkoss.image.Image) media);
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
							windowsEditarPerfil.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onClick=#btnGuardarModificacion; onOK=#btnGuardarModificacion")
	public void modificarUsuario() {
		String cedula = lblCedula.getValue().toString();
		String nombres = lblNombres.getValue();
		String apellidos = lblApellidos.getValue();
		String email = lblEmail.getValue();
		String direccion = lblDireccion.getValue();
		String telefono = lblTelefono.getValue();
		String sexo = grupoSexo.getSelectedItem().getValue();
		String foto;
		if (nuevaFoto) {
			if (media == null) {
				showNotify("Debe ingresar La foto del Empleado", btnCargarFoto);
				btnCargarFoto.focus();
				return;
			} else {
				Util.borrar(usuario.getFoto());
				if (!Util.uploadFile(media, cedula)) {
					showNotify("Error al Subir Foto del Empleado",
							imagenEmpleado);
					btnCargarFoto.focus();
					return;
				}
			}
			String typeFoto = media.getContentType();
			String extension = typeFoto.substring(typeFoto.indexOf("/") + 1);
			String fileName = cedula + "." + extension;
			foto = "/uploads/" + fileName;
		} else {
			foto = usuario.getFoto();
		}

		Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
		Usuario usuarioModificado = new Usuario(cedula,
				usuario.getContrasenna(), estatus, usuario.getTipoUsuario(),
				usuario.getEmpresa(), nombres, apellidos, email, direccion,
				sexo, telefono, foto);
		usuarioModificado.setId(usuario.getId());
		if (!usuarioModificado.equals(null)) {
			usuarioDAO.actualizarUsuario(usuarioModificado);
			showNotifyInfo("Usuario Modificado Exitosamente",
					windowsEditarPerfil);
			windowsEditarPerfil.onClose();
			Session miSession = Sessions.getCurrent();
			miSession.setAttribute("usuario", usuarioModificado);
			BindUtils.postGlobalCommand(null, null,
					"actualizarInformacionUsuario", null);
			BindUtils.postGlobalCommand(null, null, "actualizarUsuario", null);

		} else {
			showNotify("Error al almacenar Empleado", windowsEditarPerfil);
		}

	}

	@Listen("onChange = #lblCedula")
	public void validacionCedula() {
		String cedula = lblCedula.getValue().toString();
		if (cedula.equalsIgnoreCase("")) {
			showNotify("Debe ingresar la cedula del empleado", lblCedula);
			lblCedula.focus();
		}
		Usuario empleado = usuarioDAO.obtenerUsuario(cedula);

		if ((empleado != null)
				&& !(empleado.getCedula().equals(labelCedula.getValue()))) {
			showNotify("Empleado Registrado en Base de Datos", lblCedula);
			lblCedula.focus();
		}
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 5000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
