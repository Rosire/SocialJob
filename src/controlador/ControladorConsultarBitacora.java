package controlador;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import modelo.dao.EstatusNotificacionDAO;
import modelo.dao.NotificacionSolicitudDAO;
import modelo.dao.NotificacionTareaAsignadaDAO;
import modelo.dao.NotificacionTareaRepetitivaAsignadaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.BitacoraTareaAsignada;
import modelo.dto.NotificacionSolicitud;
import modelo.dto.NotificacionTareaAsignada;
import modelo.dto.NotificacionTareaRepetitivaAsignada;
import modelo.dto.Solicitud;
import modelo.dto.TareaAsignada;
import modelo.dto.TareaRepetitiva;
import modelo.dto.TareaRepetitivaAsignada;
import modelo.dto.Usuario;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class ControladorConsultarBitacora extends
		SelectorComposer<Component> {
	@Wire
	private Window windowConsultarNotificacion;
	@Wire
	private Label labelCodigo;
	@Wire
	private Label labelTipoTarea;

	@Listen("onClick=#btnSalir")
	public void salirVentana() {
		windowConsultarNotificacion.onClose();
	}

}
