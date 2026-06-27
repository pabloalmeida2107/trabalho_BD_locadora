package trabalho_BD.sistema_locadora.responseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentResponseDTO(
        UUID paymentId,
        UUID rentalId,
        BigDecimal amount,
        LocalDate paidAt,
        String method,
        String description
) { }
