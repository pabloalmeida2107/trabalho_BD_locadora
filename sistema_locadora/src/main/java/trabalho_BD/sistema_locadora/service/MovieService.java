package trabalho_BD.sistema_locadora.service;

import jakarta.transaction.Transactional;
import trabalho_BD.sistema_locadora.models.Genre;
import trabalho_BD.sistema_locadora.models.Movie;
import trabalho_BD.sistema_locadora.repository.GenreRepository;
import trabalho_BD.sistema_locadora.repository.MovieRepository;
import trabalho_BD.sistema_locadora.requestDTO.MovieRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.MovieResponseDTO;

import java.util.UUID;
import java.util.stream.Collectors;

public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    public MovieService(MovieRepository movieRepository, GenreRepository genreRepository){
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
    }

    @Transactional
    public MovieResponseDTO createMovie(MovieRequestDTO request) {


        Genre genre = genreRepository.findById(request.genreId())
                .orElseThrow(() -> new RuntimeException("Gênero não encontrado!"));


        Movie newMovie = new Movie(
                request.title(),
                request.sinopse(),
                request.releaseYear(),
                request.durationMin(),
                request.rating(),
                genre
        );

        Movie savedMovie = movieRepository.save(newMovie);

        return convertToResponseDTO(savedMovie);
    }

    private MovieResponseDTO convertToResponseDTO(Movie movie) {
        return new MovieResponseDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getSinopse(),
                movie.getReleaseYear(),
                movie.getDurationMin(),
                movie.getRating(),
                movie.getGenre().getName()
        );
    }
}
