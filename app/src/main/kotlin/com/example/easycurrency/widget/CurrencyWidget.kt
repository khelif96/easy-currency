package com.example.easycurrency.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.easycurrency.data.StaticExchangeRateProvider
import com.example.easycurrency.domain.ConversionResult
import com.example.easycurrency.domain.CurrencyConverter

private val converter = CurrencyConverter(StaticExchangeRateProvider)

class CurrencyWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val state = buildState()
            WidgetContent(state)
        }
    }

    @Composable
    private fun buildState(): CurrencyWidgetState {
        val pairs = DEFAULT_PAIRS
        val amount = 1000.0
        val pairIndex = 0
        val pair = pairs[pairIndex]

        return when (val result = converter.convert(amount, pair)) {
            is ConversionResult.Success -> CurrencyWidgetState(
                inputAmount = amount,
                selectedPairIndex = pairIndex,
                convertedAmount = result.formattedAmount,
                rateDate = result.rateDate,
                isStale = result.isStale,
                availablePairs = pairs,
            )
            is ConversionResult.UnsupportedPair -> CurrencyWidgetState(
                inputAmount = amount,
                selectedPairIndex = pairIndex,
                errorMessage = "Unsupported pair: ${result.pair}",
                availablePairs = pairs,
            )
        }
    }
}

@Composable
private fun WidgetContent(state: CurrencyWidgetState) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFF1C1C1E)))
            .padding(12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Currency Converter",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFFFFFFF)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )

            Spacer(GlanceModifier.height(8.dp))

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    style = TextStyle(color = ColorProvider(Color(0xFFFF453A))),
                )
            } else {
                Text(
                    text = state.selectedPair.toString(),
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF8E8E93)),
                        fontSize = 12.sp,
                    ),
                )

                Spacer(GlanceModifier.height(4.dp))

                Text(
                    text = "${state.inputAmount.toLong()} ${state.selectedPair.from.name}",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFFFFFF)),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                Spacer(GlanceModifier.height(4.dp))

                Text(
                    text = "= ${state.convertedAmount}",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF30D158)),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                Spacer(GlanceModifier.height(6.dp))

                val rateDateLabel = if (state.isStale) {
                    "Stale rate as of ${state.rateDate}"
                } else {
                    "Rate as of ${state.rateDate}"
                }
                Text(
                    text = rateDateLabel,
                    style = TextStyle(
                        color = ColorProvider(
                            if (state.isStale) Color(0xFFFF9F0A) else Color(0xFF636366)
                        ),
                        fontSize = 10.sp,
                    ),
                )
            }
        }
    }
}
