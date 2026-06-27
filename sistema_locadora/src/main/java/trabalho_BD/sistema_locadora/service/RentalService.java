package trabalho_BD.sistema_locadora.service;

import jakarta.transaction.Transactional;
import trabalho_BD.sistema_locadora.models.Movie;
import trabalho_BD.sistema_locadora.models.Rental;
import trabalho_BD.sistema_locadora.repository.MovieRepository;
import trabalho_BD.sistema_locadora.repository.RentalRepository;
import trabalho_BD.sistema_locadora.requestDTO.RentalRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.RentalResponseDTO;

import java.time.LocalDate;
import java.util.UUID;

public class RentalService {
    private final RentalRepository rentalRepository;
    private final MovieRepository movieRepository;

    public RentalService(RentalRepository rentalRepository, MovieRepository movieRepository) {
        this.rentalRepository = rentalRepository;
        this.movieRepository = movieRepository;
    }


    @Transactional
    public RentalResponseDTO createRental(RentalRequestDTO request) {

        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new RuntimeException("Filme não encontrado!"));

        // 2. Regra de Negócio: O filme está disponível?
        // (Assumindo que você tem um campo 'isAvailable' na sua classe Movie)
        if (!movie.isAvailable()) {
            throw new RuntimeException("Este filme já está alugado!");
        }

        // 3. Marca o filme como indisponível
        movie.setAvailable(false);
        movieRepository.save(movie);

        // 4. Cria o registro do aluguel
        Rental newRental = new Rental(movie, request.customerId(), LocalDate.now());
        Rental savedRental = rentalRepository.save(newRental);

        return convertToResponseDTO(savedRental);
    }

    @Transactional
    public void returnMovie(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Aluguel não encontrado!"));

        // 1. Marca o filme como disponível novamente
        Movie movie = rental.getMovie();
        movie.setAvailable(true);
        movieRepository.save(movie);

        // 2. Registra a data de devolução (ou deleta o registro, dependendo da sua regra)
        rental.setReturnedAt(LocalDate.now());
        rentalRepository.save(rental);
    }

    private RentalResponseDTO convertToResponseDTO(Rental rental) {
        return new RentalResponseDTO(
                rental.getId(),
                rental.getMovie().getTitle(),
                rental.getCustomerId(),
                rental.getRentedAt()
        );
    }
}
