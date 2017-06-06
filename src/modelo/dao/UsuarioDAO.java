package modelo.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import modelo.dto.Usuario;
import modelo.hibernate.config.Sesion;

public class UsuarioDAO {

	private Sesion sesionPostgres;

	public UsuarioDAO() {
		super();
	}

	public void registrarUsuario(Usuario dato) {
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

	public void actualizarUsuario(Usuario dato) {
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

	public void eliminarUsuario(Usuario dato) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Transaction tx = null;
		try {
			tx = sesion.beginTransaction();
			sesion.delete(dato);
			tx.commit();

		} catch (Exception e) {
			tx.rollback();

			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			sesion.close();
		}
	}

	public Usuario obtenerUsuario(int id) {
		@SuppressWarnings("static-access")
		Session sesion = sesionPostgres.openSession();
		Usuario dato = null;
		try {
			dato = (Usuario) sesion.get(Usuario.class, id);
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

	public Usuario obtenerUsuario(String email, String contrasenna) {
		Usuario dato = null;
		@SuppressWarnings("static-access")
		Session em = sesionPostgres.openSession();
		try {
			dato = (Usuario) em
					.createQuery(
							"FROM Usuario u WHERE u.contrasenna = :contrasenna AND u.email = :email")
					.setParameter("contrasenna", contrasenna)
					.setParameter("email", email).uniqueResult();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}
		return dato;
	}

	public Usuario obtenerUsuario(String cedula) {
		Usuario dato = null;
		Session em = sesionPostgres.openSession();
		try {
			dato = (Usuario) em
					.createQuery("FROM Usuario u WHERE u.cedula = :cedula")
					.setParameter("cedula", cedula).uniqueResult();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}
		return dato;
	}

	public Usuario obtenerUsuarioPorEmail(String email) {
		Usuario dato = null;
		Session em = sesionPostgres.openSession();
		try {
			dato = (Usuario) em
					.createQuery("FROM Usuario u WHERE u.email = :email")
					.setParameter("email", email).uniqueResult();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}
		return dato;
	}

	public List<Usuario> obtenerUsuarios() {
		List<Usuario> datos = new ArrayList<Usuario>();
		Session session = sesionPostgres.openSession();
		try {
			datos = (List<Usuario>) session.createCriteria(Usuario.class)
					.list();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		return datos;

	}

	public List<Usuario> obtenerUsuariosActivosPorEmpresa(int idEmpresa) {
		List<Usuario> datos = new ArrayList<Usuario>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Usuario>) em
					.createQuery(
							"FROM Usuario u WHERE u.estatusUsuario.nombre = :estatus AND u.empresa.id = :idEmpresa")
					.setParameter("estatus", "ACTIVO")
					.setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Usuario> obtenerUsuariosInactivosPorEmpresa(int idEmpresa) {
		List<Usuario> datos = new ArrayList<Usuario>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Usuario>) em
					.createQuery(
							"FROM Usuario u WHERE u.estatusUsuario.nombre = :estatus  AND u.empresa.id = :idEmpresa")
					.setParameter("estatus", "INACTIVO")
					.setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Usuario> obtenerUsuariosActivosPorCargo(int idCargo) {
		List<Usuario> datos = new ArrayList<Usuario>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Usuario>) em
					.createQuery(
							"FROM Usuario u WHERE u.estatusUsuario.nombre = :estatus AND u.id IN (SELECT ce.empleado.id FROM CargoEmpleado ce WHERE ce.cargo.id = :idCargo)")
					.setParameter("estatus", "ACTIVO")
					.setParameter("idCargo", idCargo).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Usuario> obtenerUsuariosInactivosPorCargo(int idCargo) {
		List<Usuario> datos = new ArrayList<Usuario>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Usuario>) em
					.createQuery(
							"FROM Usuario u WHERE u.estatusUsuario.nombre = :estatus AND u.id IN (SELECT ce.empleado.id FROM CargoEmpleado ce WHERE ce.cargo.id = :idCargo)")
					.setParameter("estatus", "INACTIVO")
					.setParameter("idCargo", idCargo).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Usuario> obtenerUsuariosEmpleadoActivosPorEmpresa(int idEmpresa) {
		List<Usuario> datos = new ArrayList<Usuario>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Usuario>) em
					.createQuery(
							"FROM Usuario u WHERE u.estatusUsuario.nombre = :estatus AND u.tipoUsuario.nombre <> :tipo AND u.empresa.id = :idEmpresa")
					.setParameter("estatus", "ACTIVO")
					.setParameter("tipo", "ADMINISTRADOR").setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}

	public List<Usuario> obtenerUsuariosEmpleadoInactivosPorEmpresa(int idEmpresa) {
		List<Usuario> datos = new ArrayList<Usuario>();
		Session em = sesionPostgres.openSession();
		try {
			datos = (List<Usuario>) em
					.createQuery(
							"FROM Usuario u WHERE u.estatusUsuario.nombre = :estatus AND u.tipoUsuario.nombre <> :tipo AND u.empresa.id = :idEmpresa")
					.setParameter("estatus", "INACTIVO")
					.setParameter("tipo", "ADMINISTRADOR").setParameter("idEmpresa", idEmpresa).list();
		} catch (Exception e) {
			try {
				throw new Exception(e.getMessage(), e.getCause());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			em.close();
		}

		return datos;
	}
	
	public List<Usuario> obtenerUsuariosEmpleadosActivosPorSupervisor(
            int idSupervisor) {
        List<Usuario> datos = new ArrayList<Usuario>();
        Session em = sesionPostgres.openSession();
        try {
            datos = (List<Usuario>) em
                    .createQuery(
                            "FROM Usuario u WHERE u.id IN (SELECT ce.empleado.id FROM CargoEmpleado ce WHERE ce.empleado.estatusUsuario.nombre = :estatus AND ce.cargo.cargoSuperior.id IN "
                            + "(SELECT cem.cargo.id FROM CargoEmpleado cem WHERE cem.empleado.id = :idSupervisor))")
                    .setParameter("estatus", "ACTIVO")
                    .setParameter("idSupervisor", idSupervisor).list();
        } catch (Exception e) {
            try {
                throw new Exception(e.getMessage(), e.getCause());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            em.close();
        }

        return datos;
    }

}
