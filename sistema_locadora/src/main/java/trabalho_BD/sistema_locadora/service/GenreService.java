package trabalho_BD.sistema_locadora.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import trabalho_BD.sistema_locadora.exception.BusinessException;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.models.Genre;
import trabalho_BD.sistema_locadora.repository.GenreRepository;
import trabalho_BD.sistema_locadora.requestDTO.GenreRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.GenreResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreResponseDTO create(GenreRequestDTO data) {
        genreRepository.findByName(data.name()).ifPresent(g -> {
            throw new BusinessException("Já existe um gênero com o nome '" + data.name() + "'.");
        });

        Genre genre = new Genre();
        genre.setName(data.name());
        return convertToDTO(genreRepository.save(genre));
    }

    public List<GenreResponseDTO> findAll() {
        return genreRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GenreResponseDTO findById(UUID id) {
        return convertToDTO(genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado!")));
    }

    public GenreResponseDTO update(UUID id, GenreRequestDTO data) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado!"));

        genreRepository.findByName(data.name())
                .filter(g -> !g.getId().equals(id))
                .ifPresent(g -> {
                    throw new BusinessException("Já existe um gênero com o nome '" + data.name() + "'.");
                });

        genre.setName(data.name());
        return convertToDTO(genreRepository.save(genre));
    }

    public void delete(UUID id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gênero não encontrado!"));
        genreRepository.delete(genre);
    }

    private GenreResponseDTO convertToDTO(Genre genre) {
        return new GenreResponseDTO(genre.getId(), genre.getName());
    }
}
