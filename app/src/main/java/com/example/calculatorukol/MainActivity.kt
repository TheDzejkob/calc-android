package com.example.calculatorukol;

import android.view.View;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.enableEdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.math.BigInteger;
import kotlin.math.log10;

var firstNum: String = "";
var operator: String = "";
var secondNum: String = "";
var result: String? = null;
var resolve: Boolean = false;
var wantSecondNum: Boolean = false;

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            insets;
        };
    }

    fun onClick(view: View) {
        val tag = view.tag.toString();
        when {
            tag == "=" -> {
                onEqualsPressed();
            }
            tag == "C" -> {
                onClearPressed();
            }
            tag == "log" -> {
                onLogPressed();
            }
            tag == "del" -> {
                onDeletePressed();
            }
            tag.matches(Regex("[+\\-*/]")) -> {
                onOperatorPressed(tag);
            }
            tag.matches(Regex("\\d")) -> {
                onNumberPressed(tag);
            }
        };
        updateDisplay();
    }

    private fun onNumberPressed(num: String) {
        if (!wantSecondNum) {
            if (firstNum == "0") firstNum = "";
            firstNum += num;
        } else {
            if (secondNum == "0") secondNum = "";
            secondNum += num;
        }
    }

    private fun onOperatorPressed(op: String) {
        if (firstNum.isNotEmpty() && !wantSecondNum) {
            operator = op;
            wantSecondNum = true;
        }
    }

    private fun onEqualsPressed() {
        if (firstNum.isNotEmpty() && operator.isNotEmpty() && secondNum.isNotEmpty()) {
            result = calculateResult();
            resolve = true;
            if (result != null) {
                firstNum = result!!;
            } else {
                firstNum = "";
            }
            secondNum = "";
            operator = "";
            wantSecondNum = false;
        }
    }

    private fun onClearPressed() {
        firstNum = "";
        operator = "";
        secondNum = "";
        result = null;
        wantSecondNum = false;
        resolve = false;
    }

    private fun onDeletePressed() {
        if (!wantSecondNum) {
            if (firstNum.isNotEmpty()) firstNum = firstNum.dropLast(1);
        } else {
            if (secondNum.isNotEmpty()) secondNum = secondNum.dropLast(1);
        }
    }

    private fun onLogPressed() {
        if (!wantSecondNum && firstNum.isNotEmpty()) {
            try {
                val num = firstNum.toDouble();
                if (num > 0) {
                    val logRes = log10(num);
                    result = logRes.toString();
                    resolve = true;
                    firstNum = result!!;
                    secondNum = "";
                    operator = "";
                    wantSecondNum = false;
                } else {
                    Toast.makeText(this, "Neplatný vstup pro log", Toast.LENGTH_SHORT).show();
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Chyba výpočtu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun updateDisplay() {
        val answearDisplay = findViewById<TextView>(R.id.AnswearDisplay);
        val problemDisplay = findViewById<TextView>(R.id.probleDisplay);

        if (resolve && result != null) {
            answearDisplay.text = result;
            problemDisplay.text = "";
            resolve = false;
        } else {
            val problemText = buildString {
                append(firstNum);
                if (operator.isNotEmpty()) append(" $operator ");
                if (secondNum.isNotEmpty()) append(secondNum);
            };
            problemDisplay.text = problemText;
            answearDisplay.text = "";
        }
    }

    private fun calculateResult(): String? {
        return try {
            val num1 = BigInteger(firstNum);
            val num2 = BigInteger(secondNum);
            val res = when (operator) {
                "+" -> num1.add(num2);
                "-" -> num1.subtract(num2);
                "*" -> num1.multiply(num2);
                "/" -> if (num2 != BigInteger.ZERO) num1.divide(num2) else null;
                else -> null;
            };
            res?.toString();
        } catch (e: Exception) {
            null;
        }
    }
}
