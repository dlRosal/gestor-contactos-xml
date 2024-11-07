package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Crear una instancia del gestor de contactos
        GestorDeContactosXML gestor = new GestorDeContactosXML("contactos.xml");
        Scanner scanner = new Scanner(System.in);

        // Menú principal
        while (true) {
            System.out.println("Seleccione una opción:");
            System.out.println("1. Agregar contacto");
            System.out.println("2. Listar contactos");
            System.out.println("3. Buscar contacto");
            System.out.println("4. Modificar contacto");
            System.out.println("5. Eliminar contacto");
            System.out.println("6. Exportar a CSV");
            System.out.println("7. Salir");

            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir nueva línea

            switch (opcion) {
                case 1:
                    // Agregar un nuevo contacto
                    System.out.print("Nombre: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Teléfono: ");
                    String telefono = scanner.nextLine();
                    System.out.print("Dirección: ");
                    String direccion = scanner.nextLine();
                    gestor.agregarContacto(nombre, telefono, direccion);
                    break;
                case 2:
                    // Listar todos los contactos
                    gestor.listarContactos();
                    break;
                case 3:
                    // Buscar un contacto por nombre
                    System.out.print("Nombre del contacto a buscar: ");
                    String nombreBuscar = scanner.nextLine();
                    String resultadoBuscar = gestor.buscarContacto(nombreBuscar);
                    if (resultadoBuscar != null) {
                        System.out.println(resultadoBuscar);
                    } else {
                        System.out.println("Contacto no encontrado.");
                    }
                    break;
                case 4:
                    // Modificar un contacto existente
                    System.out.print("Nombre del contacto a modificar: ");
                    String nombreModificar = scanner.nextLine();
                    System.out.print("Nuevo nombre: ");
                    String nuevoNombre = scanner.nextLine();
                    System.out.print("Nuevo teléfono: ");
                    String nuevoTelefono = scanner.nextLine();
                    System.out.print("Nueva dirección: ");
                    String nuevaDireccion = scanner.nextLine();
                    gestor.modificarContacto(nombreModificar, nuevoNombre, nuevoTelefono, nuevaDireccion);
                    break;
                case 5:
                    // Eliminar un contacto
                    System.out.print("Nombre del contacto a eliminar: ");
                    String nombreEliminar = scanner.nextLine();
                    gestor.eliminarContacto(nombreEliminar);
                    break;
                case 6:
                    // Exportar contactos a un archivo CSV
                    System.out.print("Ruta del archivo CSV: ");
                    String rutaCSV = scanner.nextLine();
                    gestor.exportarContactosCSV(rutaCSV);
                    break;
                case 7:
                    // Salir de la aplicación
                    System.out.println("Saliendo...");
                    scanner.close();
                    return;
                default:
                    // Opción no válida
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }
}
