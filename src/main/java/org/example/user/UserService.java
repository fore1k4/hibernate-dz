package org.example.user;

import jakarta.transaction.Transactional;
import org.example.Configurations.TransactionHelper;
import org.example.account.Account;
import org.example.account.AccountService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private AccountService accountService;
    private SessionFactory sessionFactory;
    private TransactionHelper transactionHelper;

    public UserService(
            AccountService accountService,
            SessionFactory sessionFactory,
            TransactionHelper transactionHelper
    ) {
        this.accountService = accountService;
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
    }

    public User createUser(String login) {
          return transactionHelper.executeInTransaction(() -> {
                var session = sessionFactory.getCurrentSession();
                var existedUser = session.createQuery("FROM User WHERE login = :login", User.class)
                        .setParameter("login",login)
                        .getSingleResultOrNull();

                if (existedUser != null) {
                    throw new IllegalArgumentException("user with login = %s already exists"
                            .formatted(login));
                }

                User user = new User(null, login, new ArrayList<>());
                session.persist(user);

                accountService.createAccount(user);
                return user;
            });
    }

    public Optional<User> findUserById(Long id) {
       return transactionHelper.executeInTransaction( () -> {
            var session = sessionFactory.getCurrentSession();
            var result = session.get(User.class,id);
            if (result == null) {
                throw new IllegalArgumentException("user with id = %s, not found"
                        .formatted(String.valueOf(id)));
            }
            return Optional.ofNullable(result);
        });
    }

    public List<Account> getUserAccounts(User user) {
       return transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();

            List<Account> accountList = session.createQuery("Select a FROM Account a WHERE user = :user", Account.class)
                    .setParameter("user", user)
                    .list();

            if (accountList.isEmpty())
                throw new IllegalArgumentException("user with login = %s, dont have accounts"
                        .formatted(user.getLogin()));

            return accountList;
        });
    }

    public User findUserByLogin(String login) {
      return transactionHelper.executeInTransaction(() ->{
            var session = sessionFactory.getCurrentSession();

            User user = session.createQuery("Select u FROM User u WHERE login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResultOrNull();
            if (user == null) {
                throw new IllegalArgumentException("user with login =%s, not found"
                        .formatted(login));
            }

            return user;
        });
    }



    public List<User> getAllUsers() {
        return transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();

            List<User> usersList = session.createQuery("Select u FROM User u JOIN FETCH u.accountList", User.class)
                    .list();

            return usersList;
        });
    }
}
