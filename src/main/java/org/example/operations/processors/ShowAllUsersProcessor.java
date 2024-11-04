package org.example.operations.processors;

import org.example.operations.ConsoleOperationType;
import org.example.user.UserService;
import org.springframework.stereotype.Component;

@Component
public class ShowAllUsersProcessor  implements org.example.Operations.OperationCommandProcessor {
    private final UserService userService;

    public ShowAllUsersProcessor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void processOperation() {
        userService.getAllUsers()
                .forEach(System.out::println);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.SHOW_ALL_USERS;
    }
}
