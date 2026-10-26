package app.zabludo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.zabludo.data.*
import app.zabludo.ui.theme.ZaBlyudoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZaBlyudoTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppRoot()
                }
            }
        }
    }
}

private enum class Screen { HOME, RESTAURANT, DISH }

@Composable
private fun AppRoot() {
    var screen by remember { mutableStateOf(Screen.HOME) }
    var cityId by remember { mutableStateOf(AppData.cities.first().id) }
    var restaurantId by remember { mutableStateOf<String?>(null) }
    var dishId by remember { mutableStateOf<String?>(null) }
    var dishReturnScreen by remember { mutableStateOf(Screen.HOME) }
    var query by remember { mutableStateOf("") }

    when (screen) {
        Screen.HOME -> HomeScreen(
            cityId = cityId,
            onCitySelected = { cityId = it },
            query = query,
            onQueryChange = { query = it },
            onOpenRestaurant = { id -> restaurantId = id; screen = Screen.RESTAURANT },
            onOpenDish = { id -> dishId = id; dishReturnScreen = Screen.HOME; screen = Screen.DISH }
        )
        Screen.RESTAURANT -> {
            val rid = restaurantId
            if (rid != null) {
                RestaurantScreen(
                    restaurantId = rid,
                    onBack = { screen = Screen.HOME },
                    onOpenDish = { id -> dishId = id; dishReturnScreen = Screen.RESTAURANT; screen = Screen.DISH }
                )
            } else { screen = Screen.HOME }
        }
        Screen.DISH -> {
            val did = dishId
            if (did != null) {
                DishScreen(
                    dishId = did,
                    onBack = { screen = dishReturnScreen },
                    onOpenRestaurant = { rid -> restaurantId = rid; screen = Screen.RESTAURANT }
                )
            } else { screen = Screen.HOME }
        }
    }
}

@Composable
private fun HomeScreen(
    cityId: String,
    onCitySelected: (String) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenRestaurant: (String) -> Unit,
    onOpenDish: (String) -> Unit
) {
    val results = remember(query) { AppData.searchDishes(query) }
    val restaurants = remember(cityId) { AppData.restaurantsInCity(cityId) }
    val recommended = remember(cityId) { AppData.recommendedDishes(cityId) }

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("За блюдо", fontSize = 30.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary) }
        
        item { OutlinedTextField(value = query, onValueChange = onQueryChange, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Поиск блюда...") }, singleLine = true) }
        
        item { 
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppData.cities.forEach { c ->
                    Chip(text = c.name, selected = c.id == cityId, onClick = { onCitySelected(c.id) })
                }
            }
        }

        if (query.isNotBlank()) {
            item { Text("Найдено: ${results.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            items(results) { d -> DishCard(d) { onOpenDish(d.id) } }
        } else {
            if (recommended.isNotEmpty()) {
                item { Text("Рекомендуем", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                items(recommended) { d -> DishCard(d) { onOpenDish(d.id) } }
            }
            
            item { Text("Рестораны города", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            items(restaurants) { r -> RestCard(r) { onOpenRestaurant(r.id) } }
        }
    }
}

@Composable
private fun RestaurantScreen(restaurantId: String, onBack: () -> Unit, onOpenDish: (String) -> Unit) {
    val rest = AppData.restaurant(restaurantId) ?: return
    val dishes = AppData.dishesForRestaurant(restaurantId)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = onBack) { Text("Назад") }
        Spacer(Modifier.height(16.dp))
        Text(rest.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(rest.address, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Text("Меню:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(dishes) { d -> DishCard(d) { onOpenDish(d.id) } }
        }
    }
}

@Composable
private fun DishScreen(dishId: String, onBack: () -> Unit, onOpenRestaurant: (String) -> Unit) {
    val dish = AppData.dish(dishId) ?: return
    val rest = AppData.restaurant(dish.restaurantId)
    
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Button(onClick = onBack) { Text("Назад") }
        Spacer(Modifier.height(16.dp))
        Text(dish.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        // Галерея (заглушки)
        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            dish.photos.take(3).forEachIndexed { i, _ ->
                Box(modifier = Modifier.width(100.dp).height(100.dp).background(MaterialTheme.colorScheme.surfaceVariant).clip(RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Text("Фото $i", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MacroBox("Ккал", "${dish.kcal}")
            MacroBox("Белки", "${dish.proteins}г")
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MacroBox("Жиры", "${dish.fats}г")
            MacroBox("Угл.", "${dish.carbs}г")
        }
        
        Spacer(Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Состав", fontWeight = FontWeight.Bold)
                Text(dish.ingredients.joinToString(", "))
                Spacer(Modifier.height(8.dp))
                Text("Вариации:", fontWeight = FontWeight.Bold)
                dish.variations.forEach { Text("- $it") }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        if (rest != null) {
            Button(onClick = { onOpenRestaurant(rest.id) }, modifier = Modifier.fillMaxWidth()) {
                Text("Где попробовать: ${rest.name}")
            }
        }
    }
}

// Вспомогательные компоненты
@Composable
fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 6.dp)) {
        Text(text, color = fg, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun DishCard(dish: Dish, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(60.dp).background(MaterialTheme.colorScheme.surfaceVariant).clip(RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Text("🍽️")
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(dish.name, fontWeight = FontWeight.Bold)
                Text("${dish.kcal} ккал • Б:${dish.proteins} Ж:${dish.fats} У:${dish.carbs}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun RestCard(rest: Restaurant, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(rest.name, fontWeight = FontWeight.Bold)
            Text(rest.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun MacroBox(label: String, value: String) {
    Card(modifier = Modifier.weight(1f)) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, fontWeight = FontWeight.Bold)
        }
    }
}
