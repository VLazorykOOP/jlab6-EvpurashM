import java.util.Random;

public class MovingStrings {

    public static void main(String[] args) {

        String[] lines = {
                "Hello world!",
                "Java programming",
                "Moving text",
                "Parallel motion",
                "Multithreading demo"
        };

        for (String line : lines) {
            new MovingString(line).start();
        }
    }
}

class MovingString extends Thread {

    private final String text;
    private int x, y;
    private final int direction;
    private final Random rand = new Random();

    public MovingString(String text) {
        this.text = text;

        // початкова випадкова позиція
        this.x = rand.nextInt(40) + 1;
        this.y = rand.nextInt(10) + 1;

        // випадковий напрямок (0–3)
        this.direction = rand.nextInt(4);
    }

    @Override
    public void run() {
        try {
            while (true) {

                // очистити екран
                System.out.print("\033[H\033[2J");
                System.out.flush();

                // відобразити текст
                printAtPosition(text, x, y);

                // рух
                switch (direction) {
                    case 0: x++; break; // вправо
                    case 1: y++; break; // вниз
                    case 2: x--; break; // вліво
                    case 3: y--; break; // вверх
                }

                // екранні межі (щоб не зникало)
                if (x < 0) x = 0;
                if (y < 0) y = 0;
                if (x > 80) x = 80;
                if (y > 22) y = 22;

                Thread.sleep(150);
            }
        } catch (InterruptedException ignored) {}
    }

    private void printAtPosition(String s, int x, int y) {
        for (int i = 0; i < y; i++) System.out.println();

        for (int i = 0; i < x; i++) System.out.print(" ");

        System.out.println(s);
    }
}
