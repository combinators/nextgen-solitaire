package domain.demon;

import domain.ContainerType;

public enum DemonContainerTypes implements ContainerType {
    // Special Container where cards are built down from kings.
    demonContainer("demon");

    public final String name;

    DemonContainerTypes(String name) {
        this.name = name;
    }

    public String getName() { return name; }
}
