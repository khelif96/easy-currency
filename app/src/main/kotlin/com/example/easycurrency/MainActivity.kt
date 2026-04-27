package com.example.easycurrency

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.easycurrency.data.StaticExchangeRateProvider
import com.example.easycurrency.domain.ConversionResult
import com.example.easycurrency.domain.CurrencyConverter
import com.example.easycurrency.model.CurrencyCode
import com.example.easycurrency.model.CurrencyPair

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterApp() {
    var amount by remember { mutableStateOf("1000") }
    var fromCurrency by remember { mutableStateOf(CurrencyCode.JPY) }
    var toCurrency by remember { mutableStateOf(CurrencyCode.USD) }
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }

    val converter = remember { CurrencyConverter(StaticExchangeRateProvider) }

    val allCurrencies = listOf(
        CurrencyCode.USD, CurrencyCode.JPY, CurrencyCode.EUR,
        CurrencyCode.GBP, CurrencyCode.CAD, CurrencyCode.KRW
    )

    val result = remember(amount, fromCurrency, toCurrency) {
        val amountValue = amount.toDoubleOrNull() ?: 0.0
        converter.convert(amountValue, CurrencyPair(fromCurrency, toCurrency))
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1C1C1E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Currency Converter",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Result Display at top
            when (result) {
                is ConversionResult.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2C2C2E)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${fromCurrency.name} → ${toCurrency.name}",
                                fontSize = 14.sp,
                                color = Color(0xFF8E8E93)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = result.formattedAmount,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF30D158)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (result.isStale) "Stale rate: ${result.rateDate}" else "Rate: ${result.rateDate}",
                                fontSize = 12.sp,
                                color = if (result.isStale) Color(0xFFFF9F0A) else Color(0xFF636366)
                            )
                        }
                    }
                }
                is ConversionResult.UnsupportedPair -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2C2C2E)
                        )
                    ) {
                        Text(
                            text = "Unsupported currency pair",
                            color = Color(0xFFFF453A),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF0A84FF),
                    unfocusedBorderColor = Color(0xFF8E8E93),
                    focusedLabelColor = Color(0xFF0A84FF),
                    unfocusedLabelColor = Color(0xFF8E8E93)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // From Currency Dropdown
            ExposedDropdownMenuBox(
                expanded = fromExpanded,
                onExpandedChange = { fromExpanded = it }
            ) {
                OutlinedTextField(
                    value = "From: ${fromCurrency.name}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF0A84FF),
                        unfocusedBorderColor = Color(0xFF8E8E93)
                    )
                )
                ExposedDropdownMenu(
                    expanded = fromExpanded,
                    onDismissRequest = { fromExpanded = false },
                    modifier = Modifier.background(Color(0xFF2C2C2E))
                ) {
                    allCurrencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(currency.name, color = Color.White) },
                            onClick = {
                                fromCurrency = currency
                                fromExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // To Currency Dropdown
            ExposedDropdownMenuBox(
                expanded = toExpanded,
                onExpandedChange = { toExpanded = it }
            ) {
                OutlinedTextField(
                    value = "To: ${toCurrency.name}",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF0A84FF),
                        unfocusedBorderColor = Color(0xFF8E8E93)
                    )
                )
                ExposedDropdownMenu(
                    expanded = toExpanded,
                    onDismissRequest = { toExpanded = false },
                    modifier = Modifier.background(Color(0xFF2C2C2E))
                ) {
                    allCurrencies.forEach { currency ->
                        DropdownMenuItem(
                            text = { Text(currency.name, color = Color.White) },
                            onClick = {
                                toCurrency = currency
                                toExpanded = false
                            }
                        )
                    }
                }
            }

        }
    }
}
