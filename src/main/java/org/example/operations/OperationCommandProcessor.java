package org.example.Operations;

import org.example.operations.ConsoleOperationType;

public interface OperationCommandProcessor {
    void processOperation();

    ConsoleOperationType getOperationType();
}
