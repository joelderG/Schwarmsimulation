package simulation.input;

import org.lwjgl.input.Keyboard;

public enum KeyAction {
    PAUSE(Keyboard.KEY_SPACE, "Pause/Resume simulation"),
    SHOW_PATHS(Keyboard.KEY_P, "Toggle path visibility"),
    RESET(Keyboard.KEY_R, "Reset simulation"),
    CLEAR_OBSTACLES(Keyboard.KEY_C, "Clear obstacles"),

    INCREASE_SEPARATION(Keyboard.KEY_1, "Increase separation distance"),
    DECREASE_SEPARATION(Keyboard.KEY_2, "Decrease separation distance"),
    INCREASE_ALIGNMENT(Keyboard.KEY_3, "Increase alignment distance"),
    DECREASE_ALIGNMENT(Keyboard.KEY_4, "Decrease alignment distance"),
    INCREASE_COHESION(Keyboard.KEY_5, "Increase cohesion distance"),
    DECREASE_COHESION(Keyboard.KEY_6, "Decrease cohesion distance"),
    INCREASE_SWIMMING(Keyboard.KEY_7, "Increase swimming weight"),
    DECREASE_SWIMMING(Keyboard.KEY_8, "Decrease swimming weight"),
    INCREASE_AMPLITUDE(Keyboard.KEY_9, "Increase swimming amplitude"),
    DECREASE_AMPLITUDE(Keyboard.KEY_0, "Decrease swimming amplitude"),

    INCREASE_OBSTACLE_SIZE(Keyboard.KEY_EQUALS, "Increase obstacle size"),
    DECREASE_OBSTACLE_SIZE(Keyboard.KEY_MINUS, "Decrease obstacle size"),

    EXIT(Keyboard.KEY_ESCAPE, "Exit simulation");

    private final int keyCode;
    private final String description;

    KeyAction(int keyCode, String description) {
        this.keyCode = keyCode;
        this.description = description;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public String getDescription() {
        return description;
    }

    public static KeyAction fromKeyCode(int keyCode) {
        for (KeyAction action : values()) {
            if (action.keyCode == keyCode) {
                return action;
            }
        }
        return null;
    }
}
