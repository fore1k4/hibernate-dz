package org.example.operations.processors;

import org.example.account.Account;
import org.example.account.AccountProperties;
import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.user.User;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Scanner;

@Component
public class AccountTransferProcessor implements org.example.Operations.OperationCommandProcessor {
    private final AccountService accountService;
    private final UserService userService;
    private final Scanner scanner;

    public AccountTransferProcessor(AccountService accountService, UserService userService, Scanner scanner) {
        this.accountService = accountService;
        this.userService = userService;
        this.scanner = scanner;
    }

    @Override
    public void processOperation() {
        System.out.println("Input user from: ");
        String loginUserFrom = scanner.nextLine();
        User userFrom = userService.findUserByLogin(loginUserFrom);

        System.out.println("Input user to: ");
        String loginUserTo = scanner.nextLine();
        User userTo = userService.findUserByLogin(loginUserTo);

        accountService.accountTransferMoney(userFrom, userTo);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}