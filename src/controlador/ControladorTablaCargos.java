package controlador;

import modelo.dao.CargoDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.EstatusDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Estatus;
import modelo.dto.Usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.TreeModel;
import org.zkoss.zul.TreeNode;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import arbol.demo.data.CargoList;
import arbol.demo.data.pojo.CargoArbol;

public class ControladorTablaCargos extends SelectorComposer<Component> {

	@Wire
	private Grid gridCargos;
	@Wire
	private Div divArbol;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private ListModel<Cargo> listaCargosModel;
	private List<Cargo> cargos;

	private ListModel<Cargo> listaCargosCambioModel;
	private List<Cargo> cargosCambio;

	private ListModel<Cargo> listaCargosSubalternosModel;
	private List<Cargo> cargosSubalternos;

	private Cargo cargoSeleccionado;
	private CargoDAO cargoDAO = new CargoDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	private TreeModel<TreeNode<CargoArbol>> cargosModel;

	private static final String footerMessageCargos = "Total de Cargos: %d";

	public ControladorTablaCargos() {
		super();
		BindUtils.postGlobalCommand(null, null, "cargarTablaEmpleados", null);
	}

	public ListModel<Cargo> getCargos() {
		cargos = cargoDAO.obtenerCargosActivosPorEmpresa(usuarioSession
				.getEmpresa().getId());
		listaCargosModel = new ListModelList<Cargo>(cargos);
		((ListModelList<Cargo>) listaCargosModel).setMultiple(false);
		return listaCargosModel;
	}

	public String getFooterMessageCargos() {
		return String.format(footerMessageCargos, cargos.size());
	}

	public TreeModel<TreeNode<CargoArbol>> getModeloArbol() {

		cargosModel = new DefaultTreeModel<CargoArbol>(
				new CargoList().getRoot());
		((DefaultTreeModel<CargoArbol>) cargosModel)
				.addOpenPath(new int[] { 0 });
		return cargosModel;

	}

	// Incluir un Nuevo cargo
	@Listen("onClick=#btnIncluirCargo")
	public void incluirCargo() {
		Window window = (Window) Executions.createComponents(
				"/vista/registrarCargo.zul", null, null);
		window.doModal();
	}

	// Consultar un Cargo
	@Listen("onClick=#btnConsultarCargo")
	public void consultarCargo() {
		String indexSeleccionado = getIdSelectedRowCargo();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar un cargo a Consultar", gridCargos);
		} else {
			int idCargo = Integer.parseInt(indexSeleccionado);
			cargoSeleccionado = cargoDAO.obtenerCargo(idCargo);
			Map<String, Object> arguments = new HashMap<String, Object>();
			String titulo = "Consultar Cargo";
			CargoEmpleado supervisor = cargoEmpleadoDAO
					.obtenerUsuarioSupervisorPorCargo(cargoSeleccionado
							.getCargoSuperior().getId());
			String nombreSupervisor = "";
			if (supervisor != null) {
				nombreSupervisor = supervisor.getEmpleado().getNombres() + " "
						+ supervisor.getEmpleado().getApellidos() + "(V-"
						+ supervisor.getEmpleado().getCedula() + ")";
			}

			List<Cargo> cargosSuperior = cargoDAO
					.obtenerCargosActivosDistintoPorEmpresa(idCargo,
							usuarioSession.getEmpresa().getId());
			ListModel<Cargo> listaCargosModel = new ListModelList<Cargo>(
					cargosSuperior);

			cargosSubalternos = cargoDAO.obtenerCargosSubalternos(idCargo);
			listaCargosSubalternosModel = new ListModelList<Cargo>(
					cargosSubalternos);

			if (cargoSeleccionado.getCargoSuperior() != null) {

				for (int i = 0; i < cargosSuperior.size(); i++) {
					if (cargosSuperior.get(i).getId() == cargoSeleccionado
							.getCargoSuperior().getId()) {
						((AbstractListModel<Cargo>) listaCargosModel)
								.addToSelection(((List<Cargo>) listaCargosModel)
										.get(i));
					}
				}
			}
			arguments.put("subalternos", listaCargosSubalternosModel);
			arguments.put("titulo", titulo);
			arguments.put("icono", "z-icon-eye");
			arguments.put("cargoSeleccionado", cargoSeleccionado);
			arguments.put("divConsulta", true);
			arguments.put("btnEditar", true);
			arguments.put("cargos", listaCargosModel);
			arguments.put("nombreSupervisor", nombreSupervisor);
			Window window = (Window) Executions.createComponents(
					"/vista/ConsultarEditarCargo.zul", null, arguments);
			window.doModal();
		}
	}

	// Mostrar Ventana de Editar Cargo desde la Tabla de Cargos
	@Listen("onClick=#btnModificarCargo")
	public void editarCargo() {
		String indexSeleccionado = getIdSelectedRowCargo();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar un cargo a Consultar", gridCargos);
		} else {
			int idCargo = Integer.parseInt(indexSeleccionado);
			cargoSeleccionado = cargoDAO.obtenerCargo(idCargo);
			Cargo cargoMatriz = cargoDAO
					.obtenerCargosMatrizPorEmpresa(usuarioSession.getEmpresa()
							.getId());
			Map<String, Object> arguments = new HashMap<String, Object>();
			String titulo = "Editar Cargo";
			if (cargoMatriz.getId() == cargoSeleccionado.getId()) {

			} else {
				List<Cargo> cargosSuperior = cargoDAO
						.obtenerCargosActivosDistintoPorEmpresa(idCargo,
								usuarioSession.getEmpresa().getId());
				ListModel<Cargo> listaCargosModel = new ListModelList<Cargo>(
						cargosSuperior);

				if (cargoSeleccionado.getCargoSuperior() != null) {

					for (int i = 0; i < cargosSuperior.size(); i++) {
						if (cargosSuperior.get(i).getId() == cargoSeleccionado
								.getCargoSuperior().getId()) {
							((AbstractListModel<Cargo>) listaCargosModel)
									.addToSelection(((List<Cargo>) listaCargosModel)
											.get(i));
						}
					}
				}

				arguments.put("cargos", listaCargosModel);
				arguments.put("divCargoSuperior", true);

			}

			arguments.put("titulo", titulo);
			arguments.put("icono", "z-icon-edit");
			arguments.put("cargoSeleccionado", cargoSeleccionado);
			arguments.put("divConsulta", false);
			arguments.put("divEditar", true);

			Window window = (Window) Executions.createComponents(
					"/vista/ConsultarEditarCargo.zul", null, arguments);
			window.doModal();
			Map args = new HashMap();
			args.put("codigo", cargoSeleccionado.getId());
			BindUtils.postGlobalCommand(null, null, "cambioFiltroModificar",
					args);
		}
	}

	// Eliminar Cargo Seleccionado
	@Listen("onClick=#btnEliminarCargo")
	public void eliminarCargo() {
		String indexSeleccionado = getIdSelectedRowCargo();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar un cargo a Eliminar", gridCargos);
		} else {
			int idCargo = Integer.parseInt(indexSeleccionado);
			Cargo cargo = cargoDAO.obtenerCargo(idCargo);
			cargosSubalternos = cargoDAO.obtenerCargosSubalternos(idCargo);
			if (cargosSubalternos.size() == 0) {
				Messagebox.show(
						"¿Seguro que desea Eliminar el Cargo Seleccionado?",
						"Question", Messagebox.OK | Messagebox.CANCEL,
						Messagebox.QUESTION, new EventListener<Event>() {
							public void onEvent(Event e) {
								if (Messagebox.ON_OK.equals(e.getName())) {
									// OK is clicked
									Estatus estatus = estatusDAO
											.obtenerEstatusPorNombre("INACTIVO");
									cargo.setEstatusCargo(estatus);
									cargoDAO.actualizarCargo(cargo);
									showNotifyInfo(
											"Cargo Eliminado Exitosamente",
											gridCargos);
									BindUtils.postGlobalCommand(null, null,
											"actualizarTabla", null);
									BindUtils.postGlobalCommand(null, null,
											"cambioArbol", null);
								} else if (Messagebox.ON_CANCEL.equals(e
										.getName())) {
									// Cancel is clicked
								}
							}
						});
			} else {
				showNotify("El Cargo Tiene Cargos Subalternos Activos",
						gridCargos);
			}

		}
	}

	@Listen("onClick=#navItemArbolJerarquico")
	public void mostrarArbol() {
		gridCargos.setVisible(false);
		divArbol.setVisible(true);
	}

	@Listen("onClick=#navItemTablaGeneral")
	public void mostrarTabla() {
		gridCargos.setVisible(true);
		divArbol.setVisible(false);
	}

	// Actualiza la Tabla de Cargos al realizar algun cambio
	@GlobalCommand
	@NotifyChange({ "cargos", "footerMessageCargos", "modeloArbol" })
	public void actualizarTabla() {
		cargos = cargoDAO
				.obtenerCargosActivosPorEmpresa(usuarioSession.getId());
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	private String getIdSelectedRowCargo() {
		for (Component component : gridCargos.getRows().getChildren()) {
			if (component instanceof Row) {
				Row row = (Row) component;
				for (Component component2 : row.getChildren()) {
					if (component2 instanceof Radio) {
						Radio radio = (Radio) component2;

						if (radio.isSelected()) {
							return radio.getValue().toString();
						}
					}
				}
			}
		}
		return null;
	}

	@Command
	public void detalleCargo(@BindingParam("id") int id) {
		Cargo cargoSeleccionado = cargoDAO.obtenerCargo(id);

		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Consultar Cargo";
		CargoEmpleado supervisor = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(cargoSeleccionado
						.getCargoSuperior().getId());
		String nombreSupervisor = "";
		if (supervisor != null) {
			nombreSupervisor = supervisor.getEmpleado().getNombres() + " "
					+ supervisor.getEmpleado().getApellidos() + "(V-"
					+ supervisor.getEmpleado().getCedula() + ")";
		}
		cargosSubalternos = cargoDAO.obtenerCargosSubalternos(id);
		listaCargosSubalternosModel = new ListModelList<Cargo>(
				cargosSubalternos);

		List<Cargo> cargosSuperior = cargoDAO
				.obtenerCargosActivosDistintoPorEmpresa(cargoSeleccionado
						.getId(), usuarioSession.getEmpresa().getId());
		ListModel<Cargo> listaCargosModel = new ListModelList<Cargo>(
				cargosSuperior);
		if (cargoSeleccionado.getCargoSuperior() != null) {

			for (int i = 0; i < cargosSuperior.size(); i++) {
				if (cargosSuperior.get(i).getId() == cargoSeleccionado
						.getCargoSuperior().getId()) {
					((AbstractListModel<Cargo>) listaCargosModel)
							.addToSelection(((List<Cargo>) listaCargosModel)
									.get(i));
				}
			}
		}
		arguments.put("subalternos", listaCargosSubalternosModel);
		arguments.put("nombreSupervisor", nombreSupervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("cargoSeleccionado", cargoSeleccionado);
		arguments.put("divConsulta", true);
		arguments.put("btnEditar", true);
		arguments.put("cargos", listaCargosModel);

		Window window = (Window) Executions.createComponents(
				"/vista/ConsultarEditarCargo.zul", null, arguments);
		window.doModal();

	}

	@Command
	public void cambiarCargo(@BindingParam("id") int id) {
		Cargo cargoSeleccionado = cargoDAO.obtenerCargo(id);
		List<Cargo> subalternos = cargoDAO.obtenerCargosSubalternos(id);
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Cambiar Cargo";
		if (subalternos.size() == 0) {
			List<CargoEmpleado> cargosEmpleados = cargoEmpleadoDAO
					.obtenerUsuariosEmpleadosActivosPorCargo(id);
			if (cargosEmpleados.size() > 1) {
				cargosCambio = cargoDAO.obtenerCargosSubalternosPorEmpresa(
						usuarioSession.getEmpresa().getId(), id);
			} else {
				cargosCambio = cargoDAO.obtenerCargosActivosDistintoPorEmpresa(
						id, usuarioSession.getId());
			}
		} else {
			cargosCambio = cargoDAO
					.obtenerCargosSupervisoresYSubAlternosConUnEmpleadoPorEmpresa(
							usuarioSession.getEmpresa().getId(), id);
		}
		listaCargosCambioModel = new ListModelList<Cargo>(cargosCambio);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-edit");
		arguments.put("cargoSeleccionado", cargoSeleccionado);

		arguments.put("cargos", listaCargosCambioModel);
		Window window = (Window) Executions.createComponents(
				"/vista/cambiarCargo.zul", null, arguments);
		window.doModal();
	}

	@Command
	public void agregarCargoSubalterno(@BindingParam("id") int id) {
		Window window = (Window) Executions.createComponents(
				"/vista/registrarCargo.zul", null, null);
		window.doModal();
		Map args = new HashMap();
		args.put("id", id);
		BindUtils.postGlobalCommand(null, null, "actualizarSeleccion", args);
	}

}
