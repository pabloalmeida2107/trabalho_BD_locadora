package trabalho_BD.sistema_locadora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import trabalho_BD.sistema_locadora.config.SecurityConfig;
import trabalho_BD.sistema_locadora.exception.BusinessException;
import trabalho_BD.sistema_locadora.exception.GlobalExceptionHandler;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.requestDTO.CustomerRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.CustomerResponseDTO;
import trabalho_BD.sistema_locadora.service.CustomerService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CustomerController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CustomerService customerService;

    private UUID id;
    private CustomerResponseDTO responseDTO;
    private CustomerRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        responseDTO = new CustomerResponseDTO(id, "João Silva", "12345678901", "11999990000", "joao@email.com");
        requestDTO = new CustomerRequestDTO("João Silva", "12345678901", "11999990000", "joao@email.com");
    }

    @Test
    void create_deveRetornar201_quandoRequestValida() throws Exception {
        when(customerService.create(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("João Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    void create_deveRetornar400_quandoCpfForaDoFormato() throws Exception {
        var invalido = new CustomerRequestDTO("João", "123", "11999990000", "joao@email.com");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_deveRetornar400_quandoEmailInvalido() throws Exception {
        var invalido = new CustomerRequestDTO("João", "12345678901", "11999990000", "email-invalido");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_deveRetornar400_quandoCpfDuplicado() throws Exception {
        when(customerService.create(any())).thenThrow(new BusinessException("CPF '12345678901' já cadastrado."));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF '12345678901' já cadastrado."));
    }

    @Test
    void findAll_deveRetornar200_comLista() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("João Silva"));
    }

    @Test
    void findById_deveRetornar200_quandoEncontrado() throws Exception {
        when(customerService.findById(id)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void findById_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(customerService.findById(id)).thenThrow(new ResourceNotFoundException("Cliente não encontrado!"));

        mockMvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findByCpf_deveRetornar200_quandoEncontrado() throws Exception {
        when(customerService.findByCpf("12345678901")).thenReturn(responseDTO);

        mockMvc.perform(get("/api/customers/cpf/{cpf}", "12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"));
    }

    @Test
    void findByCpf_deveRetornar404_quandoNaoEncontrado() throws Exception {
        when(customerService.findByCpf("00000000000"))
                .thenThrow(new ResourceNotFoundException("Cliente com CPF '00000000000' não encontrado."));

        mockMvc.perform(get("/api/customers/cpf/{cpf}", "00000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente com CPF '00000000000' não encontrado."));
    }

    @Test
    void update_deveRetornar200_comDadosAtualizados() throws Exception {
        var atualizado = new CustomerResponseDTO(id, "João Atualizado", "12345678901", "11888880000", "novo@email.com");
        when(customerService.update(eq(id), any())).thenReturn(atualizado);

        mockMvc.perform(put("/api/customers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Atualizado"));
    }

    @Test
    void delete_deveRetornar204_quandoDeletado() throws Exception {
        doNothing().when(customerService).delete(id);

        mockMvc.perform(delete("/api/customers/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_deveRetornar404_quandoNaoEncontrado() throws Exception {
        doThrow(new ResourceNotFoundException("Cliente não encontrado!")).when(customerService).delete(id);

        mockMvc.perform(delete("/api/customers/{id}", id))
                .andExpect(status().isNotFound());
    }
}
