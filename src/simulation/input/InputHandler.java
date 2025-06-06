package simulation.input;

public interface InputHandler {
    void handleKeyAction(KeyAction action);
    void handleLeftClick(int x, int y);
    void handleRightClick(int x, int y);
}
