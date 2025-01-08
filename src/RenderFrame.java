import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.util.concurrent.CountDownLatch;

public class RenderFrame extends JFrame {
    private final JPanel[][] panels;
    private final Field field;
    private volatile CountDownLatch latch = new CountDownLatch(1);

    public RenderFrame(int rows, int cols, Field field) {
        super("Render");
        this.field = field;
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(rows, cols));

        panels = new JPanel[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                panels[i][j] = new JPanel();
                panels[i][j].setBorder(new BasicBorders.FieldBorder(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK));
                add(panels[i][j]);
            }
        }

        field.renderLock();
        updateFields();
        field.renderUnlock();

        setVisible(true);
    }

    public void await() throws InterruptedException {
        latch.await();
    }

    public void countDown() {
        latch.countDown();
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    public void setColor(int row, int col, Color color) {
        panels[row][col].setBackground(color);
    }

    private void addRabbit(int row, int col) {
        // blue dot
        JPanel rabbit = new JPanel();
        rabbit.setBackground(Color.BLUE);
        rabbit.setBorder(new EmptyBorder(10, 10, 10, 10));
        panels[row][col].add(rabbit);
        panels[row][col].revalidate();
        panels[row][col].repaint();
    }

    private void addFarmer(int row, int col) {
        // pink dot
        JPanel farmer = new JPanel();
        farmer.setBackground(Color.PINK);
        farmer.setBorder(new EmptyBorder(10, 10, 10, 10));
        panels[row][col].add(farmer);
        panels[row][col].revalidate();
        panels[row][col].repaint();
    }

    private void addDog(int row, int col) {
        // green dot
        JPanel dog = new JPanel();
        dog.setBackground(Color.GREEN);
        dog.setBorder(new EmptyBorder(10, 10, 10, 10));
        panels[row][col].add(dog);
        panels[row][col].revalidate();
        panels[row][col].repaint();
    }

    private void addActor(ActorAbstract actor, int row, int col) {
        if (actor instanceof Rabbit) {
            addRabbit(row, col);
        } else if (actor instanceof Farmer) {
            addFarmer(row, col);
        } else if (actor instanceof Dog) {
            addDog(row, col);
        }
    }

    public void clearPanel(int row, int col) {
        panels[row][col].removeAll();
        panels[row][col].revalidate();
        panels[row][col].repaint();
    }

    public void updateFields() {
        for (int i = 0; i < field.getRows(); i++) {
            for (int j = 0; j < field.getCols(); j++) {
                clearPanel(i, j);
                setColor(i, j, field.getTileBackgroundColor(new Coordinates(i, j)));

                for (ActorAbstract actor : field.getActors(new Coordinates(i, j))) {
                    addActor(actor, i, j);
                }
            }
        }
        repaint();
        countDown();
    }

}
