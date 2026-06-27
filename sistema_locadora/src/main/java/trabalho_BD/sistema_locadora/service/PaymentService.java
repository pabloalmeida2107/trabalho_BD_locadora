package trabalho_BD.sistema_locadora.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trabalho_BD.sistema_locadora.models.Payment;
import trabalho_BD.sistema_locadora.models.Rental;
import trabalho_BD.sistema_locadora.repository.PaymentRepository;
import trabalho_BD.sistema_locadora.repository.RentalRepository;
import trabalho_BD.sistema_locadora.requestDTO.PaymentRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.PaymentResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;

    public PaymentRepository(PaymentRepository paymentRepository, RentalRepository rentalRepository){
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
    }

public PaymentResponseDTO create(PaymentRequestDTO data) {
        Rental rental = rentalRepository.findById(data.rentalId())
                .orElseThrow(() -> new RuntimeException("Rent not found!"));

        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setAmount(data.amount());
        
        payment.setPaidAt(java.time.LocalDate.now()); 
        
        payment.setMethod(data.method());
        payment.setDescription(data.description());

        return new PaymentResponseDTO(paymentRepository.save(payment));
    }

    public List<PaymentResponseDTO> findAll() {
        return paymentRepository.findAll().stream()
                .map(PaymentResponseDTO::new)
                .collect(Collectors.toList());
    }

    public PaymentResponseDTO findById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));
        return new PaymentResponseDTO(payment);
    }

    public PaymentResponseDTO update(UUID id, PaymentRequestDTO data) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));

        Rental rental = rentalRepository.findById(data.rentalId())
                .orElseThrow(() -> new RuntimeException("Rent not found!"));

        payment.setRental(rental);
        payment.setAmount(data.amount());
        payment.setMethod(data.method());
        payment.setDescription(data.description());

        return new PaymentResponseDTO(paymentRepository.save(payment));
    }

    public void delete(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));
        paymentRepository.delete(payment);
    }
}