package trabalho_BD.sistema_locadora.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trabalho_BD.sistema_locadora.models.Genre;
import trabalho_BD.sistema_locadora.repository.GenreRepository;
import trabalho_BD.sistema_locadora.requestDTO.GenreRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.GenreResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    
    public GenreService(GenreRepository genreRepository){
        this.genreRepository = genreRepository;
    }

    private GenreResponseDTO create(GenreRequestDTO data) {
        Genre genre = new Genre();
        genre.setName(data.name()); 

        Genre savedGenre = genreRepository.save(genre);
        return convertToDTO(savedGenre);
    }

    public List<GenreResponseDTO> findAll() {
        return genreRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GenreResponseDTO findById(UUID id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        return convertToDTO(genre);
    }

    public GenreResponseDTO update(UUID id, GenreRequestDTO data) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        genre.setName(data.name());

        Genre updatedGenre = genreRepository.save(genre);
        return convertToDTO(updatedGenre);
    }

    public void delete(UUID id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        genreRepository.delete(genre);
    }
    private GenreResponseDTO convertToDTO(Genre genre) {
        return new GenreResponseDTO(genre.getId(), genre.getName());
    }
}