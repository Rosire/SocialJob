package modelo.dao;

import java.util.ArrayList;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.TareaRepetitivaAsignada;
import modelo.hibernate.config.Sesion;

public class TareaRepetitivaAsignadaDAO {

	private Sesion sesionPostgres;

	public TareaRepetitivaAsignadaDAO() {
		super();
	}

	public void registrarTareaRepetitivaAsignada(TareaRepetitivaAsignada dato) {
		@SuppressWarnings("static-access")
		Session session = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(dato);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
		}
	}

	public void actualizarTareaRepetitivaAsignada(TareaRepetitivaAsignada dato) {
		@SuppressWarnings("static-access")
		Session em = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = em.beginTransaction();
			em.update(dato);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			throw e;
		} finally {
			em.close();
		}
	}

	public List<TareaRepetitivaAsignada> obtenerTareasRepetitivasAsignadas() {
		List<TareaRepetitivaAsignada> datos = new ArrayList<TareaRepetitivaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitivaAsignada>) session.createCriteria(
					TareaRepetitivaAsignada.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return datos;
	}

	public TareaRepetitivaAsignada obtenerTareaRepetitivaAsignada(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		TareaRepetitivaAsignada dato = null;
		try {
			dato = (TareaRepetitivaAsignada) sesion.get(
					TareaRepetitivaAsignada.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			sesion.close();
		}
		return dato;
	}

	public List<TareaRepetitivaAsignada> obtenerTareasRepetitivasAsignadasPorEmpleado(
			int idEmpleado) {
		List<TareaRepetitivaAsignada> datos = new ArrayList<TareaRepetitivaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitivaAsignada>) session
					.createQuery(
							"FROM TareaRepetitivaAsignada tra WHERE tra.empleado.id=:idEmpleado")
					.setParameter("idEmpleado", idEmpleado).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public List<TareaRepetitivaAsignada> obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatus(
			int idEmpleado, int idEstatusTareaAsignada) {
		List<TareaRepetitivaAsignada> datos = new ArrayList<TareaRepetitivaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitivaAsignada>) session
					.createQuery(
							"FROM TareaRepetitivaAsignada tra WHERE tra.empleado.id=:idEmpleado AND tra.estatusTareaAsignada.id=:idEstatusTareaAsignada")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("idEstatusTareaAsignada",
							idEstatusTareaAsignada).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public List<TareaRepetitivaAsignada> obtenerTareasRepetitivasAsignadasPorTareaRepetitivaPorEstatus(
			int idTareaRepetitiva, String estatus) {
		List<TareaRepetitivaAsignada> datos = new ArrayList<TareaRepetitivaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitivaAsignada>) session
					.createQuery(
							"FROM TareaRepetitivaAsignada tra WHERE tra.tareaRepetitiva.id = :idTareaRepetitiva AND tra.estatusTareaAsignada.nombre = :estatus")
					.setParameter("idTareaRepetitiva", idTareaRepetitiva)
					.setParameter("estatus", estatus).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public List<TareaRepetitivaAsignada> obtenerTareasRepetitivasAsignadasPorEmpleadoPorEstatusNombre(
			int idEmpleado, String nombreEstatusTareaAsignada) {
		List<TareaRepetitivaAsignada> datos = new ArrayList<TareaRepetitivaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitivaAsignada>) session
					.createQuery(
							"FROM TareaRepetitivaAsignada tra WHERE tra.empleado.id=:idEmpleado AND tra.estatusTareaAsignada.nombre=:nombreEstatusTareaAsignada")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("nombreEstatusTareaAsignada",
							nombreEstatusTareaAsignada).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public List<TareaRepetitivaAsignada> obtenerTareasRepetitivasAsignadasSubalternosPorEstatusNombre(
			int idSupervisor, String nombreEstatus) {
		List<TareaRepetitivaAsignada> datos = new ArrayList<TareaRepetitivaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitivaAsignada>) session
					.createQuery(
							"FROM TareaRepetitivaAsignada tra WHERE tra.estatusTareaAsignada.nombre=:nombreEstatus AND tra.tareaRepetitiva.cargo.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id =:idSupervisor )")
					.setParameter("idSupervisor", idSupervisor)
					.setParameter("nombreEstatus", nombreEstatus).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}
		return datos;
	}

	public List<TareaRepetitivaAsignada> obtenerTareasRepetitivasAsignadasSubalternos(
			int idSupervisor) {
		List<TareaRepetitivaAsignada> datos = new ArrayList<TareaRepetitivaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitivaAsignada>) session
					.createQuery(
							"FROM TareaRepetitivaAsignada tra WHERE tra.tareaRepetitiva.cargo.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id =:idSupervisor )")
					.setParameter("idSupervisor", idSupervisor).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}
		return datos;
	}

}
