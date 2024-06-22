package com.example.literalura.model.libro;

import com.example.literalura.model.autor.LibroAutorDatos;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibro(
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") List<LibroAutorDatos> autor,
        @JsonAlias("languages") List<String> idioma,
        @JsonAlias("download_count") Double numDescargas
) {
}
