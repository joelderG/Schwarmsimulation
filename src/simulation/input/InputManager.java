package simulation.input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class InputManager {
    private final InputHandler inputHandler;

    public InputManager(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void processInput(int windowHeight) {
        processKeyboardInput();
        processMouseInput(windowHeight); // Fixed typo: was "procesMouseInput"
    }

    private void processKeyboardInput() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                KeyAction action = KeyAction.fromKeyCode(Keyboard.getEventKey());
                if (action != null) {
                    inputHandler.handleKeyAction(action);
                }
            }
        }
    }

    private void processMouseInput(int windowHeight) {
        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                int mouseX = Mouse.getEventX();
                int mouseY = windowHeight - Mouse.getEventY();

                if (Mouse.getEventButton() == 0) {
                    inputHandler.handleLeftClick(mouseX, mouseY);
                } else if (Mouse.getEventButton() == 1) {
                    inputHandler.handleRightClick(mouseX, mouseY);
                }
            }
        }
    }
}
