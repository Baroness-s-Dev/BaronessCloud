package ru.baronessdev.lib.cloud.log;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Logger {

    private final ChatColor INFO_PREFIX_COLOR;
    private final ChatColor INFO_TEXT_COLOR;
    private final String INFO_LABEL;

    private final ChatColor ERROR_PREFIX_COLOR;
    private final ChatColor ERROR_TEXT_COLOR;
    private final String ERROR_LABEL;

    private final ChatColor PREFIX_COLOR;
    private final String PREFIX;

    public Logger(ChatColor info_color, ChatColor info_text_color, String info_label, ChatColor error_color, ChatColor error_text_color, String error_label, ChatColor prefix_color, String prefix) {
        INFO_PREFIX_COLOR = info_color;
        INFO_TEXT_COLOR = info_text_color;
        INFO_LABEL = info_label;
        ERROR_PREFIX_COLOR = error_color;
        ERROR_TEXT_COLOR = error_text_color;
        ERROR_LABEL = error_label;
        PREFIX_COLOR = prefix_color;
        PREFIX = prefix;
    }

    public void log(LogLevel level, String s) {
        System.out.println(getPrefix(level) + s);
    }

    private String getPrefix(LogLevel type) {
        switch (type) {
            case INFO:
                return PREFIX_COLOR + PREFIX + " " + INFO_PREFIX_COLOR + INFO_LABEL + " " + INFO_TEXT_COLOR;
            case ERROR:
                return PREFIX_COLOR + PREFIX + " " + ERROR_PREFIX_COLOR + ERROR_LABEL + " " + ERROR_TEXT_COLOR;
            default:
                return "";
        }
    }

    public static class Builder {

        private ChatColor INFO_PREFIX_COLOR = ChatColor.YELLOW;
        private ChatColor INFO_TEXT_COLOR = ChatColor.WHITE;
        private String INFO_LABEL = "[INFO]";

        private ChatColor ERROR_PREFIX_COLOR = ChatColor.DARK_RED;
        private ChatColor ERROR_TEXT_COLOR = ChatColor.RED;
        private String ERROR_LABEL = "[ERROR]";

        private ChatColor PREFIX_COLOR = ChatColor.GOLD;
        private final String PREFIX;

        public Builder(@NotNull String prefix) {
            PREFIX = prefix;
        }

        public Builder setInfoPrefixColor(@NotNull ChatColor INFO_PREFIX_COLOR) {
            this.INFO_PREFIX_COLOR = INFO_PREFIX_COLOR;
            return this;
        }

        public Builder setInfoTextColor(@NotNull ChatColor INFO_TEXT_COLOR) {
            this.INFO_TEXT_COLOR = INFO_TEXT_COLOR;
            return this;
        }

        public Builder setInfoLabel(@NotNull String INFO_LABEL) {
            this.INFO_LABEL = INFO_LABEL;
            return this;
        }

        public Builder setErrorPrefixColor(@NotNull ChatColor ERROR_PREFIX_COLOR) {
            this.ERROR_PREFIX_COLOR = ERROR_PREFIX_COLOR;
            return this;
        }

        public Builder setErrorTextColor(@NotNull ChatColor ERROR_TEXT_COLOR) {
            this.ERROR_TEXT_COLOR = ERROR_TEXT_COLOR;
            return this;
        }

        public Builder setErrorLabel(@NotNull String ERROR_LABEL) {
            this.ERROR_LABEL = ERROR_LABEL;
            return this;
        }

        public void setPrefixColor(ChatColor PREFIX_COLOR) {
            this.PREFIX_COLOR = PREFIX_COLOR;
        }

        public Logger build() {
            return new Logger(
                    INFO_PREFIX_COLOR,
                    INFO_TEXT_COLOR,
                    INFO_LABEL,
                    ERROR_PREFIX_COLOR,
                    ERROR_TEXT_COLOR,
                    ERROR_LABEL,
                    PREFIX_COLOR,
                    PREFIX
            );
        }
    }
}
