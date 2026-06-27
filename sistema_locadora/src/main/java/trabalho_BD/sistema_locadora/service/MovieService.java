package trabalho_BD.sistema_locadora.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import trabalho_BD.sistema_locadora.models.Genre;
import trabalho_BD.sistema_locadora.models.Movie;
import trabalho_BD.sistema_locadora.repository.GenreRepository;
import trabalho_BD.sistema_locadora.repository.MovieRepository;
import trabalho_BD.sistema_locadora.requestDTO.MovieRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.MovieResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
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


    public MovieResponseDTO getMovieById(UUID id) {
        return convertToResponseDTO(movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado!")));
    }


    public List<MovieResponseDTO> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public MovieResponseDTO updateMovie(UUID id, MovieRequestDTO request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Filme não encontrado!"));

        Genre genre = genreRepository.findById(request.genreId())
                .orElseThrow(() -> new RuntimeException("Gênero não encontrado!"));


        movie.setTitle(request.title());
        movie.setSinopse(request.sinopse());
        movie.setReleaseYear(request.releaseYear());
        movie.setDurationMin(request.durationMin());
        movie.setRating(request.rating());
        movie.setGenre(genre);

        return convertToResponseDTO(movieRepository.save(movie));
    }


    @Transactional
    public void deleteMovie(UUID id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Filme não encontrado!");
        }
        movieRepository.deleteById(id);
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
