package me.snipz.api.menu.filler

object MenuService {

    private val fillers = mutableMapOf<String, MenuFiller>()

    fun registerFiller(key: String, filler: MenuFiller) {
        fillers[key] = filler
        println("[МЕНЮ] Филлер $key зарегистрирован.")
    }

    fun getFillers(): Map<String, MenuFiller> {
        return fillers
    }
}
