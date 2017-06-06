package controlador;

import modelo.dao.TareaDAO;
import modelo.dao.EstatusDAO;
import modelo.dto.Tarea;
import modelo.dto.Estatus;
import modelo.dto.Usuario;

import org.postgresql.util.PGInterval;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class ControladorRegistrarTarea extends SelectorComposer<Component> {

	// wire components
	@Wire
	private Textbox lblNombre;
	@Wire
	private Listbox listaTareas;
	@Wire("#windowsRegistrarTarea")
	private Window windowsRegistrarTarea;
	@Wire
	private Spinner lblHoras;
	@Wire
	private Spinner lblMinutos;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private TareaDAO tareaDAO = new TareaDAO();
	private EstatusDAO estatusDAO = new EstatusDAO();

	public ControladorRegistrarTarea() {
		super();
	}

	@Listen("onClick=#btnGuardarTarea")
	public void registrarTarea() {
		String nombre = lblNombre.getValue();
		Tarea tareaBD = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre,
				usuarioSession.getEmpresa().getId());
		if ((tareaBD != null)) {
			showNotify("Tarea con ese nombre Registrada en Base de Datos",
					lblNombre);
			lblNombre.focus();
		} else {

			Estatus estatus = estatusDAO.obtenerEstatusPorNombre("ACTIVO");

			long duracion = lblHoras.getValue() * 3600 + lblMinutos.getValue()
					* 60;
			Tarea tarea = new Tarea(nombre, duracion, estatus,
					usuarioSession.getEmpresa());
			if (!tarea.equals(null)) {
				tareaDAO.registrarTarea(tarea);
				showNotifyInfo("Tarea Registrada Exitosamente",
						windowsRegistrarTarea);
				windowsRegistrarTarea.onClose();
			} else {
				showNotify("Error al Almacenar Tarea", windowsRegistrarTarea);
			}
		}
	}

	@Listen("onClick=#btnCancelar")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Question",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowsRegistrarTarea.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onChange = #lblNombre")
	public void cambioNombre() {
		String nombre = lblNombre.getValue().toString();
		if (nombre.equalsIgnoreCase("")) {
			showNotify("Debe ingresar el nombre de la tarea", lblNombre);
			lblNombre.focus();
		}
		Tarea tarea = tareaDAO.obtenerTareaPorNombrePorEmpresa(nombre,
				usuarioSession.getEmpresa().getId());
		if ((tarea != null)) {
			showNotify("Tarea Registrada en Base de Datos", lblNombre);
			lblNombre.focus();
		}
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

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
