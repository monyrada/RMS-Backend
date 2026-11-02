package rmsbackend.enums;

import lombok.Getter;

/**
 * System permissions seeded at startup. Authorities are exposed to Spring Security
 * as the permission code itself, so {@code hasAuthority('ORDER_VIEW')} matches {@link #ORDER_VIEW}.
 * A page is visible to a user when they hold its {@code *_VIEW} permission.
 */
@Getter
public enum PermissionName {
    ORDER_VIEW("View orders"),
    ORDER_CREATE("Create orders"),
    ORDER_UPDATE("Update orders and their status"),
    ORDER_DELETE("Delete orders"),

    MENU_VIEW("View items, categories, ingredients and item details"),
    MENU_MANAGE("Create, update and delete menu data"),

    TABLE_VIEW("View tables"),
    TABLE_MANAGE("Create, update and delete tables"),

    CUSTOMER_VIEW("View customers"),

    USER_MANAGE("Manage users"),
    ROLE_MANAGE("Manage roles and their permissions");

    private final String description;

    PermissionName(String description) {
        this.description = description;
    }
}
