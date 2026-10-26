package app.zabludo.data

import java.util.Locale

data class City(val id: String, val name: String)
data class Restaurant(val id: String, val cityId: String, val name: String, val address: String = "", val cuisines: List<String> = emptyList(), val peoples: List<String> = emptyList())
data class Dish(val id: String, val restaurantId: String, val name: String, val description: String = "", val ingredients: List<String> = emptyList(), val kcal: Int = 0, val proteins: Int = 0, val fats: Int = 0, val carbs: Int = 0, val photos: List<String> = emptyList(), val variations: List<String> = emptyList(), val isRecommended: Boolean = false)

object AppData {
    val cities = listOf(City(id = "msk", name = "Москва"), City(id = "spb", name = "Санкт-Петербург"), City(id = "kzn", name = "Казань"))
    
    val restaurants = listOf(
        Restaurant(id = "msk_sushi_wok", cityId = "msk", name = "Sushi Wok", address = "ул. Тверская, 10", cuisines = listOf("Азиатская", "Японская"), peoples = listOf("Япония")),
        Restaurant(id = "msk_georgian", cityId = "msk", name = "Хинкали & Vino", address = "ул. Пятницкая, 25", cuisines = listOf("Грузинская"), peoples = listOf("Грузия")),
        Restaurant(id = "spb_italian", cityId = "spb", name = "Pasta Nostra", address = "Невский проспект, 30", cuisines = listOf("Итальянская"), peoples = listOf("Италия")),
        Restaurant(id = "kzn_tatar", cityId = "kzn", name = "Чак-Чак House", address = "ул. Баумана, 15", cuisines = listOf("Татарская"), peoples = listOf("Татары"))
    )

    val dishes = listOf(
        Dish(id = "ramen", restaurantId = "msk_sushi_wok", name = "Рамен", description = "Наваристый японский суп.", ingredients = listOf("Лапша", "Свинина", "Яйцо"), kcal = 650, proteins = 35, fats = 25, carbs = 70, photos = listOf("photo_ramen_1", "photo_ramen_2"), variations = listOf("Рамен с курицей"), isRecommended = true),
        Dish(id = "khachapuri", restaurantId = "msk_georgian", name = "Хачапури", description = "Грузинская лодочка с сыром.", ingredients = listOf("Тесто", "Сыр", "Масло"), kcal = 800, proteins = 30, fats = 45, carbs = 70, photos = listOf("photo_khachapuri_1"), variations = listOf("По-аджарски"), isRecommended = true),
        Dish(id = "caesar", restaurantId = "spb_italian", name = "Цезарь", description = "Салат с курицей.", ingredients = listOf("Курица", "Романо", "Пармезан"), kcal = 350, proteins = 25, fats = 20, carbs = 20, photos = listOf("photo_caesar_1"), variations = listOf("С креветками"), isRecommended = false),
        Dish(id = "echpochmak", restaurantId = "kzn_tatar", name = "Эчпочмак", description = "Треугольный пирожок.", ingredients = listOf("Тесто", "Говядина", "Картофель"), kcal = 420, proteins = 18, fats = 22, carbs = 40, photos = listOf("photo_echpochmak_1"), variations = listOf("С бараниной"), isRecommended = true)
    )

    fun city(id: String): City? = cities.firstOrNull { it.id == id }
    fun restaurant(id: String): Restaurant? = restaurants.firstOrNull { it.id == id }
    fun dish(id: String): Dish? = dishes.firstOrNull { it.id == id }
    fun dishesForRestaurant(restaurantId: String): List<Dish> = dishes.filter { it.restaurantId == restaurantId }
    fun restaurantsInCity(cityId: String): List<Restaurant> = restaurants.filter { it.cityId == cityId }
    fun recommendedDishes(cityId: String): List<Dish> = dishes.filter { d -> d.isRecommended && restaurant(d.restaurantId)?.cityId == cityId }
    
    fun searchDishes(query: String): List<Dish> {
        val q = query.trim().lowercase(Locale.ROOT)
        if (q.isEmpty()) return emptyList()
        return dishes.filter { d -> 
            d.name.lowercase(Locale.ROOT).contains(q) || 
            d.ingredients.any { it.lowercase(Locale.ROOT).contains(q) }
        }
    }
}
