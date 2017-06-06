package controlador;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.dao.EstatusMensajeDAO;
import modelo.dao.MensajeDAO;
import modelo.dto.EstatusMensaje;
import modelo.dto.Mensaje;
import modelo.dto.Usuario;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

public class ControladorMensajes extends SelectorComposer<Component> {

	@Wire
	private Grid gridMensajesEnviados;
	@Wire
	private Grid gridMensajesRecibidos;
	@Wire
	private Panel panelMensajesEnviados;
	@Wire
	private Panel panelMensajesRecibidos;

	// Variable Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private EstatusMensajeDAO estatusMensajeDAO = new EstatusMensajeDAO();
	private MensajeDAO mensajeDAO = new MensajeDAO();

	private Mensaje mensaje;

	private ListModel<Mensaje> listaMensajesEnviadosModel;
	private List<Mensaje> mensajesEnviados;

	private ListModel<Mensaje> listaMensajeRecibidosModel;
	private List<Mensaje> mensajesRecibidos;

	private static final String footerMessageMensajesEnviados = "Total de Mensajes Enviados: %d";
	private static final String footerMessageMensajesRecibidos = "Total de Mensajes Recibidos: %d";

	private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

	public ControladorMensajes() {
		super();
		mensajesEnviados = mensajeDAO.obtenerMensajesEnviados(usuarioSession
				.getId());
		mensajesRecibidos = mensajeDAO.obtenerMensajesRecibidos(usuarioSession
				.getId());
	}

	public String getFooterMessageMensajesEnviados() {
		return String.format(footerMessageMensajesEnviados,
				mensajesEnviados.size());
	}

	public ListModel<Mensaje> getMensajesEnviados() {
		listaMensajesEnviadosModel = new ListModelList<Mensaje>(
				mensajesEnviados);
		return listaMensajesEnviadosModel;
	}

	public String getFooterMessageMensajesRecibidos() {
		return String.format(footerMessageMensajesRecibidos,
				mensajesRecibidos.size());
	}

	public ListModel<Mensaje> getMensajesRecibidos() {
		listaMensajeRecibidosModel = new ListModelList<Mensaje>(
				mensajesRecibidos);
		return listaMensajeRecibidosModel;
	}

	// Mostrar Mensajes Recibidos
	@Listen("onClick=#navItemMensajesRecibidos")
	public void mostrarMensajesRecibidos() {
		panelMensajesEnviados.setVisible(false);
		panelMensajesRecibidos.setVisible(true);
	}

	// Mostrar Mensajes Enviado
	@Listen("onClick=#navItemMensajesEnviados")
	public void mostrarMensajesEnviados() {
		panelMensajesEnviados.setVisible(true);
		panelMensajesRecibidos.setVisible(false);
	}

	@Listen("onClick=#consultarMensajeEnviado")
	public void consultarMensajeEnviado() {
		String indexSeleccionado = getIdSelectedRowCargo(gridMensajesEnviados);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una mensaje a Consultar",
					gridMensajesEnviados);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			mensaje = mensajeDAO.obtenerMensaje(id);
			Map<String, Object> arguments = new HashMap<String, Object>();
			String fechaMensaje;
			try {
				fechaMensaje = formatoFecha.format(mensaje.getFecha());
			} catch (Exception e) {
				fechaMensaje = "";
			}
			arguments.put("fechaMensaje", fechaMensaje);
			arguments.put("icono", "z-icon-envelope");
			arguments.put("mensaje", mensaje);
			arguments.put("btnResponder", true);
			Window window = (Window) Executions.createComponents(
					"/vista/consultarMensaje.zul", null, arguments);
			window.doModal();
		}
	}

	@Listen("onClick=#consultarMensajeRecibido")
	public void consultarMensajeRecibido() {
		String indexSeleccionado = getIdSelectedRowCargo(gridMensajesRecibidos);
		if (indexSeleccionado == null) {
			showNotify("Debe seleccionar una mensaje a Consultar",
					gridMensajesRecibidos);
		} else {
			int id = Integer.parseInt(indexSeleccionado);
			EstatusMensaje estatus = estatusMensajeDAO
					.obtenerEstatusMensajePorNombre("LEIDO");
			mensaje = mensajeDAO.obtenerMensaje(id);
			Map<String, Object> arguments = new HashMap<String, Object>();
			String fechaMensaje;
			try {
				fechaMensaje = formatoFecha.format(mensaje.getFecha());
			} catch (Exception e) {
				fechaMensaje = "";
			}
			arguments.put("fechaMensaje", fechaMensaje);
			arguments.put("icono", "z-icon-envelope");
			arguments.put("mensaje", mensaje);
			arguments.put("btnResponder", true);
			Window window = (Window) Executions.createComponents(
					"/vista/consultarMensaje.zul", null, arguments);
			window.doModal();
			mensaje.setEstatusMensaje(estatus);
			mensajeDAO.actualizarMensaje(mensaje);
			BindUtils.postGlobalCommand(null, null,
					"actualizarMensajesRecibidos", null);
			BindUtils.postGlobalCommand(null, null, "actualizarMensajes", null);
		}
	}

	@Listen("onClick=#btnIncluirMensaje; onClick=#btnEnviarMensaje ")
	public void enviarMensaje() {
		Window window = (Window) Executions.createComponents(
				"/vista/enviarMensaje.zul", null, null);
		window.doModal();
	}

	@GlobalCommand
	@NotifyChange({ "mensajesEnviados" })
	public void actualizarMensajesEnviados() {
		mensajesEnviados = mensajeDAO.obtenerMensajesEnviados(usuarioSession
				.getId());
	}

	@GlobalCommand
	@NotifyChange({ "mensajesRecibidos" })
	public void actualizarMensajesRecibidos() {
		mensajesRecibidos = mensajeDAO.obtenerMensajesRecibidos(usuarioSession
				.getId());
	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

	private String getIdSelectedRowCargo(Grid grid) {
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

}
