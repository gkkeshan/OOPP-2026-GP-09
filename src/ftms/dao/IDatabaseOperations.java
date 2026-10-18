package ftms.dao;

import java.sql.SQLException;
import java.util.List;

/** Shared DAO contract (replace with the group's version if it differs). */
public interface IDatabaseOperations<T> {
    boolean insert(T obj) throws SQLException;
    boolean update(T obj) throws SQLException;
    boolean delete(String id) throws SQLException;
    T findById(String id) throws SQLException;
    List<T> findAll() throws SQLException;
}
