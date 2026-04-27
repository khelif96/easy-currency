package com.example.easycurrency.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
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
private val AMOUNT_KEY = doublePreferencesKey("amount")
private val PRESET_AMOUNTS = listOf(100.0, 1000.0, 5000.0, 10000.0)

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
        val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
        val amount = prefs[AMOUNT_KEY] ?: 1000.0
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
            .padding(10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                        fontSize = 11.sp,
                    ),
                )

                Spacer(GlanceModifier.height(2.dp))

                Text(
                    text = "${state.inputAmount.toLong()} ${state.selectedPair.from.name}",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFFFFFFF)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                Text(
                    text = "= ${state.convertedAmount}",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF30D158)),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                Spacer(GlanceModifier.height(4.dp))

                val rateDateLabel = if (state.isStale) {
                    "Stale: ${state.rateDate}"
                } else {
                    state.rateDate
                }
                Text(
                    text = rateDateLabel,
                    style = TextStyle(
                        color = ColorProvider(
                            if (state.isStale) Color(0xFFFF9F0A) else Color(0xFF636366)
                        ),
                        fontSize = 9.sp,
                    ),
                )

                Spacer(GlanceModifier.height(8.dp))

                // Amount selection row
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    PRESET_AMOUNTS.forEach { amount ->
                        val isSelected = amount == state.inputAmount
                        Box(
                            modifier = GlanceModifier
                                .defaultWeight()
                                .height(28.dp)
                                .background(
                                    ColorProvider(
                                        if (isSelected) Color(0xFF0A84FF) else Color(0xFF2C2C2E)
                                    )
                                )
                                .clickable(
                                    actionRunCallback<UpdateAmountAction>(
                                        actionParametersOf(AMOUNT_PARAM_KEY to amount)
                                    )
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = formatAmount(amount),
                                style = TextStyle(
                                    color = ColorProvider(Color(0xFFFFFFFF)),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                ),
                            )
                        }
                        if (amount != PRESET_AMOUNTS.last()) {
                            Spacer(GlanceModifier.width(3.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun formatAmount(amount: Double): String {
    return when {
        amount >= 1000 -> "${(amount / 1000).toInt()}K"
        else -> amount.toInt().toString()
    }
}

private val AMOUNT_PARAM_KEY = ActionParameters.Key<Double>("amount")

class UpdateAmountAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val amount = parameters[AMOUNT_PARAM_KEY] ?: 1000.0
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[AMOUNT_KEY] = amount
        }
        CurrencyWidget().update(context, glanceId)
    }
}
