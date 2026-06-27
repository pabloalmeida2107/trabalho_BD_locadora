package trabalho_BD.sistema_locadora.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.models.Payment;
import trabalho_BD.sistema_locadora.models.Rental;
import trabalho_BD.sistema_locadora.repository.PaymentRepository;
import trabalho_BD.sistema_locadora.repository.RentalRepository;
import trabalho_BD.sistema_locadora.requestDTO.PaymentRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.PaymentResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public PaymentResponseDTO create(PaymentRequestDTO data) {
        Rental rental = rentalRepository.findById(data.rentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluguel não encontrado!"));

        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setAmount(data.amount());
        payment.setPaidAt(LocalDate.now());
        payment.setMethod(data.method());
        payment.setDescription(data.description());

        return convertToDTO(paymentRepository.save(payment));
    }

    public List<PaymentResponseDTO> findAll() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PaymentResponseDTO findById(UUID id) {
        return convertToDTO(paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado!")));
    }

    public List<PaymentResponseDTO> findByRentalId(UUID rentalId) {
        if (!rentalRepository.existsById(rentalId)) {
            throw new ResourceNotFoundException("Aluguel não encontrado!");
        }
        return paymentRepository.findByRentalId(rentalId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal sumAmountByRentalId(UUID rentalId) {
        if (!rentalRepository.existsById(rentalId)) {
            throw new ResourceNotFoundException("Aluguel não encontrado!");
        }
        BigDecimal total = paymentRepository.sumAmountByRentalId(rentalId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional
    public PaymentResponseDTO update(UUID id, PaymentRequestDTO data) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado!"));

        Rental rental = rentalRepository.findById(data.rentalId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluguel não encontrado!"));

        payment.setRental(rental);
        payment.setAmount(data.amount());
        payment.setMethod(data.method());
        payment.setDescription(data.description());

        return convertToDTO(paymentRepository.save(payment));
    }

    public void delete(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado!"));
        paymentRepository.delete(payment);
    }

    private PaymentResponseDTO convertToDTO(Payment payment) {
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
