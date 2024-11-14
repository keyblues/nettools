/**
 * @Author: keyblue
 * @Date: Created in 2024年11月13日 23:53:43
 * @Modified By:
 */
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setBounds(300, 300, 300, 200);
            frame.setTitle(ProgramInfo.PROGRAM_TITLE);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }
}
