package gr.novidea.weatherpay.data

/**
 * A class representing a Button List Item.
 * @param label The name of the item.
 * @param icon The icon of the item. (optional)
 * @param action The button action.
 *
 */

class ButtonItem(val label: String, val action: Int, val icon: Int? = null, val data: Any = {}) {

    override fun toString(): String = label
}
