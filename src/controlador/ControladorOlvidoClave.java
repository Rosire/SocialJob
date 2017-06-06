package controlador;

import java.util.Properties;
import java.util.Random;

import modelo.dao.UsuarioDAO;
import modelo.dto.Usuario;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import javax.mail.*;
import javax.mail.internet.*;

public class ControladorOlvidoClave extends SelectorComposer<Component> {

	private static final long serialVersionUID = -8512830370172028486L;

	private final String USUARIO = "socialjob2015@hotmail.com";
	private final String PASSWORD = "(20151234)";

	@Wire
	private Intbox txtIdOlvidoClave;

	@Wire
	private Textbox txtEMailOlvidoClave;

	@Wire("#windowOlvidoContrasenna")
	private Window windowOlvidoContrasenna;

	private UsuarioDAO usuarioDAO = new UsuarioDAO();
	Usuario usuario;

	@Listen("onClick = #btnCancelar")
	public void accionCancelar() {
		windowOlvidoContrasenna.onClose();
	}

	@Listen("onClick= #btnAceptar")
	public void accionAceptar() {
		String email = txtEMailOlvidoClave.getValue();

		Integer cedulaI = txtIdOlvidoClave.getValue();

		if (email.trim().equals("") || cedulaI == null) {
			showNotify("Debe llenar todos los campos", txtEMailOlvidoClave);
			return;
		}

		if (!email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")) {
			showNotify("Debe introducir un correo valido", txtEMailOlvidoClave);

			return;
		}

		String cedulaS = String.valueOf(cedulaI);

		usuario = usuarioDAO.obtenerUsuario(cedulaS);

		if (usuario == null) {
			showNotify("Usuario no encontrado", txtIdOlvidoClave);

			return;
		}

		if (!usuario.getEmail().equals(email)) {

			showNotify(
					"Este correo no coincide con el que usted suministro cuando se registro en el sistema",
					txtIdOlvidoClave);

			return;
		}

		String clave = generarClaveProvisional();

		enviarCorreo(email, clave);

		usuario.setContrasenna(clave);

		usuarioDAO.actualizarUsuario(usuario);

		showNotifyWarning(
				"Se te ha enviado un correo con tu nueva contraseña.",
				windowOlvidoContrasenna);
		windowOlvidoContrasenna.onClose();

	}

	String generarClaveProvisional() {
		String codigo = "";

		Random random = new Random(System.currentTimeMillis());

		for (int i = 0; i < 10; i++) {
			int valor = random.nextInt(3);

			switch (valor) {
			case 0:// Numero
				codigo += String.valueOf(random.nextInt(10));
				break;
			case 1:// LowCase
				char x = (char) (random.nextInt(26) + 97);
				codigo += ("" + x);
				break;
			case 2:// UpperCase
				char y = (char) (random.nextInt(26) + 65);
				codigo += ("" + y);
				break;
			default:
				break;
			}
		}
		return codigo;
	}

	private Properties SMTPservidor() {
		Properties prop = new Properties();

		/*
		 * Configurar Java cliente de correo saliente gmail
		 */
		// prop.put("mail.smtp.host", "smtp.gmail.com");
		// prop.put("mail.smtp.socketFactory.port", "465");
		// prop.put("mail.smtp.socketFactory.class",
		// "javax.net.ssl.SSLSocketFactory");
		// prop.put("mail.smtp.auth", "true");
		// prop.put("mail.smtp.port", "465");

		/*
		 * Configurar Java cliente de correo saliente yahoo
		 */
		// prop.put("mail.smtp.host", "smtp.mail.yahoo.com");
		// prop.put("mail.smtp.socketFactory.port", "465");
		// prop.put("mail.smtp.socketFactory.class",
		// "javax.net.ssl.SSLSocketFactory");
		// prop.put("mail.smtp.auth", "true");
		// prop.put("mail.smtp.port", "465");

		/*
		 * Configurar Java cliente de correo saliente hotmail
		 */
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.live.com");
		prop.put("mail.smtp.socketFactory.port", "587");
		prop.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.port", "587");

		return prop;
	}

	void enviarCorreo(String correo, String clave) {
		// Crear un mensaje vacio
		Message mensaje = new MimeMessage(autenticacion());
		try {
			// Â¿Quien envia este mensaje?
			mensaje.setFrom(new InternetAddress(USUARIO));
			// Â¿Para quien se envia mensaje?
			// InternetAddress[] correos = new InternetAddress[2];
			// correos[0] = new InternetAddress("rosima_08@hotmail.com");
			// correos[1] = new InternetAddress("rosire08@gmail.com");

			mensaje.setRecipients(Message.RecipientType.TO,
			// InternetAddress.parse("rosima_08@hotmail.com"));
			// correos);
					InternetAddress.parse(correo));
			// Asunto de mensaje

			// mensaje.setText("Mensaje");

			mensaje.setSubject("Cambio de clave en SocialJob");
			// El texto del mensaje

			String cuerpo = "<html><body>"
					+ "<div style='text-align:left; width:100% ; color: #2582EF'><p style='color: #999; font-size: 12px; line-height: 16px;'>Este es un mensaje de correo electr&oacute;nico autom&aacute;tico, no lo respondas.</p>"
					+ "<p>Estimado Usuario: <strong> "
					+ usuario.getNombres()
					+ " "
					+ usuario.getApellidos()
					+ " </strong> </p>"
					+ "<p>Le informamos su NUEVA clave de acceso: <strong style='color: #182EC7; font-weight:bold;'>"
					+ clave
					+ "</strong></p>"
					+ "<p>Ingresa con tu usuario y esta nueva contrase&ntilde;a al sistema.<br></br>Recuerda cambiar tu contrase&ntilde;a una vez que hayas ingresado.</p>"
					+ "</div>"
					+ ""
					+ "<div style='text-align:center; width:100% font-weight:bold; color: #2582EF'><p  style='text-align:center; width:100% font-weight:bold; color: #2582EF'>¡Hasta pronto!</p>"
					+ "<strong><p style='text-align:center; width:100% font-weight:bold; color: #2582EF'>SocialJob - ¡Tus trabajos a la mano!</p> </strong></div>"

					+ "</body></html>";

			Multipart multipart = new MimeMultipart("related");

			BodyPart bodyPartHtml = new MimeBodyPart();

			bodyPartHtml.setContent(cuerpo, "text/html");

			multipart.addBodyPart(bodyPartHtml);

			mensaje.setContent(multipart);

			/**
			 * El mensaje ya esta listo para ser enviado. Para enviarlo se usa
			 * la clase Transport mediante su metodo estatico send().
			 */
			Transport.send(mensaje);

			// En caso contrario nos muestra excepcion
		} catch (MessagingException ex) {

			ex.printStackTrace();
		}
	}

	private Session autenticacion() {
		Session sesion = Session.getDefaultInstance(SMTPservidor(),
				new Authenticator() {

					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(USUARIO, PASSWORD);
					}
				});
		return sesion;
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
