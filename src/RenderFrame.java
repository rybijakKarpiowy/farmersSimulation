import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("ALL")
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
        try {
            BufferedImage originalImage = ImageIO.read(new File("src/images/bunny.png"));
            addImagePanel(row, col, originalImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFarmer(int row, int col) {
        try {
            BufferedImage originalImage = ImageIO.read(new File("src/images/farmer.png"));
            addImagePanel(row, col, originalImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addImagePanel(int row, int col, BufferedImage originalImage) {
        Image scaledImage = originalImage.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon rabbitIcon = new ImageIcon(scaledImage);
        JLabel rabbitLabel = new JLabel(rabbitIcon);
        panels[row][col].add(rabbitLabel);
        panels[row][col].revalidate();
        panels[row][col].repaint();
    }

    private void addDog(int row, int col) {
        try {
            BufferedImage originalImage = ImageIO.read(new File("src/images/dog.png"));
            addImagePanel(row, col, originalImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
