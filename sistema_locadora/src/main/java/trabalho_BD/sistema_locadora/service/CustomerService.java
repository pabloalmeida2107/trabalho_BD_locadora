package trabalho_BD.sistema_locadora.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import trabalho_BD.sistema_locadora.exception.BusinessException;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.models.Customer;
import trabalho_BD.sistema_locadora.repository.CustomerRepository;
import trabalho_BD.sistema_locadora.requestDTO.CustomerRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.CustomerResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponseDTO create(CustomerRequestDTO data) {
        customerRepository.findByCpf(data.cpf()).ifPresent(c -> {
            throw new BusinessException("CPF '" + data.cpf() + "' já cadastrado.");
        });

        Customer customer = new Customer();
        customer.setName(data.name());
        customer.setCpf(data.cpf());
        customer.setPhone(data.phone());
        customer.setEmail(data.email());

        return convertToDTO(customerRepository.save(customer));
    }

    public List<CustomerResponseDTO> findAll() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO findById(UUID id) {
        return convertToDTO(customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado!")));
    }

    public CustomerResponseDTO findByCpf(String cpf) {
        return convertToDTO(customerRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente com CPF '" + cpf + "' não encontrado.")));
    }

    public CustomerResponseDTO update(UUID id, CustomerRequestDTO data) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado!"));

        customerRepository.findByCpf(data.cpf())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new BusinessException("CPF '" + data.cpf() + "' já pertence a outro cliente.");
                });

        customer.setName(data.name());
        customer.setCpf(data.cpf());
        customer.setPhone(data.phone());
        customer.setEmail(data.email());

        return convertToDTO(customerRepository.save(customer));
    }

    public void delete(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado!"));
        customerRepository.delete(customer);
    }

    private CustomerResponseDTO convertToDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getName(),
                customer.getCpf(),
                customer.getPhone(),
                customer.getEmail()
        );
    }
}
