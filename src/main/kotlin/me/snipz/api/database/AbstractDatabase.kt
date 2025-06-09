package me.snipz.api.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.ResultSet

abstract class AbstractDatabase {

    abstract fun getConnection(): Connection

    private suspend fun <T> withConnection(action: (Connection) -> T): T = withContext(Dispatchers.IO) {
        getConnection().use { connection ->
            action(connection)
        }
    }

    suspend fun query(sql: String, resultSetHandler: (ResultSet) -> Unit, vararg params: Any?) {
        withConnection { connection ->
            connection.prepareStatement(sql).use {
                params.forEachIndexed { index, param ->
                    it.setObject(index + 1, param)
                }

                it.executeQuery().use { resultSet ->
                    resultSetHandler.invoke(resultSet)
                }
            }
        }
    }

    suspend fun update(sql: String, vararg params: Any?): Int {
        var updateRows = 0
        withConnection { connection ->
            connection.prepareStatement(sql).use {
                params.forEachIndexed { index, param ->
                    it.setObject(index + 1, param)
                }

                updateRows = it.executeUpdate()
            }
        }

        return updateRows
    }
}