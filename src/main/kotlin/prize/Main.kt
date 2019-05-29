package prize

import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    ProcessProducts().start()
}

class ProcessProducts {
    private data class Product(val id: Int, val price: Int, val weight: Int, val volume: Int, val unitPrice: Double, val unitWeight: Double)

    private var toteLength = 45
    private var toteWidth = 30
    private var toteHeight = 35
    private var toteVolume = toteLength * toteWidth * toteHeight // 47250
    private var products = arrayOf<Product>()
    private var selProducts = mutableListOf<Product>()
    private var selProductsPrice = 0

    init {
        val _products = mutableListOf<Product>()
        File("products.csv").forEachLine {
            val nums = it.split(",").map { it.toInt() }
            val price = nums[1]
            val volume = nums[2] * nums[3] * nums[4]
            val weight = nums[5]
            val unitPrice = price / volume.toDouble() // Price of per cubic centimeter
            val unitWeight = weight / volume.toDouble() // Weight of per cubic centimeter

            if (isFit(nums[2], nums[3], nums[4])) {
                _products.add(Product(nums[0], price, weight, volume, unitPrice, unitWeight))
            }
        }
        products = _products.toTypedArray()
        products.sortWith(compareByDescending<Product> { it.unitPrice }.thenBy { it.unitWeight })
    }

    private fun isFit(width: Int, length: Int, height: Int): Boolean {
        if (toteVolume >= width * length * height) {
            if (
                    (toteLength >= width && toteWidth >= length && toteHeight >= height) ||
                    (toteLength >= width && toteWidth >= height && toteHeight >= length) ||
                    (toteLength >= length && toteWidth >= height && toteHeight >= width) ||
                    (toteLength >= length && toteWidth >= width && toteHeight >= height) ||
                    (toteLength >= height && toteWidth >= width && toteHeight >= length) ||
                    (toteLength >= height && toteWidth >= length && toteHeight >= width)
            ) {
                return true
            }
        }
        return false
    }

    fun start() {
        for (pIndex in 0 until products.size) {
            check(pIndex, mutableListOf())
        }
    }

    private fun check(pIndex: Int, selected: MutableList<Product>) {
        selected.add(products[pIndex])
        val totalVolume = selected.sumBy { it.volume }

        if (totalVolume <= toteVolume) {
            val totalPrice = selected.sumBy { it.price }
            if (totalPrice > selProductsPrice) {
                selProducts = selected.toMutableList() // Clone
                selProductsPrice = totalPrice
            } else if (totalPrice == selProductsPrice && selected.sumBy { it.weight } < selProducts.sumBy { it.weight }) {
                selProducts = selected.toMutableList() // Clone
                selProductsPrice = totalPrice
            }
        }
        if (totalVolume < toteVolume) {
            val remainVolume = toteVolume - totalVolume
            for (_pIndex in pIndex + 1 until products.size) {
                if (products[_pIndex].volume <= remainVolume) {
                    check(_pIndex, selected)
                }
            }
        } else if (selProducts.size > 0) {
            println("price:" + selProducts.sumBy { it.price } + ", weight:" + selProducts.sumBy { it.weight })
            println("total ids: " + selProducts.sumBy { it.id })
            println(selProducts.map { it.id })
            println()
            exitProcess(0)
        }

        selected.removeAt(selected.size - 1)
    }
}