package org.example.smartlawgt.command.dtos;

import lombok.Data;
import org.example.smartlawgt.command.entities.TransactionMethod;

import java.util.UUID;

@Data
public class PurchaseRequestDTO {
    private UUID userId;
    private UUID usagePackageId;
    private TransactionMethod transactionMethod;
}
