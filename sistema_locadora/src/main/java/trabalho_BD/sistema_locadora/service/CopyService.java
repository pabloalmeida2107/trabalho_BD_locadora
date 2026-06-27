package trabalho_BD.sistema_locadora.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import trabalho_BD.sistema_locadora.exception.ResourceNotFoundException;
import trabalho_BD.sistema_locadora.models.Copy;
import trabalho_BD.sistema_locadora.models.Movie;
import trabalho_BD.sistema_locadora.repository.CopyRepository;
import trabalho_BD.sistema_locadora.repository.MovieRepository;
import trabalho_BD.sistema_locadora.requestDTO.CopyRequestDTO;
import trabalho_BD.sistema_locadora.responseDTO.CopyResponseDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CopyService {

    private final CopyRepository copyRepository;
    private final MovieRepository movieRepository;

    public CopyResponseDTO create(CopyRequestDTO data) {
        Movie movie = movieRepository.findById(data.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado!"));

        Copy copy = new Copy();
        copy.setMovie(movie);
        copy.setAvailabilityStatus(data.availabilityStatus());
        copy.setFormat(data.format());

        return convertToDTO(copyRepository.save(copy));
    }

    public List<CopyResponseDTO> findAll() {
        return copyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CopyResponseDTO findById(UUID id) {
        return convertToDTO(copyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cópia não encontrada!")));
    }

    public CopyResponseDTO update(UUID id, CopyRequestDTO data) {
        Copy copy = copyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cópia não encontrada!"));

        Movie movie = movieRepository.findById(data.movieId())
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado!"));

        copy.setMovie(movie);
        copy.setAvailabilityStatus(data.availabilityStatus());
        copy.setFormat(data.format());

        return convertToDTO(copyRepository.save(copy));
    }

    public Long countAvailableCopiesByMovie(UUID movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Filme não encontrado!");
        }
        return copyRepository.countAvailableCopiesByMovie(movieId);
    }

    public void delete(UUID id) {
        Copy copy = copyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cópia não encontrada!"));
        copyRepository.delete(copy);
    }

    private CopyResponseDTO convertToDTO(Copy copy) {
        return new CopyResponseDTO(
                copy.getId(),
                copy.getMovie().getId(),
                copy.getMovie().getTitle(),
                copy.getAvailabilityStatus(),
                copy.getFormat()
        );
    }
}
