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
import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.NotificacionTareaRepetitivaAsignadaDAO;
import modelo.dao.TareaAsignadaDAO;
import modelo.dao.TareaDAO;
import modelo.dao.TareaRepetitivaAsignadaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.BitacoraTareaRepetitivaAsignada;
import modelo.dto.Cargo;
import modelo.dto.CargoEmpleado;
import modelo.dto.Estatus;
import modelo.dto.EstatusNotificacion;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.NotificacionTareaAsignada;
import modelo.dto.NotificacionTareaRepetitivaAsignada;
import modelo.dto.Tarea;
import modelo.dto.TareaAsignada;
import modelo.dto.TareaRepetitiva;
import modelo.dto.TareaRepetitivaAsignada;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
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
import org.zkoss.zkmax.zul.Navbar;
import org.zkoss.zkmax.zul.Navitem;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

public class ControladorControlSupervisiones extends
		SelectorComposer<Component> {

	// Wire Tareas Asignadas
	@Wire
	private Panel panelTareasAsignadas;
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

	// Wire Tarea Repetitivas asignadas
	@Wire
	private Panel panelTareasRepetitivasAsignadas;
	@Wire
	private Grid gridTareasRepetitivasPendientes;
	@Wire
	private Grid gridTareasRepetitivasEnMarcha;
	@Wire
	private Grid gridRepetitivasTodas;
	@Wire
	private Grid gridTareasRepetitivasPausadas;
	@Wire
	private Grid gridTareasRepetitivasCulminadas;
	@Wire
	private Grid gridTareasRepetitivasAprobadas;
	@Wire
	private Div divTareasRepetitivasPendientes;
	@Wire
	private Div divTareasRepetitivasEnMarcha;
	@Wire
	private Div divRepetitivasTodas;
	@Wire
	private Div divTareasRepetitivasPausadas;
	@Wire
	private Div divTareasRepetitivasCulminadas;
	@Wire
	private Div divTareasRepetitivasAprobadas;
	@Wire
	private Navitem navItemTareasAsignadasTodas;
	@Wire
	private Navbar navbarTareasAsignadas;
	@Wire
	private Navitem navItemTareasRepetitivasAsignadasTodas;
	@Wire
	private Navbar navbarTareasRepetitivasAsignadas;
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

	private UsuarioDAO usuarioDAO = new UsuarioDAO();

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private TareaDAO tareaDAO = new TareaDAO();
	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private EstatusTareaAsignada estatus = new EstatusTareaAsignada();
	private CargoEmpleadoDAO cargoEmpleadoDAO = new CargoEmpleadoDAO();

	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

	private TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();
	private BitacoraTareaAsignadaDAO bitacoraTareaAsignadaDAO = new BitacoraTareaAsignadaDAO();
	private NotificacionTareaAsignadaDAO notificacionTareaAsignadaDAO = new NotificacionTareaAsignadaDAO();

	private BitacoraTareaRepetitivaAsignadaDAO bitacoraTareaRepetitivaAsignadaDAO = new BitacoraTareaRepetitivaAsignadaDAO();
	private TareaRepetitivaAsignadaDAO tareaRepetitivaAsignadaDAO = new TareaRepetitivaAsignadaDAO();
	private NotificacionTareaRepetitivaAsignadaDAO notificacionTareaRepetitivaAsignadaDAO = new NotificacionTareaRepetitivaAsignadaDAO();

	private ListModel<TareaAsignada> listaTareasAsignadasEnMarchaModel;
	private ListModel<TareaAsignada> listaTareasAsignadasPendientesModel;
	private ListModel<TareaAsignada> listaTareasAsignadasPausadasModel;
	private ListModel<TareaAsignada> listaTareasAsignadasAprobadasModel;
	private ListModel<TareaAsignada> listaTareasAsignadasCulminadasModel;
	private ListModel<TareaAsignada> listaTareasAsignadasModel;

	private ListModel<BitacoraTareaAsignada> listaBitacoraTareaAsignadaModel;
	private List<BitacoraTareaAsignada> bitacorasTareaAsignada;
	private ListModel<BitacoraTareaRepetitivaAsignada> listaBitacoraTareaRepetitivaAsignadasModel;
	private List<BitacoraTareaRepetitivaAsignada> bitacorasTareaRepetitivaAsignadas;

	private ListModel<TareaRepetitivaAsignada> listaTareasRepetitivasAsignadasEnMarchaModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasRepetitivasAsignadasPendientesModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasRepetitivasAsignadasPausadasModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasRepetitivasAsignadasAprobadasModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasRepetitivasAsignadasCulminadasModel;
	private ListModel<TareaRepetitivaAsignada> listaTareasRepetitivasAsignadasModel;

	private ListModel<CargoEmpleado> listaEmpleadosCargoModel;
	private ListModel<Tarea> listaTareasModel;

	private List<TareaAsignada> tareasAsignadasPendientes;
	private List<TareaAsignada> tareasAsignadasEnMarcha;
	private List<TareaAsignada> tareasAsignadasPausadas;
	private List<TareaAsignada> tareasAsignadasAprobadas;
	private List<TareaAsignada> tareasAsignadasCulminadas;
	private List<TareaAsignada> tareasAsignadas;

	private List<TareaRepetitivaAsignada> tareasRepetitivasAsignadasPendientes;
	private List<TareaRepetitivaAsignada> tareasRepetitivasAsignadasEnMarcha;
	private List<TareaRepetitivaAsignada> tareasRepetitivasAsignadasPausadas;
	private List<TareaRepetitivaAsignada> tareasRepetitivasAsignadasAprobadas;
	private List<TareaRepetitivaAsignada> tareasRepetitivasAsignadasCulminadas;
	private List<TareaRepetitivaAsignada> tareasRepetitivasAsignadas;

	private List<CargoEmpleado> empleadosCargo = new ArrayList<CargoEmpleado>();
	private List<Tarea> tareas = new ArrayList<Tarea>();

	private static final String footerMessageTareasEnMarcha = "Total de Tareas Asignadas En Marcha: %d";
	private static final String footerMessageTareasPendientes = "Total de Tareas Asignadas Pendientes: %d";
	private static final String footerMessageTareasPausadas = "Total de Tareas Asignadas Pausadas: %d";
	private static final String footerMessageTareasCulminadas = "Total de Tareas Asignadas Culminadas: %d";
	private static final String footerMessageTareasAprobadas = "Total de Tareas Asignadas Aprobadas: %d";
	private static final String footerMessageTareas = "Total de Tareas Asignadas: %d";

	private static final String footerMessageTareasRepetitivasEnMarcha = "Total de Tareas Repetitivas Asignadas En Marcha: %d";
	private static final String footerMessageTareasRepetitivasPendientes = "Total de Tareas Repetitivas Asignadas Pendientes: %d";
	private static final String footerMessageTareasRepetitivasPausadas = "Total de Tareas Repetitivas Asignadas Pausadas: %d";
	private static final String footerMessageTareasRepetitivasCulminadas = "Total de Tareas Repetitivas Asignadas Culminadas: %d";
	private static final String footerMessageTareasRepetitivasAprobadas = "Total de Tareas Repetitivas Asignadas Aprobadas: %d";
	private static final String footerMessageTareasRepetitivas = "Total de Tareas Repetitivas Asignadas: %d";

	public ControladorControlSupervisiones() {
		tareasAsignadasEnMarcha = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "EN MARCHA");
		tareasAsignadasPendientes = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "POR INICIAR");
		tareasAsignadasPausadas = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "PAUSADA");
		tareasAsignadasCulminadas = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "CULMINADA");
		tareasAsignadasAprobadas = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "APROBADA");
		tareasAsignadas = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternos(usuarioFiltro.getId());

		tareasRepetitivasAsignadasEnMarcha = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "EN MARCHA");
		tareasRepetitivasAsignadasPendientes = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "POR INICIAR");
		tareasRepetitivasAsignadasPausadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "PAUSADA");
		tareasRepetitivasAsignadasCulminadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "CULMINADA");
		tareasRepetitivasAsignadasAprobadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "APROBADA");
		tareasRepetitivasAsignadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternos(usuarioFiltro
						.getId());

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

	@Listen("onClick=#navItemAsignacion")
	public void visiblePanelAsignacion() {
		panelTareasRepetitivasAsignadas.setVisible(false);
		panelTareasAsignadas.setVisible(true);
		navbarTareasAsignadas.selectItem(navItemTareasAsignadasTodas);
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(true);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);

		divTareasRepetitivasPendientes.setVisible(false);
		divTareasRepetitivasEnMarcha.setVisible(false);
		divRepetitivasTodas.setVisible(false);
		divTareasRepetitivasPausadas.setVisible(false);
		divTareasRepetitivasCulminadas.setVisible(false);
		divTareasRepetitivasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemRepetitivas")
	public void visiblePanelRepetitivas() {
		panelTareasRepetitivasAsignadas.setVisible(true);
		panelTareasAsignadas.setVisible(false);
		navbarTareasRepetitivasAsignadas
				.selectItem(navItemTareasRepetitivasAsignadasTodas);
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);

		divTareasRepetitivasPendientes.setVisible(false);
		divTareasRepetitivasEnMarcha.setVisible(false);
		divRepetitivasTodas.setVisible(true);
		divTareasRepetitivasPausadas.setVisible(false);
		divTareasRepetitivasCulminadas.setVisible(false);
		divTareasRepetitivasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasAsignadasTodas")
	public void visibleListaTodas() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(true);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasAsignadasPorIniciar")
	public void visibleListaPendientes() {
		divTareasPendientes.setVisible(true);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasAsignadasEnMarcha")
	public void visibleListaEnMarcha() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(true);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasAsignadasPausadas")
	public void visibleListaPausadas() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(true);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasAsignadasCulminadas")
	public void visibleListaCulminadas() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(true);
		divTareasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasAsignadasAprobadas")
	public void visibleListaAprobadas() {
		divTareasPendientes.setVisible(false);
		divTareasEnMarcha.setVisible(false);
		divTodas.setVisible(false);
		divTareasPausadas.setVisible(false);
		divTareasCulminadas.setVisible(false);
		divTareasAprobadas.setVisible(true);
	}

	// Tareas repetitivas asignadas

	public ListModel<TareaRepetitivaAsignada> getTareasRepetitivasAsignadasPendientes() {
		listaTareasRepetitivasAsignadasPendientesModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasRepetitivasAsignadasPendientes);
		return listaTareasRepetitivasAsignadasPendientesModel;
	}

	public String getFooterMessageTareasRepetitivasPendientes() {
		return String.format(footerMessageTareasRepetitivasPendientes,
				tareasRepetitivasAsignadasPendientes.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasRepetitivasAsignadasEnMarcha() {
		listaTareasRepetitivasAsignadasEnMarchaModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasRepetitivasAsignadasEnMarcha);
		return listaTareasRepetitivasAsignadasEnMarchaModel;
	}

	public String getFooterMessageTareasRepetitivasEnMarcha() {
		return String.format(footerMessageTareasRepetitivasEnMarcha,
				tareasRepetitivasAsignadasEnMarcha.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasRepetitivasAsignadasPausadas() {
		listaTareasRepetitivasAsignadasPausadasModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasRepetitivasAsignadasPausadas);
		return listaTareasRepetitivasAsignadasPausadasModel;
	}

	public String getFooterMessageTareasRepetitivasPausadas() {
		return String.format(footerMessageTareasRepetitivasPausadas,
				tareasRepetitivasAsignadasPausadas.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasRepetitivasAsignadasAprobadas() {
		listaTareasRepetitivasAsignadasAprobadasModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasRepetitivasAsignadasAprobadas);
		return listaTareasRepetitivasAsignadasAprobadasModel;
	}

	public String getFooterMessageTareasRepetitivasAprobadas() {
		return String.format(footerMessageTareasRepetitivasAprobadas,
				tareasRepetitivasAsignadasAprobadas.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasRepetitivasAsignadas() {
		listaTareasRepetitivasAsignadasModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasRepetitivasAsignadas);
		return listaTareasRepetitivasAsignadasModel;
	}

	public String getFooterMessageTareasRepetitivas() {
		return String.format(footerMessageTareasRepetitivas,
				tareasRepetitivasAsignadas.size());
	}

	public ListModel<TareaRepetitivaAsignada> getTareasRepetitivasAsignadasCulminadas() {
		listaTareasRepetitivasAsignadasCulminadasModel = new ListModelList<TareaRepetitivaAsignada>(
				tareasRepetitivasAsignadasCulminadas);
		return listaTareasRepetitivasAsignadasCulminadasModel;
	}

	public String getFooterMessageTareasRepetitivasCulminadas() {
		return String.format(footerMessageTareasRepetitivasCulminadas,
				tareasRepetitivasAsignadasCulminadas.size());
	}

	@Listen("onClick=#navItemTareasRepetitivasAsignadasTodas")
	public void visibleListaRepetitivasTodas() {
		divTareasRepetitivasPendientes.setVisible(false);
		divTareasRepetitivasEnMarcha.setVisible(false);
		divRepetitivasTodas.setVisible(true);
		divTareasRepetitivasPausadas.setVisible(false);
		divTareasRepetitivasCulminadas.setVisible(false);
		divTareasRepetitivasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasRepetitivasAsignadasPorIniciar")
	public void visibleListaRepetitivasPendientes() {
		divTareasRepetitivasPendientes.setVisible(true);
		divTareasRepetitivasEnMarcha.setVisible(false);
		divRepetitivasTodas.setVisible(false);
		divTareasRepetitivasPausadas.setVisible(false);
		divTareasRepetitivasCulminadas.setVisible(false);
		divTareasRepetitivasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasRepetitivasAsignadasEnMarcha")
	public void visibleListaRepetitivasEnMarcha() {
		divTareasRepetitivasPendientes.setVisible(false);
		divTareasRepetitivasEnMarcha.setVisible(true);
		divRepetitivasTodas.setVisible(false);
		divTareasRepetitivasPausadas.setVisible(false);
		divTareasRepetitivasCulminadas.setVisible(false);
		divTareasRepetitivasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasRepetitivasAsignadasPausadas")
	public void visibleListaRepetitivasPausadas() {
		divTareasRepetitivasPendientes.setVisible(false);
		divTareasRepetitivasEnMarcha.setVisible(false);
		divRepetitivasTodas.setVisible(false);
		divTareasRepetitivasPausadas.setVisible(true);
		divTareasRepetitivasCulminadas.setVisible(false);
		divTareasRepetitivasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasRepetitivasAsignadasCulminadas")
	public void visibleListaRepetitivasCulminadas() {
		divTareasRepetitivasPendientes.setVisible(false);
		divTareasRepetitivasEnMarcha.setVisible(false);
		divRepetitivasTodas.setVisible(false);
		divTareasRepetitivasPausadas.setVisible(false);
		divTareasRepetitivasCulminadas.setVisible(true);
		divTareasRepetitivasAprobadas.setVisible(false);
	}

	@Listen("onClick=#navItemTareasRepetitivasAsignadasAprobadas")
	public void visibleListaRepetitivasAprobadas() {
		divTareasRepetitivasPendientes.setVisible(false);
		divTareasRepetitivasEnMarcha.setVisible(false);
		divRepetitivasTodas.setVisible(false);
		divTareasRepetitivasPausadas.setVisible(false);
		divTareasRepetitivasCulminadas.setVisible(false);
		divTareasRepetitivasAprobadas.setVisible(true);

	}

	// Panel Tareas Asignadas
	// Asignar Tarea
	@Listen("onClick=#btnAsignarTarea; onClick=#btnAsignarTareaPendiente; onClick=#btnAsignarTareaEnMarcha; onClick=#btnAsignarTareaPausada; onClick=#btnAsignarTareaCulminada; onClick=#btnAsignarTareaAprobada")
	public void asignarrTarea() {
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/AsignarTarea.zul", null, null);
		window.doModal();
	}

	// Consultar Tareas del Grid Todas
	@Listen("onClick=#btnConsultarTarea")
	public void consultarTareaAsignadaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar", gridTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			String nombreEstatus = tareaAsignadaFiltro
					.getEstatusTareaAsignada().getNombre();
			switch (nombreEstatus) {
			case "CULMINADA":
				consultarTareaAsignadaCulminada(tareaAsignadaFiltro);
				break;
			case "APROBADA":
				consultarTareaAsignadaAprobada(tareaAsignadaFiltro);
				break;
			case "ELIMINADA":
				consultarTareaAsignadaAprobada(tareaAsignadaFiltro);
				break;
			default:
				consultarTareaAsignadaModificar(tareaAsignadaFiltro);
				break;
			}

		}
	}

	// Modificar Tareas del Grid Todas
	@Listen("onClick=#btnModificarTarea")
	public void modificarTareaAsignadaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Modificar", gridTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			String nombreEstatus = tareaAsignadaFiltro
					.getEstatusTareaAsignada().getNombre();
			switch (nombreEstatus) {
			case "POR INICIAR":
				modificarTareaAsignadaPorIniciar(tareaAsignadaFiltro);
				break;
			case "PAUSADA":
				modificarTareaAsignadaEnMarchaPausada(tareaAsignadaFiltro);
				break;
			case "EN MARCHA":
				modificarTareaAsignadaEnMarchaPausada(tareaAsignadaFiltro);
				break;
			case "CULMINADA":
				modificarTareaAsignadaCulminada(tareaAsignadaFiltro);
				break;
			default:
				showNotifyWarning(
						"Sólo las tareas con Estatus\"POR INICIAR\", \"EN MARCHA\", \"PAUSADA\"  se pueden editar, \"CULMINADA\" para aprobar o rechazar",
						panelTareasAsignadas);
				break;
			}

		}
	}

	// Eliminar Tareas del Grid Todas
	@Listen("onClick=#btnEliminarTarea")
	public void eliminarTareaAsignadaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Eliminar", gridTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			String nombreEstatus = tareaAsignadaFiltro
					.getEstatusTareaAsignada().getNombre();
			switch (nombreEstatus) {
			case "POR INICIAR":
				eliminarTareaAsignada(tareaAsignadaFiltro);
				break;
			default:
				showNotifyWarning(
						"Sólo las tareas con Estatus\"POR INICIAR\" se pueden eliminar",
						panelTareasAsignadas);
				break;
			}

		}
	}

	// Modificar Tareas del Grid Pendientes
	@Listen("onClick=#btnModificarTareaPendiente")
	public void modificarTareaAsignadaPendiente() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasPendientes);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Modificar",
					gridTareasPendientes);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			modificarTareaAsignadaPorIniciar(tareaAsignadaFiltro);
		}

	}

	// Eliminar Tareas del Grid Pendiente
	@Listen("onClick=#btnEliminarTareaPendiente")
	public void eliminarTareaAsignadaPendiente() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasPendientes);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Eliminar",
					gridTareasPendientes);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			eliminarTareaAsignada(tareaAsignadaFiltro);
		}
	}

	// Consultar Tareas del Grid Pendientes
	@Listen("onClick=#btnConsultarTareaPendiente")
	public void consultarTareaAsignadaPendiente() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasPendientes);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasPendientes);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			consultarTareaAsignadaModificar(tareaAsignadaFiltro);
		}
	}

	// Modificar Tareas del Grid En Marcha
	@Listen("onClick=#btnModificarTareaEnMarcha")
	public void modificarTareaAsignadaEnMarcha() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasEnMarcha);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Modificar",
					gridTareasEnMarcha);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			modificarTareaAsignadaEnMarchaPausada(tareaAsignadaFiltro);

		}
	}

	// Consultar Tareas del Grid En Marcha
	@Listen("onClick=#btnConsultarTareaEnMarcha")
	public void consultarTareaAsignadaEnMarcha() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasEnMarcha);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasEnMarcha);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			consultarTareaAsignadaModificar(tareaAsignadaFiltro);

		}
	}

	// Modificar Tareas del Grid Pausada
	@Listen("onClick=#btnModificarTareaPausada")
	public void modificarTareaAsignadaPausada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasPausadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Modificar",
					gridTareasPausadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			modificarTareaAsignadaEnMarchaPausada(tareaAsignadaFiltro);
		}
	}

	// Consultar Tareas del Grid Pausadas
	@Listen("onClick=#btnConsultarTareaPausada")
	public void consultarTareaAsignadaPausada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasPausadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasPausadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			consultarTareaAsignadaModificar(tareaAsignadaFiltro);
		}
	}

	// Modificar Tareas del Grid Tarea Culminada
	@Listen("onClick=#btnModificarTareaCulminada")
	public void modificarTareaAsignadaCulminada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasCulminadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Modificar",
					gridTareasCulminadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			modificarTareaAsignadaCulminada(tareaAsignadaFiltro);
		}
	}

	// Consultar Tareas del Grid Tarea Culminada
	@Listen("onClick=#btnConsultarTareaCulminada")
	public void consultarTareaAsignadaCulminada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasCulminadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasCulminadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);
			consultarTareaAsignadaCulminada(tareaAsignadaFiltro);
		}
	}

	// Consultar Tareas del Grid Todas
	@Listen("onClick=#btnConsultarTareaAprobada")
	public void consultarTareaAsignadaAprobada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasAprobadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasAprobadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaAsignada tareaAsignadaFiltro = tareaAsignadaDAO
					.obtenerTareaAsignada(id);

			consultarTareaAsignadaAprobada(tareaAsignadaFiltro);
		}
	}

	public void consultarTareaAsignadaModificar(
			TareaAsignada tareaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaAsignadaFiltro.getCargoEmpleado()
				.getEmpleado().getNombres()
				+ " "
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getApellidos()
				+ " (V-"
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getCedula() + ")";
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();
		String fechaTarea;
		tareas = tareaDAO.obtenerTareasActivasPorEmpresa(usuarioFiltro
				.getEmpresa().getId());
		listaTareasModel = new ListModelList<Tarea>(tareas);
		empleadosCargo = cargoEmpleadoDAO
				.obtenerUsuariosEmpleadosActivosPorCargo(tareaAsignadaFiltro
						.getCargoEmpleado().getCargo().getId());
		listaEmpleadosCargoModel = new ListModelList<CargoEmpleado>(
				empleadosCargo);
		((ListModelList<CargoEmpleado>) listaEmpleadosCargoModel)
				.setMultiple(false);

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

		for (int i = 0; i < empleadosCargo.size(); i++) {

			if (empleadosCargo.get(i).getEmpleado().getId() == tareaAsignadaFiltro
					.getCargoEmpleado().getId()) {
				((AbstractListModel<CargoEmpleado>) listaEmpleadosCargoModel)
						.addToSelection(((List<CargoEmpleado>) listaEmpleadosCargoModel)
								.get(i));
			}

		}

		for (int i = 0; i < tareas.size(); i++) {
			if (tareas.get(i).getId() == tareaAsignadaFiltro.getTarea().getId()) {
				((AbstractListModel<Tarea>) listaTareasModel)
						.addToSelection(((List<Tarea>) listaTareasModel).get(i));
			}
		}
		BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
				tareaAsignadaFiltro, usuarioFiltro, new Date(),
				"Tarea Consultada por el Supervisor", "CONSULTAR");
		bitacoraTareaAsignadaDAO
				.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
		bitacorasTareaAsignada = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacoraTareaAsignadaModel = new ListModelList<BitacoraTareaAsignada>(
				bitacorasTareaAsignada);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaAsignada.size();
		arguments.put("bitacoras", listaBitacoraTareaAsignadaModel);
		arguments.put("footer", footer);

		arguments.put("supervisor", supervisor);
		arguments.put("titulo", "Consultar Tarea Asignada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnEditarTarea", true);
		arguments.put("btnSalir", true);
		arguments.put("divLabelIndicaciones", true);
		arguments.put("empleados", listaEmpleadosCargoModel);
		arguments.put("tareas", listaTareasModel);
		arguments.put("divConsulta", true);
		arguments.put("fechaTarea", fechaTarea);
		if (tareaAsignadaFiltro.getEstatusTareaAsignada().getNombre()
				.equals("PAUSADA")
				|| tareaAsignadaFiltro.getEstatusTareaAsignada().getNombre()
						.equals("EN MARCHA")) {
			arguments.put("divFechaInicio", true);
		}

		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);

		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaAsignadaSupervisor.zul", null,
				arguments);

		window.doModal();
	}

	public void consultarTareaAsignadaAprobada(TareaAsignada tareaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaAsignadaFiltro.getCargoEmpleado()
				.getEmpleado().getNombres()
				+ " "
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getApellidos()
				+ " (V-"
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getCedula() + ")";
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();

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
			miduracion = miduracion + minutos2 + " Minutos ";
		}
		try {
			fechaTarea = formatoFecha.format(tareaAsignadaFiltro.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
				tareaAsignadaFiltro, usuarioFiltro, new Date(),
				"Tarea Consultada por el Supervisor", "CONSULTAR");
		bitacoraTareaAsignadaDAO
				.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);

		bitacorasTareaAsignada = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacoraTareaAsignadaModel = new ListModelList<BitacoraTareaAsignada>(
				bitacorasTareaAsignada);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaAsignada.size();
		arguments.put("bitacoras", listaBitacoraTareaAsignadaModel);
		arguments.put("footer", footer);

		arguments.put("supervisor", supervisor);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", "Consultar Tarea Asignada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnSalir", true);
		arguments.put("divLabelIndicaciones", true);
		arguments.put("divConsulta", true);

		arguments.put("divFechaInicio", true);
		arguments.put("divFechaFinalizacion", true);
		arguments.put("divCalificacion", true);
		arguments.put("divDuracionTarea", true);
		arguments.put("divMiDuracionTarea", true);
		arguments.put("duracion", duracion);
		arguments.put("eficiencia", miduracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaAsignadaSupervisor.zul", null,
				arguments);

		window.doModal();
	}

	public void consultarTareaAsignadaCulminada(
			TareaAsignada tareaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaAsignadaFiltro.getCargoEmpleado()
				.getEmpleado().getNombres()
				+ " "
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getApellidos()
				+ " (V-"
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getCedula() + ")";
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();
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
				"Tarea Consultada por el Supervisor", "CONSULTAR");
		bitacoraTareaAsignadaDAO
				.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);

		bitacorasTareaAsignada = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacoraTareaAsignadaModel = new ListModelList<BitacoraTareaAsignada>(
				bitacorasTareaAsignada);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaAsignada.size();
		arguments.put("bitacoras", listaBitacoraTareaAsignadaModel);
		arguments.put("footer", footer);

		arguments.put("supervisor", supervisor);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", "Consultar Tarea Asignada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnAprobarTarea", true);
		arguments.put("btnRechazarTarea", true);
		arguments.put("divLabelIndicaciones", true);
		arguments.put("btnSalir", true);
		arguments.put("divConsulta", true);

		arguments.put("divFechaInicio", true);
		arguments.put("divFechaFinalizacion", true);
		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaAsignadaSupervisor.zul", null,
				arguments);

		window.doModal();
	}

	public void modificarTareaAsignadaCulminada(
			TareaAsignada tareaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaAsignadaFiltro.getCargoEmpleado()
				.getEmpleado().getNombres()
				+ " "
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getApellidos()
				+ " (V-"
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getCedula() + ")";
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();
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
		bitacorasTareaAsignada = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacoraTareaAsignadaModel = new ListModelList<BitacoraTareaAsignada>(
				bitacorasTareaAsignada);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaAsignada.size();
		arguments.put("bitacoras", listaBitacoraTareaAsignadaModel);
		arguments.put("footer", footer);

		arguments.put("supervisor", supervisor);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", "Modificar Tarea Asignada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-edit");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnAprobarTarea", true);
		arguments.put("btnRechazarTarea", true);
		arguments.put("divLabelIndicaciones", true);
		arguments.put("btnCancelarModificacion", true);
		arguments.put("divConsulta", true);

		arguments.put("divFechaInicio", true);
		arguments.put("divFechaFinalizacion", true);

		arguments.put("divDuracionTarea", true);

		arguments.put("duracion", duracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaAsignadaSupervisor.zul", null,
				arguments);
		window.doModal();
	}

	public void modificarTareaAsignadaPorIniciar(
			TareaAsignada tareaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaAsignadaFiltro.getCargoEmpleado()
				.getEmpleado().getNombres()
				+ " "
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getApellidos()
				+ " (V-"
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getCedula() + ")";
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();
		String fechaTarea;
		tareas = tareaDAO.obtenerTareasActivasPorEmpresa(usuarioFiltro
				.getEmpresa().getId());
		listaTareasModel = new ListModelList<Tarea>(tareas);
		empleadosCargo = cargoEmpleadoDAO
				.obtenerUsuariosEmpleadosActivosPorCargo(tareaAsignadaFiltro
						.getCargoEmpleado().getCargo().getId());
		listaEmpleadosCargoModel = new ListModelList<CargoEmpleado>(
				empleadosCargo);
		((ListModelList<CargoEmpleado>) listaEmpleadosCargoModel)
				.setMultiple(false);

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
		for (int i = 0; i < empleadosCargo.size(); i++) {

			if (empleadosCargo.get(i).getEmpleado().getId() == tareaAsignadaFiltro
					.getCargoEmpleado().getId()) {
				((AbstractListModel<CargoEmpleado>) listaEmpleadosCargoModel)
						.addToSelection(((List<CargoEmpleado>) listaEmpleadosCargoModel)
								.get(i));
			}

		}

		for (int i = 0; i < tareas.size(); i++) {
			if (tareas.get(i).getId() == tareaAsignadaFiltro.getTarea().getId()) {
				((AbstractListModel<Tarea>) listaTareasModel)
						.addToSelection(((List<Tarea>) listaTareasModel).get(i));
			}
		}

		bitacorasTareaAsignada = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacoraTareaAsignadaModel = new ListModelList<BitacoraTareaAsignada>(
				bitacorasTareaAsignada);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaAsignada.size();
		arguments.put("bitacoras", listaBitacoraTareaAsignadaModel);
		arguments.put("footer", footer);

		arguments.put("supervisor", supervisor);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", "Modificar Tarea Asignada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-edit");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divModificar", true);
		arguments.put("empleados", listaEmpleadosCargoModel);
		arguments.put("tareas", listaTareasModel);

		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);

		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaAsignadaSupervisor.zul", null,
				arguments);
		window.doModal();
	}

	public void modificarTareaAsignadaEnMarchaPausada(
			TareaAsignada tareaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaAsignadaFiltro.getCargoEmpleado()
				.getEmpleado().getNombres()
				+ " "
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getApellidos()
				+ " (V-"
				+ tareaAsignadaFiltro.getCargoEmpleado().getEmpleado()
						.getCedula() + ")";
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();
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

		bitacorasTareaAsignada = bitacoraTareaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaAsignadaFiltro.getId());
		listaBitacoraTareaAsignadaModel = new ListModelList<BitacoraTareaAsignada>(
				bitacorasTareaAsignada);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaAsignada.size();
		arguments.put("bitacoras", listaBitacoraTareaAsignadaModel);
		arguments.put("footer", footer);

		arguments.put("supervisor", supervisor);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", "Modificar Tarea Asignada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-edit");
		arguments.put("tareaAsignada", tareaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnGuardarModificacion", true);
		arguments.put("btnCancelarModificacion", true);
		arguments.put("divTextIndicaciones", true);
		arguments.put("divConsulta", true);

		arguments.put("divFechaInicio", true);
		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaAsignadaSupervisor.zul", null,
				arguments);
		showNotifyWarning(
				"Sólo puede modificar las indicaciones de la tarea, ya que ha sido iniciada por el empleado.",
				window);
		window.doModal();
	}

	public void eliminarTareaAsignada(final TareaAsignada tareaAsignadaFiltro) {
		estatus = estatusTareaAsignadaDAO.obtenerEstatusPorNombre("ELIMINADA");
		tareaAsignadaFiltro.setEstatusTareaAsignada(estatus);
		Messagebox.show("¿Seguro que desea eliminar la tarea asignada?",
				"Eliminar Tarea Asignada", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							tareaAsignadaDAO
									.actualizarTareaAsignada(tareaAsignadaFiltro);
							BitacoraTareaAsignada bitacoraTareaAsignada = new BitacoraTareaAsignada(
									tareaAsignadaFiltro, usuarioFiltro,
									new Date(),
									"Tarea Eliminada por el Supervisor",
									"ELIMINAR");

							bitacoraTareaAsignadaDAO
									.registrarBitacoraTareaAsignada(bitacoraTareaAsignada);
							String nombreCompleto = usuarioFiltro.getNombres()
									+ " " + usuarioFiltro.getApellidos();
							String descripcion = "El Supervisor "
									+ nombreCompleto
									+ " (V-"
									+ usuarioFiltro.getCedula()
									+ " ) ha eliminado la tarea asignada ("
									+ tareaAsignadaFiltro.getTarea()
											.getNombre()
									+ ") que estaba pautada para el "
									+ tareaAsignadaFiltro.getFecha();
							EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
									.obtenerEstatusNotificacionPorNombre("POR LEER");
							NotificacionTareaAsignada notificacionTareaAsignada = new NotificacionTareaAsignada(
									descripcion, tareaAsignadaFiltro
											.getCargoEmpleado().getEmpleado(),
									bitacoraTareaAsignada, estatusNotificacion);
							notificacionTareaAsignadaDAO
									.registrarNotificacionTareaAsignada(notificacionTareaAsignada);
							BindUtils.postGlobalCommand(null, null,
									"actualizarTablaAsignadas", null);
							showNotifyInfo("Tarea Eliminada Exitosamente",
									panelTareasAsignadas);

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
	public void actualizarTablaAsignadas() {
		tareasAsignadasEnMarcha = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "EN MARCHA");
		tareasAsignadasPendientes = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "POR INICIAR");
		tareasAsignadasPausadas = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "PAUSADA");
		tareasAsignadasCulminadas = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "CULMINADA");
		tareasAsignadasAprobadas = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "APROBADA");
		tareasAsignadas = tareaAsignadaDAO
				.obtenerTareasAsignadasSubalternos(usuarioFiltro.getId());
	}

	// Panel de Tareas Repetitivas Asignadas

	// Consultar Tareas del Grid Todas Repetitivas
	@Listen("onClick=#btnConsultarTareaRepetitiva")
	public void consultarTareaRepetitivaAsignadaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridRepetitivasTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridRepetitivasTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			String nombreEstatus = tareaRepetitivaAsignadaFiltro
					.getEstatusTareaAsignada().getNombre();
			switch (nombreEstatus) {
			case "CULMINADA":
				consultarTareaRepetitivaAsignadaCulminada(tareaRepetitivaAsignadaFiltro);
				break;
			default:
				consultarTareaRepetitivaAsignada(tareaRepetitivaAsignadaFiltro);
				break;
			}

		}
	}

	// Modificar Tareas del Grid Todas Repetitivas
	@Listen("onClick=#btnModificarTareaRepetitiva")
	public void modificarTareaRepetitivaAsignadaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridRepetitivasTodas);
		if (indexSeleccionado == null) {
			showNotify(
					"Debe seleccionar una Tarea Culminada a aprobar o rechazar",
					gridRepetitivasTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			String nombreEstatus = tareaRepetitivaAsignadaFiltro
					.getEstatusTareaAsignada().getNombre();

			switch (nombreEstatus) {
			case "CULMINADA":
				modificarTareaRepetitivaAsignadaCulminada(tareaRepetitivaAsignadaFiltro);
				break;
			default:
				showNotifyWarning(
						"Sólo se pueden aprobar o rechazar las tareas repetitivas con estatus \"CULMINADA\"",
						gridRepetitivasTodas);
			}

		}
	}

	// Rechazar Tareas del Grid Todas Repetitivas
	@Listen("onClick=#btnRechazarTareaRepetitiva")
	public void rechazarTareaRepetitivaAsignadaTodas() {
		String indexSeleccionado = getIdSelectedRowTarea(gridRepetitivasTodas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Culminada a Rechazar",
					gridRepetitivasTodas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			String nombreEstatus = tareaRepetitivaAsignadaFiltro
					.getEstatusTareaAsignada().getNombre();

			switch (nombreEstatus) {
			case "CULMINADA":
				rechazarTareaRepetitivaAsignadaCulminada(tareaRepetitivaAsignadaFiltro);
				break;
			default:
				showNotifyWarning(
						"Sólo se pueden rechazar las tareas repetitivas con estatus \"CULMINADA\"",
						gridRepetitivasTodas);
			}

		}
	}

	// Consultar Tareas del Grid Repetitivas Pendientes
	@Listen("onClick=#btnConsultarTareaRepetitivaPendiente")
	public void consultarTareaRepetitivaAsignadaPendiente() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasRepetitivasPendientes);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasRepetitivasPendientes);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaRepetitivaAsignada(tareaRepetitivaAsignadaFiltro);

		}
	}

	// Consultar Tareas del Grid Repetitivas En Marcha
	@Listen("onClick=#btnConsultarTareaRepetitivaEnMarcha")
	public void consultarTareaRepetitivaAsignadaEnMarcha() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasRepetitivasEnMarcha);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasRepetitivasEnMarcha);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaRepetitivaAsignada(tareaRepetitivaAsignadaFiltro);

		}
	}

	// Consultar Tareas del Grid Repetitivas Pausadas
	@Listen("onClick=#btnConsultarTareaRepetitivaPausada")
	public void consultarTareaRepetitivaAsignadaPausada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasRepetitivasPausadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasRepetitivasPausadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaRepetitivaAsignada(tareaRepetitivaAsignadaFiltro);

		}
	}

	// Modificar Tarea Repetiva Asignada Culminadas
	@Listen("onClick=#btnConsultarTareaRepetitivaCulminada")
	public void consultarTareaRepetitivaAsignadaCulminada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasRepetitivasCulminadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasRepetitivasCulminadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaRepetitivaAsignadaCulminada(tareaRepetitivaAsignadaFiltro);

		}
	}

	// Modificar Tareas del Grid Repetitivas Culminadas
	@Listen("onClick=#btnModificarTareaRepetitivaCulminada")
	public void modificarTareaRepetitivaAsignadaCulminada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasRepetitivasCulminadas);
		if (indexSeleccionado == null) {
			showNotify(
					"Debe seleccionar una Tarea Culminada a aprobar o rechazar.",
					gridTareasRepetitivasCulminadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			modificarTareaRepetitivaAsignadaCulminada(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Aprobar Tareas del Grid Repetitivas Culminadas
	@Listen("onClick=#btnRechazarTareaRepetitivaCulminada")
	public void rechazarTareaRepetitivaAsignadaCulminada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasRepetitivasCulminadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea Culminada a Rechazar.",
					gridTareasRepetitivasCulminadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			rechazarTareaRepetitivaAsignadaCulminada(tareaRepetitivaAsignadaFiltro);
		}
	}

	// Consultar Tareas del Grid Repetitivas Aprobadas
	@Listen("onClick=#btnConsultarTareaRepetitivaAprobada")
	public void consultarTareaRepetitivaAsignadaAprobada() {
		String indexSeleccionado = getIdSelectedRowTarea(gridTareasRepetitivasAprobadas);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una Tarea a Consultar",
					gridTareasRepetitivasAprobadas);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro = tareaRepetitivaAsignadaDAO
					.obtenerTareaRepetitivaAsignada(id);
			consultarTareaRepetitivaAsignada(tareaRepetitivaAsignadaFiltro);

		}
	}

	public void consultarTareaRepetitivaAsignadaCulminada(
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaRepetitivaAsignadaFiltro
				.getEmpleado().getNombres()
				+ " "
				+ tareaRepetitivaAsignadaFiltro.getEmpleado().getApellidos()
				+ " (V-"
				+ tareaRepetitivaAsignadaFiltro.getEmpleado().getCedula() + ")";
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();
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
				"Tarea Consultada por el Supervisor", "CONSULTAR");
		bitacoraTareaRepetitivaAsignadaDAO
				.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);

		bitacorasTareaRepetitivaAsignadas = bitacoraTareaRepetitivaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaRepetitivaAsignadaFiltro.getId());
		listaBitacoraTareaRepetitivaAsignadasModel = new ListModelList<BitacoraTareaRepetitivaAsignada>(
				bitacorasTareaRepetitivaAsignadas);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaRepetitivaAsignadas.size();
		arguments.put("bitacoras", listaBitacoraTareaRepetitivaAsignadasModel);
		arguments.put("footer", footer);

		arguments.put("supervisor", supervisor);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", "Consultar Tarea Repetitiva Asignada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaRepetitivaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnAprobarTarea", true);
		arguments.put("btnRechazarTarea", true);
		arguments.put("divLabelIndicaciones", true);
		arguments.put("btnSalir", true);

		arguments.put("divFechaInicio", true);
		arguments.put("divFechaFinalizacion", true);

		arguments.put("divDuracionTarea", true);

		arguments.put("duracion", duracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaRepetitivaAsignadaSupervisor.zul",
				null, arguments);

		window.doModal();
	}

	public void consultarTareaRepetitivaAsignada(
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaRepetitivaAsignadaFiltro
				.getEmpleado().getNombres()
				+ " "
				+ tareaRepetitivaAsignadaFiltro.getEmpleado().getApellidos()
				+ " (V-"
				+ tareaRepetitivaAsignadaFiltro.getEmpleado().getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
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
			miduracion = miduracion + minutos2 + " Minutos ";
		}
		try {
			fechaTarea = formatoFecha.format(tareaRepetitivaAsignadaFiltro
					.getFecha());
		} catch (Exception e) {
			fechaTarea = "";
		}
		BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
				tareaRepetitivaAsignadaFiltro, usuarioFiltro, new Date(),
				"Tarea Consultada por el Supervisor", "CONSULTAR");

		bitacoraTareaRepetitivaAsignadaDAO
				.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);

		bitacorasTareaRepetitivaAsignadas = bitacoraTareaRepetitivaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaRepetitivaAsignadaFiltro.getId());
		listaBitacoraTareaRepetitivaAsignadasModel = new ListModelList<BitacoraTareaRepetitivaAsignada>(
				bitacorasTareaRepetitivaAsignadas);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaRepetitivaAsignadas.size();
		arguments.put("bitacoras", listaBitacoraTareaRepetitivaAsignadasModel);
		arguments.put("footer", footer);

		arguments.put("supervisor", supervisor);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", "Consultar Tarea Repetitiva Asignada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-eye");
		arguments.put("tareaAsignada", tareaRepetitivaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("divLabelIndicaciones", true);
		arguments.put("btnSalir", true);
		if (tareaRepetitivaAsignadaFiltro.getEstatusTareaAsignada().getNombre()
				.equals("PAUSADA")
				|| tareaRepetitivaAsignadaFiltro.getEstatusTareaAsignada()
						.getNombre().equals("EN MARCHA")) {
			arguments.put("divFechaInicio", true);
		}
		if (tareaRepetitivaAsignadaFiltro.getEstatusTareaAsignada().getNombre()
				.equals("APROBADA")) {
			arguments.put("divFechaInicio", true);
			arguments.put("divFechaFinalizacion", true);
			arguments.put("divCalificacion", true);

			arguments.put("divMiDuracionTarea", true);

			arguments.put("eficiencia", miduracion);
		}
		arguments.put("divDuracionTarea", true);
		arguments.put("duracion", duracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaRepetitivaAsignadaSupervisor.zul",
				null, arguments);

		window.doModal();
	}

	public void modificarTareaRepetitivaAsignadaCulminada(
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaRepetitivaAsignadaFiltro
				.getEmpleado().getNombres()
				+ " "
				+ tareaRepetitivaAsignadaFiltro.getEmpleado().getApellidos()
				+ " (V-"
				+ tareaRepetitivaAsignadaFiltro.getEmpleado().getCedula() + ")";
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();
		String fechaTarea;
		String duracion = "";
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
		bitacorasTareaRepetitivaAsignadas = bitacoraTareaRepetitivaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaRepetitivaAsignadaFiltro.getId());
		listaBitacoraTareaRepetitivaAsignadasModel = new ListModelList<BitacoraTareaRepetitivaAsignada>(
				bitacorasTareaRepetitivaAsignadas);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaRepetitivaAsignadas.size();
		arguments.put("bitacoras", listaBitacoraTareaRepetitivaAsignadasModel);
		arguments.put("footer", footer);

		arguments.put("supervisor", supervisor);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", "Modificar Tarea Repetitiva Asignada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-edit");
		arguments.put("tareaAsignada", tareaRepetitivaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnAprobarTarea", true);
		arguments.put("btnRechazarTarea", true);
		arguments.put("divLabelIndicaciones", true);
		arguments.put("btnCancelarModificacion", true);

		arguments.put("divFechaInicio", true);
		arguments.put("divFechaFinalizacion", true);
		arguments.put("divCalificacion", false);
		arguments.put("divDuracionTarea", true);
		arguments.put("divMiDuracionTarea", false);
		arguments.put("duracion", duracion);
		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaRepetitivaAsignadaSupervisor.zul",
				null, arguments);
		window.doModal();
	}

	public void rechazarTareaRepetitivaAsignadaCulminada(
			TareaRepetitivaAsignada tareaRepetitivaAsignadaFiltro) {
		String nombreCompletoEmpleado = tareaRepetitivaAsignadaFiltro
				.getEmpleado().getNombres()
				+ " "
				+ tareaRepetitivaAsignadaFiltro.getEmpleado().getApellidos()
				+ " (V-"
				+ tareaRepetitivaAsignadaFiltro.getEmpleado().getCedula() + ")";
		Map<String, Object> arguments = new HashMap<String, Object>();
		String supervisor = usuarioFiltro.getNombres() + " "
				+ usuarioFiltro.getApellidos() + " (V-"
				+ usuarioFiltro.getCedula() + ")";
		String fechaTarea;
		String duracion = "";
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
		bitacorasTareaRepetitivaAsignadas = bitacoraTareaRepetitivaAsignadaDAO
				.obtenerBitacorasPorTarea(tareaRepetitivaAsignadaFiltro.getId());
		listaBitacoraTareaRepetitivaAsignadasModel = new ListModelList<BitacoraTareaRepetitivaAsignada>(
				bitacorasTareaRepetitivaAsignadas);
		String footer = "Total de Movimientos de la tarea "
				+ bitacorasTareaRepetitivaAsignadas.size();
		arguments.put("bitacoras", listaBitacoraTareaRepetitivaAsignadasModel);
		arguments.put("footer", footer);
		arguments.put("supervisor", supervisor);
		arguments.put("fechaTarea", fechaTarea);
		arguments.put("titulo", "Rechazar Tarea Repetitiva Asignada Culminada");
		arguments.put("nombreCompletoEmpleado", nombreCompletoEmpleado);
		arguments.put("icono", "z-icon-times");
		arguments.put("tareaAsignada", tareaRepetitivaAsignadaFiltro);
		arguments.put("divProgreso", true);
		arguments.put("btnGuardarModificacion", true);
		arguments.put("btnCancelarModificacion", true);
		arguments.put("divLabelIndicaciones", true);
		arguments.put("divRechazo", true);
		arguments.put("divSlider", true);

		arguments.put("divFechaInicio", true);
		arguments.put("divFechaFinalizacion", true);
		arguments.put("divCalificacion", false);
		arguments.put("divDuracionTarea", true);
		arguments.put("divMiDuracionTarea", false);
		arguments.put("duracion", duracion);

		Window window = (Window) Executions.getCurrent().createComponents(
				"/vista/consultarEditarTareaRepetitivaAsignadaSupervisor.zul",
				null, arguments);

		window.doModal();
		showNotifyWarning(
				"Debe Ingresar la razón del rechazo y el progreso inicial de la tarea",
				window);
	}

	@GlobalCommand
	@NotifyChange({ "tareasRepetitivasAsignadasEnMarcha",
			"tareasRepetitivasAsignadasPendientes",
			"tareasRepetitivasAsignadasPausadas",
			"tareasRepetitivasAsignadasAprobadas",
			"tareasRepetitivasAsignadasCulminadas",
			"tareasRepetitivasAsignadas",
			"footerMessageTareasRepetitivasPausadas",
			"footerMessageTareasRepetitivasEnMarcha",
			"footerMessageTareasRepetitivasCulminadas",
			"footerMessageTareasRepetitivasPendientes",
			"footerMessageTareasRepetitivasAprobadas",
			"footerMessageTareasRepetitivas" })
	public void actualizarTablaRepetitivas() {
		tareasRepetitivasAsignadasEnMarcha = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "EN MARCHA");
		tareasRepetitivasAsignadasPendientes = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "POR INICIAR");
		tareasRepetitivasAsignadasPausadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "PAUSADA");
		tareasRepetitivasAsignadasCulminadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "CULMINADA");
		tareasRepetitivasAsignadasAprobadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
						usuarioFiltro.getId(), "APROBADA");
		tareasRepetitivasAsignadas = tareaRepetitivaAsignadaDAO
				.obtenerTareasRepetitivasAsignadasSubalternos(usuarioFiltro
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
		Clients.showNotification(msg, "warning", ref, "middle_center", 5000,
				true);
	}

}
