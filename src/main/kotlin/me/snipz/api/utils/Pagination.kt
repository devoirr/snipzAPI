package me.snipz.api.utils

class Pagination<T>(items: List<T>, pageSize: Int) {

    private val pages = mutableListOf<List<T>>()

    init {
        var list = mutableListOf<T>()

        for (item in items) {
            list.add(item)

            if (list.count() == pageSize) {
                pages.add(list)
                list = mutableListOf()
            }
        }

        if (list.isNotEmpty()) {
            pages.add(list)
        }

        if (pages.isEmpty()) {
            pages.add(emptyList())
        }
    }

    fun getPage(page: Int): List<T> {

        if (page < 0)
            return pages[0]

        if (pages.count() <= page) {
            println("Page $page out of range!")
            return emptyList()
        }

        return pages[page]
    }

    fun count() = pages.count()

}