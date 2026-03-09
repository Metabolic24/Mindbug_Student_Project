interface ButtonData {
    label: string;
    visible: boolean;
    disabled: boolean;
    event: BoardButtonsEvent;
}

type BoardButtonsEvent = "YES" | "NO" | "PLAY" | "ATTACK" | "ACTION" | "HUNT" | "MINDBUG" | "BLOCK" | "CONTINUE" | "LOSE_LP" | "NO_MINDBUG"