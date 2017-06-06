package controlador;

import modelo.dto.Empresa;
import modelo.dto.Usuario;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlNativeComponent;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Html;
import org.zkoss.zul.Vlayout;

public class ControladorTwitterEmpresa extends SelectorComposer {

	@Wire
	private Vlayout vlayoutTwitter;
	@Wire("#htmlTwitter")
	private Html htmlTwitter;

	// Variable Sesion
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");
	private Empresa empresa = usuarioSession.getEmpresa();

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		String twitter = empresa.getTwitter();
		String linkTwitterEmpresa = "https://twitter.com/" + twitter;

		String html = " <figure> <a   target=\"blank\" class=\"twitter-timeline\" href=\""
				+ linkTwitterEmpresa
				+ " \" data-screen-name=\""
				+ twitter
				+ "\" data-widget-id=\"557892757642301440\">Tweets por el @"
				+ twitter
				+ "	</a>  <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+\"://platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\"); </script> </figure>";
		htmlTwitter.setContent(html);
	}

	public ControladorTwitterEmpresa() {
		super();
	}
}
