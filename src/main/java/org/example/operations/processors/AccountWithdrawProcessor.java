package org.example.operations.processors;


import org.example.account.AccountService;
import org.example.operations.ConsoleOperationType;
import org.example.user.User;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class AccountWithdrawProcessor implements org.example.Operations.OperationCommandProcessor {
     private final AccountService accountService;
     private final Scanner scanner;
     private final UserService userService;

    public AccountWithdrawProcessor(AccountService accountService, Scanner scanner, UserService userService) {
        this.accountService = accountService;
        this.scanner = scanner;
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        System.out.println("Input user for withdraw money: ");
        String userlogin = scanner.nextLine();
        User user = userService.findUserByLogin(userlogin);
        accountService.withdrawMoney(user);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_WITHDRAW;
    }
}