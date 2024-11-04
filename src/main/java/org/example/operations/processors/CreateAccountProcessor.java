package org.example.operations.processors;

import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.user.User;
import org.example.user.UserService;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreateAccountProcessor implements org.example.Operations.OperationCommandProcessor {

    private final AccountService accountService;
    private final Scanner scanner;
    private final UserService userService;
    private final SessionFactory sessionFactory;

    public CreateAccountProcessor(AccountService accountService, Scanner scanner, UserService userService, SessionFactory sessionFactory) {
        this.accountService = accountService;
        this.scanner = scanner;
        this.userService = userService;
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void processOperation() {
        System.out.println("Input user login: ");
        String login = scanner.nextLine();
        User user = userService.findUserByLogin(login);

        accountService.createAccount(user);

        System.out.println("Account created!");

    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }
}

