package trabalho_BD.sistema_locadora.requestDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestDTO(
        @NotNull(message = "O ID do aluguel associado é obrigatório.")
        UUID rentalId,

        @NotNull(message = "O valor do pagamento é obrigatório.")
        @Positive(message = "O valor deve ser maior que zero.")
        BigDecimal amount,

        @NotBlank(message = "O método de pagamento é obrigatório.")
        String method,

        String description
) {
}
