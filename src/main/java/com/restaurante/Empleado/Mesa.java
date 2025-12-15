package com.restaurante.Empleado;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class Mesa {

    private static final String DB_URL = "jdbc:sqlite:Restaurante.db";
    private static Map<Integer, Pedido> pedidosMesa = new HashMap<>();

    private JFrame frame;
    private int numeroMesa;
    private JButton botonMesa;

    private JLabel lblFecha;
    private JLabel lblHora;
    private JLabel lblTotal;

    private List<ItemMenu> platos;
    private List<ItemMenu> acompanamientos;

    public Mesa(int numeroMesa, JButton botonMesa) {
        this.numeroMesa = numeroMesa;
        this.botonMesa = botonMesa;

        frame = new JFrame("Mesa " + numeroMesa);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 550);
        frame.setLocationRelativeTo(null);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        // Obtener items
        platos = obtenerPlatos();
        acompanamientos = obtenerAcompanamientos();

        // Panel Platos y Acompañamientos
        JPanel panelPlatos = crearPanelItems(platos, "Platos");
        JScrollPane scrollPlatos = new JScrollPane(panelPlatos);
        splitPane.setLeftComponent(scrollPlatos);

        JPanel panelAcomp = crearPanelItems(acompanamientos, "Acompañamientos");
        JScrollPane scrollAcomp = new JScrollPane(panelAcomp);
        splitPane.setRightComponent(scrollAcomp);

        // Panel inferior
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        // Fecha y hora
        JPanel panelFechaHora = new JPanel(new GridLayout(2,1));
        panelFechaHora.setOpaque(false);
        lblFecha = new JLabel();
        lblHora = new JLabel();
        lblFecha.setFont(new Font("Arial", Font.BOLD, 15));
        lblHora.setFont(new Font("Arial", Font.BOLD, 15));
        panelFechaHora.add(lblFecha);
        panelFechaHora.add(lblHora);
        actualizarFechaHora();
        panelSur.add(panelFechaHora, BorderLayout.WEST);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 14));
        btnCancelar.setBackground(new Color(200,50,50));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(e -> {
            botonMesa.setBackground(new Color(46,125,50));
            resetearPedido();
        });

        JButton btnListo = new JButton("Listo");
        btnListo.setFont(new Font("Arial", Font.BOLD, 14));
        btnListo.setBackground(new Color(0,153,0));
        btnListo.setForeground(Color.WHITE);
        btnListo.setFocusPainted(false);
        btnListo.addActionListener(e -> botonMesa.setBackground(Color.RED));

        JButton btnPago = new JButton("Pagó");
        btnPago.setFont(new Font("Arial", Font.BOLD, 14));
        btnPago.setBackground(new Color(0,102,204));
        btnPago.setForeground(Color.WHITE);
        btnPago.setFocusPainted(false);
        btnPago.addActionListener(e -> {
            registrarPago();
            botonMesa.setBackground(new Color(46,125,50));
            resetearPedido();
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnListo);
        panelBotones.add(btnPago);

        panelSur.add(panelBotones, BorderLayout.CENTER);

        // Total
        lblTotal = new JLabel("TOTAL: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        panelSur.add(lblTotal, BorderLayout.EAST);

        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(panelSur, BorderLayout.SOUTH);
        frame.setVisible(true);

        // Cargar pedido previo si existe
        cargarPedidoPrevio();
    }

    // ===== Pedido temporal =====
    private static class Pedido {
        Map<String,Integer> cantidades = new HashMap<>();
        double total = 0;
    }

    // ===== Clase ItemMenu dentro de Mesa =====
    private static class ItemMenu{
        String nombre;
        double precio;
        ItemMenu(String nombre,double precio){
            this.nombre = nombre;
            this.precio = precio;
        }
    }

    // ===== Actualiza fecha y hora =====
    private void actualizarFechaHora() {
        Timer timer = new Timer(1000, e -> {
            java.util.Date ahora = new java.util.Date();
            SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");
            lblFecha.setText("Fecha: " + sdfFecha.format(ahora));
            lblHora.setText("Hora: " + sdfHora.format(ahora));
        });
        timer.start();
    }

    // ===== Crear panel items =====
    private JPanel crearPanelItems(List<ItemMenu> items, String tituloTexto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel(tituloTexto, SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(15));

        for(ItemMenu item : items) {
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));
            itemPanel.setBackground(new Color(245,245,245));
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200,200,200),1),
                    BorderFactory.createEmptyBorder(5,10,5,10)
            ));

            JLabel lblNombre = new JLabel(item.nombre + " - $" + item.precio);
            lblNombre.setFont(new Font("Arial", Font.PLAIN,16));
            itemPanel.add(lblNombre, BorderLayout.WEST);

            JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,0));
            controles.setBackground(new Color(245,245,245));

            JButton btnMenos = new JButton("-");
            JButton btnMas = new JButton("+");
            JLabel lblCantidad = new JLabel("0");
            lblCantidad.setFont(new Font("Arial", Font.BOLD,16));

            btnMas.addActionListener(e -> {
                int val = Integer.parseInt(lblCantidad.getText());
                lblCantidad.setText(String.valueOf(val+1));
                actualizarPedido(item.nombre,1);
            });

            btnMenos.addActionListener(e -> {
                int val = Integer.parseInt(lblCantidad.getText());
                if(val>0){
                    lblCantidad.setText(String.valueOf(val-1));
                    actualizarPedido(item.nombre,-1);
                }
            });

            controles.add(btnMenos);
            controles.add(lblCantidad);
            controles.add(btnMas);

            itemPanel.add(controles, BorderLayout.EAST);
            panel.add(itemPanel);
            panel.add(Box.createVerticalStrut(10));
        }

        return panel;
    }

    // ===== Actualizar pedido temporal =====
    private void actualizarPedido(String nombreItem, int cambio) {
        Pedido pedido = pedidosMesa.getOrDefault(numeroMesa,new Pedido());
        int cant = pedido.cantidades.getOrDefault(nombreItem,0);
        cant += cambio;
        if(cant<0) cant = 0;
        pedido.cantidades.put(nombreItem,cant);

        // Recalcular total
        double totalNuevo = 0;
        for(Map.Entry<String,Integer> e: pedido.cantidades.entrySet()){
            for(ItemMenu im: platos) if(im.nombre.equals(e.getKey())) totalNuevo += im.precio*e.getValue();
            for(ItemMenu im: acompanamientos) if(im.nombre.equals(e.getKey())) totalNuevo += im.precio*e.getValue();
        }
        pedido.total = totalNuevo;
        pedidosMesa.put(numeroMesa,pedido);
        lblTotal.setText(String.format("TOTAL: $%.2f",pedido.total));
    }

    // ===== Cargar pedido previo =====
    private void cargarPedidoPrevio(){
        Pedido pedido = pedidosMesa.get(numeroMesa);
        if(pedido!=null) lblTotal.setText(String.format("TOTAL: $%.2f",pedido.total));
    }

    // ===== Resetear pedido =====
    private void resetearPedido(){
        Pedido pedido = pedidosMesa.get(numeroMesa);
        if(pedido!=null) pedido.cantidades.clear();
        pedidosMesa.remove(numeroMesa);
        lblTotal.setText("TOTAL: $0.00");
    }

    // ===== Obtener Platos =====
    private List<ItemMenu> obtenerPlatos() {
        List<ItemMenu> lista = new ArrayList<>();
        String sql = "SELECT nombre, precio FROM Plato";
        try(Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while(rs.next()){
                lista.add(new ItemMenu(rs.getString("nombre"), rs.getDouble("precio")));
            }
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null,"Error cargando platos: "+e.getMessage());
        }
        return lista;
    }

    // ===== Obtener Acompañamientos =====
    private List<ItemMenu> obtenerAcompanamientos() {
        List<ItemMenu> lista = new ArrayList<>();
        String sql = "SELECT nombre, precio FROM Acompanamiento";
        try(Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while(rs.next()){
                lista.add(new ItemMenu(rs.getString("nombre"), rs.getDouble("precio")));
            }
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null,"Error cargando acompañamientos: "+e.getMessage());
        }
        return lista;
    }

    // ===== Registrar pago y generar ticket =====
    private void registrarPago(){
        Pedido pedido = pedidosMesa.get(numeroMesa);
        if(pedido==null) return;

        java.util.Date ahora = new java.util.Date(); // <--- CORREGIDO: usar java.util.Date explícito

        // ===== Guardar en Ganancia =====
        SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");

        String sql = "INSERT INTO Ganancia (mesa_id, fecha, hora, total) VALUES (?,?,?,?)";

        try(Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, numeroMesa);
            ps.setString(2, sdfFecha.format(ahora));
            ps.setString(3, sdfHora.format(ahora));
            ps.setDouble(4, pedido.total);
            ps.executeUpdate();

        } catch(Exception e){
            JOptionPane.showMessageDialog(frame,"Error registrando pago: "+e.getMessage());
            return;
        }

        // ===== Preparar mapas de precios =====
        Map<String, Double> preciosPlatos = new HashMap<>();
        for(ItemMenu im : platos) preciosPlatos.put(im.nombre, im.precio);

        Map<String, Double> preciosAcomp = new HashMap<>();
        for(ItemMenu im : acompanamientos) preciosAcomp.put(im.nombre, im.precio);

        // ===== Mostrar ticket =====
        Comanda.generarTicket(numeroMesa, pedido.cantidades, preciosPlatos, preciosAcomp, ahora);
    }
}
