package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppRole {
    SELECT_ROLE, SALESMAN, OWNER
}

data class CartItem(
    val product: Product,
    val quantity: Int
)

class PosViewModel(private val repository: PosRepository) : ViewModel() {

    // Localization and Access State
    private val _currentLanguage = MutableStateFlow(AppLanguage.BANGLA)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentRole = MutableStateFlow(AppRole.SELECT_ROLE)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // Products & Sale flows from Db
    val products = repository.products.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val saleRecords = repository.saleRecords.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val shopProfile = repository.shopProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Active POS Cart State
    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _discountPercentage = MutableStateFlow(0.0) // 0 to 100
    val discountPercentage: StateFlow<Double> = _discountPercentage.asStateFlow()

    // Searching
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Printer Simulation
    private val _isPrinterConnected = MutableStateFlow(false)
    val isPrinterConnected: StateFlow<Boolean> = _isPrinterConnected.asStateFlow()

    private val _printedReceiptText = MutableStateFlow<String?>(null)
    val printedReceiptText: StateFlow<String?> = _printedReceiptText.asStateFlow()

    // Initializer
    init {
        viewModelScope.launch {
            repository.initDefaultDataIfNeeded()
        }
    }

    // Language Toggling
    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == AppLanguage.BANGLA) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.BANGLA
        }
    }

    // Role Switching
    fun setRole(role: AppRole) {
        _currentRole.value = role
    }

    // Offline Toggling
    fun toggleOnlineOffline() {
        _isOnline.value = !_isOnline.value
    }

    // Cart Actions
    fun addToCart(product: Product) {
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == product.id }
        
        if (index != -1) {
            val item = currentList[index]
            if (item.quantity < product.stock) {
                currentList[index] = item.copy(quantity = item.quantity + 1)
            }
        } else {
            if (product.stock > 0) {
                currentList.add(CartItem(product, 1))
            }
        }
        _cart.value = currentList
    }

    fun updateCartQuantity(productId: Int, delta: Int) {
        val currentList = _cart.value.toMutableList()
        val index = currentList.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            val item = currentList[index]
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                currentList.removeAt(index)
            } else if (newQty <= item.product.stock) {
                currentList[index] = item.copy(quantity = newQty)
            }
        }
        _cart.value = currentList
    }

    fun removeFromCart(productId: Int) {
        _cart.value = _cart.value.filter { it.product.id != productId }
    }

    fun clearCart() {
        _cart.value = emptyList()
        _discountPercentage.value = 0.0
    }

    fun applyDiscount(percentage: Double) {
        _discountPercentage.value = percentage.coerceIn(0.0, 100.0)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Bluetooth Printer simulator actions
    fun togglePrinterConnection() {
        _isPrinterConnected.value = !_isPrinterConnected.value
        if (!_isPrinterConnected.value) {
            _printedReceiptText.value = null
        }
    }

    // Product Database Actions
    fun addProduct(code: String, name: String, category: String, size: String, buyPrice: Double, sellPrice: Double, stock: Int, imageUri: String? = null) {
        viewModelScope.launch {
            repository.insertProduct(
                Product(
                    code = code,
                    name = name,
                    category = category,
                    size = size,
                    buyPrice = buyPrice,
                    sellPrice = sellPrice,
                    stock = stock,
                    imageUri = imageUri
                )
            )
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun updateShopSettings(name: String, title: String, phone: String, address: String, logoText: String, greeting: String) {
        viewModelScope.launch {
            repository.updateShopProfile(
                ShopProfile(
                    name = name,
                    title = title,
                    phone = phone,
                    address = address,
                    logoText = logoText,
                    footerGreeting = greeting
                )
            )
        }
    }

    // Checkout execution
    fun checkout(paymentMethod: String, accountNo: String?, trxId: String?, cashier: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val currentCart = _cart.value
            if (currentCart.isEmpty()) return@launch

            // Calculate receipts totals
            var rawTotal = 0.0
            var totalBuyCost = 0.0
            
            // Build items JSON structure dynamically
            val itemsStringBuilder = StringBuilder("[")
            currentCart.forEachIndexed { idx, cartItem ->
                val p = cartItem.product
                rawTotal += p.sellPrice * cartItem.quantity
                totalBuyCost += p.buyPrice * cartItem.quantity
                
                // Deduct stock levels in SQLite
                val updatedProduct = p.copy(stock = maxOf(0, p.stock - cartItem.quantity))
                repository.updateProduct(updatedProduct)

                itemsStringBuilder.append("""{"id":${p.id},"name":"${p.name}","qty":${cartItem.quantity},"sell":${p.sellPrice},"buy":${p.buyPrice}}""")
                if (idx < currentCart.size - 1) {
                    itemsStringBuilder.append(",")
                }
            }
            itemsStringBuilder.append("]")

            val discAmount = rawTotal * (_discountPercentage.value / 100.0)
            val netAmount = rawTotal - discAmount
            val profitVal = netAmount - totalBuyCost

            val record = SaleRecord(
                itemsJson = itemsStringBuilder.toString(),
                totalAmount = rawTotal,
                discountAmount = discAmount,
                netAmount = netAmount,
                profitAmount = profitVal,
                paymentMethod = paymentMethod,
                paymentDetails = if (!accountNo.isNullOrBlank()) "Acc: $accountNo | TrID: ${trxId ?: "N/A"}" else null,
                cashierName = cashier
            )

            // Insert into local Room database (offline persistence!)
            repository.insertSaleRecord(record)

            // If printer is paired, immediately synthesize thermal format lines so they can view and "print" the receipt!
            if (_isPrinterConnected.value) {
                generateSimulatedReceiptText(record, currentCart)
            }

            // Flush Active Cart
            clearCart()
            onComplete()
        }
    }

    private fun generateSimulatedReceiptText(record: SaleRecord, items: List<CartItem>) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateString = dateFormat.format(Date(record.timestamp))
        val profile = shopProfile.value ?: ShopProfile()

        val sb = StringBuilder()
        sb.append("      ${profile.name.uppercase(Locale.getDefault())}      \n")
        sb.append("   ${profile.title}   \n")
        sb.append("Contact: ${profile.phone}\n")
        sb.append("Addr: ${profile.address}\n")
        sb.append("================================\n")
        sb.append("DATE: $dateString\n")
        sb.append("CASHIER: ${record.cashierName}\n")
        sb.append("TRANS ID: POS-${record.id + 1000}\n")
        sb.append("================================\n")
        items.forEach { cartItem ->
            val p = cartItem.product
            sb.append("${p.name} (${p.size})\n")
            val sub = p.sellPrice * cartItem.quantity
            val qtyLine = "  ${cartItem.quantity} x ৳${p.sellPrice}"
            val priceLine = "৳${sub}"
            val padding = 32 - qtyLine.length - priceLine.length
            val padSpaces = " ".repeat(maxOf(1, padding))
            sb.append(qtyLine).append(padSpaces).append(priceLine).append("\n")
        }
        sb.append("--------------------------------\n")
        val rawL = "SUBTOTAL:"
        val rawV = "৳${record.totalAmount}"
        sb.append(rawL).append(" ".repeat(maxOf(1, 32 - rawL.length - rawV.length))).append(rawV).append("\n")
        
        if (record.discountAmount > 0) {
            val discL = "DISCOUNT (${_discountPercentage.value}%):"
            val discV = "-৳${record.discountAmount}"
            sb.append(discL).append(" ".repeat(maxOf(1, 32 - discL.length - discV.length))).append(discV).append("\n")
        }
        
        sb.append("--------------------------------\n")
        val netL = "NET PAYABLE:"
        val netV = "৳${record.netAmount}"
        sb.append("COLOR_BOLD").append(netL).append(" ".repeat(maxOf(1, 32 - netL.length - netV.length))).append(netV).append("\n")
        
        val payL = "PAY VIA: ${record.paymentMethod.uppercase()}"
        sb.append(payL).append("\n")
        if (record.paymentDetails != null) {
            sb.append("${record.paymentDetails}\n")
        }
        sb.append("================================\n")
        sb.append("   ${profile.footerGreeting}   \n")
        sb.append("     POWERED BY AMAR CLOTHING POS   \n")
        _printedReceiptText.value = sb.toString()
    }

    fun triggerPreExistingPrintedReceipt(record: SaleRecord) {
        viewModelScope.launch {
            // Retrieve item lists based on records
            // Simple display helper
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateString = dateFormat.format(Date(record.timestamp))
            val profile = shopProfile.value ?: ShopProfile()

            val sb = StringBuilder()
            sb.append("      ${profile.name.uppercase(Locale.getDefault())}      \n")
            sb.append("   ${profile.title}   \n")
            sb.append("Contact: ${profile.phone}\n")
            sb.append("================================\n")
            sb.append("DATE: $dateString\n")
            sb.append("CASHIER: ${record.cashierName}\n")
            sb.append("TRANS ID: POS-${record.id + 1000}\n")
            sb.append("================================\n")
            // itemsJson format description
            sb.append("Purchased Items:\n")
            
            // basic parser for display
            try {
                if (record.itemsJson.isNotBlank()) {
                    val cleaned = record.itemsJson.removeSurrounding("[", "]")
                    val items = cleaned.split("},{")
                    items.forEach { itemStr ->
                        val parts = itemStr.removeSurrounding("{", "}").split(",")
                        var name = "Item"
                        var qty = 1
                        var sell = 0.0
                        parts.forEach { part ->
                            val kv = part.split(":")
                            if (kv.size == 2) {
                                val key = kv[0].replace("\"", "").trim()
                                val value = kv[1].replace("\"", "").trim()
                                if (key == "name") name = value
                                if (key == "qty") qty = value.toIntOrNull() ?: 1
                                if (key == "sell") sell = value.toDoubleOrNull() ?: 0.0
                            }
                        }
                        sb.append("- $name x $qty: ৳${sell * qty}\n")
                    }
                }
            } catch (e: Exception) {
                sb.append("Failed to load records items list\n")
            }
            
            sb.append("--------------------------------\n")
            sb.append("TOTAL: ৳${record.totalAmount}\n")
            if (record.discountAmount > 0) {
                sb.append("DISCOUNT: -৳${record.discountAmount}\n")
            }
            sb.append("NET PAYMENT: ৳${record.netAmount}\n")
            sb.append("GATEWAY: ${record.paymentMethod}\n")
            if (record.paymentDetails != null) {
                sb.append("${record.paymentDetails}\n")
            }
            sb.append("================================\n")
            sb.append("   ${profile.footerGreeting}   \n")
            _printedReceiptText.value = sb.toString()
        }
    }

    fun dismissReceipt() {
        _printedReceiptText.value = null
    }
}

class PosViewModelFactory(private val repository: PosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
