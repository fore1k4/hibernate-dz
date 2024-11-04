package org.example.operations.processors;

import org.example.account.Account;
import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.user.User;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class CloseAccountProcessor implements org.example.Operations.OperationCommandProcessor {
    private final AccountService accountService;
    private final UserService userService;
    private final Scanner scanner;

    public CloseAccountProcessor(AccountService accountService, UserService userService, Scanner scanner) {
        this.accountService = accountService;
        this.userService = userService;
        this.scanner = scanner;
    }


    @Override
    public void processOperation() {
        System.out.println("Input user: ");
        String userLogin = scanner.nextLine();
        User user = userService.findUserByLogin(userLogin);

        accountService.closeAccount(user);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
