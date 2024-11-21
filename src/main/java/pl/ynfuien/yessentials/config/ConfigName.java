package pl.ynfuien.yessentials.config;

import pl.ynfuien.ydevlib.config.ConfigObject;

public enum ConfigName implements ConfigObject.Name {
    LANG,
    CONFIG;

    @Override
    public String getFileName() {
        return name()
                .toLowerCase()
                .replace("__", "/")
                .replace('_', '-') + ".yml";
    }
}
