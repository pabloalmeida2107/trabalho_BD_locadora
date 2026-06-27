package trabalho_BD.sistema_locadora.responseDTO;

import trabalho_BD.sistema_locadora.Enum.RentalStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RentalResponseDTO(
        UUID id,
        UUID customerId,
        String customerName,
        UUID copyId,
        String movieTitle,
        LocalDate rentedAt,
        LocalDate returnedAt,
        LocalDate dueDate,
        RentalStatus rentalStatus,   // era: availabilityStatus
        List<PaymentResponseDTO> payments
) { }
