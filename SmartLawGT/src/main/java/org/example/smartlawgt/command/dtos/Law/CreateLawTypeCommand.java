package org.example.smartlawgt.command.dtos.Law;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLawTypeCommand {
    @NotBlank(message = "Law type name is required")
    private String name;
}
