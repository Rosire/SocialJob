package controlador;

import java.util.Map;

import modelo.dto.Usuario;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;



public class ControladorAutenticacion implements Initiator {

	public void doInit(Page page, Map<String, Object> args) {
		try {
			Session session = Sessions.getCurrent();
			Usuario usuario = (Usuario) session.getAttribute("usuario");
			if (usuario.equals(null))
				Executions.sendRedirect("index.zul");
		} catch (Exception e) {
			Executions.sendRedirect("index.zul");
		}
		return;

	}

}
