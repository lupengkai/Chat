package service;


import dao.UserMySQLDao;
import model.User;
import util.PasswordNotCorrectException;
import util.UserNotFoundException;

import java.util.List;

/**
 * Created by tage on 3/23/16.
 */
public class UserService {

    public static UserService us = null;
    private UserMySQLDao dao = null;

    public UserMySQLDao getDao() {
        return dao;
    }

    public void setDao(UserMySQLDao dao) {
        this.dao = dao;
    }

    static {
        if (us == null) {
            us = new UserService();
            us.setDao(new UserMySQLDao());
        }
    }

    public static UserService getInstance() {
        return us;
    }


    public void save(User user) {
       dao.save(user);
    }

    public User loadById(int id) {
       return dao.loadById(id);
    }
    public User loadByName(String name) {
       return dao.loadByName(name);
    }

    public List<User> getUsers() {
        return dao.getUsers();
    }

    public  void deleteUser(int id) {
        dao.deleteUser(id);
    }
    public  User validate(String username, String password) throws UserNotFoundException, PasswordNotCorrectException {
       return dao.validate(username, password);
    }










}
