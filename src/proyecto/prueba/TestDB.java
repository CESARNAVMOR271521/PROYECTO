package proyecto.prueba;

import proyecto.Conexion;
import proyecto.dao.ClienteDAO;
import proyecto.modelo.Cliente;
import java.util.List;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("Iniciando prueba de base de datos...");

        // 1. Crear tablas
        Conexion.crearTablas();
        
        // 2. Probar ClienteDAO
        ClienteDAO clienteDAO = new ClienteDAO();
        
        // Insertar
        Cliente c = new Cliente();
        c.setNombre("Juan Perez");
        c.setTelefono("555-1234");
        c.setCorreo("juan@test.com");
        c.setHistorial("Nuevo");
        
        if (clienteDAO.insertar(c)) {
            System.out.println("Cliente insertado correctamente.");
        } else {
            System.out.println("Error al insertar cliente.");
        }
        
        // Listar
        List<Cliente> clientes = clienteDAO.listar();
        System.out.println("Clientes encontrados: " + clientes.size());
        for (Cliente cl : clientes) {
            System.out.println(" - " + cl.getNombre() + " (" + cl.getCorreo() + ")");
        }
        
        System.out.println("Prueba finalizada.");
    }
}
