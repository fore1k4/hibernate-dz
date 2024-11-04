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
public class DepositAccountProcessor implements org.example.Operations.OperationCommandProcessor {
    private final AccountService accountService;
    private final Scanner scanner;
    private final UserService userService;
    public DepositAccountProcessor(AccountService accountService, Scanner scanner, UserService userService) {
        this.accountService = accountService;
        this.scanner = scanner;
        this.userService = userService;
    }


    @Override
    public void processOperation() {
        System.out.println("Input user login:");
        String login = scanner.nextLine();
        User user = userService.findUserByLogin(login);

        List<Account> accountList = user.getAccountList();
        System.out.println("Users accounts: ");
        for (int i = 0; i < accountList.size(); i++) {
            System.out.println(" Schet: " + accountList.get(i).getId() + " Balance: " + accountList.get(i).getMoneyAmount());
        }

        System.out.println("Input schet for deposit money");
        int accountIndex = scanner.nextInt() - 1;

        Account account = accountList.get(accountIndex);

        System.out.println("Input money for deposit: ");
        int moneyToDeposit = scanner.nextInt();
        accountService.accountDeposit(account, moneyToDeposit);
        System.out.println("operation successfully");

    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}
