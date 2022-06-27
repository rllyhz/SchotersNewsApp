package id.rllyhz.schotersnewsapp.utils

open class Event<out T>(
    private val content: T
) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }

    fun getContentAnyway(): T = content
}