package com.example.easycurrency.model

enum class CurrencyCode(val displayName: String, val symbol: String) {
    USD("US Dollar", "$"),
    EUR("Euro", "€"),
    JPY("Japanese Yen", "¥"),
    GBP("British Pound", "£"),
    CAD("Canadian Dollar", "CA$"),
    KRW("South Korean Won", "₩");
}
