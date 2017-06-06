package controlador;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ControladorConsultarMensaje extends SelectorComposer<Component> {
	@Wire
	private Window windowConsultarMensaje;
	@Wire
	private Label labelCodigo;

	public ControladorConsultarMensaje() {
		super();
	}

	@Listen("onClick=#btnSalir")
	public void salirVentana() {
		windowConsultarMensaje.onClose();
	}

}
