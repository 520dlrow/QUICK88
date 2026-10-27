package com.mc88.quick88

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalculatorActivity : AppCompatActivity() {
    private var operand1: Double? = null
    private var operator: String? = null
    private var isNewOp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = getSharedPreferences("quick88_prefs", MODE_PRIVATE)
        if (prefs.getInt("theme_index", 0) == 1) setTheme(R.style.Theme_Quick88_Dark) else setTheme(R.style.Theme_Quick88)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        val tvResult = findViewById<TextView>(R.id.tvResult)
        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )

        for (id in buttons) {
            findViewById<Button>(id).setOnClickListener { v ->
                val btn = v as Button
                if (isNewOp) {
                    tvResult.text = btn.text
                    isNewOp = false
                } else {
                    tvResult.append(btn.text)
                }
            }
        }

        findViewById<Button>(R.id.btnPlus).setOnClickListener { setOp(tvResult, "+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { setOp(tvResult, "-") }
        findViewById<Button>(R.id.btnMul).setOnClickListener { setOp(tvResult, "*") }
        findViewById<Button>(R.id.btnDiv).setOnClickListener { setOp(tvResult, "/") }

        findViewById<Button>(R.id.btnEqual).setOnClickListener {
            if (operand1 != null && operator != null) {
                val operand2 = tvResult.text.toString().toDoubleOrNull()
                if (operand2 != null) {
                    val res = when(operator) {
                        "+" -> operand1!! + operand2
                        "-" -> operand1!! - operand2
                        "*" -> operand1!! * operand2
                        "/" -> if (operand2 != 0.0) operand1!! / operand2 else Double.NaN
                        else -> 0.0
                    }
                    tvResult.text = if (res % 1.0 == 0.0) res.toInt().toString() else res.toString()
                    isNewOp = true
                    operator = null
                    operand1 = null
                }
            }
        }

        findViewById<Button>(R.id.btnC).setOnClickListener {
            tvResult.text = "0"
            operand1 = null
            operator = null
            isNewOp = true
        }

        findViewById<Button>(R.id.btnBackCalc).setOnClickListener {
            val text = tvResult.text.toString()
            if (text.isNotEmpty() && text != "0") {
                tvResult.text = text.substring(0, text.length - 1)
            }
        }

        findViewById<Button>(R.id.btnExitCalc).setOnClickListener { finish() }
    }

    private fun setOp(tv: TextView, op: String) {
        val value = tv.text.toString().toDoubleOrNull()
        if (value != null) {
            operand1 = value
            operator = op
            isNewOp = true
        }
    }
}
