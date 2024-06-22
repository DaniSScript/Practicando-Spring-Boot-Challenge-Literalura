package com.example.literalura.principal;

import com.example.literalura.model.Datos;
import com.example.literalura.model.libro.DatosLibro;
import com.example.literalura.model.libro.Libro;
import com.example.literalura.model.autor.LibroAutor;
import com.example.literalura.repositorio.RepositorioLibro;
import com.example.literalura.service.ConsumoAPI;
import com.example.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class Principal {

    private final Scanner teclado = new Scanner(System.in);
    private final ConsumoAPI consumoAPI;
    private final ConvierteDatos conversor;
    private final RepositorioLibro repositorioLibro;
    private static final String URL = "https://gutendex.com/books/";
    private List<Libro> libros = new ArrayList<>();

    @Autowired
    public Principal(ConsumoAPI consumoAPI, ConvierteDatos conversor, RepositorioLibro repositorioLibro) {
        this.consumoAPI = consumoAPI;
        this.conversor = conversor;
        this.repositorioLibro = repositorioLibro;
    }

    public void menu() {
        int opcion = -1;
        while (opcion != 0) {
            String menu = """
                    1 - Buscar libros por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma             
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1 -> buscarLibro();
                case 2 -> listarLibrosRegistrados();
                case 3 -> listarAutoresRegistrados();
                case 4 -> listarAutoresVivos();
                case 5 -> listarLibrosPorIdioma();
                case 0 -> System.out.println("Cerrando la aplicación...");
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private void buscarLibro() {
        Datos datos = geDatosLibro();
        if (datos.resultados() != null && !datos.resultados().isEmpty()) {
            DatosLibro datosLibro = datos.resultados().get(0);
            if (repositorioLibro.existsByTitulo(datosLibro.titulo())) {
                System.out.println("El libro ya está registrado en la base de datos.");
            } else {
                Libro libro = new Libro(datosLibro);
                repositorioLibro.save(libro);
                imprimirDetallesLibro(libro);
            }
        } else {
            System.out.println("No se encontraron libros con el título especificado.");
        }
    }

    private Datos geDatosLibro() {
        System.out.println("Ingrese el nombre del libro que quiere buscar");
        String tituloLibro = teclado.nextLine();
        String json = consumoAPI.obtenerDatos(URL + "?search=" + tituloLibro.replace(" ", "+"));
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        return datos;
    }

    private void imprimirDetallesLibro(Libro libro) {
        System.out.println("Titulo: " + libro.getTitulo());
        System.out.print("Autor: ");
        for (LibroAutor autor : libro.getAutores()) {
            System.out.print(autor.getNombre() + " ");
        }
        System.out.println();
        System.out.print("Idioma: ");
        for (String idioma : libro.getLibroIdioma()) {
            System.out.print(idioma + " ");
        }
        System.out.println();
        System.out.println("Numero de descargas: " + libro.getNumDescargas());
    }

    @Transactional
    public void listarLibrosRegistrados() {
        List<Libro> librosConAutores = repositorioLibro.findAllWithAutores();
        List<Libro> librosConIdiomas = repositorioLibro.findAllWithIdiomas();

        Map<Long, Libro> libroMap = new HashMap<>();
        for (Libro libro : librosConAutores) {
            libroMap.put(libro.getId(), libro);
        }
        for (Libro libro : librosConIdiomas) {
            Libro libroExistente = libroMap.get(libro.getId());
            if (libroExistente != null) {
                libroExistente.setLibroIdioma(libro.getLibroIdioma());
            } else {
                libroMap.put(libro.getId(), libro);
            }
        }
        libros = new ArrayList<>(libroMap.values());

        libros.stream().forEach(System.out::println);
    }

    @Transactional
    private void listarAutoresRegistrados() {
        List<LibroAutor> autores = repositorioLibro.findAllAutores();
        Set<LibroAutor> autoresUnicos = new HashSet<>(autores);
        autoresUnicos.forEach(autor -> {
            System.out.println("------------------------------------");
            System.out.println("Nombre: " + autor.getNombre());
            System.out.println("Año de nacimiento: " + autor.getNacimiento());
            System.out.println("Año de muerte: " + autor.getMuerte());
            System.out.println("------------------------------------");
        });
    }

    @Transactional
    private void listarAutoresVivos() {
        System.out.println("Ingrese el año para listar los autores vivos en ese año:");
        int año = teclado.nextInt();
        teclado.nextLine();

        List<LibroAutor> autores = repositorioLibro.findAllAutores();
        System.out.println("-------- Nombre de autores vivos en " + año + " ----------");
        autores.stream()
                .filter(autor -> autor.getNacimiento() <= año && (autor.getMuerte() == null || autor.getMuerte() >= año))
                .map(LibroAutor::getNombre)
                .distinct()
                .forEach(nombre -> {
                    System.out.println("Nombre: " + nombre);
                    System.out.println("---------------------------------------------------------");
                });
    }

    @Transactional
    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el código del idioma (es, en, fr, pt):");
        String codigoIdioma = teclado.nextLine();

        List<Libro> libros = repositorioLibro.findAllByIdiomaWithAutores(codigoIdioma);
        if (libros.isEmpty()) {
            System.out.println("No hay libros en el idioma consultado.");
        } else {
            System.out.println("-------- Libros en idioma " + codigoIdioma + " ----------");
            libros.forEach(libro -> {
                System.out.println("Titulo: " + libro.getTitulo());
                System.out.print("Autor: ");
                libro.getAutores().forEach(autor -> System.out.print(autor.getNombre() + " "));
                System.out.println();
                System.out.println("Numero de descargas: " + libro.getNumDescargas());
                System.out.println("---------------------------------------------------------");
            });
        }
    }
}
