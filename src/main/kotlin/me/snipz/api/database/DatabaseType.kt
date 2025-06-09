package me.snipz.api.database

enum class DatabaseType(val local: Boolean) {

    H2(true),
    MYSQL(false);

}