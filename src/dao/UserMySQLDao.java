package dao;

import model.User;
import util.DB;
import util.PasswordNotCorrectException;
import util.UserNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tage on 3/23/16.
 */
public class UserMySQLDao {
    public void save(User user) {
        Connection conn = null;
        PreparedStatement pStmt = null;
        conn = DB.getConn();
        String sql = "insert into ruser values ( null, ?, ?)";

        pStmt = DB.prepStmt(conn, sql);

        try {
            pStmt.setString(1, user.getUsername());
            pStmt.setString(2, user.getPassword());
            pStmt.execute();
     
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeStmt(pStmt);
            DB.closeConn(conn);
        }


    }


    public  User loadById(int id) {
        User u = null;
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = DB.getConn();
            String sql = "SELECT * from ruser where id = " + id;
            rs = DB.executeQuery(conn, sql);
            if (rs.next()) {
                u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
              
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeRs(rs);
            DB.closeConn(conn);
        }


        return u;
    }


    public  User loadByName(String name) {
        User u = null;
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = DB.getConn();
            String sql = "SELECT * from ruser where username = '" + name + "'";
            rs = DB.executeQuery(conn, sql);
            if (rs.next()) {
                u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeRs(rs);
            DB.closeConn(conn);
        }


        return u;
    }
    
    
    
    public  List<User> getUsers() {
        List<User> list = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;

        try {
            conn = DB.getConn();
            String sql = "select * from ruser";
            rs = DB.executeQuery(conn, sql);
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
           
                list.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeRs(rs);
            DB.closeConn(conn);
        }

        return list;
    }

    public  void deleteUser(int id) {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = DB.getConn();
            stmt = DB.getStmt(conn);
            stmt.executeUpdate("DELETE FROM ruser WHERE id = " + id);


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeStmt(stmt);
            DB.closeConn(conn);
        }
    }

    public  User validate(String username, String password) throws UserNotFoundException, PasswordNotCorrectException {
        Connection conn = null;
        ResultSet rs = null;
        User u = null;

        try {
            conn = DB.getConn();
            String sql = "select * from ruser where username = '" + username + "'";
            rs = DB.executeQuery(conn, sql);
            if (!rs.next()) {
                throw new UserNotFoundException();
            } else if (!rs.getString("password").equals(password)) {
                throw new PasswordNotCorrectException();
            } else {
                u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DB.closeRs(rs);
            DB.closeConn(conn);
        }
        return u;

    }


}
