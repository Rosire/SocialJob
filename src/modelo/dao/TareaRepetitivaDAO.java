package modelo.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.TareaRepetitiva;
import modelo.dto.TareaRepetitivaAsignada;
import modelo.hibernate.config.Sesion;
import bean.Conexion;

public class TareaRepetitivaDAO {

	private static Sesion sesionPostgres;

	public TareaRepetitivaDAO() {
		super();
	}

	public void registrarTareaRepetitiva(TareaRepetitiva dato) {
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

	public void actualizarTareaRepetitiva(TareaRepetitiva dato) {
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

	public List<TareaRepetitiva> obtenerTareasRepetitivas() {
		List<TareaRepetitiva> datos = new ArrayList<TareaRepetitiva>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitiva>) session.createCriteria(
					TareaRepetitiva.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return datos;
	}

	public TareaRepetitiva obtenerTareaRepetitiva(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		TareaRepetitiva dato = null;
		try {
			dato = (TareaRepetitiva) sesion.get(TareaRepetitiva.class, id);
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

	public static long contarTareasRepetitivasPorFrecuencias(int idFrecuencia) {
		Session session = sesionPostgres.openSession();
		long dato = 0;
		try {
			dato = (Long) session
					.createQuery(
							"SELECT COUNT(tr.id) FROM TareaRepetitiva tr WHERE tr.frecuencia.id =:idFrecuencia")
					.setParameter("idFrecuencia", idFrecuencia).uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return dato;
	}

	public List<TareaRepetitiva> obtenerTareasRepetitivasSubalternos(
			int idSupervisor) {
		List<TareaRepetitiva> datos = new ArrayList<TareaRepetitiva>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitiva>) session
					.createQuery(
							"FROM TareaRepetitiva tr WHERE tr.cargo.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id =:idSupervisor )")
					.setParameter("idSupervisor", idSupervisor).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}
		return datos;
	}

	public List<TareaRepetitiva> obtenerTareasRepetitivasSubalternosActivas(
			int idSupervisor) {
		List<TareaRepetitiva> datos = new ArrayList<TareaRepetitiva>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitiva>) session
					.createQuery(
							"FROM TareaRepetitiva tr WHERE tr.estatus.nombre = :nombreEstatus AND tr.cargo.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id = :idSupervisor )")
					.setParameter("idSupervisor", idSupervisor)
					.setParameter("nombreEstatus", "ACTIVO").list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}
		return datos;
	}

	public List<TareaRepetitiva> obtenerTareasRepetitivasSubalternosInactivas(
			int idSupervisor) {
		List<TareaRepetitiva> datos = new ArrayList<TareaRepetitiva>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaRepetitiva>) session
					.createQuery(
							"FROM TareaRepetitiva tr WHERE tr.estatus.nombre=:nombreEstatus tr.cargo.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id =:idSupervisor )")
					.setParameter("idSupervisor", idSupervisor)
					.setParameter("nombreEstatus", "INACTIVO").list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}
		return datos;
	}

}
