package controlador;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import modelo.dao.EstatusMensajeDAO;
import modelo.dao.EstatusSolicitudDAO;
import modelo.dao.MensajeDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.Cargo;
import modelo.dto.Estatus;
import modelo.dto.EstatusMensaje;
import modelo.dto.Mensaje;
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
import org.zkoss.zkmax.zul.Chosenbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ControladorEnviarMensaje extends SelectorComposer<Component> {

	@Wire
	private Window windowEnviarMensaje;
	@Wire
	private Textbox lblAsunto;
	@Wire
	private Textbox lblTexto;
	@Wire
	private Chosenbox destinatarios;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	private MensajeDAO mensajeDAO = new MensajeDAO();
	private EstatusMensajeDAO estatusMensajeDAO = new EstatusMensajeDAO();

	private ListModel<Usuario> listaUsuariosModel;
	private List<Usuario> usuarios;

	public ControladorEnviarMensaje() {
		super();
		usuarios = usuarioDAO.obtenerUsuariosActivosPorEmpresa(usuarioSession
				.getEmpresa().getId());
	}

	public ListModel<Usuario> getUsuarios() {
		listaUsuariosModel = new ListModelList<Usuario>(usuarios);
		return listaUsuariosModel;
	}

	@Listen("onClick=#btnCancelar")
	public void cancelarRegistro() {
		Messagebox.show("¿Seguro que desea Cancelar?", "Cancelar ",
				Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						if (Messagebox.ON_OK.equals(e.getName())) {
							// OK is clicked
							windowEnviarMensaje.onClose();
						} else if (Messagebox.ON_CANCEL.equals(e.getName())) {
							// Cancel is clicked
						}
					}
				});
	}

	@Listen("onClick=#btnEnviar")
	public void enviarMensaje() {
		String asunto = lblAsunto.getValue();
		String texto = lblTexto.getValue();
		ListModelList<Object> usuariosSeleccionados = (ListModelList<Object>) destinatarios
				.getModel();
		Set<Object> conjuntoDestinatarios = usuariosSeleccionados
				.getSelection();
		if (conjuntoDestinatarios.size() == 0) {
			showNotify("Debe Seleccionar un Destinatario", destinatarios);
			destinatarios.focus();
		} else {
			EstatusMensaje estatus = estatusMensajeDAO
					.obtenerEstatusMensajePorNombre("POR LEER");
			List<Object> list = new ArrayList<Object>(conjuntoDestinatarios);
			Mensaje mensaje = new Mensaje();
			Usuario destinatario = new Usuario();
			for (int i = 0; i < list.size(); i++) {
				destinatario = (Usuario) list.get(i);
				mensaje = new Mensaje(usuarioSession, destinatario, asunto,
						texto, new Date(), estatus);
				mensajeDAO.registrarMensaje(mensaje);
			}
			BindUtils.postGlobalCommand(null, null,
					"actualizarMensajesEnviados", null);
			showNotifyInfo("Mensaje Enviado Exitosamente", windowEnviarMensaje);
			windowEnviarMensaje.onClose();

		}

	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 5000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}
}
