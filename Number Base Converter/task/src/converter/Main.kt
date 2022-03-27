package converter

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

const val SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
const val EXIT = "/exit"
const val BACK = "/back"
const val MAX_FRACTIONS = 5
const val PREFIX = "0."

fun main() {
    while (true) {
        print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
        val option = readln()
        when {
            option == EXIT -> break
            tryGetBases(option) -> {
                val (source, target) = option.split(" ").map { it.toInt() }
                iterator(source, target)
            }
        }
    }
}

fun tryGetBases(option: String): Boolean {
    return try {
        option.split(" ").map { it.toInt() }
        true
    } catch (e: NumberFormatException) {
        println("Bases format incorrect!")
        false
    }
}

fun iterator(source: Int, target: Int) {
    while (true) {
        print("Enter number in base $source to convert to base $target (To go back type /back) ")
        val option = readln()
        if (option == BACK) break
        val (integer, fraction) = tiles(option)
        var res = fromDecimal(toDecimal(integer, source), target)
        if (fraction.isNotEmpty()) {
            val resFract = fractionFromDecimal(fractionToDecimal("$PREFIX$fraction", source), target)
            res = "$res.$resFract"
        }
        toDecimal(option, source)
        println("Conversion result: $res")
    }
}

fun toDecimal(num: String, base: Int): String {
    var res = BigInteger.ZERO
    var power = 0
    num.reversed().forEach {
        res += SYMBOLS.indexOf(it.uppercase()).toBigInteger() * base.toBigInteger().pow(power)
        power++
    }
    return res.toString()
}

fun fromDecimal(num: String, base: Int): String {
    var res = ""
    var dec = num.toBigInteger()
    do {
        res = SYMBOLS[(dec % base.toBigInteger()).toInt()] + res
        dec /= base.toBigInteger()
    } while (dec != BigInteger.ZERO)
    return res
}

fun fractionToDecimal(num: String, base: Int): BigDecimal {
    var res = BigDecimal.ZERO
    var power = 1
    num.removePrefix(PREFIX).forEach {
        res += SYMBOLS.indexOf(it.uppercase()).toBigDecimal().setScale(MAX_FRACTIONS, RoundingMode.UNNECESSARY) / base.toBigDecimal().pow(power)
        power++
    }
    return res
}

fun fractionFromDecimal(num: BigDecimal, base: Int): String {
    var res = PREFIX
    var next = num * base.toBigDecimal()
    for (i in 1..MAX_FRACTIONS) {
        res += SYMBOLS[next.setScale(0, RoundingMode.DOWN).toInt()]
        next = (next - next.setScale(0, RoundingMode.DOWN)) * base.toBigDecimal()
    }
    return res.removePrefix(PREFIX)
}

fun tiles(num: String): List<String> {
    if (num.contains(".")) return num.split(".")
    return listOf(num, "")
}