package controlador;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import modelo.dao.CargoDAO;
import modelo.dao.TareaAsignadaDAO;
import modelo.dao.UsuarioDAO;
import modelo.dto.Cargo;
import modelo.dto.Usuario;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
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
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

public class ControladorGraficaEficienciaTareaAsignadaPorEmpleadosEmpresa
		extends SelectorComposer<Component> {

	@Wire
	Charts chart;
	@Wire
	private Listbox listaEmpleados;

	// Session
	private Session miSession = Sessions.getCurrent();
	private Usuario usuarioSession = (Usuario) miSession
			.getAttribute("usuario");

	private TareaAsignadaDAO tareaAsignadaDAO = new TareaAsignadaDAO();
	private UsuarioDAO usuarioDAO = new UsuarioDAO();

	private Series series = new Series();
	private Chart chartOptional = new Chart();

	private Point justoATiempo = new Point("Justo a Tiempo", 0);
	private Point fueraTiempo = new Point("Fuera de Tiempo", 0);
	private Point aTiempo = new Point("A Tiempo", 0);

	private ListModel<Usuario> listaEmpleadosModel;

	public ControladorGraficaEficienciaTareaAsignadaPorEmpleadosEmpresa() {

	}

	public ListModel<Usuario> getEmpleados() {
		listaEmpleadosModel = new ListModelList<Usuario>(
				usuarioDAO
						.obtenerUsuariosEmpleadoActivosPorEmpresa(usuarioSession
								.getEmpresa().getId()));

		((ListModelList<Usuario>) listaEmpleadosModel).setMultiple(false);

		return listaEmpleadosModel;
	}

	// @Listen("onClick=#btnGenerarGrafica")
	@Command
	@Listen("onSelect=#listaEmpleados")
	public void generarGraficaPorCargoSubAlternoYEmpleadoSeleccionado() {
		justoATiempo.setColor("#d6487e");
		fueraTiempo.setColor("#999");
		aTiempo.setColor("#24C6D2 ");
		ListModelList<Object> listaEmpleadosSeleccion = (ListModelList<Object>) listaEmpleados
				.getModel();
		Set<Object> conjuntoEmpleado = listaEmpleadosSeleccion.getSelection();
		Usuario empleado = (Usuario) conjuntoEmpleado.toArray()[0];

		series = chart.getSeries();
		series.setType("pie");
		series.setName("Porcentaje de Tareas Asignadas Culminadas Por Empleado Seleccionado");

		if (conjuntoEmpleado.size() == 0) {
			showNotify("Debe Seleccionar un empleado", listaEmpleados);
			listaEmpleados.focus();
		} else {
			series.setVisible(true);

			justoATiempo
					.setY(tareaAsignadaDAO
							.obtenerPorcentajesDeTareasAsignadasCulminadasJustoATiempoPorEmpleado(
									usuarioSession.getEmpresa().getId(),
									empleado.getId()));
			aTiempo.setY(tareaAsignadaDAO
					.obtenerPorcentajesDeTareasAsignadasCulminadasATiempoPorEmpleado(
							usuarioSession.getEmpresa().getId(),
							empleado.getId()));
			fueraTiempo
					.setY(tareaAsignadaDAO
							.obtenerPorcentajesDeTareasAsignadasCulminadasFueraDeTiempoPorEmpleado(
									usuarioSession.getEmpresa().getId(),
									empleado.getId())

					);

			fueraTiempo.setSelected(true);
			fueraTiempo.setSliced(true);

			chart.setVisible(true);
			chart.setTitle("Eficiencia de Tareas Asignadas Culminadas Por Empleado Seleccionado");
			chartOptional = chart.getChart();
			chartOptional.setPlotBorderWidth(0);
			chartOptional.setPlotShadow(false);

			chart.getTooltip().setPointFormat(
					"{series.name}: <b>{point.percentage:.1f}%</b>");

			PiePlotOptions plotOptions = chart.getPlotOptions().getPie();

			plotOptions.setAllowPointSelect(true);
			plotOptions.setCursor("pointer");
			PieDataLabels dataLabels = (PieDataLabels) plotOptions
					.getDataLabels();
			dataLabels.setEnabled(true);
			dataLabels
					.setFormat("<b>{point.name}</b>: {point.percentage:.1f} %");
		}

	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		series = chart.getSeries();

		series.setType("pie");
		series.setName("Porcentaje de Tareas Asignadas Culminadas");

		series.addPoint(justoATiempo);
		series.addPoint(fueraTiempo);
		series.addPoint(aTiempo);

		fueraTiempo.setSelected(true);
		fueraTiempo.setSliced(true);

		chartOptional = chart.getChart();
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
		chart.setVisible(false);

	}

	private void showNotify(String msg, Component ref) {
		Clients.showNotification(msg, "error", ref, "end_center", 3000, true);
	}

	private void showNotifyInfo(String msg, Component ref) {
		Clients.showNotification(msg, "info", ref, "middle_center", 5000, true);
	}

}
