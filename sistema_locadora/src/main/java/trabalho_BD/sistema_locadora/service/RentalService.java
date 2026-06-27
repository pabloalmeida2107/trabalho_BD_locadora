package trabalho_BD.sistema_locadora.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import trabalho_BD.sistema_locadora.Enum.AvailabilityStatus;
import trabalho_BD.sistema_locadora.Enum.RentalStatus;
import trabalho_BD.sistema_locadora.exception.BusinessException;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.models.*;
import trabalho_BD.sistema_locadora.repository.CopyRepository;
import trabalho_BD.sistema_locadora.repository.CustomerRepository;
import trabalho_BD.sistema_locadora.repository.RentalRepository;
import trabalho_BD.sistema_locadora.requestDTO.RentalRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.PaymentResponseDTO;
import trabalho_BD.sistema_locadora.responseDTO.RentalResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CopyRepository copyRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public RentalResponseDTO createRental(RentalRequestDTO request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado!"));

        Copy copy = copyRepository.findById(request.copyId())
                .orElseThrow(() -> new ResourceNotFoundException("Cópia não encontrada!"));

        if (copy.getAvailabilityStatus() != AvailabilityStatus.AVAILABLE) {
            throw new BusinessException("Esta cópia não está disponível para aluguel!");
        }

        copy.setAvailabilityStatus(AvailabilityStatus.RENTED);
        copyRepository.save(copy);

        Rental newRental = new Rental();
        newRental.setCustomer(customer);
        newRental.setCopy(copy);
        newRental.setRentedAt(LocalDate.now());
        newRental.setDueDate(request.dueDate());
        newRental.setRentalStatus(RentalStatus.IN_PROGRESS);

        return convertToResponseDTO(rentalRepository.save(newRental));
    }

    @Transactional
    public RentalResponseDTO returnMovie(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluguel não encontrado!"));

        if (rental.getRentalStatus() == RentalStatus.RETURNED) {
            throw new BusinessException("Este aluguel já foi encerrado!");
        }

        Copy copy = rental.getCopy();
        copy.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        copyRepository.save(copy);

        rental.setReturnedAt(LocalDate.now());
        rental.setRentalStatus(RentalStatus.RETURNED);

        return convertToResponseDTO(rentalRepository.save(rental));
    }

    public RentalResponseDTO findById(UUID rentalId) {
        return convertToResponseDTO(rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluguel não encontrado!")));
    }

    public List<RentalResponseDTO> findAll() {
        return rentalRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public List<RentalResponseDTO> findHistoricoByCustomerId(UUID customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Cliente não encontrado!");
        }
        return rentalRepository.findHistoricoByCustomerId(customerId).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Transactional
    public void delete(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluguel não encontrado!"));

        if (rental.getRentalStatus() == RentalStatus.IN_PROGRESS) {
            Copy copy = rental.getCopy();
            copy.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            copyRepository.save(copy);
        }

        rentalRepository.delete(rental);
    }

    private RentalResponseDTO convertToResponseDTO(Rental rental) {
        List<PaymentResponseDTO> paymentDTOs = rental.getPayments() == null
                ? List.of()
                : rental.getPayments().stream()
                .map(this::convertPaymentToDTO)
                .toList();

        return new RentalResponseDTO(
                rental.getId(),
                rental.getCustomer().getId(),
                rental.getCustomer().getName(),
                rental.getCopy().getId(),
                rental.getCopy().getMovie().getTitle(),
                rental.getRentedAt(),
                rental.getReturnedAt(),
                rental.getDueDate(),
                rental.getRentalStatus(),
                paymentDTOs
        );
    }

    private PaymentResponseDTO convertPaymentToDTO(Payment payment) {
        return new PaymentResponseDTO(
                payment.getPaymentId(),
                payment.getRental().getId(),
                payment.getAmount(),
                payment.getPaidAt(),
                payment.getMethod(),
                payment.getDescription()
        );
    }
}
