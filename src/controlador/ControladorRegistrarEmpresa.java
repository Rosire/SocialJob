package controlador;

import java.util.HashMap;
import java.util.Map;

import modelo.dao.CargoDAO;
import modelo.dao.EmpresaDAO;
import modelo.dao.EstatusDAO;
import modelo.dao.TipoUsuarioDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.Cargo;
import modelo.dto.Empresa;
import modelo.dto.Estatus;
import modelo.dto.TipoUsuario;
import modelo.dto.Usuario;
import modelo.util.Util;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorRegistrarEmpresa extends SelectorComposer<Component> {

	// wire components
	@Wire
	private Media mediaEmpresa;
	@Wire
	private Media mediaAdministrador;

	@Wire
	private Div divRegistroAdministrador;
	@Wire
	private Label labelRegistroEmpresa;

	@Wire
	private Div divRegistroEmpresa;
	@Wire
	private Label labelRegistroAdministrador;

	@Wire
	private Div divFinalizar;
	@Wire
	private Label labelFinalizar;

	// Datos Empresa Registrar
	@Wire
	private Button btnCargarFotoEmpresa;
	@Wire
	private Image imagenEmpresa;
	@Wire
	private Textbox lblRif;
	@Wire
	private Textbox lblNombre;
	@Wire
	private Textbox lblEmailEmpresa;
	@Wire
	private Longbox lblTelefonoEmpresa;
	@Wire
	private Textbox lblTwitter;
	@Wire
	private Textbox lblDireccionEmpresa;
	@Wire
	private Textbox lblMision;
	@Wire
	private Textbox lblVision;

	// DAtos del Administrador
	@Wire
	private Image imagenAdministrador;
	@Wire
	private Intbox lblCedula;
	@Wire
	private Textbox lblNombres;
	@Wire
	private Textbox lblApellidos;
	@Wire
	private Textbox lblEmail;
	@Wire
	private Longbox lblTelefono;
	@Wire
	private Textbox lblDireccion;
	@Wire
	private Label labelSexo;
	@Wire
	private Radiogroup grupoSexo;

	// Datos Empresa Mostrar
	@Wire
	private Button btnCargarFotoAdministrador;
	@Wire
	private Image imagenEmpresaRegistrar;
	@Wire
	private Label labelRif;
	@Wire
	private Label labelNombre;
	@Wire
	private Label labelTelefono;
	@Wire
	private Label labelDireccion;
	@Wire
	private Label labelEmail;
	@Wire
	private Label labelTwitter;
	@Wire
	private Label labelVision;
	@Wire
	private Label labelMision;

	// Datos Usuario Mostrar
	@Wire
	private Image imagenAdministradorRegistrar;
	@Wire
	private Label labelCedula;
	@Wire
	private Label labelNombres;
	@Wire
	private Label labelApellidos;
	@Wire
	private Label labelSexoAdministrador;
	@Wire
	private Label labelEmailAdministrador;
	@Wire
	private Label labelTelefonoEmpleado;
	@Wire
	private Label labelbDireccion;

	@Wire("#windowsRegistrarEmpresa")
	private Window windowsRegistrarEmpresa;
	@Wire
	private Button btnGuardar;
	@Wire
	private Button btnCancelar;
	@Wire
	private Button btnSiguiente;
	@Wire
	private Button btnAnterior;

	@Wire
	private Div divInformacionRegistrada;
	@Wire
	private Div divDatosEmpresa;
	@Wire
	private Div divDatosAdministrador;

	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();
	private TipoUsuarioDAO tipoUsuarioDAO = new TipoUsuarioDAO();
	private EmpresaDAO empresaDAO = new EmpresaDAO();
	private CargoDAO cargoDAO = new CargoDAO();

	private Session miSession = Sessions.getCurrent();
	private Empresa empresa = (Empresa) miSession.getAttribute("empresa");
	private Usuario usuario = new Usuario();

	public ControladorRegistrarEmpresa() {
		super();
		mediaEmpresa = (Media) miSession.getAttribute("mediaEmpresa");

	}

	@Listen("onClick=#btnSiguienteEmpresa; onOK=#btnSiguienteEmpresa")
	public void siguienteRegistro() {

		String rif = lblRif.getValue().toString();

		String nombre = lblNombre.getValue().toString();

		String direccion = lblDireccionEmpresa.getValue().toString();

		String telefono = lblTelefonoEmpresa.getValue().toString();

		String mision = lblMision.getValue().toString();

		String vision = lblVision.getValue().toString();

		String correo = lblEmailEmpresa.getValue().toString();

		String twitter = lblTwitter.getValue().toString();

		String logo;
		mediaEmpresa = imagenEmpresa.getContent();

		Empresa empresaBD = empresaDAO.obtenerEmpresa(rif);

		if (mediaEmpresa == null) {

			showNotify("Debe ingresar La foto del Usuario",
					btnCargarFotoEmpresa);
			btnCargarFotoEmpresa.focus();
		} else if ((empresaBD != null)) {
			showNotify("Empresa Registrada en Base de Datos", lblRif);
			lblRif.focus();
		} else {

			String typeFoto = mediaEmpresa.getContentType();
			String extension = typeFoto.substring(typeFoto.indexOf("/") + 1);
			String fileName = rif + "." + extension;
			logo = "/uploads/" + fileName;
			empresa = new Empresa(rif, nombre, direccion, telefono, mision,
					vision, correo, twitter, logo, null);
			if (!empresa.equals(null)) {

				miSession.setAttribute("empresa", empresa);
				miSession.setAttribute("mediaEmpresa", mediaEmpresa);
				windowsRegistrarEmpresa.onClose();
				Map<String, Object> arguments = new HashMap<String, Object>();
				arguments.put("empresa", empresa);
				arguments.put("media", imagenEmpresa.getContent());
				Window window = (Window) Executions.createComponents(
						"vista/registrarAdministradorEmpresa.zul", null,
						arguments);
				window.doModal();

				divRegistroAdministrador.setSclass("progresoActual");
				labelRegistroAdministrador.setSclass("labelProgresoActual");

				labelRegistroEmpresa.setSclass("labelProgresoCompleto");
				divRegistroEmpresa.setSclass("progresoCompleto");

				divFinalizar.setSclass("progreso");
				labelFinalizar.setSclass("labempleado");

			} else {
				showNotify("Error al Crear la Empresa", windowsRegistrarEmpresa);
			}
		}

	}

	@Listen("onClick=#btnSiguiente; onOK=#btnSiguiente")
	public void siguienteRegistroAdministrador() {
		if (divDatosAdministrador.isVisible()) {
			String cedula = lblCedula.getValue().toString();
			String nombres = lblNombres.getValue();
			String apellidos = lblApellidos.getValue();
			String email = lblEmail.getValue();
			String direccion = lblDireccion.getValue();
			String telefono = lblTelefono.getValue().toString();
			Usuario empleado = usuarioDAO.obtenerUsuarioPorEmail(email);
			Usuario empleadoCedula = usuarioDAO.obtenerUsuario(cedula);

			if (mediaAdministrador == null) {
				showNotify("Debe ingresar La foto del Usuario",
						btnCargarFotoAdministrador);
				btnCargarFotoAdministrador.focus();

			} else if (grupoSexo.getSelectedIndex() == -1) {
				showNotify("Debe Seleccionar el sexo del Usuario", labelSexo);
			} else if ((empleado != null)) {
				showNotify("Email de usuario Registrado en Base de Datos",
						lblCedula);
				lblEmail.focus();
			} else if ((empleadoCedula != null)) {
				showNotify("Usuario Registrado en Base de Datos", lblCedula);
				lblCedula.focus();
			} else {
				String sexo = grupoSexo.getSelectedItem().getValue();
				String typeFoto = mediaAdministrador.getContentType();
				String extension = typeFoto
						.substring(typeFoto.indexOf("/") + 1);
				String fileName = cedula + "." + extension;
				String foto = "/uploads/" + fileName;
				TipoUsuario tipoUsuario = tipoUsuarioDAO
						.obtenerTipoUsuarioPorNombre("ADMINISTRADOR");
				Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
				usuario = new Usuario(cedula, cedula, estatus, tipoUsuario,
						empresa, nombres, apellidos, email, direccion, sexo,
						telefono, foto);
				if (usuario != null) {

					divDatosAdministrador.setVisible(false);
					divInformacionRegistrada.setVisible(true);
					btnAnterior.setVisible(true);
					labelCedula.setValue(cedula);
					labelNombres.setValue(nombres);
					labelApellidos.setValue(apellidos);
					labelSexoAdministrador.setValue(sexo);
					labelEmailAdministrador.setValue(email);
					labelTelefonoEmpleado.setValue(telefono);
					labelbDireccion.setValue(direccion);

					divRegistroAdministrador.setSclass("progresoCompleto");
					labelRegistroAdministrador
							.setSclass("labelProgresoCompleto");

					labelRegistroEmpresa.setSclass("labelProgresoCompleto");
					divRegistroEmpresa.setSclass("progresoCompleto");

					divFinalizar.setSclass("progresoActual");
					labelFinalizar.setSclass("labelProgresoActual");

					btnGuardar.setVisible(true);
					btnSiguiente.setVisible(false);

				} else {
					showNotify("Error al Crear el Usuario Administrador",
							windowsRegistrarEmpresa);
				}
			}
		}
	}

	@Listen("onClick=#btnAnterior; onOK=#btnAnterior")
	public void anteriorRegistroAdministrador() {
		if (divDatosAdministrador.isVisible()) {
			windowsRegistrarEmpresa.onClose();
			miSession.setAttribute("usuario", usuario);

			Map<String, Object> arguments = new HashMap<String, Object>();
			arguments.put("empresa", empresa);
			arguments.put("media", imagenEmpresaRegistrar.getContent());
			Window window = (Window) Executions.createComponents(
					"vista/registrarEmpresaAnterior.zul", null, arguments);
			window.doModal();

		} else {
			divDatosAdministrador.setVisible(true);
			divInformacionRegistrada.setVisible(false);

			divRegistroAdministrador.setSclass("progresoActual");
			labelRegistroAdministrador.setSclass("labelProgresoCompleto");

			labelRegistroEmpresa.setSclass("labelProgresoCompleto");
			divRegistroEmpresa.setSclass("progresoCompleto");

			divFinalizar.setSclass("progreso");
			labelFinalizar.setSclass("labempleado");
		}

	}

	@Listen("onClick=#btnGuardar; onOK=#btnGuardar")
	public void guardarEmpresa() {
		if (!Util.uploadFile(mediaAdministrador, usuario.getCedula())) {
			showNotify("Error al Subir Foto del Usuario",
					imagenAdministradorRegistrar);
			imagenAdministradorRegistrar.focus();
		} else if (!Util.uploadFile(mediaEmpresa, empresa.getRif())) {
			showNotify("Error al Subir el logo de la Empresa",
					imagenEmpresaRegistrar);
			imagenEmpresaRegistrar.focus();
		} else {
			empresaDAO.registrarEmpresa(empresa);
			usuarioDAO.registrarUsuario(usuario);
			empresa.setUsuario(usuario);
			empresaDAO.actualizarEmpresa(empresa);
			Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
			Cargo cargoPresidente = new Cargo("PRESIDENTE", null, estatus,
					empresa);
			cargoDAO.registrarCargo(cargoPresidente);
			Cargo cargoDirector1 = new Cargo("DIRECTOR1", cargoPresidente,
					estatus, empresa);
			cargoDAO.registrarCargo(cargoDirector1);
			Cargo cargoDirector2 = new Cargo("DIRECTOR2", cargoPresidente,
					estatus, empresa);
			cargoDAO.registrarCargo(cargoDirector2);
			showNotifyInfo("Informacion almacenada Exitosamente",
					windowsRegistrarEmpresa);
			windowsRegistrarEmpresa.onClose();
			miSession.setAttribute("usuario", usuario);
			Executions.sendRedirect("indexRegistrado.zul");
		}
	}

	@Listen("onUpload = #btnCargarFotoEmpresa")
	public void processFotoEmpresa(UploadEvent event) {
		mediaEmpresa = event.getMedia();
		if (mediaEmpresa instanceof org.zkoss.image.Image) {
			imagenEmpresa.setContent((org.zkoss.image.Image) mediaEmpresa);

			imagenEmpresa.setVisible(true);
		} else {
			mediaEmpresa = null;
			Messagebox.show("Not an image: " + mediaEmpresa, "Error",
					Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onUpload = #btnCargarFotoAdministrador")
	public void processFotoAdministrador(UploadEvent event) {
		mediaAdministrador = event.getMedia();
		if (mediaAdministrador instanceof org.zkoss.image.Image) {
			imagenAdministrador
					.setContent((org.zkoss.image.Image) mediaAdministrador);
			imagenAdministradorRegistrar
					.setContent((org.zkoss.image.Image) mediaAdministrador);
			imagenAdministrador.setVisible(true);
		} else {
			mediaAdministrador = null;
			Messagebox.show("Not an image: " + mediaAdministrador, "Error",
					Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Listen("onClick=#btnCancelar")
	public void cancelarRegistro() {

		windowsRegistrarEmpresa.onClose();

	}

	@Listen("onChange = #lblRif")
	public void cambioRif() {
		String rif = lblRif.getValue().toString();
		if (rif.equalsIgnoreCase("")) {
			showNotify("Debe ingresar la cedula del empleado", lblCedula);
			lblCedula.focus();
		}
		Empresa empresaBD = empresaDAO.obtenerEmpresa(rif);
		if ((empresaBD != null)) {
			showNotify("Empresa Registrada en Base de Datos", lblRif);
			lblRif.focus();
		}
	}

	@Listen("onChange = #lblCedula")
	public void cambioCedula() {
		String cedula = lblCedula.getValue().toString();
		if (cedula.equalsIgnoreCase("")) {
			showNotify("Debe ingresar la cedula del empleado", lblCedula);
			lblCedula.focus();
		}
		Usuario empleado = usuarioDAO.obtenerUsuario(cedula);
		if ((empleado != null)) {
			showNotify("Usuario Registrado en Base de Datos", lblCedula);
			lblCedula.focus();
		}
	}

	@Listen("onChange = #lblEmail")
	public void cambioEmail() {
		String email = lblEmail.getValue().toString();
		if (email.equalsIgnoreCase("")) {
			showNotify("Debe ingresar la cedula del empleado", lblCedula);
			lblEmail.focus();
		}
		Usuario empleado = usuarioDAO.obtenerUsuarioPorEmail(email);
		if ((empleado != null)) {
			showNotify("Email de usuario Registrado en Base de Datos",
					lblCedula);
			lblEmail.focus();
		}
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}
}