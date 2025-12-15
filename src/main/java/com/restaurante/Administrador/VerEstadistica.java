package com.restaurante.Administrador;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class VerEstadistica extends JFrame {

    private static final String DB_URL = "jdbc:sqlite:Restaurante.db";

    public VerEstadistica() {
        setTitle("Estadísticas de Ganancias");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        JLabel titulo = new JLabel("Estadísticas de Ganancias", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(titulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        panel.add(tabs, BorderLayout.CENTER);

        // Obtener datos
        Map<String, Double> gananciasDia = obtenerGananciasPorDia();
        Map<String, Double> gananciasMes = obtenerGananciasPorMes();

        // Tab texto
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        StringBuilder texto = new StringBuilder();

        texto.append("Ganancias por Día:\n");
        double totalDia = 0;
        for (String fecha : gananciasDia.keySet()) {
            double val = gananciasDia.get(fecha);
            texto.append(fecha).append(" : $").append(String.format("%.2f", val)).append("\n");
            totalDia += val;
        }
        texto.append("TOTAL DEL DÍA: $").append(String.format("%.2f", totalDia)).append("\n\n");

        texto.append("Ganancias por Mes:\n");
        double totalMes = 0;
        for (String mes : gananciasMes.keySet()) {
            double val = gananciasMes.get(mes);
            texto.append(mes).append(" : $").append(String.format("%.2f", val)).append("\n");
            totalMes += val;
        }
        texto.append("TOTAL DEL MES: $").append(String.format("%.2f", totalMes)).append("\n");

        area.setText(texto.toString());
        tabs.add("Texto", new JScrollPane(area));

        // Tab gráficos
        JPanel graficoPanel = new JPanel(new GridLayout(2, 1, 0, 20));

        // Gráfico diario: solo últimas 7 fechas
        Map<String, Double> ultimas7Fechas = obtenerUltimasFechas(gananciasDia, 7);
        graficoPanel.add(crearGraficoDiario("Ganancias por Día", "Fecha", "Monto ($)", ultimas7Fechas, new Color(0, 123, 255)));

        // Gráfico mensual: eje Y de 0 a 20.000.000 en saltos de 2.000.000
        graficoPanel.add(crearGraficoMensual("Ganancias por Mes", "Mes", "Monto ($)", gananciasMes, new Color(40, 167, 69)));

        tabs.add("Gráficos", graficoPanel);

        setVisible(true);
    }

    // ===== Crear gráfico diario con eje Y fijo =====
    private JPanel crearGraficoDiario(String titulo, String categoria, String valor, Map<String, Double> datos, Color color) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String key : datos.keySet()) {
            dataset.addValue(datos.get(key), "Ganancias", key);
        }

        JFreeChart chart = ChartFactory.createBarChart(titulo, categoria, valor, dataset);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 240, 240));
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, color);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new BarRenderer().getBarPainter());
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 12));

        // Rotar etiquetas del eje X
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // Eje Y fijo de 0 a 1.000.000 en saltos de 100.000
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 1_000_000);
        rangeAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(100_000));

        return new ChartPanel(chart);
    }

    // ===== Crear gráfico mensual con eje Y fijo =====
    private JPanel crearGraficoMensual(String titulo, String categoria, String valor, Map<String, Double> datos, Color color) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String key : datos.keySet()) {
            dataset.addValue(datos.get(key), "Ganancias", key);
        }

        JFreeChart chart = ChartFactory.createBarChart(titulo, categoria, valor, dataset);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 240, 240));
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, color);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new BarRenderer().getBarPainter());
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 12));

        // Rotar etiquetas del eje X
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        // Eje Y fijo de 0 a 20.000.000 en saltos de 2.000.000
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 20_000_000);
        rangeAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(2_000_000));

        return new ChartPanel(chart);
    }

    // ===== Obtener últimas N fechas =====
    private Map<String, Double> obtenerUltimasFechas(Map<String, Double> original, int n) {
        Map<String, Double> ultimas = new LinkedHashMap<>();
        List<String> fechas = new ArrayList<>(original.keySet());
        int start = Math.max(fechas.size() - n, 0);
        for (int i = start; i < fechas.size(); i++) {
            String fecha = fechas.get(i);
            ultimas.put(fecha, original.get(fecha));
        }
        return ultimas;
    }

    // ===== Datos por día =====
    private Map<String, Double> obtenerGananciasPorDia() {
        Map<String, Double> ganancias = new TreeMap<>();
        String sql = "SELECT fecha, SUM(total) as suma FROM Ganancia GROUP BY fecha ORDER BY fecha ASC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ganancias.put(rs.getString("fecha"), rs.getDouble("suma"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando ganancias por día: " + e.getMessage());
        }
        return ganancias;
    }

    // ===== Datos por mes =====
    private Map<String, Double> obtenerGananciasPorMes() {
        Map<String, Double> ganancias = new TreeMap<>();
        String sql = "SELECT substr(fecha,1,7) as mes, SUM(total) as suma FROM Ganancia GROUP BY mes ORDER BY mes ASC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ganancias.put(rs.getString("mes"), rs.getDouble("suma"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando ganancias por mes: " + e.getMessage());
        }
        return ganancias;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VerEstadistica::new);
    }
}
