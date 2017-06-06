package controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.BitacoraTareaAsignadaDAO;
import modelo.dao.BitacoraTareaRepetitivaAsignadaDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.TareaAsignadaDAO;
import modelo.dao.TareaRepetitivaAsignadaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.BitacoraTareaRepetitivaAsignada;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Estatus;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.TareaAsignada;
import modelo.dto.TareaRepetitiva;
import modelo.dto.TareaRepetitivaAsignada;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Input;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Window;

public class ControladorTablaTareasRepetitivasAsignadasEmpleado extends
		SelectorComposer<Component> {

	@Wire
	private Grid gridTareasPendientes;
	@Wire
	private Grid gridTareasEnMarcha;
	@Wire
	private Grid gridTodas;
	@Wire
	private Grid gridTareasPausadas;
	@Wire
	private Grid gridTareasCulminadas;
	@Wire
	private Grid gridTareasAprobadas;
	@Wire
	private Div divTareasPendientes;
	@Wire
	private Div divTareasEnMarcha;
	@Wire
	private Div divTodas;
	@Wire
	private Div divTareasPausadas;
	@Wire
	private Div divTareasCulminadas;
	@Wire
	private Div divTareasAprobadas;
	@Wire
	private Div divFechaInicio;
	@Wire
	private Div divFechaFinalizacion;
	@Wire
	private Div divCalificacion;
	@Wire
	private Div divDuracionTarea;
	@Wire
	private Div divMiDuracionTarea;
	@Wire
	private Panel paneltabla;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
	private EstatusTareaAsignada estatusTarea;

	private TareaRepetitivaAsignadaDAO tareaRepetitivaAsignadaDAO = new TareaRepetitivaAsignadaDAO();
	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private BitacoraTareaRepetitivaAsignadaDAO bitacoraTareaRepetitivaAsignadaDAO = new BitacoraTareaRepetitivaAsignadaDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	private ListModel<TareaRepetitivaAsignada> listaTareasAsignadasEnMarchaModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasAsignadasPendientesModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasAsignadasPausadasModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasAsignadasAprobadasModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasAsignadasCulminadasModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasAsignadasModel;

	private ListModel<BitacoraTareaRepetitivaAsignada> listaBitacorasModel;

	private List<BitacoraTareaRepetitivaAsignada> bitacoras;

	private List<TareaRepetitivaAsignada> tareasAsignadasPendientes;
	private List<TareaRepetitivaAsignada> tareasAsignadasEnMarcha;
	private List<TareaRepetitivaAsignada> tareasAsignadasPausadas;
	private List<TareaRepetitivaAsignada> tareasAsignadasAprobadas;
	private List<TareaRepetitivaAsignada> tareasAsignadasCulminadas;
	private List<TareaRepetitivaAsignada> tareasAsignadas;

	private static final String footerMessageTareasEnMarcha = "Total de Tareas Repetitivas En Marcha: %d";
	private static final String footerMessageTareasPendientes = "Total de Tareas Repetitivas Pendientes: %d";
	private static final String footerMessageTareasPausadas = "Total de Tareas Repetitivas Pausadas: %d";
	private static final String footerMessageTareasCulminadas = "Total de Tareas Repetitivas Culminadas: %d";
	private static final String footerMessageTareasAprobadas = "Total de Tareas Repetitivas Aprobadas: %d";
	private static final String footerMessageTareas = "Total de Tareas Repetitivas Asignadas: %d";

	public ControladorTablaTareasRepetitivasAsignadasEmpleado() {
		super();
		tareasAsignadasEnMarcha = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "EN MARCHA");
		tareasAsignadasPendientes = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "POR INICIAR");
		tareasAsignadasPausadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "PAUSADA");
		tareasAsignadasCulminadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "CULMINADA");
		tareasAsignadasAprobadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "APROBADA");
		tareasAsignadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleado(usuarioFiltro
						.getId());

	}

	public ListModel<TareaRepetitivaAsignada> getTareasAsignadasPendientes() {
		listaTareasAsignadasPendientesModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasAsignadasPendientes);
		return listaTareasAsignadasPendientesModel;
	}

	public String getFooterMessageTareasPendientes() {
		return String.format(footerMessageTareasPendientes,
				tareasAsignadasPendientes.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasAsignadasEnMarcha() {
		listaTareasAsignadasEnMarchaModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasAsignadasEnMarcha);
		return listaTareasAsignadasEnMarchaModel;
	}

	public String getFooterMessageTareasEnMarcha() {
		return String.format(footerMessageTareasEnMarcha,
				tareasAsignadasEnMarcha.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasAsignadasPausadas() {
		listaTareasAsignadasPausadasModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasAsignadasPausadas);
		return listaTareasAsignadasPausadasModel;
	}

	public String getFooterMessageTareasPausadas() {
		return String.format(footerMessageTareasPausadas,
				tareasAsignadasPausadas.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasAsignadasAprobadas() {
		listaTareasAsignadasAprobadasModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasAsignadasAprobadas);
		return listaTareasAsignadasAprobadasModel;
	}

	public String getFooterMessageTareasAprobadas() {
		return String.format(footerMessageTareasAprobadas,
				tareasAsignadasAprobadas.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasAsignadas() {
		listaTareasAsignadasModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasAsignadas);
		return listaTareasAsignadasModel;
	}

	public String getFooterMessageTareas() {
		return String.format(footerMessageTareas, tareasAsignadas.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasAsignadasCulminadas() {
		listaTareasAsignadasCulminadasModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasAsignadasCulminadas);
		return listaTareasAsignadasCulminadasModel;
	}

	public String getFooterMessageTareasCulminadas() {
		return String.format(footerMessageTareasCulminadas,
				tareasAsignadasCulminadas.size());
	}

	@Listen("onClick=#todas")
	public void visibleListaTodas() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(true);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#pendientes")
	public void visibleListaPendientes() {
		divTareasPendientes.setVisible(true);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#enMarcha")
	public void visibleListaEnMarcha() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(true);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#pausadas")
	public void visibleListaPausadas() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(true);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#culminadas")
	public void visibleListaCulminadas() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(true);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#aprobadas")
	public void visibleListaAprobadas() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(true);
	}

	// Consultar Tareas del Grid Todas
	@Listen("onClick=#btnConsultarTarea")
	public void consultarTarea() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar", gridTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			String estatus = tareaRepetitivaAsignadaFiltro
					.getEstatusTareaAsignada().getNombre();
			switch (estatus) {
			case "EN MARCHA":
				consultarTareaEnMarcha(tareaRepetitivaAsignadaFiltro);
				break;
			case "POR INICIAR":
				consultarTareaParaIniciar(tareaRepetitivaAsignadaFiltro);
				break;
			case "PAUSADA":
				consultarTareaParaIniciar(tareaRepetitivaAsignadaFiltro);
				break;
			default:
				consultarTareaSinModificar(tareaRepetitivaAsignadaFiltro);
				break;
			}
		}
	}

	// Iniciar Tarea Grid Todas
	@Listen("onClick=#btnIniciarTarea")
	public void iniciarTarea() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea pendiente a iniciar",
					gridTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			String estatus = tareaRepetitivaAsignadaFiltro
					.getEstatusTareaAsignada().getNombre();
			if (estatus.equals("PAUSADA") || estatus.equals("POR INICIAR")) {
				iniciarTarea(tareaRepetitivaAsignadaFiltro);
			} else {
				showNotifyWarning(
						"Solo las tareas con estatus \"PAUSADA\" o \"POR INICIAR\" se pueden indicar que inician.",
						gridTodas);
			}

		}
	}

	// Pausar Tarea de Grid Todas
	@Listen("onClick=#btnPausarTarea")
	public void pausarTarea() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea en marcha a pausar",
					gridTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);

			if (tareaRepetitivaAsignadaFiltro.getEstatusTareaAsignada()
					.getNombre().equals("EN MARCHA")) {
				pausarTarea(tareaRepetitivaAsignadaFiltro);
			} else {
				showNotifyWarning(
						"Solo las tareas con estatus \"EN MARCHA\" se pueden pausar.",
						gridTodas);
			}
		}
	}

	// Finalizar Tarea de Grid Todas
	@Listen("onClick=#btnFinalizarTarea")
	public void finalizarTarea() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea pendiente a finalizar",
					gridTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignada = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			if (tareaRepetitivaAsignada.getEstatusTareaAsignada().getNombre()
					.equals("EN MARCHA")) {
				finalizarTarea(tareaRepetitivaAsignada);
			} else {
				showNotifyWarning(
						"Solo las tareas con estatus \"EN MARCHA\" se pueden finalizar directamente.",
						gridTodas);
			}
		}
	}

	// Editar Progreso de Tarea de Grid Todas
	@Listen("onClick=#btnEditarTarea")
	public void editarTarea() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea para editar su progreso",
					gridTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			String estatus = tareaRepetitivaAsignadaFiltro
					.getEstatusTareaAsignada().getNombre();
			switch (estatus) {
			case "EN MARCHA":
				modificarTareaEnMarcha(tareaRepetitivaAsignadaFiltro);
				break;
			case "POR INICIAR":
				consultarTareaParaIniciar(tareaRepetitivaAsignadaFiltro);
				break;
			case "PAUSADA":
				consultarTareaParaIniciar(tareaRepetitivaAsignadaFiltro);
				break;
			default:
				showNotifyWarning(
						"Las tareas con estatus \"APROBADA\" o  \"CULMINADA\" sólo se pueden consultar.",
						gridTodas);
				break;
			}
		}
	}

	// Consultar Tarea En Marcha
	@Listen("onClick=#btnConsultarTareaEnMarcha")
	public void consultarTareaEnMarcha() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasEnMarcha);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea en Marcha a Consultar",
					gridTareasEnMarcha);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaEnMarcha(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Editar Progreso de Tarea en Marcha
	@Listen("onClick=#btnEditarTareaEnMarcha")
	public void editarTareaEnMarcha() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasEnMarcha);
		if (indexSeleccionado == null) {
			showNotify(
					"Debe seleccionar una Tarea en Marcha para editar su progreso",
					gridTareasEnMarcha);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			modificarTareaEnMarcha(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Pausar Tarea En Marcha
	@Listen("onClick=#btnPausarTareaEnMarcha")
	public void pausarTareaEnMarcha() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasEnMarcha);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea en marcha a pausar",
					gridTareasEnMarcha);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			pausarTarea(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Finalizar Tarea En Marcha
	@Listen("onClick=#btnFinalizarTareaEnMarcha")
	public void finalizarTareaEnMarcha() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasEnMarcha);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea pendiente a finalizar",
					paneltabla);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			finalizarTarea(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Consultar Tarea Pendiente
	@Listen("onClick=#btnConsultarTareaPendiente")
	public void consultarTareaPendiente() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasPendientes);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Pendiente a Consultar",
					gridTareasPendientes);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaParaIniciar(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Editar Progreso de Tarea en Marcha
	@Listen("onClick=#btnIniciarTareaPendiente")
	public void iniciarTareaPendiente() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasPendientes);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea pendiente a iniciar",
					gridTareasPendientes);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			iniciarTarea(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Consultar Tarea Pausada
	@Listen("onClick=#btnConsultarTareaPausada")
	public void consultarTareaPausada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasPausadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Pendiente a Consultar",
					gridTareasPausadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaParaIniciar(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Iniciar Tarea Pausada
	@Listen("onClick=#btnIniciarTareaPausada")
	public void iniciarTareaPausada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasPausadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea pausada a iniciar",
					gridTareasPausadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			iniciarTarea(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Consultar Tarea Culminada
	@Listen("onClick=#btnConsultarTareaCulminada")
	public void consultarTareaCulminada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasCulminadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Culminada a Consultar",
					gridTareasCulminadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaSinModificar(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Consultar Tarea Culminada
	@Listen("onClick=#btnConsultarTareaAprobada")
	public void consultarTareaAprobada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasAprobadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Culminada a Consultar",
					gridTareasAprobadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaSinModificar(tareaRepetitivaAsignadaFiltro);
		}
	}

	public void consultarTareaSinModificar(
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(tareaRepetitivaAsignadaFiltro
						.getTareaRepetitiva().getCargo().getCargoSuperior()
						.getId());
		String supervisor = cargoEmpleado.getEmpleado().getNombres() + " "
				+ cargoEmpleado.getEmpleado().getApellidos() + " (V-"
				+ cargoEmpleado.getEmpleado().getCedula() + ")";
		String titulo = "Consultar Tarea";
		String fechaTarea;
		try {
			fechaTarea = formatoFecha.format(tareaRepetitivaAsignadaFiltro
					.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
				tareaRepetitivaAsignadaFiltro, usuarioFiltro, new Date(),
				"Tarea Consultada por el Empleado", "CONSULTAR");
		bitacoraTareaRepetitivaAsignadaDAO
				.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
		bitacoras = bitacoraTareaRepetitivaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaRepetitivaAsignadaFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraTareaRepetitivaAsignada>(
				bitacoras);
		
		String duracion;
		long d = tareaRepetitivaAsignadaFiltro.getDuracion();
		int horas = (int) (d / 3600);
		int minutos = (int) ((d % 3600) / 60);

		if (horas < 10) {
			duracion = "0" + horas + " Horas ";
		} else {
			duracion = horas + " Horas ";
		}

		if (minutos < 10) {
			duracion = duracion + "0" + minutos + " Minutos ";
		} else {
			duracion = duracion + minutos + " Minutos ";
		}
		
		String miduracion;
		long d2 = tareaRepetitivaAsignadaFiltro.getEficiencia();
		int horas2 = (int) (d2 / 3600);
		int minutos2 = (int) ((d2 % 3600) / 60);

		if (horas2 < 10) {
			miduracion = "0" + horas2 + " Horas ";
		} else {
			miduracion = horas2 + " Horas ";
		}

		if (minutos2 < 10) {
			miduracion = miduracion + "0" + minutos2 + " Minutos ";
		} else {
			miduracion = miduracion + minutos2  + " Minutos ";
		}
		
		if(tareaRepetitivaAsignadaFiltro.getEstatusTareaAsignada().getNombre().equalsIgnoreCase("APROBADA"))
		{
			arguments.put("divFechaInicio", true);
			arguments.put("divFechaFinalizacion", true);
			arguments.put("divCalificacion", true);
			arguments.put("divDuracionTarea", true);
			arguments.put("divMiDuracionTarea", true);
			arguments.put("duracion", duracion);
			arguments.put("eficiencia", miduracion);
		}
		else if(tareaRepetitivaAsignadaFiltro.getEstatusTareaAsignada().getNombre().equalsIgnoreCase("CULMINADA"))
		{
			arguments.put("divFechaInicio", true);
			arguments.put("divFechaFinalizacion", true);
			arguments.put("divCalificacion", false);
			arguments.put("divDuracionTarea", true);
			arguments.put("divMiDuracionTarea", false);
			arguments.put("duracion", duracion);
		}
			
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("supervisor", supervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaRepetitivaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnSalirLabel", true);

		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaRepetitivaAsignada.zul", null,
				arguments);
		window.doModal();
	}

	public void consultarTareaEnMarcha(
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(tareaRepetitivaAsignadaFiltro
						.getTareaRepetitiva().getCargo().getCargoSuperior()
						.getId());
		String supervisor = cargoEmpleado.getEmpleado().getNombres() + " "
				+ cargoEmpleado.getEmpleado().getApellidos() + " (V-"
				+ cargoEmpleado.getEmpleado().getCedula() + ")";
		String titulo = "Consultar Tarea";
		String fechaTarea;
		
		String duracion;
		long d = tareaRepetitivaAsignadaFiltro.getDuracion();
		int horas = (int) (d / 3600);
		int minutos = (int) ((d % 3600) / 60);

		if (horas < 10) {
			duracion = "0" + horas + " Horas ";
		} else {
			duracion = horas + " Horas ";
		}

		if (minutos < 10) {
			duracion = duracion + "0" + minutos + " Minutos ";
		} else {
			duracion = duracion + minutos + " Minutos ";
		}
		
		try {
			fechaTarea = formatoFecha.format(tareaRepetitivaAsignadaFiltro
					.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
				tareaRepetitivaAsignadaFiltro, usuarioFiltro, new Date(),
				"Tarea Consultada por el Empleado", "CONSULTAR");
		bitacoraTareaRepetitivaAsignadaDAO
				.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
		bitacoras = bitacoraTareaRepetitivaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaRepetitivaAsignadaFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraTareaRepetitivaAsignada>(
				bitacoras);
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("supervisor", supervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaRepetitivaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnSalirLabel", true);
		arguments.put("btnEditarLabel", true);

		arguments.put("divFechaInicio", true);
		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);
		
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaRepetitivaAsignada.zul", null,
				arguments);
		window.doModal();
	}

	public void consultarTareaParaIniciar(
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(tareaRepetitivaAsignadaFiltro
						.getTareaRepetitiva().getCargo().getCargoSuperior()
						.getId());
		String supervisor = cargoEmpleado.getEmpleado().getNombres() + " "
				+ cargoEmpleado.getEmpleado().getApellidos() + " (V-"
				+ cargoEmpleado.getEmpleado().getCedula() + ")";
		String titulo = "Consultar Tarea";
		String fechaTarea;
		
		String duracion;
		long d = tareaRepetitivaAsignadaFiltro.getDuracion();
		int horas = (int) (d / 3600);
		int minutos = (int) ((d % 3600) / 60);

		if (horas < 10) {
			duracion = "0" + horas + " Horas ";
		} else {
			duracion = horas + " Horas ";
		}

		if (minutos < 10) {
			duracion = duracion + "0" + minutos + " Minutos ";
		} else {
			duracion = duracion + minutos + " Minutos ";
		}
		
		try {
			fechaTarea = formatoFecha.format(tareaRepetitivaAsignadaFiltro
					.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
				tareaRepetitivaAsignadaFiltro, usuarioFiltro, new Date(),
				"Tarea Consultada por el Empleado", "CONSULTAR");
		bitacoraTareaRepetitivaAsignadaDAO
				.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
		bitacoras = bitacoraTareaRepetitivaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaRepetitivaAsignadaFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraTareaRepetitivaAsignada>(
				bitacoras);
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("supervisor", supervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaRepetitivaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnIniciarTarea", true);
		arguments.put("btnSalirLabel", true);
		
		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);

		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaRepetitivaAsignada.zul", null,
				arguments);
		window.doModal();
	}

	public void iniciarTarea(
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro) {

			
			String status=tareaRepetitivaAsignadaFiltro.getEstatusTareaAsignada().getNombre();
			estatusTarea = estatusTareaAsignadaDAO
					.obtenerEstatusPorNombre("EN MARCHA");

			tareaRepetitivaAsignadaFiltro.setEstatusTareaAsignada(estatusTarea);
			Messagebox.show("¿Seguro que desea INICIAR la Tarea seleccionada ?",
					"Iniciar Tarea", Messagebox.OK | Messagebox.CANCEL,
					Messagebox.QUESTION, new EventListener<Event>() {
						public void onEvent(Event e) {
							if (Messagebox.ON_OK.equals(e.getName())) {
								// OK is clicked
								String descripcion = "";
								String accion = "";
								if (status.equalsIgnoreCase("POR INICIAR")) {
									tareaRepetitivaAsignadaFiltro.setFechaInicio(new Date());
									descripcion = "Tarea iniciada por el Empleado";
									accion = "INICIAR";
								} else if (status.equalsIgnoreCase("PAUSADA")) {
									descripcion = "Tarea puesta en MARCHA nuevamente por el Empleado";
									accion = "REINICIAR";
								}
								tareaRepetitivaAsignadaDAO
										.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignadaFiltro);
								BitacoraTareaRepetitivaAsignada bitacoraTareaAsignada = new BitacoraTareaRepetitivaAsignada(
										tareaRepetitivaAsignadaFiltro, usuarioFiltro,
										new Date(), descripcion, accion);
								bitacoraTareaRepetitivaAsignadaDAO
										.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaAsignada);
								
								BindUtils.postGlobalCommand(null, null,
										"actualizarTabla", null);
								showNotifyInfo("Tarea INICIADA Exitosamente",
									 paneltabla);
							} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
								// Cancel is clicked
							}
						}
					});

		

	}

	public void pausarTarea(TareaRepetitivaAsignada tareaRepetitivaAsignada) {
		estatusTarea = estatusTareaAsignadaDAO
				.obtenerEstatusPorNombre("PAUSADA");
		tareaRepetitivaAsignada.setEstatusTareaAsignada(estatusTarea);
		Messagebox.show("¿Seguro que desea Pausar la Tarea ?", "Question",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							tareaRepetitivaAsignadaDAO
									.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignada);
							BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
									tareaRepetitivaAsignada, usuarioFiltro,
									new Date(),
									"Tarea Pausada por el Empleado", "PAUSAR");

							bitacoraTareaRepetitivaAsignadaDAO
									.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
							showNotifyInfo("Tarea Pusada Exitosamente",
									gridTodas);
							BindUtils.postGlobalCommand(null, null,
									"actualizarTabla", null);
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	public void finalizarTarea(TareaRepetitivaAsignada tareaRepetitivaAsignada) {
		estatusTarea = estatusTareaAsignadaDAO
				.obtenerEstatusPorNombre("CULMINADA");
		tareaRepetitivaAsignada.setEstatusTareaAsignada(estatusTarea);
		tareaRepetitivaAsignada.setProgreso(100);
		tareaRepetitivaAsignada.setFechaFinalizacion(new Date());
		Messagebox.show("¿Seguro que ya finalizo la tarea?", "Question",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							tareaRepetitivaAsignadaDAO
									.actualizarTareaRepetitivaAsignada(tareaRepetitivaAsignada);
							BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
									tareaRepetitivaAsignada, usuarioFiltro,
									new Date(),
									"Tarea Culminada por el Empleado",
									"CULMINAR");

							bitacoraTareaRepetitivaAsignadaDAO
									.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
							showNotifyInfo("Tarea Culminada Exitosamente",
									paneltabla);
							BindUtils.postGlobalCommand(null, null,
									"actualizarTabla", null);
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	public void modificarTareaEnMarcha(
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Editar Tarea";
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(tareaRepetitivaAsignadaFiltro
						.getTareaRepetitiva().getCargo().getCargoSuperior()
						.getId());
		String supervisor = cargoEmpleado.getEmpleado().getNombres() + " "
				+ cargoEmpleado.getEmpleado().getApellidos() + " (V-"
				+ cargoEmpleado.getEmpleado().getCedula() + ")";
		String fechaTarea;
		String duracion;
		long d = tareaRepetitivaAsignadaFiltro.getDuracion();
		int horas = (int) (d / 3600);
		int minutos = (int) ((d % 3600) / 60);

		if (horas < 10) {
			duracion = "0" + horas + " Horas ";
		} else {
			duracion = horas + " Horas ";
		}

		if (minutos < 10) {
			duracion = duracion + "0" + minutos + " Minutos ";
		} else {
			duracion = duracion + minutos + " Minutos ";
		}
		
		try {
			fechaTarea = formatoFecha.format(tareaRepetitivaAsignadaFiltro
					.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		bitacoras = bitacoraTareaRepetitivaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaRepetitivaAsignadaFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraTareaRepetitivaAsignada>(
				bitacoras);
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("supervisor", supervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-edit");
		arguments.put("tareaAsignada", tareaRepetitivaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("divSlider", true);
		arguments.put("btnGuardarModificacion", true);
		arguments.put("btnCancelarModificacion", true);
		
		arguments.put("divFechaInicio", true);
		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaRepetitivaAsignada.zul", null,
				arguments);
		window.doModal();
	}

	// Actualiza las Tablas de Tareas al realizar algun cambio
	@GlobalCommand
	@NotifyChange({ "tareasAsignadasEnMarcha", "tareasAsignadasPendientes",
			"tareasAsignadasPausadas", "tareasAsignadasAprobadas",
			"tareasAsignadasCulminadas", "tareasAsignadas",
			"footerMessageTareasPausadas", "footerMessageTareasEnMarcha",
			"footerMessageTareasCulminadas", "footerMessageTareasPendientes",
			"footerMessageTareasAprobadas", "footerMessageTareas" })
	public void actualizarTabla() {
		tareasAsignadasEnMarcha = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "EN MARCHA");
		tareasAsignadasPendientes = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "POR INICIAR");
		tareasAsignadasPausadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "PAUSADA");
		tareasAsignadasCulminadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "CULMINADA");
		tareasAsignadasAprobadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "APROBADA");
		tareasAsignadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasPorEmpleado(usuarioFiltro
						.getId());
	}

	private String getIdSelectedRowTarea(Grid grid) {
		for (Component component : grid.getRows().getChildren()) {
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

	private void showNotifyWarning(String msg, Component ref) {
		Clients.showNotification(msg, "warning", ref, "middle_center", 3000,
				true);
	}

}
