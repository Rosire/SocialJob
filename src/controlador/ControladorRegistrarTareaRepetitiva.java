package controlador;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import modelo.dao.BitacoraTareaRepetitivaAsignadaDAO;
import modelo.dao.CargoDAO;
import modelo.dao.EstatusDAO;
import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.FrecuenciaDAO;
import modelo.dao.NotificacionTareaRepetitivaAsignadaDAO;
import modelo.dao.PrioridadDAO;
import modelo.dao.TareaDAO;
import modelo.dao.TareaRepetitivaAsignadaDAO;
import modelo.dao.TareaRepetitivaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaRepetitivaAsignada;
import modelo.dto.Cargo;
import modelo.dto.Estatus;
import modelo.dto.EstatusNotificacion;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.Frecuencia;
import modelo.dto.NotificacionTareaRepetitivaAsignada;
import modelo.dto.Prioridad;
import modelo.dto.TareaRepetitivaAsignada;
import modelo.dto.Tarea;
import modelo.dto.TareaRepetitiva;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ControladorRegistrarTareaRepetitiva extends
		SelectorComposer<Component> {

	@Wire
	private Combobox cmbCargos;
	@Wire
	private Combobox cmbTareas;
	@Wire
	private Combobox cmbPrioridades;
	@Wire
	private Spinner lblVecesRepetir;
	@Wire
	private Combobox cmbFrecuencias;
	@Wire
	private Textbox lblIndicaciones;
	@Wire
	private Datebox lblFechaInicio;
	@Wire
	private Window windowsRegistarTareaRepetitiva;
	@Wire
	private Spinner lblHoras;
	@Wire
	private Spinner lblMinutos;
	@Wire
	private Div divDuracion;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	private CargoDAO cargoDAO = new CargoDAO();

	private TareaRepetitivaDAO tareaRepetitivaDAO = new TareaRepetitivaDAO();
	private TareaRepetitivaAsignadaDAO tareaRepetitivaAsignadaDAO = new TareaRepetitivaAsignadaDAO();

	private TareaDAO tareaDAO = new TareaDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private FrecuenciaDAO frecuenciaDAO = new FrecuenciaDAO();
	private PrioridadDAO prioridadDAO = new PrioridadDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();
	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();
	private NotificacionTareaRepetitivaAsignadaDAO notificacionTareaRepetitivaAsignadaDAO = new NotificacionTareaRepetitivaAsignadaDAO();
	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private BitacoraTareaRepetitivaAsignadaDAO bitacoraTareaRepetitivaAsignadaDAO = new BitacoraTareaRepetitivaAsignadaDAO();

	private ListModel<Cargo> listaCargosModel;
	private ListModel<Tarea> listaTareasModel;
	private ListModel<Frecuencia> listaSecuenciasModel;
	private ListModel<Prioridad> listaPrioridadesModel;

	public ControladorRegistrarTareaRepetitiva() {
		super();
	}

	public ListModel<Cargo> getCargos() {
		listaCargosModel = new ListModelList<Cargo>(
				cargoDAO.obtenerCargosActivosConSupervisorIguaActualPorEmpresa(
						usuarioFiltro.getId(), usuarioFiltro.getEmpresa()
								.getId()));
		((ListModelList<Cargo>) listaCargosModel).setMultiple(false);

		return listaCargosModel;
	}

	public ListModel<Tarea> getTareas() {
		listaTareasModel = new ListModelList<Tarea>(
				tareaDAO.obtenerTareasActivasPorEmpresa(usuarioFiltro
						.getEmpresa().getId()));
		return listaTareasModel;
	}

	public ListModel<Frecuencia> getSecuencias() {
		listaSecuenciasModel = new ListModelList<Frecuencia>(
				frecuenciaDAO.obtenerFrecuenciasPorEmpresa(usuarioFiltro
						.getEmpresa().getId()));
		return listaSecuenciasModel;
	}

	public ListModel<Prioridad> getPrioridades() {
		listaPrioridadesModel = new ListModelList<Prioridad>(
				prioridadDAO.obtenerPrioridades());
		return listaPrioridadesModel;
	}

	@Listen("onClick=#btnGuardar")
	public void registrarTareaRepetitiva() {

		ListModelList<Object> listaCargo = (ListModelList<Object>) cmbCargos
				.getModel();
		Set<Object> conjuntoCargo = listaCargo.getSelection();
		Cargo cargo = (Cargo) conjuntoCargo.toArray()[0];

		ListModelList<Object> listaFrecuencia = (ListModelList<Object>) cmbFrecuencias
				.getModel();
		Set<Object> conjuntoFrecuencia = listaFrecuencia.getSelection();
		Frecuencia frecuencia = (Frecuencia) conjuntoFrecuencia.toArray()[0];

		ListModelList<Object> listaPrioriad = (ListModelList<Object>) cmbPrioridades
				.getModel();
		Set<Object> conjuntoPrioridad = listaPrioriad.getSelection();
		Prioridad prioridad = (Prioridad) conjuntoPrioridad.toArray()[0];

		String nombre = cmbTareas.getValue();
		Tarea tarea = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre,
				usuarioFiltro.getEmpresa().getId());
		if (tarea == null) {
			Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");

			long duracion = lblHoras.getValue() * 3600 + lblMinutos.getValue()
					* 60;
			tarea = new Tarea(nombre, duracion, estatus,
					usuarioFiltro.getEmpresa());
			if (!tarea.equals(null)) {
				tareaDAO.registrarTarea(tarea);
			}
		}
		tarea = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre, usuarioFiltro
				.getEmpresa().getId());

		String indicaciones = lblIndicaciones.getValue();
		int vecesRepetir = lblVecesRepetir.getValue();
		Date fechaInicio = lblFechaInicio.getValue();
		Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");
		TareaRepetitiva tareaRepetitiva = new TareaRepetitiva(tarea, prioridad,
				cargo, frecuencia, indicaciones, vecesRepetir, fechaInicio,
				false, estatus);

		if (!tareaRepetitiva.equals(null)) {
			tareaRepetitivaDAO.registrarTareaRepetitiva(tareaRepetitiva);
			asignarTareasRepetitivas(tareaRepetitiva);
			BindUtils.postGlobalCommand(null, null,
					"actualizarTareasRepetitivas", null);
			showNotifyInfo("Tarea Repetitiva Registrada Exitosamente",
					windowsRegistarTareaRepetitiva);
			windowsRegistarTareaRepetitiva.onClose();

		}

	}

	public void asignarTareasRepetitivas(TareaRepetitiva tareaRepetitiva) {
		Date fechaAuxiliar = tareaRepetitiva.getFechaInicio();
		Calendar calendarioAuxiliar = Calendar.getInstance();
		calendarioAuxiliar.setTime(fechaAuxiliar);

		int siguientePeriodo = tareaRepetitiva.getFrecuencia().getPeriodoDias();

		List<Usuario> empleadosAsignar = usuarioDAO
				.obtenerUsuariosActivosPorCargo(tareaRepetitiva.getCargo()
						.getId());
		EstatusTareaAsignada estatusTareaAsignada = estatusTareaAsignadaDAO
				.obtenerEstatusPorNombre("POR INICIAR");

		ListModelList<Object> listatarea = (ListModelList<Object>) cmbTareas
				.getModel();
		Set<Object> conjuntoTarea = listatarea.getSelection();
		Tarea tarea = (Tarea) conjuntoTarea.toArray()[0];

		// VECES QUE SE DEBE REGISTRAR UNA TAREA ASIGNADA
		for (int i = 0; i < tareaRepetitiva.getVecesRepetir(); i++) {
			// EMPLEADOS A QUIENES SE LES ASIGNARA LA TAREA REPETITIVA SEGUN EL
			// CARGO
			for (int j = 0; j < empleadosAsignar.size(); j++) {

				TareaRepetitivaAsignada tareaRepetitivaAsignada = new TareaRepetitivaAsignada(
						tareaRepetitiva, empleadosAsignar.get(j),
						estatusTareaAsignada, fechaAuxiliar, null, null, 0, 0,
						tarea.getDuracion(), 0);
				tareaRepetitivaAsignadaDAO
						.registrarTareaRepetitivaAsignada(tareaRepetitivaAsignada);

				BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
						tareaRepetitivaAsignada, usuarioFiltro, new Date(),
						"El supervisor ha creado una Tarea Repetitiva", "CREAR");

				String nombreCompleto = usuarioFiltro.getNombres() + " "
						+ usuarioFiltro.getApellidos();
				String descripcion = "El Supervisor "
						+ nombreCompleto
						+ " (V-"
						+ usuarioFiltro.getCedula()
						+ " ) le ha asignado una nueva tarea repetitiva asignada ("
						+ tarea.getNombre()
						+ "), verifique sus tareas repetitivas asignadas con estatus \"POR INICIAR\" ";
				EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
						.obtenerEstatusNotificacionPorNombre("POR LEER");
				NotificacionTareaRepetitivaAsignada notificacionTareaRepetitivaAsignada = new NotificacionTareaRepetitivaAsignada(
						descripcion, empleadosAsignar.get(j),
						bitacoraTareaRepetitivaAsignada, estatusNotificacion);

				bitacoraTareaRepetitivaAsignadaDAO
						.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
				notificacionTareaRepetitivaAsignadaDAO
						.registrarNotificacionTareaRepetitivaAsignada(notificacionTareaRepetitivaAsignada);

			}
			// INCREMENTA LA FECHA
			calendarioAuxiliar.set(Calendar.DATE,
					(calendarioAuxiliar.get(Calendar.DATE) + siguientePeriodo));
			fechaAuxiliar = calendarioAuxiliar.getTime();

		}
	}

	@Listen("onClick=#btnCancelar")
	public void CancelarSolicitud() {
		Messagebox
				.show("¿Seguro que desea Cancelar el Registro de la Tarea Repetitiva?",
						"Question", Messagebox.OK | Messagebox.CANCEL,
						Messagebox.QUESTION, new EventListener<Event>() {
							public void onEvent(Event e) {
								if (Messagebox.ON_OK.equals(e.getName())) {
									// OK is clicked
									windowsRegistarTareaRepetitiva.onClose();
								} else if (Messagebox.ON_CANCEL.equals(e
										.getName())) {
									// Cancel is clicked
								}
							}
						});

	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	@Listen("onChange = #lblHoras")
	public void cambioMinimoMinutos() {
		int horas = lblHoras.getValue();
		if (horas == 0) {
			lblMinutos.setConstraint("no empty, min 1, max 60");
		} else {
			lblMinutos.setConstraint("no empty, min 0, max 60");
		}
	}

	@Listen("onChange = #cmbTareas")
	public void cambioTarea() {
		String nombre = cmbTareas.getValue();
		Tarea tarea = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre,
				usuarioFiltro.getEmpresa().getId());
		if (tarea == null) {
			divDuracion.setVisible(true);
			showNotify("Ingrese la Duracion de la Tarea Nueva", lblHoras);
		} else {
			divDuracion.setVisible(false);
		}
	}

}
