package trabalho_BD.sistema_locadora.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class CopyService {

    private final CopyRepository copyRepository;
    private final MovieRepository movieRepository;

    public CopyService(CopyRepository copyRepository, MovieRepository movieRepository){
        this.copyRepository = copyRepository;
        this.movieRepository = movieRepository;
    }

    public CopyResponseDTO create(CopyRequestDTO data) {
        Movie movie = movieRepository.findById(data.movieId())
                .orElseThrow(() -> new RuntimeException("Movie not found!"));

        Copy copy = new Copy();
        copy.setMovie(movie);
        copy.setAvailabilityStatus(data.availabilityStatus());
        copy.setFormat(data.format());

        Copy savedCopy = copyRepository.save(copy);
        
        return convertToDTO(savedCopy);
    }

    public List<CopyResponseDTO> findAll() {
        return copyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CopyResponseDTO findById(UUID id) {
        Copy copy = copyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Copy not found!"));
        return convertToDTO(copy);
    }

    public CopyResponseDTO update(UUID id, CopyRequestDTO data) {
        Copy copy = copyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Copy not found!"));

        Movie movie = movieRepository.findById(data.movieId())
                .orElseThrow(() -> new RuntimeException("Movie not found!"));

        copy.setMovie(movie);
        copy.setAvailabilityStatus(data.availabilityStatus());
        copy.setFormat(data.format());

        Copy updatedCopy = copyRepository.save(copy);
        return convertToDTO(updatedCopy);
    }

    public void delete(UUID id) {
        Copy copy = copyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Copy not found!"));
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