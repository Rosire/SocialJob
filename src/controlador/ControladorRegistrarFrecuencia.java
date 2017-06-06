package controlador;

import java.util.List;
import java.util.Set;

import modelo.dao.CargoDAO;
import modelo.dao.FrecuenciaDAO;
import modelo.dto.Cargo;
import modelo.dto.Frecuencia;
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class ControladorRegistrarFrecuencia extends SelectorComposer<Component> {

	// wire components
	@Wire
	private Textbox lblNombre;
	@Wire
	private Spinner lblPeriodo;
	@Wire("#windowsFrecuencia")
	private Window windowsFrecuencia;
	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private FrecuenciaDAO frecuenciaDAO = new FrecuenciaDAO();

	public ControladorRegistrarFrecuencia() {
		super();
	}

	@Listen("onClick=#btnGuardar")
	public void registrarFrecuencia() {
		String nombre = lblNombre.getValue();
		int periodoDias = lblPeriodo.getValue();
		Frecuencia frecuencia = new Frecuencia(nombre, periodoDias,
				usuarioSession.getEmpresa());
		if (!frecuencia.equals(null)) {
			frecuenciaDAO.registrarFrecuencia(frecuencia);
			BindUtils.postGlobalCommand(null, null, "actualizarFrecuencias",
					null);
			showNotifyInfo("Frecuencia Registrada Exitosamente",
					windowsFrecuencia);
			windowsFrecuencia.onClose();
		} else {
			showNotify("Error al Almacenar la Frecuencia", windowsFrecuencia);
		}
	}

	@Listen("onClick=#btnCancelar")
	public void cancelarRegistro() {
		Messagebox.show("Seguro que desea Cancelar", "Question", Messagebox.OK
				| Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowsFrecuencia.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onChange = #lblNombre")
	public void validarNombre() {
		String nombre = lblNombre.getValue();
		if (nombre.isEmpty()) {
			showNotify("Debe ingresar el nombre de la frecuencia", lblNombre);
			lblNombre.focus();
		} else {
			Frecuencia frecuencia = frecuenciaDAO
					.obtenerFrecuenciaPorNombrePorEmpresa(nombre,
							usuarioSession.getEmpresa().getId());
			if (frecuencia != null) {
				showNotify("Frecuencia resgitrada en la Base de datos",
						lblNombre);
			}
		}
	}

	@Listen("onChange = #lblPeriodo")
	public void validarPeriodo() {
		int periodo = lblPeriodo.getValue();
		if (periodo <= 0) {
			showNotify("Debe ingresar el Periodo en Dias", lblNombre);
			lblNombre.focus();
		}
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
