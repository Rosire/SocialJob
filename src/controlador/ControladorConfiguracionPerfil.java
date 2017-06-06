package controlador;

import modelo.dao.UsuarioDAO;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorConfiguracionPerfil extends SelectorComposer<Component> {

	@Wire
	private Textbox lblActualContrasenna;
	@Wire
	private Textbox lblNuevaContrasenna;
	@Wire
	private Textbox lblRepetirContrasenna;
	@Wire
	private Window windowConfiguracionPerfil;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuario = (Usuario) miSession.getAttribute("usuario");

	private UsuarioDAO usuarioDAO = new UsuarioDAO();

	@Listen("onClick=#btnCancelarModificacion")
	public void cancelarRegistro() {
		windowConfiguracionPerfil.onClose();
	}

	@Listen("onChange=#lblActualContrasenna")
	public void contrasennaActual() {

		if (lblActualContrasenna.getValue().isEmpty()) {
			showNotify("Ingrese la Contraseña Actual", lblActualContrasenna);
			lblActualContrasenna.focus();
		} else {
			String actual = lblActualContrasenna.getValue();
			if (!actual.equals(usuario.getContrasenna())) {
				showNotify("La contraseña ingresada es inválida",
						lblActualContrasenna);

				lblActualContrasenna.setValue("");
				lblActualContrasenna.focus();

			}
		}
	}

	@Listen("onChange=#lblNuevaContrasenna")
	public void contrasennaNueva() {
		if (lblActualContrasenna.getValue().isEmpty()) {
			showNotify("Ingrese la Contraseña Actual", lblActualContrasenna);
			lblNuevaContrasenna.setValue("");
			lblActualContrasenna.focus();
		} else if (lblNuevaContrasenna.getValue().isEmpty()) {
			showNotify("Ingrese la Nueva Contraseña", lblNuevaContrasenna);
			lblNuevaContrasenna.setValue("");
			lblNuevaContrasenna.focus();
		} else {
			String nuevaContrasennaa = lblNuevaContrasenna.getValue();
			if (nuevaContrasennaa.equals(usuario.getContrasenna())) {
				showNotify(
						"La nueva contraseña debe ser diferente a la actual",
						lblNuevaContrasenna);
				lblNuevaContrasenna.setValue("");
				lblNuevaContrasenna.focus();
			}
		}
	}

	@Listen("onChange=#lblRepetirContrasenna")
	public void contrasennaRepetir() {

		if (lblActualContrasenna.getValue().isEmpty()) {
			showNotify("Ingrese la Contraseña Actual", lblActualContrasenna);
			lblRepetirContrasenna.setValue("");
			lblActualContrasenna.focus();
		} else if (lblNuevaContrasenna.getValue().isEmpty()) {
			showNotify("Ingrese la Nueva Contraseña", lblNuevaContrasenna);
			lblRepetirContrasenna.setValue("");
			lblNuevaContrasenna.focus();
		} else if (lblRepetirContrasenna.getValue().isEmpty()) {
			showNotify("Repita la Nueva Contraseña", lblRepetirContrasenna);
			lblRepetirContrasenna.setValue("");
			lblRepetirContrasenna.focus();
		} else {
			String nuevaContrasenna = lblNuevaContrasenna.getValue();
			String repetirContrasenna = lblRepetirContrasenna.getValue();
			if (!nuevaContrasenna.equals(repetirContrasenna)) {
				showNotify("Debe coincidir con la nueva contraseña",
						lblRepetirContrasenna);
				lblRepetirContrasenna.setValue("");
				lblRepetirContrasenna.focus();
			}
		}
	}

	@Listen("onClick=#btnGuardarModificacion")
	public void guardar() {

		if (lblActualContrasenna.getValue().isEmpty()) {
			showNotify("Ingrese la Contraseña Actual", lblActualContrasenna);
			lblActualContrasenna.focus();
		} else if (lblNuevaContrasenna.getValue().isEmpty()) {
			showNotify("Ingrese la Nueva Contraseña", lblNuevaContrasenna);
			lblNuevaContrasenna.focus();
		} else if (lblRepetirContrasenna.getValue().isEmpty()) {
			showNotify("Repita la Nueva Contraseña", lblRepetirContrasenna);
			lblRepetirContrasenna.focus();
		} else {
			String nuevaContrasenna = lblNuevaContrasenna.getValue();
			String repetirContrasenna = lblRepetirContrasenna.getValue();
			if (!nuevaContrasenna.equals(repetirContrasenna)) {
				showNotify("Debe coincidir con la nueva contraseña",
						lblRepetirContrasenna);
				lblRepetirContrasenna.setValue("");
				lblRepetirContrasenna.focus();

			} else {
				usuario.setContrasenna(nuevaContrasenna);
				usuarioDAO.actualizarUsuario(usuario);
				miSession.setAttribute("usuario", usuario);
				BindUtils.postGlobalCommand(null, null,
						"actualizarInformacionUsuario", null);
				showNotifyInfo("Contraseña modificada Exitosamente",
						windowConfiguracionPerfil);
				windowConfiguracionPerfil.onClose();
			}
		}
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
