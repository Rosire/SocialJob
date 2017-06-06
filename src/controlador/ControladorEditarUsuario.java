package controlador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.EstatusDAO;
import modelo.dao.TipoUsuarioDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Estatus;
import modelo.dto.TipoUsuario;
import modelo.dto.Usuario;
import modelo.util.Util;

import org.zkoss.bind.BindUtils;
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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorEditarUsuario extends SelectorComposer<Component> {

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
	private Longbox lblTelefono;
	@Wire
	private Intbox lblCedula;
	@Wire
	private Textbox lblFoto;
	@Wire
	private Window windowsEditarUsuario;
	@Wire
	private Div divConsulta;
	@Wire
	private Div divModificar;
	@Wire
	private Label labelCedula;
	@Wire
	private Label labelContrasena;
	@Wire
	private Listbox listaCargos;
	@Wire
	private Button btnCargarFoto;
	@Wire
	private Radiogroup grupoSexo;
	@Wire
	private Div divCargos;
	@Wire
	private Label labelId;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private boolean nuevaFoto = false;
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();
	private TipoUsuarioDAO tipoUsuarioDAO = new TipoUsuarioDAO();

	public ControladorEditarUsuario() {
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
							windowsEditarUsuario.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onClick=#btnGuardarModificacion; onOK=#btnGuardarModificacion")
	public void modificarUsuario() {
		int id = Integer.parseInt(labelId.getValue().toString());
		if (id == usuarioSession.getId()) {
			modificarUsuarioAdministrador();
		} else {
			modificarUsuarioEmpleado();
		}

	}

	public void modificarUsuarioEmpleado() {
		int id = Integer.parseInt(labelId.getValue().toString());
		String cedula = lblCedula.getValue().toString();
		String nombres = lblNombres.getValue();
		String apellidos = lblApellidos.getValue();
		String email = lblEmail.getValue();
		String direccion = lblDireccion.getValue();
		String telefono = lblTelefono.getValue().toString();
		String sexo = grupoSexo.getSelectedItem().getValue();
		String foto;
		TipoUsuario tipoUsuario;
		Estatus estatus;
		Usuario usuario;
		Usuario usuarioAnterior = usuarioDAO.obtenerUsuario(id);

		ListModelList<Object> cargosSeleccionados = (ListModelList<Object>) listaCargos
				.getModel();
		Set<Object> conjuntoCargos = cargosSeleccionados.getSelection();
		if (nuevaFoto) {
			if (media == null) {
				showNotify("Debe ingresar la foto del usuario", btnCargarFoto);
				btnCargarFoto.focus();
				return;
			} else {
				Util.borrar(usuarioAnterior.getFoto());
				if (!Util.uploadFile(media, cedula)) {
					showNotify("Error al Subir Foto del Empleado",
							imagenEmpleado);
					btnCargarFoto.focus();
					return;
				} else {
					String typeFoto = media.getContentType();
					String extension = typeFoto
							.substring(typeFoto.indexOf("/") + 1);
					String fileName = cedula + "." + extension;
					foto = "/uploads/" + fileName;
				}
			}
		} else {
			foto = usuarioDAO.obtenerUsuario(labelCedula.getValue()).getFoto();
		}

		if (conjuntoCargos.size() == 0) {
			showNotify("Debe Seleccionar al menos un cargo del empleado",
					listaCargos);
			listaCargos.focus();
			return;
		}

		estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
		tipoUsuario = tipoUsuarioDAO.obtenerTipoUsuarioPorNombre("EMPLEADO");
		usuario = new Usuario(cedula, usuarioAnterior.getContrasenna(),
				estatus, tipoUsuario, usuarioSession.getEmpresa(), nombres,
				apellidos, email, direccion, sexo, telefono, foto);
		if (usuario != null) {
			usuario.setId(usuarioAnterior.getId());
			usuarioDAO.actualizarUsuario(usuario);

			List<Object> list = new ArrayList<Object>(conjuntoCargos);

			List<CargoEmpleado> cargosEmpleadoViejo = cargoEmpleadoDAO
					.obtenerCargosPorEmpleado(id);

			boolean encontrado = false;

			for (int i = 0; i < list.size(); i++) {
				Cargo cargo = (Cargo) list.get(i);
				for (int j = 0; j < cargosEmpleadoViejo.size(); j++) {
					if (cargosEmpleadoViejo.get(j).getCargo().getId() == cargo
							.getId()) {
						encontrado = true;
						break;
					}
				}
				if (!encontrado) {
					CargoEmpleado cargoEmpleado = new CargoEmpleado(usuario,
							cargo, new Date());
					cargoEmpleadoDAO.registrarCargoEmpleado(cargoEmpleado);
				}
				encontrado = false;
			}

			for (int i = 0; i < cargosEmpleadoViejo.size(); i++) {
				CargoEmpleado cargoEmpleado = cargosEmpleadoViejo.get(i);
				for (int j = 0; j < list.size(); j++) {
					Cargo cargo = (Cargo) list.get(j);
					if (cargoEmpleado.getCargo().getId() == cargo.getId()) {
						encontrado = true;
						break;
					}
				}
				if (!encontrado) {
					cargoEmpleadoDAO.eliminarCargosEmpleado(cargoEmpleado);
				}
				encontrado = false;
			}

			BindUtils.postGlobalCommand(null, null, "actualizarTablaActivos",
					null);
			showNotifyInfo("Usuario Modificado Exitosamente",
					windowsEditarUsuario);
			BindUtils.postGlobalCommand(null, null, "actualizarTabla", null);
			windowsEditarUsuario.onClose();
		} else {
			showNotify("Error, al modificar el usuario", windowsEditarUsuario);
		}

	}

	public void modificarUsuarioAdministrador() {
		int id = Integer.parseInt(labelId.getValue().toString());
		String cedula = lblCedula.getValue().toString();
		String nombres = lblNombres.getValue();
		String apellidos = lblApellidos.getValue();
		String email = lblEmail.getValue();
		String direccion = lblDireccion.getValue();
		String telefono = lblTelefono.getValue().toString();
		String sexo = grupoSexo.getSelectedItem().getValue();
		String foto;
		TipoUsuario tipoUsuario;
		Estatus estatus;
		Usuario usuario;
		Usuario usuarioAnterior = usuarioDAO.obtenerUsuario(id);
		ListModelList<Object> cargosSeleccionados = (ListModelList<Object>) listaCargos
				.getModel();
		Set<Object> conjuntoCargos = cargosSeleccionados.getSelection();
		if (nuevaFoto) {
			if (media == null) {
				showNotify("Debe ingresar la foto del usuario", btnCargarFoto);
				btnCargarFoto.focus();
				return;
			} else {
				Util.borrar(usuarioAnterior.getFoto());
				if (!Util.uploadFile(media, cedula)) {
					showNotify("Error al Subir Foto del Empleado",
							imagenEmpleado);
					btnCargarFoto.focus();
					return;
				} else {
					String typeFoto = media.getContentType();
					String extension = typeFoto
							.substring(typeFoto.indexOf("/") + 1);
					String fileName = cedula + "." + extension;
					foto = "/uploads/" + fileName;
				}
			}
		} else {
			foto = usuarioAnterior.getFoto();
		}
		estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
		if (conjuntoCargos.size() != 0) {
			tipoUsuario = tipoUsuarioDAO
					.obtenerTipoUsuarioPorNombre("ADMINISTRADOR/EMPLEADO");
			usuario = new Usuario(cedula, labelCedula.getValue(), estatus,
					tipoUsuario, usuarioSession.getEmpresa(), nombres,
					apellidos, email, direccion, sexo, telefono, foto);
			if (usuario != null) {
				usuario.setId(id);
				List<Object> list = new ArrayList<Object>(conjuntoCargos);

				List<CargoEmpleado> cargosEmpleadoViejo = cargoEmpleadoDAO
						.obtenerCargosPorEmpleado(id);

				boolean encontrado = false;

				for (int i = 0; i < list.size(); i++) {
					Cargo cargo = (Cargo) list.get(i);
					for (int j = 0; j < cargosEmpleadoViejo.size(); j++) {
						if (cargosEmpleadoViejo.get(j).getCargo().getId() == cargo
								.getId()) {
							encontrado = true;
							break;
						}
					}
					if (!encontrado) {
						CargoEmpleado cargoEmpleado = new CargoEmpleado(
								usuario, cargo, new Date());
						cargoEmpleadoDAO.registrarCargoEmpleado(cargoEmpleado);
					}
					encontrado = false;
				}

				for (int i = 0; i < cargosEmpleadoViejo.size(); i++) {
					CargoEmpleado cargoEmpleado = cargosEmpleadoViejo.get(i);
					for (int j = 0; j < list.size(); j++) {
						Cargo cargo = (Cargo) list.get(j);
						if (cargoEmpleado.getCargo().getId() == cargo.getId()) {
							encontrado = true;
							break;
						}
					}
					if (!encontrado) {
						cargoEmpleadoDAO.eliminarCargosEmpleado(cargoEmpleado);
					}
					encontrado = false;
				}
			}

		} else {
			tipoUsuario = tipoUsuarioDAO
					.obtenerTipoUsuarioPorNombre("ADMINISTRADOR");
			usuario = new Usuario(cedula, labelCedula.getValue(), estatus,
					tipoUsuario, usuarioSession.getEmpresa(), nombres,
					apellidos, email, direccion, sexo, telefono, foto);
			usuario.setId(id);
			List<CargoEmpleado> cargosEmpleadoViejo = cargoEmpleadoDAO
					.obtenerCargosPorEmpleado(id);
			for (int i = 0; i < cargosEmpleadoViejo.size(); i++) {
				cargoEmpleadoDAO.eliminarCargosEmpleado(cargosEmpleadoViejo
						.get(i));
			}
		}
		if (usuario != null) {
			usuarioDAO.actualizarUsuario(usuario);
			BindUtils.postGlobalCommand(null, null, "actualizarTablaActivos",
					null);
			showNotifyInfo("Usuario Modificado Exitosamente",
					windowsEditarUsuario);
			Session miSession = Sessions.getCurrent();
			miSession.setAttribute("usuario", usuario);
			BindUtils.postGlobalCommand(null, null,
					"actualizarInformacionUsuario", null);
			BindUtils.postGlobalCommand(null, null, "actualizarUsuario", null);
			BindUtils.postGlobalCommand(null, null, "actualizarTabla", null);
			windowsEditarUsuario.onClose();
			Executions.sendRedirect("indexRegistrado.zul");
		} else {
			showNotify("Error, al modificar el usuario", windowsEditarUsuario);
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
