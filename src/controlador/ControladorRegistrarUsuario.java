package controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import modelo.dao.CargoDAO;
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
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ControladorRegistrarUsuario extends SelectorComposer<Component> {

	// wire components
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
	private Image imagenEmpleado;
	@Wire
	private Listbox listaCargos;
	@Wire
	private Button btnCargarFoto;
	@Wire("#windowsRegistrarUsuario")
	private Window windowsRegistrarUsuario;
	@Wire
	private Radiogroup grupoSexo;
	@Wire
	private Div divCargos;
	@Wire
	private Label labelSexo;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private ListModel<Cargo> listaCargosModel;

	private CargoDAO cargoDAO = new CargoDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();
	private TipoUsuarioDAO tipoUsuarioDAO = new TipoUsuarioDAO();

	private List<Cargo> cargos;
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	public ControladorRegistrarUsuario() {
		super();
		cargos = cargoDAO
				.obtenerCargosActivosDisponiblesPorEmpresa(usuarioSession
						.getEmpresa().getId());
		listaCargosModel = new ListModelList<Cargo>(cargos);
		((ListModelList<Cargo>) listaCargosModel).setMultiple(true);
	}

	public ListModel<Cargo> getCargos() {
		return listaCargosModel;
	}

	@Listen("onClick=#btnRegistrar; onOK=#Registar")
	public void registrarEmpleado() {
		String cedula = lblCedula.getValue().toString();
		String nombres = lblNombres.getValue();
		String apellidos = lblApellidos.getValue();
		String email = lblEmail.getValue();
		String direccion = lblDireccion.getValue();
		String telefono = lblTelefono.getValue().toString();
		ListModelList<Object> cargosSeleccionados = (ListModelList<Object>) listaCargos
				.getModel();
		Set<Object> conjuntoCargos = cargosSeleccionados.getSelection();
		if (media == null) {
			showNotify("Debe ingresar La foto del Usuario", btnCargarFoto);
			btnCargarFoto.focus();
		} else if (!Util.uploadFile(media, cedula)) {
			showNotify("Error al Subir Foto del Usuario", imagenEmpleado);
			btnCargarFoto.focus();
		} else if (grupoSexo.getSelectedIndex() == -1) {
			showNotify("Debe Seleccionar el sexo del Usuario", labelSexo);
		}
		if (conjuntoCargos.size() == 0) {
			showNotify("Debe Seleccionar al menos un cargo del empleado",
					listaCargos);
			listaCargos.focus();
		} else {
			String sexo = grupoSexo.getSelectedItem().getValue();
			String typeFoto = media.getContentType();
			String extension = typeFoto.substring(typeFoto.indexOf("/") + 1);
			String fileName = cedula + "." + extension;
			String foto = "/uploads/" + fileName;
			TipoUsuario tipoUsuario = tipoUsuarioDAO
					.obtenerTipoUsuarioPorNombre("EMPLEADO");
			Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
			Usuario usuario = new Usuario(cedula, cedula, estatus, tipoUsuario,
					usuarioSession.getEmpresa(), nombres, apellidos, email,
					direccion, sexo, telefono, foto);
			if (usuario != null) {
				usuarioDAO.registrarUsuario(usuario);
				List<Object> list = new ArrayList<Object>(conjuntoCargos);

				List<CargoEmpleado> cargosEmpleado = new ArrayList<CargoEmpleado>();

				for (int i = 0; i < list.size(); i++) {
					Cargo cargo = (Cargo) list.get(i);
					CargoEmpleado cargoEmpleado = new CargoEmpleado(usuario,
							cargo, new Date());
					cargoEmpleadoDAO.registrarCargoEmpleado(cargoEmpleado);
					cargosEmpleado.add(cargoEmpleado);
				}

				BindUtils.postGlobalCommand(null, null,
						"actualizarTablaActivos", null);
				BindUtils.postGlobalCommand(null, null, "actualizarTabla", null);
				showNotifyInfo("Usuario Registrado Exitosamente",
						windowsRegistrarUsuario);
				windowsRegistrarUsuario.onClose();
			} else {
				showNotify("Error, al registrar el usuario",
						windowsRegistrarUsuario);
			}
		}
	}

	@Listen("onUpload = #btnCargarFoto")
	public void processFoto(UploadEvent event) {
		media = event.getMedia();
		if (media instanceof org.zkoss.image.Image) {
			imagenEmpleado.setContent((org.zkoss.image.Image) media);
			imagenEmpleado.setVisible(true);
		} else {
			Messagebox.show("Not an image: " + media, "Error", Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Listen("onClick=#btnCancelar")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Cancelar Registro",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowsRegistrarUsuario.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
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
			showNotify("Empleado Registrado en Base de Datos", lblCedula);
			lblCedula.focus();
		}
	}

	@Listen("onChange = #lblEmail")
	public void cambioEmail() {
		String email = lblEmail.getValue().toString();
		if (email.equalsIgnoreCase("")) {
			showNotify("Debe ingresar la cedula del empleado", lblCedula);
			lblCedula.focus();
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