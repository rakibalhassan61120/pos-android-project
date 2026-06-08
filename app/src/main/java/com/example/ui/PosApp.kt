package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// Illustration presets representing clothing textures
val IllustrationTemplates = listOf(
    Triple("Traditional Red & Gold", Color(0xFF8B0000), "💃"),
    Triple("Royal Forest Silk", Color(0xFF004B23), "🧶"),
    Triple("Elite Indigo Kurti", Color(0xFF1D3557), "👚"),
    Triple("Cotton Linen Classic", Color(0xFFF4A261), "🧵"),
    Triple("Indigo Denim Touch", Color(0xFF264653), "👖"),
    Triple("Lace Lavender Dream", Color(0xFF70D6FF), "👗"),
    Triple("Mustard Yellow Saree", Color(0xFFE9C46A), "✨")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosApp(viewModel: PosViewModel) {
    val currentLang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val role by viewModel.currentRole.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val shopProfile by viewModel.shopProfile.collectAsStateWithLifecycle()

    CompositionLocalProvider(LocalAppLanguage provides currentLang) {
        val appName = Translations.get("app_title", currentLang)
        val profile = shopProfile ?: ShopProfile()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when (role) {
                AppRole.SELECT_ROLE -> {
                    RoleSelectScreen(viewModel)
                }
                else -> {
                    MainWorkspaceScreen(viewModel, role, isOnline, profile)
                }
            }
        }
    }
}

@Composable
fun RoleSelectScreen(viewModel: PosViewModel) {
    val lang = LocalAppLanguage.current
    var showPinDialog by remember { mutableStateOf(false) }
    var pinValue by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(24.dp)
    ) {
        // Language Toggle floating at top
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Language",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            TextButton(onClick = { viewModel.toggleLanguage() }) {
                Text(
                    text = if (lang == AppLanguage.BANGLA) "English" else "বাংলা (Bangla)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Center Content
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Monogram Shop Logo drawing
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AP",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = Translations.get("app_title", lang),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Premium Clothing Shop Store POS Workspace Manager",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = Translations.get("role_select", lang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Roles Cards Layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sales Assistant Option Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .clickable { viewModel.setRole(AppRole.SALESMAN) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cashier",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = Translations.get("salesman", lang),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Shop Owner Protected Access Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(160.dp)
                        .clickable { showPinDialog = true },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Owner",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = Translations.get("owner", lang),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    // Owner Secure Pin dialog code (Private Interfaces Requirement!)
    if (showPinDialog) {
        AlertDialog(
            onDismissRequest = {
                showPinDialog = false
                pinValue = ""
                pinError = false
            },
            title = {
                Text(
                    text = Translations.get("role_pass", lang),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = pinValue,
                        onValueChange = {
                            pinValue = it.take(4)
                            pinError = false
                        },
                        placeholder = { Text("Demo Pin: 1234") },
                        label = { Text("PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = pinError,
                        singleLine = true
                    )
                    if (pinError) {
                        Text(
                            text = Translations.get("access_error", lang),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Secure demonstration passcode validator
                        if (pinValue == "1234") {
                            showPinDialog = false
                            viewModel.setRole(AppRole.OWNER)
                            pinValue = ""
                            pinError = false
                            Toast.makeText(context, if (lang == AppLanguage.BANGLA) "অনুমতি মঞ্জুর করা হয়েছে!" else "Owner authenticated successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            pinError = true
                        }
                    }
                ) {
                    Text(Translations.get("submit", lang))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPinDialog = false
                    pinValue = ""
                    pinError = false
                }) {
                    Text(Translations.get("cancel", lang))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainWorkspaceScreen(
    viewModel: PosViewModel,
    role: AppRole,
    isOnline: Boolean,
    profile: ShopProfile
) {
    val lang = LocalAppLanguage.current
    var currentTab by remember { mutableStateOf(0) }
    val isPrinterConnected by viewModel.isPrinterConnected.collectAsStateWithLifecycle()
    val printedReceipt by viewModel.printedReceiptText.collectAsStateWithLifecycle()

    // Determine tabs based on private role accesses
    val tabs = remember(role, lang) {
        if (role == AppRole.OWNER) {
            listOf(
                Triple(Translations.get("home_pos", lang), Icons.Default.ShoppingCart, 0),
                Triple(Translations.get("products", lang), Icons.Default.Inventory, 1),
                Triple(Translations.get("reports", lang), Icons.Default.BarChart, 2),
                Triple(Translations.get("settings", lang), Icons.Default.Settings, 3)
            )
        } else {
            // Cashier restricts Reports and Core Boutique profile edits
            listOf(
                Triple(Translations.get("home_pos", lang), Icons.Default.ShoppingCart, 0),
                Triple(Translations.get("products", lang), Icons.Default.Inventory, 1),
                // Bluetooth thermal simulation tab is available for salesmen
                Triple(Translations.get("printer_setup", lang), Icons.Default.Print, 3)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Initial Monogram Logo updated by shop setup!
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile.logoText.take(2).uppercase(Locale.getDefault()),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = profile.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    // System Connection Status offline/online toggle & indicator
                    AssistChip(
                        onClick = { viewModel.toggleOnlineOffline() },
                        label = {
                            Text(
                                text = if (isOnline) Translations.get("online", lang) else Translations.get("offline", lang),
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isOnline) Icons.Default.Cloud else Icons.Default.CloudOff,
                                contentDescription = "Sync state",
                                tint = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Language Selector Button
                    IconButton(onClick = { viewModel.toggleLanguage() }) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Language toggle",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Log Out / Switch portals
                    IconButton(onClick = { viewModel.setRole(AppRole.SELECT_ROLE) }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Exit workspace",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab.third || (tab.third == 3 && currentTab == 4),
                        onClick = { currentTab = tab.third },
                        icon = { Icon(imageVector = tab.second, contentDescription = tab.first) },
                        label = { Text(text = tab.first, maxLines = 1) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> PosSalesScreen(viewModel)
                1 -> ProductsScreen(viewModel, editable = (role == AppRole.OWNER))
                2 -> if (role == AppRole.OWNER) ReportsScreen(viewModel)
                3 -> if (role == AppRole.OWNER) SettingsScreen(viewModel) else PrinterSimulatorScreen(viewModel)
            }

            // Real-time Visual Receipts Drawer Simulation overlaying workspace
            if (printedReceipt != null) {
                ViewThermalReceiptOverlay(
                    receiptText = printedReceipt ?: "",
                    onDismiss = { viewModel.dismissReceipt() }
                )
            }
        }
    }
}

// ----------------------------------------------------
// SCREEN 1: NEW SALES TRANSACTION TERMINAL (POS POINT)
// ----------------------------------------------------
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PosSalesScreen(viewModel: PosViewModel) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val products by viewModel.products.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val discountPercent by viewModel.discountPercentage.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showCheckoutDialog by remember { mutableStateOf(false) }
    val filteredProducts = remember(products, searchQuery) {
        products.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.code.contains(searchQuery, ignoreCase = true) ||
            it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Side: Inventory Search list layout (weight 3/5 on large screen)
        Column(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight()
        ) {
            // Searching
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text(Translations.get("search_hint", lang), style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = CircleShape,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            // Dynamic grid layout for garment items list
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = "Empty",
                            modifier = Modifier.size(54.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No clothing matches found!",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        ClothingPosGridCard(product = product, onAdd = { viewModel.addToCart(product) })
                    }
                }
            }
        }

        // Right Side: Quick Checkout Shopping Cart List (weight 2/5 on large screen layout)
        Card(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = "${Translations.get("cart", lang)} (${cart.sumOf { it.quantity }})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Cart rows scrolling panel
                Box(modifier = Modifier.weight(1f)) {
                    if (cart.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBasket,
                                    contentDescription = "Empty",
                                    modifier = Modifier.size(44.dp),
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = Translations.get("empty_cart", lang),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(cart, key = { it.product.id }) { item ->
                                CartRow(item = item, viewModel = viewModel)
                            }
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Financial checkout billing block
                if (cart.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    val rawTotal = cart.sumOf { it.product.sellPrice * it.quantity }
                    val calculatedDiscount = rawTotal * (discountPercent / 100)
                    val payableTotal = rawTotal - calculatedDiscount

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = Translations.get("total", lang),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "৳$rawTotal",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Offer Discounts Option Button panel (Applied Requirement!)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Translations.get("discount", lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(0.0, 5.0, 10.0, 15.0, 20.0).forEach { disc ->
                                    FilterChip(
                                        selected = discountPercent == disc,
                                        onClick = { viewModel.applyDiscount(disc) },
                                        label = { Text("${disc.toInt()}%", fontSize = 10.sp) },
                                        modifier = Modifier.height(24.dp)
                                    )
                                }
                            }
                        }

                        if (calculatedDiscount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Discount Coupon Saved",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFC62828)
                                )
                                Text(
                                    text = "-৳$calculatedDiscount",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFC62828),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Divider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = Translations.get("net_payable", lang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "৳$payableTotal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                viewModel.checkout(
                                    paymentMethod = "Cash",
                                    accountNo = null,
                                    trxId = null,
                                    cashier = "Admin Cashier",
                                    onComplete = {
                                        Toast.makeText(context, Translations.get("checkout_success", lang), Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32), // High-quality retail green
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = "Cash Pay")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.BANGLA) "নগদ বিক্রি (ক্যাশ)" else "Quick Cash Sale",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { showCheckoutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Gateways")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.BANGLA) "ডিজিটাল / মোবাইল ব্যাংক পেমেন্ট" else "Digital MFS Gateways",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Interactive Checkout Panel dialog (Payment Gateways support!)
    if (showCheckoutDialog) {
        CheckoutPayDialog(
            viewModel = viewModel,
            onDismiss = { showCheckoutDialog = false }
        )
    }
}

@Composable
fun ClothingPosGridCard(product: Product, onAdd: () -> Unit) {
    val lang = LocalAppLanguage.current
    val template = remember(product) {
        IllustrationTemplates.getOrElse(product.id % IllustrationTemplates.size) { IllustrationTemplates[0] }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (product.stock > 0) onAdd() },
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Visual simulated boutique textile illustration instead of generic icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(template.second)
                    .padding(8.dp)
            ) {
                // Stock Alert Badge (Low/Out of Stock indicator)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (product.stock == 0) Color(0xFFC62828)
                            else if (product.stock <= 5) Color(0xFFEF6C00)
                            else Color(0xFF2E7D32)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (product.stock == 0) "SOLD OUT" else "${product.stock} left",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Sizing badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = product.size,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Textile emoji rendering
                Text(
                    text = template.third,
                    fontSize = 32.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        maxLines = 1
                    )
                    Text(
                        text = "৳${product.sellPrice.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun CartRow(item: CartItem, viewModel: PosViewModel) {
    val template = remember(item.product) {
        IllustrationTemplates.getOrElse(item.product.id % IllustrationTemplates.size) { IllustrationTemplates[0] }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(template.second),
            contentAlignment = Alignment.Center
        ) {
            Text(text = template.third, fontSize = 14.sp)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.product.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Size: ${item.product.size} | ৳${item.product.sellPrice.toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Increment / Decrement actions with modern spacing
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            IconButton(
                onClick = { viewModel.updateCartQuantity(item.product.id, -1) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Dec",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "${item.quantity}",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.width(16.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { viewModel.updateCartQuantity(item.product.id, 1) },
                modifier = Modifier.size(24.dp),
                enabled = item.quantity < item.product.stock
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Inc",
                    modifier = Modifier.size(16.dp),
                    tint = if (item.quantity < item.product.stock) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

@Composable
fun CheckoutPayDialog(viewModel: PosViewModel, onDismiss: () -> Unit) {
    val lang = LocalAppLanguage.current
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val discountPercent by viewModel.discountPercentage.collectAsStateWithLifecycle()
    val rawTotal = cart.sumOf { it.product.sellPrice * it.quantity }
    val calculatedDiscount = rawTotal * (discountPercent / 100)
    val payableTotal = rawTotal - calculatedDiscount

    var selectedGateway by remember { mutableStateOf("Cash") } // Cash, bKash, Nagad, Rocket
    var walletNo by remember { mutableStateOf("") }
    var pinNo by remember { mutableStateOf("") }
    var transactionId by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Generate simulated Transaction keys dynamically
    LaunchedEffect(selectedGateway) {
        if (selectedGateway != "Cash") {
            val randomString = (1..6)
                .map { "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".random() }
                .joinToString("")
            transactionId = "${selectedGateway.first().uppercaseChar()}-$randomString"
        } else {
            transactionId = ""
            walletNo = ""
            pinNo = ""
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Payment Gateway",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Multi Gateway list selector: Cash, bKash, Nagad, Rocket (Payment requirements!)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Gateway row: Cash
                    PaymentGatewayRow(
                        title = Translations.get("cash", lang),
                        selected = selectedGateway == "Cash",
                        logoText = "৳",
                        brandColor = BrandCash,
                        onClick = { selectedGateway = "Cash" }
                    )

                    // Gateway row: bKash
                    PaymentGatewayRow(
                        title = Translations.get("bkash", lang),
                        selected = selectedGateway == "bKash",
                        logoText = "bkash",
                        brandColor = BrandbKash,
                        onClick = { selectedGateway = "bKash" }
                    )

                    // Gateway row: Nagad
                    PaymentGatewayRow(
                        title = Translations.get("nagad", lang),
                        selected = selectedGateway == "Nagad",
                        logoText = "nagad",
                        brandColor = BrandNagad,
                        onClick = { selectedGateway = "Nagad" }
                    )

                    // Gateway row: Rocket
                    PaymentGatewayRow(
                        title = Translations.get("rocket", lang),
                        selected = selectedGateway == "Rocket",
                        logoText = "rocket",
                        brandColor = BrandRocket,
                        onClick = { selectedGateway = "Rocket" }
                    )
                }

                // Expanded wallet forms if digital money is selected
                if (selectedGateway != "Cash") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Customer Mobile Gateway details",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        OutlinedTextField(
                            value = walletNo,
                            onValueChange = { walletNo = it },
                            label = { Text(Translations.get("enter_phone", lang)) },
                            placeholder = { Text("e.g. 01712345678") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") }
                        )

                        OutlinedTextField(
                            value = pinNo,
                            onValueChange = { pinNo = it.take(4) },
                            label = { Text(Translations.get("enter_pin", lang)) },
                            placeholder = { Text("৪-সংখ্যার পিন") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "PIN") }
                        )

                        // Gen simulated transaction reference key
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Autogen TrxD Ref:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            SelectionContainer {
                                Text(
                                    text = transactionId,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Total Payable Summary details
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Gross Bill:", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "৳$rawTotal", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Offer Benefit Net:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "৳$payableTotal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Trigger Checkout
                Button(
                    onClick = {
                        // validation
                        if (selectedGateway != "Cash" && (walletNo.length < 11 || pinNo.length < 4)) {
                            Toast.makeText(context, "Please input correct wallet phone &PIN details!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.checkout(
                                paymentMethod = selectedGateway,
                                accountNo = if (selectedGateway != "Cash") walletNo else null,
                                trxId = if (selectedGateway != "Cash") transactionId else null,
                                cashier = "Admin Cashier",
                                onComplete = {
                                    onDismiss()
                                    Toast.makeText(context, Translations.get("checkout_success", lang), Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = "Pay")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Process ৳$payableTotal Bill", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun PaymentGatewayRow(
    title: String,
    selected: Boolean,
    logoText: String,
    brandColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) brandColor else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .background(if (selected) brandColor.copy(alpha = 0.08f) else Color.Transparent)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = brandColor)
        )

        // Simulated Badge Logo
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brandColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = logoText,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) brandColor else MaterialTheme.colorScheme.onBackground
        )
    }
}

// ----------------------------------------------------
// SCREEN 2: ALL PRODUCTS CATALOG MANAGER (READ & EDIT)
// ----------------------------------------------------
@Composable
fun ProductsScreen(viewModel: PosViewModel, editable: Boolean) {
    val lang = LocalAppLanguage.current
    val products by viewModel.products.collectAsStateWithLifecycle()
    var searchTxt by remember { mutableStateOf("") }
    var showFormDialog by remember { mutableStateOf(false) }

    // editing state
    var selectedProductForEdit by remember { mutableStateOf<Product?>(null) }

    val filterList = remember(products, searchTxt) {
        products.filter {
            it.name.contains(searchTxt, ignoreCase = true) ||
            it.code.contains(searchTxt, ignoreCase = true) ||
            it.category.contains(searchTxt, ignoreCase = true)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (editable) {
                ExtendedFloatingActionButton(
                    text = { Text(Translations.get("add_product", lang), fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                    onClick = {
                        selectedProductForEdit = null
                        showFormDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Translations.get("all_products", lang),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Inventory Count: ${products.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = searchTxt,
                onValueChange = { searchTxt = it },
                placeholder = { Text(Translations.get("search_hint", lang)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (filterList.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ProductionQuantityLimits,
                            contentDescription = "Empty",
                            modifier = Modifier.size(54.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "No clothing registered yet!", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filterList, key = { it.id }) { product ->
                        ClothingDetailedItemRow(
                            product = product,
                            editable = editable,
                            onEdit = {
                                selectedProductForEdit = product
                                showFormDialog = true
                            },
                            onDelete = {
                                viewModel.deleteProduct(product)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        ProductFormDialog(
            viewModel = viewModel,
            product = selectedProductForEdit,
            onDismiss = { showFormDialog = false }
        )
    }
}

@Composable
fun ClothingDetailedItemRow(
    product: Product,
    editable: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val template = remember(product) {
        IllustrationTemplates.getOrElse(product.id % IllustrationTemplates.size) { IllustrationTemplates[0] }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Textured template
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(template.second),
                contentAlignment = Alignment.Center
            ) {
                Text(text = template.third, fontSize = 28.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = product.size,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Code: ${product.code} | Cat: ${product.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Cost: ৳${product.buyPrice.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Retail: ৳${product.sellPrice.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Stock: ${product.stock} pcs",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (product.stock <= 5) Color.Red else Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Edit controls if Owner private interface is logged
            if (editable) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductFormDialog(
    viewModel: PosViewModel,
    product: Product?,
    onDismiss: () -> Unit
) {
    val lang = LocalAppLanguage.current
    val isEdit = product != null

    var nameCode by remember { mutableStateOf(product?.code ?: "CODE-${(1000..9999).random()}") }
    var nameField by remember { mutableStateOf(product?.name ?: "") }
    var categoryField by remember { mutableStateOf(product?.category ?: "Kurti") }
    var sizeField by remember { mutableStateOf(product?.size ?: "L") }
    var buyPriceStr by remember { mutableStateOf(product?.buyPrice?.toInt()?.toString() ?: "") }
    var sellPriceStr by remember { mutableStateOf(product?.sellPrice?.toInt()?.toString() ?: "") }
    var stockStr by remember { mutableStateOf(product?.stock?.toString() ?: "") }

    var selectedIlluTemplateIdx by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isEdit) Translations.get("edit_product", lang) else Translations.get("add_product", lang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Input Barcode generator
                OutlinedTextField(
                    value = nameCode,
                    onValueChange = { nameCode = it },
                    label = { Text(Translations.get("code", lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { nameCode = "CODE-${(1000..9999).random()}" }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Gen Barcode")
                        }
                    }
                )

                OutlinedTextField(
                    value = nameField,
                    onValueChange = { nameField = it },
                    label = { Text(Translations.get("name", lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Category and size inline
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = categoryField,
                        onValueChange = { categoryField = it },
                        label = { Text(Translations.get("category", lang)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sizeField,
                        onValueChange = { sizeField = it },
                        label = { Text(Translations.get("size", lang)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Sizing and buying pricing
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = buyPriceStr,
                        onValueChange = { buyPriceStr = it },
                        label = { Text(Translations.get("buy_price", lang)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sellPriceStr,
                        onValueChange = { sellPriceStr = it },
                        label = { Text(Translations.get("sell_price", lang)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { stockStr = it },
                    label = { Text(Translations.get("stock", lang)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Select image illustration templates from a horizontal sliding panel
                Text(
                    text = Translations.get("product_photo", lang),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IllustrationTemplates.forEachIndexed { idx, item ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(item.second)
                                .border(
                                    width = if (selectedIlluTemplateIdx == idx) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedIlluTemplateIdx = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.third, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(Translations.get("cancel", lang))
                    }

                    Button(
                        onClick = {
                            val buyVal = buyPriceStr.toDoubleOrNull() ?: 0.0
                            val sellVal = sellPriceStr.toDoubleOrNull() ?: 0.0
                            val stockVal = stockStr.toIntOrNull() ?: 0

                            if (nameField.isNotBlank() && nameCode.isNotBlank()) {
                                if (isEdit) {
                                    viewModel.updateProduct(
                                        product!!.copy(
                                            code = nameCode,
                                            name = nameField,
                                            category = categoryField,
                                            size = sizeField,
                                            buyPrice = buyVal,
                                            sellPrice = sellVal,
                                            stock = stockVal
                                        )
                                    )
                                } else {
                                    viewModel.addProduct(
                                        code = nameCode,
                                        name = nameField,
                                        category = categoryField,
                                        size = sizeField,
                                        buyPrice = buyVal,
                                        sellPrice = sellVal,
                                        stock = stockVal
                                    )
                                }
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(Translations.get("save", lang))
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// SCREEN 3: ANALYTICS & STRATEGIC DATABASE REPORTS (OWNER PORTAL)
// ----------------------------------------------------
@Composable
fun ReportsScreen(viewModel: PosViewModel) {
    val lang = LocalAppLanguage.current
    val salesHistory by viewModel.saleRecords.collectAsStateWithLifecycle()
    var selectedFilterIdx by remember { mutableStateOf(0) } // 0: Daily, 1: Weekly, 2: Monthly, 3: Yearly

    val currentMillis = System.currentTimeMillis()

    // Filter list based on epoch thresholds
    val filteredHistory = remember(salesHistory, selectedFilterIdx) {
        val duration = when (selectedFilterIdx) {
            0 -> 24 * 3600 * 1000L // Today
            1 -> 7 * 24 * 3600 * 1000L // 7 Days
            2 -> 30 * 24 * 3600 * 1000L // 30 Days
            3 -> 365 * 24 * 3600 * 1000L // 365 Days
            else -> 24 * 3600 * 1000L
        }
        val startTime = currentMillis - duration
        salesHistory.filter { it.timestamp >= startTime }
    }

    val totalRevenue = remember(filteredHistory) { filteredHistory.sumOf { it.netAmount } }
    val totalProfit = remember(filteredHistory) { filteredHistory.sumOf { it.profitAmount } }
    val totalMemoCount = filteredHistory.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = Translations.get("reports_analysis", lang),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )

        // Segmented timeline filters (Daily, Weekly, Monthly, Yearly)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                Translations.get("daily", lang),
                Translations.get("weekly", lang),
                Translations.get("monthly", lang),
                Translations.get("yearly", lang)
            ).forEachIndexed { index, title ->
                Button(
                    onClick = { selectedFilterIdx = index },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedFilterIdx == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (selectedFilterIdx == index) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Metrics Summary cards block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Revenue Card
            SummaryMetricCard(
                title = Translations.get("total_sales", lang),
                value = "৳${totalRevenue.toInt()}",
                colorAccent = Color(0xFF0061A4),
                cardBgColor = Color(0xFFD1E4FF),
                cardTextColor = Color(0xFF001D36),
                modifier = Modifier.weight(1f)
            )

            // Profit Margin Card
            SummaryMetricCard(
                title = Translations.get("total_profit", lang),
                value = "৳${totalProfit.toInt()}",
                colorAccent = Color(0xFF001453),
                cardBgColor = Color(0xFFDDE1FF),
                cardTextColor = Color(0xFF001453),
                modifier = Modifier.weight(1f)
            )

            // Sales Volume Card
            SummaryMetricCard(
                title = Translations.get("sales_count", lang),
                value = "$totalMemoCount",
                colorAccent = Color(0xFF0061A4),
                cardBgColor = Color(0xFFE2F1FF),
                cardTextColor = Color(0xFF003258),
                modifier = Modifier.weight(0.8f)
            )
        }

        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        Text(
            text = "Receipt Logs Match Period: ($totalMemoCount)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // Scrolling detailed ledger of sales Matching duration
        if (filteredHistory.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No sales records in selected range!", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(filteredHistory, key = { it.id }) { sale ->
                    SaleRecordLedgerRow(sale = sale, onReprint = {
                        viewModel.triggerPreExistingPrintedReceipt(sale)
                    })
                }
            }
        }
    }
}

@Composable
fun SummaryMetricCard(
    title: String,
    value: String,
    colorAccent: Color,
    modifier: Modifier = Modifier,
    cardBgColor: Color = Color.Unspecified,
    cardTextColor: Color = Color.Unspecified
) {
    val containerCol = if (cardBgColor != Color.Unspecified) cardBgColor else MaterialTheme.colorScheme.surface
    val mainTextCol = if (cardTextColor != Color.Unspecified) cardTextColor else MaterialTheme.colorScheme.onBackground
    val valTextCol = if (cardTextColor != Color.Unspecified) cardTextColor else colorAccent

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerCol)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    // Draw a subtle left accent borders
                    drawLine(
                        color = colorAccent,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 10f
                    )
                }
                .padding(start = 14.dp, top = 14.dp, bottom = 14.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = mainTextCol.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = valTextCol,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SaleRecordLedgerRow(sale: SaleRecord, onReprint: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val timeStr = dateFormat.format(Date(sale.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onReprint() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Receipt,
                    contentDescription = "Invoice",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Memo ID: POS-${sale.id + 1000}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Time: $timeStr | Gateway: ${sale.paymentMethod}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "৳${sale.netAmount.toInt()}",
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                )
                Text(
                    text = "Margin: ৳${sale.profitAmount.toInt()}",
                    color = if (sale.profitAmount >= 0) BrandCash else Color.Red,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                imageVector = Icons.Default.Print,
                contentDescription = "reprint",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ----------------------------------------------------
// SCREEN 4: BOUTIQUE PROFILE MANAGEMENT (OWNER GENERAL)
// ----------------------------------------------------
@Composable
fun SettingsScreen(viewModel: PosViewModel) {
    val lang = LocalAppLanguage.current
    val shopProfile by viewModel.shopProfile.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

    val profile = shopProfile ?: ShopProfile()

    // Local form states
    var nameEdit by remember(profile) { mutableStateOf(profile.name) }
    var titleEdit by remember(profile) { mutableStateOf(profile.title) }
    var phoneEdit by remember(profile) { mutableStateOf(profile.phone) }
    var addressEdit by remember(profile) { mutableStateOf(profile.address) }
    var logoTextEdit by remember(profile) { mutableStateOf(profile.logoText) }
    var greetingEdit by remember(profile) { mutableStateOf(profile.footerGreeting) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = Translations.get("settings", lang),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "App Mode Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Online/Offline status switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Translations.get("online_status", lang),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isOnline,
                        onCheckedChange = { viewModel.toggleOnlineOffline() }
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Boutique Profile Properties",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = nameEdit,
                    onValueChange = { nameEdit = it },
                    label = { Text(Translations.get("shop_name", lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = titleEdit,
                    onValueChange = { titleEdit = it },
                    label = { Text(Translations.get("shop_tagline", lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phoneEdit,
                        onValueChange = { phoneEdit = it },
                        label = { Text(Translations.get("shop_phone", lang)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = logoTextEdit,
                        onValueChange = { logoTextEdit = it.take(4) },
                        label = { Text(Translations.get("shop_logo", lang)) },
                        placeholder = { Text("AB") },
                        modifier = Modifier.weight(0.7f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = addressEdit,
                    onValueChange = { addressEdit = it },
                    label = { Text(Translations.get("shop_address", lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = greetingEdit,
                    onValueChange = { greetingEdit = it },
                    label = { Text(Translations.get("shop_footer", lang)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        viewModel.updateShopSettings(
                            name = nameEdit,
                            title = titleEdit,
                            phone = phoneEdit,
                            address = addressEdit,
                            logoText = logoTextEdit,
                            greeting = greetingEdit
                        )
                        Toast.makeText(context, "Shop Information Updated!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save details")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Save Boutique Configuration", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Bluetooth Printer settings card
        PrinterSimulatorScreen(viewModel)
    }
}

// ----------------------------------------------------
// COMPONENT: BLUETOOTH THERMAL PRINTER MANAGER (SALESMAN/OWNER)
// ----------------------------------------------------
@Composable
fun PrinterSimulatorScreen(viewModel: PosViewModel) {
    val lang = LocalAppLanguage.current
    val isConnected by viewModel.isPrinterConnected.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Translations.get("printer_setup", lang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isConnected) Color(0xFF2E7D32) else Color.Red)
                        .size(10.dp)
                )
            }

            Text(
                text = "Printer Status: " + if (isConnected) Translations.get("printer_connected", lang) else Translations.get("printer_disconnected", lang),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            // BT device pair
            Button(
                onClick = {
                    viewModel.togglePrinterConnection()
                    Toast.makeText(
                        context,
                        if (isConnected) "Bluetooth thermal device disconnected." else "Simulated ESC/POS 58mm Printer connected via Bluetooth!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnected) Color.Gray else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = if (isConnected) Icons.Default.Close else Icons.Default.Print,
                    contentDescription = "Bluetooth"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isConnected) "Disconnect Device" else Translations.get("printer_connect_btn", lang),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ----------------------------------------------------
// COMPONENT: VISUAL THERMAL RECEIPT DISPLAY SCREEN
// ----------------------------------------------------
@Composable
fun ViewThermalReceiptOverlay(
    receiptText: String,
    onDismiss: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.hide()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Realistic dashed physical Thermal Receipt Paper roll Card Style
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Jagged thermal boundary drawing
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .drawBehind {
                                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(0f, size.height),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = 3f,
                                        pathEffect = pathEffect
                                    )
                                }
                        ) {}

                        Spacer(modifier = Modifier.height(14.dp))

                        // Receipt text render in monospace typewriter standard
                        Text(
                            text = receiptText,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.2.sp,
                            color = Color.Black,
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .heightIn(max = 280.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .drawBehind {
                                    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                    drawLine(
                                        color = Color.Gray,
                                        start = Offset(0f, 0f),
                                        end = Offset(size.width, 0f),
                                        strokeWidth = 3f,
                                        pathEffect = pathEffect
                                    )
                                }
                        ) {}
                    }
                }

                // Controls below printer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text(text = "Dismiss")
                    }

                    val context = LocalContext.current
                    Button(
                        onClick = {
                            Toast.makeText(context, "Bluetooth command executed: Printing Job Sent! 🖨️", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = "print")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Print via BT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
