package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.TareaAsignada;
import modelo.hibernate.config.Sesion;

public class TareaAsignadaDAO {

	private Sesion sesionPostgres;

	public TareaAsignadaDAO() {
		super();
	}

	public void registrarTareaAsignada(TareaAsignada dato) {
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

	public void actualizarTareaAsignada(TareaAsignada dato) {
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

	public List<TareaAsignada> obtenerTareasAsignadas() {

		List<TareaAsignada> datos = new ArrayList<TareaAsignada>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<TareaAsignada>) em
					.createCriteria(TareaAsignada.class).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}

		return datos;

	}

	public TareaAsignada obtenerTareaAsignada(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		TareaAsignada dato = null;
		try {
			dato = (TareaAsignada) sesion.get(TareaAsignada.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			sesion.close();
		}
		return dato;
	}

	public List<TareaAsignada> obtenerTareasAsignadasPorEmpleado(int idEmpleado) {
		List<TareaAsignada> datos = new ArrayList<TareaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaAsignada>) session
					.createQuery(
							"FROM TareaAsignada ta WHERE ta.cargoEmpleado.empleado.id=:idEmpleado")
					.setParameter("idEmpleado", idEmpleado).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public List<TareaAsignada> obtenerTareasAsignadasPorEmpleadoPorEstatusNombre(
			int idEmpleado, String nombreEstatus) {
		List<TareaAsignada> datos = new ArrayList<TareaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaAsignada>) session
					.createQuery(
							"FROM TareaAsignada ta WHERE ta.cargoEmpleado.empleado.id=:idEmpleado AND ta.estatusTareaAsignada.nombre=:nombreEstatus")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("nombreEstatus", nombreEstatus).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}
		return datos;
	}

	public List<TareaAsignada> obtenerTareasAsignadasSubalternosPorEstatusNombre(
			int idSupervisor, String nombreEstatus) {
		List<TareaAsignada> datos = new ArrayList<TareaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaAsignada>) session
					.createQuery(
							"FROM TareaAsignada ta WHERE ta.estatusTareaAsignada.nombre=:nombreEstatus AND ta.cargoEmpleado.cargo.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id =:idSupervisor )")
					.setParameter("idSupervisor", idSupervisor)
					.setParameter("nombreEstatus", nombreEstatus).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}
		return datos;
	}

	public List<TareaAsignada> obtenerTareasAsignadasSubalternos(
			int idSupervisor) {
		List<TareaAsignada> datos = new ArrayList<TareaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaAsignada>) session
					.createQuery(
							"FROM TareaAsignada ta WHERE ta.cargoEmpleado.cargo.cargoSuperior.id IN (SELECT ce.cargo.id FROM CargoEmpleado ce WHERE ce.empleado.id =:idSupervisor )")
					.setParameter("idSupervisor", idSupervisor).list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return datos;
	}

	public List<TareaAsignada> obtenerTareasAsignadasPorEmpleadoPorEstatusNombreLimite(
			int idEmpleado, String nombreEstatus) {
		List<TareaAsignada> datos = new ArrayList<TareaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaAsignada>) session
					.createQuery(
							"FROM TareaAsignada ta WHERE ta.cargoEmpleado.empleado.id=:idEmpleado AND ta.estatusTareaAsignada.nombre=:nombreEstatus")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("nombreEstatus", nombreEstatus)
					.setMaxResults(3).list();
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}
		return datos;

	}

	public long obtenerCantidadTareasAsignadasPorEmpleadoPorEstatusNombre(
			int idEmpleado, String nombreEstatus) {
		Session session = sesionPostgres.openSession();
		long dato = 0;
		try {
			dato = (Long) session
					.createQuery(
							"SELECT COUNT(ta.id) FROM TareaAsignada ta WHERE ta.cargoEmpleado.empleado.id = :idEmpleado AND ta.estatusTareaAsignada.nombre = :nombreEstatus")
					.setParameter("idEmpleado", idEmpleado)
					.setParameter("nombreEstatus", nombreEstatus)
					.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
		return dato;
	}

	public List<TareaAsignada> obtenerTareasAsignadasPorEmpleadoPorEstatus(
			int idEmpleado, int idEstatusTareaAsignada) {
		List<TareaAsignada> datos = new ArrayList<TareaAsignada>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<TareaAsignada>) session
					.createQuery(
							"FROM TareaAsignada ta WHERE ta.cargoEmpleado.empleado.id=:idEmpleado AND ta.estatusTareaAsignada.is=:idEstatusTareaAsignada")
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

	public float obtenerPorcentajesDeTareasAsignadasCulminadasATiempo(
			int idEmpresa) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {

			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa ")
					.setParameter("idEmpresa", idEmpresa).uniqueResult();
			if (divisor != 0) {
				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa)  AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa "
										+ "AND (tareaasignada.eficiencia < tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa).uniqueResult();
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasFueraDeTiempo(
			int idEmpresa) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa ")
					.setParameter("idEmpresa", idEmpresa).uniqueResult();
			if (divisor != 0) {

				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa)  AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa "
										+ "AND (tareaasignada.eficiencia > tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa).uniqueResult();
			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasJustoATiempo(
			int idEmpresa) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa ")
					.setParameter("idEmpresa", idEmpresa).uniqueResult();
			if (divisor != 0) {
				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa)  AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa "
										+ "AND (tareaasignada.eficiencia = tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasJustoATiempoPorEmpleado(
			int idEmpresa, int idEmpleado) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
									+ "WHERE cargoempleado.idempleado = :idEmpleado ) ")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idEmpleado", idEmpleado).uniqueResult();
			if (divisor != 0) {
				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idempleado = :idEmpleado ) ) AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa  AND "
										+ "tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idempleado = :idEmpleado ) "
										+ "AND (tareaasignada.eficiencia = tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa)
						.setParameter("idEmpleado", idEmpleado).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasFueraDeTiempoPorEmpleado(
			int idEmpresa, int idEmpleado) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {

			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
									+ "WHERE cargoempleado.idempleado = :idEmpleado ) ")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idEmpleado", idEmpleado).uniqueResult();
			if (divisor != 0) {
				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idempleado = :idEmpleado ) ) AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa  AND "
										+ "tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idempleado = :idEmpleado ) "
										+ "AND (tareaasignada.eficiencia > tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa)
						.setParameter("idEmpleado", idEmpleado).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasATiempoPorEmpleado(
			int idEmpresa, int idEmpleado) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
									+ "WHERE cargoempleado.idempleado = :idEmpleado ) ")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idEmpleado", idEmpleado).uniqueResult();
			if (divisor != 0) {
				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idempleado = :idEmpleado ) ) AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa  AND "
										+ "tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idempleado = :idEmpleado ) "
										+ "AND (tareaasignada.eficiencia < tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa)
						.setParameter("idEmpleado", idEmpleado).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasATiempoPorCargo(
			int idEmpresa, int idCargo) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 AND "
									+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
									+ "WHERE cargoempleado.idcargo = :idCargo )")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idCargo", idCargo).uniqueResult();
			if (divisor != 0) {

				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 AND "
										+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo ) ) AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4  AND tarea.idempresa=:idEmpresa  AND "
										+ "tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo ) "
										+ "AND (tareaasignada.eficiencia < tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa)
						.setParameter("idCargo", idCargo).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasFueraDeTiempoPorCargo(
			int idEmpresa, int idCargo) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
									+ "WHERE cargoempleado.idcargo = :idCargo )")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idCargo", idCargo).uniqueResult();
			if (divisor != 0) {

				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo ) ) AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa  AND "
										+ "tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo ) "
										+ "AND (tareaasignada.eficiencia > tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa)
						.setParameter("idCargo", idCargo).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasJustoATiempPorCargo(
			int idEmpresa, int idCargo) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
									+ "WHERE cargoempleado.idcargo = :idCargo )")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idCargo", idCargo).uniqueResult();
			if (divisor != 0) {
				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo ) ) AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa  AND "
										+ "tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo ) "
										+ "AND (tareaasignada.eficiencia = tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa)
						.setParameter("idCargo", idCargo).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasJustoATiempPorCargoSubAlternoYEmpleado(
			int idEmpresa, int idCargo, int idEmpleado) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
									+ "WHERE cargoempleado.idcargo = :idCargo  AND cargoempleado.idempleado=:idEmpleado)")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idCargo", idCargo)
					.setParameter("idEmpleado", idEmpleado).uniqueResult();
			if (divisor != 0) {
				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo  AND cargoempleado.idempleado=:idEmpleado ) ) AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa  AND "
										+ "tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo  AND cargoempleado.idempleado=:idEmpleado ) "
										+ "AND (tareaasignada.eficiencia = tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa)
						.setParameter("idCargo", idCargo)
						.setParameter("idEmpleado", idEmpleado).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasFueraDeTiempoPorCargoSubAlternoYEmpleado(
			int idEmpresa, int idCargo, int idEmpleado) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
									+ "WHERE cargoempleado.idcargo = :idCargo  AND cargoempleado.idempleado=:idEmpleado)")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idCargo", idCargo)
					.setParameter("idEmpleado", idEmpleado).uniqueResult();
			if (divisor != 0) {
				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo  AND cargoempleado.idempleado=:idEmpleado ) ) AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa  AND "
										+ "tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo  AND cargoempleado.idempleado=:idEmpleado ) "
										+ "AND (tareaasignada.eficiencia > tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa)
						.setParameter("idCargo", idCargo)
						.setParameter("idEmpleado", idEmpleado).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}

	public float obtenerPorcentajesDeTareasAsignadasCulminadasATiempoPorCargoSubAlternoYEmpleado(
			int idEmpresa, int idCargo, int idEmpleado) {
		float datos = 0;
		float divisor = 0;
		Session session = sesionPostgres.openSession();
		try {
			divisor = (float) session
					.createSQLQuery(
							"SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
									+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
									+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
									+ "WHERE cargoempleado.idcargo = :idCargo  AND cargoempleado.idempleado=:idEmpleado)")
					.setParameter("idEmpresa", idEmpresa)
					.setParameter("idCargo", idCargo)
					.setParameter("idEmpleado", idEmpleado).uniqueResult();
			if (divisor != 0) {
				datos = (float) session
						.createSQLQuery(
								"SELECT CAST(COUNT(tareaasignada.id)*100 AS REAL)/(SELECT CAST(COUNT(tareaasignada.id) AS REAL)"
										+ " FROM tareaasignada, tarea  WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND "
										+ "tarea.idempresa=:idEmpresa AND tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo  AND cargoempleado.idempleado=:idEmpleado ) ) AS Porcentaje FROM tarea, tareaasignada "
										+ "WHERE tareaasignada.idestatustareaasignada=4 /*CULMINADA*/ AND tarea.idempresa=:idEmpresa  AND "
										+ "tareaasignada.idcargoempleado IN (SELECT cargoempleado.id FROM cargoempleado "
										+ "WHERE cargoempleado.idcargo = :idCargo  AND cargoempleado.idempleado=:idEmpleado ) "
										+ "AND (tareaasignada.eficiencia < tareaasignada.duracion "
										+ "AND tareaasignada.idtarea=tarea.id)")
						.setParameter("idEmpresa", idEmpresa)
						.setParameter("idCargo", idCargo)
						.setParameter("idEmpleado", idEmpleado).uniqueResult();
			}
		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			session.close();
		}

		return datos;
	}
}
