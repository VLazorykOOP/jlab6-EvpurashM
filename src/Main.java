import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

class MyArithmeticException extends ArithmeticException {
    public MyArithmeticException(String msg) {
        super(msg);
    }
}

public class Main extends JFrame {

    private JTextField fileField;
    private JButton loadButton, calcButton;
    private JTable tableX, tableY;

    private DefaultTableModel modelX;
    private DefaultTableModel modelY;

    private double[][] matrix;  // X(n,n)

    public Main() {
        super("ЛР2: Матриця X(n,n) → Вектор Y(n)");

        // Верхня панель
        JPanel top = new JPanel();
        fileField = new JTextField(20);
        loadButton = new JButton("Завантажити матрицю");
        calcButton = new JButton("Обчислити Y");
        JButton createButton = new JButton("Створити файл");

        top.add(new JLabel("Файл:"));
        top.add(fileField);
        top.add(loadButton);
        top.add(calcButton);
        top.add(createButton);

        // Таблиці
        modelX = new DefaultTableModel();
        modelY = new DefaultTableModel();
        modelY.addColumn("Y(i)");
        tableX = new JTable(modelX);
        tableY = new JTable(modelY);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2));
        center.add(new JScrollPane(tableX));
        center.add(new JScrollPane(tableY));
        add(center, BorderLayout.CENTER);

        // Події
        createButton.addActionListener(e -> createFileIfNotExist());
        loadButton.addActionListener(e -> loadMatrix());
        calcButton.addActionListener(e -> computeY());

        setSize(900, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Метод створення файлу
    private void createFileIfNotExist() {
        String filePath = fileField.getText().trim().replace("\\", "/");
        File f = new File(filePath);

        try {
            if (f.exists()) {
                JOptionPane.showMessageDialog(this,
                        "Файл вже існує: " + f.getAbsolutePath(),
                        "Інформація", JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (f.createNewFile()) {
                    JOptionPane.showMessageDialog(this,
                            "Файл успішно створено: " + f.getAbsolutePath(),
                            "Інформація", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Не вдалося створити файл!",
                            "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Помилка при створенні файлу: " + ex.getMessage(),
                    "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Метод завантаження матриці
    private void loadMatrix() {
        String filePath = fileField.getText().trim().replace("\\", "/");
        File f = new File(filePath);

        System.out.println("Перевіряю файл за шляхом: " + f.getAbsolutePath());

        if (!f.exists()) {
            JOptionPane.showMessageDialog(this,
                    "Файл не знайдено!\nШлях: " + f.getAbsolutePath(),
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            JOptionPane.showMessageDialog(this, "Файл знайдено!", "Інформація",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            java.util.List<double[]> rows = new java.util.ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                double[] row = new double[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    try {
                        row[i] = Double.parseDouble(parts[i]);
                    } catch (NumberFormatException ex) {
                        throw new NumberFormatException("Невірний формат: " + parts[i]);
                    }
                }
                rows.add(row);
            }

            int n = rows.size();
            if (n == 0) throw new MyArithmeticException("Матриця порожня");
            if (n > 15) throw new MyArithmeticException("n > 15 — не дозволено");

            matrix = new double[n][n];
            modelX.setRowCount(0);
            modelX.setColumnCount(n);

            for (int i = 0; i < n; i++) {
                if (rows.get(i).length != n)
                    throw new MyArithmeticException("Матриця не квадратна!");
                matrix[i] = rows.get(i);
                Object[] rowObj = new Object[n];
                for (int j = 0; j < n; j++) rowObj[j] = matrix[i][j];
                modelX.addRow(rowObj);
            }

        } catch (IOException ex) {
            error("Помилка читання файлу: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            error("Невірний формат числа: " + ex.getMessage());
        } catch (MyArithmeticException ex) {
            error("Помилка матриці: " + ex.getMessage());
        }
    }

    // Метод обчислення Y
    private void computeY() {
        if (matrix == null) {
            error("Завантаж матрицю!");
            return;
        }

        int n = matrix.length;
        modelY.setRowCount(0);

        for (int i = 0; i < n; i++) {
            int firstNeg = -1;
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] < 0) {
                    firstNeg = j;
                    break;
                }
            }

            if (firstNeg == -1) {
                modelY.addRow(new Object[]{-1});
            } else {
                double sum = 0;
                for (int j = firstNeg; j < n; j++) {
                    sum += Math.abs(matrix[i][j]);
                }
                modelY.addRow(new Object[]{sum});
            }
        }
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Помилка", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        new Main();
    }
}
