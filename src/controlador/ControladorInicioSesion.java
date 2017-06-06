package controlador;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modelo.dao.UsuarioDAO;
import modelo.dto.Cargo;
import modelo.dto.Usuario;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorInicioSesion extends SelectorComposer<Component> {

	@Wire
	private Textbox lblEmail;
	@Wire
	private Textbox lblContrasenna;
	@Wire
	private Popup sesion;

	private UsuarioDAO usuarioDAO = new UsuarioDAO();

	private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public ControladorInicioSesion() {
		super();
	}

	@Listen("onClick=#btnAcceder; onOK=#btnAcceder; onOK=#lblContrasenna")
	public void iniciarSesion() {
		if (lblEmail.getValue().isEmpty()
				|| lblContrasenna.getValue().isEmpty()) {
			showNotify("Debe Llenar todos los campos", sesion);
		} else if (!validarEmail(lblEmail.getValue().toString())) {
			showNotifyFinal("Formato de Email Inválido. (ejemplo@correo.es)",
					lblEmail);
		} else {

			String email = lblEmail.getValue().toString();
			String contrasenna = lblContrasenna.getValue();

			Usuario usuario = usuarioDAO.obtenerUsuario(email, contrasenna);
			Session miSession = Sessions.getCurrent();
			if (usuario != null) {
				if (usuario.getEstatusUsuario().getNombre().equals("ACTIVO")) {
					miSession.setAttribute("usuario", usuario);
					Executions.sendRedirect("indexRegistrado.zul");

				} else {
					lblContrasenna.setValue("");
					lblEmail.setValue("");
					showNotifyFinal(
							"Su usuario se encuentra bloqueado no puede ingresar al sistema.",
							lblEmail);
				}

			} else {
				showNotify("Usuario o contraseña inconrretas", sesion);
				lblContrasenna.setValue("");
				lblEmail.setValue("");
			}
		}
	}

	@Listen("onClick=#registrarse; onOK=#registrarse")
	public void registrar() {
		Window window = (Window) Executions.createComponents(
				"vista/registrarEmpresa.zul", null, null);
		window.doModal();
	}

	@Listen("onChange = #lblEmail")
	public void cambioEmail() {
		String email = lblEmail.getValue().toString();
		if (email.equalsIgnoreCase("")) {
			showNotify("Debe ingresar su email para ingresar", lblEmail);

		}
		if (!validarEmail(email)) {
			showNotifyFinal("Formato de Email Inválido. (ejemplo@correo.es)",
					lblEmail);

		}
	}

	@Listen("onClick=#olvideContrasenna; onOK=#olvideContrasenna")
	public void olvido() {
		Window window = (Window) Executions.createComponents(
				"vista/olvidoContrasenna.zul", null, null);
		window.doModal();
	}

	private static boolean validarEmail(String email) {
		Pattern pattern = Pattern.compile(PATTERN_EMAIL);

		// Match the given input against this pattern
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "middle_center", 3000, true);
	}

	private void showNotifyFinal(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	private void showNotifyWarning(String msg, Component ref) {
		Clients.showNotification(msg, "warning", ref, "middle_center", 5000,
				true);
	}

}
