package controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.CargoDAO;
import modelo.dao.EstatusDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.TareaDAO;
import modelo.dao.TareaRepetitivaAsignadaDAO;
import modelo.dao.TareaRepetitivaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.Cargo;
import modelo.dto.Estatus;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.Tarea;
import modelo.dto.TareaRepetitiva;
import modelo.dto.TareaRepetitivaAsignada;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ControladorTareaRepetitiva extends SelectorComposer<Component> {

	@Wire
	private Listbox listaTareasRepetitivas;
	@Wire
	private Grid gridTareasRepetitivas;
	@Wire
	Radiogroup grupoTareasRepetitivas;
	@Wire
	Window windowsConsultarTareaRepetitiva;
	@Wire
	private Combobox cmbCargos;
	@Wire
	private Combobox cmbTareas;
	@Wire
	private Intbox lblVecesRepetir;
	@Wire
	private Combobox cmbFrecuencias;
	@Wire
	private Textbox lblIndicaciones;
	@Wire
	private Datebox lblFechaInicio;
	@Wire
	private Textbox lblCodigo;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	private EstatusDAO estatusDAO = new EstatusDAO();
	private TareaRepetitivaDAO tareaRepetitivaDAO = new TareaRepetitivaDAO();
	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private TareaRepetitivaAsignadaDAO tareaRepetitivaAsignadaDAO = new TareaRepetitivaAsignadaDAO();

	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private List<TareaRepetitiva> tareasRepetitivas;
	private TareaRepetitiva tareaRepetitivaSeleccionada = new TareaRepetitiva();

	private ListModel<TareaRepetitiva> listaTareasRepetitivasModel;

	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

	private static final String footerMessageTareasRepetitivas = "Total de Tareas Repetitivas: %d";

	public ControladorTareaRepetitiva() {
		super();
		tareasRepetitivas = tareaRepetitivaDAO
				.obtenerTareasRepetitivasSubalternosActivas(usuarioFiltro
						.getId());
	}

	public ListModel<TareaRepetitiva> getTareasRepetitivas() {
		listaTareasRepetitivasModel = new ListModelList<TareaRepetitiva>(
				tareasRepetitivas);
		return listaTareasRepetitivasModel;

	}

	public String getFooterMessageTareasRepetitivas() {
		return String.format(footerMessageTareasRepetitivas,
				tareasRepetitivas.size());
	}

	@Listen("onClick=#btnIncluirTareaRepetitiva")
	public void iniciarRegistroTareaRepetitiva() {
		Window window = (Window) Executions.createComponents(
				"/vista/registrarTareaRepetitiva.zul", null, null);
		window.doModal();
	}

	@Listen("onClick=#btnModificarTareaRepetitiva")
	public void modificarTareaRepetitiva() {
		String indexSeleccionado = getIdSelectedRow();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Repetitiva a Consultar",
					gridTareasRepetitivas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			tareaRepetitivaSeleccionada = tareaRepetitivaDAO
					.obtenerTareaRepetitiva(id);
			modificarTareaRepetitivaSeleccionada(tareaRepetitivaSeleccionada);
		}
	}

	@Listen("onClick=#btnEliminarTareaRepetitiva")
	public void eliminarTareaRepetitiva() {
		String indexSeleccionado = getIdSelectedRow();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Repetitiva a Eliminar",
					gridTareasRepetitivas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);

			Messagebox
					.show("¿Seguro que desea Eliminar la Tarea Repetitiva Seleccionada?",
							"Eliminar", Messagebox.OK | Messagebox.CANCEL,
							Messagebox.QUESTION, new EventListener<Event>() {
								public void onEvent(Event e) {
									if (Messagebox.ON_OK.equals(e.getName())) {
										// OK is clicked
										eliminarTareaRepetitiva(id);
									} else if (Messagebox.ON_CANCEL.equals(e
											.getName())) {
										// Cancel is clicked
									}
								}
							});
		}
	}

	public void eliminarTareaRepetitiva(int codigo) {
		TareaRepetitiva tareaRepetitiva = tareaRepetitivaDAO
				.obtenerTareaRepetitiva(codigo);
		Estatus estatus = estatusDAO.obtenerEstatusPorNombre("INACTIVO");
		tareaRepetitiva.setEstatus(estatus);
		tareaRepetitivaDAO.actualizarTareaRepetitiva(tareaRepetitiva);
		EstatusTareaAsignada estatusTarea = estatusTareaAsignadaDAO
				.obtenerEstatusPorNombre("ELIMINADA");
		List<TareaRepetitivaAsignada> tareasRepetitivasAsignadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorTareaRepetitivaPorEstatus(
						codigo, "POR INICIAR");
		for (int i = 0; i < tareasRepetitivasAsignadas.size(); i++) {
			TareaRepetitivaAsignada tareaRepetitivaAsignada = tareasRepetitivasAsignadas
					.get(i);
			tareaRepetitivaAsignada.setEstatusTareaAsignada(estatusTarea);
			tareaRepetitivaAsignadaDAO
					.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignada);
		}
		BindUtils.postGlobalCommand(null, null, "actualizarTareasRepetitivas",
				null);
	}

	@Listen("onClick=#btnConsultarTareaRepetitiva")
	public void consultarTareaRepetitiva() {
		String indexSeleccionado = getIdSelectedRow();
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Repetitiva a Consultar",
					gridTareasRepetitivas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			tareaRepetitivaSeleccionada = tareaRepetitivaDAO
					.obtenerTareaRepetitiva(id);
			consultarTareaRepetitivaSeleccionada(tareaRepetitivaSeleccionada);
		}
	}

	public void modificarTareaRepetitivaSeleccionada(
			TareaRepetitiva tareaRepetitivaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		String fecha;
		try {
			fecha = formatoFecha.format(tareaRepetitivaFiltro.getFechaInicio());
		} catch (Exception e) {
			fecha = "";
		}
		arguments.put("titulo", "Consultar Tarea Asignada");
		arguments.put("icono", "z-icon-edit");
		arguments.put("tareaRepetitiva", tareaRepetitivaFiltro);
		arguments.put("divModificar", true);
		arguments.put("btnGuardar", true);
		arguments.put("btnCancelar", true);
		arguments.put("fecha", fecha);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaRepetitiva.zul", null, arguments);
		window.doModal();

	}

	public void consultarTareaRepetitivaSeleccionada(
			TareaRepetitiva tareaRepetitivaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		String fecha;
		try {
			fecha = formatoFecha.format(tareaRepetitivaFiltro.getFechaInicio());
		} catch (Exception e) {
			fecha = "";
		}
		arguments.put("titulo", "Consultar Tarea Asignada");
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaRepetitiva", tareaRepetitivaFiltro);
		arguments.put("divConsultar", true);
		arguments.put("btnEditar", true);
		arguments.put("btnSalir", true);
		arguments.put("fecha", fecha);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaRepetitiva.zul", null, arguments);
		window.doModal();

	}

	@GlobalCommand
	@NotifyChange({ "tareasRepetitivas", "footerMessageTareasRepetitivas" })
	public void actualizarTareasRepetitivas() {
		tareasRepetitivas = tareaRepetitivaDAO
				.obtenerTareasRepetitivasSubalternosActivas(usuarioFiltro
						.getId());
	}

	private String getIdSelectedRow() {
		for (Component component : gridTareasRepetitivas.getRows()
				.getChildren()) {
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

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
