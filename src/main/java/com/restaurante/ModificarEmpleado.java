package com.restaurante;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ModificarEmpleado extends JFrame {

    private static final String URL = "jdbc:sqlite:Restaurante.db";

    private JComboBox<String> empleadosCombo;
    private JTextField nombreField, apellidoField, dniField, cuilField, direccionField, localidadField, fechaIngresoField;
    private JComboBox<String> perfilCombo, rolCombo, anosExperienciaCombo;
    private JTextField sueldoInicialField, sueldoFinalField, recargoField;
    private JCheckBox activoCheck;

    public ModificarEmpleado() {
        setTitle("Modificar Empleado");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBackground(new Color(245,245,245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        add(mainPanel);

        JLabel titulo = new JLabel("Modificar Empleado", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(50,50,50));
        titulo.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
        mainPanel.add(titulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245,245,245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,10,8,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Campos básicos
        empleadosCombo = new JComboBox<>();
        cargarEmpleados();

        nombreField = new JTextField();
        apellidoField = new JTextField();
        dniField = new JTextField();
        dniField.setEditable(false); // no editable
        cuilField = new JTextField();
        direccionField = new JTextField();
        localidadField = new JTextField();
        fechaIngresoField = new JTextField();
        rolCombo = new JComboBox<>(new String[]{"Administrador", "Empleado"});
        perfilCombo = new JComboBox<>(new String[]{"null","encargado","mozo"});

        addField(formPanel, gbc,"Empleado (DNI):", empleadosCombo);
        addField(formPanel, gbc,"Nombre:", nombreField);
        addField(formPanel, gbc,"Apellido:", apellidoField);
        addField(formPanel, gbc,"DNI:", dniField);
        addField(formPanel, gbc,"CUIL:", cuilField);
        addField(formPanel, gbc,"Dirección:", direccionField);
        addField(formPanel, gbc,"Localidad:", localidadField);
        addField(formPanel, gbc,"Fecha Ingreso:", fechaIngresoField);
        addField(formPanel, gbc,"Rol:", rolCombo);
        addField(formPanel, gbc,"Perfil:", perfilCombo);

        // Panel DatosEmpleado
        JPanel datosPanel = new JPanel(new GridBagLayout());
        datosPanel.setBackground(new Color(230,250,250));
        datosPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(60,179,113)),
                "Datos Empleado", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16), new Color(60,179,113)));
        GridBagConstraints gbcDatos = new GridBagConstraints();
        gbcDatos.insets = new Insets(5,10,5,10);
        gbcDatos.fill = GridBagConstraints.HORIZONTAL;
        gbcDatos.gridx=0; gbcDatos.gridy=0;

        sueldoInicialField = new JTextField();
        sueldoFinalField = new JTextField(); sueldoFinalField.setEditable(false);
        recargoField = new JTextField(); recargoField.setEditable(false);
        activoCheck = new JCheckBox("Activo");
        anosExperienciaCombo = new JComboBox<>(new String[]{"Menor o igual a 1","Mayor a 1 y menor a 5","Mayor a 5"});

        addField(datosPanel, gbcDatos,"Sueldo Bruto Inicial:", sueldoInicialField);
        addField(datosPanel, gbcDatos,"Años Experiencia:", anosExperienciaCombo);
        addField(datosPanel, gbcDatos,"Recargo:", recargoField);
        addField(datosPanel, gbcDatos,"Sueldo Bruto Final:", sueldoFinalField);
        addField(datosPanel, gbcDatos,"", activoCheck);

        gbc.gridy++;
        formPanel.add(datosPanel, gbc);

        // Panel General
        JPanel generalPanel = new JPanel(new GridBagLayout());
        generalPanel.setBackground(new Color(220,220,220));
        generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
        GridBagConstraints gbcGeneral = new GridBagConstraints();
        gbcGeneral.insets = new Insets(5,10,5,10);
        gbcGeneral.fill = GridBagConstraints.HORIZONTAL;
        gbcGeneral.gridx=0; gbcGeneral.gridy=0;

        JButton generalBtn = new JButton("General");
        generalBtn.setFont(new Font("Segoe UI", Font.BOLD,16));
        generalBtn.setBackground(new Color(100,149,237));
        generalBtn.setForeground(Color.WHITE);
        generalBtn.setFocusPainted(false);

        JPanel generalSubPanel = new JPanel(new GridBagLayout());
        generalSubPanel.setVisible(false);
        JTextField montoField = new JTextField(); montoField.setColumns(10);
        JButton sumarBtn = new JButton("Sumar"); JButton restarBtn = new JButton("Restar");

        GridBagConstraints gbcSub = new GridBagConstraints();
        gbcSub.insets = new Insets(5,5,5,5);
        gbcSub.gridx=0; gbcSub.gridy=0; generalSubPanel.add(new JLabel("Monto:"), gbcSub);
        gbcSub.gridx=1; generalSubPanel.add(montoField, gbcSub);
        gbcSub.gridx=0; gbcSub.gridy=1; generalSubPanel.add(sumarBtn, gbcSub);
        gbcSub.gridx=1; generalSubPanel.add(restarBtn, gbcSub);

        gbcGeneral.gridx=0; gbcGeneral.gridy=0; generalPanel.add(generalBtn, gbcGeneral);
        gbcGeneral.gridy=1; generalPanel.add(generalSubPanel, gbcGeneral);

        gbc.gridy++;
        formPanel.add(generalPanel, gbc);

        JScrollPane scroll = new JScrollPane(formPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scroll, BorderLayout.CENTER);

        JButton guardarBtn = new JButton("Guardar Cambios");
        guardarBtn.setFont(new Font("Segoe UI",Font.BOLD,16));
        guardarBtn.setBackground(new Color(60,179,113));
        guardarBtn.setForeground(Color.WHITE);
        guardarBtn.setFocusPainted(false);
        JPanel btnPanel = new JPanel(); btnPanel.setBackground(new Color(245,245,245));
        btnPanel.add(guardarBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        // Listeners
        empleadosCombo.addActionListener(e -> cargarDatosEmpleado());
        guardarBtn.addActionListener(e -> actualizarEmpleado());

        generalBtn.addActionListener(e -> generalSubPanel.setVisible(!generalSubPanel.isVisible()));
        ActionListener modificarSueldos = e -> {
            String texto = montoField.getText();
            if(texto.isEmpty()) return;
            try{
                double monto = Double.parseDouble(texto);
                boolean sumar = e.getSource()==sumarBtn;
                String sql = "UPDATE DatosEmpleado SET sueldo_bruto_inicial = sueldo_bruto_inicial " +
                        (sumar?"+ ?":"- ?")+", sueldo_bruto_final = sueldo_bruto_inicial * (1 + recargo)";
                try(Connection conn = DriverManager.getConnection(URL);
                    PreparedStatement ps = conn.prepareStatement(sql)){
                    ps.setDouble(1,monto); ps.executeUpdate();
                    JOptionPane.showMessageDialog(this,"Sueldo de todos los empleados actualizado correctamente");
                    cargarDatosEmpleado();
                }
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this,"Ingrese un monto válido","Error",JOptionPane.ERROR_MESSAGE);
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(this,"Error al actualizar sueldos:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        };
        sumarBtn.addActionListener(modificarSueldos);
        restarBtn.addActionListener(modificarSueldos);

        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field){
        if(!labelText.isEmpty()){
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI",Font.PLAIN,16));
            label.setForeground(new Color(50,50,50));
            gbc.gridx=0; gbc.gridwidth=1; panel.add(label,gbc);
        }
        gbc.gridx=1;
        field.setFont(new Font("Segoe UI",Font.PLAIN,16));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(180,180,180),1));
        panel.add(field,gbc);
        gbc.gridy++;
    }

    private void cargarEmpleados(){
        String sql="SELECT dni FROM Usuario WHERE rol='Empleado'";
        try(Connection conn = DriverManager.getConnection(URL);
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)){
            while(rs.next()){
                empleadosCombo.addItem(rs.getString("dni"));
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error al cargar empleados","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosEmpleado(){
        if(empleadosCombo.getSelectedItem()==null) return;
        String dni = empleadosCombo.getSelectedItem().toString();
        try(Connection conn = DriverManager.getConnection(URL)){
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT u.*, d.sueldo_bruto_inicial, d.sueldo_bruto_final, d.anos_experiencia, d.recargo, d.esta_activo " +
                            "FROM Usuario u LEFT JOIN DatosEmpleado d ON u.id=d.usuario_id WHERE u.dni=?");
            ps.setString(1,dni);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                nombreField.setText(rs.getString("nombre"));
                apellidoField.setText(rs.getString("apellido"));
                dniField.setText(rs.getString("dni"));
                cuilField.setText(rs.getString("cuil"));
                direccionField.setText(rs.getString("direccion"));
                localidadField.setText(rs.getString("localidad"));
                fechaIngresoField.setText(rs.getString("fecha_ingreso"));
                rolCombo.setSelectedItem(rs.getString("rol"));
                perfilCombo.setSelectedItem(rs.getString("perfil")==null?"null":rs.getString("perfil"));

                sueldoInicialField.setText(rs.getString("sueldo_bruto_inicial"));
                sueldoFinalField.setText(rs.getString("sueldo_bruto_final"));
                recargoField.setText(rs.getString("recargo"));
                anosExperienciaCombo.setSelectedItem(rs.getString("anos_experiencia"));

                boolean activo = rs.getInt("esta_activo")==1;
                activoCheck.setSelected(activo);
                activoCheck.setVisible(!activo); // solo si está de baja
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error al cargar datos del empleado","Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEmpleado(){
        String sqlUsuario="UPDATE Usuario SET nombre=?, apellido=?, cuil=?, direccion=?, localidad=?, fecha_ingreso=?, rol=?, perfil=? WHERE dni=?";
        String sqlDatos="UPDATE DatosEmpleado SET sueldo_bruto_inicial=?, sueldo_bruto_final=?, anos_experiencia=?, recargo=?, esta_activo=? " +
                "WHERE usuario_id=(SELECT id FROM Usuario WHERE dni=?)";
        try(Connection conn = DriverManager.getConnection(URL)){
            PreparedStatement psU = conn.prepareStatement(sqlUsuario);
            psU.setString(1,nombreField.getText());
            psU.setString(2,apellidoField.getText());
            psU.setString(3,cuilField.getText());
            psU.setString(4,direccionField.getText());
            psU.setString(5,localidadField.getText());
            psU.setString(6,fechaIngresoField.getText());
            psU.setString(7,rolCombo.getSelectedItem().toString());
            psU.setString(8, perfilCombo.getSelectedItem().toString().equals("null")?null:perfilCombo.getSelectedItem().toString());
            psU.setString(9,dniField.getText());
            psU.executeUpdate();

            PreparedStatement psD = conn.prepareStatement(sqlDatos);
            psD.setDouble(1,Double.parseDouble(sueldoInicialField.getText()));
            psD.setDouble(2,Double.parseDouble(sueldoFinalField.getText()));
            psD.setString(3,anosExperienciaCombo.getSelectedItem().toString());
            psD.setDouble(4,Double.parseDouble(recargoField.getText()));
            psD.setInt(5,activoCheck.isSelected()?1:0);
            psD.setString(6,dniField.getText());
            psD.executeUpdate();

            JOptionPane.showMessageDialog(this,"Empleado actualizado correctamente");
            cargarDatosEmpleado();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this,"Error al actualizar empleado\n"+e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args){
        new ModificarEmpleado();
    }
}
