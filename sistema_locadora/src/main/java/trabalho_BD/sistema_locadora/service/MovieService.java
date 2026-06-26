package trabalho_BD.sistema_locadora.service;

import trabalho_BD.sistema_locadora.models.Movie;
import trabalho_BD.sistema_locadora.repository.MovieRepository;
import trabalho_BD.sistema_locadora.requestDTO.MovieRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.MovieResponseDTO;

import java.util.UUID;
import java.util.stream.Collectors;

public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    public MovieResponseDTO createMovie(MovieRequestDTO request){
        Movie newMovie = new Movie(request.title(), request.sinopse(), request.releaseYear(), request.durationMin(), request.rating());

        Movie savedMovie = movieRepository.save(newMovie);



    }


    private MovieResponseDTO convertToResponseDTO(Movie movie){
        return new MovieResponseDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getSinopse(),
                movie.getReleaseYear(),
                movie.getDurationMin(),
                movie.getSinopse(),
                movie.getMovieGenres().stream()
                        .map(m -> m.getGenre().getName())
                        .collect(Collectors.toList())

        );

    }
}
