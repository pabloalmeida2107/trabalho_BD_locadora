package trabalho_BD.sistema_locadora.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import trabalho_BD.sistema_locadora.exception.BusinessException;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.models.Genre;
import trabalho_BD.sistema_locadora.models.Movie;
import trabalho_BD.sistema_locadora.repository.GenreRepository;
import trabalho_BD.sistema_locadora.repository.MovieRepository;
import trabalho_BD.sistema_locadora.requestDTO.MovieRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.MovieResponseDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    @Value("${app.upload.dir:./uploads/covers}")
    private String uploadDir;

    private static final List<String> ALLOWED_TYPES =
            List.of("image/jpeg", "image/png", "image/gif", "image/webp");

    @Transactional
    public MovieResponseDTO createMovie(MovieRequestDTO request) {
        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado!"));

        Movie movie = new Movie(
                request.getTitle(),
                request.getSinopse(),
                request.getReleaseYear(),
                request.getDurationMin(),
                request.getRating(),
                genre
        );

        Movie saved = movieRepository.save(movie);

        MultipartFile cover = request.getCoverImage();
        if (cover != null && !cover.isEmpty()) {
            saveCoverFile(saved, cover);
            saved = movieRepository.save(saved);
        }

        return convertToResponseDTO(saved);
    }

    public List<MovieResponseDTO> findAll() {
        return movieRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public MovieResponseDTO getMovieById(UUID id) {
        return convertToResponseDTO(movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado!")));
    }

    public List<MovieResponseDTO> searchMovies(String title) {
        return movieRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MovieResponseDTO updateMovie(UUID id, MovieRequestDTO request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado!"));

        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado!"));

        movie.setTitle(request.getTitle());
        movie.setSinopse(request.getSinopse());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setDurationMin(request.getDurationMin());
        movie.setRating(request.getRating());
        movie.setGenre(genre);

        return convertToResponseDTO(movieRepository.save(movie));
    }

    @Transactional
    public void deleteMovie(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado!"));

        if (movie.getCoverImagePath() != null) {
            try { Files.deleteIfExists(Paths.get(movie.getCoverImagePath())); }
            catch (IOException ignored) {}
        }

        movieRepository.delete(movie);
    }

    @Transactional
    public MovieResponseDTO uploadCover(UUID id, MultipartFile file) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado!"));

        if (movie.getCoverImagePath() != null) {
            try { Files.deleteIfExists(Paths.get(movie.getCoverImagePath())); }
            catch (IOException ignored) {}
        }

        saveCoverFile(movie, file);
        return convertToResponseDTO(movieRepository.save(movie));
    }

    public byte[] getCoverImage(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado!"));

        if (movie.getCoverImagePath() == null) {
            throw new ResourceNotFoundException("Este filme não possui imagem de capa.");
        }

        Path path = Paths.get(movie.getCoverImagePath());
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("Arquivo de capa não encontrado no servidor.");
        }

        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new BusinessException("Erro ao ler a imagem de capa.");
        }
    }

    public String getCoverContentType(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado!"));

        if (movie.getCoverImagePath() == null) {
            throw new ResourceNotFoundException("Este filme não possui imagem de capa.");
        }

        String path = movie.getCoverImagePath();
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".gif"))  return "image/gif";
        if (path.endsWith(".webp")) return "image/webp";
        return "image/jpeg";
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private void saveCoverFile(Movie movie, MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("Formato inválido. Envie uma imagem JPEG, PNG, GIF ou WebP.");
        }
        if (file.isEmpty()) {
            throw new BusinessException("O arquivo enviado está vazio.");
        }

        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);

            String filename = movie.getId() + resolveExtension(contentType);
            Path destination = dir.resolve(filename);
            Files.write(destination, file.getBytes());

            movie.setCoverImagePath(destination.toString());
        } catch (IOException e) {
            throw new BusinessException("Erro ao salvar a imagem de capa: " + e.getMessage());
        }
    }

    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/png"  -> ".png";
            case "image/gif"  -> ".gif";
            case "image/webp" -> ".webp";
            default           -> ".jpg";
        };
    }

    private MovieResponseDTO convertToResponseDTO(Movie movie) {
        String coverUrl = movie.getCoverImagePath() != null
                ? "/api/movies/" + movie.getId() + "/cover"
                : null;

        return new MovieResponseDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getSinopse(),
                movie.getReleaseYear(),
                movie.getDurationMin(),
                movie.getRating(),
                movie.getGenre().getName(),
                coverUrl
        );
    }
}
