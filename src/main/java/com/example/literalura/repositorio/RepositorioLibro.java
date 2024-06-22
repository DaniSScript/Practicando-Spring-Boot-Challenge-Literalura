package com.example.literalura.repositorio;

import com.example.literalura.model.autor.LibroAutor;
import com.example.literalura.model.libro.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface RepositorioLibro extends JpaRepository<Libro, Long> {

    boolean existsByTitulo(String titulo);
    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.autores")
    List<Libro> findAllWithAutores();

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.libroIdioma")
    List<Libro> findAllWithIdiomas();

    @Query("SELECT DISTINCT a FROM LibroAutor a")
    List<LibroAutor> findAllAutores();

    @Query("SELECT DISTINCT l FROM Libro l LEFT JOIN FETCH l.autores WHERE :idioma MEMBER OF l.libroIdioma")
    List<Libro> findAllByIdiomaWithAutores(String idioma);


}
