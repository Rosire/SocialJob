package controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.BitacoraTareaAsignadaDAO;
import modelo.dao.CargoEmpleadoDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.TareaAsignadaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Estatus;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.TareaAsignada;
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

public class ControladorTablaTareasAsignadas extends
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

	private TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();
	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private EstatusTareaAsignada estatusTarea;
	private BitacoraTareaAsignadaDAO bitacoraTareaAsignadaDAO = new BitacoraTareaAsignadaDAO();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	private ListModel<TareaAsignada> listaTareasAsignadasEnMarchaModel;
	private ListModel<TareaAsignada> listaTareasAsignadasPendientesModel;
	private ListModel<TareaAsignada> listaTareasAsignadasPausadasModel;
	private ListModel<TareaAsignada> listaTareasAsignadasAprobadasModel;
	private ListModel<TareaAsignada> listaTareasAsignadasCulminadasModel;
	private ListModel<TareaAsignada> listaTareasAsignadasModel;

	private ListModel<BitacoraTareaAsignada> listaBitacorasModel;
	private List<BitacoraTareaAsignada> bitacoras;

	private List<TareaAsignada> tareasAsignadasPendientes;
	private List<TareaAsignada> tareasAsignadasEnMarcha;
	private List<TareaAsignada> tareasAsignadasPausadas;
	private List<TareaAsignada> tareasAsignadasAprobadas;
	private List<TareaAsignada> tareasAsignadasCulminadas;
	private List<TareaAsignada> tareasAsignadas;

	private static final String footerMessageTareasEnMarcha = "Total de Tareas En Marcha: %d";
	private static final String footerMessageTareasPendientes = "Total de Tareas Pendientes: %d";
	private static final String footerMessageTareasPausadas = "Total de Tareas Pausadas: %d";
	private static final String footerMessageTareasCulminadas = "Total de Tareas Culminadas: %d";
	private static final String footerMessageTareasAprobadas = "Total de Tareas Aprobadas: %d";
	private static final String footerMessageTareas = "Total de Tareas Asignadas: %d";

	public ControladorTablaTareasAsignadas() {
		super();
		tareasAsignadasEnMarcha = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "EN MARCHA");
		tareasAsignadasPendientes = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "POR INICIAR");
		tareasAsignadasPausadas = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "PAUSADA");
		tareasAsignadasCulminadas = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "CULMINADA");

		tareasAsignadasAprobadas = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "APROBADA");
		tareasAsignadas = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleado(usuarioFiltro.getId());

	}

	public ListModel<TareaAsignada> getTareasAsignadasPendientes() {
		listaTareasAsignadasPendientesModel = new ListModelList<TareaAsignada>(
				tareasAsignadasPendientes);
		return listaTareasAsignadasPendientesModel;
	}

	public String getFooterMessageTareasPendientes() {
		return String.format(footerMessageTareasPendientes,
				tareasAsignadasPendientes.size());
	}

	public ListModel<TareaAsignada> getTareasAsignadasEnMarcha() {
		listaTareasAsignadasEnMarchaModel = new ListModelList<TareaAsignada>(
				tareasAsignadasEnMarcha);
		return listaTareasAsignadasEnMarchaModel;
	}

	public String getFooterMessageTareasEnMarcha() {
		return String.format(footerMessageTareasEnMarcha,
				tareasAsignadasEnMarcha.size());
	}

	public ListModel<TareaAsignada> getTareasAsignadasPausadas() {
		listaTareasAsignadasPausadasModel = new ListModelList<TareaAsignada>(
				tareasAsignadasPausadas);
		return listaTareasAsignadasPausadasModel;
	}

	public String getFooterMessageTareasPausadas() {
		return String.format(footerMessageTareasPausadas,
				tareasAsignadasPausadas.size());
	}

	public ListModel<TareaAsignada> getTareasAsignadasAprobadas() {
		listaTareasAsignadasAprobadasModel = new ListModelList<TareaAsignada>(
				tareasAsignadasAprobadas);
		return listaTareasAsignadasAprobadasModel;
	}

	public String getFooterMessageTareasAprobadas() {
		return String.format(footerMessageTareasAprobadas,
				tareasAsignadasAprobadas.size());
	}

	public ListModel<TareaAsignada> getTareasAsignadas() {
		listaTareasAsignadasModel = new ListModelList<TareaAsignada>(
				tareasAsignadas);
		return listaTareasAsignadasModel;
	}

	public String getFooterMessageTareas() {
		return String.format(footerMessageTareas, tareasAsignadas.size());
	}

	public ListModel<TareaAsignada> getTareasAsignadasCulminadas() {
		listaTareasAsignadasCulminadasModel = new ListModelList<TareaAsignada>(
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
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			String estatus = tareaAsignadaFiltro.getEstatusTareaAsignada()
					.getNombre();
			switch (estatus) {
			case "EN MARCHA":
				consultarTareaEnMarcha(tareaAsignadaFiltro);
				break;
			case "PAUSADA":
				consultarTareaParaIniciar(tareaAsignadaFiltro);
				break;
			case "POR INICIAR":
				consultarTareaParaIniciar(tareaAsignadaFiltro);
				break;
			default:
				consultarTareaSinModificar(tareaAsignadaFiltro);
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
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			String estatus = tareaAsignadaFiltro.getEstatusTareaAsignada()
					.getNombre();
			switch (estatus) {
			case "PAUSADA":
				iniciarTarea(tareaAsignadaFiltro);
				break;
			case "POR INICIAR":
				iniciarTarea(tareaAsignadaFiltro);
				break;
			default:
				showNotifyWarning(
						"Solo las tareas con estatus \"PAUSADA\" o \"POR INICIAR\" se pueden indicar que inician.",
						gridTodas);
				break;
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
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			if (tareaAsignada.getEstatusTareaAsignada().getNombre()
					.equals("EN MARCHA")) {
				pausarTarea(tareaAsignada);
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
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			if (tareaAsignada.getEstatusTareaAsignada().getNombre()
					.equals("EN MARCHA")) {
				finalizarTarea(tareaAsignada);
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
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			String estatus = tareaAsignadaFiltro.getEstatusTareaAsignada()
					.getNombre();
			switch (estatus) {
			case "PAUSADA":
				consultarTareaParaIniciar(tareaAsignadaFiltro);
				break;
			case "POR INICIAR":
				consultarTareaParaIniciar(tareaAsignadaFiltro);
				break;
			case "EN MARCHA":
				modificarTareaEnMarcha(tareaAsignadaFiltro);
				break;
			default:
				showNotifyWarning(
						"Las tareas con estatus \"APROBADA\" o \"CULMINADA\"  sólo se pueden consultar.",
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
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			consultarTareaEnMarcha(tareaAsignadaFiltro);
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
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			modificarTareaEnMarcha(tareaAsignadaFiltro);
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
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			pausarTarea(tareaAsignada);
		}
	}

	// Finalizar Tarea En Marcha
	@Listen("onClick=#btnFinalizarTareaEnMarcha")
	public void finalizarTareaEnMarcha() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasEnMarcha);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una tarea pendiente a finalizar",
					gridTareasEnMarcha);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			finalizarTarea(tareaAsignada);
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
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			consultarTareaParaIniciar(tareaAsignada);
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
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			iniciarTarea(tareaAsignada);

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
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			consultarTareaParaIniciar(tareaAsignada);
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
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			iniciarTarea(tareaAsignada);
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
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			consultarTareaSinModificar(tareaAsignada);
		}
	}

	// Consultar Tarea Aprobada
	@Listen("onClick=#btnConsultarTareaAprobada")
	public void consultarTareaAprobada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasAprobadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Culminada a Consultar",
					gridTareasAprobadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignada = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			consultarTareaSinModificar(tareaAsignada);
		}
	}

	public void consultarTareaSinModificar(TareaAsignada tareaAsignadaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(tareaAsignadaFiltro
						.getCargoEmpleado().getCargo().getCargoSuperior()
						.getId());
		String supervisor = cargoEmpleado.getEmpleado().getNombres() + " "
				+ cargoEmpleado.getEmpleado().getApellidos() + " (V-"
				+ cargoEmpleado.getEmpleado().getCedula() + ")";
		String titulo = "Consultar Tarea";
		String fechaTarea;
		try {
			fechaTarea = formatoFecha.format(tareaAsignadaFiltro.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
				tareaAsignadaFiltro, usuarioFiltro, new Date(),
				"Tarea Consultada por el Empleado", "CONSULTAR");
		bitacoraTareaAsignadaDAO
				.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
		bitacoras = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraTareaAsignada>(
				bitacoras);

		String duracion;
		long d = tareaAsignadaFiltro.getDuracion();
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
		long d2 = tareaAsignadaFiltro.getEficiencia();
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
		if(tareaAsignadaFiltro.getEstatusTareaAsignada().getNombre().equalsIgnoreCase("APROBADA"))
		{

			String footer = "Total de Movimientos de la tarea " + bitacoras.size();
			arguments.put("bitacoras", listaBitacorasModel);
			arguments.put("footer", footer);
			arguments.put("fechaTarea", fechaTarea);
			arguments.put("titulo", titulo);
			arguments.put("supervisor", supervisor);
			arguments.put("icono", "z-icon-eye");
			arguments.put("tareaAsignada", tareaAsignadaFiltro);
			arguments.put("divProgreso", true);
			arguments.put("btnSalirLabel", true);
			arguments.put("divFechaInicio", true);
			arguments.put("divFechaFinalizacion", true);
			arguments.put("divCalificacion", true);
			arguments.put("divDuracionTarea", true);
			arguments.put("divMiDuracionTarea", true);
			arguments.put("duracion", duracion);
			arguments.put("eficiencia", miduracion);
		}
		else if(tareaAsignadaFiltro.getEstatusTareaAsignada().getNombre().equalsIgnoreCase("CULMINADA"))
		{
			String footer = "Total de Movimientos de la tarea " + bitacoras.size();
			arguments.put("bitacoras", listaBitacorasModel);
			arguments.put("footer", footer);
			arguments.put("fechaTarea", fechaTarea);
			arguments.put("titulo", titulo);
			arguments.put("supervisor", supervisor);
			arguments.put("icono", "z-icon-eye");
			arguments.put("tareaAsignada", tareaAsignadaFiltro);
			arguments.put("divProgreso", true);
			arguments.put("btnSalirLabel", true);
			arguments.put("divFechaInicio", true);
			arguments.put("divFechaFinalizacion", true);
			arguments.put("divCalificacion", false);
			arguments.put("divDuracionTarea", true);
			arguments.put("divMiDuracionTarea", false);
			arguments.put("duracion", duracion);

		}

		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaAsignada.zul", null, arguments);

		window.doModal();
	}

	public void consultarTareaEnMarcha(TareaAsignada tareaAsignadaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();

		String titulo = "Consultar Tarea";
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(tareaAsignadaFiltro
						.getCargoEmpleado().getCargo().getCargoSuperior()
						.getId());
		String supervisor = cargoEmpleado.getEmpleado().getNombres() + " "
				+ cargoEmpleado.getEmpleado().getApellidos() + " (V-"
				+ cargoEmpleado.getEmpleado().getCedula() + ")";

		String duracion;
		long d = tareaAsignadaFiltro.getDuracion();
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
		
		String fechaTarea;
		try {
			fechaTarea = formatoFecha.format(tareaAsignadaFiltro.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
				tareaAsignadaFiltro, usuarioFiltro, new Date(),
				"Tarea Consultada por el Empleado", "CONSULTAR");
		bitacoraTareaAsignadaDAO
				.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
		bitacoras = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraTareaAsignada>(
				bitacoras);
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("supervisor", supervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnSalirLabel", true);
		arguments.put("btnEditarLabel", true);

		arguments.put("divFechaInicio", true);
		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);
		
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaAsignada.zul", null, arguments);

		window.doModal();
	}

	public void consultarTareaParaIniciar(TareaAsignada tareaAsignadaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Consultar Tarea";
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(tareaAsignadaFiltro
						.getCargoEmpleado().getCargo().getCargoSuperior()
						.getId());
		String supervisor = cargoEmpleado.getEmpleado().getNombres() + " "
				+ cargoEmpleado.getEmpleado().getApellidos() + " (V-"
				+ cargoEmpleado.getEmpleado().getCedula() + ")";
		String fechaTarea;
		
		String duracion;
		long d = tareaAsignadaFiltro.getDuracion();
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
			fechaTarea = formatoFecha.format(tareaAsignadaFiltro.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
				tareaAsignadaFiltro, usuarioFiltro, new Date(),
				"Tarea Consultada por el Empleado", "CONSULTAR");
		bitacoraTareaAsignadaDAO
				.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
		bitacoras = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraTareaAsignada>(
				bitacoras);
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("supervisor", supervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnIniciarTarea", true);
		arguments.put("btnSalirLabel", true);

		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaAsignada.zul", null, arguments);

		window.doModal();
	}

	public void finalizarTarea(TareaAsignada tareaAsignada) {
		estatusTarea = estatusTareaAsignadaDAO
				.obtenerEstatusPorNombre("CULMINADA");
		tareaAsignada.setEstatusTareaAsignada(estatusTarea);
		tareaAsignada.setProgreso(100);
		tareaAsignada.setFechaFinalizacion(new Date());
		Messagebox.show("¿Seguro que ya finalizo la tarea ?", "Question",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							tareaAsignadaDAO
									.actualizarTareaAsignada(tareaAsignada);
							BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
									tareaAsignada, usuarioFiltro, new Date(),
									"Tarea Culminada por el Empleado",
									"CULMINAR");
							bitacoraTareaAsignadaDAO
									.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
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

	public void modificarTareaEnMarcha(TareaAsignada tareaAsignadaFiltro) {
		Map<String, Object> arguments = new HashMap<String, Object>();
		String titulo = "Editar Tarea";
		CargoEmpleado cargoEmpleado = cargoEmpleadoDAO
				.obtenerUsuarioSupervisorPorCargo(tareaAsignadaFiltro
						.getCargoEmpleado().getCargo().getCargoSuperior()
						.getId());
		String supervisor = cargoEmpleado.getEmpleado().getNombres() + " "
				+ cargoEmpleado.getEmpleado().getApellidos() + " (V-"
				+ cargoEmpleado.getEmpleado().getCedula() + ")";
		String fechaTarea;
		String duracion;
		long d = tareaAsignadaFiltro.getDuracion();
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
			fechaTarea = formatoFecha.format(tareaAsignadaFiltro.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		bitacoras = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacorasModel = new ListModelList<BitacoraTareaAsignada>(
				bitacoras);
		String footer = "Total de Movimientos de la tarea " + bitacoras.size();
		arguments.put("bitacoras", listaBitacorasModel);
		arguments.put("footer", footer);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("supervisor", supervisor);
		arguments.put("titulo", titulo);
		arguments.put("icono", "z-icon-edit");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("divSlider", true);
		arguments.put("btnGuardarModificacion", true);
		arguments.put("btnCancelarModificacion", true);
		arguments.put("divFechaInicio", true);
		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/ConsultarEditarTareaAsignada.zul", null, arguments);
		window.doModal();
	}

	public void iniciarTarea(TareaAsignada tareaAsignadaFiltro) {

		
		String status=tareaAsignadaFiltro.getEstatusTareaAsignada().getNombre();
		estatusTarea = estatusTareaAsignadaDAO
				.obtenerEstatusPorNombre("EN MARCHA");

		tareaAsignadaFiltro.setEstatusTareaAsignada(estatusTarea);
		Messagebox.show("¿Seguro que desea INICIAR la Tarea seleccionada ?",
				"Iniciar Tarea", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							String descripcion = "";
							String accion = "";
							if (status.equalsIgnoreCase("POR INICIAR")) {
								tareaAsignadaFiltro.setFechaInicio(new Date());
								descripcion = "Tarea iniciada por el Empleado";
								accion = "INICIAR";
							} else if (status.equalsIgnoreCase("PAUSADA")) {
								descripcion = "Tarea puesta en MARCHA nuevamente por el Empleado";
								accion = "REINICIAR";
							}
							tareaAsignadaDAO
									.actualizarTareaAsignada(tareaAsignadaFiltro);
							BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
									tareaAsignadaFiltro, usuarioFiltro,
									new Date(), descripcion, accion);
							bitacoraTareaAsignadaDAO
									.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
							
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

	public void pausarTarea(TareaAsignada tareaAsignada) {
		estatusTarea = estatusTareaAsignadaDAO
				.obtenerEstatusPorNombre("PAUSADA");
		tareaAsignada.setEstatusTareaAsignada(estatusTarea);
		Messagebox.show("¿Seguro que desea Pausar la Tarea seleccionada ?",
				"Pausar Tarea", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							tareaAsignadaDAO
									.actualizarTareaAsignada(tareaAsignada);
							BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
									tareaAsignada, usuarioFiltro, new Date(),
									"Tarea Pausada por el Empleado", "PAUSAR");
							bitacoraTareaAsignadaDAO
									.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
							showNotifyInfo("Tarea Pusada Exitosamente",
									gridTareasEnMarcha);
							BindUtils.postGlobalCommand(null, null,
									"actualizarTabla", null);
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
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
		tareasAsignadasEnMarcha = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "EN MARCHA");
		tareasAsignadasPendientes = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "POR INICIAR");
		tareasAsignadasPausadas = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "PAUSADA");
		tareasAsignadasCulminadas = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "CULMINADA");

		tareasAsignadasAprobadas = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
						usuarioFiltro.getId(), "APROBADA");
		tareasAsignadas = tareaAsignadaDAO
				.obtenerTareasAsignadasPorEmpleado(usuarioFiltro.getId());
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
