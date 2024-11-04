package org.example.account;


import org.example.Configurations.TransactionHelper;
import org.example.user.User;
import org.example.user.UserService;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

@Service
public class AccountService {
    private SessionFactory sessionFactory;
    private TransactionHelper transactionHelper;
    private AccountProperties accountProperties;
    private final Scanner scanner;
    private UserService userService;


    public AccountService(
            SessionFactory sessionFactory,
            TransactionHelper transactionHelper,
            AccountProperties accountProperties,
            Scanner scanner
    ) {
        this.sessionFactory = sessionFactory;
        this.transactionHelper = transactionHelper;
        this.accountProperties = accountProperties;
        this.scanner = scanner;

    }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Account createAccount(User user) {
        return transactionHelper.executeInTransaction(() ->{
            int initialAmount = user.getAccountList().isEmpty() ? accountProperties.getDefaultAccountAmount() : 0;

            Account newAccount = new Account(
                    null,
                    user,
                    initialAmount
            );
           sessionFactory.getCurrentSession().persist(newAccount);
            return newAccount;
        });

    }

    public void accountDeposit(Account account, int sum) {
        transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();
            if (!account.getUser().getAccountList().contains(account))
                throw new IllegalArgumentException("user=%s, account not found"
                        .formatted(account.getUser().getLogin()));

            if (sum <= 0) {
                throw new IllegalArgumentException("Money cant be negative");
            }
            account.setMoneyAmount(account.getMoneyAmount() + sum);
            session.update(account);
            return 0;
        });
    }

    public void accountTransferMoney(User userFrom, User userTo) {
        transactionHelper.executeInTransaction(() -> {

            var session = sessionFactory.getCurrentSession();

            System.out.println("Viberite s kakogo scheta spisat' money y polzovatela %s: "
                    .formatted(userFrom.getLogin()));

            List<Account> accountListUserFrom = userFrom.getAccountList();
            for (int i = 0; i < accountListUserFrom.size(); i++) {
                System.out.println(" Schet: " + accountListUserFrom.get(i).getId() + " Balance: " + accountListUserFrom.get(i).getMoneyAmount());
            }
            int accountIndexFrom = scanner.nextInt() - 1;


            System.out.println("Viberite na kakoy account zachislit money polzovately %s: "
                    .formatted(userTo.getLogin()));

            List<Account> accountListUserTo = userTo.getAccountList();
            for (int i = 0; i < accountListUserTo.size(); i++) {
                System.out.println(" Schet: " + accountListUserTo.get(i).getId() + " Balance: " + accountListUserTo.get(i).getMoneyAmount());
            }
            int accountIndexTo = scanner.nextInt() - 1;

            if (accountIndexTo < 0 || accountIndexTo >= accountListUserTo.size()) {
                throw new IllegalArgumentException(" neverny index ");
            }

            Account accountFrom = accountListUserFrom.get(accountIndexFrom);
            Account accountTo = accountListUserTo.get(accountIndexTo);

            System.out.println("Input money for perekinyt': ");
            int moneyToTransfer = scanner.nextInt();

            if (accountFrom.getMoneyAmount() < moneyToTransfer) {
                throw new IllegalArgumentException("Na schety =%s, polzovetela = %s, nedostatochno deneg"
                        .formatted(accountIndexFrom,userFrom.getLogin()));
            }

            if (moneyToTransfer <= 0) {
                throw new IllegalArgumentException("neverno ykazana summa");
            }


            accountFrom.setMoneyAmount(accountFrom.getMoneyAmount() - moneyToTransfer);
            accountTo.setMoneyAmount(accountTo.getMoneyAmount() + moneyToTransfer);

            session.update(accountFrom);
            session.update(accountTo);

            System.out.println("Operation was a successfully");
            return 0;
        });

    }


    public Account closeAccount(User user) {
       return transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();

            User persistedUser = session.get(User.class, user.getId());

            if (persistedUser == null)
                throw new IllegalArgumentException("user with login =%s, not found"
                        .formatted(user.getLogin()));

            if(persistedUser.getAccountList().size() <= 1)
                throw new IllegalArgumentException("user with login =%s, cant close account"
                        .formatted(persistedUser.getLogin()));


            List<Account> userAccountList = persistedUser.getAccountList();
            for (int i = 0; i < userAccountList.size(); i++) {
                System.out.println(" Schet: " + userAccountList.get(i).getId() + " Balance: " + userAccountList.get(i).getMoneyAmount());
            }

            int accountIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (accountIndex < 0 || accountIndex >= userAccountList.size()) {
                throw new IllegalArgumentException("invalid index");
            }

            Account accountToDelete = userAccountList.get(accountIndex);

            Account accountForSaveMoney = userAccountList.stream()
                    .filter(account -> !account.equals(accountIndex))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("account not found"));

            accountForSaveMoney.setMoneyAmount(accountForSaveMoney.getMoneyAmount() + accountToDelete.getMoneyAmount());
            accountToDelete.setMoneyAmount(0);

            session.remove(accountToDelete);
            session.update(persistedUser);

            return accountToDelete;
        });
    }

    public void withdrawMoney(User user) {
        transactionHelper.executeInTransaction(() -> {
            var session = sessionFactory.getCurrentSession();

            User persistedUser = session.get(User.class, user.getId());

            if (persistedUser == null)
                throw new IllegalArgumentException("user with login =%s, not found"
                        .formatted(user.getLogin()));

            List<Account> userAccountList = persistedUser.getAccountList();

            for (int i = 0; i < userAccountList.size(); i++) {
                System.out.println(" Schet: " + userAccountList.get(i).getId() + " Balance: " + userAccountList.get(i).getMoneyAmount());
            }

            System.out.println("Input account for withdrawMoney: ");
            int accountIndex = Integer.parseInt(scanner.nextLine()) - 1;

            if (accountIndex > userAccountList.size() || accountIndex <= -1) {
                throw new IllegalArgumentException("invalid account index");
            }

            System.out.println("Input money to withdraw: ");
            int moneyToWithdraw = scanner.nextInt();
            Account accountToWithdraw = userAccountList.get(accountIndex);

            if (moneyToWithdraw <= 0)
                throw new IllegalArgumentException("invalid summ for withdraw");

            if (accountToWithdraw.getMoneyAmount() < moneyToWithdraw) {
                throw new IllegalArgumentException("nedostatochno deneg");
            }

            accountToWithdraw.setMoneyAmount(accountToWithdraw.getMoneyAmount() - moneyToWithdraw);
            System.out.println("operation was successfully");
            return 0;
        });

    }
}
