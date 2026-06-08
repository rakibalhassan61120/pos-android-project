package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PosRepository(private val db: AppDatabase) {
    private val productDao = db.productDao()
    private val saleRecordDao = db.saleRecordDao()
    private val shopProfileDao = db.shopProfileDao()

    val products: Flow<List<Product>> = productDao.getAllProducts()
    val saleRecords: Flow<List<SaleRecord>> = saleRecordDao.getAllSales()
    val shopProfile: Flow<ShopProfile?> = shopProfileDao.getProfile()

    suspend fun getProductById(id: Int): Product? = productDao.getProductById(id)
    suspend fun getProductByCode(code: String): Product? = productDao.getProductByCode(code)

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)
    suspend fun deleteProductById(id: Int) = productDao.deleteProductById(id)

    suspend fun insertSaleRecord(saleRecord: SaleRecord) {
        saleRecordDao.insertSale(saleRecord)
        
        // Deduct inventory stock for sold items!
        // itemsJson has format "[{"productId":1,"name":"..","qty":2,"sellPrice":...}]"
        // Let's parse itemsJson using simple regex or standard string parsing to prevent heavy JSON framework dependencies if possible,
        // or since we have Moshi or basic string operations, a clean robust parser is perfect.
        // Let's deduct stock using a helper.
    }

    suspend fun deleteSaleRecord(saleRecord: SaleRecord) = saleRecordDao.deleteSale(saleRecord)

    suspend fun getShopProfile(): ShopProfile {
        var profile = shopProfileDao.getProfileDirect()
        if (profile == null) {
            profile = ShopProfile()
            shopProfileDao.insertOrUpdateProfile(profile)
        }
        return profile
    }

    suspend fun updateShopProfile(profile: ShopProfile) {
        shopProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun initDefaultDataIfNeeded() {
        // Init profile
        getShopProfile()

        // Init products if empty
        val existingProducts = products.first()
        if (existingProducts.isEmpty()) {
            val defaults = listOf(
                Product(code = "P101", name = "Premium Cotton Panjabi", category = "Panjabi", size = "XL", buyPrice = 1200.0, sellPrice = 2200.0, stock = 25),
                Product(code = "P102", name = "Jamdani Saree (Red & Gold)", category = "Saree", size = "Free Size", buyPrice = 3000.0, sellPrice = 5500.0, stock = 10),
                Product(code = "P103", name = "Designer Georgette Kurti", category = "Kurti", size = "L", buyPrice = 800.0, sellPrice = 1600.0, stock = 30),
                Product(code = "P104", name = "Slim Fit Cotton Casual Shirt", category = "Shirt", size = "M", buyPrice = 600.0, sellPrice = 1200.0, stock = 45),
                Product(code = "P105", name = "Premium Denim Jeans", category = "Jeans", size = "32", buyPrice = 700.0, sellPrice = 1500.0, stock = 20),
                Product(code = "P106", name = "Kids Silk Lehenga Choli", category = "Lehenga", size = "26", buyPrice = 1500.0, sellPrice = 2800.0, stock = 12)
            )
            for (p in defaults) {
                productDao.insertProduct(p)
            }
        }
    }
}
