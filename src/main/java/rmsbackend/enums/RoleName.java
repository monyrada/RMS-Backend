package rmsbackend.enums;

import lombok.Getter;

import java.util.EnumSet;
import java.util.Set;

/**
 * System roles seeded at startup. Authorities are exposed to Spring Security
 * as {@code ROLE_<name>}, so {@code hasRole('ADMIN')} matches {@link #ADMIN}.
 */
@Getter
public enum RoleName {
    ADMIN("Full access to every module, including users and roles"),
    MANAGER("Manages menu, tables, customers and orders"),
    WAITER("Takes and manages orders, views menu and tables"),
    KITCHEN("Views orders and updates their preparation status"),
    CASHIER("Views and settles orders, views menu and tables");

    private final String description;

    RoleName(String description) {
        this.description = description;
    }

    /**
     * Permissions granted to this role when it is first seeded.
     */
    public Set<PermissionName> defaultPermissions() {
        return switch (this) {
            case ADMIN -> EnumSet.allOf(PermissionName.class);
            case MANAGER -> EnumSet.of(
                    PermissionName.ORDER_VIEW, PermissionName.ORDER_CREATE,
                    PermissionName.ORDER_UPDATE, PermissionName.ORDER_DELETE,
                    PermissionName.MENU_VIEW, PermissionName.MENU_MANAGE,
                    PermissionName.TABLE_VIEW, PermissionName.TABLE_MANAGE,
                    PermissionName.CUSTOMER_VIEW);
            case WAITER -> EnumSet.of(
                    PermissionName.ORDER_VIEW, PermissionName.ORDER_CREATE,
                    PermissionName.ORDER_UPDATE, PermissionName.ORDER_DELETE,
                    PermissionName.MENU_VIEW, PermissionName.TABLE_VIEW,
                    PermissionName.CUSTOMER_VIEW);
            case KITCHEN -> EnumSet.of(
                    PermissionName.ORDER_VIEW, PermissionName.ORDER_UPDATE,
                    PermissionName.MENU_VIEW, PermissionName.TABLE_VIEW);
            case CASHIER -> EnumSet.of(
                    PermissionName.ORDER_VIEW, PermissionName.ORDER_CREATE,
                    PermissionName.ORDER_UPDATE,
                    PermissionName.MENU_VIEW, PermissionName.TABLE_VIEW,
                    PermissionName.CUSTOMER_VIEW);
        };
    }

    public static boolean isSystemRole(String name) {
        if (name == null) return false;
        for (RoleName role : values()) {
            if (role.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
