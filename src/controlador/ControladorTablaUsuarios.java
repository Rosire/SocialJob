package controlador;

import java.awt.Button;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.CargoDAO;
import modelo.dao.EstatusDAO;
import modelo.dao.TipoUsuarioDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.Cargo;
import modelo.dto.Estatus;
import modelo.dto.TipoUsuario;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

public class ControladorTablaUsuarios extends SelectorComposer<Component> {

	@Wire
	private Window windowsConsultarUsuario;
	@Wire
	private Label labelCedula;
	@Wire
	private Grid gridUsuariosActivos;
	@Wire
	private Grid gridUsuariosInactivos;
	@Wire
	private Button btnEditarUsuario;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private CargoDAO cargoDAO = new CargoDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();
	private TipoUsuarioDAO tipoUsuarioDAO = new TipoUsuarioDAO();

	private List<Usuario> usuariosActivos;
	private List<Usuario> usuariosInactivos;

	private List<Cargo> cargos;

	private List<Cargo> cargosEmpleado;

	private Usuario usuarioSeleccionado = new Usuario();
	private boolean visibleCargos;
	private boolean visibleEditar;

	private ListModel<Usuario> listaUsuariosActivosModel;
	private ListModel<Usuario> listaUsuariosInactivosModel;

	private ListModel<Cargo> listaCargosModel;

	private ListModel<Cargo> listaCargosEmpleadoModel;

	private static final String footerMessageUsuariosActivos = "Total de Usuarios Activos: %d";
	private static final String footerMessageUsuariosInactivos = "Total de Usuarios Inactivos: %d";

	public ControladorTablaUsuarios() {
		super();
		cargosEmpleado = cargoDAO.obtenerCargosPorEmpleado(usuarioSeleccionado
				.getId());
		cargos = cargoDAO.obtenerCargosActivosPorEmpresa(usuarioSession
				.getEmpresa().getId());
		usuariosActivos = usuarioDAO
				.obtenerUsuariosActivosPorEmpresa(usuarioSession.getEmpresa()
						.getId());
		usuariosInactivos = usuarioDAO
				.obtenerUsuariosInactivosPorEmpresa(usuarioSession.getEmpresa()
						.getId());

		visibleCargos = false;
	}

	public Usuario getUsuarioSeleccionado() {
		return usuarioSeleccionado;
	}

	public boolean getVisibleCargos() {
		return visibleCargos;
	}

	public boolean getVisibleEditar() {
		return visibleEditar;
	}

	public ListModel<Usuario> getUsuariosActivos() {

		listaUsuariosActivosModel = new ListModelList<Usuario>(usuariosActivos);
		return listaUsuariosActivosModel;
	}

	public ListModel<Usuario> getUsuariosInactivos() {

		listaUsuariosInactivosModel = new ListModelList<Usuario>(
				usuariosInactivos);
		return listaUsuariosInactivosModel;
	}

	public ListModel<Cargo> getCargosEmpleado() {
		listaCargosEmpleadoModel = new ListModelList<Cargo>(cargosEmpleado);
		return listaCargosEmpleadoModel;
	}

	public String getFooterMessageUsuariosActivos() {
		return String.format(footerMessageUsuariosActivos,
				usuariosActivos.size());
	}

	public String getFooterMessageUsuariosInactivos() {
		return String.format(footerMessageUsuariosInactivos,
				usuariosInactivos.size());
	}

	// Incluir un Nuevo empleado
	@Listen("onClick=#btnIncluirUsuario")
	public void incluirCargo() {
		Window window = (Window) Executions.createComponents(
				"/vista/registrarUsuario.zul", null, null);
		window.doModal();
	}

	// Consultar un Empleado
	@Listen("onClick=#btnConsultarUsuarioActivo ")
	public void consultarUsuarioActivo() {
		String indexSeleccionado = getIdSelectedRowEmpleado(gridUsuariosActivos);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar un usuario a Consultar",
					gridUsuariosActivos);
		} else {
			windowsConsultarUsuario.setVisible(true);
		}
	}

	@Listen("onClick=#btnConsultarUsuarioInactivo ")
	public void consultarUsuarioInactivo() {
		String indexSeleccionado = getIdSelectedRowEmpleado(gridUsuariosInactivos);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar un usuario a Consultar",
					gridUsuariosInactivos);
		} else {
			windowsConsultarUsuario.setVisible(true);
		}
	}

	// btnEliminarEmpleado
	@Listen("onClick=#btnEliminarUsuario")
	public void deshabilitarUsuario() {
		String indexSeleccionado = getIdSelectedRowEmpleado(gridUsuariosActivos);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar el usuario que desea Deshabilitar",
					gridUsuariosActivos);
		} else {
			final Usuario empleado = usuarioDAO
					.obtenerUsuario(indexSeleccionado);

			Messagebox.show("¿Seguro que desea dejar INACTIVO al usuario (V-"
					+ empleado.getCedula() + ") Seleccionado?",
					"Inhabilitar Usuario", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						public void onEvent(Event e) {
							if (Messagebox.ON_OK.equals(e.getName())) {
								// OK is clicked
								Estatus estatus = estatusDAO
										.obtenerEstatusPorNombre("INACTIVO");
								empleado.setEstatusUsuario(estatus);
								usuarioDAO.actualizarUsuario(empleado);

								BindUtils.postGlobalCommand(null, null,
										"actualizarTablaActivos", null);
								BindUtils.postGlobalCommand(null, null,
										"actualizarTablaInactivos", null);
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								// Cancel is clicked
							}
						}
					});
		}
	}

	// btnEliminarEmpleado
	@Listen("onClick=#btnHabilitar")
	public void habilitarUsuario() {
		String indexSeleccionado = getIdSelectedRowEmpleado(gridUsuariosInactivos);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar el usuario que desea habilitar",
					gridUsuariosInactivos);
		} else {
			final Usuario empleado = usuarioDAO
					.obtenerUsuario(indexSeleccionado);

			Messagebox.show("¿Seguro que desea Habilitar al usuario (V-"
					+ empleado.getCedula() + ") Seleccionado?",
					"Habilitar Usuario", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						public void onEvent(Event e) {
							if (Messagebox.ON_OK.equals(e.getName())) {
								// OK is clicked
								Estatus estatus = estatusDAO
										.obtenerEstatusPorNombre("ACTIVO");
								empleado.setEstatusUsuario(estatus);
								usuarioDAO.actualizarUsuario(empleado);

								BindUtils.postGlobalCommand(null, null,
										"actualizarTablaActivos", null);
								BindUtils.postGlobalCommand(null, null,
										"actualizarTablaInactivos", null);
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								// Cancel is clicked
							}
						}
					});
		}
	}

	@Listen("onClick=#btnModificarUsuario")
	public void modificarEmpleadoSeleccionado() {
		String indexSeleccionado = getIdSelectedRowEmpleado(gridUsuariosActivos);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar un cargo a Consultar",
					gridUsuariosActivos);
		} else {
			modificarEmpleado(indexSeleccionado);
		}
	}

	@Listen("onClick=#btnEditarUsuario")
	public void modificarEmpleadoConsulta() {
		windowsConsultarUsuario.setVisible(false);
		modificarEmpleado(labelCedula.getValue());
	}

	public void modificarEmpleado(String cedula) {
		Usuario usuario = usuarioDAO.obtenerUsuario(cedula);
		Map<String, Object> arguments = new HashMap<String, Object>();
		arguments.put("usuarioSeleccionado", usuario);
		if (usuario.getSexo().equals("FEMENINO")) {
			arguments.put("femenino", true);
		} else {
			arguments.put("masculino", true);
		}
		cargos = cargoDAO
				.obtenerCargosActivosDisponiblesPorEmpresa(usuarioSession
						.getEmpresa().getId());

		cargosEmpleado = cargoDAO.obtenerCargosPorEmpleado(usuario.getId());
		boolean encontro = false;
		for (int i = 0; i < cargosEmpleado.size(); i++) {
			Cargo cargoEmpleado = cargosEmpleado.get(i);
			for (int j = 0; j < cargos.size(); j++) {
				if (cargoEmpleado.getId() == cargos.get(j).getId()) {
					encontro = true;

				}
			}
			if (!encontro) {
				cargos.add(cargoEmpleado);
			}
			encontro = false;
		}
		listaCargosModel = new ListModelList<Cargo>(cargos);
		((ListModelList<Cargo>) listaCargosModel).setMultiple(true);
		for (int i = 0; i < cargos.size(); i++) {
			int idCargo = cargos.get(i).getId();
			for (int j = 0; j < cargosEmpleado.size(); j++) {
				int idCargoEmpleado = cargosEmpleado.get(j).getId();
				if (idCargoEmpleado == idCargo) {

					((AbstractListModel<Cargo>) listaCargosModel)
							.addToSelection(((List<Cargo>) listaCargosModel)
									.get(i));
				}
			}

		}

		if (usuario.getTipoUsuario().getNombre().equals("EMPLEADO")) {
			arguments.put("divCargos", true);
		} else {
			arguments.put("divCargos", false);
		}
		arguments.put("cargos", listaCargosModel);
		windowsConsultarUsuario.setVisible(false);
		Window window = (Window) Executions.createComponents(
				"/vista/editarUsuario.zul", null, arguments);
		window.doModal();
	}

	@Listen("onClick=#btnSalirConsulta")
	public void salirConsulta() {
		windowsConsultarUsuario.setVisible(false);
	}

	private String getIdSelectedRowEmpleado(Grid grid) {
		for (Component component : grid.getRows().getChildren()) {
			if (component instanceof Row) {
				Row row = (Row) component;
				for (Component component2 : row.getChildren()) {
					if (component2 instanceof Radio) {
						Radio radio = (Radio) component2;

						if (radio.isSelected()) {
							return radio.getValue();
						}
					}
				}
			}
		}
		return null;
	}

	// Actualiza Cargo Seleccionado
	@GlobalCommand
	@NotifyChange({ "usuarioSeleccionado", "cargosEmpleado", "visibleCargos",
			"visibleEditar" })
	public void actualizarUsuarioSeleccionado(@BindingParam("data") Object data) {
		Radio radio = (Radio) data;
		usuarioSeleccionado = usuarioDAO.obtenerUsuario(radio.getValue()
				.toString());
		cargosEmpleado = cargoDAO.obtenerCargosPorEmpleado(usuarioSeleccionado
				.getId());
		if (usuarioSeleccionado.getTipoUsuario().getNombre()
				.equals("ADMINISTRADOR")) {
			visibleCargos = false;

		} else {
			visibleCargos = true;

		}
		if (usuarioSeleccionado.getEstatusUsuario().getNombre()
				.equals("ACTIVO")) {
			visibleEditar = true;
		} else {
			visibleEditar = false;
		}
	}

	// Actualiza la Tabla de Usuarios al realizar algun cambio
	@GlobalCommand
	@NotifyChange({ "usuariosActivos", "footerMessageUsuariosActivos" })
	public void actualizarTablaActivos() {
		usuariosActivos = usuarioDAO
				.obtenerUsuariosActivosPorEmpresa(usuarioSession.getEmpresa()
						.getId());
	}

	// Actualiza la Tabla de Usuarios Inactivos al realizar algun cambio
	@GlobalCommand
	@NotifyChange({ "usuariosInactivos", "footerMessageUsuariosInactivos" })
	public void actualizarTablaInactivos() {
		usuariosInactivos = usuarioDAO
				.obtenerUsuariosInactivosPorEmpresa(usuarioSession.getEmpresa()
						.getId());
	}

	@GlobalCommand
	@NotifyChange({ "usuariosInactivos", "footerMessageUsuariosInactivos",
			"usuariosActivos", "footerMessageUsuariosActivos" })
	public void actualizarTablaEmpleados(@BindingParam("data") int data) {

		usuariosActivos = usuarioDAO.obtenerUsuariosActivosPorCargo(data);
		usuariosInactivos = usuarioDAO.obtenerUsuariosInactivosPorCargo(data);
	}

	@GlobalCommand
	@NotifyChange({ "usuariosInactivos", "footerMessageUsuariosInactivos",
			"usuariosActivos", "footerMessageUsuariosActivos" })
	public void cargarTablaEmpleados() {
		usuariosActivos = usuarioDAO
				.obtenerUsuariosEmpleadoActivosPorEmpresa(usuarioSession
						.getEmpresa().getId());
		usuariosInactivos = usuarioDAO
				.obtenerUsuariosEmpleadoInactivosPorEmpresa(usuarioSession
						.getEmpresa().getId());
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
