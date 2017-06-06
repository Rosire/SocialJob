package controlador;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import modelo.dao.BitacoraTareaRepetitivaAsignadaDAO;
import modelo.dao.CargoDAO;
import modelo.dao.EstatusDAO;
import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.EstatusTareaAsignadaDAO;
import modelo.dao.FrecuenciaDAO;
import modelo.dao.NotificacionTareaRepetitivaAsignadaDAO;
import modelo.dao.TareaDAO;
import modelo.dao.TareaRepetitivaAsignadaDAO;
import modelo.dao.TareaRepetitivaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaRepetitivaAsignada;
import modelo.dto.Estatus;
import modelo.dto.EstatusNotificacion;
import modelo.dto.EstatusTareaAsignada;
import modelo.dto.NotificacionTareaRepetitivaAsignada;
import modelo.dto.TareaRepetitiva;
import modelo.dto.TareaRepetitivaAsignada;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ControladorConsultarEditarTareaRepetitiva extends
		SelectorComposer<Component> {

	@Wire
	private Div divModificar;
	@Wire
	private Div divConsultar;
	@Wire
	private Button btnGuardar;
	@Wire
	private Button btnEditar;
	@Wire
	private Button btnCancelar;
	@Wire
	private Button btnSalir;
	@Wire
	private Window windowsRegistarTareaRepetitiva;
	@Wire
	private Label labelCodigo;
	@Wire
	private Spinner lblVecesRepetir;
	@Wire
	private Textbox lblIndicaciones;
	@Wire
	private Label labelIndicaciones;
	@Wire
	private Label labelTarea;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioFiltro = (Usuario) miSession.getAttribute("usuario");

	private FrecuenciaDAO frecuenciaDAO = new FrecuenciaDAO();
	private TareaRepetitivaDAO tareaRepetitivaDAO = new TareaRepetitivaDAO();
	private TareaRepetitivaAsignadaDAO tareaRepetitivaAsignadaDAO = new TareaRepetitivaAsignadaDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private NotificacionTareaRepetitivaAsignadaDAO notificacionTareaRepetitivaAsignadaDAO = new NotificacionTareaRepetitivaAsignadaDAO();
	private EstatusNotificacionDAO estatusNotificacionDAO = new EstatusNotificacionDAO();
	private BitacoraTareaRepetitivaAsignadaDAO bitacoraTareaRepetitivaAsignadaDAO = new BitacoraTareaRepetitivaAsignadaDAO();
	private EstatusTareaAsignadaDAO estatusTareaAsignadaDAO = new EstatusTareaAsignadaDAO();

	public ControladorConsultarEditarTareaRepetitiva() {
		super();
	}

	@Listen("onClick=#btnSalir")
	public void salirConsulta() {
		windowsRegistarTareaRepetitiva.onClose();
	}

	@Listen("onClick=#btnEditar")
	public void editarTarea() {
		divConsultar.setVisible(false);
		divModificar.setVisible(true);
		btnGuardar.setVisible(true);
		btnCancelar.setVisible(true);
		btnSalir.setVisible(false);
		btnEditar.setVisible(false);
	}

	// CancelarModificacion
	@Listen("onClick=#btnCancelar")
	public void cancelarModificacionEditar() {
		Messagebox.show("¿Seguro que desea cancelar la modificación?",
				"Cancelar Modificación", Messagebox.OK | Messagebox.CANCEL,
				Messagebox.QUESTION, new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowsRegistarTareaRepetitiva.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onClick=#btnGuardar")
	public void guardarCambiosTareaRepetitiva() {

		int id = Integer.parseInt(labelCodigo.getValue().toString());
		String indicaciones = lblIndicaciones.getValue();
		int vecesRepetir = lblVecesRepetir.getValue();
		TareaRepetitiva tareaRepetitiva = tareaRepetitivaDAO
				.obtenerTareaRepetitiva(id);
		tareaRepetitiva.setIndicaciones(indicaciones);
		tareaRepetitiva.setVecesRepetir(vecesRepetir);
		tareaRepetitivaDAO.actualizarTareaRepetitiva(tareaRepetitiva);
		if (tareaRepetitiva.getVecesRepetir() < vecesRepetir) {

			int cantidadAgregar = vecesRepetir
					- tareaRepetitiva.getVecesRepetir();

			int siguientePeriodo = frecuenciaDAO.obtenerFrecuencia(
					tareaRepetitiva.getFrecuencia().getId()).getPeriodoDias();

			List<Usuario> empleadosAsignar = usuarioDAO
					.obtenerUsuariosActivosPorCargo(tareaRepetitiva.getCargo()
							.getId());
			EstatusTareaAsignada estatusTareaAsignada = estatusTareaAsignadaDAO
					.obtenerEstatusPorNombre("POR INICIAR");
			Date fechaAuxiliar = tareaRepetitiva.getFechaInicio();
			Calendar calendarioAuxiliar = Calendar.getInstance();
			calendarioAuxiliar.setTime(fechaAuxiliar);
			calendarioAuxiliar
					.set(Calendar.DATE,
							(calendarioAuxiliar.get(Calendar.DATE) + (siguientePeriodo * tareaRepetitiva
									.getVecesRepetir())));
			fechaAuxiliar = calendarioAuxiliar.getTime();

			// VECES QUE SE DEBE REGISTRAR UNA TAREA ASIGNADA
			for (int i = 0; i < cantidadAgregar; i++) {
				// EMPLEADOS A QUIENES SE LES ASIGNARA LA TAREA REPETITIVA SEGUN
				// EL
				// CARGO
				for (int j = 0; j < empleadosAsignar.size(); j++) {

					TareaRepetitivaAsignada tareaRepetitivaAsignada = new TareaRepetitivaAsignada(
							tareaRepetitiva, empleadosAsignar.get(j),
							estatusTareaAsignada, fechaAuxiliar, null, null, 0,
							0, tareaRepetitiva.getTarea().getDuracion(), 0);
					tareaRepetitivaAsignadaDAO
							.registrarTareaRepetitivaAsignada(tareaRepetitivaAsignada);

					BitacoraTareaRepetitivaAsignada bitacoraTareaRepetitivaAsignada = new BitacoraTareaRepetitivaAsignada(
							tareaRepetitivaAsignada, usuarioFiltro, new Date(),
							"El supervisor ha creado una Tarea Repetitiva",
							"CREAR");
					String nombreCompleto = usuarioFiltro.getNombres() + " "
							+ usuarioFiltro.getApellidos();
					String descripcion = "El Supervisor "
							+ nombreCompleto
							+ " (V-"
							+ usuarioFiltro.getCedula()
							+ " ) le ha asignado una nueva tarea repetitiva asignada ("
							+ tareaRepetitiva.getTarea().getNombre()
							+ "), verifique sus tareas repetitivas asignadas con estatus \"POR INICIAR\" ";
					EstatusNotificacion estatusNotificacion = estatusNotificacionDAO
							.obtenerEstatusNotificacionPorNombre("POR LEER");
					NotificacionTareaRepetitivaAsignada notificacionTareaRepetitivaAsignada = new NotificacionTareaRepetitivaAsignada(
							descripcion, empleadosAsignar.get(j),
							bitacoraTareaRepetitivaAsignada,
							estatusNotificacion);

					bitacoraTareaRepetitivaAsignadaDAO
							.registrarBitacoraTareaRepetitivaAsignada(bitacoraTareaRepetitivaAsignada);
					notificacionTareaRepetitivaAsignadaDAO
							.registrarNotificacionTareaRepetitivaAsignada(notificacionTareaRepetitivaAsignada);

				}
				// INCREMENTA LA FECHA
				calendarioAuxiliar
						.set(Calendar.DATE,
								(calendarioAuxiliar.get(Calendar.DATE) + siguientePeriodo));
				fechaAuxiliar = calendarioAuxiliar.getTime();

			}

		}
		BindUtils.postGlobalCommand(null, null, "actualizarTareasRepetitivas",
				null);
		showNotifyInfo("Tarea Repetitiva Modificada Exitosamente",
				windowsRegistarTareaRepetitiva);
		windowsRegistarTareaRepetitiva.onClose();
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
