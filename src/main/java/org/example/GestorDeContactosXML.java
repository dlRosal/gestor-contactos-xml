package org.example;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Clase para gestionar contactos en un archivo XML. Permite agregar, listar, buscar,
 * modificar, eliminar y exportar contactos a un archivo CSV.
 */

public class GestorDeContactosXML {
    private final String rutaArchivo;

    /**
     * Constructor que inicializa la ruta del archivo XML y asegura que el archivo
     * esté listo para ser usado.
     * @param rutaArchivo Ruta donde se almacena el archivo XML de contactos.
     */

    public GestorDeContactosXML(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
        inicializarArchivoXML();
    }

    /**
     * Inicializa el archivo XML si no existe o está vacío, creando la estructura base.
     */

    private void inicializarArchivoXML() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || archivo.length() == 0) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.newDocument();

                // Crear elemento raíz <contactos>
                Element root = document.createElement("contactos");
                document.appendChild(root);

                // Guardar el documento en el archivo
                guardarCambios(document);
                System.out.println("Archivo " + rutaArchivo + " inicializado correctamente.");
            } catch (ParserConfigurationException | TransformerException e) {
                System.out.println("Error inicializando el archivo XML: " + e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Carga y retorna el documento XML desde el archivo.
     * @return Documento XML cargado.
     * @throws Exception Si el archivo no existe o está vacío.
     */

    public Document cargarDocumento() throws Exception {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || archivo.length() == 0) {
            throw new IOException("El archivo no existe o está vacío.");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(archivo);
    }

    /**
     * Guarda los cambios en el documento XML aplicando una indentación para mejorar la legibilidad.
     * @param document Documento XML a guardar.
     * @throws TransformerException Si ocurre un error al guardar el documento.
     */
    private void guardarCambios(Document document) throws TransformerException, IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Configurar propiedades de salida para la indentación
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        // Escribir el XML a un StringWriter para verificar el formato antes de guardarlo en el archivo
        StringWriter writer = new StringWriter();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);

        // Convertir el contenido formateado a un archivo
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(rutaArchivo))) {
            fileWriter.write(writer.toString());
        }
    }


    /**
     * Agrega un nuevo contacto al archivo XML.
     * @param nombre Nombre del contacto.
     * @param telefono Teléfono del contacto.
     * @param direccion Dirección del contacto.
     */

    public void agregarContacto(String nombre, String telefono, String direccion) {
        try {
            Document document = cargarDocumento();
            Element root = document.getDocumentElement();

            // Crear elemento <contacto> y agregar detalles
            Element contacto = document.createElement("contacto");
            contacto.appendChild(crearElemento(document, "nombre", nombre));
            contacto.appendChild(crearElemento(document, "telefono", telefono));
            contacto.appendChild(crearElemento(document, "direccion", direccion));

            root.appendChild(contacto);
            guardarCambios(document);
            System.out.println("Contacto agregado: " + nombre);
        } catch (Exception e) {
            System.out.println("Error al agregar contacto: " + e.getMessage());
        }
    }

    /**
     * Crea un elemento XML con texto asociado.
     * @param document Documento donde se crea el elemento.
     * @param etiqueta Nombre de la etiqueta.
     * @param texto Contenido de texto del elemento.
     * @return Elemento XML creado.
     */

    private Element crearElemento(Document document, String etiqueta, String texto) {
        Element element = document.createElement(etiqueta);
        element.appendChild(document.createTextNode(texto));
        return element;
    }

    /**
     * Lista todos los contactos del archivo XML.
     */

    public void listarContactos() {
        try {
            Document document = cargarDocumento();
            NodeList listaContactos = document.getElementsByTagName("contacto");

            for (int i = 0; i < listaContactos.getLength(); i++) {
                Element contacto = (Element) listaContactos.item(i);
                String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
                String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();
                String direccion = contacto.getElementsByTagName("direccion").item(0).getTextContent();
                System.out.println("Contacto " + (i + 1) + ": " + nombre + ", Teléfono: " + telefono + ", Dirección: " + direccion);
            }
        } catch (Exception e) {
            System.out.println("Error al listar contactos: " + e.getMessage());
        }
    }

    /**
     * Busca un contacto por su nombre.
     *
     * @param nombre Nombre del contacto a buscar.
     */

    public String buscarContacto(String nombre) {
        try {
            Document document = cargarDocumento();
            NodeList listaContactos = document.getElementsByTagName("contacto");
            boolean encontrado = false;

            for (int i = 0; i < listaContactos.getLength(); i++) {
                Element contacto = (Element) listaContactos.item(i);
                if (contacto.getElementsByTagName("nombre").item(0).getTextContent().equalsIgnoreCase(nombre)) {
                    String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();
                    String direccion = contacto.getElementsByTagName("direccion").item(0).getTextContent();
                    System.out.println("Contacto encontrado: " + nombre + ", Teléfono: " + telefono + ", Dirección: " + direccion);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                System.out.println("Contacto no encontrado: " + nombre);
            }
        } catch (Exception e) {
            System.out.println("Error al buscar contacto: " + e.getMessage());
        }
        return nombre;
    }

    /**
     * Modifica un contacto existente.
     * @param nombreOriginal Nombre actual del contacto.
     * @param nuevoNombre Nuevo nombre del contacto.
     * @param nuevoTelefono Nuevo teléfono del contacto.
     * @param nuevaDireccion Nueva dirección del contacto.
     */

    public void modificarContacto(String nombreOriginal, String nuevoNombre, String nuevoTelefono, String nuevaDireccion) {
        try {
            Document document = cargarDocumento();
            NodeList listaContactos = document.getElementsByTagName("contacto");
            boolean encontrado = false;

            for (int i = 0; i < listaContactos.getLength(); i++) {
                Element contacto = (Element) listaContactos.item(i);
                if (contacto.getElementsByTagName("nombre").item(0).getTextContent().equalsIgnoreCase(nombreOriginal)) {
                    contacto.getElementsByTagName("nombre").item(0).setTextContent(nuevoNombre);
                    contacto.getElementsByTagName("telefono").item(0).setTextContent(nuevoTelefono);
                    contacto.getElementsByTagName("direccion").item(0).setTextContent(nuevaDireccion);
                    guardarCambios(document);
                    System.out.println("Contacto modificado: " + nuevoNombre);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                System.out.println("Contacto no encontrado para modificar: " + nombreOriginal);
            }
        } catch (Exception e) {
            System.out.println("Error al modificar contacto: " + e.getMessage());
        }
    }

    /**
     * Elimina un contacto del archivo XML.
     * @param nombre Nombre del contacto a eliminar.
     */

    public void eliminarContacto(String nombre) {
        try {
            Document document = cargarDocumento();
            NodeList listaContactos = document.getElementsByTagName("contacto");
            boolean encontrado = false;

            for (int i = 0; i < listaContactos.getLength(); i++) {
                Element contacto = (Element) listaContactos.item(i);
                if (contacto.getElementsByTagName("nombre").item(0).getTextContent().equalsIgnoreCase(nombre)) {
                    contacto.getParentNode().removeChild(contacto);
                    guardarCambios(document);
                    System.out.println("Contacto eliminado: " + nombre);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                System.out.println("Contacto no encontrado para eliminar: " + nombre);
            }
        } catch (Exception e) {
            System.out.println("Error al eliminar contacto: " + e.getMessage());
        }
    }

    /**
     * Exporta todos los contactos a un archivo CSV.
     * @param rutaCSV Ruta del archivo CSV donde se exportarán los contactos.
     */

    public void exportarContactosCSV(String rutaCSV) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaCSV))) {
            writer.write("Nombre, Teléfono, Dirección\n");
            Document document = cargarDocumento();
            NodeList listaContactos = document.getElementsByTagName("contacto");

            for (int i = 0; i < listaContactos.getLength(); i++) {
                Element contacto = (Element) listaContactos.item(i);
                String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
                String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();
                String direccion = contacto.getElementsByTagName("direccion").item(0).getTextContent();
                writer.write( "Cliente número " + i+ ", " +nombre + ", " + telefono + ", " + direccion + "\n");
            }
            System.out.println("Contactos exportados a CSV: " + rutaCSV);
        } catch (Exception e) {
            System.out.println("Error al exportar contactos a CSV: " + e.getMessage());
        }
    }
}