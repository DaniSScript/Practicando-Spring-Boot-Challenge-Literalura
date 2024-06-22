package com.example.literalura.model.libro;

import com.example.literalura.model.autor.LibroAutor;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LibroAutor> autores = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "libro_idiomas", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "idioma")
    private Set<String> libroIdioma = new HashSet<>();

    private Double numDescargas;

    public Libro() {
    }

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.libroIdioma = new HashSet<>(datosLibro.idioma());
        this.numDescargas = datosLibro.numDescargas();
        if (datosLibro.autor() != null) {
            for (var autorDatos : datosLibro.autor()) {
                this.autores.add(new LibroAutor(autorDatos, this));
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<LibroAutor> getAutores() {
        return autores;
    }

    public void setAutores(List<LibroAutor> autores) {
        this.autores = autores;
    }

    public Set<String> getLibroIdioma() {
        return libroIdioma;
    }

    public void setLibroIdioma(Set<String> libroIdioma) {
        this.libroIdioma = libroIdioma;
    }

    public Double getNumDescargas() {
        return numDescargas;
    }

    public void setNumDescargas(Double numDescargas) {
        this.numDescargas = numDescargas;
    }

    @Override
    public String toString() {
        String autoresStr = autores.stream()
                .map(LibroAutor::getNombre)
                .reduce((a, b) -> a + ", " + b)
                .orElse("N/A");

        String idiomasStr = String.join(", ", libroIdioma);

        return String.format("------------------------------------%n" +
                        "%-10s: %s%n" +
                        "%-10s: %s%n" +
                        "%-10s: %s%n" +
                        "%-10s: %.1f%n",
                "TITULO", titulo,
                "AUTOR", autoresStr,
                "IDIOMA", idiomasStr,
                "DESCARGAS", numDescargas);
    }
}
