package com.restaurante.Empleado;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Comanda {

    public static void generarTicket(int numeroMesa, Map<String,Integer> cantidades,
                                     Map<String,Double> preciosPlatos,
                                     Map<String,Double> preciosAcomp,
                                     Date fechaHora) {

        JFrame frame = new JFrame("Comanda - Mesa " + numeroMesa);
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setEditable(false);

        SimpleDateFormat sdfFecha = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm:ss");

        area.append("MESA: " + numeroMesa + "\n");
        area.append("FECHA: " + sdfFecha.format(fechaHora) + "\n");
        area.append("HORA: " + sdfHora.format(fechaHora) + "\n");
        area.append("--------------------------------------\n");

        double total = 0;

        for(Map.Entry<String,Integer> e : cantidades.entrySet()){
            String nombre = e.getKey();
            int cant = e.getValue();
            double precio = preciosPlatos.getOrDefault(nombre, preciosAcomp.getOrDefault(nombre,0.0));
            if(cant>0){
                area.append(nombre + " x" + cant + " .... $" + String.format("%.2f", precio*cant) + "\n");
                total += precio*cant;
            }
        }

        area.append("--------------------------------------\n");
        area.append(String.format("TOTAL: $%.2f\n", total));

        JScrollPane scroll = new JScrollPane(area);
        frame.add(scroll);
        frame.setVisible(true);
    }
}
