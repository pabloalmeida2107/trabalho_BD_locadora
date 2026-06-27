package trabalho_BD.sistema_locadora.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sistema Locadora de Filmes")
                        .description("API administrativa para gerenciamento de uma locadora de filmes. " +
                                "Permite o controle de gêneros, filmes, cópias físicas, clientes, aluguéis e pagamentos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pablo Almeida")
                                .email("pablo.mengo66@gmail.com")))
                .tags(List.of(
                        new Tag().name("Gêneros").description("Gerenciamento dos gêneros de filmes"),
                        new Tag().name("Filmes").description("Catálogo de filmes disponíveis na locadora"),
                        new Tag().name("Cópias").description("Controle das cópias físicas de cada filme (DVD, VHS, Blu-ray)"),
                        new Tag().name("Clientes").description("Cadastro e gerenciamento dos clientes"),
                        new Tag().name("Aluguéis").description("Registro e controle dos aluguéis e devoluções"),
                        new Tag().name("Pagamentos").description("Registro de pagamentos vinculados aos aluguéis")
                ));
    }
}
