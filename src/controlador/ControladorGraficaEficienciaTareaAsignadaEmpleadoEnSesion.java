package controlador;

import modelo.dao.TareaAsignadaDAO;
import modelo.dto.Usuario;

import org.zkoss.chart.Chart;
import org.zkoss.chart.Charts;
import org.zkoss.chart.Point;
import org.zkoss.chart.Series;
import org.zkoss.chart.plotOptions.PieDataLabels;
import org.zkoss.chart.plotOptions.PiePlotOptions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

public class ControladorGraficaEficienciaTareaAsignadaEmpleadoEnSesion extends
		SelectorComposer<Component> {

	@Wire
	Charts chart;
	private TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	public ControladorGraficaEficienciaTareaAsignadaEmpleadoEnSesion() {
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Series series = chart.getSeries();
		series.setType("pie");
		series.setName("Porcentaje de Tareas Asignadas Culminadas");
		Point justoATiempo = new Point(
				"Justo a Tiempo",
				tareaAsignadaDAO
						.obtenerPorcentajesDeTareasAsignadasCulminadasJustoATiempoPorEmpleado(
								usuarioSession.getEmpresa().getId(),
								usuarioSession.getId()));

		Point aTiempo = new Point(
				"A Tiempo",
				tareaAsignadaDAO
						.obtenerPorcentajesDeTareasAsignadasCulminadasATiempoPorEmpleado(
								usuarioSession.getEmpresa().getId(),
								usuarioSession.getId()));

		series.addPoint(aTiempo);
		series.addPoint(justoATiempo);
		Point fueraTiempo = new Point(
				"Fuera de Tiempo",
				tareaAsignadaDAO
						.obtenerPorcentajesDeTareasAsignadasCulminadasFueraDeTiempoPorEmpleado(
								usuarioSession.getEmpresa().getId(),
								usuarioSession.getId()));
		fueraTiempo.setSelected(true);
		fueraTiempo.setSliced(true);
		series.addPoint(fueraTiempo);
		justoATiempo.setColor("#d6487e");
		fueraTiempo.setColor("#999");
		aTiempo.setColor("#24C6D2 ");

		Chart chartOptional = chart.getChart();
		chartOptional.setPlotBorderWidth(0);
		chartOptional.setPlotShadow(false);

		chart.getTooltip().setPointFormat(
				"{series.name}: <b>{point.percentage:.1f}%</b>");

		PiePlotOptions plotOptions = chart.getPlotOptions().getPie();

		plotOptions.setAllowPointSelect(true);
		plotOptions.setCursor("pointer");
		PieDataLabels dataLabels = (PieDataLabels) plotOptions.getDataLabels();
		dataLabels.setEnabled(true);
		dataLabels.setFormat("<b>{point.name}</b>: {point.percentage:.1f} %");
	}

}
