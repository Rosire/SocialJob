package controlador;

import modelo.dao.FrecuenciaDAO;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorEditarFrecuencia extends

SelectorComposer<Component> {
	@Wire
	private Window windowEditarFrecuencia;
	@Wire
	private Button btnGuardar;
	@Wire
	private Button btnSalir;
	@Wire
	private Button btnCancelar;
	@Wire
	private Textbox nombreFrecuencia;
	@Wire
	private Spinner periodoFrecuencia;
	@Wire
	private Label codigoFrecuencia;

	private FrecuenciaDAO frecuenciaDAO = new FrecuenciaDAO();

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	public ControladorEditarFrecuencia() {
		super();
	}

	@Listen("onClick=#btnSalir")
	public void salirVentana() {
		windowEditarFrecuencia.onClose();
	}

	// Cancelar
	@Listen("onClick=#btnCancelar")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Cancelar Modificación",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowEditarFrecuencia.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});

	}

	@Listen("onClick=#btnGuardar")
	public void modificarFrecuencia() {
		String nombre = nombreFrecuencia.getValue();
		int periodoDias = periodoFrecuencia.getValue();
		int codigo = Integer.parseInt(codigoFrecuencia.getValue());
		Frecuencia frecuencia = new Frecuencia(nombre, periodoDias,
				usuarioSession.getEmpresa());
		if (!frecuencia.equals(null)) {
			frecuencia.setId(codigo);
			frecuenciaDAO.actualizarFrecuencia(frecuencia);
			showNotifyInfo("Frecuencia modificada Exitosamente",
					windowEditarFrecuencia);
			BindUtils.postGlobalCommand(null, null, "actualizarFrecuencias",
					null);
			windowEditarFrecuencia.onClose();
		} else {
			showNotify("Error al Modificar la Frecuencia",
					windowEditarFrecuencia);
		}

	}

	@Listen("onChange = #nombreFrecuencia")
	public void validarNombre() {
		String nombre = nombreFrecuencia.getValue();
		int id = Integer.parseInt(codigoFrecuencia.getValue());
		if (nombre.isEmpty()) {
			showNotify("Debe ingresar el nombre de la fecuencia",
					nombreFrecuencia);
			nombreFrecuencia.focus();
		} else {
			Frecuencia frecuencia = frecuenciaDAO
					.obtenerFrecuenciaPorNombrePorEmpresa(nombre,
							usuarioSession.getEmpresa().getId());
			if ((frecuencia != null) && frecuencia.getId() != id) {
				showNotify("Frecuencia resgitrada en la Base de datos",
						nombreFrecuencia);
			}
		}
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
